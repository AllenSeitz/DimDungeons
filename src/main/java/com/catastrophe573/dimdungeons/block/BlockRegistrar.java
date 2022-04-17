package com.catastrophe573.dimdungeons.block;

import com.catastrophe573.dimdungeons.DimDungeons;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;
import net.minecraftforge.registries.RegistryObject;

public class BlockRegistrar
{
    @ObjectHolder("dimdungeons:block_gilded_portal")
    public static Block block_gilded_portal;
    @ObjectHolder("dimdungeons:block_gold_portal")
    public static Block block_gold_portal;
    @ObjectHolder("dimdungeons:block_local_teleporter")
    public static Block block_local_teleporter;
    @ObjectHolder("dimdungeons:block_portal_keyhole")
    public static Block block_portal_keyhole;
    @ObjectHolder("dimdungeons:block_portal_crown")
    public static Block block_portal_crown;
    @ObjectHolder("dimdungeons:block_key_charger")
    public static Block block_key_charger;
    @ObjectHolder("dimdungeons:block_key_charger_used")
    public static Block block_key_charger_used;
    @ObjectHolder("dimdungeons:block_key_charger_damaged")
    public static Block block_key_charger_damaged;

    public static String REG_NAME_CHARGER_FULL = "block_key_charger";
    public static String REG_NAME_CHARGER_USED = "block_key_charger_used";
    public static String REG_NAME_CHARGER_DAMAGED = "block_key_charger_damaged";

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, DimDungeons.MOD_ID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, DimDungeons.MOD_ID);

    // these DeferredRegister objects replace the old registry events
    public static final RegistryObject<Block> BLOCK_GILDED_PORTAL = BLOCKS.register(BlockGildedPortal.REG_NAME, () -> new BlockGildedPortal());
    public static final RegistryObject<Block> BLOCK_GOLD_PORTAL = BLOCKS.register(BlockGoldPortal.REG_NAME, () -> new BlockGoldPortal());
    public static final RegistryObject<Block> BLOCK_LOCAL_TELEPORTER = BLOCKS.register(BlockLocalTeleporter.REG_NAME, () -> new BlockLocalTeleporter());
    public static final RegistryObject<Block> BLOCK_PORTAL_KEYHOLE = BLOCKS.register(BlockPortalKeyhole.REG_NAME, () -> new BlockPortalKeyhole());
    public static final RegistryObject<Block> BLOCK_PORTAL_CROWN = BLOCKS.register(BlockPortalCrown.REG_NAME, () -> new BlockPortalCrown());
    public static final RegistryObject<Block> BLOCK_CHARGER_FULL = BLOCKS.register(REG_NAME_CHARGER_FULL, () -> new BlockKeyCharger());
    public static final RegistryObject<Block> BLOCK_CHARGER_USED = BLOCKS.register(REG_NAME_CHARGER_USED, () -> new BlockKeyCharger());
    public static final RegistryObject<Block> BLOCK_CHARGER_DAMAGED = BLOCKS.register(REG_NAME_CHARGER_DAMAGED, () -> new BlockKeyCharger());

    // register BlockEntities too
    public static final RegistryObject<BlockEntityType<?>> BE_PORTAL_KEYHOLE = BLOCK_ENTITIES.register(TileEntityPortalKeyhole.REG_NAME, () -> BlockEntityType.Builder.of(TileEntityPortalKeyhole::new, block_portal_keyhole).build(null));
    public static final RegistryObject<BlockEntityType<?>> BE_GOLD_PORTAL = BLOCK_ENTITIES.register(TileEntityGoldPortal.REG_NAME, () -> BlockEntityType.Builder.of(TileEntityGoldPortal::new, block_gold_portal).build(null));
    public static final RegistryObject<BlockEntityType<?>> BE_LOCAL_TELEPORTER = BLOCK_ENTITIES.register(TileEntityLocalTeleporter.REG_NAME, () -> BlockEntityType.Builder.of(TileEntityLocalTeleporter::new, block_local_teleporter).build(null));

    public static void register()
    {
	BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
	BLOCK_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}