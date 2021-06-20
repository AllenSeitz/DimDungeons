package com.catastrophe573.dimdungeons.block;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.DungeonConfig;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.registries.ObjectHolder;

public class TileEntityGoldPortal extends TileEntity
{
    public static final String REG_NAME = "tileentity_gold_portal";

    @ObjectHolder(DimDungeons.RESOURCE_PREFIX + REG_NAME)
    public static TileEntityType<TileEntityGoldPortal> TYPE;

    private double destX = 0, destY = -10000, destZ = 0;
    private int cooldown = DungeonConfig.portalCooldownTicks;
    private int lastUpdate = 0;
    private String destDimension = "minecraft:overworld";

    public TileEntityGoldPortal()
    {
	super(TYPE);
    }

    public TileEntityGoldPortal(TileEntityType<?> tileEntityTypeIn)
    {
	super(tileEntityTypeIn);
    }

    @Override
    public void load(BlockState stateIn, CompoundNBT compound)
    {
	super.load(stateIn, compound);
	if (compound.contains("destX") && compound.contains("destY") && compound.contains("destZ"))
	{
	    this.destX = compound.getDouble("destX");
	    this.destY = compound.getDouble("destY");
	    this.destZ = compound.getDouble("destZ");
	}
	if (compound.contains("cooldown"))
	{
	    this.cooldown = compound.getInt("cooldown");
	}

	// default value for portals that players created before I allowed portals to other dimensions 
	if (compound.contains("destDimension"))
	{
	    this.destDimension = compound.getString("destDimension");
	}
	else
	{
	    this.destDimension = "minecraft:overworld";
	}
    }

    @Override
    public CompoundNBT save(CompoundNBT compound)
    {
	compound.putDouble("destX", this.destX);
	compound.putDouble("destY", this.destY);
	compound.putDouble("destZ", this.destZ);
	compound.putInt("cooldown", this.cooldown);
	compound.putString("destDimension", this.destDimension);
	return super.save(compound);
    }

    public void setDestination(double posX, double posY, double posZ, String destDim)
    {
	this.destX = posX;
	this.destY = posY;
	this.destZ = posZ;
	this.destDimension = destDim;
    }

    public BlockPos getDestination()
    {
	return new BlockPos(destX, destY, destZ);
    }

    public int getCooldown()
    {
	return cooldown;
    }

    public RegistryKey<World> getDestinationDimension()
    {
	return RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(destDimension));
    }

    public boolean needsUpdateThisTick(int tick)
    {
	return tick > lastUpdate;
    }

    // return true if recursion is necessary
    public boolean setCooldown(int value, World worldIn, BlockPos pos, int currentServerTick)
    {
	if (cooldown == value || currentServerTick <= lastUpdate)
	{
	    return false;
	}

	cooldown = value;
	lastUpdate = currentServerTick;

	// search each neighboring block for siblings and update them too
	int numBlocksUpdated = 0;
	if (worldIn.getBlockEntity(pos.west()) instanceof TileEntityGoldPortal)
	{
	    numBlocksUpdated += ((TileEntityGoldPortal) worldIn.getBlockEntity(pos.west())).setCooldown(value, worldIn, pos.west(), currentServerTick) ? 1 : 0;
	}
	if (worldIn.getBlockEntity(pos.east()) instanceof TileEntityGoldPortal)
	{
	    numBlocksUpdated += ((TileEntityGoldPortal) worldIn.getBlockEntity(pos.east())).setCooldown(value, worldIn, pos.east(), currentServerTick) ? 1 : 0;
	}
	if (worldIn.getBlockEntity(pos.north()) instanceof TileEntityGoldPortal)
	{
	    numBlocksUpdated += ((TileEntityGoldPortal) worldIn.getBlockEntity(pos.north())).setCooldown(value, worldIn, pos.north(), currentServerTick) ? 1 : 0;
	}
	if (worldIn.getBlockEntity(pos.south()) instanceof TileEntityGoldPortal)
	{
	    numBlocksUpdated += ((TileEntityGoldPortal) worldIn.getBlockEntity(pos.south())).setCooldown(value, worldIn, pos.south(), currentServerTick) ? 1 : 0;
	}
	if (worldIn.getBlockEntity(pos.above()) instanceof TileEntityGoldPortal)
	{
	    numBlocksUpdated += ((TileEntityGoldPortal) worldIn.getBlockEntity(pos.above())).setCooldown(value, worldIn, pos.above(), currentServerTick) ? 1 : 0;
	}
	if (worldIn.getBlockEntity(pos.below()) instanceof TileEntityGoldPortal)
	{
	    numBlocksUpdated += ((TileEntityGoldPortal) worldIn.getBlockEntity(pos.below())).setCooldown(value, worldIn, pos.below(), currentServerTick) ? 1 : 0;
	}

	return numBlocksUpdated > 0;
    }
}
