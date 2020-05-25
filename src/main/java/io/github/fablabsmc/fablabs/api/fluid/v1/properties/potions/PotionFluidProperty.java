package io.github.fablabsmc.fablabs.api.fluid.v1.properties.potions;

import java.util.Collections;
import java.util.List;

import io.github.fablabsmc.fablabs.api.fluid.v1.math.Fraction;
import io.github.fablabsmc.fablabs.api.fluid.v1.properties.FluidProperty;

import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.StringTag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * A fluid property that stores the registered ID of the potion this fluid volume is holding.
 */
public class PotionFluidProperty implements FluidProperty<StringTag> {
	@Override
	public StringTag merge(Fluid fluid, Fraction amountA, Fraction amountB, StringTag aData, StringTag bData) {
		return aData.copy();
	}

	@Override
	public boolean areCompatible(Fluid fluid, StringTag aData, StringTag bData) {
		return aData.equals(bData);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public List<Text> getTooltipText(StringTag data) {
		return Collections.singletonList(new TranslatableText(Util.createTranslationKey("potion", new Identifier(data.asString()))).formatted(Formatting.GRAY));
	}
}
