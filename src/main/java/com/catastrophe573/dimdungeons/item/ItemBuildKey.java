package com.catastrophe573.dimdungeons.item;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.structure.DungeonDesigner;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public class ItemBuildKey extends BaseItemKey
{
	public static final String REG_NAME = "item_key_build";

	public static final int BLOCKS_APART_PER_PLOT = 16 * 32; // 32 chunks apart to be really sure
	public static final float PLOT_ENTRANCE_OFFSET_X = (4 * 16);
	public static final float PLOT_ENTRANCE_OFFSET_Z = (8 * 16);

	public ItemBuildKey()
	{
		super(new Item.Properties().
				setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(DimDungeons.MOD_ID, REG_NAME))).
				rarity(Rarity.COMMON));
	}

	public boolean isPlotBuilt(ItemStack stack)
	{
		if (stack.has(DimDungeons.DUNGEON_KEY_DATA))
		{
			return stack.get(DimDungeons.DUNGEON_KEY_DATA).built();
		}
		return false;
	}

	public void setPlotBuilt(ItemStack stack)
	{
		if (!stack.has(DimDungeons.DUNGEON_KEY_DATA))
		{
			DimDungeons.logMessageError("ERROR: setting isBuilt on a non-key or an object without a dungeon key data component.");
			return;
		}

		// records are immutable, so make a new record just to change one field? am I doing this right?
		// TODO: bope, I'm not doing it right. Make DungeonKeyDataComponentRecord more like the vanilla classes. It doesn't have to be immutable everywhere.
		DungeonKeyDataComponentRecord data = stack.get(DimDungeons.DUNGEON_KEY_DATA);
		DungeonKeyDataComponentRecord newData = new DungeonKeyDataComponentRecord(
				data.key_activated(),
				true, // built
				data.dest_x(),
				data.dest_z(),
				data.name_type(),
				data.name_part_1(),
				data.name_part_2(),
				data.theme(),
				data.dungeon_type()
		);

		stack.set(DimDungeons.DUNGEON_KEY_DATA, newData);
	}

	// personal build plots are further apart than regular dungeons
	@Override
	public float getWarpX(ItemStack stack)
	{
		if (stack != null && !stack.isEmpty() && stack.has(DimDungeons.DUNGEON_KEY_DATA))
		{
			long dest_x = stack.get(DimDungeons.DUNGEON_KEY_DATA).dest_x();

			return (dest_x * BLOCKS_APART_PER_PLOT) + (4 * 16) + PLOT_ENTRANCE_OFFSET_X;
		}
		return -1;
	}

	// personal build plots are further apart than regular dungeons
	@Override
	public float getWarpZ(ItemStack stack)
	{
		if (stack != null && !stack.isEmpty() && stack.has(DimDungeons.DUNGEON_KEY_DATA))
		{
			long dest_z = stack.get(DimDungeons.DUNGEON_KEY_DATA).dest_z();

			return (dest_z * BLOCKS_APART_PER_PLOT) + (4 * 16) + PLOT_ENTRANCE_OFFSET_Z;
		}
		return -1;
	}

	// personal build plots are further apart than regular dungeons
	@Override
	public long getDungeonTopLeftX(ItemStack stack)
	{
		if (stack != null && !stack.isEmpty() && stack.has(DimDungeons.DUNGEON_KEY_DATA))
		{
			long dest_x = stack.get(DimDungeons.DUNGEON_KEY_DATA).dest_x();

			return (dest_x * BLOCKS_APART_PER_PLOT);
		}
		return -1;
	}

	// personal build plots are further apart than regular dungeons
	@Override
	public long getDungeonTopLeftZ(ItemStack stack)
	{
		if (stack != null && !stack.isEmpty() && stack.has(DimDungeons.DUNGEON_KEY_DATA))
		{
			long dest_z = stack.get(DimDungeons.DUNGEON_KEY_DATA).dest_z();

			return (dest_z * BLOCKS_APART_PER_PLOT);
		}
		return -1;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public Component getName(ItemStack stack)
	{
		// when keys are upgraded from 1.20 to 1.21 or newer for the first time
		if ( hasLegacyData(stack) )
		{
			return Component.translatable("item.dimdungeons.item_legacy_key_name");
		}

		// this will likely never be seen because the key will probably be renamed
		if (this.isActivated(stack))
		{
			return Component.translatable("item.dimdungeons.item_build_key");
		}

		// basically return "Blank Personal Dimension Key"
		return Component.translatable("item.dimdungeons.item_build_key");
	}
}