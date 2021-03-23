package com.catastrophe573.dimdungeons;

import com.catastrophe573.dimdungeons.dimension.DungeonDimensionType;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.monster.ShulkerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PlayerDungeonEvents {
    @SubscribeEvent
    public void teleportStart(EnderTeleportEvent event)
    {
        // restrict player teleports
        if (event.getEntityLiving() instanceof ServerPlayerEntity)
        {
            // I only care about restricting teleports in the Dungeon CHALLENGE Dimension
            //	    if (DungeonUtils.isDimensionDungeon(event.getEntityLiving().getEntityWorld()))
            //	    {
            //		event.setCanceled(true);
            //	    }
        }

        // restrict enderman/shulker teleports
        if (event.getEntityLiving() instanceof EndermanEntity || event.getEntityLiving() instanceof ShulkerEntity)
        {
            // I only care about restricting teleports within my dimensions
            if (event.getEntityLiving().dimension == DungeonDimensionType.getDimensionType())
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
        if (!(event.getEntityLiving() instanceof ServerPlayerEntity))
        {
            return;
        }

        // cancel chorus fruits in any of my dimensions
        if (event.getEntityLiving().dimension == DungeonDimensionType.getDimensionType())
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
