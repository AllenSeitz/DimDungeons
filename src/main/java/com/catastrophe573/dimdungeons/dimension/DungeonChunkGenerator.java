package com.catastrophe573.dimdungeons.dimension;

import java.util.List;
import java.util.Random;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.feature.BasicDungeonFeature;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityClassification;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.Heightmap;
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
	if (BasicDungeonFeature.isDungeonChunk(x, z))
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
			    if (BasicDungeonFeature.isEntranceChunk(x, z))
			    {
				chunkIn.setBlockState(new BlockPos(px, py, pz), Blocks.COBBLESTONE.getDefaultState(), false);
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
    }

    @Override
    protected void makeBedrock(IChunk chunkIn, Random rand) 
    {
	// actually nah I'm good, lets keep the void world please because it looks better on a map
    }
    
    // TODO: delete this old 1.12 function
    public void unusedGenerateSurface(IChunk chunkIn)
    {
	int x = chunkIn.getPos().x;
	int z = chunkIn.getPos().z;
	this.randomSeed.setSeed(this.world.getSeed());
	long k = this.randomSeed.nextLong() / 2L * 2L + 1L;
	long l = this.randomSeed.nextLong() / 2L * 2L + 1L;
	this.randomSeed.setSeed((long) x * k + (long) z * l ^ this.world.getSeed());
	// if this is an entrance chunk then generate a dungeon here
	if (BasicDungeonFeature.isEntranceChunk(x, z))
	{
	    DimDungeons.LOGGER.info("DIM DUNGEONS: Putting a new dungeon at " + x + ", " + z);
	    //generateDungeonAroundChunk(x - 4, z - 7); // the topleft corner of this 8x8 chunk area 
	}
    }

    // copied from NoiseChunkGenerator because that chunk generator is the only one that tells the biomes to buildSurface() 
    //TODO: remove this too
    public void alsoUnusedGenerateSurface(IChunk chunkIn)
    {
	ChunkPos chunkpos = chunkIn.getPos();
	int chunkX = chunkpos.x;
	int chunkZ = chunkpos.z;
	SharedSeedRandom sharedseedrandom = new SharedSeedRandom();
	sharedseedrandom.setBaseChunkSeed(chunkX, chunkZ);
	ChunkPos chunkpos1 = chunkIn.getPos();
	int globalX = chunkpos1.getXStart();
	int globalZ = chunkpos1.getZStart();
	Biome[] abiome = chunkIn.getBiomes();
	DimDungeons.LOGGER.info("DIM DUNGEONS: generatingSurface at " + chunkX + ", " + chunkZ);

	for (int sx = 0; sx < 16; sx++)
	{
	    for (int sz = 0; sz < 16; sz++)
	    {
		int offsetX = globalX + sx;
		int offsetZ = globalZ + sz;
		int height = chunkIn.getTopBlockY(Heightmap.Type.WORLD_SURFACE_WG, sx, sz) + 1;
		double noise = 0L;
		abiome[sz * 16 + sx].buildSurface(sharedseedrandom, chunkIn, offsetX, offsetZ, height, noise, this.getSettings().getDefaultBlock(), this.getSettings().getDefaultFluid(), this.getSeaLevel(), this.world.getSeed());
		//DimDungeons.LOGGER.info("BUILD SURFACE BIOME " + abiome[sz * 16 + sx].getDisplayName().getString());
	    }
	}
    }
}