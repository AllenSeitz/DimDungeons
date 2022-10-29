package com.catastrophe573.dimdungeons.item;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.DungeonConfig;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemBlankThemeKey extends BaseItemKey
{
	public static final String REG_NAME = "item_blank_theme_key";

	public ItemBlankThemeKey()
	{
		super(new Item.Properties().rarity(Rarity.COMMON));
	}

	public static int getTheme(ItemStack stack)
	{
		CompoundTag itemData = stack.getTag();
		if (itemData == null)
		{
			return 0;
		}
		return itemData.contains(NBT_THEME) ? itemData.getInt(NBT_THEME) : 0;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public Component getName(ItemStack stack)
	{
		int theme = 0;

		// no NBT data on this item at all? well then return a blank key
		if (stack.hasTag())
		{
			theme = getTheme(stack);

			String start = I18n.get("item.dimdungeons.item_blank_theme_key");
			String place = I18n.get("npart.dimdungeons.theme_" + theme);

			return Component.translatable(start + " (" + place + ")");
		}

		// basically return "Blank Theme Key"
		return Component.translatable(this.getDescriptionId(stack), new Object[0]);
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
			// delete this item and replace it with a regular blank key, but first remember
			// which inventory slot it was in
			int slot = player.getInventory().findSlotMatchingItem(itemstack);
			int theme = ItemBlankThemeKey.getTheme(itemstack);
			if (theme < 1)
			{
				// by design, pick a random theme if the NBT isn't set
				theme = worldIn.getRandom().nextInt(DungeonConfig.themeSettings.size()) + 1;
			}
			itemstack.shrink(1);

			// generate the blank key and try to insert it into the player's inventory
			// multiple ways as a fail-safe
			ItemStack newkey = new ItemStack(ItemRegistrar.ITEM_PORTAL_KEY.get());
			activateKeyLevel1(worldIn.getServer(), newkey, theme);

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
