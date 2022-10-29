package com.catastrophe573.dimdungeons.dimension;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.catastrophe573.dimdungeons.structure.DungeonPlacement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.Climate.Sampler;
import net.minecraft.world.level.biome.FixedBiomeSource;

public final class DungeonChunkGenerator extends ChunkGenerator
{
	// copied from FlatLevelSource
	public static final Codec<FlatLevelSource> myCodec = RecordCodecBuilder.create((p_204551_) ->
	{
		return commonCodec(p_204551_).and(FlatLevelGeneratorSettings.CODEC.fieldOf("settings").forGetter(FlatLevelSource::settings)).apply(p_204551_,
		        p_204551_.stable(FlatLevelSource::new));
	});

	private final FlatLevelGeneratorSettings settings;

	public DungeonChunkGenerator(Registry<StructureSet> p_209099_, FlatLevelGeneratorSettings p_209100_)
	{
		super(p_209099_, p_209100_.structureOverrides(), new FixedBiomeSource(p_209100_.getBiomeFromSettings()), new FixedBiomeSource(p_209100_.getBiome()), 0L);
		this.settings = p_209100_;
	}

	protected Codec<? extends ChunkGenerator> codec()
	{
		return myCodec;
	}

	@OnlyIn(Dist.CLIENT)
	public ChunkGenerator withSeed(long p_230349_1_)
	{
		// worldSeed = p_230349_1_;
		return this;
	}

	public FlatLevelGeneratorSettings settings()
	{
		return this.settings;
	}

	// in vanilla this would build the flat worldgen layers, I think
	@Override
	public CompletableFuture<ChunkAccess> fillFromNoise(Executor p_187748_, Blender p_187749_, StructureFeatureManager p_187750_, ChunkAccess p_187751_)
	{
		// I don't know why this function isn't being called, but it doesn't really
		// matter
		makeBase(p_187751_);

		return CompletableFuture.completedFuture(p_187751_);
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

	// public void makeBase(LevelAccessor worldIn, ChunkAccess chunkIn)
	public void makeBase(ChunkAccess chunkIn)
	{
		int x = chunkIn.getPos().x;
		int z = chunkIn.getPos().z;

		// first generate a superflat world - sandstone where dungeons can appear, and
		// void otherwise
		if (DungeonPlacement.isDungeonChunk(x, z))
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
							// for debugging mostly but it also kind of looks good when you're in creative
							// mode
							if (DungeonPlacement.isEntranceChunk(x, z))
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
			// add barrier blocks to the void in case the player escapes (although these are
			// escapable, too)
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

	@Override
	public Sampler climateSampler()
	{
		// none of the new 1.18 worldgen applies to this pocket dimension
		return null;
	}

	@Override
	public void applyCarvers(WorldGenRegion p_187691_, long p_187692_, BiomeManager p_187693_, StructureFeatureManager p_187694_, ChunkAccess p_187695_,
	        GenerationStep.Carving p_187696_)
	{
		// intentionally do nothing!
	}

	// @Override
	// public void applyBiomeDecoration(WorldGenLevel p_187712_, ChunkAccess
	// p_187713_, StructureFeatureManager p_187714_)
	// {
	// intentionally do nothing!
	// }

	@Override
	// I do not know why this isn't being called in 1.18
	public void buildSurface(WorldGenRegion p_187697_, StructureFeatureManager p_187698_, ChunkAccess p_187699_)
	{
		// no decoration! no structures!
	}

	@Override
	public void createStructures(RegistryAccess p_62200_, StructureFeatureManager p_62201_, ChunkAccess p_62202_, StructureManager p_62203_, long p_62204_)
	{
		// intentionally do nothing!
	}

	@Override
	public void spawnOriginalMobs(WorldGenRegion p_62167_)
	{
		// not doing this either
	}

	@Override
	public int getMinY()
	{
		return -63;
	}

	@Override
	public int getGenDepth()
	{
		return 384;
	}

	@Override
	public int getSeaLevel()
	{
		return -64;
	}

	@Override
	public void addDebugScreenInfo(List<String> p_208054_, BlockPos p_208055_)
	{
		// this is required in 1.18.2
	}
}