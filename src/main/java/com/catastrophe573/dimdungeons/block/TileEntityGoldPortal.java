package com.catastrophe573.dimdungeons.block;

import com.catastrophe573.dimdungeons.DungeonConfig;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;

public class TileEntityGoldPortal extends BlockEntity
{
	public static final String REG_NAME = "tileentity_gold_portal";

	private double destX = 0, destY = -10000, destZ = 0;
	private Direction facing = Direction.NORTH; // the direction to face after teleporting
	private int cooldown = DungeonConfig.portalCooldownTicks;
	private int lastUpdate = 0;
	private String destDimension = "minecraft:overworld";

	public TileEntityGoldPortal(BlockPos pos, BlockState state)
	{
		super(BlockRegistrar.BE_GOLD_PORTAL.get(), pos, state);
	}

	@Override
	protected void loadAdditional(@NonNull ValueInput input)
	{
		super.loadAdditional(input);

		this.destX = input.getDoubleOr("destX", 0);
		this.destY = input.getDoubleOr("destY", -10000);
		this.destZ = input.getDoubleOr("destZ", 0);

		this.cooldown = input.getIntOr("cooldown", DungeonConfig.portalCooldownTicks);

		this.destDimension = input.getStringOr("destDimension", "minecraft:overworld");

		this.facing = Direction.valueOf(input.getStringOr("facing", Direction.NORTH.getName()));
	}

	@Override
	protected void saveAdditional(@NonNull ValueOutput output)
	{
		super.saveAdditional(output);

		output.putDouble("destX", this.destX);
		output.putDouble("destY", this.destY);
		output.putDouble("destZ", this.destZ);
		output.putInt("cooldown", this.cooldown);
		output.putString("destDimension", this.destDimension);
		output.putString("facing", this.facing.name());
	}

	public void setDestination(double posX, double posY, double posZ, String destDim, Direction faceExit)
	{
		this.destX = posX;
		this.destY = posY;
		this.destZ = posZ;
		this.destDimension = destDim;
		this.facing = faceExit;
	}

	public BlockPos getDestination()
	{
		return new BlockPos((int)destX, (int)destY, (int)destZ);
	}

	public int getCooldown()
	{
		return cooldown;
	}

	public ResourceKey<Level> getDestinationDimension()
	{
		return ResourceKey.create(Registries.DIMENSION, Identifier.parse(destDimension));
	}

	public Direction getExitDirection()
	{
		return facing;
	}

	public boolean needsUpdateThisTick(int tick)
	{
		return tick > lastUpdate;
	}

	// return true if recursion is necessary
	public boolean setCooldown(int value, Level worldIn, BlockPos pos, int currentServerTick)
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