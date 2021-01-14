package com.catastrophe573.dimdungeons;

import java.util.List;

import com.catastrophe573.dimdungeons.utils.DungeonUtils;
import com.google.common.collect.Lists;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
    public void explosionStart(ExplosionEvent.Start event)
    {
    }

    @SubscribeEvent
    public void explosionModify(ExplosionEvent.Detonate event)
    {
	// I only care about explosions in the Dungeon Dimension
	if (!DungeonUtils.isDimensionDungeon((World) event.getWorld()))
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
	event.getExplosion().clearAffectedBlockPositions();
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
	if (!DungeonUtils.isDimensionDungeon((World) event.getWorld()))
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
	if (!DungeonUtils.isDimensionDungeon((World) event.getWorld()))
	{
	    return;
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
	if (!DungeonUtils.isDimensionDungeon((World) event.getWorld()))
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
	if (!DungeonUtils.isDimensionDungeon((World) event.getWorld()))
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
}