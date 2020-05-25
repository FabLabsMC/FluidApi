package io.github.fablabsmc.fablabs.api.fluid.v1.volume.api;

import java.util.Collection;

import io.github.fablabsmc.fablabs.api.fluid.v1.math.Fraction;
import io.github.fablabsmc.fablabs.api.fluid.v1.volume.ImmutableFluidVolume;

import net.minecraft.text.Text;

/**
 * the fluid counterpart to {@link net.minecraft.inventory.Inventory}.
 */
public interface FluidContainer {
	/**
	 * same as {@link #merge(FluidVolume)} but it returns the amount of fluid left-over, rather than how much was added.
	 * no guarantee is given that the passed fluidvolume will not be mutated.
	 *
	 * @param container the fluid to add
	 * @return the amount of fluid not merged
	 */
	default Fraction mergeLeftover(FluidVolume container) {
		return container.amount.subtract(merge(container));
	}

	/**
	 * merges the current fluid volume with the target fluid, returning a newly created fluid with the combined properties.
	 * please do not blindly merge fluids, remember to add proper handling for the return value!
	 * no guarantee is given that the passed fluidvolume will not be mutated.
	 *
	 * @return the amount actually merged
	 */
	Fraction merge(FluidVolume volume);

	/**
	 * drain an amount of a fluid from this container.
	 * does not respect the nbt data of the passed fluid.
	 * no guarantee is given that the passed fluidvolume will not be mutated.
	 *
	 * @param volume the fluid volume to drain
	 * @return the fluid actually drained
	 */
	FluidVolume drain(FluidVolume volume);

	/**
	 * draws a wildcard-matched amount of fluid from the container.
	 * the combined sum of the volumes of each of the fluids is not guaranteed to be the same as the fraction passed.
	 *
	 * @param fraction the amount to drain
	 * @return the fluids actually drained
	 */
	FluidContainer draw(Fraction fraction);

	/**
	 * @return true if the fluid container is empty.
	 */
	boolean isEmpty();

	/**
	 * returns an immutable collection of the container, or itself if it is a {@link FluidVolume}.
	 *
	 * @return a non-null immutable collection of the fluid containers
	 */
	Collection<FluidContainer> subContainers();

	/**
	 * returns the total volume of the container.
	 *
	 * @return the volume
	 */
	Fraction getTotalVolume();

	/**
	 * gets a textual representation of the fluids in this fluid container.
	 */
	Text toText();

	/**
	 * @return true if the fluid container is immutable.
	 */
	default boolean isImmutable() {
		return this instanceof ImmutableFluidVolume;
	}
}
