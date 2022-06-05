package com.catastrophe573.dimdungeons.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;


public class ItemBlankTeleporterKey extends BaseItemKey
{
    public static final String REG_NAME = "item_blank_teleporter_key";

    public ItemBlankTeleporterKey()
    {
	super(new Item.Properties().rarity(Rarity.COMMON));
    }
}
