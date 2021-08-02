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
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

// basically just global functions
public class DungeonUtils
{
    // World.OVERWORLD is the Overworld. This block has different behavior in the Overworld than in the Dungeon Dimension
    public static boolean isDimensionOverworld(World worldIn)
    {
	return worldIn.dimension() == World.OVERWORLD;
    }

    // this is the best idea I have for unmapped 1.16.1
    public static boolean isDimensionDungeon(World worldIn)
    {
	if (worldIn == null)
	{
	    DimDungeons.logMessageError("FATAL ERROR: This 1.16 port is still broken.");
	    return false;
	}
	return worldIn.dimension().location().getPath() == DimDungeons.dungeon_basic_regname;
    }

    // this is used by the dungeon building logic
    public static ServerWorld getDungeonWorld(MinecraftServer server)
    {
	return server.getLevel(DimDungeons.DUNGEON_DIMENSION);
    }

    // now returns true if a dungeon was built
    public static boolean buildDungeon(World worldIn, DungeonGenData genData)
    {
	// only build dungeons on the server
	if (worldIn.isClientSide)
	{
	    return false;
	}

	if (!(genData.keyItem.getItem() instanceof ItemPortalKey))
	{
	    DimDungeons.logMessageError("FATAL ERROR: Using a non-key item to build a dungeon? What happened?");
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
	    DimDungeons.logMessageWarn("DIMDUNGEONS: Cancelling dungeon contruction. A dungeon already exists here.");
	    return false;
	}

	if (genData.keyItem.hasCustomHoverName() && DungeonConfig.enableDebugCheats)
	{
	    String name = genData.keyItem.getHoverName().getContents();
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
	    if (name.contentEquals("bas-4"))
	    {
		DungeonPlacementLogicDebug.place(dungeonWorld, buildX, buildZ, 5, genData);
		return true;
	    }
	    if (name.contentEquals("bas-3"))
	    {
		DungeonPlacementLogicDebug.place(dungeonWorld, buildX, buildZ, 6, genData);
		return true;
	    }
	    if (name.contentEquals("bas-h"))
	    {
		DungeonPlacementLogicDebug.place(dungeonWorld, buildX, buildZ, 7, genData);
		return true;
	    }
	    if (name.contentEquals("bas-c"))
	    {
		DungeonPlacementLogicDebug.place(dungeonWorld, buildX, buildZ, 8, genData);
		return true;
	    }
	    if (name.contentEquals("bas-1"))
	    {
		DungeonPlacementLogicDebug.place(dungeonWorld, buildX, buildZ, 9, genData);
		return true;
	    }
	    if (name.contentEquals("adv-4"))
	    {
		DungeonPlacementLogicDebug.place(dungeonWorld, buildX, buildZ, 10, genData);
		return true;
	    }
	    if (name.contentEquals("adv-3"))
	    {
		DungeonPlacementLogicDebug.place(dungeonWorld, buildX, buildZ, 11, genData);
		return true;
	    }
	    if (name.contentEquals("adv-h"))
	    {
		DungeonPlacementLogicDebug.place(dungeonWorld, buildX, buildZ, 12, genData);
		return true;
	    }
	    if (name.contentEquals("adv-c"))
	    {
		DungeonPlacementLogicDebug.place(dungeonWorld, buildX, buildZ, 13, genData);
		return true;
	    }
	    if (name.contentEquals("adv-1"))
	    {
		DungeonPlacementLogicDebug.place(dungeonWorld, buildX, buildZ, 14, genData);
		return true;
	    }
	    if (name.contains("theme-"))
	    {
		String themeStr = name.replaceFirst("theme-", "");
		genData.dungeonTheme = Integer.parseUnsignedInt(themeStr);
		DungeonPlacementLogicDebug.place(dungeonWorld, buildX, buildZ, 15, genData);
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
	    DimDungeons.logMessageError("DIMDUNGEONS FATAL ERROR: trying to build a dungeon at coordinates where no dungeon is supposed to start?");
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

	return true;
    }

    // returns false if this function fails because the dungeon on the other side was reset
    public static boolean reprogramExistingExitDoorway(World worldIn, long entranceX, long entranceZ, DungeonGenData genData)
    {
	World ddim = DungeonUtils.getDungeonWorld(worldIn.getServer());
	int zoffset = entranceZ < 0 ? +1 : +2;

	for (int x = 0; x <= 1; x++)
	{
	    for (int y = 55; y <= 57; y++)
	    {
		BlockPos pos = new BlockPos(entranceX - x, y, entranceZ + zoffset);

		TileEntityGoldPortal te = (TileEntityGoldPortal) ddim.getBlockEntity(pos);
		if (te != null)
		{
		    te.setDestination(genData.returnPoint.getX() + 0.5D, genData.returnPoint.getY() + 0.1D, genData.returnPoint.getZ() + 0.5D, genData.returnDimension);
		    //DimDungeons.logMessageInfo("DIMDUNGEONS INFO: Reprogrammed exit door at (" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")");
		}
		else
		{
		    // this is now expected behavior if a server admin resets the dungeon dimension
		    DimDungeons.logMessageWarn("DIMDUNGEONS WARNING: why is there no exit portal here? (" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")");
		    return false;
		}
	    }
	}
	
	return true;
    }

    // takes World.OVERWORLD and returns "minecraft:overworld"
    public static String serializeDimensionKey(RegistryKey<World> dimension)
    {
	return dimension.location().getNamespace() + ":" + dimension.location().getPath();
    }
    
    // returns the limit of the dungeon space not in blocks, but in dungeon widths (BLOCKS_APART_PER_DUNGEON)
    public static int getLimitOfWorldBorder(MinecraftServer server)
    {
	int block_limit = ItemPortalKey.RANDOM_COORDINATE_RANGE * ItemPortalKey.BLOCKS_APART_PER_DUNGEON;
		
	// I know that the world border setting is global and affects all dimensions, but some mods change this
	RegistryKey<World> configkey = RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(DungeonConfig.worldborderToRespect));
	ServerWorld world = server.getLevel(configkey);
	double size = world.getWorldBorder().getSize() / 2;
	if ( size < block_limit )
	{
	    return (int)(Math.round(size) / ItemPortalKey.BLOCKS_APART_PER_DUNGEON);
	}
	
	// the world border is not an issue, this function does nothing, proceed as normal
	return ItemPortalKey.RANDOM_COORDINATE_RANGE;
    }
}
