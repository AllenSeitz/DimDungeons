package com.catastrophe573.dimdungeons.structure;

import java.util.ArrayList;
import java.util.List;

import com.catastrophe573.dimdungeons.DungeonConfig;
import com.catastrophe573.dimdungeons.structure.DungeonDesigner.DungeonType;
import com.catastrophe573.dimdungeons.structure.DungeonDesigner.RoomType;
import com.google.common.collect.Lists;

import net.minecraft.world.level.block.Rotation;

// this class exists to produce some hardcoded test dungeons for me to test with
public class DungeonDesignerTestShapes
{
    public static void MakeTestDungeonOne(DungeonDesigner dbl)
    {
	for (int i = 1; i <= 8; i++)
	{
	    dbl.placeRoomShape(i - 1, 0, "deadend_" + i, RoomType.END, Rotation.NONE);
	}
	for (int i = 1; i <= 4; i++)
	{
	    dbl.placeRoomShape(i - 1, 1, "redspuzzle_" + i, RoomType.END, Rotation.NONE);
	}
	dbl.placeRoomShape(5, 1, "freebie_1", RoomType.END, Rotation.NONE);
	dbl.placeRoomShape(6, 1, "freebie_2", RoomType.END, Rotation.NONE);
	dbl.placeRoomShape(7, 1, "freebie_3", RoomType.END, Rotation.NONE);
	for (int i = 1; i <= 6; i++)
	{
	    dbl.placeRoomShape(i - 1, 2, "spawner_" + i, RoomType.END, Rotation.NONE);
	}
	for (int i = 1; i <= 3; i++)
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
	dbl.placeRoomShape(4, 7, "entrance_9", RoomType.ENTRANCE, Rotation.NONE);
    }

    public static void MakeTestDungeonTwo(DungeonDesigner dbl)
    {
	for (int i = 1; i <= 8; i++)
	{
	    dbl.placeRoomShape(i - 1, 0, "entrance_" + i, RoomType.ENTRANCE, Rotation.NONE);
	}
	for (int i = 1; i <= 6; i++)
	{
	    dbl.placeRoomShape(i - 1, 1, "hallway_" + i, RoomType.HALLWAY, Rotation.NONE);
	}
	dbl.placeRoomShape(7, 1, "entrance_9", RoomType.ENTRANCE, Rotation.NONE);
	for (int i = 1; i <= 5; i++)
	{
	    dbl.placeRoomShape(i - 1, 2, "extrahall_" + i, RoomType.HALLWAY, Rotation.NONE);
	}
	dbl.placeRoomShape(6, 2, "moohall_1", RoomType.HALLWAY, Rotation.NONE);
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

	dbl.placeRoomShape(4, 7, "entrance_9", RoomType.ENTRANCE, Rotation.NONE);
    }

    public static void MakeTestDungeonThree(DungeonDesigner dbl)
    {
	for (int i = 1; i <= 4; i++)
	{
	    dbl.placeRoomShape(i - 1, 0, "disco_" + i, RoomType.FOURWAY, Rotation.NONE);
	}
	dbl.placeRoomShape(4, 0, "advice_1", RoomType.END, Rotation.NONE);
	dbl.placeRoomShape(5, 0, "advice_2", RoomType.THREEWAY, Rotation.NONE);
	dbl.placeRoomShape(6, 0, "advice_3", RoomType.HALLWAY, Rotation.NONE);
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

	dbl.placeRoomShape(4, 7, "entrance_9", RoomType.ENTRANCE, Rotation.NONE);
    }

