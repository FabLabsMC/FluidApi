package io.github.fablabsmc.fablabs.api.fluidvolume.v1.mixin;

import io.github.fablabsmc.fablabs.api.fluidvolume.v1.access.BucketItemAccess;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.containers.ItemFluidContainer;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.volume.api.FluidContainer;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.volume.vanilla.BucketFluidVolume;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin (BucketItem.class) // buckets hold fluids, but not fractional amounts
public class BucketItemMixin implements BucketItemAccess, ItemFluidContainer {
	@Shadow @Final private Fluid fluid;

	@Override
	public Fluid getFluid() {
		return this.fluid;
	}

	@Override
	public FluidContainer getVolume(ItemStack stack) {
		return new BucketFluidVolume(stack);
	}
}
