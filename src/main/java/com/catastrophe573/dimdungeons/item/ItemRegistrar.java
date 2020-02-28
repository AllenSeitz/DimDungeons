package com.catastrophe573.dimdungeons.item;

import com.catastrophe573.dimdungeons.DimDungeons;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.ObjectHolder;

public class ItemRegistrar
{
    public static int NUM_TROPHIES = 8;

    @ObjectHolder("dimdungeons:" + ItemPortalKey.REG_NAME)
    public static Item item_portal_key;

    @ObjectHolder("dimdungeons:" + ItemGuidebook.REG_NAME)
    public static Item item_guidebook;
        
    public static final ItemGroup CREATIVE_TAB = new ItemGroup(DimDungeons.MOD_ID)
    {
	@Override
	public ItemStack createIcon()
	{
	    return new ItemStack(item_portal_key);
	}
    };

    public static void registerAllItems(RegistryEvent.Register<Item> event)
    {
	// register trophy items
	for (int i = 1; i <= NUM_TROPHIES; i++)
	{
	    Item trophy = new Item(new Item.Properties().maxStackSize(1));
	    trophy.setRegistryName(DimDungeons.MOD_ID, "item_trophy_" + i);
	    event.getRegistry().register(trophy);
	}

	// register basic items
	event.getRegistry().register(new ItemPortalKey());
	event.getRegistry().register(new ItemGuidebook());
    }
}
