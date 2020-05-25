package io.github.fablabsmc.fablabs.api.fluid.v1.properties.potions;

import io.github.fablabsmc.fablabs.api.fluid.v1.math.Fraction;
import io.github.fablabsmc.fablabs.api.fluid.v1.properties.FluidProperty;

import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.IntTag;

public class CustomPotionColorProperty implements FluidProperty<IntTag> {
	@Override
	public IntTag merge(Fluid fluid, Fraction amountA, Fraction amountB, IntTag aData, IntTag bData) {
		return IntTag.of(average(aData.getInt(), bData.getInt()));
	}

	@Override
	public boolean areCompatible(Fluid fluid, IntTag aData, IntTag bData) {
		return true;
	}

	// "good enough" color blending algorithm
	private static int average(int rgbLeft, int rgbRight) {
		return rgbLeft & 0xFF000000 | avg(rgbLeft, rgbRight, 16) | avg(rgbLeft, rgbRight, 8) | avg(rgbLeft, rgbRight, 0);
	}

	private static int avg(int a, int b, int off) {
		return ((a >> off & 0xFF) + (b >> off & 0xFF)) / 2 << off;
	}
}
