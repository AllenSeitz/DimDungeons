package com.catastrophe573.dimdungeons.command;

import javax.annotation.Nonnull;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

// TODO: figure out if this is needed or if Entity.enterDimension or player.teleport is enough
public class CustomTeleporter extends Teleporter 
{
    public CustomTeleporter(WorldServer world, double x, double y, double z)
    {
        super(world);
        this.worldServer = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    private final WorldServer worldServer;
    private double x;
    private double y;
    private double z;

    @Override
    public void placeInPortal(@Nonnull Entity entity, float rotationYaw)
    {
        // The main purpose of this function is to *not* create a nether portal
        this.worldServer.getBlockState(new BlockPos((int) this.x, (int) this.y, (int) this.z));

        entity.setPosition(this.x, this.y, this.z);
        entity.motionX = 0.0f;
        entity.motionY = 0.0f;
        entity.motionZ = 0.0f;
    }

    // a static helper function for GoldPortalBlock
    public static void teleportToDimension(EntityPlayer player, int dimension, double x, double y, double z)
    {
        int oldDimension = player.getEntityWorld().getDimension().getId();
        MinecraftServer server = player.getEntityWorld().getServer();
        WorldServer worldServer = server.getWorld(dimension);
        player.addExperienceLevel(0); // I forget what this was for in 1.12.2 or if it is still needed?

        if (worldServer == null || worldServer.getServer() == null)
        {
            // Dimension doesn't exist
            throw new IllegalArgumentException("Dimension: "+dimension+" doesn't exist!");
        }

        // actually perform the teleport now
        player.setPositionAndUpdate(x, y, z);
        player.changeDimension(dimension, new CustomTeleporter(worldServer, x, y, z));
        if (oldDimension == 1)
        {
            // For some reason teleporting out of the end does weird things. Compensate for that
            player.setPositionAndUpdate(x, y, z);
            worldServer.spawnEntity(player);
            //worldServer.updateEntityWithOptionalForce(player, false); // 1.12.2
            worldServer.tickEntity(player, false); // seems like what 1.13 wants instead of the above line?
        }
    }
}