package com.catastrophe573.dimdungeons.item;

import com.catastrophe573.dimdungeons.DimDungeons;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.event.RegistryEvent;

public class ItemRegistrar
{
    public static int NUM_TROPHIES = 8;

    public static void registerAllItems(RegistryEvent.Register<Item> event)
    {
	// register trophy items
	for (int i = 1; i <= NUM_TROPHIES; i++)
	{
	    Item trophy = new Item(new Item.Properties().group(ItemGroup.MISC).maxStackSize(1));
	    trophy.setRegistryName(DimDungeons.MOD_ID, "item_trophy_" + i);
	    event.getRegistry().register(trophy);
	}

	// register basic items
	event.getRegistry().register(new ItemPortalKey());	
    }
}
