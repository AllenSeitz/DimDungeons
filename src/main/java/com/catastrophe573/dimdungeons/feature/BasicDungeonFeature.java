package com.catastrophe573.dimdungeons.feature;

import java.util.Random;
import java.util.function.Function;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.mojang.datafixers.Dynamic;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.ScatteredStructure;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class BasicDungeonFeature extends ScatteredStructure<NoFeatureConfig>
{
    public static String STRUCT_ID = "feature_basic_dungeon";
    
    public BasicDungeonFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> function)
    {
	super(function);
    }

    @Override
    public boolean place(IWorld world, ChunkGenerator<? extends GenerationSettings> chunkGenerator, Random rand, BlockPos pos, NoFeatureConfig config)
    {
	DimDungeons.LOGGER.info("MyFeature: PLACING DUNGEON FEATURE");
	world.setBlockState(pos, Blocks.BEDROCK.getDefaultState(), 2);
	return true;
    }

    @Override
    public boolean hasStartAt(ChunkGenerator<?> chunkGen, Random rand, int chunkPosX, int chunkPosZ)
    {
	DimDungeons.LOGGER.info("MyFeature: HAS START AT");
	return false;
    }

    @Override
    public IStartFactory getStartFactory()
    {
	DimDungeons.LOGGER.info("MyFeature: GET START FACTORY");
	return BasicDungeonFeature.Start::new;
    }

    @Override
    public String getStructureName()
    {
	return STRUCT_ID;
    }

    @Override
    // presumably 8 chunks, and coincidentally the same as a stronghold
    public int getSize()
    {
	DimDungeons.LOGGER.info("MyFeature: GET SIZE");
	return 8;
    }

    public static class Start extends StructureStart
    {
	public Start(Structure<?> p_i50780_1_, int p_i50780_2_, int p_i50780_3_, Biome p_i50780_4_, MutableBoundingBox p_i50780_5_, int p_i50780_6_, long p_i50780_7_)
	{
	    super(p_i50780_1_, p_i50780_2_, p_i50780_3_, p_i50780_4_, p_i50780_5_, p_i50780_6_, p_i50780_7_);
	    DimDungeons.LOGGER.info("MyFeature: ****** START ******");
	}

	public void init(ChunkGenerator<?> generator, TemplateManager templateManagerIn, int chunkX, int chunkZ, Biome biomeIn)
	{
	    DimDungeons.LOGGER.info("MyFeature: ****** INIT ******");
	}
    }

    @Override
    // required override
    protected int getSeedModifier()
    {
	return 0;
    }
}