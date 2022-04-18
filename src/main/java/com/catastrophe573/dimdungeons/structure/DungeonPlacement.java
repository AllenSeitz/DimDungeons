package com.catastrophe573.dimdungeons.structure;

import java.util.Random;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.DungeonConfig;
import com.catastrophe573.dimdungeons.block.BlockRegistrar;
import com.catastrophe573.dimdungeons.block.TileEntityGoldPortal;
import com.catastrophe573.dimdungeons.block.TileEntityLocalTeleporter;
import com.catastrophe573.dimdungeons.block.TileEntityPortalKeyhole;
import com.catastrophe573.dimdungeons.item.ItemPortalKey;
import com.catastrophe573.dimdungeons.item.ItemRegistrar;
import com.catastrophe573.dimdungeons.structure.DungeonDesigner.DungeonType;
import com.catastrophe573.dimdungeons.structure.DungeonDesigner.RoomType;
import com.catastrophe573.dimdungeons.utils.DungeonGenData;
import com.catastrophe573.dimdungeons.utils.DungeonUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

// this class takes the dungeon layout which is designed by DungeonBuilderLogic and actually places it in the world
public class DungeonPlacement
{
    public static int SIGN_Y = 49; // for the slow building feature

    public DungeonPlacement()
    {
    }

    // this is the function that actually writes the 8x8 chunk structure to the world, and ALL AT ONCE
    // this function is deprecated now that slow building is implemented
    @Deprecated
    public static boolean place(ServerLevel world, long x, long z, DungeonGenData genData)
    {
	// calculate and double check the starting point
	long entranceChunkX = getEntranceX(x);
	long entranceChunkZ = getEntranceZ(z);
	if (!isEntranceChunk(entranceChunkX, entranceChunkZ))
	{
	    DimDungeons.logMessageError("DIMDUNGEONS FATAL ERROR: incorrect entrance chunk chosen: " + x + ", " + z);
	    return false;
	}
	DimDungeons.logMessageInfo("DIMDUNGEONS START STRUCTURE at chunk: " + x + ", " + z);

	// pick which logic and set of configs to use
	DungeonDesigner dbl;
	int dungeonSize = DungeonConfig.DEFAULT_BASIC_DUNGEON_SIZE;
	DungeonType dungeonType = DungeonType.BASIC;
	boolean useLarge = false;

	if (genData.dungeonTheme == 2)
	{
	    dungeonType = DungeonType.THEME_OPEN;
	    dbl = new DungeonDesignerThemeOpen(world.getRandom(), entranceChunkX, entranceChunkZ, dungeonType, genData.dungeonTheme);
	    dungeonSize = DungeonConfig.themeSettings.get(genData.dungeonTheme - 1).themeDungeonSize;
	}
	else if (z < 0)
	{
	    dungeonType = DungeonType.ADVANCED;
	    dbl = new DungeonDesigner(world.getRandom(), entranceChunkX, entranceChunkZ, dungeonType, genData.dungeonTheme);
	    dungeonSize = DungeonConfig.DEFAULT_ADVANCED_DUNGEON_SIZE;
	    useLarge = true;
	}
	else
	{
	    dungeonType = DungeonType.BASIC;
	    dbl = new DungeonDesigner(world.getRandom(), entranceChunkX, entranceChunkZ, dungeonType, genData.dungeonTheme);
	    if (genData.dungeonTheme > 0)
	    {
		dungeonSize = DungeonConfig.themeSettings.get(genData.dungeonTheme - 1).themeDungeonSize;
	    }
	}

	// this is the big call that shuffles the rooms and actually designs the build
	dbl.calculateDungeonShape(dungeonSize, useLarge);

	// place all 64 rooms (many will be blank), for example the entrance room is at [4][7] in this array
	for (int i = 0; i < 8; i++)
	{
	    for (int j = 0; j < 8; j++)
	    {
		DungeonRoom nextRoom = dbl.finalLayout[i][j];
		if (!nextRoom.hasRoom())
		{
		    continue;
		}
		else
		{
		    // calculate the chunkpos of the room at 0,0 in the top left of the map
		    // The +4 offset is so that the dungeons align with vanilla blank maps!
		    ChunkPos cpos = new ChunkPos(((int) x / 16) + i + 4, ((int) z / 16) + j + 4);

		    if (nextRoom.type == RoomType.LARGE)
		    {
			if (!putLargeRoomHere(cpos, world, nextRoom, genData, dungeonType))
			{
			    DimDungeons.logMessageError("DIMDUNGEONS ERROR UNABLE TO PLACE ***LARGE*** STRUCTURE: " + nextRoom.structure);
			}
			closeDoorsOnLargeRoom(cpos, world, nextRoom, genData);
		    }
		    else if (nextRoom.type == RoomType.LARGE_DUMMY)
		    {
			// this isn't trivial because dummy rooms still have to close doorways that lead out of bounds
			closeDoorsOnLargeRoom(cpos, world, nextRoom, genData);
		    }
		    else if (!putRoomHere(cpos, world, nextRoom, genData, dungeonType))
		    {
			DimDungeons.logMessageError("DIMDUNGEONS ERROR UNABLE TO PLACE STRUCTURE: " + nextRoom.structure);
		    }
		}
	    }
	}

	return true;
    }

