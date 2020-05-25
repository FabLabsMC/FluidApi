package io.github.fablabsmc.fablabs.api.fluid.v1.volume;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;

import io.github.fablabsmc.fablabs.api.fluid.v1.math.Fraction;
import io.github.fablabsmc.fablabs.api.fluid.v1.volume.api.FluidVolume;
import io.github.fablabsmc.fablabs.api.fluid.v1.volume.api.FluidContainer;

import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

/**
 * a fluid container that holds multiple fluid containers in itself.
 */
public class MultiFluidContainer extends AbstractCollection<FluidContainer> implements FluidContainer {
	protected final Collection<FluidContainer> containers;

	/**
	 * create a multifluid container that wraps the given array.
	 *
	 * @param containers the subcontainers for this instance
	 */
	public MultiFluidContainer(FluidContainer... containers) {
		this(Arrays.asList(containers));
	}

	/**
	 * create a multifluid container that wraps the given collection.
	 *
	 * @param containers the subcontainers for this instance
	 */
	public MultiFluidContainer(Collection<FluidContainer> containers) {
		this.containers = Collections.unmodifiableCollection(containers);
	}

	@Override
	public Fraction merge(FluidVolume volume) {
		return this.combine(volume, FluidContainer::merge);
	}

	protected Fraction combine(FluidVolume volume, BiFunction<FluidContainer, FluidVolume, Fraction> combiner) {
		Fraction toMerge = volume.getTotalVolume();

		if (toMerge.equals(Fraction.ZERO)) {
			return Fraction.ZERO;
		}

		for (FluidContainer container : containers) {
			Fraction fraction = combiner.apply(container, volume.of(toMerge));
			this.update(container);
			toMerge = toMerge.subtract(fraction);
			if (toMerge.equals(Fraction.ZERO)) return volume.getTotalVolume();
		}

		return volume.getTotalVolume().subtract(toMerge);
	}

	protected void update(FluidContainer container) {
	}

	@Override
	public FluidVolume drain(FluidVolume volume) {
		if (volume.isEmpty()) {
			return ImmutableFluidVolume.EMPTY;
		}

		FluidVolume fluidVolume = volume.copy();

		for (FluidContainer container : containers) {
			FluidVolume fraction = container.drain(volume);
			this.update(container);
			fluidVolume.drain(fraction);

			if (fluidVolume.isEmpty()) {
				return volume;
			}
		}

		return volume.drain(fluidVolume);
	}

	@Override
	public FluidContainer draw(Fraction fraction) {
		List<FluidContainer> draws = new ArrayList<>();

		for (FluidContainer container : containers) {
			if (fraction.equals(Fraction.ZERO)) break;
			FluidContainer drained = container.draw(fraction);
			this.update(container);
			fraction = fraction.subtract(drained.getTotalVolume());
			draws.add(drained);
		}

		return new MultiFluidContainer(draws);
	}

	@Override
	public Collection<FluidContainer> subContainers() {
		return containers;
	}

	@Override
	public Fraction getTotalVolume() {
		Fraction fraction = Fraction.ZERO;

		for (FluidContainer container : containers) {
			fraction = fraction.add(container.getTotalVolume());
		}

		return fraction;
	}

	@Override
	public Text toText() {
		Text text = new LiteralText("");
		boolean first = true;

		for (FluidContainer container : this) {
			if (!container.isEmpty()) {
				if (!first) {
					text.append(new TranslatableText("fabric.fluid.and"));
				} else {
					first = false;
				}

				text.append(container.toText());
			}
		}

		return text;
	}

	@Override
	public Iterator<FluidContainer> iterator() {
		return containers.iterator();
	}

	@Override
	public int size() {
		return containers.size();
	}

	@Override
	public boolean isEmpty() {
		for (FluidContainer container : this) {
			if (!container.isEmpty()) return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "MultiFluidContainer{" + "containers=" + containers + '}';
	}

	@Override
	public int hashCode() {
		int result = containers != null ? containers.hashCode() : 0;
		result = 31 * result + containers.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;

		if (!(o instanceof MultiFluidContainer)) return false;

		MultiFluidContainer that = (MultiFluidContainer) o;

		if (that.containers.size() != containers.size()) {
			return false;
		}

		Iterator<FluidContainer> iterator = that.containers.iterator();
		Iterator<FluidContainer> thisIterator = containers.iterator();

		while (iterator.hasNext()) {
			if (!iterator.next().equals(thisIterator.next())) {
				return false;
			}
		}

		return true;
	}
}
