package io.github.fablabsmc.fablabs.api.fluidvolume.v1.volume;

import io.github.fablabsmc.fablabs.api.fluidvolume.v1.math.Fraction;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.volume.api.FluidContainer;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.volume.api.FluidVolume;
import java.util.*;
import java.util.function.BiFunction;

/**
 * a fluid container that holds multiple fluid containers in itself
 */
public class MultiFluidContainer extends AbstractCollection<FluidContainer> implements FluidContainer {
	private final Collection<FluidContainer> containers;
	private final Collection<FluidContainer> immutableContainers;

	/**
	 * create a multifluid container that wraps the given array
	 *
	 * @param containers the subcontainers for this instance
	 */
	public MultiFluidContainer(FluidContainer... containers) {
		this(Arrays.asList(containers));
	}

	/**
	 * create a multifluid container that wraps the given collection
	 *
	 * @param containers the subcontainers for this instance
	 */
	public MultiFluidContainer(Collection<FluidContainer> containers) {
		this.containers = containers;
		this.immutableContainers = Collections.unmodifiableCollection(containers);
	}

	@Override
	public Fraction merge(FluidVolume volume) {
		return this.combine(volume, FluidContainer::merge);
	}

	protected Fraction combine(FluidVolume volume, BiFunction<FluidContainer, FluidVolume, Fraction> combiner) {
		Fraction toMerge = volume.getTotalVolume();
		if (toMerge.equals(Fraction.ZERO)) return Fraction.ZERO;
		for (FluidContainer container : this.containers) {
			Fraction fraction = combiner.apply(container, volume.of(toMerge));
			toMerge = toMerge.subtract(fraction);
			if (toMerge.equals(Fraction.ZERO)) return volume.getTotalVolume();
		}
		return volume.getTotalVolume().subtract(toMerge);
	}

	@Override
	public Fraction drain(FluidVolume volume) {
		return this.combine(volume, FluidContainer::drain);
	}

	@Override
	public FluidContainer draw(Fraction fraction) {
		List<FluidContainer> draws = new ArrayList<>();
		for (FluidContainer container : this.containers) {
			if (fraction.equals(Fraction.ZERO)) break;
			FluidContainer drained = container.draw(fraction);
			fraction = fraction.subtract(drained.getTotalVolume());
			draws.add(drained);
		}
		return new MultiFluidContainer(draws);
	}

	@Override
	public Collection<FluidContainer> subContainers() {
		return this.immutableContainers;
	}

	@Override
	public Fraction getTotalVolume() {
		Fraction fraction = Fraction.ZERO;
		for (FluidContainer container : this.containers) {
			fraction = fraction.add(container.getTotalVolume());
		}
		return fraction;
	}

	@Override
	public Iterator<FluidContainer> iterator() {
		return this.containers.iterator();
	}

	@Override
	public int size() {
		return this.containers.size();
	}

	@Override
	public boolean isEmpty() {
		return this.containers.stream().allMatch(FluidContainer::isEmpty);
	}

	@Override
	public String toString() {
		return "MultiFluidContainer{" + "containers=" + this.containers + '}';
	}

	@Override
	public int hashCode() {
		int result = this.containers != null ? this.containers.hashCode() : 0;
		result = 31 * result + (this.immutableContainers != null ? this.immutableContainers.hashCode() : 0);
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		MultiFluidContainer that = (MultiFluidContainer) o;
		return Objects.equals(this.containers, that.containers);
	}
}
