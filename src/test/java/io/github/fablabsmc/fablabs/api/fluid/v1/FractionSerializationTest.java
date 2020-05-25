package io.github.fablabsmc.fablabs.api.fluid.v1;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.types.JsonOps;
import io.github.fablabsmc.fablabs.api.fluid.v1.math.Fraction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.minecraft.datafixer.NbtOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Util;

public class FractionSerializationTest {
	@Test
	@DisplayName("Tag of 1/2 -> Fraction")
	public void testSerializationOfHalfToFraction() {
		final CompoundTag compoundTag = Util.make(new CompoundTag(), tag -> {
			tag.putLong("numerator", 1L);
			tag.putLong("denominator", 2L);
		});

		final Tag serializedFraction = Fraction.of(4L, 8L).serialize(NbtOps.INSTANCE);
		Assertions.assertEquals(compoundTag, serializedFraction,
				String.format("Incorrectly serialized unsimplified fraction. Expected: %s. Found %s.", compoundTag.toString(), serializedFraction.toString()));
	}

	@Test
	@DisplayName("Json of 2/5 -> Fraction of 8/20 (Simplified)")
	public void testSerializationOfUnsimplified() {
		final JsonElement toJson = Fraction.of(8L, 20L).serialize(JsonOps.INSTANCE);
		final JsonObject jsonObject = Util.make(new JsonObject(), object -> {
			object.addProperty("numerator", 2L);
			object.addProperty("denominator", 5L);
		});

		Assertions.assertEquals(jsonObject, toJson,
				String.format("Incorrectly serialized unsimplified fraction. Expected: %s. Found %s.", toJson.toString(), jsonObject.toString()));
	}

	@Test
	@DisplayName("Fraction of 1 -> Json of 1/1")
	public void testSerializationOfOneToOne() {
		final JsonObject oneAsJson = Util.make(new JsonObject(), object -> {
			object.addProperty("numerator", 1L);
			object.addProperty("denominator", 1L);
		});

		final JsonElement serialize = Fraction.ONE.serialize(JsonOps.INSTANCE);
		Assertions.assertEquals(oneAsJson, serialize,
				String.format("Incorrectly serialized unsimplified fraction. Expected: %s. Found %s.", oneAsJson.toString(), serialize.toString()));
	}

	@Test
	@DisplayName("Fraction of 0 -> Tag of 0")
	public void testSerializationOfZeroToZero() {
		final CompoundTag compoundTag = Util.make(new CompoundTag(), tag -> {
			tag.putLong("numerator", 0L);
			tag.putLong("denominator", 1L);
		});

		final Tag zeroTag = Fraction.ZERO.serialize(NbtOps.INSTANCE);

		Assertions.assertEquals(compoundTag, zeroTag,
				String.format("Incorrectly serialized unsimplified fraction. Expected: %s. Found %s.", compoundTag.toString(), zeroTag.toString()));
	}
}
