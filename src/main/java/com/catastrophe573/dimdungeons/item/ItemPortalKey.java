package com.catastrophe573.dimdungeons.item;

import com.catastrophe573.dimdungeons.structure.DungeonDesigner.DungeonType;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemPortalKey extends BaseItemKey
{
	public static final String REG_NAME = "item_portal_key";

	public ItemPortalKey()
	{
		super(new Item.Properties());
	}

	// used in the item model json to change the graphic based on the dimdungeons:keytype property
	public static float getKeyLevelAsFloat(ItemStack stack)
	{
		if (((ItemPortalKey) stack.getItem()).isActivated(stack))
		{
			if (((ItemPortalKey) stack.getItem()).getDungeonType(stack) == DungeonType.TELEPORTER_HUB)
			{
				return 0.21f; // teleporter hub
			}
			if (((ItemPortalKey) stack.getItem()).getKeyLevel(stack) == 2)
			{
				return 0.2f; // level 2 key
			}
			return 0.1f; // level 1 key
		}
		return 0.0f; // unactivated key
	}

	public static float getKeyThemeAsFloat(ItemStack stack)
	{
		int theme = ((ItemPortalKey) stack.getItem()).getDungeonTheme(stack);
		return (float)theme / 100.0f;
	}
	
	public boolean isDungeonBuilt(ItemStack stack)
	{
		if (stack.hasTag())
		{
			if (stack.getTag().contains(NBT_BUILT))
			{
				return stack.getTag().getBoolean(NBT_BUILT);
			}
		}
		return false;
	}

	public void setDungeonBuilt(ItemStack stack)
	{
		if (stack.hasTag())
		{
			stack.getTag().putBoolean(NBT_BUILT, true);
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public Component getName(ItemStack stack)
	{
		// no NBT data on this item at all? well then return a blank key
		if (stack.hasTag())
		{
			CompoundTag itemData = stack.getTag();

			if (itemData.contains(NBT_KEY_ACTIVATED))
			{
				int nameType = itemData.contains(NBT_NAME_TYPE) ? itemData.getInt(NBT_NAME_TYPE) : 0;
				int word_index_1 = itemData.contains(NBT_NAME_PART_1) ? itemData.getInt(NBT_NAME_PART_1) : 2;
				int word_index_2 = itemData.contains(NBT_NAME_PART_2) ? itemData.getInt(NBT_NAME_PART_2) : 1;
				int theme = itemData.contains(NBT_THEME) ? itemData.getInt(NBT_THEME) : 0;
				String retval = "";

				if (nameType == 0)
				{
					// some tier 1 keys
					String start = I18n.get("npart.dimdungeons.struct_1");
					String preposition = I18n.get("npart.dimdungeons.struct_2");
					String noun1 = I18n.get("npart.dimdungeons.noun_" + word_index_1);
					String noun2 = I18n.get("npart.dimdungeons.noun_" + word_index_2);
					if (word_index_1 == word_index_2)
					{
						retval = start + " " + noun1;
					}
					else
					{
						retval = start + " " + noun1 + " " + preposition + " " + noun2;
					}
				}
				else if (nameType == 1)
				{
					// some tier 1 keys
					String start = I18n.get("npart.dimdungeons.struct_3");
					String preposition = I18n.get("npart.dimdungeons.struct_4");
					String noun1 = I18n.get("npart.dimdungeons.noun_" + word_index_1);
					String noun2 = I18n.get("npart.dimdungeons.noun_" + word_index_2);
					if (word_index_1 == word_index_2)
					{
						retval = start + " " + noun1;
					}
					else
					{
						retval = start + " " + noun1 + " " + preposition + " " + noun2;
					}
				}
				else if (nameType == 2)
				{
					// themes AND some tier 1 keys
					String start = I18n.get("npart.dimdungeons.struct_5");
					String preposition = I18n.get("npart.dimdungeons.struct_6");
					String place = I18n.get("npart.dimdungeons.place_" + word_index_1);
					if (theme > 0)
					{
						place = I18n.get("npart.dimdungeons.theme_" + theme);
					}
					String noun = I18n.get("npart.dimdungeons.noun_" + word_index_2);
					retval = start + " " + place + " " + preposition + " " + noun;
				}
				else if (nameType == 3)
				{
					// advanced keys
					String start = I18n.get("npart.dimdungeons.struct_7");
					String place = I18n.get("npart.dimdungeons.place_" + word_index_1);
					String largeness = I18n.get("npart.dimdungeons.large_" + word_index_2);
					retval = start + " " + largeness + " " + place;
				}
				else if (nameType == 4)
				{
					// teleporter hub - main key
					String start = I18n.get("npart.dimdungeons.struct_9");
					String noun1 = I18n.get("npart.dimdungeons.noun_" + word_index_1);
					retval = start + noun1;
				}
				else if (nameType == 5)
				{
					// teleporter hub - 7 other keys
					String start = I18n.get("npart.dimdungeons.struct_9_" + word_index_1);
					retval = start;
				}

				return Component.translatable(retval);
			}
		}

		// basically return "Blank Portal Key"
		// return I18n.format(stack.getTranslationKey());
		return Component.translatable(this.getDescriptionId(stack));
	}

	/**
	 * Called when the player Left Clicks (attacks) an entity. Processed before
	 * damage is done, if return value is true further processing is canceled and
	 * the entity is not attacked.
	 *
	 * @param stack  The Item being used
	 * @param player The player that is attacking
	 * @param entity The entity being attacked
	 * @return True to cancel the rest of the interaction.
	 */
	@Override
	public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity)
	{
		return false;
	}
}
