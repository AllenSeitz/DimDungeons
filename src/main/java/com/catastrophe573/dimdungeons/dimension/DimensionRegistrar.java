package com.catastrophe573.dimdungeons.dimension;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import com.catastrophe573.dimdungeons.DimDungeons;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.Dimension;
import net.minecraft.world.DimensionType;
//import net.minecraftforge.common.DimensionManager;
//import net.minecraftforge.common.ModDimension;
//import net.minecraftforge.event.world.RegisterDimensionsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

// thank you tterrag for showing me how this is done in Tropicraft (1.14)
public class DimensionRegistrar
{
    public static final String dungeon_basic_regname = "dungeon_dimension";

    public static final DeferredRegister<ModDimension> DIMENSIONS = new DeferredRegister<>(ForgeRegistries.MOD_DIMENSIONS, DimDungeons.MOD_ID);

    public static DimensionType DUNGEON_DIMENSION;
    public static final RegistryObject<ModDimension> DUNGEON_MOD_DIMENSION = register(dungeon_basic_regname, DimensionRegistrar::dimFactory);

    private static ModDimension dimFactory()
    {
	return new ModDimension() {
	    @Override
	    public BiFunction<World, DimensionType, ? extends Dimension> getFactory()
	    {
		return DungeonDimension::new;
	    }
	};
    }

    private static RegistryObject<ModDimension> register(final String name, final Supplier<ModDimension> sup)
    {
	return DIMENSIONS.register(name, sup);
    }

    @Mod.EventBusSubscriber(modid = DimDungeons.MOD_ID)
    public static class EventDimensionType
    {
	@SubscribeEvent
	public static void onModDimensionRegister(final RegisterDimensionsEvent event)
	{
	    ResourceLocation id = new ResourceLocation(DimDungeons.MOD_ID, dungeon_basic_regname);
	    if (DimensionType.byName(id) == null)
	    {
		DUNGEON_DIMENSION = DimensionManager.registerDimension(id, DUNGEON_MOD_DIMENSION.get(), new PacketBuffer(Unpooled.buffer()), true);
		DimensionManager.keepLoaded(DUNGEON_DIMENSION, false);
	    }
	    else
	    {
		DUNGEON_DIMENSION = DimensionType.byName(id);
	    }
	}
    }
}