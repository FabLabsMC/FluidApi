package io.github.fablabsmc.fablabs.api.fluidvolume.v1;

import static io.github.fablabsmc.fablabs.api.fluidvolume.v1.math.Fraction.ONE;
import static io.github.fablabsmc.fablabs.api.fluidvolume.v1.math.Fraction.ZERO;
import static io.github.fablabsmc.fablabs.api.fluidvolume.v1.math.Fraction.ofWhole;
import static net.minecraft.fluid.Fluids.LAVA;
import static net.minecraft.fluid.Fluids.WATER;

import io.github.fablabsmc.fablabs.api.fluidvolume.v1.math.Fraction;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.volume.ImmutableFluidVolume;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.volume.MultiFluidContainer;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.volume.api.FixedFractionFixedSizeFluidVolume;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.volume.api.SimpleFixedSizedFluidVolume;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.volume.api.FluidVolume;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import net.minecraft.Bootstrap;
import net.minecraft.fluid.Fluids;

public class VolumeTest {
	@BeforeAll
	public static void boostrap() {
		Bootstrap.initialize();
	}

	@Test
	public void testOverflow() {
		SimpleFixedSizedFluidVolume volume = new SimpleFixedSizedFluidVolume(ONE);
		Assertions.assertEquals(volume.merge(new FluidVolume(WATER, Fraction.of(3, 2))), ONE);
	}

	@Test
	public void testUnderflow() {
		SimpleFixedSizedFluidVolume volume = new SimpleFixedSizedFluidVolume(ONE);
		Fraction twoThirds = Fraction.of(2, 3);
		Assertions.assertEquals(volume.merge(new FluidVolume(WATER, twoThirds)), twoThirds);
	}

	@Test
	public void testDrainUnderflow() {
		SimpleFixedSizedFluidVolume volume = new SimpleFixedSizedFluidVolume(ONE);
		Fraction twoThirds = Fraction.of(2, 3);
		volume.merge(new FluidVolume(WATER, twoThirds));
		Fraction oneHalf = Fraction.of(1, 2);
		Assertions.assertEquals(volume.draw(oneHalf), new FluidVolume(WATER, oneHalf));
	}

	@Test
	public void testDrainOverflow() {
		SimpleFixedSizedFluidVolume volume = new SimpleFixedSizedFluidVolume(ONE);
		Fraction twoThirds = Fraction.of(2, 3);
		Assertions.assertEquals(volume.merge(new FluidVolume(WATER, twoThirds)), twoThirds);
		Assertions.assertEquals(volume.drain(new FluidVolume(LAVA, ONE)), ImmutableFluidVolume.EMPTY);
		Assertions.assertEquals(volume.drain(new FluidVolume(WATER, ONE)), new FluidVolume(WATER, twoThirds));
	}

	@Test
	public void testMultiInsertAndDraw() {
		MultiFluidContainer contater = new MultiFluidContainer(
						new SimpleFixedSizedFluidVolume(ONE),
						new SimpleFixedSizedFluidVolume(ONE),
						new SimpleFixedSizedFluidVolume(ONE)
		);
		Assertions.assertEquals(contater.merge(new FluidVolume(WATER, ONE)), ONE);
		Assertions.assertEquals(contater.merge(new FluidVolume(LAVA, ONE)), ONE);
		Assertions.assertEquals(contater.draw(ofWhole(2)), new MultiFluidContainer(
						new FluidVolume(WATER, ONE),
						new FluidVolume(LAVA, ONE)
		));
	}

	@Test
	public void testFractionalInsert() {
		FixedFractionFixedSizeFluidVolume volume = new FixedFractionFixedSizeFluidVolume(ONE.multiply(4), ONE);
		Fraction twoThirds = Fraction.of(2, 3);
		Assertions.assertEquals(volume.merge(new FluidVolume(WATER, twoThirds)), ZERO);
		Assertions.assertEquals(volume.draw(ONE), ImmutableFluidVolume.EMPTY);
		Assertions.assertEquals(volume.merge(new FluidVolume(LAVA, ONE)), ONE);
	}

	@Test
	public void testText() {
		FluidVolume volume = new FluidVolume(WATER, ONE);
		Assertions.assertEquals(volume.toText().asFormattedString(), "text.fluid.singular");
		Assertions.assertEquals(ImmutableFluidVolume.EMPTY.toText().asFormattedString(), "text.fluid.empty");
		Assertions.assertEquals(new FluidVolume(WATER, ofWhole(2)).toText().asFormattedString(), "text.fluid.plural");
	}
}
