package com.catastrophe573.dimdungeons.dimension;

import javax.annotation.Nullable;

import com.catastrophe573.dimdungeons.biome.BiomeProviderDungeon;
import com.catastrophe573.dimdungeons.block.BlockRegistrar;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.ChunkGeneratorType;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.OverworldGenSettings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class DungeonDimension extends Dimension
{
    public DungeonDimension(World worldIn, DimensionType typeIn)
    {
    	super(worldIn, typeIn, 0); // the third parameter is new in 1.15? It is unnamed but seems to have something to do with time/celestial angle
    }

    public ChunkGenerator<? extends GenerationSettings> createChunkGenerator()
    {
	BiomeProviderDungeon biomeProvider = new BiomeProviderDungeon();
	ChunkGeneratorType<OverworldGenSettings, DungeonChunkGenerator> gen = new ChunkGeneratorType<>(DungeonChunkGenerator::new, true, OverworldGenSettings::new);
	OverworldGenSettings gensettings = new OverworldGenSettings();
	return gen.create(this.world, biomeProvider, gensettings);
    }

    @Nullable
    @OnlyIn(Dist.CLIENT)
    public MusicTicker.MusicType getMusicType()
    {
	return MusicTicker.MusicType.GAME;
    }

    public SleepResult canSleepAt(PlayerEntity player, BlockPos pos)
    {
	return SleepResult.DENY;
    }

    public boolean shouldMapSpin(String entity, double x, double z, double rotation)
    {
	return false;
    }

    public DimensionType getRespawnDimension(ServerPlayerEntity player)
    {
	return player.getSpawnDimension();
    }

    @Override
    // no block breaking in this dimension!
    public boolean canMineBlock(PlayerEntity player, BlockPos pos)
    {
	Block block = getWorld().getBlockState(pos).getBlock();

	// okay, except some interactable blocks need to return true so that they can be interacted with
	if (block.isIn(BlockTags.WOODEN_DOORS) || block.isIn(BlockTags.WOODEN_TRAPDOORS))
	{
	    return true;
	}
	if (block == Blocks.LEVER || block.isIn(BlockTags.BUTTONS) || block == Blocks.CAULDRON)
	{
	    return true;
	}
	if (block == Blocks.CHEST || block == Blocks.TRAPPED_CHEST || block == Blocks.BARREL)
	{
	    return true;
	}
	if (block == BlockRegistrar.block_portal_keyhole)
	{
	    return true;
	}
	if (block.getRegistryName().toString().contains("gravestone")) // GraveStone Mod by henkelmax, also catches "gravestone_lite"
	{
	    return true;
	}
	if (block.getRegistryName().toString().contains("gravestone_lite")) // a second version of the above mod by the same author
	{
	    return true;
	}
	if (block.getRegistryName().toString().contains("tombstone")) // Corail Tombstone, allow players to interact with and break it
	{
	    return true;
	}
	
	/* TODO: someday make this a config option
	if (player.hasPermissionLevel(2))
	{
	    return true;
	}
	//*/

	//return getWorld().canMineBlockBody(player, pos);
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

    // copied from vanilla OverworldDimension because it is required
    public float calculateCelestialAngle(long worldTime, float partialTicks)
    {
	// intentionally lock the time at midday
	//double d0 = MathHelper.frac((double) worldTime / 24000.0D - 0.25D);
	double d0 = MathHelper.frac(1000.0D / 24000.0D - 0.25D);
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