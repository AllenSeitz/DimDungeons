package com.catastrophe573.dimdungeons.item;

import com.catastrophe573.dimdungeons.dimension.CustomTeleporter;
import com.catastrophe573.dimdungeons.utils.DungeonUtils;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

public class ItemHomewardPearl extends Item
{
    public static final String REG_NAME = "item_homeward_pearl";

    public ItemHomewardPearl(Item.Properties builderIn)
    {
	super(builderIn);
    }

    @SuppressWarnings("resource")
    @Override
    //public ActionResultType onItemUse(ItemUseContext parameters)
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn)
    {
	ItemStack itemstack = playerIn.getItemInHand(handIn);

	// this item only works in the Dungeon Dimension
	if (!DungeonUtils.isDimensionDungeon((Level) playerIn.getCommandSenderWorld()))
	{
	    return new InteractionResultHolder<>(InteractionResult.FAIL, itemstack);
	}

	// do nothing on the client, let the server do the teleport
	if (playerIn.getCommandSenderWorld().isClientSide)
	{
	    return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemstack);
	}

	// this is the dungeon dimension
	ServerLevel serverWorld = playerIn.getCommandSenderWorld().getServer().getLevel(playerIn.getCommandSenderWorld().dimension());

	// teleport the player
	double newx = getHomeX(playerIn.getX());
	double newy = 55.1D;
	double newz = getHomeZ(playerIn.getZ());
	CustomTeleporter tele = new CustomTeleporter(serverWorld);
	tele.setDestPos(newx, newy, newz, 180.0f, 0.0f);
	playerIn.changeDimension(serverWorld, tele); // changing within the same dimension, but still teleport safely anyways

	// consume one pearl from the stack
	itemstack.shrink(1);
	playerIn.getCooldowns().addCooldown(this, 80);

	return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemstack);
    }

    public double getHomeX(double currentX)
    {
	// figure out what the x/z of this key would be
	double topLeftX = Math.floor(currentX / ItemPortalKey.BLOCKS_APART_PER_DUNGEON);

	return topLeftX * ItemPortalKey.BLOCKS_APART_PER_DUNGEON + ItemPortalKey.ENTRANCE_OFFSET_X;
    }

    public double getHomeZ(double currentZ)
    {
	// figure out what the x/z of this key would be
	double topLeftZ = Math.floor(currentZ / ItemPortalKey.BLOCKS_APART_PER_DUNGEON);

	return topLeftZ * ItemPortalKey.BLOCKS_APART_PER_DUNGEON + ItemPortalKey.ENTRANCE_OFFSET_Z;
    }
}
