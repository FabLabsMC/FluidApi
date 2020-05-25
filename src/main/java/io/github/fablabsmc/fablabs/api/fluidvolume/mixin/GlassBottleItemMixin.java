package io.github.fablabsmc.fablabs.api.fluidvolume.mixin;

import io.github.fablabsmc.fablabs.api.fluidvolume.v1.containers.ItemFluidContainerProvider;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.volume.ImmutableFluidVolume;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.volume.api.FluidContainer;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.item.GlassBottleItem;
import net.minecraft.item.ItemStack;

@Mixin(GlassBottleItem.class)
// glass bottles do not hold fluid, but they can once they are filled, however they technically become a different item
public class GlassBottleItemMixin implements ItemFluidContainerProvider {
	@Override
	public FluidContainer getContainer(ItemStack stack) {
		return ImmutableFluidVolume.EMPTY;
	}
}
