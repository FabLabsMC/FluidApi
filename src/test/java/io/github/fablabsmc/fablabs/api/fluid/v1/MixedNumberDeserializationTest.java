package io.github.fablabsmc.fablabs.api.fluid.v1;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import io.github.fablabsmc.fablabs.api.fluid.v1.math.MixedNumber;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class MixedNumberDeserializationTest {
	@Test
	@DisplayName("JsonObject of 1 -> MixedNumber")
	public void testDeserialization() {
		final MixedNumber oneMixedNumberFromJson = MixedNumber.deserialize(new Dynamic<>(JsonOps.INSTANCE, Util.make(new JsonObject(), object -> {
			object.addProperty("numerator", 1L);
		})));
		Assertions.assertEquals(MixedNumber.ONE, oneMixedNumberFromJson,
				String.format("Incorrectly deserialized MixedNumber. Expected: %s. Found %s.", MixedNumber.ONE.toString(), oneMixedNumberFromJson.toString()));
	}

	@Test
	@DisplayName("Invalid type -> MixedNumber of 0")
	public void testInvalidValue() {
		final MixedNumber fromInvalid = MixedNumber.deserialize(new Dynamic<>(JsonOps.INSTANCE, new JsonPrimitive(true)));
		Assertions.assertEquals(MixedNumber.ZERO, fromInvalid,
				String.format("Incorrectly deserialized MixedNumber. Expected: %s. Found %s.", MixedNumber.ZERO, fromInvalid.toString())); // invalid value
	}

	@Test
	@DisplayName("Empty Json -> MixedNumber of 0")
	public void testEmptyValue() {
		final MixedNumber fromEmpty = MixedNumber.deserialize(new Dynamic<>(JsonOps.INSTANCE, new JsonObject()));
		Assertions.assertEquals(MixedNumber.ZERO, fromEmpty,
				String.format("Incorrectly deserialized MixedNumber. Expected: %s. Found %s.", MixedNumber.ZERO, fromEmpty.toString()));
	}

	@Test
	@DisplayName("Tag of 1/2 -> MixedNumber")
	public void testDeserializationOfSimplifiedMixedNumber() {
		final CompoundTag halfCompoundTag = Util.make(new CompoundTag(), tag -> {
			tag.putLong("numerator", 1L);
			tag.putLong("denominator", 2L);
		});

		final MixedNumber halfMixedNumber = MixedNumber.ofValidDenominator(0, 1, 2);
		final MixedNumber MixedNumberFromHalfTag = MixedNumber.deserialize(new Dynamic<>(NbtOps.INSTANCE, halfCompoundTag));
		Assertions.assertEquals(halfMixedNumber, MixedNumberFromHalfTag,
				String.format("Incorrectly deserialized MixedNumber. Expected: %s. Found %s.", halfMixedNumber.toString(), MixedNumberFromHalfTag.toString()));
	}

	@Test
	@DisplayName("Tag of 4/8 -> MixedNumber of 1/2")
	public void testDeserializationOfUnsimplifiedMixedNumber() {
		final CompoundTag halfUnsimplifiedTag = Util.make(new CompoundTag(), tag -> {
			tag.putLong("numerator", 4L);
			tag.putLong("denominator", 8L);
		});

		final MixedNumber halfMixedNumber = MixedNumber.ofValidDenominator(0, 1, 2);
		// MixedNumber of 4/8
		final MixedNumber MixedNumberFromUnsimplifiedHalfTag = MixedNumber.deserialize(new Dynamic<>(NbtOps.INSTANCE, halfUnsimplifiedTag));
		Assertions.assertEquals(halfMixedNumber, MixedNumberFromUnsimplifiedHalfTag,
				String.format("Incorrectly deserialized unsimplified MixedNumber. Expected: %s. Found %s.", halfMixedNumber.toString(), MixedNumberFromUnsimplifiedHalfTag.toString()));

		final MixedNumber sevenTenthsMixedNumber = MixedNumber.ofValidDenominator(0, 7, 10);
		final MixedNumber MixedNumberFromFortyTwoSixtieths = MixedNumber.deserialize(new Dynamic<>(JsonOps.INSTANCE, Util.make(new JsonObject(), jsonObject -> {
			jsonObject.addProperty("numerator", 42L);
			jsonObject.addProperty("denominator", 60L);
		})));

		Assertions.assertEquals(sevenTenthsMixedNumber, MixedNumberFromFortyTwoSixtieths,
				String.format("Incorrectly deserialized unsimplified MixedNumber. Expected: %s. Found %s.", sevenTenthsMixedNumber.toString(), MixedNumberFromFortyTwoSixtieths.toString()));
	}

	@Test
	@DisplayName("Tag of 9246000/56280000 -> MixedNumber of 69/420")
	public void testDeserializationOfLargeUnsimplifiedMixedNumber() {
		final CompoundTag largeUnsimplifiedTag = Util.make(new CompoundTag(), tag -> {
			tag.putLong("numerator", 9246000L);
			tag.putLong("denominator", 56280000L);
		});

		final MixedNumber sixtyNineFourTwentyMixedNumber = MixedNumber.ofValidDenominator(0, 69, 420);
		final MixedNumber MixedNumberFromLargeUnsimplifiedTag = MixedNumber.deserialize(new Dynamic<>(NbtOps.INSTANCE, largeUnsimplifiedTag));

		Assertions.assertEquals(sixtyNineFourTwentyMixedNumber, MixedNumberFromLargeUnsimplifiedTag,
				String.format("Incorrectly deserialized unsimplified MixedNumber. Expected: %s. Found %s.", sixtyNineFourTwentyMixedNumber.toString(), MixedNumberFromLargeUnsimplifiedTag.toString()));
	}
}
