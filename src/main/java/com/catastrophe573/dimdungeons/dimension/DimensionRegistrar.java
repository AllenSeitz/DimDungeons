package com.catastrophe573.dimdungeons.dimension;

import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.DimensionManager;

public class DimensionRegistrar
{
    //ObjectHolder("dimdungeons:dimension_dungeons") public static DimensionType dungeon_dimension_type;   
    public static final DimensionType dungeon_dimension_type = DimensionType.create("dimension_dungeons", getDungeonDimensionID(), "Dungeon", "_DIM_DUNGEON", DungeonDimension::new, false);
    
    // TODO: make this use a config
    private static int dungeon_id = 573;

    public static int getDungeonDimensionID()
    {
	return dungeon_id;
    }

    public static void registerDimensions()
    {
        DimensionManager.registerDimension(getDungeonDimensionID(), dungeon_dimension_type);
    }    
    
    // maybe someday in 1.14?
    //public static void registerAllDimensions(RegistryEvent.Register<Dimension> event)
    //{
    //event.getRegistry().register( );
    //}
}
