package com.catastrophe573.dimdungeons.item;

import com.catastrophe573.dimdungeons.DimDungeons;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.ObjectHolder;

public class ItemRegistrar
{
    public static int NUM_TROPHIES = 8;

    @ObjectHolder("dimdungeons:" + ItemPortalKey.REG_NAME)
    public static Item item_portal_key;

    // this item is now unused because I've switched to Patchouli
    //@ObjectHolder("dimdungeons:" + ItemGuidebook.REG_NAME)
    //public static Item item_guidebook;

    @ObjectHolder("dimdungeons:" + ItemSecretBell.REG_NAME)
    public static Item item_secret_bell;

    public static final CreativeModeTab CREATIVE_TAB = new CreativeModeTab(DimDungeons.MOD_ID) {
	@Override
	public ItemStack makeIcon()
	{
	    return new ItemStack(item_portal_key);
	}
    };

    public static void registerAllItems(RegistryEvent.Register<Item> event)
    {
	// register trophy items
	for (int i = 1; i <= NUM_TROPHIES; i++)
	{
	    Item trophy = new Item(new Item.Properties().stacksTo(1));
	    trophy.setRegistryName(DimDungeons.MOD_ID, "item_trophy_" + i);
	    event.getRegistry().register(trophy);
	}

	// register basic items
	event.getRegistry().register(new ItemPortalKey());
	event.getRegistry().register(new ItemSecretBell(new Item.Properties().tab(CREATIVE_TAB).stacksTo(1)));
	event.getRegistry().register(new ItemHomewardPearl(new Item.Properties().tab(CREATIVE_TAB).stacksTo(16)));

	// this item is now unused because I've switched to Patchouli
	//event.getRegistry().register(new ItemGuidebook());
    }
}
