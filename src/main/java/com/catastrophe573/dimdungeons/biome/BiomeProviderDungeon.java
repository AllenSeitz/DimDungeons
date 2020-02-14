package com.catastrophe573.dimdungeons.biome;

import net.minecraft.world.biome.provider.SingleBiomeProvider;
import net.minecraft.world.biome.provider.SingleBiomeProviderSettings;

public class BiomeProviderDungeon extends SingleBiomeProvider
{
	public BiomeProviderDungeon()
	{
		// new in 1.15 - passing null here seems okay here because the constructor for this class is just { }
		super(new SingleBiomeProviderSettings(null).setBiome(BiomeRegistrar.biome_dungeon));
	}
}
