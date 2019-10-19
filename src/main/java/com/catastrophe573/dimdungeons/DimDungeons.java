package com.catastrophe573.dimdungeons;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ModDimension;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.catastrophe573.dimdungeons.biome.BiomeRegistrar;
import com.catastrophe573.dimdungeons.block.BlockRegistrar;
import com.catastrophe573.dimdungeons.block.TileEntityPortalKeyhole;
import com.catastrophe573.dimdungeons.dimension.DimensionRegistrar;
import com.catastrophe573.dimdungeons.item.ItemRegistrar;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("dimdungeons")
public class DimDungeons
{
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();

    // constants used by other classes
    public static final String MOD_ID = "dimdungeons"; // this must match mods.toml
    public static final String RESOURCE_PREFIX = MOD_ID + ":";
    
    public DimDungeons()
    {
	// register event listeners that don't use the event bus
	FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
	FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
	FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
	FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doCommonStuff);
	FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
	
	// Register ourselves for server, registry and other game events we are interested in
	MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
	// some preinit code
    }

    private void doCommonStuff(final FMLCommonSetupEvent event)
    {
	DimensionRegistrar.registerDimensions();
    }
    
    private void doClientStuff(final FMLClientSetupEvent event)
    {
	// do something that can only be done on the client
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
	// some example code to dispatch IMC to another mod
    }

    private void processIMC(final InterModProcessEvent event)
    {
	// some example code to receive and process InterModComms from other mods
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public static void onServerStarting(FMLServerStartingEvent event)
    {
	// do something when the server starts
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD event bus
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents
    {
	@SubscribeEvent
	public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent)
	{
	    BlockRegistrar.registerAllBlocks(blockRegistryEvent);
	}

	@SubscribeEvent
	public static void onItemsRegistry(final RegistryEvent.Register<Item> itemRegistryEvent)
	{
	    ItemRegistrar.registerAllItems(itemRegistryEvent);
	    BlockRegistrar.registerAllItemBlocks(itemRegistryEvent);
	}

	@SubscribeEvent
	public static void registerTE(RegistryEvent.Register<TileEntityType<?>> teRegistryEvent)
	{
	    TileEntityType<TileEntityPortalKeyhole> tetPortalKeyhole = TileEntityType.Builder.create(TileEntityPortalKeyhole::new).build(null);
	    tetPortalKeyhole.setRegistryName(MOD_ID, TileEntityPortalKeyhole.REG_NAME);
	    teRegistryEvent.getRegistry().register(tetPortalKeyhole);
	}
	
	@SubscribeEvent
	public static void registerBiomes(RegistryEvent.Register<Biome> biomeRegistryEvent)
	{
	    BiomeRegistrar.registerAllBiomes(biomeRegistryEvent);
	}	
	
	@SubscribeEvent
	public static void registerDims(RegistryEvent.Register<ModDimension> dimRegistryEvent)
	{
	    // DimensionRegistrar is also listening for this event, and does not appreciate being told twice
	    // TODO: hey wait why don't I make the other registrars work this way too?
	}
    }
}
