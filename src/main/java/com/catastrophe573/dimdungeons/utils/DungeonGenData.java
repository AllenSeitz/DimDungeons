package com.catastrophe573.dimdungeons.utils;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.DungeonConfig;

import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;

//Holder for passing data along during dungeon generation.
public class DungeonGenData
{
    //The key that was used to open the dungeon.
    public ItemStack keyItem;

    //The calculated point to teleport the player to when exiting the dungeon.
    public BlockPos returnPoint;
    public String returnDimension;

    // this is derived from the key, but stored here for convenience. remember theme < 1 means no theme
    public int dungeonTheme = -1;

    DungeonGenData()
    {
    }

    public static DungeonGenData Create()
    {
	return new DungeonGenData();
    }

    public DungeonGenData setKeyItem(ItemStack stack)
    {
	this.keyItem = stack;
	return this;
    }

    public DungeonGenData setReturnPoint(BlockPos returnPoint, String returnDimension)
    {
	this.returnPoint = returnPoint;
	this.returnDimension = returnDimension;
	return this;
    }

    public DungeonGenData setTheme(int themeNum)
    {
	dungeonTheme = themeNum;
	if (dungeonTheme > DungeonConfig.themeSettings.size())
	{
	    DimDungeons.logMessageError("DIMDUNGEONS ERROR: attempting to build a dungeon with a theme number greater than the number of themes defined in the config. The theme will be ignored.");
	    dungeonTheme = 0;
	}
	return this;
    }
}
