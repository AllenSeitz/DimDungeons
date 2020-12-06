package com.catastrophe573.dimdungeons.dimension;

import java.util.Random;
import java.util.function.Function;

import net.minecraft.block.PortalInfo;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

public class CustomTeleporter implements net.minecraftforge.common.util.ITeleporter
{
    protected final ServerWorld world;
    protected final Random random;

    public CustomTeleporter(ServerWorld worldIn)
    {
	this.world = worldIn;
	this.random = new Random(worldIn.getSeed());
    }

    @Override
    public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destWorld, float yaw, Function<Boolean, Entity> repositionEntity)
    {
	return repositionEntity.apply(true);
    }
    
    @Override
    public PortalInfo getPortalInfo(Entity entity, ServerWorld destWorld, Function<ServerWorld, PortalInfo> defaultPortalInfo)
    {
	// TODO: make the entity always face north after entering the dimension
        return new PortalInfo(entity.getPositionVec(), Vector3d.ZERO, entity.rotationYaw, entity.rotationPitch);
    }    

    // no idea what this was ever used for in previous versiohs
    public boolean placeInPortal(Entity p_222268_1_, float p_222268_2_)
    {
	return true;
    }

    // the whole point of this teleporter is that it DOESN'T place obsidian blocks when I teleport
    public boolean makePortal(Entity entityIn)
    {
	return true;
    }
}