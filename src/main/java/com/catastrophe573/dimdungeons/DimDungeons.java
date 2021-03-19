package com.catastrophe573.dimdungeons;

import com.catastrophe573.dimdungeons.block.TileEntityGoldPortal;
import com.catastrophe573.dimdungeons.block.TileEntityLocalTeleporter;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
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
import com.catastrophe573.dimdungeons.dimension.DungeonChunkGenerator;
import com.catastrophe573.dimdungeons.item.ItemPortalKey;
import com.catastrophe573.dimdungeons.item.ItemRegistrar;
import com.catastrophe573.dimdungeons.item.ItemSecretBell;
import com.catastrophe573.dimdungeons.utils.CommandDimDungeons;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("dimdungeons")
public class DimDungeons
{
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();

    // constants used by other classes
    public static final String MOD_ID = "dimdungeons"; // this must match mods.toml
    public static final String RESOURCE_PREFIX = MOD_ID + ":";
    public static final String dungeon_basic_regname = "dungeon_dimension";

    public static final RegistryKey<World> DUNGEON_DIMENSION = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(MOD_ID, dungeon_basic_regname));

    public static final PlayerDungeonEvents eventHandler = new PlayerDungeonEvents();

    public DimDungeons()
    {
	// register event listeners that don't use the event bus
	FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
	FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
	FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
	FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doCommonStuff);
	FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
	FMLJavaModLoadingContext.get().getModEventBus().addListener(this::modConfig);

	// Register ourselves for server, registry and other game events we are interested in
	//MinecraftForge.EVENT_BUS.register(this);
	MinecraftForge.EVENT_BUS.register(eventHandler);
	MinecraftForge.EVENT_BUS.addListener(this::registerCommands);

	ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, DungeonConfig.SERVER_SPEC);
	ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, DungeonConfig.CLIENT_SPEC);
	ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, DungeonConfig.COMMON_SPEC);
    }

    // some preinit code	
    private void setup(final FMLCommonSetupEvent event)
    {
    }

    private void doCommonStuff(final FMLCommonSetupEvent event)
    {
    }

    private void doClientStuff(final FMLClientSetupEvent event)
    {
	RenderTypeLookup.setRenderLayer(BlockRegistrar.block_gold_portal, RenderType.getTranslucent());
	RenderTypeLookup.setRenderLayer(BlockRegistrar.block_local_teleporter, RenderType.getTranslucent());

	// register the custom property for the keys that allows for switching their model
	ItemModelsProperties.registerProperty(ItemRegistrar.item_portal_key, new ResourceLocation(DimDungeons.MOD_ID, "keytype"), (stack, world, entity) ->
	{
	    return ItemPortalKey.getKeyLevelAsFloat(stack);
	});
	ItemModelsProperties.registerProperty(ItemRegistrar.item_secret_bell, new ResourceLocation(DimDungeons.MOD_ID, "bellupgrade"), (stack, world, entity) ->
	{
	    return ItemSecretBell.getUpgradeLevelAsFloat(stack);
	});
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

    public void modConfig(ModConfig.ModConfigEvent event)
    {
	ModConfig config = event.getConfig();
	if (config.getSpec() == DungeonConfig.CLIENT_SPEC)
	{
	    DungeonConfig.refreshClient();
	}
	else if (config.getSpec() == DungeonConfig.SERVER_SPEC)
	{
	    DungeonConfig.refreshServer();
	}
    }

    private void registerCommands(RegisterCommandsEvent evt)
    {
	CommandDimDungeons.register(evt.getDispatcher());
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD event bus
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = DimDungeons.MOD_ID)
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
	    TileEntityType<TileEntityGoldPortal> tetGoldPortal = TileEntityType.Builder.create(TileEntityGoldPortal::new).build(null);
	    TileEntityType<TileEntityLocalTeleporter> tetLocalTeleporter = TileEntityType.Builder.create(TileEntityLocalTeleporter::new).build(null);
	    tetPortalKeyhole.setRegistryName(MOD_ID, TileEntityPortalKeyhole.REG_NAME);
	    tetGoldPortal.setRegistryName(MOD_ID, TileEntityGoldPortal.REG_NAME);
	    tetLocalTeleporter.setRegistryName(MOD_ID, TileEntityLocalTeleporter.REG_NAME);
	    teRegistryEvent.getRegistry().register(tetPortalKeyhole);
	    teRegistryEvent.getRegistry().register(tetGoldPortal);
	    teRegistryEvent.getRegistry().register(tetLocalTeleporter);

	    // register a chunk generator here because I can get away with it
	    Registry.register(Registry.CHUNK_GENERATOR_CODEC, "dimdungeons:dimdungeons_chunkgen", DungeonChunkGenerator.myCodec);

	}

	//@SubscribeEvent
	// this isn't called? wrong type?
	//public static void registerChunkGenerators(RegistryEvent.Register<? extends ChunkGenerator> cgRegistryEvent)
	//{
	//    System.out.println("DIMDUNGEONS TEST: REGISTERING CHUNK GENERATOR!");
	//    Registry.register(Registry.CHUNK_GENERATOR_CODEC, "dimdungeons:dimdungeons_chunkgen", DungeonChunkGenerator.myCodec);
	//}
    }
}
