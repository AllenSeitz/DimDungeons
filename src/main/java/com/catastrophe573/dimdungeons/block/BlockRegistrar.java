package com.catastrophe573.dimdungeons.block;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.item.ItemRegistrar;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.ObjectHolder;

public class BlockRegistrar
{
    @ObjectHolder("dimdungeons:block_gilded_portal") public static Block block_gilded_portal;    
    @ObjectHolder("dimdungeons:block_gold_portal") public static Block block_gold_portal;    
    @ObjectHolder("dimdungeons:block_local_teleporter") public static Block block_local_teleporter;    
    @ObjectHolder("dimdungeons:block_portal_keyhole") public static Block block_portal_keyhole;    
    @ObjectHolder("dimdungeons:block_portal_crown") public static Block block_portal_crown;    
    @ObjectHolder("dimdungeons:block_key_charger") public static Block block_key_charger;    
    @ObjectHolder("dimdungeons:block_key_charger_used") public static Block block_key_charger_used;    
    @ObjectHolder("dimdungeons:block_key_charger_damaged") public static Block block_key_charger_damaged;    

    public static String REG_NAME_CHARGER_FULL = "block_key_charger";
    public static String REG_NAME_CHARGER_USED = "block_key_charger_used";
    public static String REG_NAME_CHARGER_DAMAGED = "block_key_charger_damaged";
    
    public static void registerAllBlocks(RegistryEvent.Register<Block> event)
    {
	event.getRegistry().register(new BlockGildedPortal());
	event.getRegistry().register(new BlockGoldPortal());
	event.getRegistry().register(new BlockLocalTeleporter());
	event.getRegistry().register(new BlockPortalKeyhole());
	event.getRegistry().register(new BlockPortalCrown());

	event.getRegistry().register(new BlockKeyCharger().setRegistryName(DimDungeons.MOD_ID, REG_NAME_CHARGER_FULL));
	event.getRegistry().register(new BlockKeyCharger().setRegistryName(DimDungeons.MOD_ID, REG_NAME_CHARGER_USED));
	event.getRegistry().register(new BlockKeyCharger().setRegistryName(DimDungeons.MOD_ID, REG_NAME_CHARGER_DAMAGED));
    }

    // this is called by the ItemRegistrar
    public static void registerAllItemBlocks(RegistryEvent.Register<Item> event)
    {
	event.getRegistry().register(new BlockItem(block_gilded_portal, new Item.Properties().tab(ItemRegistrar.CREATIVE_TAB)).setRegistryName(block_gilded_portal.getRegistryName()));
	event.getRegistry().register(new BlockItem(block_gold_portal, new Item.Properties()).setRegistryName(block_gold_portal.getRegistryName()));
	event.getRegistry().register(new BlockItem(block_local_teleporter, new Item.Properties()).setRegistryName(block_local_teleporter.getRegistryName()));
	event.getRegistry().register(new BlockItem(block_portal_keyhole, new Item.Properties().tab(ItemRegistrar.CREATIVE_TAB)).setRegistryName(block_portal_keyhole.getRegistryName()));
	event.getRegistry().register(new BlockItem(block_portal_crown, new Item.Properties().tab(ItemRegistrar.CREATIVE_TAB)).setRegistryName(block_portal_crown.getRegistryName()));
	event.getRegistry().register(new BlockItem(block_key_charger, new Item.Properties().tab(ItemRegistrar.CREATIVE_TAB)).setRegistryName(block_key_charger.getRegistryName()));
	event.getRegistry().register(new BlockItem(block_key_charger_used, new Item.Properties()).setRegistryName(block_key_charger_used.getRegistryName()));
	event.getRegistry().register(new BlockItem(block_key_charger_damaged, new Item.Properties()).setRegistryName(block_key_charger_damaged.getRegistryName()));
    }
}