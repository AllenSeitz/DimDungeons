package com.catastrophe573.dimdungeons.dimension;

import java.util.function.BiFunction;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.biome.BiomeProviderDungeon;

import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.MusicTicker.MusicType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.biome.provider.BiomeProviderType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.ChunkGeneratorType;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ModDimension;

public class DungeonDimension extends Dimension
{
    public static final ResourceLocation dimension_id = new ResourceLocation(DimDungeons.MOD_ID, "dimension_dungeon");

    private DimensionType mySavedDimType; // remembered from the constructor and returned on demand

    public DungeonDimension(World worldIn, DimensionType typeIn)
    {
	super(worldIn, typeIn);
    }

    @Override
    public DimensionType getType()
    {
	DimDungeons.LOGGER.info("Someone asked for the DimDungeon type: " + mySavedDimType.toString());
	return mySavedDimType;
    }

    @Override
    // this is strictly required
    public Biome getBiome(BlockPos pos)
    {
	return new BiomeProviderDungeon().getBiome(pos);
    }

    public ChunkGenerator<? extends GenerationSettings> createChunkGenerator()
    {
	//return new DungeonChunkGenerator(this.world, new BiomeProviderDungeon());
	BiomeProviderType<AMBiomeProviderSettings, BiomeProviderDungeon> biomeprovidertype1 = new BiomeProviderType<>(BiomeProviderDungeon::new, AMBiomeProviderSettings::new);
	AMBiomeProviderSettings biomeprovidersettings1 = biomeprovidertype1.createSettings().setGeneratorSettings(new AMGenSettings()).setWorldInfo(this.world.getWorldInfo());
	BiomeProviderDungeon biomeprovider = biomeprovidertype1.create(biomeprovidersettings1);

	ChunkGeneratorType<AMGenSettings, DungeonChunkGenerator> chunkgeneratortype4 = new ChunkGeneratorType<>(DungeonChunkGenerator::new, true, AMGenSettings::new);
	AMGenSettings gensettings1 = chunkgeneratortype4.createSettings();
	gensettings1.setDefaultFluid(Blocks.LAVA.getDefaultState());
	return chunkgeneratortype4.create(this.world, biomeprovider, gensettings1);
    }

    @Nullable
    // no respawning in this dimension
    public BlockPos findSpawn(ChunkPos chunkPosIn, boolean checkValid)
    {
	return null;
    }

    @Nullable
    public BlockPos findSpawn(int posX, int posZ, boolean checkValid)
    {
	return null;
    }

    // basically copied from vanilla OverworldDimension but with the current time locked to keep the sun out, because why not
    // also required by Forge for some reason
    public float calculateCelestialAngle(long worldTime, float partialTicks)
    {
	// this is where I lock the angle of the sun
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
    // this is here for safety
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
    public SleepResult canSleepAt(PlayerEntity player, BlockPos pos)
    {
	return SleepResult.DENY;
    }

    public boolean shouldMapSpin(String entity, double x, double z, double rotation)
    {
	return false;
    }

    @Override
    // no block breaking in this dimension!
    public boolean canMineBlock(PlayerEntity player, BlockPos pos)
    {
	return false;
    }

    @Override
    public float getCloudHeight()
    {
	return 199.0f;
    }

    // sends a player back to their spawn point
    // currently unused
    public void evictPlayer(PlayerEntity player)
    {
	// does this player have a bed?
	BlockPos cc = player.getBedLocation(player.getSpawnDimension());
	if (cc != null)
	{
	    //CustomTeleporter.teleportToDimension(player, player.getSpawnDimension(), cc.getX(), cc.getY(), cc.getZ());
	    player.changeDimension(player.getSpawnDimension());
	    player.setPosition(cc.getX(), cc.getY(), cc.getZ());
	}
	else
	{
	    // otherwise just send them to overworld spawn
	    cc = world.getServer().getWorld(DimensionType.OVERWORLD).getSpawnPoint();
	    //CustomTeleporter.teleportToDimension(player, DimensionType.OVERWORLD, cc.getX(), cc.getY() + 1, cc.getZ());
	    player.changeDimension(player.getSpawnDimension());
	    player.setPosition(cc.getX(), cc.getY() + 1, cc.getZ());
	}

	// print cryptic message
	StringTextComponent message = new StringTextComponent("It was all just a dream...");
	message.getStyle().setColor(TextFormatting.DARK_PURPLE).setBold(true);
	player.sendMessage(message);
    }
}