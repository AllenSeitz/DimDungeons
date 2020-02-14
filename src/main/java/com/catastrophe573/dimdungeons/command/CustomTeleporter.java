package com.catastrophe573.dimdungeons.command;

import com.catastrophe573.dimdungeons.DimDungeons;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SPlayEntityEffectPacket;
import net.minecraft.network.play.server.SPlaySoundEventPacket;
import net.minecraft.network.play.server.SPlayerAbilitiesPacket;
import net.minecraft.network.play.server.SRespawnPacket;
import net.minecraft.network.play.server.SServerDifficultyPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper.UnableToAccessFieldException;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper.UnableToFindFieldException;

public class CustomTeleporter
{
    // basically, I want ServerPlayerEntity.changeDimension() but without the hardcoded special cases for placing obsidian blocks
    // in fact this code was copied from ServerPlayerEntity.changeDimension()
    // there were three places where I could have used reflection to achieve a perfect result, but I decided not to
    // 1. entity.invulnerableDimensionChange is not set by this function
    // 2. advancements and triggers will not happen
    // 3. lastFoodLevel is not updated (but any potential desync on the player's food bar will be minor, and will fix itself soon anyway)
    //
    public static void teleportEntityToDimension(ServerPlayerEntity entity, DimensionType destination, boolean respectMovementFactor, double x, double y, double z, float pitch, float yaw)
    {
	if (!net.minecraftforge.common.ForgeHooks.onTravelToDimension(entity, destination))
	{
	    return;
	}

	// gross hack to compensate for this variable being private
	//entity.invulnerableDimensionChange = true; // private case #1: not having access to this member variable
	try
	{
	    //ObfuscationReflectionHelper.setPrivateValue(ServerPlayerEntity.class, entity, true, "invulnerableDimensionChange");
	    ObfuscationReflectionHelper.setPrivateValue(ServerPlayerEntity.class, entity, true, "field_184851_cj");
	    //Field hack = ServerPlayerEntity.class.getDeclaredField("invulnerableDimensionChange");
	    //hack.setAccessible(true);
	    //hack.set(entity, true);
	}
	catch (SecurityException | UnableToFindFieldException | UnableToAccessFieldException e)
	{
	    DimDungeons.LOGGER.info("DIMDUNGEONS ERROR: UNABLE TO SET field invulnerableDimensionChange on Player. Teleporting is about to go wrong!");
	}

	DimensionType dimensiontype = entity.dimension;

	MinecraftServer minecraftserver = entity.getServer();

	ServerWorld originWorld = minecraftserver.getWorld(dimensiontype);
	ServerWorld destinationWorld = minecraftserver.getWorld(destination);
	entity.dimension = destination;
	WorldInfo worldinfo = entity.world.getWorldInfo();
	entity.connection.sendPacket(new SRespawnPacket(destination, worldinfo.getSeed(), worldinfo.getGenerator(), entity.interactionManager.getGameType())); // 1.14 -> 1.15 change, this function now requires the seed? okay sure?
	entity.connection.sendPacket(new SServerDifficultyPacket(worldinfo.getDifficulty(), worldinfo.isDifficultyLocked()));
	PlayerList playerlist = entity.server.getPlayerList();
	playerlist.updatePermissionLevel(entity);
	originWorld.removeEntity(entity, true); //Forge: the player entity is moved to the new world, NOT cloned. So keep the data alive with no matching invalidate call.
	entity.revive();
	double d0 = x; //entity.posX;
	double d1 = y; //entity.posY;
	double d2 = z; //entity.posZ;
	float f = pitch;
	float f1 = yaw;
	if (respectMovementFactor)
	{
	    double moveFactor = originWorld.getDimension().getMovementFactor() / destinationWorld.getDimension().getMovementFactor();
	    d0 *= moveFactor;
	    d2 *= moveFactor;
	}

	// clamp to world border
	entity.setLocationAndAngles(d0, d1, d2, f1, f);
	entity.connection.setPlayerLocation(x, y, z, f1, f);
	double d7 = Math.min(-2.9999872E7D, destinationWorld.getWorldBorder().minX() + 16.0D);
	double d4 = Math.min(-2.9999872E7D, destinationWorld.getWorldBorder().minZ() + 16.0D);
	double d5 = Math.min(2.9999872E7D, destinationWorld.getWorldBorder().maxX() - 16.0D);
	double d6 = Math.min(2.9999872E7D, destinationWorld.getWorldBorder().maxZ() - 16.0D);
	d0 = MathHelper.clamp(d0, d7, d5);
	d2 = MathHelper.clamp(d2, d4, d6);
	entity.setLocationAndAngles(d0, d1, d2, f1, f);
	entity.connection.setPlayerLocation(d0, d1, d2, f1, f);

	entity.setWorld(destinationWorld);
	destinationWorld.func_217447_b(entity);
	//entity.func_213846_b(serverworld); // private case #2: seems to be an advancement trigger thing
	entity.connection.setPlayerLocation(d0, d1, d2, f1, f);
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
    }
    
    
}
