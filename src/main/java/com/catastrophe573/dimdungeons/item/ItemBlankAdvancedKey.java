package com.catastrophe573.dimdungeons.item;

import com.catastrophe573.dimdungeons.DimDungeons;

import com.catastrophe573.dimdungeons.structure.DungeonDesigner;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
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
		super(new Item.Properties().
				setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(DimDungeons.MOD_ID, REG_NAME))).
				rarity(Rarity.UNCOMMON).stacksTo(1).component(DimDungeons.DUNGEON_KEY_DATA.get(), new DungeonKeyDataComponentRecord(false, false, -1, -1, 0, 0, 0, 0, String.valueOf(DungeonDesigner.DungeonType.valueOf(String.valueOf(DungeonDesigner.DungeonType.BASIC))))));
	}

	@Override
	public ItemStack performActivationRitual(Player player, ItemStack itemstack, Level worldIn, BlockPos pos)
	{
		worldIn.playSound((Player) null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0F, 1.0F);

		if (player == null)
		{
			DimDungeons.logMessageError("Somehow activated a blank advanced key without a player present. Do not do this.");
			return null;
		}

		if (!worldIn.isClientSide())
		{
			// generate the blank key and try to insert it into the player's inventory multiple ways as a fail-safe
			ItemStack newkey = new ItemStack(ItemRegistrar.ITEM_PORTAL_KEY.get());
			activateKeyLevel2(worldIn.getServer(), newkey);
			return newkey;
		}

		createActivationParticleEffects(worldIn, pos);
		return null;
	}
}