package com.catastrophe573.dimdungeons.biome;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.Builder;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.ObjectHolder;

public class BiomeRegistrar
{
    @ObjectHolder("dimdungeons:biome_dungeon") public static Biome biome_dungeon;    
    
    public static void registerAllBiomes(RegistryEvent.Register<Biome> event)
    {
	event.getRegistry().register(new BiomeDungeon(new Builder()).setRegistryName("biome_dungeon"));
    }
}