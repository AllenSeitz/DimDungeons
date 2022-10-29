package com.catastrophe573.dimdungeons.structure;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Rotation;

public class DungeonDesignerTeleporterHub extends DungeonDesigner
{
	public DungeonDesignerTeleporterHub(RandomSource randIn, long chunkX, long chunkZ, DungeonType type, int theme)
	{
		super(randIn, chunkX, chunkZ, type, theme);
	}

	// kind of funny but teleporter hubs are just one large room
	public void calculateDungeonShape(int maxNumRooms, boolean useLarge)
	{
		placeRoomShape(3, 6, "dimdungeons:portal_room_1", RoomType.LARGE, Rotation.NONE);
	}
}
