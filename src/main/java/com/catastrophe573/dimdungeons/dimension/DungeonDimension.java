package com.catastrophe573.dimdungeons.dimension;

import java.util.function.Function;

import javax.annotation.Nullable;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.biome.BiomeProviderDungeon;
import com.catastrophe573.dimdungeons.command.CustomTeleporter;

import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.MusicTicker.MusicType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ModDimension;

public class DungeonDimension extends Dimension
{
    public static final ResourceLocation dimension_id = new ResourceLocation(DimDungeons.MOD_ID, "dimension_dungeon");
    private final DimensionType type;
    
    // a bit of boiler plate code for Forge, don't worry about it or use it for anything
    public static ModDimension newModDimension() {
        return new ModDimension() {
	    @Override
	    public Function<DimensionType, ? extends Dimension> getFactory()
	    {
		return DungeonDimension::new;
	    }
        }.setRegistryName(dimension_id);
    }    
        
    public DungeonDimension(DimensionType type)
    {
	DimDungeons.LOGGER.info("Someone constructed a dungeon instance with type: " + type.toString());
	
	this.type = type;
    }
    
    @Override
    public DimensionType getType()
    {
	DimDungeons.LOGGER.info("Someone asked for the DimDungeon type: " + type.toString());
	return type;
    }

    @Override
    // this is strictly required
    public Biome getBiome(BlockPos pos)
    {
	return new BiomeProviderDungeon().getBiome(pos, Biomes.DESERT);
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
	this.doesWaterVaporize = false;
	setAllowedSpawnTypes(false, false);
    }

    public IChunkGenerator<? extends IChunkGenSettings> createChunkGenerator()
    {
	return new DungeonChunkGenerator(this.world, new BiomeProviderDungeon());
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
    // hard crash without this function
    public Vec3d getSkyColor(Entity cameraEntity, float partialTicks)
    {
	return new Vec3d(0.82d, 0.82d, 0.99d);
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
    public SleepResult canSleepAt(net.minecraft.entity.player.EntityPlayer player, BlockPos pos)
    {
	return SleepResult.DENY;
    }

    public boolean shouldMapSpin(String entity, double x, double z, double rotation)
    {
	return false;
    }

    @Override
    // no block breaking in this dimension!
    public boolean canMineBlock(EntityPlayer player, BlockPos pos)
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
    public void evictPlayer(EntityPlayer player)
    {
	// does this player have a bed?
	BlockPos cc = player.getBedLocation(player.getSpawnDimension());
	if (cc != null)
	{
	    CustomTeleporter.teleportToDimension(player, player.getSpawnDimension(), cc.getX(), cc.getY(), cc.getZ());
	}
	else
	{
	    // otherwise just send them to overworld spawn
	    cc = world.getServer().getWorld(DimensionType.OVERWORLD).getSpawnPoint();
	    CustomTeleporter.teleportToDimension(player, DimensionType.OVERWORLD, cc.getX(), cc.getY() + 1, cc.getZ());
	}

	// print cryptic message
	TextComponentString message = new TextComponentString("It was all just a dream...");
	message.getStyle().setColor(TextFormatting.DARK_PURPLE).setBold(true);
	player.sendMessage(message);
    }
}