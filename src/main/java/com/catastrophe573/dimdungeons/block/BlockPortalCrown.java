package com.catastrophe573.dimdungeons.block;

import com.catastrophe573.dimdungeons.DimDungeons;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.WallBlock;
import net.minecraft.block.material.Material;

public class BlockPortalCrown extends WallBlock
{
    public static String REG_NAME = "block_portal_crown";
    
    public BlockPortalCrown()
    {
	//super(Block.Builder.create(Material.ROCK).hardnessAndResistance(2).sound(SoundType.METAL));
	super(Block.Properties.create(Material.ROCK).hardnessAndResistance(2).sound(SoundType.METAL));
	this.setRegistryName(DimDungeons.MOD_ID, REG_NAME);    
    }
}