    public static void MakeTestDungeonFour(DungeonDesigner dbl)
    {
	dbl.placeRoomShape(0, 0, "redspuzzle_4", RoomType.END, Rotation.NONE);
	dbl.placeRoomShape(1, 0, "morethree_4", RoomType.THREEWAY, Rotation.NONE);
	dbl.placeRoomShape(2, 0, "morethree_5", RoomType.THREEWAY, Rotation.NONE);
	dbl.placeRoomShape(3, 0, "morethree_6", RoomType.THREEWAY, Rotation.NONE);
	dbl.placeRoomShape(4, 0, "library_1", RoomType.HALLWAY, Rotation.NONE);
	dbl.placeRoomShape(5, 0, "library_2", RoomType.END, Rotation.NONE);

	dbl.placeRoomShape(0, 1, "crueltrap_1", RoomType.END, Rotation.NONE);
	dbl.placeRoomShape(1, 1, "crueltrap_2", RoomType.END, Rotation.NONE);
	dbl.placeRoomShape(2, 1, "crueltrap_3", RoomType.END, Rotation.NONE);
	dbl.placeRoomShape(3, 1, "blastchest_1", RoomType.END, Rotation.NONE);
	dbl.placeRoomShape(4, 1, "smeltery_v2", RoomType.END, Rotation.NONE);
	dbl.placeRoomShape(5, 1, "hiddenpath_3", RoomType.THREEWAY, Rotation.NONE);
	dbl.placeRoomShape(6, 1, "hiddenpath_1", RoomType.THREEWAY, Rotation.NONE);
	dbl.placeRoomShape(7, 1, "hiddenpath_2", RoomType.FOURWAY, Rotation.NONE);

	dbl.placeRoomShape(0, 2, "waterhall_1", RoomType.HALLWAY, Rotation.NONE);
	dbl.placeRoomShape(1, 2, "yinyang_1", RoomType.HALLWAY, Rotation.NONE);
	dbl.placeRoomShape(2, 2, "yinyang_2", RoomType.HALLWAY, Rotation.NONE);
	dbl.placeRoomShape(3, 2, "magicpuzzle_1", RoomType.END, Rotation.NONE);
	dbl.placeRoomShape(4, 2, "beacon_1", RoomType.END, Rotation.NONE);
	dbl.placeRoomShape(5, 2, "beacon_2", RoomType.END, Rotation.NONE);

	dbl.placeRoomShape(0, 4, "spawner_1", RoomType.END, Rotation.NONE);
	dbl.placeRoomShape(1, 4, "spawner_2", RoomType.END, Rotation.NONE);
	dbl.placeRoomShape(2, 4, "spawner_3", RoomType.END, Rotation.NONE);
	dbl.placeRoomShape(3, 4, "spawner_4", RoomType.END, Rotation.NONE);
	dbl.placeRoomShape(4, 4, "spawner_5", RoomType.END, Rotation.NONE);
	dbl.placeRoomShape(5, 4, "spawner_6", RoomType.END, Rotation.NONE);

	dbl.placeRoomShape(4, 7, "entrance_9", RoomType.ENTRANCE, Rotation.NONE);
    }

    public static void MakeTestDungeonDynamic(DungeonDesigner dbl, DungeonType tier, RoomType type)
    {
	ArrayList<String> allrooms = Lists.newArrayList();
	List<? extends List<String>> theListWeCareAbout = DungeonConfig.basicEntrances;

	// first create a 'pointer' to the list of rooms that I need to debug
	if (tier == DungeonType.BASIC && type == RoomType.FOURWAY)
	{
	    theListWeCareAbout = DungeonConfig.basicFourways;
	}
	if (tier == DungeonType.BASIC && type == RoomType.THREEWAY)
	{
	    theListWeCareAbout = DungeonConfig.basicThreeways;
	}
	if (tier == DungeonType.BASIC && type == RoomType.HALLWAY)
	{
	    theListWeCareAbout = DungeonConfig.basicHallways;
	}
	if (tier == DungeonType.BASIC && type == RoomType.CORNER)
	{
	    theListWeCareAbout = DungeonConfig.basicCorners;
	}
	if (tier == DungeonType.BASIC && type == RoomType.END)
	{
	    theListWeCareAbout = DungeonConfig.basicEnds;
	}
	if (tier == DungeonType.ADVANCED && type == RoomType.FOURWAY)
	{
	    theListWeCareAbout = DungeonConfig.advancedFourways;
	}
	if (tier == DungeonType.ADVANCED && type == RoomType.THREEWAY)
	{
	    theListWeCareAbout = DungeonConfig.advancedThreeways;
	}
	if (tier == DungeonType.ADVANCED && type == RoomType.HALLWAY)
	{
	    theListWeCareAbout = DungeonConfig.advancedHallways;
	}
	if (tier == DungeonType.ADVANCED && type == RoomType.CORNER)
	{
	    theListWeCareAbout = DungeonConfig.advancedCorners;
	}
	if (tier == DungeonType.ADVANCED && type == RoomType.END)
	{
	    theListWeCareAbout = DungeonConfig.advancedEnds;
	}

	// next assemble a list of every room in every set of rooms for the type of room that we care about
	for (int i = 0; i < theListWeCareAbout.size(); i++)
	{
	    for (int j = 0; j < theListWeCareAbout.get(i).size(); j++)
	    {
		String temp = theListWeCareAbout.get(i).get(j);
		allrooms.add(temp.replace("dimdungeons:", ""));
	    }
	}

	// first place an entrance, because I got sick of not having one
	dbl.placeRoomShape(4, 7, "entrance_1", RoomType.ENTRANCE, Rotation.NONE);

	// now place all the rooms in the layout
	for (int i = 0; i < allrooms.size(); i++)
	{
	    // hack to skip over the entrance room, and move this room elsewhere
	    if ((i / 8 == 4) && (i % 8 == 7))
	    {
		int beyondEnd = allrooms.size() + 1;
		dbl.placeRoomShape(beyondEnd / 8, beyondEnd % 8, allrooms.get(i), type, Rotation.NONE);
	    }
	    else
	    {
		dbl.placeRoomShape(i / 8, i % 8, allrooms.get(i), type, Rotation.NONE);
	    }
	}
    }

