package com.catastrophe573.dimdungeons.dimension;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.catastrophe573.dimdungeons.structure.DungeonPlacementLogicAdvanced;
import com.catastrophe573.dimdungeons.structure.DungeonPlacementLogicBasic;
import com.mojang.serialization.Codec;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.FixedBiomeSource;

public final class DungeonChunkGenerator extends ChunkGenerator
{
    public static final Codec<FlatLevelSource> myCodec = FlatLevelGeneratorSettings.CODEC.fieldOf("settings").xmap(FlatLevelSource::new, FlatLevelSource::settings).codec();
    private final FlatLevelGeneratorSettings settings;
    private long worldSeed = 0;

    public DungeonChunkGenerator(FlatLevelGeneratorSettings settings)
    {
	super(new FixedBiomeSource(settings.getBiome()), new FixedBiomeSource(settings.getBiome()), settings.structureSettings(), 0L);
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

    public FlatLevelGeneratorSettings settings()
    {
	return this.settings;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void buildSurfaceAndBedrock(WorldGenRegion p_225551_1_, ChunkAccess p_225551_2_)
    {
	// generate my sandstone base and void chunks, which usually don't matter at all but might as well
	ServerLevel world = p_225551_1_.getLevel();
	makeBase(world, p_225551_2_);

	// and intentionally do nothing with structures
    }

    // is this decorate()?
    @Override
    public void applyBiomeDecoration(WorldGenRegion p_230351_1_, StructureFeatureManager p_230351_2_)
    {
	// in vanilla this function basically does this:
	//biome.generateFeatures(p_230351_2_, this, p_230351_1_, i1, sharedseedrandom, blockpos);
    }

    @Override
    // I think this is Carve()
    public void applyCarvers(long p_230350_1_, BiomeManager p_230350_3_, ChunkAccess p_230350_4_, GenerationStep.Carving p_230350_5_)
    {
    }

    // I don't know what this does. I copied it from the vanilla code.
    public CompletableFuture<ChunkAccess> fillFromNoise(Executor p_158281_, StructureFeatureManager p_158282_, ChunkAccess p_158283_)
    {
	List<BlockState> list = this.settings.getLayers();
	BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
	Heightmap heightmap = p_158283_.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
	Heightmap heightmap1 = p_158283_.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);

	for (int i = 0; i < Math.min(p_158283_.getHeight(), list.size()); ++i)
	{
	    BlockState blockstate = list.get(i);
	    if (blockstate != null)
	    {
		int j = p_158283_.getMinBuildHeight() + i;

		for (int k = 0; k < 16; ++k)
		{
		    for (int l = 0; l < 16; ++l)
		    {
			p_158283_.setBlockState(blockpos$mutableblockpos.set(k, j, l), blockstate, false);
			heightmap.update(k, j, l, blockstate);
			heightmap1.update(k, j, l, blockstate);
		    }
		}
	    }
	}

	return CompletableFuture.completedFuture(p_158283_);
    }

    // 1.16 version
    public int getBaseHeight(int x, int z, Heightmap.Types heightmapType)
    {
	List<BlockState> ablockstate = this.settings.getLayers();

	for (int i = ablockstate.size() - 1; i >= 0; --i)
	{
	    BlockState blockstate = ablockstate.get(i);
	    if (blockstate != null && heightmapType.isOpaque().test(blockstate))
	    {
		return i + 1;
	    }
	}

	return 0;
    }

    // 1.17 version
    @Override
    public int getBaseHeight(int x, int z, Types p_156155_, LevelHeightAccessor p_156156_)
    {
	return getBaseHeight(x, z, p_156155_);
    }

    // I don't know what this does. I copied it from the vanilla code.
    public NoiseColumn getBaseColumn(int p_158270_, int p_158271_, LevelHeightAccessor p_158272_)
    {
	return new NoiseColumn(p_158272_.getMinBuildHeight(), this.settings.getLayers().stream().limit((long) p_158272_.getHeight()).map((p_64189_) ->
	{
	    return p_64189_ == null ? Blocks.AIR.defaultBlockState() : p_64189_;
	}).toArray((p_64171_) ->
	{
	    return new BlockState[p_64171_];
	}));
    }

    public void makeBase(LevelAccessor worldIn, ChunkAccess chunkIn)
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