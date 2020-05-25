package io.github.fablabsmc.fablabs.api.fluid.v1;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import io.github.fablabsmc.fablabs.api.fluid.v1.math.Fraction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.minecraft.datafixer.NbtOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Util;

public class FractionDeserializationTest {
	@Test
	@DisplayName("JsonObject of 1 -> Fraction")
	public void testDeserialization() {
		final Fraction oneFractionFromJson = Fraction.deserialize(new Dynamic<>(JsonOps.INSTANCE, Util.make(new JsonObject(), object -> {
			object.addProperty("numerator", 1L);
		})));
		Assertions.assertEquals(Fraction.ONE, oneFractionFromJson,
				String.format("Incorrectly deserialized fraction. Expected: %s. Found %s.", Fraction.ONE.toString(), oneFractionFromJson.toString()));
	}

	@Test
	@DisplayName("Invalid type -> Fraction of 0")
	public void testInvalidValue() {
		final Fraction fromInvalid = Fraction.deserialize(new Dynamic<>(JsonOps.INSTANCE, new JsonPrimitive(true)));
		Assertions.assertEquals(Fraction.ZERO, fromInvalid,
				String.format("Incorrectly deserialized fraction. Expected: %s. Found %s.", Fraction.ZERO, fromInvalid.toString())); // invalid value
	}

	@Test
	@DisplayName("Empty Json -> Fraction of 0")
	public void testEmptyValue() {
		final Fraction fromEmpty = Fraction.deserialize(new Dynamic<>(JsonOps.INSTANCE, new JsonObject()));
		Assertions.assertEquals(Fraction.ZERO, fromEmpty,
				String.format("Incorrectly deserialized fraction. Expected: %s. Found %s.", Fraction.ZERO, fromEmpty.toString()));
	}

	@Test
	@DisplayName("Tag of 1/2 -> Fraction")
	public void testDeserializationOfSimplifiedFraction() {
		final CompoundTag halfCompoundTag = Util.make(new CompoundTag(), tag -> {
			tag.putLong("numerator", 1L);
			tag.putLong("denominator", 2L);
		});

		final Fraction halfFraction = Fraction.ofValidDenominator(1L, 2L);
		final Fraction fractionFromHalfTag = Fraction.deserialize(new Dynamic<>(NbtOps.INSTANCE, halfCompoundTag));
		Assertions.assertEquals(halfFraction, fractionFromHalfTag,
				String.format("Incorrectly deserialized fraction. Expected: %s. Found %s.", halfFraction.toString(), fractionFromHalfTag.toString()));
	}

	@Test
	@DisplayName("Tag of 4/8 -> Fraction of 1/2")
	public void testDeserializationOfUnsimplifiedFraction() {
		final CompoundTag halfUnsimplifiedTag = Util.make(new CompoundTag(), tag -> {
			tag.putLong("numerator", 4L);
			tag.putLong("denominator", 8L);
		});

		final Fraction halfFraction = Fraction.ofValidDenominator(1L, 2L);
		// Fraction of 4/8
		final Fraction fractionFromUnsimplifiedHalfTag = Fraction.deserialize(new Dynamic<>(NbtOps.INSTANCE, halfUnsimplifiedTag));
		Assertions.assertEquals(halfFraction, fractionFromUnsimplifiedHalfTag,
				String.format("Incorrectly deserialized unsimplified fraction. Expected: %s. Found %s.", halfFraction.toString(), fractionFromUnsimplifiedHalfTag.toString()));

		final Fraction sevenTenthsFraction = Fraction.ofValidDenominator(7L, 10L);
		final Fraction fractionFromFortyTwoSixtieths = Fraction.deserialize(new Dynamic<>(JsonOps.INSTANCE, Util.make(new JsonObject(), jsonObject -> {
			jsonObject.addProperty("numerator", 42L);
			jsonObject.addProperty("denominator", 60L);
		})));

		Assertions.assertEquals(sevenTenthsFraction, fractionFromFortyTwoSixtieths,
				String.format("Incorrectly deserialized unsimplified fraction. Expected: %s. Found %s.", sevenTenthsFraction.toString(), fractionFromFortyTwoSixtieths.toString()));
	}

	@Test
	@DisplayName("Tag of 9246000/56280000 -> Fraction of 69/420")
	public void testDeserializationOfLargeUnsimplifiedFraction() {
		final CompoundTag largeUnsimplifiedTag = Util.make(new CompoundTag(), tag -> {
			tag.putLong("numerator", 9246000L);
			tag.putLong("denominator", 56280000L);
		});

		final Fraction sixtyNineFourTwentyFraction = Fraction.ofValidDenominator(69L, 420L);
		final Fraction fractionFromLargeUnsimplifiedTag = Fraction.deserialize(new Dynamic<>(NbtOps.INSTANCE, largeUnsimplifiedTag));

		Assertions.assertEquals(sixtyNineFourTwentyFraction, fractionFromLargeUnsimplifiedTag,
				String.format("Incorrectly deserialized unsimplified fraction. Expected: %s. Found %s.", sixtyNineFourTwentyFraction.toString(), fractionFromLargeUnsimplifiedTag.toString()));
	}
}
