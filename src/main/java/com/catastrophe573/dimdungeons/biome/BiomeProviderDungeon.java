package com.catastrophe573.dimdungeons.biome;

import net.minecraft.world.biome.provider.SingleBiomeProvider;
import net.minecraft.world.biome.provider.SingleBiomeProviderSettings;

public class BiomeProviderDungeon extends SingleBiomeProvider
{
    public BiomeProviderDungeon()
    {
	super(new SingleBiomeProviderSettings().setBiome(BiomeRegistrar.biome_dungeon));
    }   
}
