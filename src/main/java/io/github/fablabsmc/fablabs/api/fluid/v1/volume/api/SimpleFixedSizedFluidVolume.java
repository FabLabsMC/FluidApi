package io.github.fablabsmc.fablabs.api.fluid.v1.volume.api;

import io.github.fablabsmc.fablabs.api.fluid.v1.math.Fraction;

import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundTag;

/**
 * a fluid contained with a fixed size.
 */
public class SimpleFixedSizedFluidVolume extends FluidVolume implements FixedSizedFluidVolume {
	public final Fraction max;

	/**
	 * @param max the maximum amount of fluid this volume may hold.
	 */
	public SimpleFixedSizedFluidVolume(Fluid fluid, Fraction amount, Fraction max) {
		super(fluid, amount);
		this.max = max;
	}

	/**
	 * @param max the maximum amount of fluid this volume may hold.
	 */
	public SimpleFixedSizedFluidVolume(Fluid fluid, Fraction amount, CompoundTag tag, Fraction max) {
		super(fluid, amount, tag);
		this.max = max;
	}

	/**
	 * @param max the maximum amount of fluid this volume may hold.
	 */
	public SimpleFixedSizedFluidVolume(Fraction max) {
		this.max = max;
	}

	@Override
	public Fraction merge(FluidVolume volume) {
		if (this.max.isGreaterThan(this.getTotalVolume().add(volume.getTotalVolume()))) {
			return super.merge(volume); // underfill
		} else {
			FluidVolume copy = volume.copy();
			copy.amount = this.max.subtract(this.amount);
			return super.merge(copy); // overfill
		}
	}

	@Override
	public Fraction getMax() {
		return this.max;
	}
}
