package com.catastrophe573.dimdungeons.structure;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.structure.DungeonDesigner.DungeonType;
import com.catastrophe573.dimdungeons.structure.DungeonDesigner.RoomType;
import com.catastrophe573.dimdungeons.utils.DungeonGenData;
import com.catastrophe573.dimdungeons.utils.DungeonUtils;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.server.level.ServerLevel;

// unlike the regular DungeonPlacement class, this class builds the entire dungeon in one tick
public class DungeonPlacementDebug extends DungeonPlacement
{
    public DungeonPlacementDebug()
    {
    }

    // this is something hardcoded and personal, and not meant to be invoked by players
    public static boolean place(ServerLevel world, long x, long z, int debugType, DungeonGenData genData)
    {
	long entranceChunkX = (x / 16) + 8;
	long entranceChunkZ = (z / 16) + 11;
	if (!isEntranceChunk(entranceChunkX, entranceChunkZ))
	{
	    DimDungeons.logMessageError("DIMDUNGEONS FATAL ERROR: debug dungeon does not start at " + x + ", " + z);
	    return false;
	}
	DimDungeons.logMessageInfo("DIMDUNGEONS START DEBUG STRUCTURE at " + x + ", " + z);

	// this is the data structure for an entire dungeon
	DungeonDesigner dbl = new DungeonDesigner(world.getRandom(), entranceChunkX, entranceChunkZ, DungeonType.BASIC, genData.dungeonTheme);
	switch (debugType)
	{
	case 1:
	    DungeonDesignerTestShapes.MakeTestDungeonOne(dbl);
	    break;
	case 2:
	    DungeonDesignerTestShapes.MakeTestDungeonTwo(dbl);
	    break;
	case 3:
	    DungeonDesignerTestShapes.MakeTestDungeonThree(dbl);
	    break;
	case 4:
	    DungeonDesignerTestShapes.MakeTestDungeonFour(dbl);
	    break;
	case 5:
	    DungeonDesignerTestShapes.MakeTestDungeonDynamic(dbl, DungeonType.BASIC, RoomType.FOURWAY);
	    break;
	case 6:
	    DungeonDesignerTestShapes.MakeTestDungeonDynamic(dbl, DungeonType.BASIC, RoomType.THREEWAY);
	    break;
	case 7:
	    DungeonDesignerTestShapes.MakeTestDungeonDynamic(dbl, DungeonType.BASIC, RoomType.HALLWAY);
	    break;
	case 8:
	    DungeonDesignerTestShapes.MakeTestDungeonDynamic(dbl, DungeonType.BASIC, RoomType.CORNER);
	    break;
	case 9:
	    DungeonDesignerTestShapes.MakeTestDungeonDynamic(dbl, DungeonType.BASIC, RoomType.END);
	    break;
	case 10:
	    DungeonDesignerTestShapes.MakeTestDungeonDynamic(dbl, DungeonType.ADVANCED, RoomType.FOURWAY);
	    break;
	case 11:
	    DungeonDesignerTestShapes.MakeTestDungeonDynamic(dbl, DungeonType.ADVANCED, RoomType.THREEWAY);
	    break;
	case 12:
	    DungeonDesignerTestShapes.MakeTestDungeonDynamic(dbl, DungeonType.ADVANCED, RoomType.HALLWAY);
	    break;
	case 13:
	    DungeonDesignerTestShapes.MakeTestDungeonDynamic(dbl, DungeonType.ADVANCED, RoomType.CORNER);
	    break;
	case 14:
	    DungeonDesignerTestShapes.MakeTestDungeonDynamic(dbl, DungeonType.ADVANCED, RoomType.END);
	    break;
	case 15:
	    DungeonDesignerTestShapes.MakeTestDungeonForTheme(dbl, genData.dungeonTheme);
	    break;
	}

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
		    // I'm not sure what the +4 is for, but it is needed
		    ChunkPos cpos = new ChunkPos(((int) x / 16) + i + 4, ((int) z / 16) + j + 4);

		    if (!putRoomHere(cpos, world, nextRoom, genData))
		    {
			DimDungeons.logMessageError("DIMDUNGEONS ERROR UNABLE TO PLACE STRUCTURE: " + nextRoom.structure);
		    }
		}
	    }
	}

	return true;
    }

    public static boolean isDungeonChunk(long x, long z)
    {
	if (x < 0 || z < 0)
	{
	    return false; // dungeons only spawn in the +x/+z quadrant
	}

	long plotX = x % 16;
	long plotZ = z % 16;
	return plotX > 3 && plotX < 12 && plotZ > 3 && plotZ < 12;
    }

    public static boolean isEntranceChunk(long x, long z)
    {
	if (x < 0 || z < 0)
	{
	    return false; // dungeons only spawn in the +x/+z quadrant
	}

	long plotX = x % 16;
	long plotZ = z % 16;
	return plotX == 8 && plotZ == 11;
    }

    // a test/debugging function that shouldn't be used in the final version
    public static void putTestStructureHere(long x, long z, LevelAccessor world)
    {
	ChunkPos cpos = new ChunkPos((int) x, (int) z);
	MinecraftServer minecraftserver = ((Level) world).getServer();
	StructureTemplateManager templatemanager = DungeonUtils.getDungeonWorld(minecraftserver).getStructureManager();

	StructureTemplate template = templatemanager.getOrCreate(new ResourceLocation(DimDungeons.RESOURCE_PREFIX + "basic_template"));
	StructurePlaceSettings placementsettings = (new StructurePlaceSettings()).setMirror(Mirror.NONE).setRotation(Rotation.NONE).setIgnoreEntities(false);
	placementsettings.setBoundingBox(placementsettings.getBoundingBox());
	placementsettings.setRotation(Rotation.NONE);
	BlockPos position = new BlockPos(cpos.getMinBlockX(), 50, cpos.getMinBlockZ());
	BlockPos sizeRange = new BlockPos(16, 13, 16);

	// I assume this function is addBlocksToWorld()			
	template.placeInWorld((ServerLevelAccessor) world, position, sizeRange, placementsettings, world.getRandom(), 2);
    }

    // used by the place() function to actually place rooms
    public static boolean putRoomHere(ChunkPos cpos, ServerLevel world, DungeonRoom room, DungeonGenData genData)
    {
	MinecraftServer minecraftserver = ((Level) world).getServer();
	StructureTemplateManager templatemanager = DungeonUtils.getDungeonWorld(minecraftserver).getStructureManager();

	StructureTemplate template = templatemanager.getOrCreate(new ResourceLocation(DimDungeons.RESOURCE_PREFIX + room.structure));
	StructurePlaceSettings placementsettings = (new StructurePlaceSettings()).setMirror(Mirror.NONE).setRotation(Rotation.NONE).setIgnoreEntities(false);
	placementsettings.setBoundingBox(placementsettings.getBoundingBox());

	placementsettings.setRotation(room.rotation);
	BlockPos position = new BlockPos(cpos.getMinBlockX(), 50, cpos.getMinBlockZ());
	BlockPos sizeRange = new BlockPos(16, 13, 16);

	if (template == null)
	{
	    DimDungeons.logMessageError("DIMDUNGEONS FATAL ERROR: Structure does not exist (" + room.structure + ")");
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

	// formerly: call Template.addBlocksToWorld()
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
		    handleDataBlock(template$blockinfo.nbt.getString("metadata"), template$blockinfo.pos, world, world.getRandom(), placementsettings.getBoundingBox(), room);
		}
	    }
	}
	return success;
    }
}