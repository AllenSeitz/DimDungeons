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
    public static final Codec<DungeonChunkGenerator> myCodec = FlatGenerationSettings.field_236932_a_.fieldOf("settings").xmap(DungeonChunkGenerator::new, DungeonChunkGenerator::func_236073_g_).codec();
    private final FlatGenerationSettings field_236070_e_;
    private long worldSeed = 0;

    public DungeonChunkGenerator(FlatGenerationSettings p_i231902_1_)
    {
	super(new SingleBiomeProvider(p_i231902_1_.getBiome()), new SingleBiomeProvider(p_i231902_1_.getBiome()), p_i231902_1_.func_236943_d_(), 0L);	
	this.field_236070_e_ = p_i231902_1_;
    }

    protected Codec<? extends ChunkGenerator> func_230347_a_()
    {
	return myCodec;
    }

    @OnlyIn(Dist.CLIENT)
    public ChunkGenerator func_230349_a_(long p_230349_1_)
    {
	worldSeed = p_230349_1_;
	return this;
    }

    public FlatGenerationSettings func_236073_g_()
    {
	return this.field_236070_e_;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void generateSurface(WorldGenRegion p_225551_1_, IChunk p_225551_2_)
    {
	// generate my sandstone base and void chunks, which usually don't matter at all but might as well
	ServerWorld world = p_225551_1_.getWorld();
	makeBase(world, p_225551_2_);

	// and intentionally do nothing with structures
    }

    // is this decorate()?
    @Override
    public void func_230351_a_(WorldGenRegion p_230351_1_, StructureManager p_230351_2_)
    {
	// in vanilla this function basically does this:
	//biome.func_242427_a(p_230351_2_, this, p_230351_1_, i1, sharedseedrandom, blockpos);
    }

    @Override
    // I think this is Carve()
    public void func_230350_a_(long p_230350_1_, BiomeManager p_230350_3_, IChunk p_230350_4_, GenerationStage.Carving p_230350_5_)
    {
    }

    public int getGroundHeight()
    {
	BlockState[] ablockstate = this.field_236070_e_.getStates();

	for (int i = 0; i < ablockstate.length; ++i)
	{
	    BlockState blockstate = ablockstate[i] == null ? Blocks.AIR.getDefaultState() : ablockstate[i];
	    if (!Heightmap.Type.MOTION_BLOCKING.getHeightLimitPredicate().test(blockstate))
	    {
		return i - 1;
	    }
	}

	return ablockstate.length;
    }

    public void func_230352_b_(IWorld p_230352_1_, StructureManager p_230352_2_, IChunk p_230352_3_)
    {
	BlockState[] ablockstate = this.field_236070_e_.getStates();
	BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
	Heightmap heightmap = p_230352_3_.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
	Heightmap heightmap1 = p_230352_3_.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);

	for (int i = 0; i < ablockstate.length; ++i)
	{
	    BlockState blockstate = ablockstate[i];
	    if (blockstate != null)
	    {
		for (int j = 0; j < 16; ++j)
		{
		    for (int k = 0; k < 16; ++k)
		    {
			p_230352_3_.setBlockState(blockpos$mutable.setPos(j, i, k), blockstate, false);
			heightmap.update(j, i, k, blockstate);
			heightmap1.update(j, i, k, blockstate);
		    }
		}
	    }
	}

    }

    public int getHeight(int x, int z, Heightmap.Type heightmapType)
    {
	BlockState[] ablockstate = this.field_236070_e_.getStates();

	for (int i = ablockstate.length - 1; i >= 0; --i)
	{
	    BlockState blockstate = ablockstate[i];
	    if (blockstate != null && heightmapType.getHeightLimitPredicate().test(blockstate))
	    {
		return i + 1;
	    }
	}

	return 0;
    }

    public IBlockReader func_230348_a_(int p_230348_1_, int p_230348_2_)
    {
	return new Blockreader(Arrays.stream(this.field_236070_e_.getStates()).map((p_236072_0_) ->
	{
	    return p_236072_0_ == null ? Blocks.AIR.getDefaultState() : p_236072_0_;
	}).toArray((p_236071_0_) ->
	{
	    return new BlockState[p_236071_0_];
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
			    chunkIn.setBlockState(new BlockPos(px, py, pz), Blocks.BEDROCK.getDefaultState(), false);
			}
			else if (py < 50)
			{
			    // for debugging mostly but it also kind of looks good when you're in creative mode
			    if (DungeonPlacementLogicBasic.isEntranceChunk(x, z) || DungeonPlacementLogicAdvanced.isEntranceChunk(x, z))
			    {
				chunkIn.setBlockState(new BlockPos(px, py, pz), Blocks.GRANITE.getDefaultState(), false);
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
	    // add barrier blocks to the void in case the player escapes (although these are escapable, too)
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
}