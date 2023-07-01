package com.catastrophe573.dimdungeons.structure;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.DungeonConfig;
import com.catastrophe573.dimdungeons.block.BlockPortalKeyhole;
import com.catastrophe573.dimdungeons.block.BlockRegistrar;
import com.catastrophe573.dimdungeons.block.TileEntityGoldPortal;
import com.catastrophe573.dimdungeons.block.TileEntityLocalTeleporter;
import com.catastrophe573.dimdungeons.block.TileEntityPortalKeyhole;
import com.catastrophe573.dimdungeons.dimension.DungeonData;
import com.catastrophe573.dimdungeons.item.ItemPortalKey;
import com.catastrophe573.dimdungeons.item.ItemRegistrar;
import com.catastrophe573.dimdungeons.structure.DungeonDesigner.DungeonType;
import com.catastrophe573.dimdungeons.structure.DungeonDesigner.RoomType;
import com.catastrophe573.dimdungeons.utils.DungeonGenData;
import com.catastrophe573.dimdungeons.utils.DungeonUtils;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

// this class takes the dungeon layout which is designed by DungeonBuilderLogic and actually places it in the world
public class DungeonPlacement
{
	public static int SIGN_Y = 49; // for the slow building feature

	public DungeonPlacement()
	{
	}

	// step 1 of the dungeon building process: build the dungeon in memory, then
	// place signs where all the rooms will go
	public static boolean beginDesignAndBuild(ServerLevel world, long x, long z, DungeonGenData genData)
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

		if (DungeonConfig.enableDebugCheats && DungeonUtils.doesKeyMatchDebugCheat(genData) > 0)
		{
			// no ticking, the dungeon is built instantly abort the operation here
			return DungeonPlacementDebug.place(world, x, z, DungeonUtils.doesKeyMatchDebugCheat(genData), genData);
		}
		if (genData.dungeonTheme == 2)
		{
			dungeonType = DungeonType.THEME_OPEN;
			dbl = new DungeonDesignerThemeOpen(world.getRandom(), entranceChunkX, entranceChunkZ, dungeonType, genData.dungeonTheme);
			dungeonSize = DungeonConfig.themeSettings.get(genData.dungeonTheme - 1).themeDungeonSize;
		}
		else if (genData.dungeonType == DungeonType.ADVANCED)
		{
			dungeonType = DungeonType.ADVANCED;
			dbl = new DungeonDesigner(world.getRandom(), entranceChunkX, entranceChunkZ, dungeonType, genData.dungeonTheme);
			dungeonSize = DungeonConfig.DEFAULT_ADVANCED_DUNGEON_SIZE;
			useLarge = true;
		}
		else if (genData.dungeonType == DungeonType.TELEPORTER_HUB)
		{
			dungeonType = DungeonType.TELEPORTER_HUB;
			dbl = new DungeonDesignerTeleporterHub(world.getRandom(), entranceChunkX, entranceChunkZ, dungeonType, genData.dungeonTheme);
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

		// and this function registers all the rooms the in the dimension data so they
		// can be built later
		DungeonData.get(world).registerNewRooms(dbl, x, z);

		return true;
	}