    // step 1 of the dungeon building process: build the dungeon in memory, then place signs where all the rooms will go
    public static boolean placeSigns(ServerLevel world, long x, long z, DungeonGenData genData)
    {
	// calculate and double check the starting point
	long entranceChunkX = getEntranceX(x);
	long entranceChunkZ = getEntranceZ(z);
	if (!isEntranceChunk(entranceChunkX, entranceChunkZ))
	{
	    DimDungeons.logMessageError("DIMDUNGEONS FATAL ERROR: incorrect entrance chunk chosen: " + x + ", " + z);
	    return false;
	}
	DimDungeons.logMessageInfo("DIMDUNGEONS START STRUCTURE at chunk: " + x + ", " + z);

	// pick which logic and set of configs to use
	DungeonDesigner dbl;
	int dungeonSize = DungeonConfig.DEFAULT_BASIC_DUNGEON_SIZE;
	DungeonType dungeonType = DungeonType.BASIC;
	String stringDungeonType = "basic";
	boolean useLarge = false;

	if (genData.dungeonTheme == 2)
	{
	    dungeonType = DungeonType.THEME_OPEN;
	    dbl = new DungeonDesignerThemeOpen(world.getRandom(), entranceChunkX, entranceChunkZ, dungeonType, genData.dungeonTheme);
	    dungeonSize = DungeonConfig.themeSettings.get(genData.dungeonTheme - 1).themeDungeonSize;
	    stringDungeonType = "theme " + genData.dungeonTheme;
	}
	else if (z < 0)
	{
	    dungeonType = DungeonType.ADVANCED;
	    dbl = new DungeonDesigner(world.getRandom(), entranceChunkX, entranceChunkZ, dungeonType, genData.dungeonTheme);
	    dungeonSize = DungeonConfig.DEFAULT_ADVANCED_DUNGEON_SIZE;
	    useLarge = true;
	    stringDungeonType = "advanced";
	}
	else
	{
	    dungeonType = DungeonType.BASIC;
	    dbl = new DungeonDesigner(world.getRandom(), entranceChunkX, entranceChunkZ, dungeonType, genData.dungeonTheme);
	    stringDungeonType = "basic";
	    if (genData.dungeonTheme > 0)
	    {
		dungeonSize = DungeonConfig.themeSettings.get(genData.dungeonTheme - 1).themeDungeonSize;
		stringDungeonType = "theme " + genData.dungeonTheme;
	    }
	}

	// this is the big call that shuffles the rooms and actually designs the build
	dbl.calculateDungeonShape(dungeonSize, useLarge);

	// place signs in chunks where rooms will go. the contents of the room are written on the sign for future ticks to refer to
	for (int i = 0; i < 8; i++)
	{
	    for (int j = 0; j < 8; j++)
	    {
		DungeonRoom nextRoom = dbl.finalLayout[i][j];
		if (!nextRoom.hasRoom())
		{
		    continue;
		}
		else
		{
		    // calculate the chunkpos of this room and the blockpos to place the sign at
		    // The +4 offset is so that the dungeons align with vanilla blank maps!
		    ChunkPos cpos = new ChunkPos(((int) x / 16) + i + 4, ((int) z / 16) + j + 4);
		    BlockPos bpos = new BlockPos(cpos.getMinBlockX(), SIGN_Y, cpos.getMinBlockZ());

		    world.setBlockAndUpdate(bpos.below(), Blocks.BEDROCK.defaultBlockState());
		    world.setBlockAndUpdate(bpos, Blocks.OAK_SIGN.defaultBlockState());
		    SignBlockEntity sign = (SignBlockEntity) world.getBlockEntity(bpos);
		    if (sign != null)
		    {
			sign.setColor(DyeColor.BLACK);
			sign.setHasGlowingText(true);
			sign.setMessage(0, new TextComponent(nextRoom.structure));
			sign.setMessage(1, new TextComponent(nextRoom.rotation.toString()));
			sign.setMessage(2, new TextComponent(nextRoom.type.toString()));
			sign.setMessage(3, new TextComponent(stringDungeonType)); // "basic", "advanced", or "theme #"
			sign.setEditable(false);
		    }
		}
	    }
	}

	return true;
    }

    // returns true if a room was built here or false if this chunk was skipped
    public static boolean buildRoomAboveSign(ServerLevel world, ChunkPos cpos, DungeonGenData genData)
    {
	BlockPos bpos = new BlockPos(cpos.getMinBlockX(), SIGN_Y, cpos.getMinBlockZ());
	SignBlockEntity sign = (SignBlockEntity) world.getBlockEntity(bpos);

	if (sign == null || wasRoomBuiltAtChunk(world, cpos))
	{
	    return false; // no sign here means no room
	}

	// step 2: decode the sign
	DungeonRoom nextRoom = readSignAtChunk(world, cpos);
	String dunTypeText = ((TextComponent) sign.getMessage(3, false)).toString();
	DungeonType dungeonType = DungeonType.BASIC;
	if (dunTypeText.contains("advanced"))
	{
	    dungeonType = DungeonType.ADVANCED;
	}

	// also, set bedrock two blocked under the sign to permanently signal that this function has been called on this chunk already
	world.setBlockAndUpdate(bpos.below().below(), Blocks.BEDROCK.defaultBlockState());

	// step 3: place room here, with these parameters
	if (nextRoom.type == RoomType.LARGE)
	{
	    if (!putLargeRoomHere(cpos, world, nextRoom, genData, dungeonType))
	    {
		DimDungeons.logMessageError("DIMDUNGEONS ERROR UNABLE TO PLACE ***LARGE*** STRUCTURE: " + nextRoom.structure);
	    }
	    closeDoorsOnLargeRoom(cpos, world, nextRoom, genData);
	}
	else if (nextRoom.type == RoomType.LARGE_DUMMY)
	{
	    // this isn't trivial because dummy rooms still have to close doorways that lead out of bounds
	    closeDoorsOnLargeRoom(cpos, world, nextRoom, genData);
	}
	else if (!putRoomHere(cpos, world, nextRoom, genData, dungeonType))
	{
	    DimDungeons.logMessageError("DIMDUNGEONS ERROR UNABLE TO PLACE STRUCTURE: " + nextRoom.structure);
	}

	return true;
    }

