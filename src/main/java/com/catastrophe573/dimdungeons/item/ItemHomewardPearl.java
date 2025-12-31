package com.catastrophe573.dimdungeons.item;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.utils.DungeonUtils;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class ItemHomewardPearl extends Item
{
	public static final String REG_NAME = "item_homeward_pearl";

	public ItemHomewardPearl(Item.Properties builderIn)
	{
		super(builderIn.setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(DimDungeons.MOD_ID, REG_NAME))));
	}

	@Override
	// public ActionResultType onItemUse(ItemUseContext parameters)
	public @NotNull InteractionResult use(Level worldIn, Player playerIn, InteractionHand handIn)
	{
		ItemStack itemstack = playerIn.getItemInHand(handIn);

		// this item only works in the Dungeon Dimension
		if (!DungeonUtils.isDimensionDungeon((Level) playerIn.level()))
		{
			return InteractionResult.FAIL;
		}

		// do nothing on the client, let the server do the teleport
		if (playerIn.level().isClientSide())
		{
			return InteractionResult.SUCCESS_SERVER.withoutItem();
		}

		// this is the dungeon dimension
		ServerLevel serverWorld = playerIn.level().getServer().getLevel(playerIn.level().dimension());

		double newx = getHomeX(playerIn.getX());
		double newy = 55.1D;
		double newz = getHomeZ(playerIn.getZ());

		// old 1.20 logic - remove once the port is complete
		//CustomTeleporter tele = new CustomTeleporter(serverWorld);
		//tele.setDestPos(newx, newy, newz, 180.0f, 0.0f);
		//playerIn.changeDimension(serverWorld, tele); // changing within the same dimension, but still teleport safely anyways

		// 1.21 replacement logic
		//DimensionTransition dt = new DimensionTransition(serverWorld, new Vec3(newx, newy, newz), new Vec3(0, 0, 0), 180.0f, 0.0f, false, DO_NOTHING);

		// 1.21.2 replacement logic
		TeleportTransition tt = new TeleportTransition(serverWorld, new Vec3(newx, newy, newz), new Vec3(0, 0, 0), 180.0f, 0.0f, TeleportTransition.DO_NOTHING);
		playerIn.teleport(tt);

		// consume one pearl from the stack
		itemstack.shrink(1);
		playerIn.getCooldowns().addCooldown(ItemRegistrar.ITEM_HOMEWARD_PEARL.getId(), 80);

		return InteractionResult.SUCCESS_SERVER.withoutItem();
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