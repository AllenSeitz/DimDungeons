package com.catastrophe573.dimdungeons.capability;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.dimension.DungeonDimensionType;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

//@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class PlayerDungeonDataEvents
{
    public static final ResourceLocation DUNGEONDATA_CAPABILITY = new ResourceLocation(DimDungeons.MOD_ID, "dungeondata_capability");

    @SubscribeEvent
    public void onAttachCaps(AttachCapabilitiesEvent<Entity> event)
    {
	if (event.getObject() instanceof PlayerEntity)
	{
	    if (!event.getObject().getCapability(PlayerDungeonDataProvider.DUNGEONDATA_CAPABILITY).isPresent())
	    {
		event.addCapability(DUNGEONDATA_CAPABILITY, new PlayerDungeonDataProvider());
	    }
	}
    }

    @SubscribeEvent
    public void onTravelAcrossDimensions(PlayerEvent.PlayerChangedDimensionEvent event)
    {
	if (event.getPlayer() != null)
	{
	    if (event.getPlayer().getCapability(PlayerDungeonDataProvider.DUNGEONDATA_CAPABILITY).isPresent())
	    {
		IPlayerDungeonData data = event.getPlayer().getCapability(PlayerDungeonDataProvider.DUNGEONDATA_CAPABILITY).orElse(null);

		if (event.getFrom() == DimensionType.OVERWORLD && event.getTo() == DungeonDimensionType.getDimensionType())
		{
		    data.setLastOverworldPortalX((float)event.getPlayer().posX);
		    data.setLastOverworldPortalY((float)event.getPlayer().posY);
		    data.setLastOverworldPortalZ((float)event.getPlayer().posZ);
		    data.setLastOverworldPortalYaw((float)event.getPlayer().getPitchYaw().y);		    
		}
	    }
	}
    }

    @SubscribeEvent
    // this event is fired for two unrelated reasons: onPlayerDeath (and respawn). And when returning from the end to the overworld.
    public void onPlayerClone(PlayerEvent.Clone event)
    {
	if (!event.isWasDeath())
	{
	    //DimDungeons.LOGGER.info("DIMDUNGEONS: JUST CASUALLY RETURNING FROM THE END TO THE OVERWORLD");
	    return;
	}

	if (event.getOriginal().getCapability(PlayerDungeonDataProvider.DUNGEONDATA_CAPABILITY).isPresent())
	{
	    IPlayerDungeonData data = event.getPlayer().getCapability(PlayerDungeonDataProvider.DUNGEONDATA_CAPABILITY).orElse(new DefaultPlayerDungeonData());
	    IPlayerDungeonData oldData = event.getOriginal().getCapability(PlayerDungeonDataProvider.DUNGEONDATA_CAPABILITY).orElse(new DefaultPlayerDungeonData());

	    //DimDungeons.LOGGER.info("DIMDUNGEONS: RESTORING CAPABILITY DATA ON RESPAWN (" + oldData.getLastOverworldPortalY() + ")");
	    data.setLastOverworldPortalX(oldData.getLastOverworldPortalX());
	    data.setLastOverworldPortalY(oldData.getLastOverworldPortalY());
	    data.setLastOverworldPortalZ(oldData.getLastOverworldPortalZ());
	    data.setLastOverworldPortalYaw(oldData.getLastOverworldPortalYaw());
	}
    }
}