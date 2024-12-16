package com.catastrophe573.dimdungeons.datagen;

import com.catastrophe573.dimdungeons.block.BlockRegistrar;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Set;

public class DimDungeonsLootTableBlockProvider extends BlockLootSubProvider
{
    public DimDungeonsLootTableBlockProvider(HolderLookup.Provider lookupProvider)
    {
        // The first parameter is a set of blocks we are creating loot tables for. Instead of hardcoding,
        // we use our block registry and just pass an empty set here.
        // The second parameter is the feature flag set, this will be the default flags
        // unless you are adding custom flags (which is beyond the scope of this article).
        super(Set.of(), FeatureFlags.DEFAULT_FLAGS, lookupProvider);
    }

    // The contents of this Iterable are used for validation.
    @Override
    protected @NotNull Iterable<Block> getKnownBlocks()
    {
        ArrayList<Block> blocksWithDrops = new ArrayList<>();

        blocksWithDrops.add(BlockRegistrar.BLOCK_PORTAL_KEYHOLE.get());
        blocksWithDrops.add(BlockRegistrar.BLOCK_GILDED_PORTAL.get());
        blocksWithDrops.add(BlockRegistrar.BLOCK_PORTAL_CROWN.get());
        blocksWithDrops.add(BlockRegistrar.BLOCK_CHARGER_FULL.get());
        blocksWithDrops.add(BlockRegistrar.BLOCK_CHARGER_USED.get());
        blocksWithDrops.add(BlockRegistrar.BLOCK_CHARGER_DAMAGED.get());

        return blocksWithDrops;
    }

    @Override
    protected void generate()
    {
        // Equivalent to calling add(MyBlocks.EXAMPLE_BLOCK.get(), createSingleItemTable(MyBlocks.EXAMPLE_BLOCK.get()));
        dropSelf(BlockRegistrar.BLOCK_PORTAL_KEYHOLE.get());
        dropSelf(BlockRegistrar.BLOCK_GILDED_PORTAL.get());
        dropSelf(BlockRegistrar.BLOCK_PORTAL_CROWN.get());
        dropSelf(BlockRegistrar.BLOCK_CHARGER_FULL.get());
        dropSelf(BlockRegistrar.BLOCK_CHARGER_USED.get());
        dropSelf(BlockRegistrar.BLOCK_CHARGER_DAMAGED.get());

        // Add a table with a silk touch only loot table.
        //add(BlockRegistrar.EXAMPLE_SILK_TOUCHABLE_BLOCK.get(), createSilkTouchOnlyTable(BlockRegistrar.EXAMPLE_SILK_TOUCHABLE_BLOCK.get()));
    }
}