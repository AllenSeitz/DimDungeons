package com.catastrophe573.dimdungeons.block;

import com.catastrophe573.dimdungeons.DimDungeons;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.material.Material;

import net.minecraft.world.level.block.state.BlockBehaviour;

public class BlockPortalCrown extends WallBlock
{
    public static String REG_NAME = "block_portal_crown";

    public BlockPortalCrown()
    {
	super(BlockBehaviour.Properties.of(Material.STONE).strength(2).sound(SoundType.METAL));
	this.setRegistryName(DimDungeons.MOD_ID, REG_NAME);
    }
}