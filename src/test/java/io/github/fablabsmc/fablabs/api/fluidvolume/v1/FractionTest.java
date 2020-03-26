package io.github.fablabsmc.fablabs.api.fluidvolume.v1;

import static io.github.fablabsmc.fablabs.api.fluidvolume.v1.Fraction.ONE;
import static io.github.fablabsmc.fablabs.api.fluidvolume.v1.Fraction.ZERO;
import static io.github.fablabsmc.fablabs.api.fluidvolume.v1.Fraction.deserialize;
import static io.github.fablabsmc.fablabs.api.fluidvolume.v1.Fraction.multiply;
import static io.github.fablabsmc.fablabs.api.fluidvolume.v1.Fraction.of;
import static io.github.fablabsmc.fablabs.api.fluidvolume.v1.Fraction.ofValidDenominator;
import static io.github.fablabsmc.fablabs.api.fluidvolume.v1.Fraction.ofWhole;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import net.minecraft.datafixer.NbtOps;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.util.Util;

public class FractionTest {
	@Test
	public void testSign() {
		Assertions.assertEquals(of(2, 5), of(-2, -5));
		Assertions.assertEquals(of(-2, 5), of(2, -5));
	}

	@Test
	public void testSimplification() {
		Assertions.assertEquals(ofValidDenominator(5, 5), ofValidDenominator(47, 47));
		Assertions.assertEquals(ofWhole(2), ofValidDenominator(4, 2));
		Assertions.assertEquals(Fraction.ZERO, ofValidDenominator(0, 234));
		Assertions.assertEquals(ofValidDenominator(7, 10), ofValidDenominator(42, 60));
	}

	@Test
	public void badInverseTest() {
		Assertions.assertThrows(ArithmeticException.class, Fraction.ZERO::inverse);
		Assertions.assertThrows(ArithmeticException.class, () -> ofWhole(25).divide(ofValidDenominator(0, 23)));
	}

	@Test
	public void generalTest() {
		Fraction eightBottles = Fraction.ofThirds(8);
		Fraction fiveIngots = Fraction.ofNinths(5);

		Assertions.assertEquals(Fraction.ofNinths(29), Fraction.add(eightBottles, fiveIngots)); // 8/3 + 5/9
		Assertions.assertEquals(Fraction.ofNinths(19), Fraction.subtract(eightBottles, fiveIngots)); // 8/3 - 5/9
		Assertions.assertEquals(multiply(eightBottles, fiveIngots), Fraction.ofValidDenominator(40, 27)); // 8/3 * 5/9
		Assertions.assertEquals(ofValidDenominator(72, 15), Fraction.divide(eightBottles, fiveIngots)); // 8/3 / 5/9
	}

	@Test
	public void testMultiply() {
		Assertions.assertEquals(ofValidDenominator(7, 8), multiply(ofValidDenominator(8, 10), ofValidDenominator(7, 6), ofValidDenominator(5, 8), ofValidDenominator(6, 4)));
		Assertions.assertEquals(ofValidDenominator(-7, 8), multiply(ofValidDenominator(-8, 10), ofValidDenominator(7, 6), ofValidDenominator(-5, 8), ofValidDenominator(-6, 4)));
	}

	@Test
	public void testDeserialization() {
		IntArrayTag intArrTag = new IntArrayTag(new int[] {1, 2});
		Assertions.assertEquals(ofValidDenominator(1, 2), deserialize(new Dynamic<>(NbtOps.INSTANCE, intArrTag)));
		Assertions.assertEquals(ofValidDenominator(1, 2), deserialize(new Dynamic<>(NbtOps.INSTANCE, new IntArrayTag(new int[] {4, 8}))));
		Assertions.assertEquals(ofValidDenominator(7, 10), deserialize(new Dynamic<>(JsonOps.INSTANCE, Util.make(new JsonArray(), array -> {
			array.add(42);
			array.add(60);
		}))));

		Assertions.assertEquals(ONE, deserialize(new Dynamic<>(JsonOps.INSTANCE, Util.make(new JsonArray(), array -> {
			array.add(1);
		}))));

		Assertions.assertEquals(ZERO, deserialize(new Dynamic<>(JsonOps.INSTANCE, new JsonPrimitive(true)))); // invalid value

		Assertions.assertEquals(ZERO, deserialize(new Dynamic<>(JsonOps.INSTANCE, new JsonArray())));
	}

	@Test
	public void testSerialization() {
		Assertions.assertEquals(new IntArrayTag(new int[] {1, 2}), Fraction.of(4, 8).serialize(NbtOps.INSTANCE));
		Assertions.assertEquals(Util.make(new JsonArray(), array -> {
			array.add(2);
			array.add(5);
		}), Fraction.of(8, 20).serialize(JsonOps.INSTANCE));
		Assertions.assertEquals(Util.make(new JsonArray(), array -> {
			array.add(1);
			array.add(1);
		}), ONE.serialize(JsonOps.INSTANCE));
		Assertions.assertEquals(new IntArrayTag(new int[] {0, 1}), ZERO.serialize(NbtOps.INSTANCE));
	}

	@Test
	public void testFloorWithDenominator() {
		// 4.75 <- 4.8
		Assertions.assertEquals(Fraction.of(19, 4), Fraction.of(72, 15).floorWithDenominator(4));
		// 1.4 <- 40/27 (1.4814..)
		Assertions.assertEquals(Fraction.of(14, 10), Fraction.of(40, 27).floorWithDenominator(10));
	}
}
