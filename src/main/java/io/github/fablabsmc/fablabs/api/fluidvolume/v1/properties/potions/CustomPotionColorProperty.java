package io.github.fablabsmc.fablabs.api.fluidvolume.v1.properties.potions;

import io.github.fablabsmc.fablabs.api.fluidvolume.v1.math.Fraction;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.properties.FluidProperty;

import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.IntTag;

public class CustomPotionColorProperty implements FluidProperty<IntTag> {
	@Override
	public IntTag merge(Fluid fluidA, Fraction amountA, Fraction amountB, IntTag a, IntTag b) {
		return IntTag.of(average(a.getInt(), b.getInt()));
	}

	@Override
	public boolean areCompatible(Fluid fluidA, IntTag a, IntTag b) {
		return true;
	}

	// "good enough" color blending algorithm
	private static int average(int a, int b) {
		return a & 0xFF000000 | avg(a, b, 16) | avg(a, b, 8) | avg(a, b, 0);
	}

	private static int avg(int a, int b, int off) {
		return ((a >> off & 0xFF) + (b >> off & 0xFF)) / 2 << off;
	}
}
