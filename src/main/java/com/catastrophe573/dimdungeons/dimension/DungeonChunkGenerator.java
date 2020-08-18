package com.catastrophe573.dimdungeons.dimension;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.catastrophe573.dimdungeons.feature.AdvancedDungeonFeature;
import com.catastrophe573.dimdungeons.feature.BasicDungeonFeature;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityClassification;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Blockreader;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.FlatChunkGenerator;
import net.minecraft.world.gen.FlatGenerationSettings;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.INoiseGenerator;
import net.minecraft.world.gen.ImprovedNoiseGenerator;
import net.minecraft.world.gen.NoiseChunkGenerator;
import net.minecraft.world.gen.OctavesNoiseGenerator;
import net.minecraft.world.gen.PerlinNoiseGenerator;
import net.minecraft.world.gen.SimplexNoiseGenerator;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.biome.provider.EndBiomeProvider;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.gen.feature.jigsaw.JigsawJunction;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.settings.NoiseSettings;
import net.minecraft.world.spawner.WorldEntitySpawner;

public final class DungeonChunkGenerator extends ChunkGenerator
{
    public static final Codec<DungeonChunkGenerator> field_236079_d_ = RecordCodecBuilder.create((p_236091_0_) ->
    {
	return p_236091_0_.group(BiomeProvider.field_235202_a_.fieldOf("biome_source").forGetter((p_236096_0_) ->
	{
	    return p_236096_0_.biomeProvider;
	}), Codec.LONG.fieldOf("seed").stable().forGetter((p_236093_0_) ->
	{
	    return p_236093_0_.field_236084_w_;
	}), DimensionSettings.field_236098_b_.fieldOf("settings").forGetter((p_236090_0_) ->
	{
	    return p_236090_0_.field_236080_h_;
	})).apply(p_236091_0_, p_236091_0_.stable(DungeonChunkGenerator::new));
    });

    private static final float[] field_222561_h = Util.make(new float[13824], (p_236094_0_) ->
    {
	for (int i = 0; i < 24; ++i)
	{
	    for (int j = 0; j < 24; ++j)
	    {
		for (int k = 0; k < 24; ++k)
		{
		    p_236094_0_[i * 24 * 24 + j * 24 + k] = (float) func_222554_b(j - 12, k - 12, i - 12);
		}
	    }
	}

    });
    private static final float[] field_236081_j_ = Util.make(new float[25], (p_236092_0_) ->
    {
	for (int i = -2; i <= 2; ++i)
	{
	    for (int j = -2; j <= 2; ++j)
	    {
		float f = 10.0F / MathHelper.sqrt((float) (i * i + j * j) + 0.2F);
		p_236092_0_[i + 2 + (j + 2) * 5] = f;
	    }
	}

    });
    private static final BlockState AIR = Blocks.AIR.getDefaultState();
    private final int verticalNoiseGranularity;
    private final int horizontalNoiseGranularity;
    private final int noiseSizeX;
    private final int noiseSizeY;
    private final int noiseSizeZ;
    protected final SharedSeedRandom randomSeed;
    private final OctavesNoiseGenerator field_222568_o;
    private final OctavesNoiseGenerator field_222569_p;
    private final OctavesNoiseGenerator field_222570_q;
    private final INoiseGenerator surfaceDepthNoise;
    private final OctavesNoiseGenerator field_236082_u_;
    @Nullable
    private final SimplexNoiseGenerator field_236083_v_;
    protected final BlockState defaultBlock;
    protected final BlockState defaultFluid;
    private final long field_236084_w_;
    protected final DimensionSettings field_236080_h_;
    private final int field_236085_x_;

    public DungeonChunkGenerator(BiomeProvider p_i231903_1_, long p_i231903_2_, DimensionSettings p_i231903_4_)
    {
	this(p_i231903_1_, p_i231903_1_, p_i231903_2_, p_i231903_4_);
    }

