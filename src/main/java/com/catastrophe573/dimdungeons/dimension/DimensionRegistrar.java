package com.catastrophe573.dimdungeons.dimension;

import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ModDimension;
import net.minecraftforge.event.RegistryEvent.Register;

public class DimensionRegistrar
{
    public static DimensionType dungeon_dimension_type;
    
    public static void registerAllDimensions(Register<ModDimension> dimRegistryEvent)
    {
	dimRegistryEvent.getRegistry().register(DungeonDimension.newModDimension());

	dungeon_dimension_type = DimensionManager.registerDimension(DungeonDimension.dimension_id, DungeonDimension.newModDimension(), null);
    }
}