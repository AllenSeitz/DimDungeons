package com.catastrophe573.dimdungeons.item;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
	super(new Item.Properties().rarity(Rarity.COMMON).tab(ItemRegistrar.CREATIVE_TAB));
    }

    @Override
    public void performActivationRitual(Player player, ItemStack itemstack, Level worldIn, BlockPos pos)
    {
	worldIn.playSound((Player) null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0F, 1.0F);

	if (!worldIn.isClientSide)
	{
	    // delete this item and replace it with a regular key, but first remember which inventory slot it was in
	    int slot = player.getInventory().findSlotMatchingItem(itemstack);
	    itemstack.shrink(1);

	    // generate the activated key and try to insert it into the player's inventory multiple ways as a fail-safe
	    ItemStack newkey = new ItemStack(ItemRegistrar.item_portal_key);
	    activateKeyForNewTeleporterHub(worldIn.getServer(), newkey);

	    if (!player.getInventory().add(slot, newkey))
	    {
		if (!player.addItem(newkey))
		{
		    player.drop(newkey, false);
		}
	    }
	}

	createActivationParticleEffects(worldIn, pos);
	createActivationParticleEffectsForTeleporterKey(worldIn, pos, itemstack);
    }

    // EVEN MORE particle effects for this special event!
    public void createActivationParticleEffectsForTeleporterKey(Level worldIn, BlockPos pos, ItemStack itemstack)
    {
	Random random = worldIn.getRandom();
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