    @SuppressWarnings("deprecation")
    private DungeonChunkGenerator(BiomeProvider p_i231904_1_, BiomeProvider p_i231904_2_, long p_i231904_3_, DimensionSettings p_i231904_5_)
    {
	super(p_i231904_1_, p_i231904_2_, p_i231904_5_.func_236108_a_(), p_i231904_3_);
	this.field_236084_w_ = p_i231904_3_;
	this.field_236080_h_ = p_i231904_5_;
	NoiseSettings noisesettings = p_i231904_5_.func_236113_b_();
	this.field_236085_x_ = noisesettings.func_236169_a_();
	this.verticalNoiseGranularity = noisesettings.func_236175_f_() * 4;
	this.horizontalNoiseGranularity = noisesettings.func_236174_e_() * 4;
	this.defaultBlock = p_i231904_5_.func_236115_c_();
	this.defaultFluid = p_i231904_5_.func_236116_d_();
	this.noiseSizeX = 16 / this.horizontalNoiseGranularity;
	this.noiseSizeY = noisesettings.func_236169_a_() / this.verticalNoiseGranularity;
	this.noiseSizeZ = 16 / this.horizontalNoiseGranularity;
	this.randomSeed = new SharedSeedRandom(p_i231904_3_);
	this.field_222568_o = new OctavesNoiseGenerator(this.randomSeed, IntStream.rangeClosed(-15, 0));
	this.field_222569_p = new OctavesNoiseGenerator(this.randomSeed, IntStream.rangeClosed(-15, 0));
	this.field_222570_q = new OctavesNoiseGenerator(this.randomSeed, IntStream.rangeClosed(-7, 0));
	this.surfaceDepthNoise = (INoiseGenerator) (noisesettings.func_236178_i_() ? new PerlinNoiseGenerator(this.randomSeed, IntStream.rangeClosed(-3, 0)) : new OctavesNoiseGenerator(this.randomSeed, IntStream.rangeClosed(-3, 0)));
	this.randomSeed.skip(2620);
	this.field_236082_u_ = new OctavesNoiseGenerator(this.randomSeed, IntStream.rangeClosed(-15, 0));
	if (noisesettings.func_236180_k_())
	{
	    SharedSeedRandom sharedseedrandom = new SharedSeedRandom(p_i231904_3_);
	    sharedseedrandom.skip(17292);
	    this.field_236083_v_ = new SimplexNoiseGenerator(sharedseedrandom);
	}
	else
	{
	    this.field_236083_v_ = null;
	}

    }

    protected Codec<? extends ChunkGenerator> func_230347_a_()
    {
	return field_236079_d_;
    }

    @OnlyIn(Dist.CLIENT)
    public ChunkGenerator func_230349_a_(long p_230349_1_)
    {
	return new DungeonChunkGenerator(this.biomeProvider.func_230320_a_(p_230349_1_), p_230349_1_, this.field_236080_h_);
    }

    public boolean func_236088_a_(long p_236088_1_, DimensionSettings.Preset p_236088_3_)
    {
	return this.field_236084_w_ == p_236088_1_ && this.field_236080_h_.func_236109_a_(p_236088_3_);
    }

    private double func_222552_a(int p_222552_1_, int p_222552_2_, int p_222552_3_, double p_222552_4_, double p_222552_6_, double p_222552_8_, double p_222552_10_)
    {
	double d0 = 0.0D;
	double d1 = 0.0D;
	double d2 = 0.0D;
	boolean flag = true;
	double d3 = 1.0D;

	for (int i = 0; i < 16; ++i)
	{
	    double d4 = OctavesNoiseGenerator.maintainPrecision((double) p_222552_1_ * p_222552_4_ * d3);
	    double d5 = OctavesNoiseGenerator.maintainPrecision((double) p_222552_2_ * p_222552_6_ * d3);
	    double d6 = OctavesNoiseGenerator.maintainPrecision((double) p_222552_3_ * p_222552_4_ * d3);
	    double d7 = p_222552_6_ * d3;
	    ImprovedNoiseGenerator improvednoisegenerator = this.field_222568_o.getOctave(i);
	    if (improvednoisegenerator != null)
	    {
		d0 += improvednoisegenerator.func_215456_a(d4, d5, d6, d7, (double) p_222552_2_ * d7) / d3;
	    }

	    ImprovedNoiseGenerator improvednoisegenerator1 = this.field_222569_p.getOctave(i);
	    if (improvednoisegenerator1 != null)
	    {
		d1 += improvednoisegenerator1.func_215456_a(d4, d5, d6, d7, (double) p_222552_2_ * d7) / d3;
	    }

	    if (i < 8)
	    {
		ImprovedNoiseGenerator improvednoisegenerator2 = this.field_222570_q.getOctave(i);
		if (improvednoisegenerator2 != null)
		{
		    d2 += improvednoisegenerator2.func_215456_a(OctavesNoiseGenerator.maintainPrecision((double) p_222552_1_ * p_222552_8_ * d3), OctavesNoiseGenerator.maintainPrecision((double) p_222552_2_ * p_222552_10_ * d3),
			    OctavesNoiseGenerator.maintainPrecision((double) p_222552_3_ * p_222552_8_ * d3), p_222552_10_ * d3, (double) p_222552_2_ * p_222552_10_ * d3) / d3;
		}
	    }

	    d3 /= 2.0D;
	}

	return MathHelper.clampedLerp(d0 / 512.0D, d1 / 512.0D, (d2 / 10.0D + 1.0D) / 2.0D);
    }

