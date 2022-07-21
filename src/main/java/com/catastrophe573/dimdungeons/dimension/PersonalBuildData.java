package com.catastrophe573.dimdungeons.dimension;

import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.DungeonConfig;
import com.catastrophe573.dimdungeons.item.ItemBuildKey;
import com.catastrophe573.dimdungeons.utils.DungeonUtils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

public class PersonalBuildData extends SavedData
{
    // just a data structure
    public class OwnerData
    {
	UUID uuid;
	String playerName;

	OwnerData(Player player)
	{
	    uuid = player.getUUID();
	    playerName = player.getName().getString();
	}

	OwnerData(UUID id, String name)
	{
	    uuid = id;
	    playerName = name;
	}
    };

    // keep track of which rooms are supposed to exist at each coordinate
    private ConcurrentHashMap<ChunkPos, OwnerData> ownerMap = new ConcurrentHashMap<>();

    private static String MY_DATA = "build_data";

    @Nonnull
    public static PersonalBuildData get(Level level)
    {
	if (level.isClientSide)
	{
	    throw new RuntimeException("Don't access this client-side!");
	}
	if (!DungeonUtils.isDimensionPersonalBuild(level))
	{
	    throw new RuntimeException("PersonalBuildData is not supposed to exist in other dimensions! Calling this was probably unintended.");
	}

	// get the vanilla storage manager from the level
	DimensionDataStorage storage = ((ServerLevel) level).getDataStorage();

	// get the DungeonData if it already exists for this level, otherwise create a new one
	return storage.computeIfAbsent(PersonalBuildData::new, PersonalBuildData::new, MY_DATA);
    }

    // if the chunk is empty then return null (this is expected)
    public OwnerData getOwnerAtPos(ChunkPos pos)
    {
	return ownerMap.getOrDefault(pos, null);
    }

    // returns a new or existing plot for this player
    // note that the return value is not a ChunkPos, but a [dest_x, dest_z] pair that is consistent with other key types
    public ChunkPos getPosForOwner(LivingEntity player)
    {
	Iterator<ChunkPos> iter = ownerMap.keySet().iterator();
	ChunkPos cpos;

	// sanity check. The parameter to this function should always be a Player except when debugging.
	if (player == null || (!(player instanceof Player) && !DungeonConfig.enableDebugCheats))
	{
	    DimDungeons.logMessageError("DIMENSIONAL DUNGEONS ERROR: registering personal key for a non-player or a null player.");
	    return null;
	}

	while (iter.hasNext())
	{
	    cpos = iter.next();
	    OwnerData nextOwner = ownerMap.get(cpos);

	    if (nextOwner.uuid.equals(player.getUUID()))
	    {
		DimDungeons.logMessageInfo("DIMENSIONAL DUNGEONS: Found existing build plot for player " + player.getName().getString() + " at (" + cpos.x + ", " + cpos.z + ")");
		return cpos; // this player has an existing plot
	    }
	}

	// pick the next available plot and also register it now
	cpos = getNewChunkPos(ownerMap.size() + 1, player.getServer());
	DimDungeons.logMessageInfo("DIMENSIONAL DUNGEONS: Assigning player " + player.getName().getString() + " the build plot at (" + cpos.x + ", " + cpos.z + ")");
	OwnerData newOwner = new OwnerData(player.getUUID(), player.getName().getString());
	ownerMap.computeIfAbsent(cpos, cp -> newOwner);
	setDirty();
	return cpos;
    }

    protected ChunkPos getNewChunkPos(int numOtherPlayers, MinecraftServer server)
    {
	// where is this key going?
	long generation_limit = DungeonUtils.getLimitOfPersonalBuildDimension(server);
	int plotsPerLimit = (int) (generation_limit / ItemBuildKey.BLOCKS_APART_PER_PLOT);

	// go as far as possible on the z-axis, then the x-axis, staying in the positive x/z quadrant
	int destZ = numOtherPlayers / plotsPerLimit;
	int destX = numOtherPlayers % plotsPerLimit;

	// warning: not actually a ChunkPos, but it is consistent with other keys use of dest_x and dest_z
	return new ChunkPos(destX, destZ);
    }
    
    // this constructor is called on fresh levels
    public PersonalBuildData()
    {
    }

    // this constructor is called when data already exists
    public PersonalBuildData(CompoundTag tag)
    {
	ListTag allOwners = tag.getList("player_data", tag.getId()); // the second parameter returns a hardcoded 10, ask vanilla why

	for (net.minecraft.nbt.Tag t : allOwners)
	{
	    CompoundTag ownerTag = (CompoundTag) t;
	    ChunkPos pos = new ChunkPos(ownerTag.getInt("x"), ownerTag.getInt("z"));
	    UUID id = UUID.fromString(ownerTag.getString("uuid"));
	    String name = ownerTag.getString("name");
	    OwnerData owner = new OwnerData(id, name);

	    ownerMap.put(pos, owner);
	}
    }

    @Override
    public CompoundTag save(CompoundTag tag)
    {
	ListTag allOwners = new ListTag();
	ownerMap.forEach((chunkPos, owner) ->
	{
	    CompoundTag ownerTag = new CompoundTag();
	    ownerTag.putInt("x", chunkPos.x);
	    ownerTag.putInt("z", chunkPos.z);
	    ownerTag.putString("uuid", owner.uuid.toString());
	    ownerTag.putString("name", owner.playerName);
	    allOwners.add(ownerTag);
	});

	tag.put("room_data", allOwners);
	return tag;
    }
    
    // do not do this on a real world, obviously
    public void debugClearKnownOwners()
    {
	ownerMap.clear();
	setDirty();
    }
}