package com.catastrophe573.dimdungeons.structure;

import com.catastrophe573.dimdungeons.structure.DungeonBuilderLogic.RoomType;

import net.minecraft.util.Rotation;

// this class exists to produce some hardcoded test dungeons for me to test with
public class DungeonBuilderTestShapes
{
    public static void MakeTestDungeonEnds(DungeonBuilderLogic dbl)
    {
	for (int i = 1; i <= 8; i++)
	{
	    dbl.placeRoomShape(i - 1, 0, "deadend_" + i, RoomType.END, Rotation.NONE);
	}
	for (int i = 1; i <= 4; i++)
	{
	    dbl.placeRoomShape(i - 1, 1, "redspuzzle_" + i, RoomType.END, Rotation.NONE);
	}
	for (int i = 1; i <= 6; i++)
	{
	    dbl.placeRoomShape(i - 1, 2, "spawner_" + i, RoomType.END, Rotation.NONE);
	}
	for (int i = 1; i <= 2; i++)
	{
	    dbl.placeRoomShape(i - 1, 3, "shoutout_" + i, RoomType.END, Rotation.NONE);
	}
	for (int i = 1; i <= 5; i++)
	{
	    dbl.placeRoomShape(i - 1 + 3, 3, "keytrap_" + i, RoomType.END, Rotation.NONE);
	}
	for (int i = 1; i <= 5; i++)
	{
	    dbl.placeRoomShape(i - 1, 4, "coffin_" + i, RoomType.END, Rotation.NONE);
	}
	for (int i = 1; i <= 5; i++)
	{
	    dbl.placeRoomShape(i - 1, 5, "restroom_" + i, RoomType.END, Rotation.NONE);
	}
	for (int i = 1; i <= 4; i++)
	{
	    dbl.placeRoomShape(i - 1, 6, "deathtrap_" + i, RoomType.END, Rotation.NONE);
	}
	for (int i = 1; i <= 4; i++)
	{
	    dbl.placeRoomShape(i - 1 + 4, 6, "keyroom_" + i, RoomType.END, Rotation.NONE);
	}
    }

    public static void MakeTestDungeonTwos(DungeonBuilderLogic dbl)
    {
	for (int i = 1; i <= 8; i++)
	{
	    dbl.placeRoomShape(i - 1, 0, "entrance_" + i, RoomType.ENTRANCE, Rotation.NONE);
	}
	for (int i = 1; i <= 6; i++)
	{
	    dbl.placeRoomShape(i - 1, 1, "hallway_" + i, RoomType.HALLWAY, Rotation.NONE);
	}
	for (int i = 1; i <= 5; i++)
	{
	    dbl.placeRoomShape(i - 1, 2, "extrahall_" + i, RoomType.HALLWAY, Rotation.NONE);
	}
	dbl.placeRoomShape(6, 2, "moohall", RoomType.HALLWAY, Rotation.NONE);
	dbl.placeRoomShape(7, 2, "skullcorner", RoomType.CORNER, Rotation.NONE);
	for (int i = 1; i <= 4; i++)
	{
	    dbl.placeRoomShape(i - 1, 3, "tempt_" + i, RoomType.HALLWAY, Rotation.NONE);
	}
	for (int i = 1; i <= 3; i++)
	{
	    dbl.placeRoomShape(i - 1 + 4, 3, "coalhall_" + i, RoomType.HALLWAY, Rotation.NONE);
	}
	for (int i = 1; i <= 8; i++)
	{
	    dbl.placeRoomShape(i - 1, 4, "corner_" + i, RoomType.CORNER, Rotation.NONE);
	}
	for (int i = 1; i <= 5; i++)
	{
	    dbl.placeRoomShape(i - 1, 5, "longcorner_" + i, RoomType.CORNER, Rotation.NONE);
	}
	dbl.placeRoomShape(5, 5, "yinyang_1", RoomType.HALLWAY, Rotation.NONE);
	dbl.placeRoomShape(6, 5, "yinyang_2", RoomType.HALLWAY, Rotation.NONE);	
    }

    public static void MakeTestDungeonThreesAndFours(DungeonBuilderLogic dbl)
    {
	for (int i = 1; i <= 4; i++)
	{
	    dbl.placeRoomShape(i - 1, 0, "disco_" + i, RoomType.FOURWAY, Rotation.NONE);
	}
	dbl.placeRoomShape(4, 0, "advice_room_1", RoomType.END, Rotation.NONE);
	dbl.placeRoomShape(5, 0, "advice_room_2", RoomType.THREEWAY, Rotation.NONE);
	dbl.placeRoomShape(6, 0, "advice_room_3", RoomType.HALLWAY, Rotation.NONE);
	for (int i = 1; i <= 6; i++)
	{
	    dbl.placeRoomShape(i - 1, 1, "combat_" + i, RoomType.FOURWAY, Rotation.NONE);
	}
	dbl.placeRoomShape(6, 1, "swimmaze_1", RoomType.FOURWAY, Rotation.NONE);
	dbl.placeRoomShape(7, 1, "fourway_9", RoomType.FOURWAY, Rotation.NONE);
	for (int i = 1; i <= 8; i++)
	{
	    dbl.placeRoomShape(i - 1, 2, "fourway_" + i, RoomType.FOURWAY, Rotation.NONE);
	}
	for (int i = 1; i <= 5; i++)
	{
	    dbl.placeRoomShape(i - 1, 3, "threeway_" + i, RoomType.THREEWAY, Rotation.NONE);
	}
	dbl.placeRoomShape(5, 3, "tetris_1", RoomType.THREEWAY, Rotation.NONE);	
	dbl.placeRoomShape(6, 3, "tetris_2", RoomType.THREEWAY, Rotation.NONE);	
	dbl.placeRoomShape(7, 3, "tetris_3", RoomType.THREEWAY, Rotation.NONE);		
	for (int i = 1; i <= 3; i++)
	{
	    dbl.placeRoomShape(i - 1, 4, "morethree_" + i, RoomType.THREEWAY, Rotation.NONE);
	}
	dbl.placeRoomShape(0, 5, "redstrap_1", RoomType.FOURWAY, Rotation.NONE);
	dbl.placeRoomShape(1, 5, "redstrap_2", RoomType.HALLWAY, Rotation.NONE);
	dbl.placeRoomShape(2, 5, "redstrap_3", RoomType.CORNER, Rotation.NONE);
	dbl.placeRoomShape(3, 5, "redstrap_4", RoomType.THREEWAY, Rotation.NONE);	
	dbl.placeRoomShape(4, 5, "mazenotfound_1", RoomType.CORNER, Rotation.NONE);	
	dbl.placeRoomShape(5, 5, "mazenotfound_2", RoomType.THREEWAY, Rotation.NONE);	
	dbl.placeRoomShape(6, 5, "mazenotfound_3", RoomType.HALLWAY, Rotation.NONE);	
    }
}
