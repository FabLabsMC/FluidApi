package io.github.fablabsmc.fablabs.api.fluid.v1.properties.potions;

import io.github.fablabsmc.fablabs.api.fluid.v1.math.Fraction;
import io.github.fablabsmc.fablabs.api.fluid.v1.properties.FluidProperty;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

public class CustomPotionEffectsFluidProperty implements FluidProperty<ListTag> {
	@Override
	public ListTag merge(Fluid fluid, Fraction amountA, Fraction amountB, ListTag aData, ListTag bData) {
		ListTag tags = aData.copy();

		for (Tag tag : aData) {
			tags.add(tag.copy());
		}

		return tags;
	}

	@Override
	public boolean areCompatible(Fluid fluid, ListTag aData, ListTag bData) {
		IntSet set = new IntOpenHashSet();

		for (Tag tag : aData) {
			CompoundTag effect = (CompoundTag) tag;
			set.add(effect.getInt("Id"));
		}

		for (Tag tag : bData) {
			CompoundTag effect = (CompoundTag) tag;

			if (set.contains(effect.getInt("Id"))) {
				return false;
			}
		}

		return true;
	}
}
