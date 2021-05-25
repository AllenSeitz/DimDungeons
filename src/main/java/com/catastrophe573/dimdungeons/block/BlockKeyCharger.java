package com.catastrophe573.dimdungeons.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

public class BlockKeyCharger extends Block
{
    protected static final VoxelShape BASE_SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 13.0D, 16.0D);

    public BlockKeyCharger()
    {
	super(Block.Properties.create(Material.GLASS).hardnessAndResistance(10).sound(SoundType.METAL));
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
	return BASE_SHAPE;
    }

    @Override
    public BlockRenderType getRenderType(BlockState iBlockState)
    {
	return BlockRenderType.MODEL;
    }
}