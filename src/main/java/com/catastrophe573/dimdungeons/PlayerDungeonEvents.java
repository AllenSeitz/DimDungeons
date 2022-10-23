package com.catastrophe573.dimdungeons;

import java.util.List;

import com.catastrophe573.dimdungeons.dimension.DungeonData;
import com.catastrophe573.dimdungeons.item.ItemBuildKey;
import com.catastrophe573.dimdungeons.structure.DungeonDesigner.DungeonType;
import com.catastrophe573.dimdungeons.structure.DungeonRoom;
import com.catastrophe573.dimdungeons.utils.CommandDimDungeons;
import com.catastrophe573.dimdungeons.utils.DungeonUtils;
import com.google.common.collect.Lists;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.living.LivingDestroyBlockEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
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

    // for some reason this doesn't use SubscribeEvent and is instead registered from the main class
    public static void onWorldTick(TickEvent.WorldTickEvent event)
    {
	if (event.world.isClientSide)
	{
	    return;
	}
	if (event.phase == TickEvent.Phase.START)
	{
	    return;
	}

	// make sure the tick is for my custom dimension
	if (DungeonUtils.isDimensionDungeon(event.world))
	{
	    DungeonData.get(event.world).tick(event.world);
	}
    }

    @SubscribeEvent
    public void livingDamage(LivingHurtEvent event)
    {
	// only apply the damage multiplier server-side
	if (event.getEntity().getLevel().isClientSide())
	{
	    return;
	}

	// only apply the damage multiplier in the Dungeon Dimension
	if (!DungeonUtils.isDimensionDungeon((Level) event.getEntityLiving().getLevel()))
	{
	    return;
	}

	// only players suffer the damage multiplier
	if (event.getEntityLiving() instanceof Player)
	{
	    // are they in a basic dungeon or an advanced dungeon?
	    ChunkPos cpos = event.getEntityLiving().chunkPosition();
	    DungeonRoom room = DungeonData.get(event.getEntityLiving().getLevel()).getRoomAtPos(cpos);

	    if (room == null)
	    {
		return; // this can happen
	    }
	    if (room.dungeonType == DungeonType.BASIC || room.dungeonType == DungeonType.THEME_OPEN || room.dungeonType == DungeonType.THEME_REGULAR)
	    {
		event.setAmount((float) (event.getAmount() * DungeonConfig.basicDamageMultiplier));
	    }
	    if (room.dungeonType == DungeonType.ADVANCED)
	    {
		event.setAmount((float) (event.getAmount() * DungeonConfig.advancedDamageMultiplier));
	    }
	}
    }

    @SubscribeEvent
    public void livingUpdate(LivingEvent.LivingUpdateEvent event)
    {
	if (event.getEntity().getLevel().isClientSide())
	{
	    return;
	}

	if (event.getEntityLiving() instanceof Player)
	{
	    if (((Player) (event.getEntityLiving())).isSpectator())
	    {
		return; // ignore spectators, they don't need to be saved from the void
	    }
	}

	// handle standing on the void in the build world
	if (DungeonUtils.isDimensionPersonalBuild(event.getEntity().getLevel()))
	{
	    if (!DungeonUtils.isPersonalBuildChunk(event.getEntity().blockPosition()))
	    {
		// player is leaving the build area (probably on elytra) and needs a correction
		ChunkPos chunk = new ChunkPos(event.getEntity().blockPosition());

		int nx = (chunk.x - 4) % (ItemBuildKey.BLOCKS_APART_PER_PLOT / 16);
		int nz = (chunk.z - 4) % (ItemBuildKey.BLOCKS_APART_PER_PLOT / 16);

		if (nx < -1 || (nx > 8 && nx < 31) || nz < -1 || (nz > 8 && nz < 31))
		{
		    DungeonUtils.sendEntityHomeInBuildWorld(event.getEntity());
		    return;
		}
	    }

	    // if the player is in the buildable area or just one chunk away then only snap them back to safety if they've fallen below the map
	    if (event.getEntity().position().y < 1)
	    {
		DungeonUtils.sendEntityHomeInBuildWorld(event.getEntity());
		return;
	    }
	}
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
	// the build dimension is always block-protected outside of the build space, no matter what
	if (DungeonUtils.isDimensionPersonalBuild((Level) event.getWorld()))
	{
	    if (!DungeonUtils.isPersonalBuildChunk(event.getPos()))
	    {
		event.setCanceled(true);
		return;
	    }
	}

	// intentionally check this AFTER the build dimension
	if (!DungeonConfig.globalBlockProtection)
	{
	    return; // config disabled
	}

	// next after the build world, I only care about blocks breaking in the Dungeon Dimension
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

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void blockPlace(BlockEvent.EntityPlaceEvent event)
    {
	// the build dimension is always block-protected outside of the build space, no matter what
	if (DungeonUtils.isDimensionPersonalBuild((Level) event.getWorld()))
	{
	    if (!DungeonUtils.isPersonalBuildChunk(event.getPos()))
	    {
		event.setCanceled(true);
		return;
	    }
	}

	// intentionally check this AFTER the build dimension	
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

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void blockMultiPlace(BlockEvent.EntityMultiPlaceEvent event)
    {
	// the build dimension is always block-protected outside of the build space, no matter what
	if (DungeonUtils.isDimensionPersonalBuild((Level) event.getWorld()))
	{
	    if (!DungeonUtils.isPersonalBuildChunk(event.getPos()))
	    {
		event.setCanceled(true);
		return;
	    }
	}

	// intentionally check this AFTER the build dimension
	if (!DungeonConfig.globalBlockProtection)
	{
	    return; // config disabled
	}

	// I only care about placing blocks in the Dungeon Dimension
	if (!DungeonUtils.isDimensionDungeon((Level) event.getWorld()))
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
	if (!DungeonUtils.isDimensionDungeon((Level) event.getWorld()))
	{
	    return;
	}

	event.setCanceled(true);
    }

    @SubscribeEvent
    public void anythingDestroyBlock(LivingDestroyBlockEvent event)
    {
	// the build dimension is always block-protected outside of the build space, no matter what
	if (DungeonUtils.isDimensionPersonalBuild((Level) event.getEntity().getLevel()))
	{
	    if (!DungeonUtils.isPersonalBuildChunk(event.getPos()))
	    {
		event.setCanceled(true);
		return;
	    }
	}

	// intentionally check this AFTER the build dimension	
	if (!DungeonConfig.globalBlockProtection)
	{
	    return; // config disabled
	}

	// I only care about restricting access in the Dungeon Dimension
	if (!DungeonUtils.isDimensionDungeon((Level) event.getEntityLiving().getLevel()))
	{
	    return;
	}

	// check for a possible whitelist exception
	BlockState targetBlock = event.getEntityLiving().getLevel().getBlockState(event.getPos());
	if (DungeonConfig.blockBreakWhitelist.contains(targetBlock.getBlock()))
	{
	    return;
	}

	event.setCanceled(true);
    }

    @SubscribeEvent
    public void leftClickBlock(LeftClickBlock event)
    {
	// the build dimension is always block-protected outside of the build space, no matter what
	if (DungeonUtils.isDimensionPersonalBuild((Level) event.getWorld()))
	{
	    if (!DungeonUtils.isPersonalBuildChunk(event.getPos()))
	    {
		event.setCanceled(true);
		return;
	    }
	}

	// intentionally check this AFTER the build dimension	
	if (!DungeonConfig.globalBlockProtection)
	{
	    return; // config disabled
	}

	// I only care about restricting access in the Dungeon Dimension
	if (!DungeonUtils.isDimensionDungeon((Level) event.getEntityLiving().getLevel()))
	{
	    return;
	}

	// check for a possible whitelist exception
	BlockState targetBlock = event.getEntityLiving().getLevel().getBlockState(event.getPos());
	if (DungeonConfig.blockBreakWhitelist.contains(targetBlock.getBlock()))
	{
	    return;
	}

	event.setCanceled(true);
    }

    @SubscribeEvent
    public void rightClickBlock(RightClickBlock event)
    {
	// the build dimension is always block-protected outside of the build space, no matter what
	if (DungeonUtils.isDimensionPersonalBuild((Level) event.getWorld()))
	{
	    if (!DungeonUtils.isPersonalBuildChunk(event.getPos()))
	    {
		event.setCanceled(true);
		return;
	    }
	}

	// intentionally check this AFTER the build dimension	
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

	// one other thing, if the player's hands aren't empty, then don't let them place a block? since canceling block placement happens too late?
	ItemStack itemInHand = event.getItemStack();
	if (itemInHand != null && !itemInHand.isEmpty())
	{
	    if (itemInHand.getItem() instanceof BlockItem)
	    {
		event.setCanceled(true);
	    }
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
	    if (DungeonUtils.isDimensionDungeon(event.getEntity().getCommandSenderWorld()))
	    {
		// TODO: teleporting over the void? cancelled
		// TODO: teleporting above y= roof level? cancelled
	    }
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
    public void onChorusTeleport(EntityTeleportEvent.ChorusFruit event)
    {
	// I only care about restricting teleports within my dimensions
	if (DungeonUtils.isDimensionDungeon(event.getEntity().getCommandSenderWorld()))
	{
	    event.setCanceled(true);
	}
    }

    @SubscribeEvent
    public void useItem(LivingEntityUseItemEvent.Start event)
    {
	// do not run this function on non-players
	if (!(event.getEntityLiving() instanceof ServerPlayer))
	{
	    return;
	}
    }
}