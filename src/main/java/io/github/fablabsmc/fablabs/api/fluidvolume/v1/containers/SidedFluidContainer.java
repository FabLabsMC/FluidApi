package io.github.fablabsmc.fablabs.api.fluidvolume.v1.containers;

import io.github.fablabsmc.fablabs.api.fluidvolume.v1.volume.api.FluidContainer;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

/**
 * implemented on the {@link net.minecraft.block.Block} class, this interface states
 * that the block may have a container for a blockstate of it's type
 */
public interface SidedFluidContainer {
	/**
	 * get the fluid volume for the given side or null if none found
	 */
	FluidContainer getContainer(World world, BlockState state, BlockPos pos, Direction side);
}