    // this function will return null if there is no room at this position
    public static DungeonRoom readSignAtChunk(ServerLevel world, ChunkPos cpos)
    {
	BlockPos bpos = new BlockPos(cpos.getMinBlockX(), SIGN_Y, cpos.getMinBlockZ());
	SignBlockEntity sign = (SignBlockEntity) world.getBlockEntity(bpos);

	if (sign == null)
	{
	    return null; // no sign = no room here, and this result is expected
	}

	DungeonRoom room = new DungeonRoom();
	room.structure = ((TextComponent) sign.getMessage(0, false)).getString();
	String rotationText = ((TextComponent) sign.getMessage(1, false)).getString();
	String roomTypeText = ((TextComponent) sign.getMessage(2, false)).getString();
	room.rotation = Rotation.valueOf(rotationText);
	room.type = RoomType.valueOf(roomTypeText);

	return room;
    }

    // used to check if any room exists in the given chunk
    // does a slightly faster check for the bedrock under the sign, technically
    public static boolean doesSignExistAtChunk(Level world, ChunkPos cpos)
    {
	BlockPos bpos = new BlockPos(cpos.getMinBlockX(), SIGN_Y - 1, cpos.getMinBlockZ());
	return world.getBlockState(bpos).getBlock() == Blocks.BEDROCK;
    }

    // checks for the second piece of bedrock under the sign. this is placed during buildRoomAboveSign()
    public static boolean wasRoomBuiltAtChunk(Level world, ChunkPos cpos)
    {
	BlockPos bpos = new BlockPos(cpos.getMinBlockX(), SIGN_Y - 2, cpos.getMinBlockZ());
	return world.getBlockState(bpos).getBlock() == Blocks.BEDROCK;
    }

    public static boolean isDungeonChunk(long x, long z)
    {
	if (x < 0)
	{
	    return false; // dungeons only spawn in the NE and SE quadrants
	}

	if (z >= 0)
	{
	    long plotX = x % 16;
	    long plotZ = z % 16;
	    return plotX > 3 && plotX < 12 && plotZ > 3 && plotZ < 12;
	}
	else
	{
	    long plotX = x % 16;
	    long plotZ = z % 16;
	    return plotX > 3 && plotX < 12 && plotZ < -4 && plotZ > -13;
	}
    }

    public static boolean isEntranceChunk(long x, long z)
    {
	if (x < 0)
	{
	    return false; // dungeons only spawn in the NE and SE quadrants
	}

	if (z >= 0)
	{
	    long plotX = x % 16;
	    long plotZ = z % 16;
	    return plotX == 8 && plotZ == 11;
	}
	else
	{
	    long plotX = x % 16;
	    long plotZ = z % 16;
	    return plotX == 8 && plotZ == -5;
	}
    }

    // automatically adjusts for NE quadrant or SE quadrant
    public static long getEntranceX(long chunkX)
    {
	return (chunkX / 16) + 8;
    }

    // automatically adjusts for NE quadrant or SE quadrant
    public static long getEntranceZ(long chunkZ)
    {
	if (chunkZ >= 0)
	{
	    return (chunkZ / 16) + 11; // north
	}
	else
	{
	    return (chunkZ / 16) + 11; // south
	}
    }

    // used by the place() function to actually place rooms
    public static boolean putLargeRoomHere(ChunkPos cpos, ServerLevel world, DungeonRoom room, DungeonGenData genData, DungeonType type)
    {
	MinecraftServer minecraftserver = ((Level) world).getServer();
	StructureManager templatemanager = DungeonUtils.getDungeonWorld(minecraftserver).getStructureManager();

	StructureTemplate template = templatemanager.getOrCreate(new ResourceLocation(room.structure));
	StructurePlaceSettings placementsettings = (new StructurePlaceSettings()).setMirror(Mirror.NONE).setRotation(Rotation.NONE).setIgnoreEntities(false);
	placementsettings.setRotation(Rotation.NONE);
	placementsettings.setBoundingBox(new BoundingBox(cpos.x * 16, 0, cpos.z * 16, (cpos.x * 16) + 32 - 1, 255, (cpos.z * 16) + 32 - 1));
	BlockPos position = new BlockPos(cpos.getMinBlockX(), 50, cpos.getMinBlockZ());
	BlockPos sizeRange = new BlockPos(32, 13, 32);

	if (template == null)
	{
	    DimDungeons.logMessageError("DIMDUNGEONS FATAL ERROR: LARGE structure does not exist (" + room.structure + ")");
	    return false;
	}

	DimDungeons.logMessageInfo("Placing a large room: " + room.structure);
	boolean success = template.placeInWorld((ServerLevelAccessor) world, position, sizeRange, placementsettings, world.getRandom(), 2);

	// handle data blocks - this code block is copied from TemplateStructurePiece
	for (StructureTemplate.StructureBlockInfo template$blockinfo : template.filterBlocks(position, placementsettings, Blocks.STRUCTURE_BLOCK))
	{
	    if (template$blockinfo.nbt != null)
	    {
		StructureMode structuremode = StructureMode.valueOf(template$blockinfo.nbt.getString("mode"));
		if (structuremode == StructureMode.DATA)
		{
		    handleDataBlock(template$blockinfo.nbt.getString("metadata"), template$blockinfo.pos, world, world.getRandom(), placementsettings.getBoundingBox(), genData, type);
		}
	    }
	}

	return success;
    }

