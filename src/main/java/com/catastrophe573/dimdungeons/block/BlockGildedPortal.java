package com.catastrophe573.dimdungeons.block;

import com.catastrophe573.dimdungeons.DimDungeons;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

import net.minecraft.world.level.block.state.BlockBehaviour;

public class BlockGildedPortal extends Block
{
    public static String REG_NAME = "block_gilded_portal";

    public BlockGildedPortal()
    {
	super(BlockBehaviour.Properties.of(Material.STONE).strength(2).sound(SoundType.METAL));
	this.setRegistryName(DimDungeons.MOD_ID, REG_NAME);
    }

    @Override
    public RenderShape getRenderShape(BlockState iBlockState)
    {
	return RenderShape.MODEL;
    }
}