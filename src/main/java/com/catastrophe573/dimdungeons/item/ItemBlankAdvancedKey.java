package com.catastrophe573.dimdungeons.item;

import com.catastrophe573.dimdungeons.DimDungeons;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

public class ItemBlankAdvancedKey extends BaseItemKey
{
	public static final String REG_NAME = "item_blank_advanced_key";

	public ItemBlankAdvancedKey()
	{
		super(new Item.Properties().rarity(Rarity.UNCOMMON));
	}

	@Override
	public void performActivationRitual(Player player, ItemStack itemstack, Level worldIn, BlockPos pos)
	{
		worldIn.playSound((Player) null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0F, 1.0F);

		if (player == null)
		{
			DimDungeons.logMessageError("Somehow activated a blank advanced key without a player present. Do not do this.");
			return;
		}

		if (!worldIn.isClientSide)
		{
			// delete the advanced blank key and replace it with a regular blank key, but
			// first remember which inventory slot it was in
			int slot = player.getInventory().findSlotMatchingItem(itemstack);
			itemstack.shrink(1);

			// generate the blank key and try to insert it into the player's inventory
			// multiple ways as a fail-safe
			ItemStack newkey = new ItemStack(ItemRegistrar.item_portal_key);
			activateKeyLevel2(worldIn.getServer(), newkey);

			if (!player.getInventory().add(slot, newkey))
			{
				if (!player.addItem(newkey))
				{
					player.drop(newkey, false);
				}
			}
		}

		createActivationParticleEffects(worldIn, pos);
	}
}
