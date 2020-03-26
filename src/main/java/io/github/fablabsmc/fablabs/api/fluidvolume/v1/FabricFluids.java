package io.github.fablabsmc.fablabs.api.fluidvolume.v1;

import java.util.function.BiFunction;

import net.minecraft.fluid.Fluid;
import net.minecraft.util.Identifier;

public interface FabricFluids {
	//TODO: getInstance

	void registerProperty(Identifier id, BiFunction<FluidVolume, FluidVolume, Boolean> mergeHandler, Fluid... fluids);

	void registerUniveralProperty(Identifier id, BiFunction<FluidVolume, FluidVolume, Boolean> mergeHander);
}
