package io.github.fablabsmc.fablabs.api.fluidvolume.v1.properties.potions;

import io.github.fablabsmc.fablabs.api.fluidvolume.v1.math.Fraction;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.properties.FluidProperty;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

public class CustomPotionEffectsFluidProperty implements FluidProperty<ListTag> {
	@Override
	public ListTag merge(Fluid fluidA, Fraction amountA, Fraction amountB, ListTag a, ListTag b) {
		ListTag tags = a.copy();

		for (Tag tag : a) {
			tags.add(tag.copy());
		}

		return tags;
	}

	@Override
	public boolean areCompatible(Fluid fluidA, ListTag a, ListTag b) {
		IntSet set = new IntOpenHashSet();

		for (Tag tag : a) {
			CompoundTag effect = (CompoundTag) tag;
			set.add(effect.getInt("Id"));
		}

		for (Tag tag : b) {
			CompoundTag effect = (CompoundTag) tag;

			if (set.contains(effect.getInt("Id"))) {
				return false;
			}
		}

		return true;
	}
}
