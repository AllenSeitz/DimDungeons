package com.catastrophe573.dimdungeons.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;

import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jspecify.annotations.NonNull;

public class BlockKeyCharger extends Block
{
	protected static final VoxelShape BASE_SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 13.0D, 16.0D);

	public BlockKeyCharger(BlockBehaviour.Properties props)
	{
		super(props);
	}

	@Override
	protected boolean useShapeForLightOcclusion(@NonNull BlockState state)
	{
		return true;
	}

	@Override
	public @NonNull VoxelShape getShape(@NonNull BlockState state, @NonNull BlockGetter level, @NonNull BlockPos pos, @NonNull CollisionContext context)
	{
		return BASE_SHAPE;
	}

	@Override
	public @NonNull RenderShape getRenderShape(@NonNull BlockState iBlockState)
	{
		return RenderShape.MODEL;
	}
}