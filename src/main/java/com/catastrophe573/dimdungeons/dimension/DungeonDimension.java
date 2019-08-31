package com.catastrophe573.dimdungeons.dimension;

import javax.annotation.Nullable;

import com.catastrophe573.dimdungeons.biome.BiomeProviderDungeon;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.ChunkGeneratorType;
import net.minecraft.world.gen.FlatGenerationSettings;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class DungeonDimension extends Dimension
{
    public DungeonDimension(World worldIn, DimensionType typeIn)
    {
	super(worldIn, typeIn);
    }

    public ChunkGenerator<? extends GenerationSettings> createChunkGenerator()
    {
	BiomeProviderDungeon biomeProvider = new BiomeProviderDungeon();	
	ChunkGeneratorType<FlatGenerationSettings, DungeonChunkGenerator> gen = new ChunkGeneratorType<>(DungeonChunkGenerator::new, true, FlatGenerationSettings::new);
	FlatGenerationSettings gensettings = gen.createSettings();
	return gen.create(this.world, biomeProvider, gensettings);
    }

    @Override
    // no block breaking in this dimension!
    public boolean canMineBlock(PlayerEntity player, BlockPos pos)
    {
	return false;
    }

    @Nullable
    // no respawning in this dimension
    public BlockPos findSpawn(int posX, int posZ, boolean checkValid)
    {
	return null;
    }

    @Nullable
    // no respawning in this dimension
    public BlockPos findSpawn(ChunkPos chunkPosIn, boolean checkValid)
    {
	return null;
    }

    @Override
    // no respawning in this dimension, go back to your bed
    public boolean canRespawnHere()
    {
	return false;
    }

    // basically copied from vanilla OverworldDimension but with the current time locked to keep the sun at midday, because why not
    public float calculateCelestialAngle(long worldTime, float partialTicks)
    {
	double d0 = MathHelper.frac((double) worldTime / 24000.0D - 0.25D);
	double d1 = 0.5D - Math.cos(d0 * Math.PI) / 2.0D;
	return (float) (d0 * 2.0D + d1) / 3.0F;
    }

    @Override
    public boolean hasSkyLight()
    {
	return true;
    }

    @Override
    // return true if this is the overworld and false for extra dimensions?
    public boolean isSurfaceWorld()
    {
	return false;
    }

    @Override
    // return true if this is the vanilla nether and false for custom dimensions?
    public boolean isNether()
    {
	return false;
    }

    @Override
    // oh the possibilities
    public boolean doesWaterVaporize()
    {
	return false;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    // the sky is further customizable with other functions not implemented in this class
    public boolean isSkyColored()
    {
	return true;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    // no fog in this dimension
    public boolean doesXZShowFog(int x, int z)
    {
	return false;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    // implementing this function is required in some versions of Forge even if the previous function returns false
    public Vec3d getFogColor(float celestialAngle, float partialTicks)
    {
	return new Vec3d(0.75d, 0.75d, 0.95d);
    }

    @Override
    // basically get rid of the clouds
    public float getCloudHeight()
    {
	return 199.0f;
    }
}