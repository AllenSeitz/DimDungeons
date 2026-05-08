package com.catastrophe573.dimdungeons.datagen;

import com.catastrophe573.dimdungeons.block.BlockRegistrar;
import com.catastrophe573.dimdungeons.item.ItemRegistrar;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.util.concurrent.CompletableFuture;

public class DimDungeonsRecipeProvider extends RecipeProvider
{
    protected DimDungeonsRecipeProvider(HolderLookup.Provider provider, RecipeOutput output)
    {
        super(provider, output);
    }

    @Override
    protected void buildRecipes()
    {
        TagKey<Item> gold_ingots = ItemTags.create(Identifier.fromNamespaceAndPath("c","ingots/gold"));
        TagKey<Item> trapped_chests = ItemTags.create(Identifier.fromNamespaceAndPath("c","chests/trapped"));

        // gilded portal
        shaped(RecipeCategory.MISC, BlockRegistrar.BLOCK_GILDED_PORTAL.get())
                .pattern("GGG")
                .pattern("GSG")
                .pattern("GGG")
                .define('G', gold_ingots)
                .define('S', Items.CHISELED_STONE_BRICKS)
                .unlockedBy("has_item", has(Items.CHISELED_STONE_BRICKS))
                .showNotification(false)
                .save(output);

        // keyhole
        shapeless(RecipeCategory.MISC, BlockRegistrar.BLOCK_PORTAL_KEYHOLE.get())
                .requires(ItemRegistrar.ITEM_GILDED_PORTAL)
                .requires(Items.ENDER_EYE)
                .requires(trapped_chests)
                .unlockedBy("has_item", has(ItemRegistrar.ITEM_GILDED_PORTAL))
                .save(output);

        // blank portal key
        shapeless(RecipeCategory.MISC, ItemRegistrar.ITEM_PORTAL_KEY.get())
                .requires(Items.NAME_TAG)
                .requires(Items.BLAZE_ROD)
                .unlockedBy("has_item", has(Items.BLAZE_ROD))
                .save(output);

        // homeward pearl
        shapeless(RecipeCategory.MISC, ItemRegistrar.ITEM_HOMEWARD_PEARL.get())
                .requires(ItemRegistrar.ITEM_PORTAL_KEY)
                .requires(Items.ENDER_PEARL)
                .requires(Items.EXPERIENCE_BOTTLE)
                .unlockedBy("has_item", has(ItemRegistrar.ITEM_PORTAL_KEY))
                .save(output);

        // blank build key
        shaped(RecipeCategory.MISC, ItemRegistrar.ITEM_BLANK_BUILD_KEY.get())
                .pattern("OHO")
                .pattern("OKO")
                .pattern("OGO")
                .define('O', Items.ENDER_PEARL)
                .define('H', ItemRegistrar.ITEM_HOMEWARD_PEARL)
                .define('K', Items.ECHO_SHARD)
                .define('G', Items.GRASS_BLOCK)
                .unlockedBy("has_item", has(Items.ECHO_SHARD))
                .showNotification(false)
                .save(output);
    }

    // The runner to add to the data generator
    public static class Runner extends RecipeProvider.Runner
    {
        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider)
        {
            super(output, lookupProvider);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput output)
        {
            return new DimDungeonsRecipeProvider(provider, output);
        }

        @Override
        public String getName()
        {
            return "DimDungeons Recipes";
        }
    }
}