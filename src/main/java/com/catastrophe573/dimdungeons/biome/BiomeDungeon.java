package com.catastrophe573.dimdungeons.biome;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.surfacebuilders.CompositeSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

public class BiomeDungeon extends Biome
{
    public BiomeDungeon(Biome.BiomeBuilder biomeBuilder)
    {
	super(biomeBuilder.surfaceBuilder(new CompositeSurfaceBuilder<SurfaceBuilderConfig>(NOOP_SURFACE_BUILDER, AIR_SURFACE)).precipitation(Biome.RainType.NONE).category(Biome.Category.NONE).depth(0.0F).scale(0.025F).temperature(0.8F).downfall(0.4F).waterColor(0xFFFF66).waterFogColor(329011).parent((String)null));

	// this dimension spawns no monsters or entities
	this.getSpawns(EnumCreatureType.AMBIENT).clear();
	this.getSpawns(EnumCreatureType.CREATURE).clear();
	this.getSpawns(EnumCreatureType.MONSTER).clear();
	this.getSpawns(EnumCreatureType.WATER_CREATURE).clear();
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