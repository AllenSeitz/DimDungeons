package com.catastrophe573.dimdungeons.teleporter;

import java.util.Random;
import net.minecraft.entity.Entity;
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