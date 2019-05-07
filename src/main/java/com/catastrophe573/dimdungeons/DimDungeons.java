package com.catastrophe573.dimdungeons;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.catastrophe573.dimdungeons.block.BlockRegistrar;
import com.catastrophe573.dimdungeons.block.TileEntityPortalKeyhole;
import com.catastrophe573.dimdungeons.dimension.DimensionRegistrar;
import com.catastrophe573.dimdungeons.item.ItemRegistrar;

import java.util.stream.Collectors;

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
	// Register the setup method for modloading
	FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
	// Register the enqueueIMC method for modloading
	FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
	// Register the processIMC method for modloading
	FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
	// Register the doClientStuff method for modloading
	FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

	// Register ourselves for server, registry and other game events we are interested in
	MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
	// some preinit code
    }

    private void doClientStuff(final FMLClientSetupEvent event)
    {
	// do something that can only be done on the client
	LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
	// some example code to dispatch IMC to another mod
	InterModComms.sendTo("forge", "helloworld", () ->
	{
	    LOGGER.info("Hello world");
	    return "Hello world";
	});
    }

    private void processIMC(final InterModProcessEvent event)
    {
	// some example code to receive and process InterModComms from other mods
	LOGGER.info("Got IMC", event.getIMCStream().map(m -> m.getMessageSupplier().get()).collect(Collectors.toList()));
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public static void onServerStarting(FMLServerStartingEvent event)
    {
	// do something when the server starts
	LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD event bus
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents
    {
	@SubscribeEvent
	public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent)
	{
	    LOGGER.info("HELLO from Register Block");
	    BlockRegistrar.registerAllBlocks(blockRegistryEvent);
	}

	@SubscribeEvent
	public static void onItemsRegistry(final RegistryEvent.Register<Item> itemRegistryEvent)
	{
	    LOGGER.info("HELLO from Register Item");
	    ItemRegistrar.registerAllItems(itemRegistryEvent);
	    BlockRegistrar.registerAllItemBlocks(itemRegistryEvent);
	}

	@SubscribeEvent
	public static void registerTE(RegistryEvent.Register<TileEntityType<?>> teRegistryEvent)
	{
	    LOGGER.info("HELLO from Register TileEntityType");
	    TileEntityType<TileEntityPortalKeyhole> tetPortalKeyhole = TileEntityType.Builder.create(TileEntityPortalKeyhole::new).build(null);
	    tetPortalKeyhole.setRegistryName(MOD_ID, TileEntityPortalKeyhole.REG_NAME);
	    teRegistryEvent.getRegistry().register(tetPortalKeyhole);
	    
	    // HACK: we're just going to register dungeons here because there's no better time to do it until 1.14
	    DimensionRegistrar.registerDimensions();
	}
    }
}