    private double[] func_222547_b(int p_222547_1_, int p_222547_2_)
    {
	double[] adouble = new double[this.noiseSizeY + 1];
	this.fillNoiseColumn(adouble, p_222547_1_, p_222547_2_);
	return adouble;
    }

    @SuppressWarnings("deprecation")
    private void fillNoiseColumn(double[] noiseColumn, int noiseX, int noiseZ)
    {
	NoiseSettings noisesettings = this.field_236080_h_.func_236113_b_();
	double d0;
	double d1;
	if (this.field_236083_v_ != null)
	{
	    d0 = (double) (EndBiomeProvider.func_235317_a_(this.field_236083_v_, noiseX, noiseZ) - 8.0F);
	    if (d0 > 0.0D)
	    {
		d1 = 0.25D;
	    }
	    else
	    {
		d1 = 1.0D;
	    }
	}
	else
	{
	    float f = 0.0F;
	    float f1 = 0.0F;
	    float f2 = 0.0F;
	    int i = 2;
	    int j = this.func_230356_f_();
	    float f3 = this.biomeProvider.getNoiseBiome(noiseX, j, noiseZ).getDepth();

	    for (int k = -2; k <= 2; ++k)
	    {
		for (int l = -2; l <= 2; ++l)
		{
		    Biome biome = this.biomeProvider.getNoiseBiome(noiseX + k, j, noiseZ + l);
		    float f4 = biome.getDepth();
		    float f5 = biome.getScale();
		    float f6;
		    float f7;
		    if (noisesettings.func_236181_l_() && f4 > 0.0F)
		    {
			f6 = 1.0F + f4 * 2.0F;
			f7 = 1.0F + f5 * 4.0F;
		    }
		    else
		    {
			f6 = f4;
			f7 = f5;
		    }

		    float f8 = f4 > f3 ? 0.5F : 1.0F;
		    float f9 = f8 * field_236081_j_[k + 2 + (l + 2) * 5] / (f6 + 2.0F);
		    f += f7 * f9;
		    f1 += f6 * f9;
		    f2 += f9;
		}
	    }

	    float f10 = f1 / f2;
	    float f11 = f / f2;
	    double d16 = (double) (f10 * 0.5F - 0.125F);
	    double d18 = (double) (f11 * 0.9F + 0.1F);
	    d0 = d16 * 0.265625D;
	    d1 = 96.0D / d18;
	}

	double d12 = 684.412D * noisesettings.func_236171_b_().func_236151_a_();
	double d13 = 684.412D * noisesettings.func_236171_b_().func_236153_b_();
	double d14 = d12 / noisesettings.func_236171_b_().func_236154_c_();
	double d15 = d13 / noisesettings.func_236171_b_().func_236155_d_();
	double d17 = (double) noisesettings.func_236172_c_().func_236186_a_();
	double d19 = (double) noisesettings.func_236172_c_().func_236188_b_();
	double d20 = (double) noisesettings.func_236172_c_().func_236189_c_();
	double d21 = (double) noisesettings.func_236173_d_().func_236186_a_();
	double d2 = (double) noisesettings.func_236173_d_().func_236188_b_();
	double d3 = (double) noisesettings.func_236173_d_().func_236189_c_();
	double d4 = noisesettings.func_236179_j_() ? this.func_236095_c_(noiseX, noiseZ) : 0.0D;
	double d5 = noisesettings.func_236176_g_();
	double d6 = noisesettings.func_236177_h_();

	for (int i1 = 0; i1 <= this.noiseSizeY; ++i1)
	{
	    double d7 = this.func_222552_a(noiseX, i1, noiseZ, d12, d13, d14, d15);
	    double d8 = 1.0D - (double) i1 * 2.0D / (double) this.noiseSizeY + d4;
	    double d9 = d8 * d5 + d6;
	    double d10 = (d9 + d0) * d1;
	    if (d10 > 0.0D)
	    {
		d7 = d7 + d10 * 4.0D;
	    }
	    else
	    {
		d7 = d7 + d10;
	    }

	    if (d19 > 0.0D)
	    {
		double d11 = ((double) (this.noiseSizeY - i1) - d20) / d19;
		d7 = MathHelper.clampedLerp(d17, d7, d11);
	    }

	    if (d2 > 0.0D)
	    {
		double d22 = ((double) i1 - d3) / d2;
		d7 = MathHelper.clampedLerp(d21, d7, d22);
	    }

	    noiseColumn[i1] = d7;
	}

    }

