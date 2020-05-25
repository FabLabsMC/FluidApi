package io.github.fablabsmc.fablabs.api.fluid.v1.properties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.fablabsmc.fablabs.api.fluid.v1.math.Fraction;
import io.github.fablabsmc.fablabs.api.fluid.v1.properties.potions.CustomPotionColorProperty;
import io.github.fablabsmc.fablabs.api.fluid.v1.properties.potions.PotionFluidProperty;

import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.text.Text;

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
	 * @param fluid  the type of the fluid that is being combined
	 * @param amountA the amount of the original fluid
	 * @param amountB the amount of the fluid being merged
	 * @param aData   the data of the original fluid
	 * @param bData   the data of the fluid being merged
	 * @return a newly created compound tag representing the merged data
	 */
	public CompoundTag merge(Fluid fluid, Fraction amountA, Fraction amountB, CompoundTag aData, CompoundTag bData) {
		CompoundTag tag = new CompoundTag();

		for (String key : aData.getKeys()) {
			this.merge(tag, key, fluid, amountA, amountB, aData, bData);
		}

		for (String key : bData.getKeys()) {
			if (!tag.contains(key)) {
				this.merge(tag, key, fluid, amountA, amountB, aData, bData);
			}
		}

		return tag;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	protected void merge(CompoundTag tag, String key, Fluid fluid, Fraction amountA, Fraction amountB, CompoundTag aData, CompoundTag bData) {
		if (aData.get(key) == null) {
			tag.put(key, bData.get(key));
			return;
		}

		if (bData.get(key) == null) {
			tag.put(key, aData.get(key));
			return;
		}

		FluidProperty property = this.properties.get(key);
		tag.put(key, property.merge(fluid, amountA, amountB, aData.get(key), bData.get(key)));
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

	@SuppressWarnings("unchecked")
	public <T extends Tag> List<Text> getPropertyTooltip(CompoundTag tag) {
		List<Text> tooltip = new ArrayList<>();

		for (String key : tag.getKeys()) {
			FluidProperty<T> property = (FluidProperty<T>) properties.get(key);
			tooltip.add(property.toText((T) tag.get(key)));
		}

		return tooltip;
	}
}
