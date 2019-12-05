package com.catastrophe573.dimdungeons.block;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.ObjectHolder;

public class BlockRegistrar
{
    // mod id required in 1.13.0 because reasons lol?
    @ObjectHolder("dimdungeons:block_gilded_portal") public static Block block_gilded_portal;    
    @ObjectHolder("dimdungeons:block_gold_portal") public static Block block_gold_portal;    
    @ObjectHolder("dimdungeons:block_portal_keyhole") public static Block block_portal_keyhole;    
    @ObjectHolder("dimdungeons:block_portal_crown") public static Block block_portal_crown;    
    
    public static void registerAllBlocks(RegistryEvent.Register<Block> event)
    {
	event.getRegistry().register(new BlockGildedPortal());
	event.getRegistry().register(new BlockGoldPortal());
	event.getRegistry().register(new BlockPortalKeyhole());
	event.getRegistry().register(new BlockPortalCrown());
    }

    // this is called by the ItemRegistrar
    public static void registerAllItemBlocks(RegistryEvent.Register<Item> event)
    {
	event.getRegistry().register(new BlockItem(block_gilded_portal, new Item.Properties().group(ItemGroup.DECORATIONS)).setRegistryName(block_gilded_portal.getRegistryName()));
	event.getRegistry().register(new BlockItem(block_gold_portal, new Item.Properties()).setRegistryName(block_gold_portal.getRegistryName()));
	event.getRegistry().register(new BlockItem(block_portal_keyhole, new Item.Properties().group(ItemGroup.DECORATIONS)).setRegistryName(block_portal_keyhole.getRegistryName()));
	event.getRegistry().register(new BlockItem(block_portal_crown, new Item.Properties().group(ItemGroup.DECORATIONS)).setRegistryName(block_portal_crown.getRegistryName()));
    }
}