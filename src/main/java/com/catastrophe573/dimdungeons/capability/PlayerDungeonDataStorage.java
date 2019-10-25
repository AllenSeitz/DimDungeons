package com.catastrophe573.dimdungeons.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class PlayerDungeonDataStorage implements IStorage<IPlayerDungeonData>
{
    @Override
    public CompoundNBT writeNBT(Capability<IPlayerDungeonData> capability, IPlayerDungeonData instance, Direction side)
    {
	CompoundNBT nbt = new CompoundNBT();
	nbt.putFloat("LastOverworldPortalX", instance.getLastOverworldPortalX());
	nbt.putFloat("LastOverworldPortalY", instance.getLastOverworldPortalY());
	nbt.putFloat("LastOverworldPortalZ", instance.getLastOverworldPortalZ());
	nbt.putFloat("LastOverworldPortalYaw", instance.getLastOverworldPortalYaw());
	return nbt;
    }

    @Override
    public void readNBT(Capability<IPlayerDungeonData> capability, IPlayerDungeonData instance, Direction side, INBT nbt)
    {
	instance.setLastOverworldPortalX(((CompoundNBT)nbt).getFloat("LastOverworldPortalX"));
	instance.setLastOverworldPortalY(((CompoundNBT)nbt).getFloat("LastOverworldPortalY"));
	instance.setLastOverworldPortalZ(((CompoundNBT)nbt).getFloat("LastOverworldPortalZ"));
	instance.setLastOverworldPortalYaw(((CompoundNBT)nbt).getFloat("LastOverworldPortalYaw"));
    }
}