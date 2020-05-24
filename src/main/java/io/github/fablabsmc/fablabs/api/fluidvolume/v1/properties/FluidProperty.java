package io.github.fablabsmc.fablabs.api.fluidvolume.v1.properties;

import io.github.fablabsmc.fablabs.api.fluidvolume.v1.math.Fraction;

import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.Tag;

/**
 * A property of a fluid that is stored in NBT.
 *
 * @param <T> the type this property prefers
 */
public interface FluidProperty<T extends Tag> {
	/**
	 * merge the properties of 2 fluids.
	 *
	 * @param fluid   the type of the fluid that is being combined
	 * @param amountA the amount of the original fluid
	 * @param amountB the amount of the fluid being merged
	 * @param aData   the data of the original fluid
	 * @param bData   the data of the fluid being merged
	 * @return a newly created compound tag representing the merged data
	 */
	T merge(Fluid fluid, Fraction amountA, Fraction amountB, T aData, T bData);

	/**
	 * checks if the data between 2 fluids are incompatible with one another.
	 *
	 * @param fluid the fluid
	 * @param aData the data in original fluid
	 * @param bData the data in the fluid being merged
	 * @return true if the data values are compatible with one another
	 */
	boolean areCompatible(Fluid fluid, T aData, T bData);
}
