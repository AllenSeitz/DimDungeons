package com.catastrophe573.dimdungeons.feature;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.biome.BiomeRegistrar;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = DimDungeons.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class FeatureRegistrar
{    
    @ObjectHolder("dimdungeons:feature_basic_dungeon")
    public static Feature<NoFeatureConfig> feature_basic_dungeon = null;

    @SubscribeEvent
    public static void onFeaturesRegistry(RegistryEvent.Register<Feature<?>> event)
    {
	registerFeature(event, new BasicDungeonFeature(NoFeatureConfig::deserialize), BasicDungeonFeature.FEATURE_ID);
    }

    // a helper function for onFeaturesRegistry(), copied from Laton95's mod Rune-Mysteries
    private static void registerFeature(RegistryEvent.Register<Feature<?>> event, Feature<?> feature, String name)
    {
	feature.setRegistryName(DimDungeons.MOD_ID, name);
	event.getRegistry().register(feature);
    }

    @SubscribeEvent
    public static void applyFeatures(FMLCommonSetupEvent event)
    {
	addFeatureToBiome(BiomeRegistrar.biome_dungeon, GenerationStage.Decoration.SURFACE_STRUCTURES, feature_basic_dungeon);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static void addFeatureToBiome(Biome biome, GenerationStage.Decoration stage, Feature feature)
    {
	biome.addFeature(stage, Biome.createDecoratedFeature(feature, IFeatureConfig.NO_FEATURE_CONFIG, Placement.NOPE, IPlacementConfig.NO_PLACEMENT_CONFIG));
    }
}