    private double func_236095_c_(int p_236095_1_, int p_236095_2_)
    {
	double d0 = this.field_236082_u_.getValue((double) (p_236095_1_ * 200), 10.0D, (double) (p_236095_2_ * 200), 1.0D, 0.0D, true);
	double d1;
	if (d0 < 0.0D)
	{
	    d1 = -d0 * 0.3D;
	}
	else
	{
	    d1 = d0;
	}

	double d2 = d1 * 24.575625D - 2.0D;
	return d2 < 0.0D ? d2 * 0.009486607142857142D : Math.min(d2, 1.0D) * 0.006640625D;
    }

    public int func_222529_a(int p_222529_1_, int p_222529_2_, Heightmap.Type heightmapType)
    {
	return this.func_236087_a_(p_222529_1_, p_222529_2_, (BlockState[]) null, heightmapType.getHeightLimitPredicate());
    }

    public IBlockReader func_230348_a_(int p_230348_1_, int p_230348_2_)
    {
	BlockState[] ablockstate = new BlockState[this.noiseSizeY * this.verticalNoiseGranularity];
	this.func_236087_a_(p_230348_1_, p_230348_2_, ablockstate, (Predicate<BlockState>) null);
	return new Blockreader(ablockstate);
    }

    private int func_236087_a_(int p_236087_1_, int p_236087_2_, @Nullable BlockState[] p_236087_3_, @Nullable Predicate<BlockState> p_236087_4_)
    {
	int i = Math.floorDiv(p_236087_1_, this.horizontalNoiseGranularity);
	int j = Math.floorDiv(p_236087_2_, this.horizontalNoiseGranularity);
	int k = Math.floorMod(p_236087_1_, this.horizontalNoiseGranularity);
	int l = Math.floorMod(p_236087_2_, this.horizontalNoiseGranularity);
	double d0 = (double) k / (double) this.horizontalNoiseGranularity;
	double d1 = (double) l / (double) this.horizontalNoiseGranularity;
	double[][] adouble = new double[][] { this.func_222547_b(i, j), this.func_222547_b(i, j + 1), this.func_222547_b(i + 1, j), this.func_222547_b(i + 1, j + 1) };

	for (int i1 = this.noiseSizeY - 1; i1 >= 0; --i1)
	{
	    double d2 = adouble[0][i1];
	    double d3 = adouble[1][i1];
	    double d4 = adouble[2][i1];
	    double d5 = adouble[3][i1];
	    double d6 = adouble[0][i1 + 1];
	    double d7 = adouble[1][i1 + 1];
	    double d8 = adouble[2][i1 + 1];
	    double d9 = adouble[3][i1 + 1];

	    for (int j1 = this.verticalNoiseGranularity - 1; j1 >= 0; --j1)
	    {
		double d10 = (double) j1 / (double) this.verticalNoiseGranularity;
		double d11 = MathHelper.lerp3(d10, d0, d1, d2, d6, d4, d8, d3, d7, d5, d9);
		int k1 = i1 * this.verticalNoiseGranularity + j1;
		BlockState blockstate = this.func_236086_a_(d11, k1);
		if (p_236087_3_ != null)
		{
		    p_236087_3_[k1] = blockstate;
		}

		if (p_236087_4_ != null && p_236087_4_.test(blockstate))
		{
		    return k1 + 1;
		}
	    }
	}

	return 0;
    }

    protected BlockState func_236086_a_(double p_236086_1_, int p_236086_3_)
    {
	BlockState blockstate;
	if (p_236086_1_ > 0.0D)
	{
	    blockstate = this.defaultBlock;
	}
	else if (p_236086_3_ < this.func_230356_f_())
	{
	    blockstate = this.defaultFluid;
	}
	else
	{
	    blockstate = AIR;
	}

	return blockstate;
    }

