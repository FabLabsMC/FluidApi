package io.github.fablabsmc.fablabs.api.fluid.v1.properties.potions;

import java.util.ArrayList;
import java.util.List;

import io.github.fablabsmc.fablabs.api.fluid.v1.math.Fraction;
import io.github.fablabsmc.fablabs.api.fluid.v1.properties.FluidProperty;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.potion.PotionUtil;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

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

	@Environment(EnvType.CLIENT)
	@Override
	public List<Text> getTooltipText(ListTag data) {
		//TODO: I think this is the best way to do it without just duping the vanilla code?
		ItemStack stack = new ItemStack(Items.POTION);
		stack.getOrCreateTag().put("CustomPotionEffects", data);
		List<Text> tooltip = new ArrayList<>();
		PotionUtil.buildTooltip(stack, tooltip, 1);
		return tooltip;
	}
}
