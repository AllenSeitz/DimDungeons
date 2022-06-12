package com.catastrophe573.dimdungeons.structure;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Rotation;

public class DungeonDesignerThemeOpen extends DungeonDesigner
{
    public DungeonDesignerThemeOpen(RandomSource randIn, long chunkX, long chunkZ, DungeonType type, int theme)
    {
	super(randIn, chunkX, chunkZ, type, theme);
    }

    // when this function is done you may read the dungeon layout from the public variable finalLayout
    public void calculateDungeonShape(int maxNumRooms, boolean useLarge)
    {
	//System.out.println("START CALC DUNGEON SHAPE");

	// step 1: place a constant entrance at the center of the bottom row
	placeRoomShape(4, 7, entrance.get(entranceIndex), RoomType.ENTRANCE, Rotation.NONE);
	entranceIndex++;

	// step 2: place two corners next to the entrance
	placeRoomShape(3, 7, corner.get(cornerIndex), RoomType.CORNER, Rotation.NONE);
	cornerIndex = cornerIndex == corner.size() - 1 ? 0 : cornerIndex + 1;
	placeRoomShape(5, 7, corner.get(cornerIndex), RoomType.CORNER, Rotation.COUNTERCLOCKWISE_90);
	cornerIndex = cornerIndex == corner.size() - 1 ? 0 : cornerIndex + 1;

	// place a 3x3 of fourways in the middle
	placeRoomShape(3, 4, fourway.get(fourwayIndex), RoomType.FOURWAY, Rotation.NONE);
	fourwayIndex = fourwayIndex == fourway.size() - 1 ? 0 : fourwayIndex + 1;
	placeRoomShape(4, 4, fourway.get(fourwayIndex), RoomType.FOURWAY, Rotation.NONE);
	fourwayIndex = fourwayIndex == fourway.size() - 1 ? 0 : fourwayIndex + 1;
	placeRoomShape(5, 4, fourway.get(fourwayIndex), RoomType.FOURWAY, Rotation.NONE);
	fourwayIndex = fourwayIndex == fourway.size() - 1 ? 0 : fourwayIndex + 1;
	placeRoomShape(3, 5, fourway.get(fourwayIndex), RoomType.FOURWAY, Rotation.NONE);
	fourwayIndex = fourwayIndex == fourway.size() - 1 ? 0 : fourwayIndex + 1;
	placeRoomShape(4, 5, fourway.get(fourwayIndex), RoomType.FOURWAY, Rotation.NONE);
	fourwayIndex = fourwayIndex == fourway.size() - 1 ? 0 : fourwayIndex + 1;
	placeRoomShape(5, 5, fourway.get(fourwayIndex), RoomType.FOURWAY, Rotation.NONE);
	fourwayIndex = fourwayIndex == fourway.size() - 1 ? 0 : fourwayIndex + 1;
	placeRoomShape(3, 6, fourway.get(fourwayIndex), RoomType.FOURWAY, Rotation.NONE);
	fourwayIndex = fourwayIndex == fourway.size() - 1 ? 0 : fourwayIndex + 1;
	placeRoomShape(4, 6, fourway.get(fourwayIndex), RoomType.FOURWAY, Rotation.NONE);
	fourwayIndex = fourwayIndex == fourway.size() - 1 ? 0 : fourwayIndex + 1;
	placeRoomShape(5, 6, fourway.get(fourwayIndex), RoomType.FOURWAY, Rotation.NONE);
	fourwayIndex = fourwayIndex == fourway.size() - 1 ? 0 : fourwayIndex + 1;

	// place dead ends around the edge
	placeRoomShape(3, 3, end.get(endIndex), RoomType.END, Rotation.NONE);
	endIndex = endIndex == end.size() - 1 ? 0 : endIndex + 1;
	placeRoomShape(4, 3, end.get(endIndex), RoomType.END, Rotation.NONE);
	endIndex = endIndex == end.size() - 1 ? 0 : endIndex + 1;
	placeRoomShape(5, 3, end.get(endIndex), RoomType.END, Rotation.NONE);
	endIndex = endIndex == end.size() - 1 ? 0 : endIndex + 1;
	placeRoomShape(2, 4, end.get(endIndex), RoomType.END, Rotation.COUNTERCLOCKWISE_90);
	endIndex = endIndex == end.size() - 1 ? 0 : endIndex + 1;
	placeRoomShape(2, 5, end.get(endIndex), RoomType.END, Rotation.COUNTERCLOCKWISE_90);
	endIndex = endIndex == end.size() - 1 ? 0 : endIndex + 1;
	placeRoomShape(2, 6, end.get(endIndex), RoomType.END, Rotation.COUNTERCLOCKWISE_90);
	endIndex = endIndex == end.size() - 1 ? 0 : endIndex + 1;
	placeRoomShape(6, 4, end.get(endIndex), RoomType.END, Rotation.CLOCKWISE_90);
	endIndex = endIndex == end.size() - 1 ? 0 : endIndex + 1;
	placeRoomShape(6, 5, end.get(endIndex), RoomType.END, Rotation.CLOCKWISE_90);
	endIndex = endIndex == end.size() - 1 ? 0 : endIndex + 1;
	placeRoomShape(6, 6, end.get(endIndex), RoomType.END, Rotation.CLOCKWISE_90);
	endIndex = endIndex == end.size() - 1 ? 0 : endIndex + 1;
    }
}
