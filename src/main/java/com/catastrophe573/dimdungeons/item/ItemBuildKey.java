package com.catastrophe573.dimdungeons.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class ItemBuildKey extends BaseItemKey
{
	public static final String REG_NAME = "item_key_build";

	public static final int BLOCKS_APART_PER_PLOT = 16 * 32; // 32 chunks apart to be really sure
	public static final float PLOT_ENTRANCE_OFFSET_X = (4 * 16);
	public static final float PLOT_ENTRANCE_OFFSET_Z = (8 * 16);

	public ItemBuildKey()
	{
		super(new Item.Properties().rarity(Rarity.COMMON));
	}

	public boolean isPlotBuilt(ItemStack stack)
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

	public void setPlotBuilt(ItemStack stack)
	{
		if (stack.hasTag())
		{
			stack.getTag().putBoolean(NBT_BUILT, true);
		}
	}

	// personal build plots are further apart than regular dungeons
	@Override
	public float getWarpX(ItemStack stack)
	{
		if (stack != null && !stack.isEmpty())
		{
			CompoundTag itemData = stack.getTag();
			if (itemData != null && itemData.contains(NBT_KEY_DESTINATION_X))
			{
				return (itemData.getInt(NBT_KEY_DESTINATION_X) * BLOCKS_APART_PER_PLOT) + (4 * 16) + PLOT_ENTRANCE_OFFSET_X;
			}
		}
		return -1;
	}

	// personal build plots are further apart than regular dungeons
	@Override
	public float getWarpZ(ItemStack stack)
	{
		if (stack != null && !stack.isEmpty())
		{
			CompoundTag itemData = stack.getTag();
			if (itemData != null && itemData.contains(NBT_KEY_DESTINATION_Z))
			{
				float z = (itemData.getInt(NBT_KEY_DESTINATION_Z) * BLOCKS_APART_PER_PLOT) + (4 * 16) + PLOT_ENTRANCE_OFFSET_Z;
				return z;
			}
		}
		return -1;
	}

	// personal build plots are further apart than regular dungeons
	@Override
	public long getDungeonTopLeftX(ItemStack stack)
	{
		if (stack != null && !stack.isEmpty())
		{
			CompoundTag itemData = stack.getTag();
			if (itemData != null && itemData.contains(NBT_KEY_DESTINATION_X))
			{
				return (itemData.getInt(NBT_KEY_DESTINATION_X) * BLOCKS_APART_PER_PLOT);
			}
		}
		return -1;
	}

	// personal build plots are further apart than regular dungeons
	@Override
	public long getDungeonTopLeftZ(ItemStack stack)
	{
		if (stack != null && !stack.isEmpty())
		{
			CompoundTag itemData = stack.getTag();
			if (itemData != null && itemData.contains(NBT_KEY_DESTINATION_Z))
			{
				return (itemData.getInt(NBT_KEY_DESTINATION_Z) * BLOCKS_APART_PER_PLOT);
			}
		}
		return -1;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public Component getName(ItemStack stack)
	{
		// this will likely never be seen because the key will probably be renamed
		if (this.isActivated(stack))
		{
			return Component.translatable("item.dimdungeons.item_build_key");
		}

		// basically return "Blank Personal Dimension Key"
		return Component.translatable("item.dimdungeons.item_blank_build_key");
	}
}
