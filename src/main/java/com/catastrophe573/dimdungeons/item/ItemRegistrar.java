package com.catastrophe573.dimdungeons.item;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.block.BlockGildedPortal;
import com.catastrophe573.dimdungeons.block.BlockGoldPortal;
import com.catastrophe573.dimdungeons.block.BlockLocalTeleporter;
import com.catastrophe573.dimdungeons.block.BlockPortalCrown;
import com.catastrophe573.dimdungeons.block.BlockPortalKeyhole;
import com.catastrophe573.dimdungeons.block.BlockRegistrar;
import com.catastrophe573.dimdungeons.utils.DungeonUtils;

import net.minecraft.world.item.Item;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemRegistrar
{
	public static int NUM_TROPHIES = 8;

	// DeferredRegister objects replace the old registry events
	private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, DimDungeons.MOD_ID);

	public static final RegistryObject<Item> ITEM_PORTAL_KEY = ITEMS.register(ItemPortalKey.REG_NAME, () -> new ItemPortalKey());
	public static final RegistryObject<Item> ITEM_BLANK_ADVANCED_KEY = ITEMS.register(ItemBlankAdvancedKey.REG_NAME, () -> new ItemBlankAdvancedKey());
	public static final RegistryObject<Item> ITEM_BLANK_THEME_KEY = ITEMS.register(ItemBlankThemeKey.REG_NAME, () -> new ItemBlankThemeKey());
	public static final RegistryObject<Item> ITEM_BLANK_BUILD_KEY = ITEMS.register(ItemBlankBuildKey.REG_NAME, () -> new ItemBlankBuildKey());
	public static final RegistryObject<Item> ITEM_BUILD_KEY = ITEMS.register(ItemBuildKey.REG_NAME, () -> new ItemBuildKey());
	public static final RegistryObject<Item> ITEM_BLANK_TELEPORTER_KEY = ITEMS.register(ItemBlankTeleporterKey.REG_NAME, () -> new ItemBlankTeleporterKey());

	public static final RegistryObject<Item> ITEM_SECRET_BELL = ITEMS.register(ItemSecretBell.REG_NAME, () -> new ItemSecretBell(new Item.Properties().stacksTo(1)));
	public static final RegistryObject<Item> ITEM_HOMEWARD_PEARL = ITEMS.register(ItemHomewardPearl.REG_NAME, () -> new ItemHomewardPearl(new Item.Properties().stacksTo(16)));

	public static final RegistryObject<Item> ITEM_TROPHY_1 = ITEMS.register("item_trophy_1", () -> new Item(new Item.Properties().stacksTo(1)));
	public static final RegistryObject<Item> ITEM_TROPHY_2 = ITEMS.register("item_trophy_2", () -> new Item(new Item.Properties().stacksTo(1)));
	public static final RegistryObject<Item> ITEM_TROPHY_3 = ITEMS.register("item_trophy_3", () -> new Item(new Item.Properties().stacksTo(1)));
	public static final RegistryObject<Item> ITEM_TROPHY_4 = ITEMS.register("item_trophy_4", () -> new Item(new Item.Properties().stacksTo(1)));
	public static final RegistryObject<Item> ITEM_TROPHY_5 = ITEMS.register("item_trophy_5", () -> new Item(new Item.Properties().stacksTo(1)));
	public static final RegistryObject<Item> ITEM_TROPHY_6 = ITEMS.register("item_trophy_6", () -> new Item(new Item.Properties().stacksTo(1)));
	public static final RegistryObject<Item> ITEM_TROPHY_7 = ITEMS.register("item_trophy_7", () -> new Item(new Item.Properties().stacksTo(1)));
	public static final RegistryObject<Item> ITEM_TROPHY_8 = ITEMS.register("item_trophy_8", () -> new Item(new Item.Properties().stacksTo(1)));

	// more DeferredRegister objects for the BlockItems
	public static final RegistryObject<Item> ITEM_GILDED_PORTAL = ItemRegistrar.ITEMS.register(BlockGildedPortal.REG_NAME, () -> new BlockItem(BlockRegistrar.BLOCK_GILDED_PORTAL.get(), new Item.Properties()));
	public static final RegistryObject<Item> ITEM_GOLD_PORTAL = ItemRegistrar.ITEMS.register(BlockGoldPortal.REG_NAME, () -> new BlockItem(BlockRegistrar.BLOCK_GOLD_PORTAL.get(), new Item.Properties()));
	public static final RegistryObject<Item> ITEM_LOCAL_TELEPORTER = ItemRegistrar.ITEMS.register(BlockLocalTeleporter.REG_NAME, () -> new BlockItem(BlockRegistrar.BLOCK_LOCAL_TELEPORTER.get(), new Item.Properties()));
	public static final RegistryObject<Item> ITEM_PORTAL_KEYHOLE = ItemRegistrar.ITEMS.register(BlockPortalKeyhole.REG_NAME, () -> new BlockItem(BlockRegistrar.BLOCK_PORTAL_KEYHOLE.get(), new Item.Properties()));
	public static final RegistryObject<Item> ITEM_PORTAL_CROWN = ItemRegistrar.ITEMS.register(BlockPortalCrown.REG_NAME, () -> new BlockItem(BlockRegistrar.BLOCK_PORTAL_CROWN.get(), new Item.Properties()));
	public static final RegistryObject<Item> ITEM_CHARGER_FULL = ItemRegistrar.ITEMS.register(BlockRegistrar.REG_NAME_CHARGER_FULL, () -> new BlockItem(BlockRegistrar.BLOCK_CHARGER_FULL.get(), new Item.Properties()));
	public static final RegistryObject<Item> ITEM_CHARGER_USED = ItemRegistrar.ITEMS.register(BlockRegistrar.REG_NAME_CHARGER_USED, () -> new BlockItem(BlockRegistrar.BLOCK_CHARGER_USED.get(), new Item.Properties()));
	public static final RegistryObject<Item> ITEM_CHARGER_DAMAGED = ItemRegistrar.ITEMS.register(BlockRegistrar.REG_NAME_CHARGER_DAMAGED, () -> new BlockItem(BlockRegistrar.BLOCK_CHARGER_DAMAGED.get(), new Item.Properties()));

	public static void register()
	{
		ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
	public static void registerCreativeTab(CreativeModeTabEvent.Register event)
	{
		event.registerCreativeModeTab(new ResourceLocation(DimDungeons.MOD_ID, "creative_tab"), builder -> builder.icon(() -> DungeonUtils.getExampleKey()).displayItems((featureFlags, output, hasOp) ->
		{
			output.accept(ItemRegistrar.ITEM_GILDED_PORTAL.get());
			output.accept(ItemRegistrar.ITEM_PORTAL_KEYHOLE.get());
			output.accept(ItemRegistrar.ITEM_PORTAL_CROWN.get());
			output.accept(ItemRegistrar.ITEM_CHARGER_FULL.get());

			output.accept(ItemRegistrar.ITEM_PORTAL_KEY.get());
			output.accept(ItemRegistrar.ITEM_BLANK_ADVANCED_KEY.get());
			output.accept(ItemRegistrar.ITEM_BLANK_BUILD_KEY.get());
			output.accept(ItemRegistrar.ITEM_BLANK_TELEPORTER_KEY.get());

			output.accept(ItemRegistrar.ITEM_SECRET_BELL.get());
			output.accept(ItemRegistrar.ITEM_HOMEWARD_PEARL.get());
		}).title(Component.translatable("itemGroup.dimdungeons")));
	}	
}
