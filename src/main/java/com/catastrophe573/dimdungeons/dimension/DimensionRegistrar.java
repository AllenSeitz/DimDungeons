package com.catastrophe573.dimdungeons.dimension;

import com.catastrophe573.dimdungeons.DimDungeons;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ModDimension;
import net.minecraftforge.event.RegistryEvent.Register;

public class DimensionRegistrar
{
    // a reference to the dimension that this mod adds, similar to EntityType or any Block
    public static DimensionType dungeon_dimension_type;
    
    public static void registerAllDimensions(Register<ModDimension> dimRegistryEvent)
    {
	DimensionType dungeon_dimension_type = DimensionManager.registerDimension(DungeonDimension.dimension_id, DungeonDimension.newModDimension(), new PacketBuffer(Unpooled.wrappedBuffer(new byte[] {})));
	
	DimDungeons.LOGGER.info("TESTING TYPE: " + dungeon_dimension_type.toString());	
	
	dimRegistryEvent.getRegistry().register(DungeonDimension.newModDimension());
    }    
}