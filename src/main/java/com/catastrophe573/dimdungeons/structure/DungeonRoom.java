package com.catastrophe573.dimdungeons.structure;

import com.catastrophe573.dimdungeons.dimension.DungeonData;
import com.catastrophe573.dimdungeons.structure.DungeonDesigner.DungeonType;
import com.catastrophe573.dimdungeons.structure.DungeonDesigner.RoomType;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Rotation;

// dungeons are a maximum of 8x8 chunks (always much smaller) where each chunk is a structure at a specific rotation
public class DungeonRoom
{
	// chunkX and chunkZ only exist for serializing rooms in SavedData
	public int chunkX;
	public int chunkZ;

	public String structure;
	public Rotation rotation;
	public RoomType roomType;
	public DungeonDesigner.DungeonType dungeonType;
	public int theme;

	public DungeonRoom()
	{
		chunkX = chunkZ = -1;

		structure = "";
		rotation = Rotation.NONE;
		roomType = RoomType.NONE;
		dungeonType = DungeonType.BASIC;
		theme = 0;
	}

	public static final Codec<DungeonRoom> DUNGEON_ROOM_CODEC = RecordCodecBuilder.create(
			instance ->
			{
				return instance.group(
						Codec.INT.fieldOf("x").forGetter(sd -> sd.chunkX),
						Codec.INT.fieldOf("z").forGetter(sd -> sd.chunkZ),
						Codec.STRING.fieldOf("structure").forGetter(sd -> sd.structure.toString()),
						Codec.STRING.fieldOf("rotation").forGetter(sd -> sd.rotation.toString()),
						Codec.STRING.fieldOf("room_type").forGetter(sd -> sd.roomType.toString()),
						Codec.STRING.fieldOf("dungeon_type").forGetter(sd -> sd.dungeonType.toString()),
						Codec.INT.fieldOf("theme").forGetter(sd -> sd.theme)
				).apply(instance, DungeonRoom::new);
			}
	);

	// ugly constructor just for the above Codec
	public DungeonRoom(int p_x, int p_z, String p_structure, String p_rotation, String p_roomType, String p_dungeonType, int p_theme)
	{
		chunkX = p_x;
		chunkZ = p_z;

		structure = p_structure;
		rotation = Rotation.valueOf(p_rotation);
		roomType = RoomType.valueOf(p_roomType);
		dungeonType = DungeonType.valueOf(p_dungeonType);
		theme = p_theme;
	}

	public boolean hasRoom()
	{
		return roomType != RoomType.NONE;
	}

	// warning: this function always returns true for large rooms
	public boolean hasDoorNorth()
	{
		return roomType == RoomType.FOURWAY || roomType == RoomType.LARGE || roomType == RoomType.LARGE_DUMMY
		        || (roomType == RoomType.ENTRANCE && rotation != Rotation.CLOCKWISE_180) || (roomType == RoomType.THREEWAY && rotation != Rotation.NONE)
		        || (roomType == RoomType.CORNER && rotation == Rotation.NONE) || (roomType == RoomType.CORNER && rotation == Rotation.COUNTERCLOCKWISE_90)
		        || (roomType == RoomType.HALLWAY && rotation == Rotation.NONE) || (roomType == RoomType.HALLWAY && rotation == Rotation.CLOCKWISE_180)
		        || (roomType == RoomType.END && rotation == Rotation.CLOCKWISE_180);
	}

	// warning: this function always returns true for large rooms
	public boolean hasDoorSouth()
	{
		return roomType == RoomType.FOURWAY || roomType == RoomType.LARGE || roomType == RoomType.LARGE_DUMMY || (roomType == RoomType.ENTRANCE && rotation != Rotation.NONE)
		        || (roomType == RoomType.THREEWAY && rotation != Rotation.CLOCKWISE_180) || (roomType == RoomType.CORNER && rotation == Rotation.CLOCKWISE_90)
		        || (roomType == RoomType.CORNER && rotation == Rotation.CLOCKWISE_180) || (roomType == RoomType.HALLWAY && rotation == Rotation.NONE)
		        || (roomType == RoomType.HALLWAY && rotation == Rotation.CLOCKWISE_180) || (roomType == RoomType.END && rotation == Rotation.NONE);
	}

	// warning: this function always returns true for large rooms
	public boolean hasDoorWest()
	{
		return roomType == RoomType.FOURWAY || roomType == RoomType.LARGE || roomType == RoomType.LARGE_DUMMY
		        || (roomType == RoomType.ENTRANCE && rotation != Rotation.CLOCKWISE_90) || (roomType == RoomType.THREEWAY && rotation != Rotation.COUNTERCLOCKWISE_90)
		        || (roomType == RoomType.CORNER && rotation == Rotation.COUNTERCLOCKWISE_90) || (roomType == RoomType.CORNER && rotation == Rotation.CLOCKWISE_180)
		        || (roomType == RoomType.HALLWAY && rotation == Rotation.CLOCKWISE_90) || (roomType == RoomType.HALLWAY && rotation == Rotation.COUNTERCLOCKWISE_90)
		        || (roomType == RoomType.END && rotation == Rotation.CLOCKWISE_90);
	}

	// warning: this function always returns true for large rooms
	public boolean hasDoorEast()
	{
		return roomType == RoomType.FOURWAY || roomType == RoomType.LARGE || roomType == RoomType.LARGE_DUMMY
		        || (roomType == RoomType.ENTRANCE && rotation != Rotation.COUNTERCLOCKWISE_90) || (roomType == RoomType.THREEWAY && rotation != Rotation.CLOCKWISE_90)
		        || (roomType == RoomType.CORNER && rotation == Rotation.NONE) || (roomType == RoomType.CORNER && rotation == Rotation.CLOCKWISE_90)
		        || (roomType == RoomType.HALLWAY && rotation == Rotation.CLOCKWISE_90) || (roomType == RoomType.HALLWAY && rotation == Rotation.COUNTERCLOCKWISE_90)
		        || (roomType == RoomType.END && rotation == Rotation.COUNTERCLOCKWISE_90);
	}
};