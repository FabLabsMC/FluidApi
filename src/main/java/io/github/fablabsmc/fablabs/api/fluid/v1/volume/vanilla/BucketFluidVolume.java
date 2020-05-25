package io.github.fablabsmc.fablabs.api.fluid.v1.volume.vanilla;

import io.github.fablabsmc.fablabs.api.fluid.v1.access.BucketItemAccess;
import io.github.fablabsmc.fablabs.api.fluid.v1.math.Fraction;
import io.github.fablabsmc.fablabs.api.fluid.v1.volume.ImmutableFluidVolume;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class BucketFluidVolume extends ImmutableFluidVolume /*unfortunately, due to the nature of items in mc and how potions are implemented, it must be immutable*/ {
	public BucketFluidVolume(ItemStack buckets) {
		super(((BucketItemAccess) buckets.getItem()).getFluid(), (buckets.getItem() == Items.BUCKET ? Fraction.ZERO : Fraction.ONE).multiply(buckets.getCount()), buckets.getOrCreateSubTag("fluid_data"));
	}
}
