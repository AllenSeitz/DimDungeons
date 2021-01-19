package com.catastrophe573.dimdungeons.block;

import com.catastrophe573.dimdungeons.DimDungeons;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.registries.ObjectHolder;

public class TileEntityGoldPortal extends TileEntity
{
    public static final String REG_NAME = "tileentity_gold_portal";

    @ObjectHolder(DimDungeons.RESOURCE_PREFIX + REG_NAME)
    public static TileEntityType<TileEntityGoldPortal> TYPE;

    private double destX, destY, destZ;

    public TileEntityGoldPortal()
    {
	super(TYPE);
    }

    public TileEntityGoldPortal(TileEntityType<?> tileEntityTypeIn)
    {
	super(tileEntityTypeIn);
    }

    @Override
    public void read(BlockState stateIn, CompoundNBT compound)
    {
	super.read(stateIn, compound);
	if (compound.contains("destX") && compound.contains("destY") && compound.contains("destZ"))
	{
	    this.destX = compound.getDouble("destX");
	    this.destY = compound.getDouble("destY");
	    this.destZ = compound.getDouble("destZ");
	}
    }

    @Override
    public CompoundNBT write(CompoundNBT compound)
    {
	compound.putDouble("destX", this.destX);
	compound.putDouble("destY", this.destY);
	compound.putDouble("destZ", this.destZ);
	return super.write(compound);
    }

    public void setDestination(double posX, double posY, double posZ)
    {
	this.destX = posX;
	this.destY = posY;
	this.destZ = posZ;
    }

    public BlockPos getDestination()
    {
	return new BlockPos(destX, destY, destZ);
    }
}
