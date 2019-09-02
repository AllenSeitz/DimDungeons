package com.catastrophe573.dimdungeons.dimension;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.EntityClassification;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.FlatChunkGenerator;
import net.minecraft.world.gen.FlatGenerationSettings;
import net.minecraft.world.gen.WorldGenRegion;

public class DungeonChunkGenerator extends FlatChunkGenerator
{
    // I still want a random seed, like the overworld, for use in structures
    private Random randomSeed;

    public DungeonChunkGenerator(IWorld world, BiomeProvider provider, FlatGenerationSettings settingsIn)
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
}