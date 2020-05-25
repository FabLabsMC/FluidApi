package io.github.fablabsmc.fablabs.api.fluid.v1.volume;

import io.github.fablabsmc.fablabs.api.fluid.v1.math.Fraction;
import io.github.fablabsmc.fablabs.api.fluid.v1.volume.api.FluidVolume;
import io.github.fablabsmc.fablabs.api.fluid.v1.volume.api.FluidContainer;

import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundTag;

/**
 * a fluid volume whose fluid, contents, and NBT cannot change.
 */
public class ImmutableFluidVolume extends FluidVolume {
	public static final FluidVolume EMPTY = new ImmutableFluidVolume();

	/**
	 * create an immutable fluid volume with no fluid (empty), no amount, and no data.
	 */
	public ImmutableFluidVolume() {
	}

	/**
	 * create an immutable fluid volume.
	 */
	public ImmutableFluidVolume(Fluid fluid, Fraction amount, CompoundTag tag) {
		super(fluid, amount, tag);
	}

	/**
	 * create an immutable fluid volume with no data.
	 */
	public ImmutableFluidVolume(Fluid fluid, Fraction amount) {
		super(fluid, amount);
	}

	@Override
	public Fraction merge(FluidVolume volume) {
		return Fraction.ZERO;
	}

	@Override
	public FluidVolume drain(FluidVolume volume) {
		return EMPTY;
	}

	@Override
	public FluidContainer draw(Fraction fraction) {
		return EMPTY;
	}

	@Override
	public CompoundTag getData() {
		return this.tag.copy();
	}
}
