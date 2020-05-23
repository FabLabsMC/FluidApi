package io.github.fablabsmc.fablabs.api.fluidvolume.v1;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.math.Fraction;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.volume.ImmutableFluidVolume;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.volume.MultiFluidContainer;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.volume.api.FixedFractionFixedSizeFluidVolume;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.volume.api.FixedSizedFluidVolume;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.volume.api.FluidVolume;
import net.minecraft.Bootstrap;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static io.github.fablabsmc.fablabs.api.fluidvolume.v1.math.Fraction.*;
import static net.minecraft.fluid.Fluids.WATER;

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
		Assertions.assertEquals(new IntArrayTag(new int[]{1, 2}), of(4, 8).serialize(NbtOps.INSTANCE));
		Assertions.assertEquals(Util.make(new JsonArray(), array -> {
			array.add(2);
			array.add(5);
		}), of(8, 20).serialize(JsonOps.INSTANCE));
		Assertions.assertEquals(Util.make(new JsonArray(), array -> {
			array.add(1);
			array.add(1);
		}), ONE.serialize(JsonOps.INSTANCE));
		Assertions.assertEquals(new IntArrayTag(new int[]{0, 1}), ZERO.serialize(NbtOps.INSTANCE));
	}

	@Test
	public void testFloorWithDenominator() {
		// 4.75 <- 4.8
		Assertions.assertEquals(of(19, 4), of(72, 15).floorWithDenominator(4));
		// 1.4 <- 40/27 (1.4814..)
		Assertions.assertEquals(of(14, 10), of(40, 27).floorWithDenominator(10));
	}


	@Test
	public void testOverflow() {
		Bootstrap.initialize();
		FixedSizedFluidVolume volume = new FixedSizedFluidVolume(ONE);
		Assertions.assertEquals(volume.merge(new FluidVolume(WATER, Fraction.of(3, 2))), ONE);
	}

	@Test
	public void testUnderflow() {
		Bootstrap.initialize();
		FixedSizedFluidVolume volume = new FixedSizedFluidVolume(ONE);
		Fraction twoThirds = Fraction.of(2, 3);
		Assertions.assertEquals(volume.merge(new FluidVolume(WATER, twoThirds)), twoThirds);
	}

	@Test
	public void testDrainUnderflow() {
		Bootstrap.initialize();
		FixedSizedFluidVolume volume = new FixedSizedFluidVolume(ONE);
		Fraction twoThirds = Fraction.of(2, 3);
		volume.merge(new FluidVolume(WATER, twoThirds));
		Fraction oneHalf = Fraction.of(1, 2);
		Assertions.assertEquals(volume.draw(oneHalf), new FluidVolume(WATER, oneHalf));
	}

	@Test
	public void testDrainOverflow() {
		Bootstrap.initialize();
		FixedSizedFluidVolume volume = new FixedSizedFluidVolume(ONE);
		Fraction twoThirds = Fraction.of(2, 3);
		volume.merge(new FluidVolume(WATER, twoThirds));
		Assertions.assertEquals(volume.draw(ONE), new FluidVolume(WATER, twoThirds));
	}

	@Test
	public void testMultiInsertAndDraw() {
		Bootstrap.initialize();
		MultiFluidContainer contater = new MultiFluidContainer(
						new FixedSizedFluidVolume(ONE),
						new FixedSizedFluidVolume(ONE),
						new FixedSizedFluidVolume(ONE)
		);
		Assertions.assertEquals(contater.merge(new FluidVolume(WATER, ONE)), ONE);
		Assertions.assertEquals(contater.merge(new FluidVolume(Fluids.LAVA, ONE)), ONE);
		Assertions.assertEquals(contater.draw(ofWhole(2)), new MultiFluidContainer(
						new FluidVolume(WATER, ONE),
						new FluidVolume(Fluids.LAVA, ONE)
		));
	}

	@Test
	public void testFractionalInsert() {
		FixedFractionFixedSizeFluidVolume volume = new FixedFractionFixedSizeFluidVolume(ONE.multiply(4), ONE);
		Fraction twoThirds = Fraction.of(2, 3);
		Assertions.assertEquals(volume.merge(new FluidVolume(WATER, twoThirds)), ZERO);
		Assertions.assertEquals(volume.draw(ONE), ImmutableFluidVolume.EMPTY);
		Assertions.assertEquals(volume.merge(new FluidVolume(Fluids.LAVA, ONE)), ONE);
	}

	@Test
	public void testText() {
		FluidVolume volume = new FluidVolume(WATER, ONE);
		Assertions.assertEquals(volume.toText().asFormattedString(), "text.fluid.singular");
		Assertions.assertEquals(ImmutableFluidVolume.EMPTY.toText().asFormattedString(), "text.fluid.empty");
		Assertions.assertEquals(new FluidVolume(WATER, ofWhole(2)).toText().asFormattedString(), "text.fluid.plural");
	}
}
