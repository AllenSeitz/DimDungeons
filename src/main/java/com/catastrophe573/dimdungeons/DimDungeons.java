package com.catastrophe573.dimdungeons;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.bus.api.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.catastrophe573.dimdungeons.block.BlockRegistrar;
import com.catastrophe573.dimdungeons.dimension.DungeonChunkGenerator;
import com.catastrophe573.dimdungeons.item.ItemBlankThemeKey;
import com.catastrophe573.dimdungeons.item.ItemPortalKey;
import com.catastrophe573.dimdungeons.item.ItemRegistrar;
import com.catastrophe573.dimdungeons.item.ItemSecretBell;
import com.catastrophe573.dimdungeons.utils.CommandDimDungeons;
import com.catastrophe573.dimdungeons.utils.LootModifierNoDrops;
import com.mojang.serialization.Codec;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("dimdungeons")
public class DimDungeons
{
	// reference a log4j logger
	private static final Logger LOGGER = LogManager.getLogger();

	// constants used by other classes
	public static final String MOD_ID = "dimdungeons"; // this must match mods.toml
	public static final String RESOURCE_PREFIX = MOD_ID + ":";

	public static final String dungeon_dimension_regname = "dungeon_dimension";
	public static final String build_dimension_regname = "build_dimension";

	// commonly used ResourceLocations for my two dimensions
	public static final ResourceKey<Level> DUNGEON_DIMENSION = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(MOD_ID, dungeon_dimension_regname));
	public static final ResourceKey<Level> BUILD_DIMENSION = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(MOD_ID, build_dimension_regname));

	// register my custom ChunkGenerator here instead of in a separate class
	private static final DeferredRegister<Codec<? extends ChunkGenerator>> CHUNK_GENERATORS = DeferredRegister.create(Registries.CHUNK_GENERATOR, DimDungeons.MOD_ID);
	public static final DeferredHolder<Codec<? extends ChunkGenerator>, Codec<FlatLevelSource>> MY_CHUNK_GEN = CHUNK_GENERATORS.register("dimdungeons_chunkgen", () -> DungeonChunkGenerator.CODEC);

	// see PlayerDungeonEvents.java
	public static final PlayerDungeonEvents eventHandler = new PlayerDungeonEvents();

	// global loot modifiers
	public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> GLM_REGISTRAR = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MOD_ID);
	public static final RegistryObject<Codec<LootModifierNoDrops>> NO_DUNGEON_DROPS = GLM_REGISTRAR.register("no_dungeon_drops", LootModifierNoDrops.CODEC);

	public DimDungeons()
	{
		BlockRegistrar.register();
		ItemRegistrar.register();
		CHUNK_GENERATORS.register(FMLJavaModLoadingContext.get().getModEventBus());
		GLM_REGISTRAR.register(FMLJavaModLoadingContext.get().getModEventBus());

		// register event listeners that don't use the event bus
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doCommonStuff);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::modConfig);

		// Register ourselves for server, registry and other game events we are interested in
		NeoForge.EVENT_BUS.register(eventHandler);
		NeoForge.EVENT_BUS.addListener(PlayerDungeonEvents::onWorldTick);

		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, DungeonConfig.SERVER_SPEC);
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, DungeonConfig.CLIENT_SPEC);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, DungeonConfig.COMMON_SPEC, "dimdungeons-common-r177.toml");
	}

	private void doCommonStuff(final FMLCommonSetupEvent event)
	{
		// Registries.register(Registries.CHUNK_GENERATOR, "dimdungeons:dimdungeons_chunkgen", DungeonChunkGenerator.CODEC);
	}

	@SuppressWarnings("deprecation")
	private void doClientStuff(final FMLClientSetupEvent event)
	{
		// this needs enqueueWork() because of the DeferredRegister
		event.enqueueWork(() ->
		{
			// TODO: set the render type in the model json instead of doing it here
			ItemBlockRenderTypes.setRenderLayer(BlockRegistrar.BLOCK_GOLD_PORTAL.get(), RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(BlockRegistrar.BLOCK_LOCAL_TELEPORTER.get(), RenderType.translucent());

			// register the custom property for the keys that allows for switching their model
			ItemProperties.register(ItemRegistrar.ITEM_PORTAL_KEY.get(), new ResourceLocation(DimDungeons.MOD_ID, "keytype"), (stack, world, entity, number) ->
			{
				return ItemPortalKey.getKeyLevelAsFloat(stack);
			});
			ItemProperties.register(ItemRegistrar.ITEM_PORTAL_KEY.get(), new ResourceLocation(DimDungeons.MOD_ID, "keytheme"), (stack, world, entity, number) ->
			{
				return ItemPortalKey.getKeyThemeAsFloat(stack);
			});
			ItemProperties.register(ItemRegistrar.ITEM_BLANK_THEME_KEY.get(), new ResourceLocation(DimDungeons.MOD_ID, "keytheme"), (stack, world, entity, number) ->
			{
				return ItemBlankThemeKey.getKeyThemeAsFloat(stack);
			});
			ItemProperties.register(ItemRegistrar.ITEM_SECRET_BELL.get(), new ResourceLocation(DimDungeons.MOD_ID, "bellupgrade"), (stack, world, entity, number) ->
			{
				return ItemSecretBell.getUpgradeLevelAsFloat(stack);
			});
			ItemProperties.register(ItemRegistrar.ITEM_SECRET_BELL.get(), new ResourceLocation(DimDungeons.MOD_ID, "bellupgrade"), (stack, world, entity, number) ->
			{
				return ItemSecretBell.getUpgradeLevelAsFloat(stack);
			});
		});
	}

	@SubscribeEvent
	public void onRegisterCommands(RegisterCommandsEvent event)
	{
		CommandDimDungeons.register(event.getDispatcher());
	}

	private void enqueueIMC(final InterModEnqueueEvent event)
	{
		// some example code to dispatch IMC to another mod
	}

	private void processIMC(final InterModProcessEvent event)
	{
		// some example code to receive and process InterModComms from other mods
	}

	public void modConfig(ModConfigEvent event)
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

	public static void logMessageInfo(String message)
	{
		if (DungeonConfig.logLevel.equalsIgnoreCase("all") || DungeonConfig.logLevel.equalsIgnoreCase("info"))
		{
			DimDungeons.LOGGER.info(message);
		}
	}

	public static void logMessageWarn(String message)
	{
		if (DungeonConfig.logLevel.equalsIgnoreCase("all") || DungeonConfig.logLevel.equalsIgnoreCase("info") || DungeonConfig.logLevel.equalsIgnoreCase("warn"))
		{
			DimDungeons.LOGGER.warn(message);
		}
	}

	public static void logMessageError(String message)
	{
		DimDungeons.LOGGER.error(message);
	}
}
