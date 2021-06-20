package com.catastrophe573.dimdungeons.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

//Holder for passing data along during dungeon generation.
public class DungeonGenData
{
    //The key that was used to open the dungeon.
    public ItemStack keyItem;

    //The calculated point to teleport the player to when exiting the dungeon.
    public BlockPos returnPoint;
    public String returnDimension;

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
}
