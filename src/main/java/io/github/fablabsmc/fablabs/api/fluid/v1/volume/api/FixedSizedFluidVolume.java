package io.github.fablabsmc.fablabs.api.fluid.v1.volume.api;

import io.github.fablabsmc.fablabs.api.fluid.v1.math.Fraction;

/**
 * a fluid volume who's size is limited.
 */
public interface FixedSizedFluidVolume extends SingleFluidContainer {
	/**
	 * @return the maximum amount of fluid this container may hold.
	 */
	Fraction getMax();
}
