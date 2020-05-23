package io.github.fablabsmc.fablabs.api.fluidvolume.v1.mixin;

import io.github.fablabsmc.fablabs.api.fluidvolume.v1.containers.SidedFluidContainer;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.volume.api.FluidVolume;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.volume.vanilla.CauldronFluidVolume;
import net.minecraft.block.BlockState;
import net.minecraft.block.CauldronBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin (CauldronBlock.class) // cauldrons can hold fluids (well, just water), in fact they're the only vanilla block that stores them as far as I'm aware
public class CauldronBlockMixin implements SidedFluidContainer {
	@Override
	public FluidVolume getContainer(World world, BlockState state, BlockPos pos, Direction side) {
		return new CauldronFluidVolume(pos, world, state);
	}
}
