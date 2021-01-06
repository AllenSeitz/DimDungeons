package com.catastrophe573.dimdungeons;

import com.catastrophe573.dimdungeons.utils.DungeonUtils;

import net.minecraft.block.BlockState;
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