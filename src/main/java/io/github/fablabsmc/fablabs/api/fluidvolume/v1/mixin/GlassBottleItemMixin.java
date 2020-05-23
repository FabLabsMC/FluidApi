package io.github.fablabsmc.fablabs.api.fluidvolume.v1.mixin;

import io.github.fablabsmc.fablabs.api.fluidvolume.v1.containers.ItemFluidContainer;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.volume.ImmutableFluidVolume;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.volume.api.FluidContainer;
import net.minecraft.item.GlassBottleItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin (GlassBottleItem.class) // glass bottles do not hold fluid, but they can once they are filled, however they technically become a different item
public class GlassBottleItemMixin implements ItemFluidContainer {
	@Override
	public FluidContainer getVolume(ItemStack stack) {
		return ImmutableFluidVolume.EMPTY;
	}
}
