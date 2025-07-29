package com.catastrophe573.dimdungeons.datagen;

import com.catastrophe573.dimdungeons.DimDungeons;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = DimDungeons.MOD_ID)
public class DataGenerators
{
    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client  event)
    {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        // one provider for each data generator type. this one is for loot tables
        generator.addProvider(event.includeDev(), new DimDungeonsLootTableProvider(packOutput,
            Collections.emptySet(),
                List.of(new LootTableProvider.SubProviderEntry(DimDungeonsLootTableChestProvider::new, LootContextParamSets.CHEST),
                        new LootTableProvider.SubProviderEntry(DimDungeonsLootTableBlockProvider::new, LootContextParamSets.BLOCK)),
            lookupProvider));

    }
}