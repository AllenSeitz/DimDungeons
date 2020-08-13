package com.catastrophe573.dimdungeons.dimension;

import java.util.List;
import java.util.Random;

import com.catastrophe573.dimdungeons.feature.AdvancedDungeonFeature;
import com.catastrophe573.dimdungeons.feature.BasicDungeonFeature;
import com.catastrophe573.dimdungeons.feature.FeatureRegistrar;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityClassification;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.gen.OverworldChunkGenerator;
import net.minecraft.world.gen.OverworldGenSettings;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class DungeonChunkGenerator extends ChunkGenerator
{
    // I still want a random seed, like the overworld, for use in structures
    private Random randomSeed;

    public DungeonChunkGenerator(IWorld world, BiomeProvider provider, OverworldGenSettings settingsIn)
    {
	super(world, provider, settingsIn);

	randomSeed = world.getRandom();
    }

    @Override
    public void spawnMobs(WorldGenRegion region)
    {
	// nope, not in this dungeon
    }

    @Override
    public void spawnMobs(ServerWorld worldIn, boolean spawnHostileMobs, boolean spawnPeacefulMobs)
    {
	// nope, not in this dungeon
    }

    @Override
    //public List<Biome.SpawnListEntry> getPossibleCreatures(EntityClassification creatureType, BlockPos pos)
    public List<Biome.SpawnListEntry> func_230353_a_(Biome p_230353_1_, StructureManager p_230353_2_, EntityClassification p_230353_3_, BlockPos p_230353_4_) {
	return Lists.newArrayList();
	   }

    
    @Override
    public int getGroundHeight()
    {
	return this.world.getSeaLevel() + 1;
    }

    @Override
    public int getSeaLevel()
    {
	return 20; // no reason, and should never matter for my purposes
    }

    public void makeBase(IWorld worldIn, IChunk chunkIn)
    {
	int x = chunkIn.getPos().x;
	int z = chunkIn.getPos().z;
	long worldSeed = world.getSeed();
	randomSeed.setSeed((worldSeed + (long) (x * x * 4987142) + (long) (x * 5947611) + (long) (z * z) * 4392871L + (long) (z * 389711) ^ worldSeed));

	// first generate a superflat world - sandstone where dungeons can appear, and void otherwise
	if (BasicDungeonFeature.isDungeonChunk(x, z) || AdvancedDungeonFeature.isDungeonChunk(x, z))
	{
	    for (int px = 0; px < 16; px++)
	    {
		for (int py = 1; py < 255; py++)
		{
		    for (int pz = 0; pz < 16; pz++)
		    {
			if (py < 2)
			{
			    chunkIn.setBlockState(new BlockPos(px, py, pz), Blocks.BEDROCK.getDefaultState(), false);
			}
			else if (py < 50)
			{
			    // for debugging mostly but it also kind of looks good when you're in creative mode
			    if (BasicDungeonFeature.isEntranceChunk(x, z) || AdvancedDungeonFeature.isEntranceChunk(x, z))
			    {
				chunkIn.setBlockState(new BlockPos(px, py, pz), Blocks.ANDESITE.getDefaultState(), false);
			    }
			    else
			    {
				chunkIn.setBlockState(new BlockPos(px, py, pz), Blocks.SANDSTONE.getDefaultState(), false);
			    }
			}
		    }
		}
	    }
	}
	else
	{
	    // add barrier blocks to the void, just to be sure (players could escape with ender pearls, use elytra with fireworks, etc)
	    if (x % 16 == 0 || z % 16 == 0)
	    {
		for (int px = 0; px < 16; px++)
		{
		    for (int py = 1; py < 255; py++)
		    {
			for (int pz = 0; pz < 16; pz++)
			{
			    chunkIn.setBlockState(new BlockPos(px, py, pz), Blocks.BARRIER.getDefaultState(), false);
			}
		    }
		}
	    }
	}
    }

    @Override
    protected void makeBedrock(IChunk chunkIn, Random rand)
    {
	// actually nah I'm good, lets keep the void world please because it looks better on a map
    }

    @Override
    public void generateStructures(BiomeManager p_227058_1_, IChunk p_227058_2_, ChunkGenerator<?> p_227058_3_, TemplateManager p_227058_4_)
    {
	// intentionally do nothing
    }

    @Override
    public void decorate(final WorldGenRegion region)
    {
	// these local variables are copied from the vanilla ChunkGenerator class
	int i = region.getMainChunkX();
	int j = region.getMainChunkZ();
	int k = i * 16;
	int l = j * 16;
	BlockPos blockpos = new BlockPos(k, 0, l);
	//Biome biome = this.func_225552_a_(region.getBiomeManager(), blockpos.add(8, 8, 8));
	SharedSeedRandom sharedseedrandom = new SharedSeedRandom();
	//long i1 = sharedseedrandom.setDecorationSeed(region.getSeed(), k, l);

	// instead of doing this
	//biome.decorate(generationstage$decoration, this, region, i1, sharedseedrandom, blockpos);

	// let's just place the only two features that I care about. this prevents any other features from other mods from appearing
	FeatureRegistrar.feature_basic_dungeon.place(region, this, sharedseedrandom, blockpos, null);
	FeatureRegistrar.feature_advanced_dungeon.place(region, this, sharedseedrandom, blockpos, null);
    }

    @Override
    protected Codec<? extends ChunkGenerator> func_230347_a_()
    {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public ChunkGenerator func_230349_a_(long p_230349_1_)
    {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public void generateSurface(WorldGenRegion p_225551_1_, IChunk p_225551_2_)
    {
	// TODO Auto-generated method stub
	
    }

    @Override
    public void func_230352_b_(IWorld p_230352_1_, StructureManager p_230352_2_, IChunk p_230352_3_)
    {
	// TODO Auto-generated method stub
	
    }

    @Override
    public int func_222529_a(int p_222529_1_, int p_222529_2_, Type heightmapType)
    {
	// TODO Auto-generated method stub
	return 0;
    }

    @Override
    public IBlockReader func_230348_a_(int p_230348_1_, int p_230348_2_)
    {
	// TODO Auto-generated method stub
	return null;
    }
}