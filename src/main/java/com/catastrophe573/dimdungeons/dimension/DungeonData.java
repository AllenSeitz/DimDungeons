package com.catastrophe573.dimdungeons.dimension;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import com.catastrophe573.dimdungeons.structure.DungeonDesigner;
import com.catastrophe573.dimdungeons.structure.DungeonDesigner.DungeonType;
import com.catastrophe573.dimdungeons.structure.DungeonDesigner.RoomType;
import com.catastrophe573.dimdungeons.structure.DungeonRoom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

// based off McJty's 1.18.1 example
public class DungeonData extends SavedData
{
    // keep track of which rooms are supposed to exist at each coordinate
    private Map<ChunkPos, DungeonRoom> roomMap = new HashMap<>();

    // keep track of the total number of dungeons ever built in this dimension
    private int numKeysRegistered = 0;
    
    private static String MY_DATA = "dungeon_data";
    
    @Nonnull
    public static DungeonData get(Level level)
    {
	if (level.isClientSide)
	{
	    throw new RuntimeException("Don't access this client-side!");
	}
	
	// get the vanilla storage manager from the level
	DimensionDataStorage storage = ((ServerLevel) level).getDataStorage();
	
	// get the DungeonData if it already exists for this level, otherwise create a new one
	return storage.computeIfAbsent(DungeonData::new, DungeonData::new, MY_DATA);
    }

    // if the chunk is empty then return null (this is expected)
    public DungeonRoom getRoomAtPos(ChunkPos pos)
    {
	return roomMap.getOrDefault(pos, null);
    }
    
    public int getNumKeysRegistered()
    {
	return numKeysRegistered;
    }

    public void notifyOfNewKeyActivation()
    {
	numKeysRegistered++;
    	setDirty();
    }    
    
    public void registerNewRooms(DungeonDesigner layout, long x, long z)
    {
	for (int i = 0; i < 8; i++)
	{
	    for (int j = 0; j < 8; j++)
	    {
		DungeonRoom nextRoom = layout.finalLayout[i][j];
		if (!nextRoom.hasRoom())
		{
		    continue;
		}
		else
		{
		    // calculate the chunkpos of this room and the blockpos to place the sign at
		    // The +4 offset is so that the dungeons align with vanilla blank maps!
		    ChunkPos cpos = new ChunkPos(((int) x / 16) + i + 4, ((int) z / 16) + j + 4);	
		    roomMap.computeIfAbsent(cpos, cp -> nextRoom);
		}
	    }
	}
	
    	setDirty();
    }
    
    // this is only called if it is registered, which it currently is not
    public void tick(Level level)
    {
    }

    // this constructor is called on fresh levels
    public DungeonData()
    {
	numKeysRegistered = 0;
    }

    // this constructor is called when data already exists
    public DungeonData(CompoundTag tag) 
    {
        ListTag allRooms = tag.getList("room_data", tag.getId()); // the second parameter returns a hardcoded 10, ask vanilla why

        for (net.minecraft.nbt.Tag t : allRooms) 
        {
            CompoundTag roomTag = (CompoundTag) t;
            ChunkPos pos = new ChunkPos(roomTag.getInt("x"), roomTag.getInt("z"));
            DungeonRoom room = new DungeonRoom();
            room.structure = roomTag.getString("structure");
            room.rotation = Rotation.valueOf(roomTag.getString("rotation"));
            room.roomType = RoomType.valueOf(roomTag.getString("room_type"));
            room.dungeonType = DungeonType.valueOf(roomTag.getString("dungeon_type"));
            
            roomMap.put(pos, room);
        }
        
        // the next thing in allData is the "otherData"
        CompoundTag totalKeyData = tag.getCompound("total_key_data");
        numKeysRegistered = totalKeyData.getInt("numKeysActivated");
    }

    @Override
    public CompoundTag save(CompoundTag tag)
    {	
	ListTag allRooms = new ListTag();
	roomMap.forEach((chunkPos, room) ->
	{
	    CompoundTag roomTag = new CompoundTag();
	    roomTag.putInt("x", chunkPos.x);
	    roomTag.putInt("z", chunkPos.z);
	    roomTag.putString("structure", room.structure);
	    roomTag.putString("rotation", room.rotation.toString());
	    roomTag.putString("room_type", room.roomType.toString());
	    roomTag.putString("dungeon_type", room.dungeonType.toString());
	    roomTag.putInt("theme", room.theme);
	    allRooms.add(roomTag);
	});	
	
	CompoundTag totalKeyData = new CompoundTag();
	totalKeyData.putInt("numKeysActivated", numKeysRegistered);
	
	tag.put("room_data", allRooms);
	tag.put("total_key_data", totalKeyData);
	return tag;
    }
}
