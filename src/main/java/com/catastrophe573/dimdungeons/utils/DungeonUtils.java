package com.catastrophe573.dimdungeons.utils;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.DungeonConfig;
import com.catastrophe573.dimdungeons.block.BlockPortalKeyhole;
import com.catastrophe573.dimdungeons.block.TileEntityGoldPortal;
import com.catastrophe573.dimdungeons.block.TileEntityPortalKeyhole;
import com.catastrophe573.dimdungeons.item.ItemPortalKey;
import com.catastrophe573.dimdungeons.item.ItemRegistrar;
import com.catastrophe573.dimdungeons.structure.DungeonPlacement;
import com.catastrophe573.dimdungeons.structure.DungeonDesigner.DungeonType;

import net.minecraft.server.MinecraftServer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

// basically just global functions
public class DungeonUtils
{
    // World.OVERWORLD is the Overworld. This block has different behavior in the Overworld than in the Dungeon Dimension
    public static boolean isDimensionOverworld(Level worldIn)
    {
	return worldIn.dimension() == Level.OVERWORLD;
    }

    // this is the best idea I have for unmapped 1.16.1
    public static boolean isDimensionDungeon(Level worldIn)
    {
	if (worldIn == null)
	{
	    return false;
	}
	return worldIn.dimension().location().getPath() == DimDungeons.dungeon_basic_regname;
    }

    // this is used by the dungeon building logic
    public static ServerLevel getDungeonWorld(MinecraftServer server)
    {
	return server.getLevel(DimDungeons.DUNGEON_DIMENSION);
    }

    // returns 0 for false, or 1 or higher for the type of debug dungeon to build
    public static int doesKeyMatchDebugCheat(DungeonGenData genData)
    {
	if (!(genData.keyItem.getItem() instanceof ItemPortalKey))
	{
	    DimDungeons.logMessageError("FATAL ERROR: Using a non-key item to build a dungeon? What happened?");
	    return 0;
	}

	if (genData.keyItem.hasCustomHoverName() && DungeonConfig.enableDebugCheats)
	{
	    String name = genData.keyItem.getHoverName().getContents();
	    if (name.contentEquals("DebugOne"))
	    {
		return 1;
	    }
	    if (name.contentEquals("DebugTwo"))
	    {
		return 2;
	    }
	    if (name.contentEquals("DebugThree"))
	    {
		return 3;
	    }
	    if (name.contentEquals("DebugFour"))
	    {
		return 4;
	    }
	    if (name.contentEquals("bas-4"))
	    {
		return 5;
	    }
	    if (name.contentEquals("bas-3"))
	    {
		return 6;
	    }
	    if (name.contentEquals("bas-h"))
	    {
		return 7;
	    }
	    if (name.contentEquals("bas-c"))
	    {
		return 8;
	    }
	    if (name.contentEquals("bas-1"))
	    {
		return 9;
	    }
	    if (name.contentEquals("adv-4"))
	    {
		return 10;
	    }
	    if (name.contentEquals("adv-3"))
	    {
		return 11;
	    }
	    if (name.contentEquals("adv-h"))
	    {
		return 12;
	    }
	    if (name.contentEquals("adv-c"))
	    {
		return 13;
	    }
	    if (name.contentEquals("adv-1"))
	    {
		return 14;
	    }
	    if (name.contains("theme-"))
	    {
		//String themeStr = name.replaceFirst("theme-", "");
		//genData.dungeonTheme = Integer.parseUnsignedInt(themeStr);
		return 15;
	    }
	}
	return 0;
    }

    // assume that if a sign was placed in the entrance chunk that the build must be either started or finished
    public static boolean dungeonAlreadyExistsHere(Level worldIn, long entranceX, long entranceZ)
    {
	ChunkPos cpos = new ChunkPos(((int) entranceX / 16) + 4, ((int) entranceZ / 16) + 4);
	return DungeonPlacement.doesSignExistAtChunk(worldIn, cpos);
    }

