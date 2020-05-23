package io.github.fablabsmc.fablabs.api.fluidvolume.v1.world;

import io.github.fablabsmc.fablabs.api.fluidvolume.v1.containers.SidedFluidContainer;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * this is a callback that is invoked in {@link io.github.fablabsmc.fablabs.api.fluidvolume.v1.FluidView}
 * when a mod attempts to find a fluid container in the world, here, mods can add custom handling for when
 * blocks aren't sufficient for whatever reason.
 */
public interface WorldFluidCallback {
	Event<WorldFluidCallback> EVENT = EventFactory.createArrayBacked(WorldFluidCallback.class, c -> (w, b) -> {
		for (WorldFluidCallback callback : c) {
			SidedFluidContainer container = callback.getContainer(w, b);
			if (container != null) return container;
		}
		return null;
	});


	/**
	 * find a fluid container at the given position
	 *
	 * @return null if none exists
	 */
	SidedFluidContainer getContainer(World world, BlockPos position);
}
