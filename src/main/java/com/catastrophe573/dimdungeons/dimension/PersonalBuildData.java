package com.catastrophe573.dimdungeons.dimension;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.DungeonConfig;
import com.catastrophe573.dimdungeons.item.ItemBuildKey;
import com.catastrophe573.dimdungeons.structure.DungeonRoom;
import com.catastrophe573.dimdungeons.utils.DungeonUtils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.DimensionDataStorage;

public class PersonalBuildData extends SavedData
{
	// keep track of which rooms are supposed to exist at each coordinate
	private ConcurrentHashMap<ChunkPos, OwnerData> ownerMap = new ConcurrentHashMap<>();

	private static String PERSONAL_OWNER_DATA = "build_data";

	public static final Codec<PersonalBuildData> PERSONAL_OWNER_DATA_CODEC = RecordCodecBuilder.create(
			instance ->
			{
				return instance.group(
						//Codec.unboundedMap(ChunkPos.CODEC, OwnerData.OWNER_DATA_CODEC).fieldOf("player_data").forGetter(sd -> sd.ownerMap)
						Codec.list(OwnerData.OWNER_DATA_CODEC).fieldOf("player_data").forGetter(PersonalBuildData::preSerializeOwnerData)
				).apply(instance, PersonalBuildData::new);
			}
	);

	public static final SavedDataType<PersonalBuildData> PERSONAL_OWNER_DATA_TYPE = new SavedDataType<>(PERSONAL_OWNER_DATA, PersonalBuildData::new, PERSONAL_OWNER_DATA_CODEC);

	// this constructor is called on fresh levels
	public PersonalBuildData()
	{
	}

	public PersonalBuildData(List<OwnerData> allPlayerData)
	{
		for ( OwnerData owner : allPlayerData )
		{
			ChunkPos pos = new ChunkPos(owner.chunkX, owner.chunkZ);
			ownerMap.put(pos, owner);
		}
	}

	// older versions of this mod saved a simple list of OwnerData with the chunk pos (x,z) included with the other data, then reconstructed the HashMap
	// and so newer versions must respect this odd way of serializing data, even though it is now easy to directly store an unboundedMap
	public List<OwnerData> preSerializeOwnerData()
	{
		List<OwnerData> list = new ArrayList<OwnerData>();

		ownerMap.forEach((chunkPos, owner) ->
			 {
				 OwnerData temp = owner;
				 temp.chunkX = chunkPos.x;
				 temp.chunkZ = chunkPos.z;
				 list.add(temp);
			 }
		);
		return list;
	}

	@Nonnull
	public static SavedData get(Level level)
	{
		if (level.isClientSide())
		{
			throw new RuntimeException("Don't access this client-side!");
		}
		if (!DungeonUtils.isDimensionPersonalBuild(level))
		{
			throw new RuntimeException("PersonalBuildData is not supposed to exist in other dimensions! Calling this was probably unintended.");
		}

		// get the vanilla storage manager from the level
		DimensionDataStorage storage = ((ServerLevel) level).getDataStorage();

		// get the PersonalBuildData if it already exists for this level, otherwise create a new one
		return storage.computeIfAbsent(PERSONAL_OWNER_DATA_TYPE);
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

		// sanity check. The parameter to this function should always be a Player except when debugging
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
		cpos = getNewChunkPos(ownerMap.size() + 1, player.level().getServer());
		DimDungeons.logMessageInfo("DIMENSIONAL DUNGEONS: Assigning player " + player.getName().getString() + " the build plot at (" + cpos.x + ", " + cpos.z + ")");
		OwnerData newOwner = new OwnerData((Player) player);
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

	public void changeBlacklistMode(Player player, boolean isBlacklist)
	{
		OwnerData owner = getOwnerAtPos(getPosForOwner(player));
		owner.isBlacklist = isBlacklist;
		ownerMap.put(getPosForOwner(player), owner);
		setDirty();
	}

	public boolean getBlacklistMode(Player player)
	{
		OwnerData owner = getOwnerAtPos(getPosForOwner(player));
		return owner.isBlacklist;
	}

	public boolean isNameOnGuestList(Player player, String guestName)
	{
		OwnerData owner = getOwnerAtPos(getPosForOwner(player));
		if (owner.guestList.contains(guestName))
		{
			return true;
		}
		return false;
	}

	// returns true if the name was added, false otherwise
	public boolean toggleNameOnGuestList(Player player, String guestName)
	{
		OwnerData owner = getOwnerAtPos(getPosForOwner(player));
		if (owner.guestList.contains(guestName))
		{
			owner.guestList.remove(guestName);
			ownerMap.put(getPosForOwner(player), owner);
			setDirty();
			return false;
		}
		else
		{
			owner.guestList.add(guestName);
			ownerMap.put(getPosForOwner(player), owner);
			setDirty();
			return true;
		}
	}

	public void clearGuestListForPlayer(Player player)
	{
		OwnerData owner = getOwnerAtPos(getPosForOwner(player));
		owner.guestList.clear();
		ownerMap.put(getPosForOwner(player), owner);
		setDirty();
	}

	public ArrayList<String> getGuestListForPlayer(Player player)
	{
		OwnerData owner = getOwnerAtPos(getPosForOwner(player));
		return owner.guestList;
	}

	// remember that the ChunkPos destination is in "key coordinates" and is not an actual ChunkPos
	public boolean isPlayerAllowedInPersonalDimension(ServerPlayer visitor, ChunkPos destination)
	{
		OwnerData owner = getOwnerAtPos(destination);

		if (owner == null)
		{
			DimDungeons.logMessageInfo(visitor.nameAndId().name() + " is entering a personal build dimension, but the owner is null? Allowing entry.");
			return true; // shouldn't happen, but it happened to one person so this check is here now
		}

		String visitorName = visitor.nameAndId().name();

		// creative mode players and the owner themselves are never banned, even if configured otherwise
		// if (owner.uuid == visitor.getUUID() || visitor.isCreative() || DungeonConfig.disablePersonalDimSecurity)
		if (visitorName.contentEquals(owner.playerName) || visitor.isCreative() || DungeonConfig.disablePersonalDimSecurity)
		{
			return true;
		}

		// implement blacklist or whitelist
		if (owner.isBlacklist)
		{
			return !owner.guestList.contains(visitorName);
		}
		else
		{
			return owner.guestList.contains(visitorName);
		}
	}

	// do not do this on a real world, obviously
	public void debugClearKnownOwners()
	{
		ownerMap.clear();
		setDirty();
	}
}