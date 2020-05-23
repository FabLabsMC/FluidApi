package io.github.fablabsmc.fablabs.api.fluidvolume.v1;

import io.github.fablabsmc.fablabs.api.fluidvolume.v1.containers.ItemFluidContainer;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.containers.SidedFluidContainer;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.volume.ImmutableFluidVolume;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.volume.api.FluidContainer;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.world.WorldFluidCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public final class FluidView {
	private FluidView() {}

	/**
	 * attempt to get a fluid container from the world for a given face
	 *
	 * @param world the world
	 * @param pos the position
	 * @param side the side of the block to access
	 * @return ImmutableFluidVolume#EMPTY if no fluid container exists for the given side
	 */
	public static FluidContainer getContainer(World world, BlockPos pos, Direction side) {
		BlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (block instanceof SidedFluidContainer) {
			return ((SidedFluidContainer) block).getContainer(world, state, pos, side);
		}
		SidedFluidContainer sidedContainer = WorldFluidCallback.EVENT.invoker().getContainer(world, pos);
		if (sidedContainer != null) {
			FluidContainer container = sidedContainer.getContainer(world, state, pos, side);
			if (container != null) {
				return container;
			}
		}
		return ImmutableFluidVolume.EMPTY;
	}

	/**
	 * attempt to get a fluid container from an ItemStack
	 *
	 * @param stack the stack
	 * @return ImmutableFluidVolume#EMPTY if no fluid container exists for the itemstack
	 */
	public static FluidContainer getContainer(ItemStack stack) {
		Item item = stack.getItem();
		if (item instanceof ItemFluidContainer) {
			FluidContainer container = ((ItemFluidContainer) item).getVolume(stack);
			if (container != null) return container;
		}
		return ImmutableFluidVolume.EMPTY;
	}

}
