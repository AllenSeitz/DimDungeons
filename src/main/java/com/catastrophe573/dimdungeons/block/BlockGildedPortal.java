package com.catastrophe573.dimdungeons.block;

import com.catastrophe573.dimdungeons.DimDungeons;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockGildedPortal extends Block
{
    public static String REG_NAME = "block_gilded_portal";
    
    public BlockGildedPortal()
    {
	//super(Block.Builder.create(Material.ROCK).hardnessAndResistance(2).sound(SoundType.METAL));
	super(Block.Properties.create(Material.ROCK).hardnessAndResistance(2).sound(SoundType.METAL));
	this.setRegistryName(DimDungeons.MOD_ID, REG_NAME);
    }

    // used by the renderer to control lighting and visibility of other blocks, also by
    // (eg) wall or fence to control whether the fence joins itself to this block
    // set to true because this block occupies the entire 1x1x1 space
    // not strictly required because the default (super method) is true
    @Override
    public boolean isFullCube(IBlockState iBlockState)
    {
	return true;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState iBlockState)
    {
	return EnumBlockRenderType.MODEL;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public BlockRenderLayer getRenderLayer()
    {
	return BlockRenderLayer.SOLID;
    }
}