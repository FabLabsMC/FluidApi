package io.github.fablabsmc.fablabs.api.fluidvolume.v1.access;

import net.minecraft.fluid.Fluid;

/**
 * a duck interface for getting the fluid from a bucket
 */
public interface BucketItemAccess {
	Fluid getFluid();
}
