package com.catastrophe573.dimdungeons.feature;

import java.util.Random;
import java.util.function.Function;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.mojang.datafixers.Dynamic;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;

public class BasicDungeonFeature extends Structure<NoFeatureConfig>
{
    public BasicDungeonFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> function)
    {
	super(function);
    }
    
    @Override
    public boolean place(IWorld world, ChunkGenerator<? extends GenerationSettings> chunkGenerator, Random rand, BlockPos pos, NoFeatureConfig config)
    {
	DimDungeons.LOGGER.info("PLACING DUNGEON FEATURE");
	world.setBlockState(pos, Blocks.BEDROCK.getDefaultState(), 2);
	return true;
    }

    @Override
    public boolean hasStartAt(ChunkGenerator<?> chunkGen, Random rand, int chunkPosX, int chunkPosZ)
    {
	DimDungeons.LOGGER.info("HAS START AT");
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public IStartFactory getStartFactory()
    {
	DimDungeons.LOGGER.info("GET START FACTORY");
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public String getStructureName()
    {
	DimDungeons.LOGGER.info("GET STRUCTURE NAME");
	return "basic_dungeon";
    }

    @Override
    public int getSize()
    {
	DimDungeons.LOGGER.info("GET SIZE");
	// TODO Auto-generated method stub
	return 0;
    }
}