    public static void closeDoorsOnLargeRoom(ChunkPos cpos, ServerLevel world, DungeonRoom room, DungeonGenData genData)
    {
	BlockState fillBlock = Blocks.STONE_BRICKS.defaultBlockState();
	BlockState airBlock = Blocks.AIR.defaultBlockState();
	BlockState redBlock = Blocks.RED_CONCRETE.defaultBlockState();
	BlockPos startPos = new BlockPos(cpos.getMinBlockX(), 55, cpos.getMinBlockZ());
	int x = cpos.x;
	int z = cpos.z;

	// remember, these can be null
	ChunkPos west = new ChunkPos(x - 1, z);
	ChunkPos east = new ChunkPos(x + 1, z);
	ChunkPos north = new ChunkPos(x, z - 1);
	ChunkPos south = new ChunkPos(x, z + 1);
	DungeonRoom westRoom = readSignAtChunk(world, west);
	DungeonRoom eastRoom = readSignAtChunk(world, east);
	DungeonRoom northRoom = readSignAtChunk(world, north);
	DungeonRoom southRoom = readSignAtChunk(world, south);

	// does west lead into a void?
	if (westRoom == null)
	{
	    // place 12 stone bricks
	    world.setBlock(startPos.south(7).east(0).above(0), fillBlock, 2);
	    world.setBlock(startPos.south(7).east(1).above(0), fillBlock, 2);
	    world.setBlock(startPos.south(8).east(0).above(0), fillBlock, 2);
	    world.setBlock(startPos.south(8).east(1).above(0), fillBlock, 2);
	    world.setBlock(startPos.south(7).east(0).above(1), fillBlock, 2);
	    world.setBlock(startPos.south(7).east(1).above(1), fillBlock, 2);
	    world.setBlock(startPos.south(8).east(0).above(1), fillBlock, 2);
	    world.setBlock(startPos.south(8).east(1).above(1), fillBlock, 2);
	    world.setBlock(startPos.south(7).east(0).above(2), fillBlock, 2);
	    world.setBlock(startPos.south(7).east(1).above(2), fillBlock, 2);
	    world.setBlock(startPos.south(8).east(0).above(2), fillBlock, 2);
	    world.setBlock(startPos.south(8).east(1).above(2), fillBlock, 2);
	    // and erase 2 red concrete from the roof
	    world.setBlock(startPos.south(7).east(0).above(7), airBlock, 2);
	    world.setBlock(startPos.south(8).east(0).above(7), airBlock, 2);
	}
	else
	{
	    // extend the doorway on the minimap
	    if (westRoom.type != RoomType.LARGE && westRoom.type != RoomType.LARGE_DUMMY)
	    {
		world.setBlock(startPos.south(7).east(1).above(7), redBlock, 2);
		world.setBlock(startPos.south(8).east(1).above(7), redBlock, 2);
		world.setBlock(startPos.south(7).east(2).above(7), redBlock, 2);
		world.setBlock(startPos.south(8).east(2).above(7), redBlock, 2);
		world.setBlock(startPos.south(7).east(3).above(7), redBlock, 2);
		world.setBlock(startPos.south(8).east(3).above(7), redBlock, 2);
		world.setBlock(startPos.south(7).east(4).above(7), redBlock, 2);
		world.setBlock(startPos.south(8).east(4).above(7), redBlock, 2);
		world.setBlock(startPos.south(7).east(5).above(7), redBlock, 2);
		world.setBlock(startPos.south(8).east(5).above(7), redBlock, 2);
		world.setBlock(startPos.south(7).east(6).above(7), redBlock, 2);
		world.setBlock(startPos.south(8).east(6).above(7), redBlock, 2);
	    }
	}

	// does east lead into a void?
	if (eastRoom == null)
	{
	    // place 12 stone bricks
	    world.setBlock(startPos.south(7).east(14).above(0), fillBlock, 2);
	    world.setBlock(startPos.south(7).east(15).above(0), fillBlock, 2);
	    world.setBlock(startPos.south(8).east(14).above(0), fillBlock, 2);
	    world.setBlock(startPos.south(8).east(15).above(0), fillBlock, 2);
	    world.setBlock(startPos.south(7).east(14).above(1), fillBlock, 2);
	    world.setBlock(startPos.south(7).east(15).above(1), fillBlock, 2);
	    world.setBlock(startPos.south(8).east(14).above(1), fillBlock, 2);
	    world.setBlock(startPos.south(8).east(15).above(1), fillBlock, 2);
	    world.setBlock(startPos.south(7).east(14).above(2), fillBlock, 2);
	    world.setBlock(startPos.south(7).east(15).above(2), fillBlock, 2);
	    world.setBlock(startPos.south(8).east(14).above(2), fillBlock, 2);
	    world.setBlock(startPos.south(8).east(15).above(2), fillBlock, 2);
	    // and erase 2 red concrete from the roof
	    world.setBlock(startPos.south(7).east(15).above(7), airBlock, 2);
	    world.setBlock(startPos.south(8).east(15).above(7), airBlock, 2);
	}
	else
	{
	    // extend the doorway on the minimap
	    if (eastRoom.type != RoomType.LARGE && eastRoom.type != RoomType.LARGE_DUMMY)
	    {
		world.setBlock(startPos.south(7).east(14).above(7), redBlock, 2);
		world.setBlock(startPos.south(8).east(14).above(7), redBlock, 2);
		world.setBlock(startPos.south(7).east(13).above(7), redBlock, 2);
		world.setBlock(startPos.south(8).east(13).above(7), redBlock, 2);
		world.setBlock(startPos.south(7).east(12).above(7), redBlock, 2);
		world.setBlock(startPos.south(8).east(12).above(7), redBlock, 2);
		world.setBlock(startPos.south(7).east(11).above(7), redBlock, 2);
		world.setBlock(startPos.south(8).east(11).above(7), redBlock, 2);
		world.setBlock(startPos.south(7).east(10).above(7), redBlock, 2);
		world.setBlock(startPos.south(8).east(10).above(7), redBlock, 2);
		world.setBlock(startPos.south(7).east(9).above(7), redBlock, 2);
		world.setBlock(startPos.south(8).east(9).above(7), redBlock, 2);
	    }
	}

	// does north lead into a void?
	if (northRoom == null)
	{
	    // can't happen yet
	}
	else
	{
	    // extend the doorway on the minimap
	    if (northRoom.type != RoomType.LARGE && northRoom.type != RoomType.LARGE_DUMMY)
	    {
		// extend the doorway on the minimap
		world.setBlock(startPos.south(1).east(7).above(7), redBlock, 2);
		world.setBlock(startPos.south(1).east(8).above(7), redBlock, 2);
		world.setBlock(startPos.south(2).east(7).above(7), redBlock, 2);
		world.setBlock(startPos.south(2).east(8).above(7), redBlock, 2);
		world.setBlock(startPos.south(3).east(7).above(7), redBlock, 2);
		world.setBlock(startPos.south(3).east(8).above(7), redBlock, 2);
		world.setBlock(startPos.south(4).east(7).above(7), redBlock, 2);
		world.setBlock(startPos.south(4).east(8).above(7), redBlock, 2);
		world.setBlock(startPos.south(5).east(7).above(7), redBlock, 2);
		world.setBlock(startPos.south(5).east(8).above(7), redBlock, 2);
		world.setBlock(startPos.south(6).east(7).above(7), redBlock, 2);
		world.setBlock(startPos.south(6).east(8).above(7), redBlock, 2);
	    }
	}

	// does south lead into a void?
	if (southRoom == null)
	{
	    // can't happen yet
	}
	else
	{
	    if (southRoom.type != RoomType.LARGE && southRoom.type != RoomType.LARGE_DUMMY)
	    {
		// extend the doorway on the minimap
		world.setBlock(startPos.south(14).east(7).above(7), redBlock, 2);
		world.setBlock(startPos.south(14).east(8).above(7), redBlock, 2);
		world.setBlock(startPos.south(13).east(7).above(7), redBlock, 2);
		world.setBlock(startPos.south(13).east(8).above(7), redBlock, 2);
		world.setBlock(startPos.south(12).east(7).above(7), redBlock, 2);
		world.setBlock(startPos.south(12).east(8).above(7), redBlock, 2);
		world.setBlock(startPos.south(11).east(7).above(7), redBlock, 2);
		world.setBlock(startPos.south(11).east(8).above(7), redBlock, 2);
		world.setBlock(startPos.south(10).east(7).above(7), redBlock, 2);
		world.setBlock(startPos.south(10).east(8).above(7), redBlock, 2);
		world.setBlock(startPos.south(9).east(7).above(7), redBlock, 2);
		world.setBlock(startPos.south(9).east(8).above(7), redBlock, 2);
	    }
	}
    }