	// returns true if a room was built here or false if this chunk was skipped
	public static boolean buildRoomAtChunk(ServerLevel world, ChunkPos cpos)
	{
		BlockPos bpos = new BlockPos(cpos.getMinBlockX(), SIGN_Y, cpos.getMinBlockZ());

		DungeonRoom nextRoom = DungeonData.get(world).getRoomAtPos(cpos);
		if (nextRoom == null || wasRoomBuiltAtChunk(world, cpos))
		{
			return false; // no sign here means no room
		}

		// also, set bedrock two blocked under the sign to permanently signal that this
		// function has been called on this chunk already
		world.setBlockAndUpdate(bpos.below().below(), Blocks.BEDROCK.defaultBlockState());

		// step 3: place room here, with these parameters
		if (nextRoom.roomType == RoomType.LARGE)
		{
			if (!putLargeRoomHere(cpos, world, nextRoom))
			{
				DimDungeons.logMessageError("DIMDUNGEONS ERROR UNABLE TO PLACE ***LARGE*** STRUCTURE: " + nextRoom.structure);
				return false;
			}

			// only perform this step on normal dungeons. Teleporter hubs have a glass roof.
			if (nextRoom.dungeonType != DungeonType.TELEPORTER_HUB)
			{
				closeDoorsOnLargeRoom(cpos, world, nextRoom);

				// close the doors on the other 3 'fake' rooms now
				closeDoorsOnLargeRoom(new ChunkPos(cpos.x + 1, cpos.z), world, nextRoom);
				closeDoorsOnLargeRoom(new ChunkPos(cpos.x, cpos.z + 1), world, nextRoom);
				closeDoorsOnLargeRoom(new ChunkPos(cpos.x + 1, cpos.z + 1), world, nextRoom);
			}
		}
		else if (nextRoom.roomType == RoomType.LARGE_DUMMY)
		{
			// these doors are now closed after placing the large room, to ensure they
			// aren't overwritten
		}
		else if (!putRoomHere(cpos, world, nextRoom))
		{
			DimDungeons.logMessageError("DIMDUNGEONS ERROR UNABLE TO PLACE STRUCTURE: " + nextRoom.structure);
		}

		return true;
	}

	// checks for the second piece of bedrock under the sign. this is placed during
	// buildRoomAboveSign()
	public static boolean wasRoomBuiltAtChunk(Level world, ChunkPos cpos)
	{
		Level dungeonDim = DungeonUtils.getDungeonWorld(world.getServer());
		BlockPos bpos = new BlockPos(cpos.getMinBlockX(), SIGN_Y - 2, cpos.getMinBlockZ());
		return dungeonDim.getBlockState(bpos).getBlock() == Blocks.BEDROCK;
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
	public static boolean putLargeRoomHere(ChunkPos cpos, ServerLevel world, DungeonRoom room)
	{
		MinecraftServer minecraftserver = ((Level) world).getServer();
		StructureTemplateManager templatemanager = DungeonUtils.getDungeonWorld(minecraftserver).getStructureManager();

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
			if (template$blockinfo.nbt() != null)
			{
				StructureMode structuremode = StructureMode.valueOf(template$blockinfo.nbt().getString("mode"));
				if (structuremode == StructureMode.DATA)
				{
					handleDataBlock(template$blockinfo.nbt().getString("metadata"), template$blockinfo.pos(), world, world.getRandom(), placementsettings.getBoundingBox(), room);
				}
			}
		}

		return success;
	}

	public static void closeDoorsOnLargeRoom(ChunkPos cpos, ServerLevel world, DungeonRoom room)
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
		DungeonRoom westRoom = DungeonData.get(world).getRoomAtPos(west);
		DungeonRoom eastRoom = DungeonData.get(world).getRoomAtPos(east);
		DungeonRoom northRoom = DungeonData.get(world).getRoomAtPos(north);
		DungeonRoom southRoom = DungeonData.get(world).getRoomAtPos(south);

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
			if (westRoom.roomType != RoomType.LARGE && westRoom.roomType != RoomType.LARGE_DUMMY)
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
			if (eastRoom.roomType != RoomType.LARGE && eastRoom.roomType != RoomType.LARGE_DUMMY)
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
			if (northRoom.roomType != RoomType.LARGE && northRoom.roomType != RoomType.LARGE_DUMMY)
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
			if (southRoom.roomType != RoomType.LARGE && southRoom.roomType != RoomType.LARGE_DUMMY)
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

