package io.github.fablabsmc.fablabs.api.fluidvolume.v1.volume.api;

import io.github.fablabsmc.fablabs.api.fluidvolume.v1.math.Fraction;

import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * a container who only stores 1 type of fluid, also known as a Volume.
 */
public interface SingleFluidContainer extends FluidContainer {
	/**
	 * get the fluid this volume contains.
	 * if the fluid is EMPTY, then {@link #getTotalVolume()} must be zero, a EMPTY fluid represents no fluid at all.
	 *
	 * @return the fluid this volume represents
	 */
	Fluid getFluid();

	/**
	 * gets the nbt data of the fluid.
	 *
	 * @return the nbt data
	 */
	CompoundTag getData();

	@Override
	default Text toText() {
		Fraction total = this.getTotalVolume();
		Identifier fluid = Registry.FLUID.getId(this.getFluid());

		if (total.equals(Fraction.ONE)) {
			return new TranslatableText("text.fluid.singular", total, new TranslatableText("fluid." + fluid.getNamespace() + "." + fluid.getPath()));
		} else if (total.equals(Fraction.ZERO)) {
			return new TranslatableText("text.fluid.empty");
		} else {
			return new TranslatableText("text.fluid.plural", new TranslatableText("fluid." + fluid.getNamespace() + "." + fluid.getPath()));
		}
	}
}
