package com.catastrophe573.dimdungeons.biome;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.entity.EntityClassification;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeAmbience;
import net.minecraft.world.biome.MoodSoundAmbience;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

public class BiomeDungeon extends Biome
{
    public BiomeDungeon(Builder biomeBuilder)
    {
	// func_235246_b() is waterColor()
	// func_235248_c_() is waterFogColor()
	// my intended waterColor is 0x676767
	// my intended waterFogColor is 329011
	// not sure what func_235239_a_() is but I set it anyway, probably fog color
	// not sure what func_235243_a_() is but it gets the MoodSoundAmbience from the VOID biome so w/e
	// also not sure what calling func_235238_a_() does besides throw an exception if any values are left blank
	super(biomeBuilder.surfaceBuilder(new ConfiguredSurfaceBuilder<SurfaceBuilderConfig>(SurfaceBuilder.DEFAULT, SurfaceBuilder.AIR_CONFIG)).precipitation(Biome.RainType.RAIN).category(Biome.Category.NONE).depth(0.0F).scale(0.025F).temperature(0.8F)
		.downfall(0.4F).func_235097_a_((new BiomeAmbience.Builder()).func_235246_b_(0x676767).func_235248_c_(329011).func_235239_a_(12638463).func_235243_a_(MoodSoundAmbience.field_235027_b_).func_235238_a_()).parent((String) null));

        //this.myPersonalAddFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, BasicDungeonFeature);
        //this.myPersonalAddFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, AdvancedDungeonFeature);
        //this.setRegistryName("biome_dungeon");	
	
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
    public List<Biome.SpawnListEntry> getSpawns(EntityClassification creatureType)
    {
	return Lists.newArrayList();
    }

    @Override
    public List<ConfiguredFeature<?, ?>> getFeatures(GenerationStage.Decoration decorationStage)
    {
	return Lists.newArrayList();
    }
    
    @Override
    public List<ConfiguredCarver<?>> getCarvers(GenerationStage.Carving stage)
    {
	return Lists.newArrayList();
    }    
    
    @Override
    public void addFeature(GenerationStage.Decoration decorationStage, ConfiguredFeature<?, ?> featureIn)
    {
	// no you can't add features to this biome, really, trust me, this isn't like normal world gen
    }
    
    // because the previous function was removed, this is the alternative
    public void myPersonalAddFeature(GenerationStage.Decoration decorationStage, ConfiguredFeature<?, ?> featureIn)
    {
	super.addFeature(decorationStage, featureIn);
    }    
}