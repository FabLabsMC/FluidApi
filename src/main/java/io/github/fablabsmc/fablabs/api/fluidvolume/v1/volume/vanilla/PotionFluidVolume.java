package io.github.fablabsmc.fablabs.api.fluidvolume.v1.volume.vanilla;

import io.github.fablabsmc.fablabs.api.fluidvolume.v1.math.Fraction;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.volume.ImmutableFluidVolume;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;

/**
 * potions, in my mind atleast, are just water bottles with stuff in it
 *
 * @author HalfOf2
 */
public class PotionFluidVolume extends ImmutableFluidVolume /*unfortunately, due to the nature of items in mc and how potions are implemented, it must be immutable*/ {
	public PotionFluidVolume(ItemStack stack) {
		// do cauldrons decide how much 1 bottle holds, or do 3 bottles define how much 1 cauldron holds?
		super(Fluids.WATER, Fraction.of(stack.getCount(), CauldronFluidVolume.MAX_LEVEL), stack.getTag());
	}
}