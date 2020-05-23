package io.github.fablabsmc.fablabs.api.fluidvolume.v1.containers;

import io.github.fablabsmc.fablabs.api.fluidvolume.v1.volume.api.FluidContainer;

import net.minecraft.item.ItemStack;

/**
 * implemented on an {@link ItemStack} class, this interface states the item may have a container for
 * an item stack with it's own type.
 */
public interface ItemFluidContainer {
	/**
	 * return the container for the given stack.
	 * reminder: ItemStack#getCount is not guaranteed to be 1.
	 * However if the stack size of your item is 1, you shouldn't worry about it.
	 *
	 * @param stack the itemstack
	 * @return an instance of the fluid container, or null if none found
	 */
	FluidContainer getVolume(ItemStack stack);
}
