package io.github.fablabsmc.fablabs.api.fluid.v1;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import io.github.fablabsmc.fablabs.api.fluid.v1.math.Fraction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import net.minecraft.datafixer.NbtOps;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.util.Util;

public class FractionTest {
	@Test
	public void testSign() {
		Assertions.assertEquals(Fraction.of(2, 5), Fraction.of(-2, -5));
		Assertions.assertEquals(Fraction.of(-2, 5), Fraction.of(2, -5));
	}

	@Test
	public void testSimplification() {
		Assertions.assertEquals(Fraction.ofValidDenominator(5, 5), Fraction.ofValidDenominator(47, 47));
		Assertions.assertEquals(Fraction.ofWhole(2), Fraction.ofValidDenominator(4, 2));
		Assertions.assertEquals(Fraction.ZERO, Fraction.ofValidDenominator(0, 234));
		Assertions.assertEquals(Fraction.ofValidDenominator(7, 10), Fraction.ofValidDenominator(42, 60));
	}

	@Test
	public void badInverseTest() {
		Assertions.assertThrows(ArithmeticException.class, Fraction.ZERO::inverse);
		Assertions.assertThrows(ArithmeticException.class, () -> Fraction.ofWhole(25).divide(Fraction.ofValidDenominator(0, 23)));
	}

	@Test
	public void generalTest() {
		Fraction eightBottles = Fraction.ofThirds(8);
		Fraction fiveIngots = Fraction.ofNinths(5);

		Assertions.assertEquals(Fraction.ofNinths(29), Fraction.add(eightBottles, fiveIngots)); // 8/3 + 5/9
		Assertions.assertEquals(Fraction.ofNinths(19), Fraction.subtract(eightBottles, fiveIngots)); // 8/3 - 5/9
		Assertions.assertEquals(Fraction.multiply(eightBottles, fiveIngots), Fraction.ofValidDenominator(40, 27)); // 8/3 * 5/9
		Assertions.assertEquals(Fraction.ofValidDenominator(72, 15), Fraction.divide(eightBottles, fiveIngots)); // 8/3 / 5/9
	}

	@Test
	public void testMultiply() {
		Assertions.assertEquals(Fraction.ofValidDenominator(7, 8), Fraction.multiply(Fraction.ofValidDenominator(8, 10), Fraction.ofValidDenominator(7, 6), Fraction.ofValidDenominator(5, 8), Fraction.ofValidDenominator(6, 4)));
		Assertions.assertEquals(Fraction.ofValidDenominator(-7, 8), Fraction.multiply(Fraction.ofValidDenominator(-8, 10), Fraction.ofValidDenominator(7, 6), Fraction.ofValidDenominator(-5, 8), Fraction.ofValidDenominator(-6, 4)));
	}

	@Test
	public void testDeserialization() {
		LongArrayTag longTag = new LongArrayTag(new long[] {1, 2});
		Assertions.assertEquals(Fraction.ofValidDenominator(1, 2), Fraction.deserialize(new Dynamic<>(NbtOps.INSTANCE, longTag)));
		Assertions.assertEquals(Fraction.ofValidDenominator(1, 2), Fraction.deserialize(new Dynamic<>(NbtOps.INSTANCE, new LongArrayTag(new long[] {4, 8}))));
		Assertions.assertEquals(Fraction.ofValidDenominator(7, 10), Fraction.deserialize(new Dynamic<>(JsonOps.INSTANCE, Util.make(new JsonArray(), array -> {
			array.add(42);
			array.add(60);
		}))));

		Assertions.assertEquals(Fraction.ONE, Fraction.deserialize(new Dynamic<>(JsonOps.INSTANCE, Util.make(new JsonArray(), array -> {
			array.add(1);
		}))));

		Assertions.assertEquals(Fraction.ZERO, Fraction.deserialize(new Dynamic<>(JsonOps.INSTANCE, new JsonPrimitive(true)))); // invalid value

		Assertions.assertEquals(Fraction.ZERO, Fraction.deserialize(new Dynamic<>(JsonOps.INSTANCE, new JsonArray())));
	}

	@Test
	public void testSerialization() {
		Assertions.assertEquals(new LongArrayTag(new long[]{1, 2}), Fraction.of(4, 8).serialize(NbtOps.INSTANCE));
		Assertions.assertEquals(Util.make(new JsonArray(), array -> {
			array.add(2);
			array.add(5);
		}), Fraction.of(8, 20).serialize(JsonOps.INSTANCE));
		Assertions.assertEquals(Util.make(new JsonArray(), array -> {
			array.add(1);
			array.add(1);
		}), Fraction.ONE.serialize(JsonOps.INSTANCE));
		Assertions.assertEquals(new LongArrayTag(new long[]{0, 1}), Fraction.ZERO.serialize(NbtOps.INSTANCE));
	}

	@Test
	public void testFloorWithDenominator() {
		// 4.75 <- 4.8
		Assertions.assertEquals(Fraction.of(19, 4), Fraction.of(72, 15).floorWithDenominator(4));
		// 1.4 <- 40/27 (1.4814..)
		Assertions.assertEquals(Fraction.of(14, 10), Fraction.of(40, 27).floorWithDenominator(10));
	}
}
