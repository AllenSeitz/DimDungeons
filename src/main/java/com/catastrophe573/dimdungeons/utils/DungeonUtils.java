package com.catastrophe573.dimdungeons.utils;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.DungeonConfig;
import com.catastrophe573.dimdungeons.block.TileEntityGoldPortal;
import com.catastrophe573.dimdungeons.item.ItemPortalKey;
import com.catastrophe573.dimdungeons.structure.DungeonPlacementLogicAdvanced;
import com.catastrophe573.dimdungeons.structure.DungeonPlacementLogicBasic;
import com.catastrophe573.dimdungeons.structure.DungeonPlacementLogicDebug;

import net.minecraft.block.BlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
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

    // now returns true if a dungeon was built
    public static boolean buildDungeon(World worldIn, DungeonGenData genData)
    {
	// only build dungeons on the server
	if (worldIn.isRemote)
	{
	    return false;
	}

	if (!(genData.keyItem.getItem() instanceof ItemPortalKey))
	{
	    System.out.println("FATAL ERROR: Using a non-key item to build a dungeon? What happened?");
	    return false;
	}

	ItemPortalKey key = (ItemPortalKey) genData.keyItem.getItem();

	long buildX = (long) key.getDungeonTopLeftX(genData.keyItem);
	long buildZ = (long) key.getDungeonTopLeftZ(genData.keyItem);
	long entranceX = buildX + (8 * 16);
	long entranceZ = buildZ + (11 * 16);
	ServerWorld dungeonWorld = DungeonUtils.getDungeonWorld(worldIn.getServer());

	if (dungeonAlreadyExistsHere(dungeonWorld, entranceX, entranceZ))
	{
	    System.out.println("DIMDUNGEONS: Cancelling dungeon contruction. A dungeon already exists here.");
	    return false;
	}

	if (genData.keyItem.hasDisplayName() && DungeonConfig.enableDebugCheats)
	{
	    String name = genData.keyItem.getDisplayName().getUnformattedComponentText();
	    if (name.contentEquals("DebugOne"))
	    {
		DungeonPlacementLogicDebug.place(dungeonWorld, buildX, buildZ, 1, genData);
		return true;
	    }
	    if (name.contentEquals("DebugTwo"))
	    {
		DungeonPlacementLogicDebug.place(dungeonWorld, buildX, buildZ, 2, genData);
		return true;
	    }
	    if (name.contentEquals("DebugThree"))
	    {
		DungeonPlacementLogicDebug.place(dungeonWorld, buildX, buildZ, 3, genData);
		return true;
	    }
	    if (name.contentEquals("DebugFour"))
	    {
		DungeonPlacementLogicDebug.place(dungeonWorld, buildX, buildZ, 4, genData);
		return true;
	    }
	}

	// actually place the dungeon
	if (DungeonPlacementLogicBasic.isEntranceChunk(entranceX / 16, entranceZ / 16))
	{
	    DungeonPlacementLogicBasic.place(dungeonWorld, buildX, buildZ, genData);
	    return true;
	}
	else if (DungeonPlacementLogicAdvanced.isEntranceChunk(entranceX / 16, entranceZ / 16))
	{
	    DungeonPlacementLogicAdvanced.place(dungeonWorld, buildX, buildZ, genData);
	    return true;
	}
	else
	{
	    DimDungeons.LOGGER.error("DIMDUNGEONS FATAL ERROR: trying to build a dungeon at coordinates where no dungeon is supposed to start?");
	}

	return false;
    }

    @SuppressWarnings("deprecation")
    public static boolean dungeonAlreadyExistsHere(World worldIn, long entranceX, long entranceZ)
    {
	BlockState temp = worldIn.getBlockState(new BlockPos(entranceX, 51, entranceZ));
	if (temp.isAir())
	{
	    return false;
	}

	//System.out.println("Canceling dungeon construction. A dungeon already exists here: " + temp.getBlock().getRegistryName());
	return true;
    }

    public static void reprogramExistingExitDoorway(World worldIn, long entranceX, long entranceZ, DungeonGenData genData)
    {
	World ddim = DungeonUtils.getDungeonWorld(worldIn.getServer());
	int zoffset = entranceZ < 0 ? +1 : +2;
	
	for (int x = 0; x <= 1; x++)
	{
	    for (int y = 55; y <= 57; y++)
	    {
		BlockPos pos = new BlockPos(entranceX-x, y, entranceZ+zoffset);

		TileEntityGoldPortal te = (TileEntityGoldPortal) ddim.getTileEntity(pos);
		if (te != null)
		{
		    te.setDestination(genData.returnPoint.getX() + 0.5D, genData.returnPoint.getY() + 0.1D, genData.returnPoint.getZ() + 0.5D);
		    //DimDungeons.LOGGER.error("DIMDUNGEONS INFO: Reprogrammed exit door at (" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")");
		}
		else
		{
		    DimDungeons.LOGGER.error("DIMDUNGEONS ERROR: why is there no exit portal here? (" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")" );
		    //DimDungeons.LOGGER.error("DIMDUNGEONS ERROR: found " + ddim.getBlockState(pos).getBlock().getRegistryName().getPath() );		    
		}
	    }
	}
    }
}
