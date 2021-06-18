package com.catastrophe573.dimdungeons.item;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.dimension.CustomTeleporter;
import com.catastrophe573.dimdungeons.utils.DungeonUtils;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ItemHomewardPearl extends Item
{
    public static final String REG_NAME = "item_homeward_pearl";

    public ItemHomewardPearl(Item.Properties builderIn)
    {
	super(builderIn);
	this.setRegistryName(DimDungeons.MOD_ID, REG_NAME);
    }

    @Override
    //public ActionResultType onItemUse(ItemUseContext parameters)
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn)
    {
	ItemStack itemstack = playerIn.getItemInHand(handIn);

	// this item only works in the Dungeon Dimension
	if (!DungeonUtils.isDimensionDungeon((World) playerIn.getCommandSenderWorld()))
	{
	    return new ActionResult<>(ActionResultType.FAIL, itemstack);
	}

	// do nothing on the client, let the server do the teleport
	if (playerIn.getCommandSenderWorld().isClientSide)
	{
	    return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
	}

	// this is the dungeon dimension
	ServerWorld serverWorld = playerIn.getCommandSenderWorld().getServer().getLevel(playerIn.getCommandSenderWorld().dimension());

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

	return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
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
