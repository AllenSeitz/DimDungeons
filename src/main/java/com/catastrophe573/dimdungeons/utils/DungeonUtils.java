package com.catastrophe573.dimdungeons.utils;

import com.catastrophe573.dimdungeons.DimDungeons;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

// basically just global functions
public class DungeonUtils
{
    // World.field_234918_g_ is the Overworld. This block has different behavior in the Overworld than in the Dungeon Dimension
    public static boolean isDimensionOverworld(World worldIn)
    {
	return worldIn.getDimensionKey() == World.OVERWORLD;
    }    
    
    // this is the best idea I have for unmapped 1.16.1
    public static boolean isDimensionDungeon(World worldIn)
    {
	if (worldIn == null)
	{
	    System.out.println("FATAL ERROR: This 1.16 port is still broken.");
	    return false;
	}
	return getDimensionRegistryKey() == worldIn.getDimensionKey();
    }
    
    // this is used by the dungeon building logic
    public static ServerWorld getDungeonWorld(MinecraftServer server)
    {
	return server.getWorld(getDimensionRegistryKey());
    }    

    // only used by the two functions above
    private static RegistryKey<World> getDimensionRegistryKey()
    {
	return RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(DimDungeons.MOD_ID, DimDungeons.dungeon_basic_regname));	
    }    
}
