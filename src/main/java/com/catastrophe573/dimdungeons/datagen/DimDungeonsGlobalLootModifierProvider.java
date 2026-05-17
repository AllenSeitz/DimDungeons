package com.catastrophe573.dimdungeons.datagen;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.utils.LootModifierNoDrops;
import net.minecraft.advancements.criterion.LocationPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.storage.loot.predicates.LocationCheck;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;

import java.util.concurrent.CompletableFuture;

public class DimDungeonsGlobalLootModifierProvider extends GlobalLootModifierProvider
{
    public DimDungeonsGlobalLootModifierProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries)
    {
        super(output, registries, DimDungeons.MOD_ID);
    }

    @Override
    protected void start()
    {
        this.add(
                "no_dungeon_drops",
                new LootModifierNoDrops(new LootItemCondition[] {
                        LocationCheck.checkLocation(LocationPredicate.Builder.inDimension(DimDungeons.DUNGEON_DIMENSION)).build()
                }, 1)
        );
    }
}