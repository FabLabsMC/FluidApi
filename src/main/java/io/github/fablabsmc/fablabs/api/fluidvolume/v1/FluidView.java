package io.github.fablabsmc.fablabs.api.fluidvolume.v1;

import java.util.ArrayList;
import java.util.List;

import io.github.fablabsmc.fablabs.api.fluidvolume.v1.containers.EntityFluidContainer;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.containers.ItemFluidContainer;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.containers.SidedFluidContainer;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.volume.ImmutableFluidVolume;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.volume.MultiFluidContainer;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.volume.api.FluidContainer;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.world.WorldFluidCallback;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public final class FluidView {
	/**
	 * attempt to get a fluid container from the world from a specific block (or entities) for a given face.
	 *
	 * @param world the world
	 * @param pos   the position
	 * @param side  the side of the block to access
	 * @return ImmutableFluidVolume#EMPTY if no fluid container exists for the given side
	 */
	public static FluidContainer getContainer(World world, BlockPos pos, Direction side) {
		BlockState state = world.getBlockState(pos);
		Block block = state.getBlock();

		FluidContainer container = ImmutableFluidVolume.EMPTY;

		if (block instanceof SidedFluidContainer) {
			container = ((SidedFluidContainer) block).getContainer(world, state, pos, side);
		} else {
			SidedFluidContainer sidedContainer = WorldFluidCallback.EVENT.invoker().getContainer(world, pos);

			if (sidedContainer != null) {
				container = sidedContainer.getContainer(world, state, pos, side);
			}
		}

		List<FluidContainer> containers = new ArrayList<>();

		for (Entity entity : world.getEntities((Entity) null, new Box(pos), e -> e instanceof EntityFluidContainer)) {
			containers.add(((EntityFluidContainer) entity).getContainer(side));
		}

		if (!containers.isEmpty()) {
			containers.add(container);
			return new MultiFluidContainer(containers);
		}

		return container;
	}

	/**
	 * attempt to get a fluid container from an ItemStack.
	 * because of the way buckets and potions are implemented, buckets and potions are ImmutableFluidVolumes, so plan accordingly!
	 *
	 * @param stack the stack with a count of one
	 * @return ImmutableFluidVolume#EMPTY if no fluid container exists for the itemstack
	 */
	public static FluidContainer getContainer(ItemStack stack) {
		Item item = stack.getItem();

		if (item instanceof ItemFluidContainer) {
			FluidContainer container = ((ItemFluidContainer) item).getVolume(stack);

			if (container != null) {
				return container;
			}
		}

		return ImmutableFluidVolume.EMPTY;
	}
}
