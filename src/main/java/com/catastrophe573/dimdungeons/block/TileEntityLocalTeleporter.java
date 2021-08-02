package com.catastrophe573.dimdungeons.block;

import com.catastrophe573.dimdungeons.DimDungeons;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.BlockPos;
import net.minecraftforge.registries.ObjectHolder;

public class TileEntityLocalTeleporter extends BlockEntity
{
    public static final String REG_NAME = "tileentity_local_teleporter";

    @ObjectHolder(DimDungeons.RESOURCE_PREFIX + REG_NAME)
    public static BlockEntityType<TileEntityLocalTeleporter> TYPE;

    private double destX, destY, destZ;
    private double destYaw;
    private double destPitch;

    public TileEntityLocalTeleporter(BlockPos pos, BlockState state)
    {
	super(TYPE, pos, state);
    }

    @Override
    public void load(CompoundTag compound)
    {
	super.load(compound);
	if (compound.contains("destX") && compound.contains("destY") && compound.contains("destZ") && compound.contains("destPitch") && compound.contains("destYaw"))
	{
	    this.destX = compound.getDouble("destX");
	    this.destY = compound.getDouble("destY");
	    this.destZ = compound.getDouble("destZ");
	    this.destPitch = compound.getDouble("destPitch");
	    this.destYaw = compound.getDouble("destYaw");
	}
    }

    @Override
    public CompoundTag save(CompoundTag compound)
    {
	compound.putDouble("destX", this.destX);
	compound.putDouble("destY", this.destY);
	compound.putDouble("destZ", this.destZ);
	compound.putDouble("destPitch", this.destPitch);
	compound.putDouble("destYaw", this.destYaw);
	return super.save(compound);
    }

    public void setDestination(double posX, double posY, double posZ, double pitch, double yaw)
    {
	this.destX = posX;
	this.destY = posY;
	this.destZ = posZ;
	this.destPitch = pitch;
	this.destYaw = yaw;
    }

    public BlockPos getDestination()
    {
	return new BlockPos(destX, destY, destZ);
    }

    public double getPitch()
    {
	return destPitch;
    }

    public double getYaw()
    {
	return destYaw;
    }
}
