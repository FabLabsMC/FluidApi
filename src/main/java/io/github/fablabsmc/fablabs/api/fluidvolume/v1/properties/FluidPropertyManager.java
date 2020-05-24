package io.github.fablabsmc.fablabs.api.fluidvolume.v1.properties;

import java.util.HashMap;
import java.util.Map;

import io.github.fablabsmc.fablabs.api.fluidvolume.v1.math.Fraction;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.properties.potions.CustomPotionColorProperty;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.properties.potions.PotionFluidProperty;

import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

/**
 * A utility class used for describing the behavior for merging.
 */
public class FluidPropertyManager {
	public static final FluidPropertyManager INSTANCE = new FluidPropertyManager();

	static {
		INSTANCE.register("CustomPotionEffects", new CustomPotionColorProperty());
		INSTANCE.register("Potion", new PotionFluidProperty());
		INSTANCE.register("CustomPotionColor", new CustomPotionColorProperty());
	}

	private final Map<String, FluidProperty<?>> properties = new HashMap<>();

	public <T extends Tag> void register(String id, FluidProperty<T> property) {
		this.properties.put(id, property);
	}

	/**
	 * merge the nbt data of 2 fluids.
	 *
	 * @param fluidA  the type of the fluid that is being combined
	 * @param amountA the amount of the original fluid
	 * @param amountB the amount of the fluid being merged
	 * @param a       the data of the original fluid
	 * @param b       the data of the fluid being merged
	 * @return a newly created compound tag representing the merged data
	 */
	public CompoundTag merge(Fluid fluidA, Fraction amountA, Fraction amountB, CompoundTag a, CompoundTag b) {
		CompoundTag tag = new CompoundTag();

		for (String key : a.getKeys()) {
			this.merge(tag, key, fluidA, amountA, amountB, a, b);
		}

		for (String key : b.getKeys()) {
			if (!tag.contains(key)) {
				this.merge(tag, key, fluidA, amountA, amountB, a, b);
			}
		}

		return tag;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	protected void merge(CompoundTag tag, String key, Fluid fluidA, Fraction amountA, Fraction amountB, CompoundTag a, CompoundTag b) {
		if (a.get(key) == null) {
			tag.put(key, b.get(key));
			return;
		}

		if (b.get(key) == null) {
			tag.put(key, a.get(key));
			return;
		}

		FluidProperty property = this.properties.get(key);
		tag.put(key, property.merge(fluidA, amountA, amountB, a.get(key), b.get(key)));
	}

	/**
	 * checks if the data between 2 fluids are incompatible with one another.
	 *
	 * @param fluidA the fluid
	 * @param a      the data in original fluid
	 * @param b      the data in the fluid being merged
	 * @return true if the data values are compatible with one another
	 */
	public boolean areCompatible(Fluid fluidA, CompoundTag a, CompoundTag b) {
		for (String key : a.getKeys()) {
			if (this.areIncompatible(key, fluidA, a, b)) {
				return false;
			}
		}

		for (String key : b.getKeys()) {
			if (this.areIncompatible(key, fluidA, a, b)) {
				return false;
			}
		}

		return true;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	protected boolean areIncompatible(String key, Fluid fluidA, CompoundTag a, CompoundTag b) {
		FluidProperty property = this.properties.get(key);
		return !property.areCompatible(fluidA, a.get(key), b.get(key));
	}
}
