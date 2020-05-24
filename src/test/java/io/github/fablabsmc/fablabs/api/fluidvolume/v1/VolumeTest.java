package io.github.fablabsmc.fablabs.api.fluidvolume.v1;

import static net.minecraft.fluid.Fluids.LAVA;
import static net.minecraft.fluid.Fluids.WATER;

import io.github.fablabsmc.fablabs.api.fluidvolume.v1.math.Fraction;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.volume.ImmutableFluidVolume;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.volume.MultiFluidContainer;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.volume.api.FixedFractionFixedSizeFluidVolume;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.volume.api.FluidVolume;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.volume.api.SimpleFixedSizedFluidVolume;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import net.minecraft.Bootstrap;

public class VolumeTest {
	@BeforeAll
	public static void boostrap() {
		Bootstrap.initialize();
	}

	@Test
	public void testOverflow() {
		SimpleFixedSizedFluidVolume volume = new SimpleFixedSizedFluidVolume(Fraction.ONE);
		Assertions.assertEquals(volume.merge(new FluidVolume(WATER, Fraction.of(3, 2))), Fraction.ONE);
	}

	@Test
	public void testUnderflow() {
		SimpleFixedSizedFluidVolume volume = new SimpleFixedSizedFluidVolume(Fraction.ONE);
		Fraction twoThirds = Fraction.of(2, 3);
		Assertions.assertEquals(volume.merge(new FluidVolume(WATER, twoThirds)), twoThirds);
	}

	@Test
	public void testDrainUnderflow() {
		SimpleFixedSizedFluidVolume volume = new SimpleFixedSizedFluidVolume(Fraction.ONE);
		Fraction twoThirds = Fraction.of(2, 3);
		volume.merge(new FluidVolume(WATER, twoThirds));
		Fraction oneHalf = Fraction.of(1, 2);
		Assertions.assertEquals(volume.draw(oneHalf), new FluidVolume(WATER, oneHalf));
	}

	@Test
	public void testDrainOverflow() {
		SimpleFixedSizedFluidVolume volume = new SimpleFixedSizedFluidVolume(Fraction.ONE);
		Fraction twoThirds = Fraction.of(2, 3);
		Assertions.assertEquals(volume.merge(new FluidVolume(WATER, twoThirds)), twoThirds);
		Assertions.assertEquals(volume.drain(new FluidVolume(LAVA, Fraction.ONE)), ImmutableFluidVolume.EMPTY);
		Assertions.assertEquals(volume.drain(new FluidVolume(WATER, Fraction.ONE)), new FluidVolume(WATER, twoThirds));
	}

	@Test
	public void testMultiInsertAndDraw() {
		MultiFluidContainer contater = new MultiFluidContainer(
						new SimpleFixedSizedFluidVolume(Fraction.ONE),
						new SimpleFixedSizedFluidVolume(Fraction.ONE),
						new SimpleFixedSizedFluidVolume(Fraction.ONE)
		);
		Assertions.assertEquals(contater.merge(new FluidVolume(WATER, Fraction.ONE)), Fraction.ONE);
		Assertions.assertEquals(contater.merge(new FluidVolume(LAVA, Fraction.ONE)), Fraction.ONE);
		Assertions.assertEquals(contater.draw(Fraction.ofWhole(2)), new MultiFluidContainer(
						new FluidVolume(WATER, Fraction.ONE),
						new FluidVolume(LAVA, Fraction.ONE)
		));
	}

	@Test
	public void testFractionalInsert() {
		FixedFractionFixedSizeFluidVolume volume = new FixedFractionFixedSizeFluidVolume(Fraction.ONE.multiply(4), Fraction.ONE);
		Fraction twoThirds = Fraction.of(2, 3);
		Assertions.assertEquals(volume.merge(new FluidVolume(WATER, twoThirds)), Fraction.ZERO);
		Assertions.assertEquals(volume.draw(Fraction.ONE), ImmutableFluidVolume.EMPTY);
		Assertions.assertEquals(volume.merge(new FluidVolume(LAVA, Fraction.ONE)), Fraction.ONE);
	}

	@Test
	public void testText() {
		FluidVolume volume = new FluidVolume(WATER, Fraction.ONE);
		Assertions.assertEquals(volume.toText().asFormattedString(), "text.fluid.singular");
		Assertions.assertEquals(ImmutableFluidVolume.EMPTY.toText().asFormattedString(), "text.fluid.empty");
		Assertions.assertEquals(new FluidVolume(WATER, Fraction.ofWhole(2)).toText().asFormattedString(), "text.fluid.plural");
	}
}
