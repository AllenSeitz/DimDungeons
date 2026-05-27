package com.catastrophe573.dimdungeons.block;

import com.catastrophe573.dimdungeons.DimDungeons;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BlockRegistrar
{
	public static String REG_NAME_CHARGER_FULL = "block_key_charger";
	public static String REG_NAME_CHARGER_USED = "block_key_charger_used";
	public static String REG_NAME_CHARGER_DAMAGED = "block_key_charger_damaged";

	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(DimDungeons.MOD_ID);
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, DimDungeons.MOD_ID);

	// these DeferredRegister objects replace the old registry events
	public static final DeferredBlock<Block> BLOCK_GILDED_PORTAL = BLOCKS.register(BlockGildedPortal.REG_NAME, BlockGildedPortal::new);
	public static final DeferredBlock<Block> BLOCK_GOLD_PORTAL = BLOCKS.register(BlockGoldPortal.REG_NAME, BlockGoldPortal::new);
	public static final DeferredBlock<Block> BLOCK_LOCAL_TELEPORTER = BLOCKS.register(BlockLocalTeleporter.REG_NAME, BlockLocalTeleporter::new);
	public static final DeferredBlock<Block> BLOCK_PORTAL_KEYHOLE = BLOCKS.register(BlockPortalKeyhole.REG_NAME, BlockPortalKeyhole::new);
	public static final DeferredBlock<Block> BLOCK_PORTAL_CROWN = BLOCKS.register(BlockPortalCrown.REG_NAME, BlockPortalCrown::new);
	public static final DeferredBlock<Block> BLOCK_CHARGER_FULL = BLOCKS.register(REG_NAME_CHARGER_FULL,
																					registryName -> new BlockKeyCharger(BlockBehaviour.Properties.of().
																					setId(ResourceKey.create(Registries.BLOCK, registryName)).
																					mapColor(MapColor.STONE).
																					instrument(NoteBlockInstrument.BIT).
																					strength(3).
																					sound(SoundType.METAL)));
	public static final DeferredBlock<Block> BLOCK_CHARGER_USED = BLOCKS.register(REG_NAME_CHARGER_USED,
																				  registryName -> new BlockKeyCharger(BlockBehaviour.Properties.of().
																						  setId(ResourceKey.create(Registries.BLOCK, registryName)).
																						  mapColor(MapColor.STONE).
																						  instrument(NoteBlockInstrument.BIT).
																						  strength(3).
																						  sound(SoundType.METAL)));
	public static final DeferredBlock<Block> BLOCK_CHARGER_DAMAGED = BLOCKS.register(REG_NAME_CHARGER_DAMAGED,
																				  registryName -> new BlockKeyCharger(BlockBehaviour.Properties.of().
																						  setId(ResourceKey.create(Registries.BLOCK, registryName)).
																						  mapColor(MapColor.STONE).
																						  instrument(NoteBlockInstrument.BIT).
																						  strength(3).
																						  sound(SoundType.METAL)));
	// register BlockEntities too
	public static final Supplier<BlockEntityType<TileEntityPortalKeyhole>> BE_PORTAL_KEYHOLE = BLOCK_ENTITIES.register(
			TileEntityPortalKeyhole.REG_NAME, () -> new BlockEntityType<>(TileEntityPortalKeyhole::new, BLOCK_PORTAL_KEYHOLE.get())
	);

	public static final Supplier<BlockEntityType<TileEntityGoldPortal>> BE_GOLD_PORTAL = BLOCK_ENTITIES.register(
			TileEntityGoldPortal.REG_NAME, () -> new BlockEntityType<>(TileEntityGoldPortal::new, BLOCK_GOLD_PORTAL.get())
	);

	public static final Supplier<BlockEntityType<TileEntityLocalTeleporter>> BE_LOCAL_TELEPORTER = BLOCK_ENTITIES.register(
			TileEntityLocalTeleporter.REG_NAME, () -> new BlockEntityType<>(TileEntityLocalTeleporter::new, BLOCK_LOCAL_TELEPORTER.get())
	);

	public static void register(IEventBus modEventBus)
	{
		BLOCKS.register(modEventBus);
		BLOCK_ENTITIES.register(modEventBus);
	}
}