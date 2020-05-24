package io.github.fablabsmc.fablabs.api.fluidvolume.v1.world;

import java.util.ArrayList;
import java.util.List;

import io.github.fablabsmc.fablabs.api.fluidvolume.v1.FluidHelper;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.volume.ImmutableFluidVolume;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.volume.MultiFluidContainer;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.volume.api.FluidContainer;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * this is a callback that is invoked in {@link FluidHelper}.
 * when a mod attempts to find a fluid container in the world, here, mods can add custom handling for when
 * blocks aren't sufficient for whatever reason.
 */
public interface WorldFluidCallback {
	Event<WorldFluidCallback> EVENT = EventFactory.createArrayBacked(WorldFluidCallback.class, c -> (world, pos) -> {
		List<FluidContainer> provider = new ArrayList<>();

		for (WorldFluidCallback callback : c) {
			FluidContainer container = callback.getContainer(world, pos);

			if (container != null) {
				provider.add(container);
			}
		}

		if (provider.isEmpty()) {
			return ImmutableFluidVolume.EMPTY;
		} else if (provider.size() == 1) {
			return provider.get(0);
		} else {
			return new MultiFluidContainer(provider);
		}
	});

	/**
	 * find a fluid container at the given position.
	 *
	 * @return null if none exists
	 */
	FluidContainer getContainer(World world, BlockPos position);
}
