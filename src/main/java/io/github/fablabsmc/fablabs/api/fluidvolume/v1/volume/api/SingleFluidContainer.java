package io.github.fablabsmc.fablabs.api.fluidvolume.v1.volume.api;

import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundTag;

/**
 * a container who only stores 1 type of fluid, also known as a Volume.
 */
public interface SingleFluidContainer extends FluidContainer {
	/**
	 * get the fluid this volume contains.
	 * if the fluid is EMPTY, then {@link #getTotalVolume()}
	 * must be zero, a EMPTY fluid represents no fluid at all
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
}
