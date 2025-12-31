package com.catastrophe573.dimdungeons.dimension;

import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.catastrophe573.dimdungeons.structure.DungeonPlacement;
import com.catastrophe573.dimdungeons.structure.DungeonRoom;
import com.catastrophe573.dimdungeons.utils.DungeonUtils;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.FixedBiomeSource;

public final class DungeonChunkGenerator extends ChunkGenerator
{
	// copied from FlatLevelSource
	public static final MapCodec<FlatLevelSource> CODEC = RecordCodecBuilder.mapCodec((p_255577_) ->
																					  {
																						  return p_255577_.group(FlatLevelGeneratorSettings.CODEC.fieldOf("settings").forGetter(FlatLevelSource::settings)).apply(p_255577_, p_255577_.stable(FlatLevelSource::new));
																					  });

	private final FlatLevelGeneratorSettings settings;

	public DungeonChunkGenerator(Registry<StructureSet> p_209099_, FlatLevelGeneratorSettings p_209100_)
	{
		super(new FixedBiomeSource(p_209100_.getBiome()));
		this.settings = p_209100_;
	}

	public MapCodec<? extends ChunkGenerator> codec()
	{
		return CODEC;
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

	@Override
	public void createStructures(RegistryAccess registryAccess, ChunkGeneratorStructureState structureState, StructureManager structureManager, ChunkAccess chunk, StructureTemplateManager structureTemplateManager, ResourceKey<Level> level)
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
	public void applyCarvers(WorldGenRegion level, long seed, RandomState random, BiomeManager biomeManager, StructureManager structureManager, ChunkAccess chunk)
	{
		// intentionally do nothing!
	}

	@Override
	public Pair<BlockPos, Holder<Structure>> findNearestMapStructure(ServerLevel level, HolderSet<Structure> structure, BlockPos pos, int searchRadius, boolean skipKnownStructures)
	{
		return null; // expected by vanilla in some circumstances
	}

	@Override
	public void applyBiomeDecoration(WorldGenLevel level, ChunkAccess chunk, StructureManager structureManager)
	{
		// intentionally do nothing!
	}

	@Override
	public void buildSurface(WorldGenRegion p_223050_, net.minecraft.world.level.StructureManager p_223051_, RandomState p_223052_, ChunkAccess p_223053_)
	{
		// intentionally do nothing!
	}

	@Override
	public CompletableFuture<ChunkAccess> fillFromNoise(Blender blender, RandomState randomState, StructureManager structureManager, ChunkAccess chunk)
	{
		return CompletableFuture.completedFuture(chunk);
	}

	@Override
	public int getBaseHeight(int x, int z, Types p_223034_, LevelHeightAccessor p_223035_, RandomState p_223036_)
	{
		return getBaseHeight(x, z, p_223034_);
	}

	@Override
	// copied this from FlatLevelSource just to have something that doesn't return null
	public NoiseColumn getBaseColumn(int p_223028_, int p_223029_, LevelHeightAccessor p_223030_, RandomState p_223031_)
	{
		return new NoiseColumn(p_223030_.getMinY(), this.settings.getLayers().stream().limit((long) p_223030_.getHeight()).map((p_204549_) ->
		{
			return p_204549_ == null ? Blocks.AIR.defaultBlockState() : p_204549_;
		}).toArray((p_204543_) ->
		{
			return new BlockState[p_204543_];
		}));
	}

	@Override
	// TODO: this is now possible in 1.21.9!!!
	public void addDebugScreenInfo(List<String> p_224304_, RandomState p_224305_, BlockPos p_224306_)
	{
		//ChunkPos cpos = new ChunkPos(p_224306_);
		//DungeonRoom room = DungeonData.get(DungeonUtils.getDungeonWorld()).getRoomAtPos(cpos); // this requires syncing data from the server to the client
		//p_224304_.add("Dungeon Room: " + room.structure);
	}
}