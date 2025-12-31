package com.catastrophe573.dimdungeons.block;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class TileEntityLocalTeleporter extends BlockEntity
{
	public static final String REG_NAME = "tileentity_local_teleporter";

	private double destX, destY, destZ;
	private double destYaw;
	private double destPitch;

	public TileEntityLocalTeleporter(BlockPos pos, BlockState state)
	{
		super(BlockRegistrar.BE_LOCAL_TELEPORTER.get(), pos, state);
	}

	@Override
	protected void loadAdditional(ValueInput input)
	{
		super.loadAdditional(input);

		this.destX = input.getDoubleOr("destX", 0);
		this.destY = input.getDoubleOr("destY", -10000);
		this.destZ = input.getDoubleOr("destZ", 0);

		this.destPitch = input.getDoubleOr("destPitch", 0);
		this.destYaw = input.getDoubleOr("destYaw", 0);
	}

	@Override
	protected void saveAdditional(ValueOutput output)
	{
		super.saveAdditional(output);

		output.putDouble("destX", this.destX);
		output.putDouble("destY", this.destY);
		output.putDouble("destZ", this.destZ);

		output.putDouble("destPitch", this.destPitch);
		output.putDouble("destYaw", this.destYaw);
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
		return new BlockPos((int)destX, (int)destY, (int)destZ); // change to "new BlockPos()" once the mappings are fixed
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