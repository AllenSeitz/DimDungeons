package com.catastrophe573.dimdungeons.structure;

import com.catastrophe573.dimdungeons.structure.DungeonDesigner.RoomType;

import net.minecraft.world.level.block.Rotation;

// dungeons are a maximum of 8x8 chunks (always much smaller) where each chunk is a structure at a specific rotation
public class DungeonRoom
{
    public String structure;
    public Rotation rotation;
    public RoomType type;

    DungeonRoom()
    {
	structure = "";
	rotation = Rotation.NONE;
	type = RoomType.NONE;
    }

    public boolean hasRoom()
    {
	return type != RoomType.NONE;
    }

    // warning: this function always returns true for large rooms
    public boolean hasDoorNorth()
    {
	return type == RoomType.FOURWAY || type == RoomType.LARGE || type == RoomType.LARGE_DUMMY || (type == RoomType.ENTRANCE && rotation != Rotation.CLOCKWISE_180) || (type == RoomType.THREEWAY && rotation != Rotation.NONE)
		|| (type == RoomType.CORNER && rotation == Rotation.NONE) || (type == RoomType.CORNER && rotation == Rotation.COUNTERCLOCKWISE_90) || (type == RoomType.HALLWAY && rotation == Rotation.NONE)
		|| (type == RoomType.HALLWAY && rotation == Rotation.CLOCKWISE_180) || (type == RoomType.END && rotation == Rotation.CLOCKWISE_180);
    }

    // warning: this function always returns true for large rooms
    public boolean hasDoorSouth()
    {
	return type == RoomType.FOURWAY || type == RoomType.LARGE || type == RoomType.LARGE_DUMMY || (type == RoomType.ENTRANCE && rotation != Rotation.NONE) || (type == RoomType.THREEWAY && rotation != Rotation.CLOCKWISE_180)
		|| (type == RoomType.CORNER && rotation == Rotation.CLOCKWISE_90) || (type == RoomType.CORNER && rotation == Rotation.CLOCKWISE_180) || (type == RoomType.HALLWAY && rotation == Rotation.NONE)
		|| (type == RoomType.HALLWAY && rotation == Rotation.CLOCKWISE_180) || (type == RoomType.END && rotation == Rotation.NONE);
    }

    // warning: this function always returns true for large rooms
    public boolean hasDoorWest()
    {
	return type == RoomType.FOURWAY || type == RoomType.LARGE || type == RoomType.LARGE_DUMMY || (type == RoomType.ENTRANCE && rotation != Rotation.CLOCKWISE_90) || (type == RoomType.THREEWAY && rotation != Rotation.COUNTERCLOCKWISE_90)
		|| (type == RoomType.CORNER && rotation == Rotation.COUNTERCLOCKWISE_90) || (type == RoomType.CORNER && rotation == Rotation.CLOCKWISE_180) || (type == RoomType.HALLWAY && rotation == Rotation.CLOCKWISE_90)
		|| (type == RoomType.HALLWAY && rotation == Rotation.COUNTERCLOCKWISE_90) || (type == RoomType.END && rotation == Rotation.CLOCKWISE_90);
    }

    // warning: this function always returns true for large rooms
    public boolean hasDoorEast()
    {
	return type == RoomType.FOURWAY || type == RoomType.LARGE || type == RoomType.LARGE_DUMMY || (type == RoomType.ENTRANCE && rotation != Rotation.COUNTERCLOCKWISE_90) || (type == RoomType.THREEWAY && rotation != Rotation.CLOCKWISE_90)
		|| (type == RoomType.CORNER && rotation == Rotation.NONE) || (type == RoomType.CORNER && rotation == Rotation.CLOCKWISE_90) || (type == RoomType.HALLWAY && rotation == Rotation.CLOCKWISE_90)
		|| (type == RoomType.HALLWAY && rotation == Rotation.COUNTERCLOCKWISE_90) || (type == RoomType.END && rotation == Rotation.COUNTERCLOCKWISE_90);
    }
};
