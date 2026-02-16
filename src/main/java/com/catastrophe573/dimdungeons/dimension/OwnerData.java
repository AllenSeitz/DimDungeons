package com.catastrophe573.dimdungeons.dimension;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// this just a data structure for information about a single pocket dimension's owner
// the class PersonalBuildData manages a list of these structures
public class OwnerData
{
    UUID uuid;
    String string_uuid; // added in 1.21.11 for convenience during serialization
    String playerName;

    // this data was added later and might not exist in all worlds
    ArrayList<String> guestList;
    boolean isBlacklist;

    // added for the 1.21.11 port because legacy servers saved a simple list of OwnerData and used this information to recreate the hash later
    int chunkX = -1;
    int chunkZ = -1;

    public static final Codec<OwnerData> OWNER_DATA_CODEC = RecordCodecBuilder.create(
            instance ->
            {
                return instance.group(
                        Codec.STRING.fieldOf("uuid").forGetter(sd -> sd.string_uuid),
                        Codec.STRING.fieldOf("name").forGetter(sd -> sd.playerName),
                        Codec.list(Codec.STRING).fieldOf("guestList").forGetter(sd -> sd.guestList),
                        Codec.BOOL.fieldOf("isBlacklist").forGetter(sd -> sd.isBlacklist),
                        Codec.INT.fieldOf("x").forGetter(sd -> sd.chunkX),
                        Codec.INT.fieldOf("z").forGetter(sd -> sd.chunkZ)
                ).apply(instance, OwnerData::new);
            }
    );

    OwnerData(Player player)
    {
        uuid = player.getUUID();
        string_uuid = uuid.toString();
        playerName = player.getName().getString();

        guestList = new ArrayList<String>();
        isBlacklist = false;
    }

    public OwnerData(String p_uuid, String p_name, List<String> p_guestlist, Boolean p_blacklist, int p_x, int p_z)
    {
        uuid = UUID.fromString(p_uuid);
        string_uuid = p_uuid;
        playerName = p_name;

        guestList = new ArrayList<String>();
        isBlacklist = p_blacklist;

        chunkX = p_x;
        chunkZ = p_z;
    }
};