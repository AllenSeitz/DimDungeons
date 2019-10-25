package com.catastrophe573.dimdungeons.capability;

import com.catastrophe573.dimdungeons.DimDungeons;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
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
	    DimDungeons.LOGGER.info("DIMDUNGEONS: ATTACHING CAPABILITY TO PLAYER");
	    event.addCapability(DUNGEONDATA_CAPABILITY, new PlayerDungeonDataProvider());
	}
    }

    public void onTravelAcrossDimensions(PlayerEvent.PlayerChangedDimensionEvent event)
    {
	if (event.getPlayer() != null)
	{
	    if (event.getPlayer().getCapability(PlayerDungeonDataProvider.DUNGEONDATA_CAPABILITY).isPresent())
	    {
		IPlayerDungeonData data = event.getPlayer().getCapability(PlayerDungeonDataProvider.DUNGEONDATA_CAPABILITY).orElse(new DefaultPlayerDungeonData());
		DimDungeons.LOGGER.info("DIMDUNGEONS: JUST CASUALLY CHANGING DIMENSIONS WITH SAVED DATA " + data.getLastOverworldPortalY());
	    }
	}
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event)
    {
	PlayerEntity player = event.getPlayer();

	if (!event.isWasDeath())
	{
	    DimDungeons.LOGGER.info("DIMDUNGEONS: JUST CASUALLY CHANGING DIMENSIONS NOTHING TO SEE HERE");
	}

	if (event.getOriginal().getCapability(PlayerDungeonDataProvider.DUNGEONDATA_CAPABILITY).isPresent())
	{
	    IPlayerDungeonData data = player.getCapability(PlayerDungeonDataProvider.DUNGEONDATA_CAPABILITY).orElse(new DefaultPlayerDungeonData());
	    IPlayerDungeonData oldData = event.getOriginal().getCapability(PlayerDungeonDataProvider.DUNGEONDATA_CAPABILITY).orElse(new DefaultPlayerDungeonData());

	    DimDungeons.LOGGER.info("DIMDUNGEONS: RESTORING CAPABILITY DATA ON RESPAWN (" + oldData.getLastOverworldPortalY() + ")");
	    data.setLastOverworldPortalX(oldData.getLastOverworldPortalX());
	    data.setLastOverworldPortalY(oldData.getLastOverworldPortalY());
	    data.setLastOverworldPortalZ(oldData.getLastOverworldPortalZ());
	    data.setLastOverworldPortalYaw(oldData.getLastOverworldPortalYaw());
	}
	else
	{
	    DimDungeons.LOGGER.info("DIMDUNGEONS: PLAYER DIED WITH NO CAPABILITY");
	}
    }
}