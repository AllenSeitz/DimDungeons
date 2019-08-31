package com.catastrophe573.dimdungeons.dimension;

import com.catastrophe573.dimdungeons.DimDungeons;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ModDimension;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

public class DimensionRegistrar
{
    public static final String dungeon_basic_regname = "dungeon_dimension";

    @ObjectHolder(DimDungeons.MOD_ID + ":" + dungeon_basic_regname)
    public static final DungeonDimensionType DUNGEON_BASIC = new DungeonDimensionType(new ResourceLocation(DimDungeons.MOD_ID, dungeon_basic_regname));

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents
    {
	@SubscribeEvent
	public static void registerModDimensions(final RegistryEvent.Register<ModDimension> event)
	{
	    event.getRegistry().registerAll(DUNGEON_BASIC);

	    DimDungeons.LOGGER.info("Registered Dimensions");
	}
    }

    public static void registerDimensions()
    {
	DimensionManager.registerDimension(new ResourceLocation(DimDungeons.MOD_ID, dungeon_basic_regname), DUNGEON_BASIC, new PacketBuffer(Unpooled.buffer(16)), true);
    }
}