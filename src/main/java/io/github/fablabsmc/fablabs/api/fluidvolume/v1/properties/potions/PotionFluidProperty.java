package io.github.fablabsmc.fablabs.api.fluidvolume.v1.properties.potions;

import io.github.fablabsmc.fablabs.api.fluidvolume.v1.math.Fraction;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.properties.FluidProperty;

import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.StringTag;

public class PotionFluidProperty implements FluidProperty<StringTag> {
	@Override
	public StringTag merge(Fluid fluid, Fraction amountA, Fraction amountB, StringTag aData, StringTag bData) {
		return aData.copy();
	}

	@Override
	public boolean areCompatible(Fluid fluid, StringTag aData, StringTag bData) {
		return aData.equals(bData);
	}
}
