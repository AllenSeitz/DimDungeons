package com.catastrophe573.dimdungeons.dimension;

import java.util.Arrays;
import java.util.Random;

import com.catastrophe573.dimdungeons.structure.DungeonPlacementLogicAdvanced;
import com.catastrophe573.dimdungeons.structure.DungeonPlacementLogicBasic;
import com.mojang.serialization.Codec;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Blockreader;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.FlatChunkGenerator;
import net.minecraft.world.gen.FlatGenerationSettings;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.biome.provider.SingleBiomeProvider;

public final class DungeonChunkGenerator extends ChunkGenerator
{
    public static final Codec<FlatChunkGenerator> myCodec = FlatGenerationSettings.CODEC.fieldOf("settings").xmap(FlatChunkGenerator::new, FlatChunkGenerator::settings).codec();
    private final FlatGenerationSettings settings;
    private long worldSeed = 0;

    public DungeonChunkGenerator(FlatGenerationSettings settings)
    {
	super(new SingleBiomeProvider(settings.getBiome()), new SingleBiomeProvider(settings.getBiome()), settings.structureSettings(), 0L);
	this.settings = settings;
    }

    protected Codec<? extends ChunkGenerator> codec()
    {
	return myCodec;
    }

    @OnlyIn(Dist.CLIENT)
    public ChunkGenerator withSeed(long p_230349_1_)
    {
	worldSeed = p_230349_1_;
	return this;
    }

    public FlatGenerationSettings settings()
    {
	return this.settings;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void buildSurfaceAndBedrock(WorldGenRegion p_225551_1_, IChunk p_225551_2_)
    {
	// generate my sandstone base and void chunks, which usually don't matter at all but might as well
	ServerWorld world = p_225551_1_.getLevel();
	makeBase(world, p_225551_2_);

	// and intentionally do nothing with structures
    }

    // is this decorate()?
    @Override
    public void applyBiomeDecoration(WorldGenRegion p_230351_1_, StructureManager p_230351_2_)
    {
	// in vanilla this function basically does this:
	//biome.generateFeatures(p_230351_2_, this, p_230351_1_, i1, sharedseedrandom, blockpos);
    }

    @Override
    // I think this is Carve()
    public void applyCarvers(long p_230350_1_, BiomeManager p_230350_3_, IChunk p_230350_4_, GenerationStage.Carving p_230350_5_)
    {
    }

    public int getSpawnHeight()
    {
	BlockState[] ablockstate = this.settings.getLayers();

	for (int i = 0; i < ablockstate.length; ++i)
	{
	    BlockState blockstate = ablockstate[i] == null ? Blocks.AIR.defaultBlockState() : ablockstate[i];
	    if (!Heightmap.Type.MOTION_BLOCKING.isOpaque().test(blockstate))
	    {
		return i - 1;
	    }
	}

	return ablockstate.length;
    }

    public void fillFromNoise(IWorld p_230352_1_, StructureManager p_230352_2_, IChunk p_230352_3_)
    {
	BlockState[] ablockstate = this.settings.getLayers();
	BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
	Heightmap heightmap = p_230352_3_.getOrCreateHeightmapUnprimed(Heightmap.Type.OCEAN_FLOOR_WG);
	Heightmap heightmap1 = p_230352_3_.getOrCreateHeightmapUnprimed(Heightmap.Type.WORLD_SURFACE_WG);

	for (int i = 0; i < ablockstate.length; ++i)
	{
	    BlockState blockstate = ablockstate[i];
	    if (blockstate != null)
	    {
		for (int j = 0; j < 16; ++j)
		{
		    for (int k = 0; k < 16; ++k)
		    {
			p_230352_3_.setBlockState(blockpos$mutable.set(j, i, k), blockstate, false);
			heightmap.update(j, i, k, blockstate);
			heightmap1.update(j, i, k, blockstate);
		    }
		}
	    }
	}

    }

    public int getBaseHeight(int x, int z, Heightmap.Type heightmapType)
    {
	BlockState[] ablockstate = this.settings.getLayers();

	for (int i = ablockstate.length - 1; i >= 0; --i)
	{
	    BlockState blockstate = ablockstate[i];
	    if (blockstate != null && heightmapType.isOpaque().test(blockstate))
	    {
		return i + 1;
	    }
	}

	return 0;
    }

    public IBlockReader getBaseColumn(int p_230348_1_, int p_230348_2_)
    {
	return new Blockreader(Arrays.stream(this.settings.getLayers()).map((state) ->
	{
	    return state == null ? Blocks.AIR.defaultBlockState() : state;
	}).toArray((size) ->
	{
	    return new BlockState[size];
	}));
    }

    public void makeBase(IWorld worldIn, IChunk chunkIn)
    {
	// I still want a random seed, like the overworld, for use in structures
	Random randomSeed = worldIn.getRandom();

	// 1.14 seed changing logic
	int x = chunkIn.getPos().x;
	int z = chunkIn.getPos().z;
	randomSeed.setSeed((worldSeed + (long) (x * x * 4987142) + (long) (x * 5947611) + (long) (z * z) * 4392871L + (long) (z * 389711) ^ worldSeed));

	// first generate a superflat world - sandstone where dungeons can appear, and void otherwise
	if (DungeonPlacementLogicBasic.isDungeonChunk(x, z) || DungeonPlacementLogicAdvanced.isDungeonChunk(x, z))
	{
	    for (int px = 0; px < 16; px++)
	    {
		for (int py = 1; py < 255; py++)
		{
		    for (int pz = 0; pz < 16; pz++)
		    {
			if (py < 2)
			{
			    chunkIn.setBlockState(new BlockPos(px, py, pz), Blocks.BEDROCK.defaultBlockState(), false);
			}
			else if (py < 50)
			{
			    // for debugging mostly but it also kind of looks good when you're in creative mode
			    if (DungeonPlacementLogicBasic.isEntranceChunk(x, z) || DungeonPlacementLogicAdvanced.isEntranceChunk(x, z))
			    {
				chunkIn.setBlockState(new BlockPos(px, py, pz), Blocks.BLACKSTONE.defaultBlockState(), false);
			    }
			    else
			    {
				chunkIn.setBlockState(new BlockPos(px, py, pz), Blocks.SANDSTONE.defaultBlockState(), false);
			    }
			}
		    }
		}
	    }
	}
	else
	{
	    // add barrier blocks to the void in case the player escapes (although these are escapable, too)
	    if (x % 16 == 0 || z % 16 == 0)
	    {
		for (int px = 0; px < 16; px++)
		{
		    for (int py = 1; py < 255; py++)
		    {
			for (int pz = 0; pz < 16; pz++)
			{
			    chunkIn.setBlockState(new BlockPos(px, py, pz), Blocks.BARRIER.defaultBlockState(), false);
			}
		    }
		}
	    }
	}
    }
}