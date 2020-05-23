package io.github.fablabsmc.fablabs.api.fluidvolume.v1.volume.vanilla;

import io.github.fablabsmc.fablabs.api.fluidvolume.v1.math.Fraction;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.volume.api.FixedFractionFixedSizeFluidVolume;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.volume.api.FluidVolume;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CauldronBlock;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * A fluid container for cauldrons, does not support tags, so hot water and cold water is just water.
 */
public class CauldronFluidVolume extends FixedFractionFixedSizeFluidVolume {
	public static final int MAX_LEVEL = CauldronBlock.LEVEL.getValues().size() - 1; // incase a mod decides to change the cauldron level
	private static final Fraction ONE_THIRD = Fraction.of(1, MAX_LEVEL); // vanilla is 1/3

	// location of the cauldron
	private final BlockPos pos;
	// the world in which the cauldron is in
	private final World world;

	public CauldronFluidVolume(BlockPos pos, World world, BlockState state) {
		super(Fluids.WATER, Fraction.of(state.get(CauldronBlock.LEVEL), MAX_LEVEL), Fraction.ONE, ONE_THIRD);
		this.pos = pos;
		this.world = world;
	}

	@Override
	public Fraction merge(FluidVolume volume) {
		if (volume.getFluid() != Fluids.WATER) return Fraction.ZERO;
		return super.merge(volume);
	}

	@Override
	protected void resync() {
		this.world.setBlockState(this.pos, Blocks.CAULDRON.getDefaultState().with(CauldronBlock.LEVEL, this.amount.getNumerator(3)));
	}
}
