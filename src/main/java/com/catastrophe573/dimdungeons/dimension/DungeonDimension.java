package com.catastrophe573.dimdungeons.dimension;

import javax.annotation.Nullable;

import com.mojang.datafixers.Dynamic;

import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.MusicTicker.MusicType;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.biome.provider.BiomeProviderType;
import net.minecraft.world.biome.provider.SingleBiomeProvider;
import net.minecraft.world.biome.provider.SingleBiomeProviderSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkGeneratorFlat;
import net.minecraft.world.gen.ChunkGeneratorType;
import net.minecraft.world.gen.FlatGenSettings;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.extensions.IForgeDimension;

public class DungeonDimension extends Dimension implements IForgeDimension
{
    @Override
    public DimensionType getType()
    {
	return DimensionRegistrar.dungeon_dimension_type;
    }

    /**
     * Called to determine if the chunk at the given chunk coordinates within the provider's world can be dropped. Used in
     * WorldProviderSurface to prevent spawn chunks from being unloaded.
     */
    @Override
    public boolean canDropChunk(int x, int z)
    {
	return true;
    }

    /**
     * Creates a new {@link BiomeProvider} for the WorldProvider, and also sets the values of {@link #hasSkylight} and
     * {@link #hasNoSky} appropriately. Note that subclasses generally override this method without calling the parent
     * version.
     */
    protected void init()
    {
	this.hasSkyLight = true;
    }

    // TODO: revisit this function, lines not relevant to flat worldgen deleted
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public IChunkGenerator<? extends IChunkGenSettings> createChunkGenerator()
    {
	ChunkGeneratorType<FlatGenSettings, ChunkGeneratorFlat> chunkgeneratortype = ChunkGeneratorType.FLAT;
	BiomeProviderType<SingleBiomeProviderSettings, SingleBiomeProvider> biomeprovidertype = BiomeProviderType.FIXED;

	FlatGenSettings flatgensettings = FlatGenSettings.createFlatGenerator(new Dynamic(NBTDynamicOps.INSTANCE, this.world.getWorldInfo().getGeneratorOptions()));
	SingleBiomeProviderSettings singlebiomeprovidersettings1 = ((SingleBiomeProviderSettings) biomeprovidertype.createSettings()).setBiome(flatgensettings.getBiome());
	return chunkgeneratortype.create(this.world, biomeprovidertype.create(singlebiomeprovidersettings1), flatgensettings);
    }

    @Override
    // no respawning in this dimension
    public BlockPos findSpawn(ChunkPos p_206920_1_, boolean checkValid)
    {
	return null;
    }

    @Override
    // no respawning in this dimension
    public BlockPos findSpawn(int p_206921_1_, int p_206921_2_, boolean checkValid)
    {
	return null;
    }

    /**
     * Calculates the angle of sun and moon in the sky relative to a specified time (usually worldTime)
     */
    // basically copied from vanilla OverworldDimension but with the current time locked to keep the sun out, because why not
    public float calculateCelestialAngle(long worldTime, float partialTicks)
    {
	//int i = (int) (worldTime % 24000L);
	int i = 10000;
	float f = ((float) i + partialTicks) / 24000.0F - 0.25F;
	if (f < 0.0F)
	{
	    ++f;
	}

	if (f > 1.0F)
	{
	    --f;
	}

	float f1 = 1.0F - (float) ((Math.cos((double) f * Math.PI) + 1.0D) / 2.0D);
	f = f + (f1 - f) / 3.0F;
	return f;
    }

    public boolean isSurfaceWorld()
    {
	return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    // hard crash without this function
    public Vec3d getSkyColor(Entity cameraEntity, float partialTicks)
    {
	return new Vec3d(0.82d, 0.82d, 0.99d);
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    public Vec3d getFogColor(float p_76562_1_, float p_76562_2_)
    {
	return new Vec3d(0.75d, 0.75d, 0.95d);
    }

    public boolean canRespawnHere()
    {
	return false;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean doesXZShowFog(int x, int z)
    {
	return false;
    }

    // from IForgeDimension
    public boolean canDoLightning(Chunk chunk)
    {
	return false;
    }

    // from IForgeDimension
    public boolean canDoRainSnowIce(Chunk chunk)
    {
	return false;
    }

    @Nullable
    @OnlyIn(Dist.CLIENT)
    // from IForgeDimension
    public MusicTicker.MusicType getMusicType()
    {
	return MusicType.GAME;
    }
    
    // from IForgeDimension
    public SleepResult canSleepAt(net.minecraft.entity.player.EntityPlayer player, BlockPos pos)
    {
        return SleepResult.DENY;
    }

    // from IForgeDimension
    // TODO: revisit this later
    public Biome getBiome(BlockPos pos)
    {
	return null;
    }   
    
    public boolean shouldMapSpin(String entity, double x, double z, double rotation)
    {
	return false;
    }
}