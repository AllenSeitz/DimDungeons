package com.catastrophe573.dimdungeons.biome;

import net.minecraft.entity.EntityClassification;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

public class BiomeDungeon extends Biome
{
    public BiomeDungeon(Builder biomeBuilder)
    {
	super(biomeBuilder.surfaceBuilder(new ConfiguredSurfaceBuilder<SurfaceBuilderConfig>(SurfaceBuilder.NOPE, SurfaceBuilder.AIR_CONFIG)).precipitation(Biome.RainType.NONE).category(Biome.Category.NONE).depth(0.0F).scale(0.025F).temperature(0.8F).downfall(0.4F).waterColor(0xFFFF66).waterFogColor(329011).parent((String)null));

	// this dimension spawns no monsters or entities
	this.getSpawns(EntityClassification.AMBIENT).clear();
	this.getSpawns(EntityClassification.CREATURE).clear();
	this.getSpawns(EntityClassification.MONSTER).clear();
	this.getSpawns(EntityClassification.WATER_CREATURE).clear();
    }

    @Override
    public float getSpawningChance()
    {
	return 0; // nope not in this dimension
    }
    
    @Override
    public String getTranslationKey()
    {
	return "biome.dimdungeons.dungeon";
    }
}