package io.github.fablabsmc.fablabs.api.fluidvolume.v1.volume.api;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

import com.google.common.collect.Iterators;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.math.Fraction;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.properties.FluidPropertyManager;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * a mutable representation of a quantity and it's data of a fluid.
 * this class can and should be extended for custom logic when dealing with volume merging.
 *
 * @see #merge(FluidVolume)
 * @see #drain(FluidVolume)
 * @see #draw(Fraction)
 * @see FixedSizedFluidVolume
 */
public class FluidVolume extends AbstractCollection<FluidContainer> implements FluidContainer, SingleFluidContainer {
	// not nullable
	protected Fluid fluid;
	// not nullable
	protected Fraction amount;
	// not nullable
	protected CompoundTag tag;

	/**
	 * creates a new empty fluid container.
	 */
	public FluidVolume() {
		this(Fluids.EMPTY, Fraction.ZERO);
	}

	/**
	 * creates a new fluid container with an empty data tag.
	 *
	 * @param fluid  the fluid
	 * @param amount the amount of fluid contained, must be 0 if fluid is null
	 */
	public FluidVolume(Fluid fluid, Fraction amount) {
		this(fluid, amount, new CompoundTag());
	}

	/**
	 * creates a new fluid container with custom data.
	 *
	 * @param fluid  the fluid
	 * @param amount the amount of fluid contained, must be 0 if fluid is EMPTY
	 * @param tag    the data
	 */
	public FluidVolume(Fluid fluid, Fraction amount, CompoundTag tag) {
		if (fluid == Fluids.EMPTY && amount.getNumerator() != 0) {
			throw new IllegalArgumentException("EMPTY fluid must have zero amount!");
		}

		if (fluid == null) {
			throw new IllegalArgumentException("Fluid must not be null! Hint: Fluids.EMPTY");
		}

		this.fluid = fluid;
		this.amount = amount;
		this.tag = tag;
	}

	@Override
	public Fraction merge(FluidVolume volume) {
		if (volume.isEmpty()) {
			return Fraction.ZERO;
		}

		if (this.isEmpty()) { // empty
			this.fluid = volume.fluid;
			this.tag = volume.tag;
			this.resync();
			return this.amount = volume.amount;
		}

		if (this.fluid == volume.fluid && FluidPropertyManager.INSTANCE.areCompatible(volume.fluid, volume.tag, this.tag)) {
			this.tag = FluidPropertyManager.INSTANCE.merge(volume.fluid, this.getTotalVolume(), volume.getTotalVolume(), volume.tag, this.tag);
			this.amount = this.amount.add(volume.amount);
			this.resync();
			return volume.amount;
		}

		return Fraction.ZERO;
	}

	/**
	 * this method is invoked after any mutable changes to the volume.
	 */
	protected void resync() {
	}

	@Override
	public Fraction drain(FluidVolume volume) {
		if (volume.fluid != this.fluid) return Fraction.ZERO;
		Fraction fraction = volume.amount;

		if (fraction.isNegative()) {
			throw new UnsupportedOperationException("Fraction may not be negative, use merge for adding fluids to a container");
		}

		if (fraction.getNumerator() == 0) return fraction;

		if (fraction.isGreaterThanOrEqualTo(this.amount)) {
			Fraction remove = this.amount;
			this.amount = Fraction.ZERO;
			this.fluid = Fluids.EMPTY;
			this.tag = new CompoundTag(); // reset tag as tank is emptied
			this.resync();
			return remove;
		} else {
			this.amount = this.amount.subtract(fraction);
			this.resync();
			return fraction;
		}
	}

	@Override
	public FluidContainer draw(Fraction fraction) {
		return this.of(this.drain(this.of(fraction)));
	}

	@Override
	public Collection<FluidContainer> subContainers() {
		return this;
	}

	@Override
	public Fraction getTotalVolume() {
		return this.amount;
	}

	public FluidVolume of(Fraction amount) {
		if (amount.equals(Fraction.ZERO) || this.fluid == Fluids.EMPTY) {
			return new FluidVolume(Fluids.EMPTY, Fraction.ZERO, this.tag);
		}

		return new FluidVolume(this.fluid, amount, this.tag);
	}

	@Override
	public Fluid getFluid() {
		return this.fluid;
	}

	@Override
	public CompoundTag getData() {
		return this.tag;
	}

	/**
	 * clones the fluid volume, including it's custom data tag.
	 *
	 * @return a newly created object
	 */
	public FluidVolume copy() {
		return new FluidVolume(this.fluid, this.amount /*amount is immutable*/, this.tag.copy());
	}

	@Override
	public Iterator<FluidContainer> iterator() {
		return Iterators.singletonIterator(this);
	}

	@Override
	public int size() {
		return 1;
	}

	@Override
	public boolean isEmpty() {
		return this.fluid == null || this.fluid == Fluids.EMPTY || this.amount.getNumerator() == 0;
	}

	@Override
	public String toString() {
		return "FluidVolume{" + "fluid=" + this.fluid + ", amount=" + this.amount + ", tag=" + this.tag + '}';
	}

	public final void fromTag(CompoundTag tag) {
		String key = tag.getString("fluid");
		this.fluid = Registry.FLUID.get(new Identifier(key));
		this.amount = Fraction.fromTag(tag);
		this.tag = tag.getCompound("tag");
	}

	public final void toTag(CompoundTag tag) {
		tag.putString("fluid", Registry.FLUID.getId(this.fluid).toString());
		tag.put("tag", this.tag);
		this.amount.toTag(tag);
	}

	@Override
	public int hashCode() {
		int result = this.fluid != null ? this.fluid.hashCode() : 0;
		result = 31 * result + (this.amount != null ? this.amount.hashCode() : 0);
		result = 31 * result + (this.tag != null ? this.tag.hashCode() : 0);
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;

		FluidVolume volume = (FluidVolume) o;

		if (!(this.fluid == volume.fluid)) return false;
		if (!Objects.equals(this.amount, volume.amount)) return false;
		return Objects.equals(this.tag, volume.tag);
	}
}
