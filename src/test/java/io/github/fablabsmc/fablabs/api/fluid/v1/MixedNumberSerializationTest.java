package io.github.fablabsmc.fablabs.api.fluid.v1;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.types.JsonOps;
import io.github.fablabsmc.fablabs.api.fluid.v1.math.MixedNumber;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class MixedNumberSerializationTest {
	@Test
	@DisplayName("Tag of 1/2 -> MixedNumber")
	public void testSerializationOfHalfToMixedNumber() {
		final CompoundTag compoundTag = Util.make(new CompoundTag(), tag -> {
			tag.putLong("whole", 0);
			tag.putLong("numerator", 1);
			tag.putLong("denominator", 2);
		});

		final Tag serializedMixedNumber = MixedNumber.of(0, 4, 8).serialize(NbtOps.INSTANCE);
		Assertions.assertEquals(compoundTag, serializedMixedNumber,
				String.format("Incorrectly serialized unsimplified MixedNumber. Expected: %s. Found %s.", compoundTag.toString(), serializedMixedNumber.toString()));
	}

	@Test
	@DisplayName("Json of 2/5 -> MixedNumber of 8/20 (Simplified)")
	public void testSerializationOfUnsimplified() {
		final JsonElement toJson = MixedNumber.of(0, 8, 20).serialize(JsonOps.INSTANCE);
		final JsonObject jsonObject = Util.make(new JsonObject(), object -> {
			object.addProperty("whole", 0);
			object.addProperty("numerator", 2);
			object.addProperty("denominator", 5);
		});

		Assertions.assertEquals(jsonObject, toJson,
				String.format("Incorrectly serialized unsimplified MixedNumber. Expected: %s. Found %s.", toJson.toString(), jsonObject.toString()));
	}

	@Test
	@DisplayName("MixedNumber of 1 -> Json of 1/1")
	public void testSerializationOfOneToOne() {
		final JsonObject oneAsJson = Util.make(new JsonObject(), object -> {
			object.addProperty("whole", 1);
			object.addProperty("numerator", 0);
			object.addProperty("denominator", 1);
		});

		final JsonElement serialize = MixedNumber.ONE.serialize(JsonOps.INSTANCE);
		Assertions.assertEquals(oneAsJson, serialize,
				String.format("Incorrectly serialized unsimplified MixedNumber. Expected: %s. Found %s.", oneAsJson.toString(), serialize.toString()));
	}

	@Test
	@DisplayName("MixedNumber of 0 -> Tag of 0")
	public void testSerializationOfZeroToZero() {
		final CompoundTag compoundTag = Util.make(new CompoundTag(), tag -> {
			tag.putLong("whole", 0);
			tag.putLong("numerator", 0);
			tag.putLong("denominator", 1);
		});

		final Tag zeroTag = MixedNumber.ZERO.serialize(NbtOps.INSTANCE);

		Assertions.assertEquals(compoundTag, zeroTag,
				String.format("Incorrectly serialized unsimplified MixedNumber. Expected: %s. Found %s.", compoundTag.toString(), zeroTag.toString()));
	}
}
