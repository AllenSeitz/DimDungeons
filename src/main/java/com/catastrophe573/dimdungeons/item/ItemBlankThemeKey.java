package com.catastrophe573.dimdungeons.item;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.DungeonConfig;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import org.jetbrains.annotations.NotNull;

public class ItemBlankThemeKey extends BaseItemKey
{
	public static final String REG_NAME = "item_blank_theme_key";

	public ItemBlankThemeKey()
	{
		super(new Item.Properties().
				setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(DimDungeons.MOD_ID, REG_NAME))).
				rarity(Rarity.COMMON));
	}

	public static int getTheme(ItemStack stack)
	{
		if ( !stack.has(DimDungeons.DUNGEON_KEY_DATA) )
		{
			return 0;
		}

		DungeonKeyDataComponentRecord itemData = stack.get(DimDungeons.DUNGEON_KEY_DATA);
		if (itemData != null)
		{
			return itemData.theme();
		}
		return 0;
	}

	public static float getKeyThemeAsFloat(ItemStack stack)
	{
		int theme = getTheme(stack);
		return (float)theme / 100.0f;
	}

	@Override
	public @NotNull Component getName(ItemStack stack)
	{
		int theme = 0;

		if ( hasLegacyData(stack) )
		{
			return Component.translatable("item.dimdungeons.item_legacy_key_name");
		}

		// no NBT data on this item at all? well then return a blank key
		if ( !stack.has(DimDungeons.DUNGEON_KEY_DATA) )
		{
			return Component.translatable(this.getDescriptionId(), new Object[0]);
		}

		theme = getTheme(stack);

		String start = I18n.get("item.dimdungeons.item_blank_theme_key");
		String place = I18n.get("npart.dimdungeons.theme_" + theme);

		return Component.translatable(start + " (" + place + ")");
	}

	@Override
	public ItemStack performActivationRitual(Player player, ItemStack itemstack, Level worldIn, BlockPos pos)
	{
		worldIn.playSound((Player) null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0F, 1.0F);

		if (player == null)
		{
			DimDungeons.logMessageError("Somehow activated a blank theme key without a player present. Do not do this.");
			return null;
		}

		if (!worldIn.isClientSide())
		{
			int theme = ItemBlankThemeKey.getTheme(itemstack);
			if (theme < 1)
			{
				// by design, pick a random theme if the NBT isn't set
				theme = worldIn.getRandom().nextInt(DungeonConfig.themeSettings.size()) + 1;
			}

			// generate the blank key and try to insert it into the player's inventory multiple ways as a fail-safe
			ItemStack newkey = new ItemStack(ItemRegistrar.ITEM_PORTAL_KEY.get());
			activateKeyLevel1(worldIn.getServer(), newkey, theme);
			return newkey;
		}

		createActivationParticleEffects(worldIn, pos);
		return null;
	}
}