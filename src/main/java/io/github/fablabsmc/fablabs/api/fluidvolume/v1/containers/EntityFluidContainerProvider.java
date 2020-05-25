package io.github.fablabsmc.fablabs.api.fluidvolume.v1.containers;

import io.github.fablabsmc.fablabs.api.fluidvolume.v1.volume.api.FluidContainer;

import net.minecraft.util.math.Direction;

/**
 * an entity that carries fluids.
 */
public interface EntityFluidContainerProvider {
	/**
	 * @param face the side in which the entity is trying to be accessed from
	 * @return the fluid container for this entity
	 */
	FluidContainer getContainer(Direction face);
}
