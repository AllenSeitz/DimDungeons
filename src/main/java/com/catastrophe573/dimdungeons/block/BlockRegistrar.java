package com.catastrophe573.dimdungeons.block;

import com.catastrophe573.dimdungeons.DimDungeons;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.ObjectHolder;

public class BlockRegistrar
{
    // mod id required in 1.13.0 because reasons lol?
    @ObjectHolder("dimdungeons:block_gilded_portal") public static Block block_gilded_portal;    
    @ObjectHolder("dimdungeons:block_gold_portal") public static Block block_gold_portal;    
    @ObjectHolder("dimdungeons:block_portal_keyhole") public static Block block_portal_keyhole;    
    
    public static void registerAllBlocks(RegistryEvent.Register<Block> event)
    {
	event.getRegistry().register(new BlockGildedPortal());
	event.getRegistry().register(new BlockGoldPortal());
	event.getRegistry().register(new BlockPortalKeyhole());
    }

    // this is called by the ItemRegistrar
    public static void registerAllItemBlocks(RegistryEvent.Register<Item> event)
    {
	DimDungeons.LOGGER.info("HELLO from Register ItemBlock");

	event.getRegistry().register(new ItemBlock(block_gilded_portal, new Item.Builder().group(ItemGroup.DECORATIONS)).setRegistryName(block_gilded_portal.getRegistryName()));
	event.getRegistry().register(new ItemBlock(block_gold_portal, new Item.Builder().group(ItemGroup.DECORATIONS)).setRegistryName(block_gold_portal.getRegistryName()));
	event.getRegistry().register(new ItemBlock(block_portal_keyhole, new Item.Builder().group(ItemGroup.DECORATIONS)).setRegistryName(block_portal_keyhole.getRegistryName()));
    }
}