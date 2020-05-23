package io.github.fablabsmc.fablabs.api.fluidvolume.v1.util;

import io.github.fablabsmc.fablabs.api.fluidvolume.v1.volume.api.FluidVolume;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

/**
 * a utility class for making custom volumes/fluid containers for items.
 */
public final class ItemVolumes {
	private ItemVolumes() {
	}

	/**
	 * deserializes a fluid volume from the nbt data in an item stack.
	 */
	public static void load(FluidVolume volume, ItemStack stack) {
		CompoundTag tag = stack.getTag();

		if (tag != null) {
			volume.fromTag(tag.getCompound("fluid_data"));
		}
	}

	/**
	 * serializes the data in a fluid volume into an item stack.
	 */
	public static void resync(FluidVolume volume, ItemStack stack) {
		CompoundTag tag = new CompoundTag();
		if (stack.getTag() == null) stack.setTag(new CompoundTag());
		volume.toTag(tag);
		stack.getTag().put("fluid_data", tag);
	}
}