    // used by the place() and the slow building logic function to place a single room
    public static boolean putRoomHere(ChunkPos cpos, ServerLevel world, DungeonRoom room, DungeonGenData genData, DungeonType type)
    {
	MinecraftServer minecraftserver = ((Level) world).getServer();
	StructureManager templatemanager = DungeonUtils.getDungeonWorld(minecraftserver).getStructureManager();

	StructureTemplate template = templatemanager.getOrCreate(new ResourceLocation(room.structure));
	StructurePlaceSettings placementsettings = (new StructurePlaceSettings()).setMirror(Mirror.NONE).setRotation(Rotation.NONE).setIgnoreEntities(false);
	placementsettings.setBoundingBox(placementsettings.getBoundingBox());

	placementsettings.setRotation(room.rotation);
	BlockPos position = new BlockPos(cpos.getMinBlockX(), 50, cpos.getMinBlockZ());
	BlockPos sizeRange = new BlockPos(16, 13, 16);

	if (template == null)
	{
	    DimDungeons.logMessageInfo("DIMDUNGEONS FATAL ERROR: Structure does not exist (" + room.structure + ")");
	    return false;
	}

	// next if the structure is to be rotated then it must also be offset, because rotating a structure also moves it
	if (room.rotation == Rotation.COUNTERCLOCKWISE_90)
	{
	    // west: rotate CCW and push +Z
	    placementsettings.setRotation(Rotation.COUNTERCLOCKWISE_90);
	    position = position.offset(0, 0, template.getSize().getZ() - 1);
	}
	else if (room.rotation == Rotation.CLOCKWISE_90)
	{
	    // east rotate CW and push +X
	    placementsettings.setRotation(Rotation.CLOCKWISE_90);
	    position = position.offset(template.getSize().getX() - 1, 0, 0);
	}
	else if (room.rotation == Rotation.CLOCKWISE_180)
	{
	    // south: rotate 180 and push both +X and +Z
	    placementsettings.setRotation(Rotation.CLOCKWISE_180);
	    position = position.offset(template.getSize().getX() - 1, 0, template.getSize().getZ() - 1);
	}
	else //if (nextRoom.rotation == Rotation.NONE)
	{
	    // north: no rotation
	    placementsettings.setRotation(Rotation.NONE);
	}

	// this is the big call to the structure block
	DimDungeons.logMessageInfo("Placing a room: " + room.structure);
	boolean success = template.placeInWorld((ServerLevelAccessor) world, position, sizeRange, placementsettings, world.getRandom(), 2);

	// handle data blocks - this code block is copied from TemplateStructurePiece
	for (StructureTemplate.StructureBlockInfo template$blockinfo : template.filterBlocks(position, placementsettings, Blocks.STRUCTURE_BLOCK))
	{
	    if (template$blockinfo.nbt != null)
	    {
		StructureMode structuremode = StructureMode.valueOf(template$blockinfo.nbt.getString("mode"));
		if (structuremode == StructureMode.DATA)
		{
		    handleDataBlock(template$blockinfo.nbt.getString("metadata"), template$blockinfo.pos, world, world.getRandom(), placementsettings.getBoundingBox(), genData, type);
		}
	    }
	}

	// replace all red carpet in entrance rooms with green carpet
	if (type == DungeonType.ADVANCED)
	{
	    for (StructureBlockInfo info : template.filterBlocks(position, placementsettings, Blocks.RED_CARPET))
	    {
		world.setBlock(info.pos, Blocks.GREEN_CARPET.defaultBlockState(), 3);
	    }
	}

	return success;
    }