	// used by the place() and the slow building logic function to place a single
	// room
	public static boolean putRoomHere(ChunkPos cpos, ServerLevel world, DungeonRoom room)
	{
		MinecraftServer minecraftserver = ((Level) world).getServer();
		StructureTemplateManager templatemanager = DungeonUtils.getDungeonWorld(minecraftserver).getStructureManager();

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

		// next if the structure is to be rotated then it must also be offset, because
		// rotating a structure also moves it
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
		else // if (nextRoom.rotation == Rotation.NONE)
		{
			// north: no rotation
			placementsettings.setRotation(Rotation.NONE);
		}

		// this is the big call to the structure block
		boolean success = template.placeInWorld((ServerLevelAccessor) world, position, sizeRange, placementsettings, world.getRandom(), 2);

		// handle data blocks - this code block is copied from TemplateStructurePiece
		for (StructureTemplate.StructureBlockInfo template$blockinfo : template.filterBlocks(position, placementsettings, Blocks.STRUCTURE_BLOCK))
		{
			if (template$blockinfo.nbt() != null)
			{
				StructureMode structuremode = StructureMode.valueOf(template$blockinfo.nbt().getString("mode"));
				if (structuremode == StructureMode.DATA)
				{
					handleDataBlock(template$blockinfo.nbt().getString("metadata"), template$blockinfo.pos(), world, world.getRandom(), placementsettings.getBoundingBox(), room);
				}
			}
		}

		// replace all red carpet in entrance rooms with green carpet
		if (room.dungeonType == DungeonType.ADVANCED)
		{
			for (StructureBlockInfo info : template.filterBlocks(position, placementsettings, Blocks.RED_CARPET))
			{
				world.setBlock(info.pos(), Blocks.GREEN_CARPET.defaultBlockState(), 3);
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
	protected static void handleDataBlock(String name, BlockPos pos, ServerLevel world, RandomSource rand, BoundingBox bb, DungeonRoom room)
	{
		// DimDungeons.LOGGER.info("DATA BLOCK NAME: " + name);

		if ("ReturnPortal".equals(name))
		{
			world.setBlock(pos, BlockRegistrar.BLOCK_GOLD_PORTAL.get().defaultBlockState(), 2); // erase this data block
			TileEntityGoldPortal te = (TileEntityGoldPortal) world.getBlockEntity(pos);
			if (te != null)
			{
				// rely on the portal linking logic to update this later
				te.setDestination(0, -10000, 0, "minecraft:overworld", Direction.NORTH);
			}
		}
		else if ("BackToEntrance".equals(name))
		{
			world.setBlock(pos, BlockRegistrar.BLOCK_LOCAL_TELEPORTER.get().defaultBlockState(), 2); // erase this data block
			TileEntityLocalTeleporter te = (TileEntityLocalTeleporter) world.getBlockEntity(pos);
			if (te != null)
			{
				// this logic is copy/pasted from the HomewardPearl, which was implemented later
				// than this block
				double topLeftX = Math.floor(pos.getX() / ItemPortalKey.BLOCKS_APART_PER_DUNGEON);
				double entranceX = topLeftX * ItemPortalKey.BLOCKS_APART_PER_DUNGEON + ItemPortalKey.ENTRANCE_OFFSET_X;
				double topLeftZ = Math.floor(pos.getZ() / ItemPortalKey.BLOCKS_APART_PER_DUNGEON);
				double entranceZ = topLeftZ * ItemPortalKey.BLOCKS_APART_PER_DUNGEON + ItemPortalKey.ENTRANCE_OFFSET_Z;

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
//		else if ("LockWithCode".equals(name))
//		{
//			world.setBlock(pos, Blocks.AIR.defaultBlockState(), 2); // erase this data block
//			BlockEntity te = world.getBlockEntity(pos.below());
//
//			if (te instanceof BaseContainerBlockEntity)
//			{
//				CompoundTag tag = ((BaseContainerBlockEntity) te).getUpdateTag();
//				tag.putString("Lock", makeChunkCode(world.getChunkAt(pos).getPos()));
//				te.handleUpdateTag(tag);
//			}
//		}		
		else if ("FortuneTeller".equals(name))
		{
			world.setBlock(pos, Blocks.STONE_BRICKS.defaultBlockState(), 2); // erase this data block

			// put a message inside the dispenser
			BlockEntity te = world.getBlockEntity(pos.below());
			if (te instanceof DispenserBlockEntity)
			{
				((DispenserBlockEntity) te).clearContent();
				ItemStack message = generateLuckyMessage(rand, room.dungeonType);
				((DispenserBlockEntity) te).addItem(message);
			}
		}
		else if ("ChestLoot1".equals(name) || "SetTrappedLoot".equals(name) || "BarrelLoot1".equals(name))
		{
			String lootType = room.dungeonType == DungeonType.BASIC ? "basic" : "advanced";
			String lootTable = "chests/chestloot_" + lootType + "_easy";
			fillChestBelow(pos, new ResourceLocation(DimDungeons.RESOURCE_PREFIX + lootTable), world, rand);
		}
		else if ("ChestLoot2".equals(name))
		{
			String lootType = room.dungeonType == DungeonType.BASIC ? "basic" : "advanced";
			String lootTable = "chests/chestloot_" + lootType + "_hard";
			fillChestBelow(pos, new ResourceLocation(DimDungeons.RESOURCE_PREFIX + lootTable), world, rand);
		}
		else if ("ChestLootKit".equals(name))
		{
			String lootTable = "chests/kit_random";
			fillChestBelow(pos, new ResourceLocation(DimDungeons.RESOURCE_PREFIX + lootTable), world, rand);
		}
		else if ("ChestLootLucky".equals(name))
		{
			// 70% nothing, 30% random minecraft loot table that isn't an end city
			int lucky = rand.nextInt(100);
			if (lucky < 30)
			{
				if (room.dungeonType == DungeonType.BASIC)
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
				// actually within that 70% of nothing, if Artifacts is installed, then have a
				// 10% chance of a mimic!
				if (DungeonConfig.isModInstalled("artifacts"))
				{
					if (lucky < 40)
					{
						spawnEnemyHere(pos, "artifacts:mimic", world, room.theme, room.dungeonType);
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
				te.removeContents();
				te.setContents(new ItemStack(ItemRegistrar.ITEM_BLANK_ADVANCED_KEY.get()));
			}
		}
		else if (name.contains("TeleporterKey_"))
		{
			String tempDoornum = name.replace("TeleporterKey_", "");
			int doornum = Integer.valueOf(tempDoornum);

			world.setBlock(pos, Blocks.AIR.defaultBlockState(), 2); // erase this data block
			TileEntityPortalKeyhole te = (TileEntityPortalKeyhole) world.getBlockEntity(pos.below(2));
			if (te != null)
			{
				ItemStack newkey = new ItemStack(ItemRegistrar.ITEM_PORTAL_KEY.get());

				// reverse calculate the destX and destZ of the original key
				int topLeftX = bb.minX() - (3 * 16);
				int destX = (topLeftX / ItemPortalKey.BLOCKS_APART_PER_DUNGEON);
				int topLeftZ = bb.minZ() - (3 * 16);
				int destZ = (topLeftZ / ItemPortalKey.BLOCKS_APART_PER_DUNGEON);

				ItemPortalKey.activateKeyForExistingTeleporterHub(world.getServer(), newkey, destX, destZ, doornum);

				te.removeContents();
				te.setContents(newkey);

				// mark this keyhole as filled
				BlockState state = world.getBlockState(pos.below(2));
				BlockState newBlockState = state.setValue(BlockPortalKeyhole.FACING, state.getValue(BlockPortalKeyhole.FACING)).setValue(BlockPortalKeyhole.FILLED, true).setValue(BlockPortalKeyhole.LIT, false);
				world.setBlockAndUpdate(pos.below(2), newBlockState);

			}
		}
		else if ("SummonWitch".equals(name))
		{
			world.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
			spawnEnemyHere(pos, "minecraft:witch", world, room.theme, room.dungeonType);
		}
		else if ("SummonWaterEnemy".equals(name))
		{
			world.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
			spawnEnemyHere(pos, "minecraft:guardian", world, room.theme, room.dungeonType);
		}
		else if ("SummonEnderman".equals(name))
		{
			world.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
			spawnEnemyHere(pos, "minecraft:enderman", world, room.theme, room.dungeonType);
		}
		else if ("SummonEnemy1".equals(name))
		{
			world.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);

			int poolSize = DungeonConfig.basicEnemySet1.size();
			String mobid = DungeonConfig.basicEnemySet1.get(rand.nextInt(poolSize));
			if (room.theme > 0)
			{
				poolSize = DungeonConfig.themeSettings.get(room.theme - 1).themeEnemySet1.size();
				mobid = DungeonConfig.themeSettings.get(room.theme - 1).themeEnemySet1.get(rand.nextInt(poolSize));
			}
			if (room.dungeonType == DungeonType.ADVANCED)
			{
				poolSize = DungeonConfig.advancedEnemySet1.size();
				mobid = DungeonConfig.advancedEnemySet1.get(rand.nextInt(poolSize));
			}

			spawnEnemyHere(pos, mobid, world, room.theme, room.dungeonType);
		}
		else if ("SummonEnemy2".equals(name))
		{
			world.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);

			int poolSize = DungeonConfig.basicEnemySet2.size();
			String mobid = DungeonConfig.basicEnemySet2.get(rand.nextInt(poolSize));
			if (room.theme > 0)
			{
				poolSize = DungeonConfig.themeSettings.get(room.theme - 1).themeEnemySet2.size();
				mobid = DungeonConfig.themeSettings.get(room.theme - 1).themeEnemySet2.get(rand.nextInt(poolSize));
			}
			if (room.dungeonType == DungeonType.ADVANCED)
			{
				poolSize = DungeonConfig.advancedEnemySet2.size();
				mobid = DungeonConfig.advancedEnemySet2.get(rand.nextInt(poolSize));
			}

			Entity mob = spawnEnemyHere(pos, mobid, world, room.theme, room.dungeonType);
			
//			// Keyholders are entity 2s with some extra stuff
//			if ( "SummonKeyholder".equals(name) )
//			{
//				if (!((Mob) mob).hasItemInSlot(EquipmentSlot.CHEST))
//				{
//					ItemStack stack = new ItemStack(Items.STICK);
//					stack.setHoverName(Component.translatable(makeChunkCode(world.getChunk(pos).getPos())));
//
//					((Mob) mob).setItemSlot(EquipmentSlot.CHEST, stack);
//					((Mob) mob).setDropChance(EquipmentSlot.CHEST, 1.0f);
//				}
//			}

			// and give it an extra 50% health plus some potion buffs, because
			AttributeInstance tempHealth = ((Mob) mob).getAttribute(Attributes.MAX_HEALTH);
			((Mob) mob).getAttribute(Attributes.MAX_HEALTH).setBaseValue(tempHealth.getBaseValue() * 1.5f);
			((Mob) mob).setHealth((float) ((Mob) mob).getAttribute(Attributes.MAX_HEALTH).getBaseValue());

			((Mob) mob).addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 9999999, 1, false, false));
			((Mob) mob).addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 9999999, 1, false, false));			
		}
		else
		{
			DimDungeons.logMessageWarn("UNHANDLED DATA BLOCK WITH name = " + name);
			world.setBlock(pos, Blocks.AIR.defaultBlockState(), 2); // erase this data block
		}
	}

	private static Entity spawnEnemyHere(BlockPos pos, String resourceLocation, ServerLevel world, int theme, DungeonType type)
	{
		EntityType<?> entitytype = EntityType.byString(resourceLocation).orElse(EntityType.CHICKEN);

		// funny thing in 1.19.3 here. The second parameter really should be null.
		// however, a new alternative version of spawn() was added with nullable 2nd and 3rd parameters as well, causing an ambiguous reference
		// so instead this forces one of the two to be called, and should have no side effects
		Entity mob = entitytype.spawn((ServerLevel) world, new CompoundTag(), null, pos, MobSpawnType.STRUCTURE, true, true);
		// Entity mob = entitytype.spawn((ServerLevel) world, null, null, pos, MobSpawnType.STRUCTURE, true, true);

		if (mob == null)
		{
			return null; // this can happen if the mob in question does not exist, such as another mod
			             // named "Bad Mobs" preventing minecraft:zombie from spawning
		}
		mob.moveTo(pos, 0.0F, 0.0F);

		// append a "2" to the mob name in advanced dungeons
		String advancedDungeonNames = type == DungeonType.ADVANCED ? "2" : "";
		MutableComponent fancyName = Component.translatable("enemy.dimdungeons." + resourceLocation + advancedDungeonNames);

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
			int chanceForTheme = DungeonConfig.chanceForThemeKeys;
			if ( type == DungeonType.ADVANCED )
			{
				chanceForTheme /= 2; // because there are so many mobs
			}
			if (world.getRandom().nextInt(100) < chanceForTheme && DungeonConfig.themeSettings.size() > 0 && theme < 1)
			{
				// if the mob's offhand slot is occupied then just skip it
				if (!((Mob) mob).hasItemInSlot(EquipmentSlot.OFFHAND))
				{
					int numThemes = DungeonConfig.themeSettings.size();
					ItemStack stack = new ItemStack(ItemRegistrar.ITEM_PORTAL_KEY.get());
					((ItemPortalKey) (ItemRegistrar.ITEM_PORTAL_KEY.get())).activateKeyLevel1(world.getServer(), stack, world.getRandom().nextInt(numThemes) + 1);

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
		
		return mob;
	}

	private static void fillChestBelow(BlockPos pos, ResourceLocation lootTable, LevelAccessor world, RandomSource rand)
	{
		world.setBlock(pos, Blocks.AIR.defaultBlockState(), 2); // erase this data block
		RandomizableContainerBlockEntity.setLootTable(world, rand, pos.below(), lootTable);
	}

	// I was originally thinking that this would contain direct hints about the
	// dungeon, but that would involve a post generation step
	private static ItemStack generateLuckyMessage(RandomSource rand, DungeonType type)
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
			title = Component.translatable("book.dimdungeons.title_1").getString();
			body = Component.translatable("book.dimdungeons.fun_message_" + messageVariation).getString();

		}
		else if (bookType == 1)
		{
			title = Component.translatable("book.dimdungeons.title_2").getString();
			body = Component.translatable("book.dimdungeons.helpful_message_" + messageVariation).getString();
		}
		else if (bookType == 2)
		{
			title = Component.translatable("book.dimdungeons.title_3").getString();
			body = Component.translatable("book.dimdungeons.dangerous_message_" + messageVariation).getString();
		}
		else if (bookType == 3)
		{
			title = Component.translatable("book.dimdungeons.title_4").getString();
			body = Component.translatable("book.dimdungeons.advanced_message_" + messageVariation).getString();
		}

		// create the complicated NBT tag list for the list of pages in the book
		ListTag pages = new ListTag();
		Component text = Component.translatable(body);
		String json = Component.Serializer.toJson(text);
		pages.add(0, StringTag.valueOf(json)); // 1.15

		// actually set all the bookish NBT on the item
		stack.getTag().putBoolean("resolved", false);
		stack.getTag().putInt("generation", 0);
		stack.getTag().put("pages", pages);
		stack.getTag().putString("title", title);
		stack.getTag().putString("author", Component.translatable("book.dimdungeons.author").getString());
		return stack;
	}
}