    public static void openPortalAfterBuild(Level worldIn, BlockPos pos, DungeonGenData genData, TileEntityPortalKeyhole myEntity)
    {
	// should portal blocks be spawned?
	if (!worldIn.isClientSide)
	{
	    BlockState state = worldIn.getBlockState(pos);
	    ItemPortalKey key = (ItemPortalKey) genData.keyItem.getItem();

	    // regardless of if this is a new or old dungeon, reprogram the exit door
	    float entranceX = key.getWarpX(genData.keyItem);
	    float entranceZ = key.getWarpZ(genData.keyItem);
	    boolean dungeonExistsHere = DungeonUtils.reprogramExistingExitDoorway(worldIn, (long) entranceX, (long) entranceZ, genData);
	    boolean anotherKeyWasFirst = false; // TODO: figure out if this can happen anymore and if it matters?	    

	    // this function only checks for the air blocks below the keyhole and the keyhole blockstate
	    if (BlockPortalKeyhole.isOkayToSpawnPortalBlocks(worldIn, pos, state, myEntity) && dungeonExistsHere)
	    {
		Direction keyholeFacing = state.getValue(BlockPortalKeyhole.FACING);
		Direction.Axis axis = (keyholeFacing == Direction.NORTH || keyholeFacing == Direction.SOUTH) ? Direction.Axis.X : Direction.Axis.Z;

		BlockPortalKeyhole.addGoldenPortalBlock(worldIn, pos.below(), genData.keyItem, axis);
		BlockPortalKeyhole.addGoldenPortalBlock(worldIn, pos.below(2), genData.keyItem, axis);
	    }

	    // the client config can't disable this because it is played on the server, sorry
	    worldIn.playLocalSound((double) pos.getX() + 0.5D, (double) pos.getY(), (double) pos.getZ() + 0.5D, SoundEvents.END_PORTAL_SPAWN, SoundSource.BLOCKS, 0.4F, 1.0F, false);

	    // this function prints no message on success
	    Player player = worldIn.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), -1.0f, false);
	    if (player != null)
	    {
		BlockPortalKeyhole.checkForProblemsAndLiterallySpeakToPlayer(worldIn, pos, state, myEntity, player, dungeonExistsHere, anotherKeyWasFirst);
	    }
	}
    }

    // returns false if this function fails because the dungeon on the other side was reset
    public static boolean reprogramExistingExitDoorway(Level worldIn, long entranceX, long entranceZ, DungeonGenData genData)
    {
	Level ddim = DungeonUtils.getDungeonWorld(worldIn.getServer());
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
    public static String serializeDimensionKey(ResourceKey<Level> dimension)
    {
	return dimension.location().getNamespace() + ":" + dimension.location().getPath();
    }

    // returns the limit of the dungeon space not in blocks, but in dungeon widths (which is BLOCKS_APART_PER_DUNGEON)
    public static long getLimitOfWorldBorder(MinecraftServer server)
    {
	ResourceKey<Level> configkey = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(DungeonConfig.worldborderToRespect));
	ServerLevel world = server.getLevel(configkey);
	double size = world.getWorldBorder().getSize() / 2;

	return Math.round(size);
    }

    // THIS MUST ONLY BE USED for the purposes of displaying an activated in a gui
    // the key returned by this function is not valid
    public static ItemStack getExampleKey()
    {
	ItemStack icon = new ItemStack(ItemRegistrar.item_portal_key);
	CompoundTag data = new CompoundTag();
	data.putBoolean(ItemPortalKey.NBT_KEY_ACTIVATED, true);
	data.putString(ItemPortalKey.NBT_DUNGEON_TYPE, DungeonType.BASIC.toString());
	data.putInt(ItemPortalKey.NBT_KEY_DESTINATION_Z, 0);
	data.putInt(ItemPortalKey.NBT_NAME_TYPE, 2); // key to the
	data.putInt(ItemPortalKey.NBT_NAME_PART_1, 0); // dungeon of
	data.putInt(ItemPortalKey.NBT_NAME_PART_2, 17); // catastrophe

	icon.setTag(data);
	return icon;
    }
}
