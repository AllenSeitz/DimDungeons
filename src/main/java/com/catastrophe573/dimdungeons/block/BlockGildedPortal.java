package com.catastrophe573.dimdungeons.block;

import com.catastrophe573.dimdungeons.DimDungeons;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

import net.minecraft.block.AbstractBlock;

public class BlockGildedPortal extends Block
{
    public static String REG_NAME = "block_gilded_portal";
    
    public BlockGildedPortal()
    {
	super(AbstractBlock.Properties.create(Material.ROCK).hardnessAndResistance(2).sound(SoundType.METAL));
	this.setRegistryName(DimDungeons.MOD_ID, REG_NAME);
    }

    @Override
    public BlockRenderType getRenderType(BlockState iBlockState)
    {
	return BlockRenderType.MODEL;
    }
}