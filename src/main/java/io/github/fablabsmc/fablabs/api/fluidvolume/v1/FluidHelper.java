package io.github.fablabsmc.fablabs.api.fluidvolume.v1;

import java.util.ArrayList;
import java.util.List;

import io.github.fablabsmc.fablabs.api.fluidvolume.v1.containers.EntityFluidContainerProvider;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.containers.ItemFluidContainerProvider;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.containers.BlockFluidContainerProvider;
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

public final class FluidHelper {
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

		if (block instanceof BlockFluidContainerProvider) {
			container = ((BlockFluidContainerProvider) block).getContainer(world, state, pos, side);
		} else {
			FluidContainer worldContainer = WorldFluidCallback.EVENT.invoker().getContainer(world, pos);

			if (worldContainer != null) {
				container = new MultiFluidContainer(container, worldContainer);
			}
		}

		List<FluidContainer> containers = new ArrayList<>();

		for (Entity entity : world.getEntities((Entity) null, new Box(pos), e -> e instanceof EntityFluidContainerProvider)) {
			containers.add(((EntityFluidContainerProvider) entity).getContainer(side));
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

		if (item instanceof ItemFluidContainerProvider) {
			FluidContainer container = ((ItemFluidContainerProvider) item).getVolume(stack);

			if (container != null) {
				return container;
			}
		}

		return ImmutableFluidVolume.EMPTY;
	}
}
