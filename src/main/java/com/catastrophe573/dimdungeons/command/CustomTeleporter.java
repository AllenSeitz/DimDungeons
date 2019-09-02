package com.catastrophe573.dimdungeons.command;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SPlayEntityEffectPacket;
import net.minecraft.network.play.server.SPlaySoundEventPacket;
import net.minecraft.network.play.server.SPlayerAbilitiesPacket;
import net.minecraft.network.play.server.SRespawnPacket;
import net.minecraft.network.play.server.SServerDifficultyPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldInfo;

public class CustomTeleporter
{
    // basically, I want ServerPlayerEntity.changeDimension() but without the hardcoded special cases for placing obsidian blocks
    // in fact this code was copied from ServerPlayerEntity.changeDimension()
    // there were three places where I could have used reflection to achieve a perfect result, but I decided not to
    // 1. entity.invulnerableDimensionChange is not set by this function
    // 2. advancements and triggers will not happen
    // 3. lastFoodLevel is not updated (but any potential desync on the player's food bar will be minor, and will fix itself soon anyway)
    //
    public static Entity teleportEntityToDimension(ServerPlayerEntity entity, DimensionType destination, boolean respectMovementFactor, double x, double y, double z)
    {
	if (!net.minecraftforge.common.ForgeHooks.onTravelToDimension(entity, destination))
	{
	    return null;
	}
	//entity.invulnerableDimensionChange = true; // private case #1: not having access to this member variable
	DimensionType dimensiontype = entity.dimension;

	ServerWorld originWorld = entity.server.getWorld(dimensiontype);
	entity.dimension = destination;
	ServerWorld destinationWorld = entity.server.getWorld(destination);
	WorldInfo worldinfo = entity.world.getWorldInfo();
	entity.connection.sendPacket(new SRespawnPacket(destination, worldinfo.getGenerator(), entity.interactionManager.getGameType()));
	entity.connection.sendPacket(new SServerDifficultyPacket(worldinfo.getDifficulty(), worldinfo.isDifficultyLocked()));
	PlayerList playerlist = entity.server.getPlayerList();
	playerlist.updatePermissionLevel(entity);
	originWorld.removeEntity(entity, true); //Forge: the player entity is moved to the new world, NOT cloned. So keep the data alive with no matching invalidate call.
	entity.revive();
	double d0 = x; //entity.posX;
	double d1 = y; //entity.posY;
	double d2 = z; //entity.posZ;
	float f = entity.rotationPitch;
	float f1 = entity.rotationYaw;
	if (respectMovementFactor)
	{
	    double moveFactor = originWorld.getDimension().getMovementFactor() / destinationWorld.getDimension().getMovementFactor();
	    d0 *= moveFactor;
	    d2 *= moveFactor;
	}

	entity.setLocationAndAngles(d0, d1, d2, f1, f);
	double d7 = Math.min(-2.9999872E7D, destinationWorld.getWorldBorder().minX() + 16.0D);
	double d4 = Math.min(-2.9999872E7D, destinationWorld.getWorldBorder().minZ() + 16.0D);
	double d5 = Math.min(2.9999872E7D, destinationWorld.getWorldBorder().maxX() - 16.0D);
	double d6 = Math.min(2.9999872E7D, destinationWorld.getWorldBorder().maxZ() - 16.0D);
	d0 = MathHelper.clamp(d0, d7, d5);
	d2 = MathHelper.clamp(d2, d4, d6);
	entity.setLocationAndAngles(d0, d1, d2, f1, f);

	entity.setWorld(destinationWorld);
	destinationWorld.func_217447_b(entity);
	//entity.func_213846_b(serverworld); // private case #2: seems to be an advancement trigger thing
	entity.connection.setPlayerLocation(entity.posX, entity.posY, entity.posZ, f1, f);
	entity.interactionManager.setWorld(destinationWorld);
	entity.connection.sendPacket(new SPlayerAbilitiesPacket(entity.abilities));
	playerlist.sendWorldInfo(entity, destinationWorld);
	playerlist.sendInventory(entity);

	for (EffectInstance effectinstance : entity.getActivePotionEffects())
	{
	    entity.connection.sendPacket(new SPlayEntityEffectPacket(entity.getEntityId(), effectinstance));
	}

	entity.connection.sendPacket(new SPlaySoundEventPacket(1032, BlockPos.ZERO, 0, false));
	entity.giveExperiencePoints(0); // resets some internal state
	entity.setPlayerHealthUpdated();
	//entity.lastFoodLevel = -1; // private case #3: oh well it'll update soon anyway
	net.minecraftforge.fml.hooks.BasicEventHooks.firePlayerChangedDimensionEvent(entity, dimensiontype, destination);
	return entity;
    }
}