    public void generateSurface(WorldGenRegion p_225551_1_, IChunk p_225551_2_)
    {
	//this.makeBedrock(p_225551_2_, sharedseedrandom);
    }

    private void makeBedrock(IChunk chunkIn, Random rand)
    {
	// just a whole lot of this in loops, for example
	//chunkIn.setBlockState(blockpos$mutable.setPos(blockpos.getX(), l - j1, blockpos.getZ()), Blocks.BEDROCK.getDefaultState(), false);
    }

    public void func_230352_b_(IWorld p_230352_1_, StructureManager p_230352_2_, IChunk p_230352_3_)
    {
	// placing structures and handling jigsaws? nah
    }

    private static double func_222556_a(int p_222556_0_, int p_222556_1_, int p_222556_2_)
    {
	int i = p_222556_0_ + 12;
	int j = p_222556_1_ + 12;
	int k = p_222556_2_ + 12;
	if (i >= 0 && i < 24)
	{
	    if (j >= 0 && j < 24)
	    {
		return k >= 0 && k < 24 ? (double) field_222561_h[k * 24 * 24 + i * 24 + j] : 0.0D;
	    }
	    else
	    {
		return 0.0D;
	    }
	}
	else
	{
	    return 0.0D;
	}
    }

    private static double func_222554_b(int p_222554_0_, int p_222554_1_, int p_222554_2_)
    {
	double d0 = (double) (p_222554_0_ * p_222554_0_ + p_222554_2_ * p_222554_2_);
	double d1 = (double) p_222554_1_ + 0.5D;
	double d2 = d1 * d1;
	double d3 = Math.pow(Math.E, -(d2 / 16.0D + d0 / 16.0D));
	double d4 = -d1 * MathHelper.fastInvSqrt(d2 / 2.0D + d0 / 2.0D) / 2.0D;
	return d4 * d3;
    }

    public int func_230355_e_()
    {
	return this.field_236085_x_;
    }

    public int func_230356_f_()
    {
	return this.field_236080_h_.func_236119_g_();
    }

    public List<Biome.SpawnListEntry> func_230353_a_(Biome p_230353_1_, StructureManager p_230353_2_, EntityClassification p_230353_3_, BlockPos p_230353_4_)
    {
	if (p_230353_2_.func_235010_a_(p_230353_4_, true, Structure.field_236374_j_).isValid())
	{
	    if (p_230353_3_ == EntityClassification.MONSTER)
	    {
		return Structure.field_236374_j_.getSpawnList();
	    }

	    if (p_230353_3_ == EntityClassification.CREATURE)
	    {
		return Structure.field_236374_j_.getCreatureSpawnList();
	    }
	}

	if (p_230353_3_ == EntityClassification.MONSTER)
	{
	    if (p_230353_2_.func_235010_a_(p_230353_4_, false, Structure.field_236366_b_).isValid())
	    {
		return Structure.field_236366_b_.getSpawnList();
	    }

	    if (p_230353_2_.func_235010_a_(p_230353_4_, false, Structure.field_236376_l_).isValid())
	    {
		return Structure.field_236376_l_.getSpawnList();
	    }

	    if (p_230353_2_.func_235010_a_(p_230353_4_, true, Structure.field_236378_n_).isValid())
	    {
		return Structure.field_236378_n_.getSpawnList();
	    }
	}

	return super.func_230353_a_(p_230353_1_, p_230353_2_, p_230353_3_, p_230353_4_);
    }

    public void func_230354_a_(WorldGenRegion p_230354_1_)
    {
	// whatever this function is doing, I don't want it

	//WorldEntitySpawner.performWorldGenSpawning(p_230354_1_, biome, i, j, sharedseedrandom);
    }

    public void makeBase(IWorld worldIn, IChunk chunkIn)
    {
	// I still want a random seed, like the overworld, for use in structures
	Random randomSeed = worldIn.getRandom();

	// 1.14 seed changing logic
	int x = chunkIn.getPos().x;
	int z = chunkIn.getPos().z;
	//randomSeed.setSeed((worldSeed + (long) (x * x * 4987142) + (long) (x * 5947611) + (long) (z * z) * 4392871L + (long) (z * 389711) ^ worldSeed));

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
}