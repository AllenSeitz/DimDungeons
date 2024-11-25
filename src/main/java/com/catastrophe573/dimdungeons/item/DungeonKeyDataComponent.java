package com.catastrophe573.dimdungeons.item;

import com.catastrophe573.dimdungeons.structure.DungeonDesigner;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class DungeonKeyDataComponent
{
    public static DungeonKeyDataComponentRecord getDefaultKeyData()
    {
        return new DungeonKeyDataComponentRecord(
                false, // activated
                false, // built
                -1, // dest_x
                -1, // dest_z
                0,  // name_type
                0,  // name_part_1
                0,  // name_part_2
                0,  // theme
                String.valueOf(DungeonDesigner.DungeonType.valueOf(String.valueOf(DungeonDesigner.DungeonType.BASIC)))
        );
    }

    public static final Codec<DungeonKeyDataComponentRecord> DUNGEON_KEY_DCR_CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
             Codec.BOOL.fieldOf("key_activated").forGetter(DungeonKeyDataComponentRecord::key_activated),
             Codec.BOOL.fieldOf("built").forGetter(DungeonKeyDataComponentRecord::built),
             Codec.LONG.fieldOf("dest_x").forGetter(DungeonKeyDataComponentRecord::dest_x),
             Codec.LONG.fieldOf("dest_z").forGetter(DungeonKeyDataComponentRecord::dest_z),
             Codec.INT.fieldOf("name_type").forGetter(DungeonKeyDataComponentRecord::name_type),
             Codec.INT.fieldOf("name_part_1").forGetter(DungeonKeyDataComponentRecord::name_part_1),
             Codec.INT.fieldOf("name_part_2").forGetter(DungeonKeyDataComponentRecord::name_part_2),
             Codec.INT.fieldOf("theme").forGetter(DungeonKeyDataComponentRecord::theme),
             Codec.STRING.fieldOf("dungeon_type").forGetter(DungeonKeyDataComponentRecord::dungeon_type)
        ).apply(instance, DungeonKeyDataComponentRecord::new)
    );
}