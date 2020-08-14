package com.catastrophe573.dimdungeons;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.MinecraftForge;
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
import com.catastrophe573.dimdungeons.dimension.DungeonChunkGenerator;
import com.catastrophe573.dimdungeons.item.ItemPortalKey;
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
    public static final String dungeon_basic_regname = "dungeon_dimension";

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

    // some preinit code	
    private void setup(final FMLCommonSetupEvent event)
    {
	//MinecraftForge.EVENT_BUS.register(new PlayerDungeonDataEvents());

	//CapabilityManager.INSTANCE.register(IPlayerDungeonData.class, new PlayerDungeonDataStorage(), DefaultPlayerDungeonData::new);
    }

    private void doCommonStuff(final FMLCommonSetupEvent event)
    {
	//BiomeDictionary.addTypes(BiomeRegistrar.biome_dungeon, BiomeDictionary.Type.VOID);
    }

    private void doClientStuff(final FMLClientSetupEvent event)
    {
	RenderTypeLookup.setRenderLayer(BlockRegistrar.block_gold_portal, RenderType.getTranslucent());

	// register the custom property for the keys that allows for switching their model
	ItemModelsProperties.func_239418_a_(ItemRegistrar.item_portal_key, new ResourceLocation("keytype"), (stack, world, entity) ->
	{
	    return ItemPortalKey.getKeyLevelAsFloat(stack);
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
	public static void registerChunkGenerators(RegistryEvent.Register<? extends ChunkGenerator> cgRegistryEvent)
	{
	    Registry.register(Registry.field_239690_aB_, "dimdungeon_chunkgen", DungeonChunkGenerator.field_235948_a_);
	}
    }
}
