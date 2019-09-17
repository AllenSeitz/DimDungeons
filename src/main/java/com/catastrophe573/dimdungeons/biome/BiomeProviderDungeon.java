package com.catastrophe573.dimdungeons.biome;

import com.catastrophe573.dimdungeons.feature.BasicDungeonFeature;

import net.minecraft.world.biome.provider.SingleBiomeProvider;
import net.minecraft.world.biome.provider.SingleBiomeProviderSettings;
import net.minecraft.world.gen.feature.structure.Structure;

public class BiomeProviderDungeon extends SingleBiomeProvider
{
    public BiomeProviderDungeon()
    {
	super(new SingleBiomeProviderSettings().setBiome(BiomeRegistrar.biome_dungeon));
    }

    @Override
    public boolean hasStructure(Structure<?> structure)
    {
	return structure instanceof BasicDungeonFeature;
    }
}
