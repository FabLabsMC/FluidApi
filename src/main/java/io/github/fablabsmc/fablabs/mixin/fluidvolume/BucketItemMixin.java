package io.github.fablabsmc.fablabs.mixin.fluidvolume;

import io.github.fablabsmc.fablabs.api.fluidvolume.v1.access.BucketItemAccess;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.containers.ItemFluidContainerProvider;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.volume.vanilla.BucketFluidVolume;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;

@Mixin(BucketItem.class) // buckets hold fluids, but not fractional amounts
public class BucketItemMixin implements BucketItemAccess, ItemFluidContainerProvider {
	@Shadow
	@Final
	private Fluid fluid;

	@Override
	public Fluid getFluid() {
		return this.fluid;
	}

	@Override
	public BucketFluidVolume getContainer(ItemStack stack) {
		return new BucketFluidVolume(stack);
	}
}