    // another debugging function
    public void printMap(DungeonDesigner dbl)
    {
	for (int j = 0; j < 8; j++)
	{
	    String dungeonRowShape = "";
	    for (int i = 0; i < 8; i++)
	    {
		dungeonRowShape += dbl.finalLayout[i][j].hasRoom() ? "*" : ".";
	    }
	    System.out.println(dungeonRowShape);
	}
    }

    // resembles TemplateStructurePiece.handleDataMarker() from vanilla
    protected static void handleDataBlock(String name, BlockPos pos, ServerLevel world, Random rand, BoundingBox bb, DungeonGenData genData, DungeonType type)
    {
	//DimDungeons.LOGGER.info("DATA BLOCK NAME: " + name);

	if ("ReturnPortal".equals(name))
	{
	    world.setBlock(pos, BlockRegistrar.block_gold_portal.defaultBlockState(), 2); // erase this data block
	    TileEntityGoldPortal te = (TileEntityGoldPortal) world.getBlockEntity(pos);
	    if (te != null)
	    {
		te.setDestination(genData.returnPoint.getX() + 0.5D, genData.returnPoint.getY() + 0.1D, genData.returnPoint.getZ() + 0.5D, genData.returnDimension);
	    }
	}
	else if ("BackToEntrance".equals(name))
	{
	    world.setBlock(pos, BlockRegistrar.block_local_teleporter.defaultBlockState(), 2); // erase this data block
	    TileEntityLocalTeleporter te = (TileEntityLocalTeleporter) world.getBlockEntity(pos);
	    if (te != null)
	    {
		ItemPortalKey key = (ItemPortalKey) genData.keyItem.getItem();
		double entranceX = key.getWarpX(genData.keyItem);
		double entranceZ = key.getWarpZ(genData.keyItem);
		te.setDestination(entranceX, 55.1D, entranceZ, 0.0f, 180.0f);
	    }
	}
	else if ("LockItStoneBrick".equals(name))
	{
	    world.setBlock(pos, Blocks.STONE_BRICKS.defaultBlockState(), 2); // erase this data block 
	}
	else if ("LockIt".equals(name))
	{
	    // do nothing!
	}
	else if ("FortuneTeller".equals(name))
	{
	    world.setBlock(pos, Blocks.STONE_BRICKS.defaultBlockState(), 2); // erase this data block 

	    // put a message inside the dispenser
	    BlockEntity te = world.getBlockEntity(pos.below());
	    if (te instanceof DispenserBlockEntity)
	    {
		((DispenserBlockEntity) te).clearContent();
		ItemStack message = generateLuckyMessage(rand, type);
		((DispenserBlockEntity) te).addItem(message);
	    }
	}
	else if ("ChestLoot1".equals(name) || "SetTrappedLoot".equals(name) || "BarrelLoot1".equals(name))
	{
	    String lootType = type == DungeonType.BASIC ? "basic" : "advanced";
	    String lootTable = "chests/chestloot_" + lootType + "_easy";
	    fillChestBelow(pos, new ResourceLocation(DimDungeons.RESOURCE_PREFIX + lootTable), world, rand);
	}
	else if ("ChestLoot2".equals(name))
	{
	    String lootType = type == DungeonType.BASIC ? "basic" : "advanced";
	    String lootTable = "chests/chestloot_" + lootType + "_hard";
	    fillChestBelow(pos, new ResourceLocation(DimDungeons.RESOURCE_PREFIX + lootTable), world, rand);
	}
	else if ("ChestLootLucky".equals(name))
	{
	    // 70% nothing, 30% random minecraft loot table that isn't an end city
	    int lucky = rand.nextInt(100);
	    if (lucky < 30)
	    {
		if (type == DungeonType.BASIC)
		{
		    fillChestBelow(pos, new ResourceLocation(DimDungeons.RESOURCE_PREFIX + "chests/chestloot_lucky"), world, rand);
		}
		else
		{
		    fillChestBelow(pos, new ResourceLocation(DimDungeons.RESOURCE_PREFIX + "chests/chestloot_crazy"), world, rand);
		}
	    }
	    else
	    {
		// actually within that 70% of nothing, if Artifacts is installed, then have a 10% chance of a mimic!
		if (DungeonConfig.isModInstalled("artifacts"))
		{
		    if (lucky < 40)
		    {
			spawnMimicFromArtifactsMod(pos, "mimic", world);
		    }
		}

		world.setBlock(pos, Blocks.AIR.defaultBlockState(), 2); // erase this data block 
		world.setBlock(pos.below(), Blocks.AIR.defaultBlockState(), 2); // and erase the chest below it
	    }
	}
	else if ("PlaceL2Key".equals(name))
	{
	    world.setBlock(pos, Blocks.AIR.defaultBlockState(), 2); // erase this data block
	    TileEntityPortalKeyhole te = (TileEntityPortalKeyhole) world.getBlockEntity(pos.below());
	    if (te != null)
	    {
		ItemStack key = te.getObjectInserted();
		if (key.getItem() instanceof ItemPortalKey)
		{
		    ((ItemPortalKey) key.getItem()).activateKeyLevel2(world.getServer(), key);
		    te.setContents(key);
		}
	    }
	}
	else if ("SummonWitch".equals(name))
	{
	    world.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
	    spawnEnemyHere(pos, "minecraft:witch", world, genData.dungeonTheme, type);
	}
	else if ("SummonWaterEnemy".equals(name))
	{
	    world.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
	    spawnEnemyHere(pos, "minecraft:guardian", world, genData.dungeonTheme, type);
	}
	else if ("SummonEnderman".equals(name))
	{
	    world.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
	    spawnEnemyHere(pos, "minecraft:enderman", world, genData.dungeonTheme, type);
	}
	else if ("SummonEnemy1".equals(name))
	{
	    world.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);

	    int poolSize = DungeonConfig.basicEnemySet1.size();
	    String mobid = DungeonConfig.basicEnemySet1.get(rand.nextInt(poolSize));
	    if (genData.dungeonTheme > 0)
	    {
		poolSize = DungeonConfig.themeSettings.get(genData.dungeonTheme - 1).themeEnemySet1.size();
		mobid = DungeonConfig.themeSettings.get(genData.dungeonTheme - 1).themeEnemySet1.get(rand.nextInt(poolSize));
	    }
	    if (type == DungeonType.ADVANCED)
	    {
		poolSize = DungeonConfig.advancedEnemySet1.size();
		mobid = DungeonConfig.advancedEnemySet1.get(rand.nextInt(poolSize));
	    }

	    spawnEnemyHere(pos, mobid, world, genData.dungeonTheme, type);
	}
	else if ("SummonEnemy2".equals(name))
	{
	    world.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);

