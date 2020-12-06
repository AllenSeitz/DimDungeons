package com.catastrophe573.dimdungeons.utils;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.item.ItemPortalKey;
import com.catastrophe573.dimdungeons.structure.DungeonPlacementLogicAdvanced;
import com.catastrophe573.dimdungeons.structure.DungeonPlacementLogicBasic;

import net.minecraft.item.ItemStack;
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
	return worldIn.getDimensionKey().getLocation().getPath() == DimDungeons.dungeon_basic_regname;
    }
    
    // this is used by the dungeon building logic
    public static ServerWorld getDungeonWorld(MinecraftServer server)
    {
	return server.getWorld(DimDungeons.DUNGEON_DIMENSION);
    }    

    public static void buildDungeon(World worldIn, ItemStack stack)
    {
	// only build dungeons on the server
	if (worldIn.isRemote)
	{
	    return;
	}	
	
	if (!(stack.getItem() instanceof ItemPortalKey))
	{
	    System.out.println("FATAL ERROR: Using a non-key item to build a dungeon? What happened?");
	    return;
	}
	
	ItemPortalKey key = (ItemPortalKey)stack.getItem();
	
	long buildX = (long) key.getDungeonTopLeftX(stack);
	long buildZ = (long) key.getDungeonTopLeftZ(stack);
	long entranceX = buildX + (8*16);
	long entranceZ = buildZ + (11*16);
	ServerWorld dungeonWorld = DungeonUtils.getDungeonWorld(worldIn.getServer());
	
	// actually place the dungeon
	if (DungeonPlacementLogicBasic.isEntranceChunk(entranceX/16, entranceZ/16))
	{
	    DungeonPlacementLogicBasic.place(dungeonWorld, buildX, buildZ);
	}
	else if (DungeonPlacementLogicAdvanced.isEntranceChunk(entranceX/16, entranceZ/16))
	{
	    DungeonPlacementLogicAdvanced.place(dungeonWorld, buildX, buildZ);
	}
	else
	{
	    DimDungeons.LOGGER.error("DIMDUNGEONS FATAL ERROR: trying to build a dungeon at coordinates where no dungeon is supposed to start?");
	}
    }
}
