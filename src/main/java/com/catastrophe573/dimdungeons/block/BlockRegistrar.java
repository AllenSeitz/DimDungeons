package com.catastrophe573.dimdungeons.block;

import com.catastrophe573.dimdungeons.DimDungeons;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BlockRegistrar
{
	public static String REG_NAME_CHARGER_FULL = "block_key_charger";
	public static String REG_NAME_CHARGER_USED = "block_key_charger_used";
	public static String REG_NAME_CHARGER_DAMAGED = "block_key_charger_damaged";

	private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(DimDungeons.MOD_ID);
	private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, DimDungeons.MOD_ID);

	// these DeferredRegister objects replace the old registry events
	public static final DeferredBlock<BlockGildedPortal> BLOCK_GILDED_PORTAL = BLOCKS.register(BlockGildedPortal.REG_NAME, BlockGildedPortal::new);
	public static final DeferredBlock<BlockGoldPortal> BLOCK_GOLD_PORTAL = BLOCKS.register(BlockGoldPortal.REG_NAME, BlockGoldPortal::new);
	public static final DeferredBlock<BlockLocalTeleporter> BLOCK_LOCAL_TELEPORTER = BLOCKS.register(BlockLocalTeleporter.REG_NAME, BlockLocalTeleporter::new);
	public static final DeferredBlock<BlockPortalKeyhole> BLOCK_PORTAL_KEYHOLE = BLOCKS.register(BlockPortalKeyhole.REG_NAME, BlockPortalKeyhole::new);
	public static final DeferredBlock<BlockPortalCrown> BLOCK_PORTAL_CROWN = BLOCKS.register(BlockPortalCrown.REG_NAME, BlockPortalCrown::new);
	public static final DeferredBlock<BlockKeyCharger> BLOCK_CHARGER_FULL = BLOCKS.register(REG_NAME_CHARGER_FULL, BlockKeyCharger::new);
	public static final DeferredBlock<BlockKeyCharger> BLOCK_CHARGER_USED = BLOCKS.register(REG_NAME_CHARGER_USED, BlockKeyCharger::new);
	public static final DeferredBlock<BlockKeyCharger> BLOCK_CHARGER_DAMAGED = BLOCKS.register(REG_NAME_CHARGER_DAMAGED, BlockKeyCharger::new);

	// register BlockEntities too
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileEntityPortalKeyhole>> BE_PORTAL_KEYHOLE = BLOCK_ENTITIES.register(TileEntityPortalKeyhole.REG_NAME, () -> BlockEntityType.Builder.of(TileEntityPortalKeyhole::new, BLOCK_PORTAL_KEYHOLE.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileEntityGoldPortal>> BE_GOLD_PORTAL = BLOCK_ENTITIES.register(TileEntityGoldPortal.REG_NAME, () -> BlockEntityType.Builder.of(TileEntityGoldPortal::new, BLOCK_GOLD_PORTAL.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileEntityLocalTeleporter>> BE_LOCAL_TELEPORTER = BLOCK_ENTITIES.register(TileEntityLocalTeleporter.REG_NAME, () -> BlockEntityType.Builder.of(TileEntityLocalTeleporter::new, BLOCK_LOCAL_TELEPORTER.get()).build(null));

	public static void register(IEventBus modEventBus)
	{
		BLOCKS.register(modEventBus);
		BLOCK_ENTITIES.register(modEventBus);
	}
}