package com.catastrophe573.dimdungeons.feature;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.biome.BiomeRegistrar;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
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
    @ObjectHolder("dimdungeons:basic_dungeon")
    public static Structure<NoFeatureConfig> basic_dungeon_feature = null;

    @SubscribeEvent
    public static void onFeaturesRegistry(RegistryEvent.Register<Feature<?>> event)
    {
	DimDungeons.LOGGER.info("Registering features.");

	basic_dungeon_feature = Registry.register(Registry.STRUCTURE_FEATURE, "dimdungeons:basic_dungeon", new BasicDungeonFeature(NoFeatureConfig::deserialize));
    }

    @SubscribeEvent
    public static void applyFeatures(FMLCommonSetupEvent event)
    {
	DimDungeons.LOGGER.info("Applying features to biomes.");

	addStructure(BiomeRegistrar.biome_dungeon, GenerationStage.Decoration.SURFACE_STRUCTURES, basic_dungeon_feature);
    }

    // a helper function for applyFeatures(), copied from Laton95's mod Rune-Mysteries
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static void addStructure(Biome biome, GenerationStage.Decoration stage, Structure structure)
    {
	biome.addFeature(stage, Biome.createDecoratedFeature(structure, IFeatureConfig.NO_FEATURE_CONFIG, Placement.NOPE, IPlacementConfig.NO_PLACEMENT_CONFIG));	
	biome.addStructure(structure, IFeatureConfig.NO_FEATURE_CONFIG);
    }
}