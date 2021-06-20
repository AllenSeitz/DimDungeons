package com.catastrophe573.dimdungeons.block;

import com.catastrophe573.dimdungeons.DimDungeons;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.registries.ObjectHolder;

public class TileEntityLocalTeleporter extends TileEntity
{
    public static final String REG_NAME = "tileentity_local_teleporter";

    @ObjectHolder(DimDungeons.RESOURCE_PREFIX + REG_NAME)
    public static TileEntityType<TileEntityLocalTeleporter> TYPE;

    private double destX, destY, destZ;
    private double destYaw;
    private double destPitch;

    public TileEntityLocalTeleporter()
    {
	super(TYPE);
	destX = destZ = 0;
	destY = 55;
	destPitch = 0;
	destYaw = 180.0f;
    }

    public TileEntityLocalTeleporter(TileEntityType<?> tileEntityTypeIn)
    {
	super(tileEntityTypeIn);
    }

    @Override
    public void load(BlockState stateIn, CompoundNBT compound)
    {
	super.load(stateIn, compound);
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
    public CompoundNBT save(CompoundNBT compound)
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