	    int poolSize = DungeonConfig.basicEnemySet2.size();
	    String mobid = DungeonConfig.basicEnemySet2.get(rand.nextInt(poolSize));
	    if (genData.dungeonTheme > 0)
	    {
		poolSize = DungeonConfig.themeSettings.get(genData.dungeonTheme - 1).themeEnemySet2.size();
		mobid = DungeonConfig.themeSettings.get(genData.dungeonTheme - 1).themeEnemySet2.get(rand.nextInt(poolSize));
	    }
	    if (type == DungeonType.ADVANCED)
	    {
		poolSize = DungeonConfig.advancedEnemySet2.size();
		mobid = DungeonConfig.advancedEnemySet2.get(rand.nextInt(poolSize));
	    }

	    spawnEnemyHere(pos, mobid, world, genData.dungeonTheme, type);
	}
	else
	{
	    DimDungeons.logMessageWarn("UNHANDLED DATA BLOCK WITH name = " + name);
	    world.setBlock(pos, Blocks.AIR.defaultBlockState(), 2); // erase this data block
	}
    }

    private static void spawnEnemyHere(BlockPos pos, String resourceLocation, ServerLevel world, int theme, DungeonType type)
    {
	EntityType<?> entitytype = EntityType.byString(resourceLocation).orElse(EntityType.CHICKEN);

	Entity mob = entitytype.spawn((ServerLevel) world, null, null, pos, MobSpawnType.STRUCTURE, true, true);
	mob.moveTo(pos, 0.0F, 0.0F);

	// append a "2" to the mob name in advanced dungeons
	String advancedDungeonNames = type == DungeonType.ADVANCED ? "2" : "";
	TranslatableComponent fancyName = new TranslatableComponent("enemy.dimdungeons." + resourceLocation + advancedDungeonNames);

	// don't nametag the mob if the translation string fails
	if (!(fancyName == null || fancyName.getString().contains("enemy.dimdungeons.")))
	{
	    mob.setCustomName(fancyName);
	}

	if (mob instanceof Mob)
	{
	    ((Mob) mob).setCanPickUpLoot(false);
	    ((Mob) mob).restrictTo(pos, 8);
	    ((Mob) mob).setPersistenceRequired();

	    // health scaling
	    double healthScaling = DungeonConfig.basicEnemyHealthScaling;
	    if (theme > 0)
	    {
		healthScaling = DungeonConfig.themeSettings.get(theme - 1).themeEnemyHealthScaling;
	    }
	    if (type == DungeonType.ADVANCED)
	    {
		healthScaling = DungeonConfig.advancedEnemyHealthScaling;
	    }
	    AttributeInstance tempHealth = ((Mob) mob).getAttribute(Attributes.MAX_HEALTH);
	    ((Mob) mob).getAttribute(Attributes.MAX_HEALTH).setBaseValue(tempHealth.getBaseValue() * healthScaling);
	    ((Mob) mob).setHealth((float) ((Mob) mob).getAttribute(Attributes.MAX_HEALTH).getBaseValue());

	    // randomly put a themed key into a mob's offhand
	    if (world.getRandom().nextInt(100) < DungeonConfig.chanceForThemeKeys && DungeonConfig.themeSettings.size() > 0 && theme < 1 && type != DungeonType.ADVANCED)
	    {
		// if the mob's offhand slot is occupied then just skip it
		if (!((Mob) mob).hasItemInSlot(EquipmentSlot.OFFHAND))
		{
		    int numThemes = DungeonConfig.themeSettings.size();
		    ItemStack stack = new ItemStack(ItemRegistrar.item_portal_key);
		    ((ItemPortalKey) (ItemRegistrar.item_portal_key.asItem())).activateKeyLevel1(world.getServer(), stack, world.getRandom().nextInt(numThemes) + 1);

		    ((Mob) mob).setItemInHand(InteractionHand.OFF_HAND, stack);
		    ((Mob) mob).setDropChance(EquipmentSlot.OFFHAND, 1.0f);
		}
	    }

	    if (type == DungeonType.ADVANCED)
	    {
		// ADVANCED MODE! EVEN HARDER MOBS!
		((Mob) mob).getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.35f); // baby zombie speed
		((Mob) mob).addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 9999999, 1, false, false));
		((Mob) mob).addEffect(new MobEffectInstance(MobEffects.JUMP, 9999999, 3, false, false));
		((Mob) mob).addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 9999999, 1, false, false));
	    }
	}
    }

    private static void fillChestBelow(BlockPos pos, ResourceLocation lootTable, LevelAccessor world, Random rand)
    {
	world.setBlock(pos, Blocks.AIR.defaultBlockState(), 2); // erase this data block
	RandomizableContainerBlockEntity.setLootTable(world, rand, pos.below(), lootTable);
    }

    // I was originally thinking that this would contain direct hints about the dungeon, but that would involve a post generation step
    private static ItemStack generateLuckyMessage(Random rand, DungeonType type)
    {
	ItemStack stack = new ItemStack(Items.WRITTEN_BOOK);
	stack.setTag(new CompoundTag());

	// randomize book contents
	int bookType = rand.nextInt(3);
	if (type == DungeonType.ADVANCED)
	{
	    bookType = 3;
	}

	int messageVariation = rand.nextInt(8) + 1;
	String title = "";
	String body = "";

	if (bookType == 0)
	{
	    title = new TranslatableComponent("book.dimdungeons.title_1").getString();
	    body = new TranslatableComponent("book.dimdungeons.fun_message_" + messageVariation).getString();

	}
	else if (bookType == 1)
	{
	    title = new TranslatableComponent("book.dimdungeons.title_2").getString();
	    body = new TranslatableComponent("book.dimdungeons.helpful_message_" + messageVariation).getString();
	}
	else if (bookType == 2)
	{
	    title = new TranslatableComponent("book.dimdungeons.title_3").getString();
	    body = new TranslatableComponent("book.dimdungeons.dangerous_message_" + messageVariation).getString();
	}
	else if (bookType == 3)
	{
	    title = new TranslatableComponent("book.dimdungeons.title_4").getString();
	    body = new TranslatableComponent("book.dimdungeons.advanced_message_" + messageVariation).getString();
	}

	// create the complicated NBT tag list for the list of pages in the book
	ListTag pages = new ListTag();
	Component text = new TranslatableComponent(body);
	String json = Component.Serializer.toJson(text);
	pages.add(0, StringTag.valueOf(json)); // 1.15

	// actually set all the bookish NBT on the item
	stack.getTag().putBoolean("resolved", false);
	stack.getTag().putInt("generation", 0);
	stack.getTag().put("pages", pages);
	stack.getTag().putString("title", title);
	stack.getTag().putString("author", new TranslatableComponent("book.dimdungeons.author").getString());
	return stack;
    }

    private static void spawnMimicFromArtifactsMod(BlockPos pos, String casualName, LevelAccessor world)
    {
	Mob mob = null;

	if (!DungeonConfig.isModInstalled("artifacts"))
	{
	    return; // fail safe
	}

	mob = (Mob) EntityType.byString("artifacts:mimic").get().create((Level) world);
	mob.setPos(pos.getX(), pos.getY(), pos.getZ());

	mob.setCanPickUpLoot(false);
	mob.moveTo(pos, 0.0F, 0.0F);
	mob.setPersistenceRequired();

	mob.finalizeSpawn((ServerLevelAccessor) world, world.getCurrentDifficultyAt(pos), MobSpawnType.STRUCTURE, (SpawnGroupData) null, (CompoundTag) null);
	world.addFreshEntity(mob);
    }
}
