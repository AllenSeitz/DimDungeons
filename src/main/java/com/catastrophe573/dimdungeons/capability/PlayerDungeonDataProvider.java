package com.catastrophe573.dimdungeons.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class PlayerDungeonDataProvider implements ICapabilitySerializable<CompoundNBT>
{
    @CapabilityInject(IPlayerDungeonData.class)
    //public static final Capability<IPlayerDungeonData> DUNGEONDATA_CAPABILITY = null;

    //private IPlayerDungeonData instance = DUNGEONDATA_CAPABILITY.getDefaultInstance();

    @SuppressWarnings("unchecked")
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side)
    {
	return (LazyOptional<T>) LazyOptional.of(() ->
	{
	    return new DefaultPlayerDungeonData();
	});
    }

    @Override
    public CompoundNBT serializeNBT()
    {
	return null;
	//return (CompoundNBT) DUNGEONDATA_CAPABILITY.getStorage().writeNBT(DUNGEONDATA_CAPABILITY, instance, null);
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
	//DUNGEONDATA_CAPABILITY.getStorage().readNBT(DUNGEONDATA_CAPABILITY, instance, null, nbt);
    }
}