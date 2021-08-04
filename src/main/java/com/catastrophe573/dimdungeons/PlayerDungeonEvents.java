package com.catastrophe573.dimdungeons;

import java.util.List;

import com.catastrophe573.dimdungeons.utils.CommandDimDungeons;
import com.catastrophe573.dimdungeons.utils.DungeonUtils;
import com.google.common.collect.Lists;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PlayerDungeonEvents
{
    //@SubscribeEvent
    //public void pickupItem(EntityItemPickupEvent event)
    //{
    //}

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event)
    {
	CommandDimDungeons.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void explosionStart(ExplosionEvent.Start event)
    {
    }

    @SubscribeEvent
    public void explosionModify(ExplosionEvent.Detonate event)
    {
	// I only care about explosions in the Dungeon Dimension
	if (!DungeonUtils.isDimensionDungeon((Level) event.getWorld()))
	{
	    return;
	}

	// allow only cracked stone bricks to be broken
	List<BlockPos> crackedBricks = Lists.newArrayList();

	for (int i = 0; i < event.getAffectedBlocks().size(); i++)
	{
	    if (event.getWorld().getBlockState(event.getAffectedBlocks().get(i)).getBlock().getRegistryName().getPath().equals("cracked_stone_bricks"))
	    {
		crackedBricks.add(event.getAffectedBlocks().get(i));
	    }
	    if (event.getWorld().getBlockState(event.getAffectedBlocks().get(i)).getBlock().getRegistryName().getPath().equals("trapped_chest"))
	    {
		crackedBricks.add(event.getAffectedBlocks().get(i));
	    }
	    if (event.getWorld().getBlockState(event.getAffectedBlocks().get(i)).getBlock().getRegistryName().getPath().equals("tnt"))
	    {
		crackedBricks.add(event.getAffectedBlocks().get(i));
	    }
	}

	//DimDungeons.LOGGER.info("EXPLODING BRICKS: " + crackedBricks.size());
	event.getExplosion().clearToBlow();
	event.getAffectedBlocks().addAll(crackedBricks);
    }

    @SubscribeEvent
    public void blockBreak(BlockEvent.BreakEvent event)
    {
	if (!DungeonConfig.globalBlockProtection)
	{
	    return; // config disabled
	}

	// I only care about blocks breaking in the Dungeon Dimension
	if (!DungeonUtils.isDimensionDungeon((Level) event.getWorld()))
	{
	    return;
	}

	// check for a possible whitelist exception
	BlockState targetBlock = event.getWorld().getBlockState(event.getPos());
	if (DungeonConfig.blockBreakWhitelist.contains(targetBlock.getBlock()))
	{
	    //DimDungeons.LOGGER.info("dimdungeons: the WHITELIST ALLOWED to break: " + targetBlock.getBlock().getTranslatedName().getString());
	    return;
	}

	event.setCanceled(true);
    }

    @SubscribeEvent
    public void blockPlace(BlockEvent.EntityPlaceEvent event)
    {
	if (!DungeonConfig.globalBlockProtection)
	{
	    return; // config disabled
	}

	// I only care about placing blocks in the Dungeon Dimension
	if (!DungeonUtils.isDimensionDungeon((Level) event.getWorld()))
	{
	    return;
	}

	// assume this is frost walker and allow it?
	String whatBlock = event.getPlacedBlock().getBlock().getRegistryName().getPath();
	String whyBlock = event.getBlockSnapshot().getReplacedBlock().getBlock().getRegistryName().getPath();
	if ("water".equals(whatBlock) && "water".equals(whyBlock))
	{
	    return; // not sure why the block isn't frosted_ice though?
	}

	event.setCanceled(true);
    }

    @SubscribeEvent
    public void fillBucket(FillBucketEvent event)
    {
	if (!DungeonConfig.globalBlockProtection)
	{
	    return; // config disabled
	}

	// I only care about taking liquids in the Dungeon Dimension
	if (!DungeonUtils.isDimensionDungeon((Level) event.getWorld()))
	{
	    return;
	}

	event.setCanceled(true);
    }

    @SubscribeEvent
    public void rightClickBlock(RightClickBlock event)
    {
	if (!DungeonConfig.globalBlockProtection)
	{
	    return; // config disabled
	}

	// I only care about restricting access in the Dungeon Dimension
	if (!DungeonUtils.isDimensionDungeon((Level) event.getWorld()))
	{
	    return;
	}

	// now the blacklist needs to be checked
	BlockState targetBlock = event.getWorld().getBlockState(event.getPos());
	if (DungeonConfig.blockInteractBlacklist.contains(targetBlock.getBlock()))
	{
	    //DimDungeons.LOGGER.info("Entity " + event.getEntity().getName().getString() + " was BLACKLISTED from touching: " + targetBlock.getBlock().getTranslatedName().getString());
	    event.setCanceled(true);
	    return;
	}

	//DimDungeons.LOGGER.info("Entity " + event.getEntity().getName().getString() + " just interacted with: " + targetBlock.getBlock().getTranslatedName().getString());
    }

    @SubscribeEvent
    public void teleportStart(EntityTeleportEvent event)
    {
	// restrict player teleports
	if (event.getEntity() instanceof ServerPlayer)
	{
	    // I only care about restricting teleports in the Dungeon CHALLENGE Dimension
	    //	    if (DungeonUtils.isDimensionDungeon(event.getEntityLiving().getEntityWorld()))
	    //	    {
	    //		event.setCanceled(true);
	    //	    }
	}

	// restrict enderman/shulker teleports
	if (event.getEntity() instanceof EnderMan || event.getEntity() instanceof Shulker)
	{
	    // I only care about restricting teleports within my dimensions
	    if (DungeonUtils.isDimensionDungeon(event.getEntity().getCommandSenderWorld()))
	    {
		event.setCanceled(true);
	    }
	}
    }

    @SubscribeEvent
    public void useItem(LivingEntityUseItemEvent.Start event)
    {
	ItemStack stack = event.getItem();

	// do not run this function on non-players
	if (!(event.getEntityLiving() instanceof ServerPlayer))
	{
	    return;
	}

	// cancel chorus fruits in any of my dimensions
	if (DungeonUtils.isDimensionDungeon(event.getEntityLiving().getCommandSenderWorld()))
	{
	    if (stack.getItem() == Items.CHORUS_FRUIT)
	    {
		event.setDuration(-1); // stop the chorus fruit from being used
		event.setCanceled(true);
		//DimDungeons.LOGGER.info("CANCELLING CHORUS FRUIT at START!");
	    }
	}
    }
}