package com.catastrophe573.dimdungeons.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;

import net.minecraft.world.level.block.state.BlockBehaviour;

public class BlockKeyCharger extends Block
{
    protected static final VoxelShape BASE_SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 13.0D, 16.0D);

    public BlockKeyCharger()
    {
	super(BlockBehaviour.Properties.of(Material.STONE).strength(3).sound(SoundType.METAL));
    }

    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
	return BASE_SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState iBlockState)
    {
	return RenderShape.MODEL;
    }    
}