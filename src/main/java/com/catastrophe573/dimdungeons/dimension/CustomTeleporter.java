package com.catastrophe573.dimdungeons.dimension;

import java.util.Random;
import java.util.function.Function;

import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;

public class CustomTeleporter implements net.minecraftforge.common.util.ITeleporter
{
    protected final ServerLevel world;
    protected final Random random;

    protected Vec3 destPos;
    protected float destYaw;
    protected float destPitch;

    public CustomTeleporter(ServerLevel worldIn)
    {
	this.world = worldIn;
	this.random = new Random(worldIn.getSeed());
    }

    public void setDestPos(double x, double y, double z, float yaw, float pitch)
    {
	destPos = new Vec3(x, y, z);
	destYaw = yaw;
	destPitch = pitch;
    }

    @Override
    public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw, Function<Boolean, Entity> repositionEntity)
    {
	return repositionEntity.apply(true);
    }

    @Override
    public PortalInfo getPortalInfo(Entity entity, ServerLevel destWorld, Function<ServerLevel, PortalInfo> defaultPortalInfo)
    {
	return new PortalInfo(destPos, Vec3.ZERO, destYaw, destPitch);
    }

    // no idea what this was ever used for in previous versions
    public boolean placeInPortal(Entity entity, float yaw)
    {
	return true;
    }

    // the whole point of this teleporter is that it DOESN'T place obsidian blocks when I teleport
    public boolean makePortal(Entity entityIn)
    {
	return true;
    }
}