package io.github.fablabsmc.fablabs.mixin.fluid;

import io.github.fablabsmc.fablabs.api.fluid.v1.containers.ItemFluidContainerProvider;
import io.github.fablabsmc.fablabs.api.fluid.v1.volume.api.FluidContainer;
import io.github.fablabsmc.fablabs.api.fluid.v1.volume.vanilla.PotionFluidVolume;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;

@Mixin(PotionItem.class) // potions hold fluid
public class PotionItemMixin implements ItemFluidContainerProvider {
	@Override
	public FluidContainer getContainer(ItemStack stack) {
		return new PotionFluidVolume(stack);
	}
}
