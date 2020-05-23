package io.github.fablabsmc.fablabs.api.fluidvolume.v1.mixin;

import io.github.fablabsmc.fablabs.api.fluidvolume.v1.containers.ItemFluidContainer;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.volume.api.FluidContainer;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.volume.vanilla.PotionFluidVolume;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;

@Mixin(PotionItem.class) // potions hold fluid
public class PotionItemMixin implements ItemFluidContainer {
	@Override
	public FluidContainer getVolume(ItemStack stack) {
		return new PotionFluidVolume(stack);
	}
}
