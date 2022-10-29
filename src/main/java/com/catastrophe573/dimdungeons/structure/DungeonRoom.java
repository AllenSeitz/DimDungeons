package com.catastrophe573.dimdungeons.structure;

import com.catastrophe573.dimdungeons.structure.DungeonDesigner.DungeonType;
import com.catastrophe573.dimdungeons.structure.DungeonDesigner.RoomType;

import net.minecraft.world.level.block.Rotation;

// dungeons are a maximum of 8x8 chunks (always much smaller) where each chunk is a structure at a specific rotation
public class DungeonRoom
{
	public String structure;
	public Rotation rotation;
	public RoomType roomType;
	public DungeonDesigner.DungeonType dungeonType;
	public int theme;

	public DungeonRoom()
	{
		structure = "";
		rotation = Rotation.NONE;
		roomType = RoomType.NONE;
		dungeonType = DungeonType.BASIC;
		theme = 0;
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
