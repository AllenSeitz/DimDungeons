package com.catastrophe573.dimdungeons.item;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.block.BlockGildedPortal;
import com.catastrophe573.dimdungeons.block.BlockGoldPortal;
import com.catastrophe573.dimdungeons.block.BlockLocalTeleporter;
import com.catastrophe573.dimdungeons.block.BlockPortalCrown;
import com.catastrophe573.dimdungeons.block.BlockPortalKeyhole;
import com.catastrophe573.dimdungeons.block.BlockRegistrar;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;
import net.minecraftforge.registries.RegistryObject;

public class ItemRegistrar
{
    public static int NUM_TROPHIES = 8;

    @ObjectHolder("dimdungeons:" + ItemPortalKey.REG_NAME)
    public static Item item_portal_key;

    @ObjectHolder("dimdungeons:" + ItemBlankAdvancedKey.REG_NAME)
    public static Item item_blank_advanced_key;

    @ObjectHolder("dimdungeons:" + ItemBlankThemeKey.REG_NAME)
    public static Item item_blank_theme_key;

    // this item is now unused because I've switched to Patchouli
    //@ObjectHolder("dimdungeons:" + ItemGuidebook.REG_NAME)
    //public static Item item_guidebook;

    @ObjectHolder("dimdungeons:" + ItemSecretBell.REG_NAME)
    public static Item item_secret_bell;

    public static final CreativeModeTab CREATIVE_TAB = new CreativeModeTab(DimDungeons.MOD_ID)
    {
	@Override
	public ItemStack makeIcon()
	{
	    return new ItemStack(item_portal_key);
	}
    };

    // DeferredRegister objects replace the old registry events
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, DimDungeons.MOD_ID);

    public static final RegistryObject<Item> ITEM_PORTAL_KEY = ITEMS.register(ItemPortalKey.REG_NAME, () -> new ItemPortalKey());
    public static final RegistryObject<Item> ITEM_BLANK_ADVANCED_KEY = ITEMS.register(ItemBlankAdvancedKey.REG_NAME, () -> new ItemBlankAdvancedKey());
    public static final RegistryObject<Item> ITEM_BLANK_THEME_KEY = ITEMS.register(ItemBlankThemeKey.REG_NAME, () -> new ItemBlankThemeKey());
    public static final RegistryObject<Item> ITEM_SECRET_BELL = ITEMS.register(ItemSecretBell.REG_NAME, () -> new ItemSecretBell(new Item.Properties().tab(CREATIVE_TAB).stacksTo(1)));
    public static final RegistryObject<Item> ITEM_HOMEWARD_PEARL = ITEMS.register(ItemHomewardPearl.REG_NAME, () -> new ItemHomewardPearl(new Item.Properties().tab(CREATIVE_TAB).stacksTo(16)));
    public static final RegistryObject<Item> ITEM_TROPHY_1 = ITEMS.register("item_trophy_1", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ITEM_TROPHY_2 = ITEMS.register("item_trophy_2", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ITEM_TROPHY_3 = ITEMS.register("item_trophy_3", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ITEM_TROPHY_4 = ITEMS.register("item_trophy_4", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ITEM_TROPHY_5 = ITEMS.register("item_trophy_5", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ITEM_TROPHY_6 = ITEMS.register("item_trophy_6", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ITEM_TROPHY_7 = ITEMS.register("item_trophy_7", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ITEM_TROPHY_8 = ITEMS.register("item_trophy_8", () -> new Item(new Item.Properties().stacksTo(1)));

    // more DeferredRegister objects for the BlockItems
    public static final RegistryObject<Item> ITEM_GILDED_PORTAL = ItemRegistrar.ITEMS.register(BlockGildedPortal.REG_NAME, () -> new BlockItem(BlockRegistrar.block_gilded_portal, new Item.Properties().tab(ItemRegistrar.CREATIVE_TAB)));
    public static final RegistryObject<Item> ITEM_GOLD_PORTAL = ItemRegistrar.ITEMS.register(BlockGoldPortal.REG_NAME, () -> new BlockItem(BlockRegistrar.block_gold_portal, new Item.Properties()));
    public static final RegistryObject<Item> ITEM_LOCAL_TELEPORTER = ItemRegistrar.ITEMS.register(BlockLocalTeleporter.REG_NAME, () -> new BlockItem(BlockRegistrar.block_local_teleporter, new Item.Properties()));
    public static final RegistryObject<Item> ITEM_PORTAL_KEYHOLE = ItemRegistrar.ITEMS.register(BlockPortalKeyhole.REG_NAME, () -> new BlockItem(BlockRegistrar.block_portal_keyhole, new Item.Properties().tab(ItemRegistrar.CREATIVE_TAB)));
    public static final RegistryObject<Item> ITEM_PORTAL_CROWN = ItemRegistrar.ITEMS.register(BlockPortalCrown.REG_NAME, () -> new BlockItem(BlockRegistrar.block_portal_crown, new Item.Properties().tab(ItemRegistrar.CREATIVE_TAB)));
    public static final RegistryObject<Item> ITEM_CHARGER_FULL = ItemRegistrar.ITEMS.register(BlockRegistrar.REG_NAME_CHARGER_FULL, () -> new BlockItem(BlockRegistrar.block_key_charger, new Item.Properties().tab(ItemRegistrar.CREATIVE_TAB)));
    public static final RegistryObject<Item> ITEM_CHARGER_USED = ItemRegistrar.ITEMS.register(BlockRegistrar.REG_NAME_CHARGER_USED, () -> new BlockItem(BlockRegistrar.block_key_charger_used, new Item.Properties()));
    public static final RegistryObject<Item> ITEM_CHARGER_DAMAGED = ItemRegistrar.ITEMS.register(BlockRegistrar.REG_NAME_CHARGER_DAMAGED, () -> new BlockItem(BlockRegistrar.block_key_charger_damaged, new Item.Properties()));

    public static void register()
    {
	ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
