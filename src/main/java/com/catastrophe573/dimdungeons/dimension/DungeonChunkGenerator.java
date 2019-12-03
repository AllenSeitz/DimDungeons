package com.catastrophe573.dimdungeons.dimension;

import java.util.List;
import java.util.Random;

import com.catastrophe573.dimdungeons.feature.AdvancedDungeonFeature;
import com.catastrophe573.dimdungeons.feature.BasicDungeonFeature;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityClassification;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.OverworldChunkGenerator;
import net.minecraft.world.gen.OverworldGenSettings;
import net.minecraft.world.gen.WorldGenRegion;

public class DungeonChunkGenerator extends OverworldChunkGenerator
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
    public List<Biome.SpawnListEntry> getPossibleCreatures(EntityClassification creatureType, BlockPos pos)
    {
	// let the biome handle which mobs can spawn
	// otherwise, implement exceptions and structure-specific logic here (witch huts, ocean monuments, etc)
	return super.getPossibleCreatures(creatureType, pos);
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
}