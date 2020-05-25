package io.github.fablabsmc.fablabs.api.fluid.v1.volume.api;

import io.github.fablabsmc.fablabs.api.fluid.v1.math.Fraction;

import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundTag;

/**
 * a mutable volume which has a fixed size, and can only be extracted/inserted into in descrete quanties.
 * eg. Cauldron can only be interacted with in thirds (bottles)
 */
public class FixedFractionFixedSizeFluidVolume extends SimpleFixedSizedFluidVolume {
	public final Fraction fraction;

	/**
	 * create a pre-filled fixed fraction fixed size fluid volume.
	 *
	 * @param max      the maximum quantity of fluid this volume can hold
	 * @param fraction the smallest unit in which this volume can be interacted with
	 */
	public FixedFractionFixedSizeFluidVolume(Fluid fluid, Fraction amount, Fraction max, Fraction fraction) {
		super(fluid, amount, max);
		this.fraction = fraction;
	}

	/**
	 * create a pre-filled fixed fraction fixed size fluid volume and data.
	 *
	 * @param max      the maximum quantity of fluid this volume can hold
	 * @param fraction the smallest unit in which this volume can be interacted with
	 */
	public FixedFractionFixedSizeFluidVolume(Fluid fluid, Fraction amount, CompoundTag tag, Fraction max, Fraction fraction) {
		super(fluid, amount, tag, max);
		this.fraction = fraction;
	}

	/**
	 * create a fixed fraction fixed size fluid volume.
	 *
	 * @param max      the maximum quantity of fluid this volume can hold
	 * @param fraction the smallest unit in which this volume can be interacted with
	 */
	public FixedFractionFixedSizeFluidVolume(Fraction max, Fraction fraction) {
		super(max);
		this.fraction = fraction;
	}

	@Override
	public Fraction merge(FluidVolume volume) {
		FluidVolume floor = volume.of(volume.getTotalVolume().floorNearest(fraction));
		return super.merge(floor);
	}

	@Override
	public FluidVolume drain(FluidVolume volume) {
		return super.drain(volume.of(volume.getTotalVolume().floorNearest(fraction)));
	}

	@Override
	public FluidContainer draw(Fraction fraction) {
		return super.draw(fraction.floorNearest(fraction));
	}
}
