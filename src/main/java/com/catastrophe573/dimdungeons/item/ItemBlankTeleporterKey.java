package com.catastrophe573.dimdungeons.item;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.structure.DungeonDesigner;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

public class ItemBlankTeleporterKey extends BaseItemKey
{
	public static final String REG_NAME = "item_blank_teleporter_key";

	public ItemBlankTeleporterKey()
	{
		super(new Item.Properties().
				setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(DimDungeons.MOD_ID, REG_NAME))).
				rarity(Rarity.COMMON).stacksTo(1).component(DimDungeons.DUNGEON_KEY_DATA.get(), new DungeonKeyDataComponentRecord(false, false, -1, -1, 0, 0, 0, 0, String.valueOf(DungeonDesigner.DungeonType.valueOf(String.valueOf(DungeonDesigner.DungeonType.BASIC))))));
	}

	@Override
	public void performActivationRitual(Player player, ItemStack itemstack, Level worldIn, BlockPos pos)
	{
		worldIn.playSound((Player) null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0F, 1.0F);

		// in 1.21 do these first, because stack.shrink(1) causes the itemstack to empty (why wasn't this broken in previous versions?)
		createActivationParticleEffects(worldIn, pos);
		createActivationParticleEffectsForTeleporterKey(worldIn, pos, itemstack);

		if (!worldIn.isClientSide)
		{
			// delete this item and replace it with a regular key, but first remember which
			// inventory slot it was in
			int slot = player.getInventory().findSlotMatchingItem(itemstack);
			itemstack.shrink(1);

			// generate the activated key and try to insert it into the player's inventory
			// multiple ways as a fail-safe
			ItemStack newkey = new ItemStack(ItemRegistrar.ITEM_PORTAL_KEY.get());
			activateKeyForNewTeleporterHub(worldIn.getServer(), newkey);

			if (!player.getInventory().add(slot, newkey))
			{
				if (!player.addItem(newkey))
				{
					player.drop(newkey, false);
				}
			}
		}
	}

	// EVEN MORE particle effects for this special event!
	public void createActivationParticleEffectsForTeleporterKey(Level worldIn, BlockPos pos, ItemStack itemstack)
	{
		RandomSource random = worldIn.getRandom();
		for (int i = 0; i < 24; i++)
		{
			double d0 = (double) ((float) pos.getX() + 0.5F);
			double d1 = (double) ((float) pos.getY() + 0.8F);
			double d2 = (double) ((float) pos.getZ() + 0.5F);
			double xspeed = (random.nextFloat() * 0.08) * (random.nextBoolean() ? 1 : -1);
			double yspeed = random.nextFloat() * 0.45;
			double zspeed = (random.nextFloat() * 0.08) * (random.nextBoolean() ? 1 : -1);
			worldIn.addParticle(new ItemParticleOption(ParticleTypes.ITEM, itemstack), d0, d1, d2, xspeed, yspeed, zspeed);
		}
	}
}