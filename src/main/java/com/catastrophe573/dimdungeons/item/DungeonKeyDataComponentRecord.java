package com.catastrophe573.dimdungeons.item;

public record DungeonKeyDataComponentRecord (
        boolean key_activated,
        boolean built,
        long dest_x,
        long dest_z,
        int name_type,
        int name_part_1,
        int name_part_2,
        int theme,
        String dungeon_type
) {}