package com.catastrophe573.dimdungeons;

import com.catastrophe573.dimdungeons.item.*;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.bus.api.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.catastrophe573.dimdungeons.block.BlockRegistrar;
import com.catastrophe573.dimdungeons.dimension.DungeonChunkGenerator;
import com.catastrophe573.dimdungeons.utils.CommandDimDungeons;
import com.catastrophe573.dimdungeons.utils.LootModifierNoDrops;

import java.util.function.Supplier;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("dimdungeons")
public class DimDungeons
{
	// reference a log4j logger
	public static final Logger LOGGER = LogManager.getLogger();

	// used in the new ValueInput and ValueOutput functions when editing entity NBT
	public static ProblemReporter.ScopedCollector PROBLEM_REPORTER = new ProblemReporter.ScopedCollector(LogUtils.getLogger());

	// constants used by other classes
	public static final String MOD_ID = "dimdungeons"; // this must match mods.toml

	public static final String dungeon_dimension_regname = "dungeon_dimension";
	public static final String build_dimension_regname = "build_dimension";

	// commonly used Identifiers for my two dimensions
	public static final ResourceKey<Level> DUNGEON_DIMENSION = ResourceKey.create(Registries.DIMENSION, Identifier.fromNamespaceAndPath(MOD_ID, dungeon_dimension_regname));
	public static final ResourceKey<Level> BUILD_DIMENSION = ResourceKey.create(Registries.DIMENSION, Identifier.fromNamespaceAndPath(MOD_ID, build_dimension_regname));

	// register my custom ChunkGenerator here instead of in a separate class
	private static final DeferredRegister<MapCodec<? extends ChunkGenerator>> CHUNK_GENERATORS = DeferredRegister.create(Registries.CHUNK_GENERATOR.identifier(), DimDungeons.MOD_ID);
	public static final DeferredHolder<MapCodec<? extends ChunkGenerator>, MapCodec<FlatLevelSource>> MY_CHUNK_GEN = CHUNK_GENERATORS.register("dimdungeons_chunkgen", () -> DungeonChunkGenerator.CODEC);

	// see PlayerDungeonEvents.java
	public static final PlayerDungeonEvents eventHandler = new PlayerDungeonEvents();

	// global loot modifiers
	public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> GLOBAL_LOOT_MODIFIER_SERIALIZERS = DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, DimDungeons.MOD_ID);
	public static final Supplier<MapCodec<LootModifierNoDrops>> NO_DUNGEON_DROPS = GLOBAL_LOOT_MODIFIER_SERIALIZERS.register("no_dungeon_drops", () -> LootModifierNoDrops.CODEC);

	// register custom data components for my classes
	public static final DeferredRegister.DataComponents DATA_COMPONENT_REGISTRAR = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, DimDungeons.MOD_ID);
	public static final Supplier<DataComponentType<DungeonKeyDataComponentRecord>> DUNGEON_KEY_DATA = DATA_COMPONENT_REGISTRAR.registerComponentType(
			"dungeon_key_data", builder -> builder.persistent(DungeonKeyDataComponent.DUNGEON_KEY_DCR_CODEC)
	);
	public static final Supplier<DataComponentType<SecretBellDataComponentRecord>> SECRET_BELL_DATA = DATA_COMPONENT_REGISTRAR.registerComponentType(
			"secret_bell_data", builder -> builder.persistent(SecretBellDataComponent.SECRET_BELL_DCR_CODEC)
	);

	public DimDungeons(IEventBus modEventBus, ModContainer container)
	{
		BlockRegistrar.register(modEventBus);
		ItemRegistrar.register(modEventBus);
		CHUNK_GENERATORS.register(modEventBus);
		GLOBAL_LOOT_MODIFIER_SERIALIZERS.register(modEventBus);
		DATA_COMPONENT_REGISTRAR.register(modEventBus);

		// register event listeners that don't use the event bus
		modEventBus.addListener(this::enqueueIMC);
		modEventBus.addListener(this::processIMC);
		modEventBus.addListener(this::modConfig);
		modEventBus.addListener(this::onRegisterItemModelProperties);

		// Register ourselves for server, registry and other game events we are interested in
		NeoForge.EVENT_BUS.register(eventHandler);
		NeoForge.EVENT_BUS.addListener(PlayerDungeonEvents::onWorldTick);

		container.registerConfig(ModConfig.Type.SERVER, DungeonConfig.SERVER_SPEC, "dimdungeons-server-r206.toml");
		container.registerConfig(ModConfig.Type.CLIENT, DungeonConfig.CLIENT_SPEC);
		container.registerConfig(ModConfig.Type.COMMON, DungeonConfig.COMMON_SPEC, "dimdungeons-common-r206.toml");
	}

	@SubscribeEvent
	public void onRegisterCommands(RegisterCommandsEvent event)
	{
		CommandDimDungeons.register(event.getDispatcher());
	}

	@SubscribeEvent
	public void onRegisterItemModelProperties(net.neoforged.neoforge.client.event.RegisterRangeSelectItemModelPropertyEvent event)
	{
		//DimDungeons.logMessageInfo("Registering custom item model properties!");
		event.register(Identifier.fromNamespaceAndPath(DimDungeons.MOD_ID, "dungeon_theme"), ModelPropertyTheme.MAP_CODEC);
		event.register(Identifier.fromNamespaceAndPath(DimDungeons.MOD_ID, "dungeon_level"), ModelPropertyLevel.MAP_CODEC);
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

		if ( event instanceof ModConfigEvent.Unloading )
		{
			return; // do not call refreshServer() after it has been unloaded (Unloading only happens for the server)
		}

		if (config.getSpec() == DungeonConfig.CLIENT_SPEC)
		{
			DungeonConfig.refreshClient();
		}
		else if (config.getSpec() == DungeonConfig.SERVER_SPEC)
		{
			DungeonConfig.refreshServer();
		}
	}

	// You can use SubscribeEvent and let the Event Bus discover methods to call
	@SubscribeEvent
	public void onServerStarting(ServerStartingEvent event)
	{
		// Do something when the server starts
		//LOGGER.info("HELLO from server starting");
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