package com.catastrophe573.dimdungeons.dimension;

import java.util.function.BiFunction;

import com.catastrophe573.dimdungeons.DimDungeons;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.ModDimension;

public class DungeonDimensionType extends ModDimension
{
    public DungeonDimensionType(final ResourceLocation registryName)
    {
	this.setRegistryName(registryName);
    }

    public static DimensionType getDimensionType()
    {
	return DimensionType.byName(new ResourceLocation(DimDungeons.MOD_ID, DimensionRegistrar.dungeon_basic_regname));
    }

    @Override
    public BiFunction<World, DimensionType, ? extends Dimension> getFactory()
    {
	return null;//return AMDimension::new;
    }
}