    public static void MakeTestDungeonForTheme(DungeonDesigner dbl, int theme)
    {
	ArrayList<String> allrooms = Lists.newArrayList();

	// assemble a list of every room in every set of rooms for the theme that we care about
	for (int i = 0; i < DungeonConfig.themeSettings.get(theme-1).themeEntrances.size(); i++)
	{
	    for (int j = 0; j < DungeonConfig.themeSettings.get(theme-1).themeEntrances.get(i).size(); j++)
	    {
		String temp = DungeonConfig.themeSettings.get(theme-1).themeEntrances.get(i).get(j);
		allrooms.add(temp.replace("dimdungeons:", ""));
	    }
	}
	for (int i = 0; i < DungeonConfig.themeSettings.get(theme-1).themeFourways.size(); i++)
	{
	    for (int j = 0; j < DungeonConfig.themeSettings.get(theme-1).themeFourways.get(i).size(); j++)
	    {
		String temp = DungeonConfig.themeSettings.get(theme-1).themeFourways.get(i).get(j);
		allrooms.add(temp.replace("dimdungeons:", ""));
	    }
	}
	for (int i = 0; i < DungeonConfig.themeSettings.get(theme-1).themeThreeways.size(); i++)
	{
	    for (int j = 0; j < DungeonConfig.themeSettings.get(theme-1).themeThreeways.get(i).size(); j++)
	    {
		String temp = DungeonConfig.themeSettings.get(theme-1).themeThreeways.get(i).get(j);
		allrooms.add(temp.replace("dimdungeons:", ""));
	    }
	}
	for (int i = 0; i < DungeonConfig.themeSettings.get(theme-1).themeHallways.size(); i++)
	{
	    for (int j = 0; j < DungeonConfig.themeSettings.get(theme-1).themeHallways.get(i).size(); j++)
	    {
		String temp = DungeonConfig.themeSettings.get(theme-1).themeHallways.get(i).get(j);
		allrooms.add(temp.replace("dimdungeons:", ""));
	    }
	}
	for (int i = 0; i < DungeonConfig.themeSettings.get(theme-1).themeCorners.size(); i++)
	{
	    for (int j = 0; j < DungeonConfig.themeSettings.get(theme-1).themeCorners.get(i).size(); j++)
	    {
		String temp = DungeonConfig.themeSettings.get(theme-1).themeCorners.get(i).get(j);
		allrooms.add(temp.replace("dimdungeons:", ""));
	    }
	}
	for (int i = 0; i < DungeonConfig.themeSettings.get(theme-1).themeEnds.size(); i++)
	{
	    for (int j = 0; j < DungeonConfig.themeSettings.get(theme-1).themeEnds.get(i).size(); j++)
	    {
		String temp = DungeonConfig.themeSettings.get(theme-1).themeEnds.get(i).get(j);
		allrooms.add(temp.replace("dimdungeons:", ""));
	    }
	}

	// first place an entrance, because I got sick of not having one
	dbl.placeRoomShape(4, 7, allrooms.get(0), RoomType.ENTRANCE, Rotation.NONE);

	// now place all the rooms in the layout
	for (int i = 1; i < allrooms.size(); i++)
	{
	    // hack to skip over the entrance room, and move this room elsewhere
	    if ((i / 8 == 4) && (i % 8 == 7))
	    {
		int beyondEnd = allrooms.size() + 1;
		dbl.placeRoomShape(beyondEnd / 8, beyondEnd % 8, allrooms.get(i), RoomType.FOURWAY, Rotation.NONE);
	    }
	    else
	    {
		dbl.placeRoomShape(i / 8, i % 8, allrooms.get(i), RoomType.FOURWAY, Rotation.NONE);
	    }
	}
    }
}
