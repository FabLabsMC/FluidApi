package io.github.fablabsmc.fablabs.api.fluidvolume.v1;

import java.util.function.BiPredicate;

import net.minecraft.fluid.Fluid;
import net.minecraft.util.Identifier;

public interface FabricFluids {
	//TODO: getInstance

	void registerProperty(Identifier id, BiPredicate<FluidVolume, FluidVolume> mergeHandler, Fluid... fluids);

	void registerUniversalProperty(Identifier id, BiPredicate<FluidVolume, FluidVolume> mergeHandler);
}
