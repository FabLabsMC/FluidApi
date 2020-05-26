package io.github.fablabsmc.fablabs.api.fluid.v1.volume.api;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import com.google.common.collect.Iterators;
import io.github.fablabsmc.fablabs.api.fluid.v1.event.client.FluidTooltipCallback;
import io.github.fablabsmc.fablabs.api.fluid.v1.math.Fraction;
import io.github.fablabsmc.fablabs.api.fluid.v1.properties.FluidPropertyManager;
import io.github.fablabsmc.fablabs.api.fluid.v1.volume.ImmutableFluidVolume;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * a mutable representation of a quantity and it's data of a fluid.
 * this class can and should be extended for custom logic when dealing with volume merging.
 *
 * @see #merge(FluidVolume)
 * @see #drain(FluidVolume)
 * @see #draw(Fraction)
 * @see SimpleFixedSizedFluidVolume
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
	 * if the fluid is Fluids#EMPTY, the fraction must be equal to 0 and no data must exist in the tag
	 *
	 * @param fluid  the fluid
	 * @param amount the amount of fluid contained, must be 0 if fluid is EMPTY
	 * @param tag    the data
	 */
	public FluidVolume(Fluid fluid, Fraction amount, CompoundTag tag) {
		if (fluid == null) {
			throw new IllegalArgumentException("Fluid must not be null! Hint: Fluids.EMPTY");
		}

		if (fluid == Fluids.EMPTY && (amount.getNumerator() != 0 || !tag.isEmpty())) {
			throw new IllegalArgumentException("EMPTY fluid must have zero amount!");
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

		if (isEmpty()) { // empty
			fluid = volume.fluid;
			tag = volume.tag;
			update();
			return amount = volume.amount;
		}

		if (fluid == volume.fluid && FluidPropertyManager.INSTANCE.areCompatible(volume.fluid, volume.tag, tag)) {
			tag = FluidPropertyManager.INSTANCE.merge(volume.fluid, getTotalVolume(), volume.getTotalVolume(), volume.tag, tag);
			amount = amount.add(volume.amount);
			update();
			return volume.amount;
		}

		return Fraction.ZERO;
	}

	/**
	 * this method is invoked after any mutations to the volume.
	 */
	protected void update() {
	}

	@Override
	public FluidVolume drain(FluidVolume volume) {
		if (volume.fluid != fluid) return ImmutableFluidVolume.EMPTY;
		Fraction fraction = volume.amount;

		if (fraction.isNegative()) {
			throw new UnsupportedOperationException("Fraction may not be negative, use merge for adding fluids to a container");
		}

		if (fraction.getNumerator() == 0) {
			return ImmutableFluidVolume.EMPTY;
		}

		if (fraction.isGreaterThanOrEqualTo(amount)) {
			FluidVolume drained = copy();
			amount = Fraction.ZERO;
			fluid = Fluids.EMPTY;
			tag = new CompoundTag(); // reset tag as tank is emptied
			update();
			return drained;
		} else {
			amount = amount.subtract(fraction);
			update();
			return of(fraction);
		}
	}

	@Override
	public FluidContainer draw(Fraction fraction) {
		return drain(of(fraction));
	}

	@Override
	public Collection<FluidContainer> subContainers() {
		return this;
	}

	@Override
	public Fraction getTotalVolume() {
		return amount;
	}

	public FluidVolume of(Fraction amount) {
		if (amount.equals(Fraction.ZERO) || fluid == Fluids.EMPTY) {
			return new FluidVolume(Fluids.EMPTY, Fraction.ZERO, tag);
		}

		return new FluidVolume(fluid, amount, tag);
	}

	@Override
	public Fluid getFluid() {
		return fluid;
	}

	//TODO: should this be exposed and mutable?
	@Override
	public CompoundTag getData() {
		return tag;
	}

	/**
	 * clones the fluid volume, including it's custom data tag.
	 *
	 * @return a newly created object
	 */
	public FluidVolume copy() {
		return new FluidVolume(fluid, amount /*amount is immutable*/, tag.copy());
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
		return fluid == null || fluid == Fluids.EMPTY || amount.getNumerator() == 0;
	}

	@Override
	public String toString() {
		return "FluidVolume{" + "fluid=" + fluid + ", amount=" + amount + ", tag=" + tag + '}';
	}

	@Environment(EnvType.CLIENT)
	public List<Text> getTooltip(TooltipContext context) {
		List<Text> tooltip = new ArrayList<>();
		tooltip.add(new TranslatableText(Util.createTranslationKey("fluid", Registry.FLUID.getId(fluid))));
		tooltip.add(new TranslatableText("text.fluid.amount", amount.toString()).formatted(Formatting.GRAY));
		tooltip.add(new LiteralText("")); //blank line
		FluidTooltipCallback.EVENT.invoker().getTooltip(this, context, tooltip);
		tooltip.add(new LiteralText("")); //blank line
		tooltip.addAll(FluidPropertyManager.INSTANCE.getPropertyTooltip(tag));
		return tooltip;
	}

	public final void fromTag(CompoundTag tag) {
		String key = tag.getString("fluid");
		fluid = Registry.FLUID.get(Identifier.tryParse(key));
		amount = Fraction.fromTag(tag);
		this.tag = tag.getCompound("tag");
	}

	public final void toTag(CompoundTag tag) {
		tag.putString("fluid", Registry.FLUID.getId(fluid).toString());
		tag.put("tag", tag);
		amount.toTag(tag);
	}

	@Override
	public int hashCode() {
		int result = fluid != null ? fluid.hashCode() : 0;
		result = 31 * result + (amount != null ? amount.hashCode() : 0);
		result = 31 * result + (tag != null ? tag.hashCode() : 0);
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof FluidVolume)) return false;

		FluidVolume volume = (FluidVolume) o;

		if (!(fluid == volume.fluid)) return false;
		if (!amount.equals(volume.amount)) return false;
		return Objects.equals(tag, volume.tag);
	}
}
