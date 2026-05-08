package com.catastrophe573.dimdungeons.datagen;


import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.item.ItemRegistrar;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.SelectorPattern;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.functions.*;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.Optional;
import java.util.function.BiConsumer;

public class DimDungeonsLootTableChestProvider implements LootTableSubProvider
{
    HolderLookup.Provider registries;

    public DimDungeonsLootTableChestProvider(HolderLookup.Provider lookupProvider)
    {
        super();
        registries = lookupProvider;
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> biConsumer)
    {
        HolderLookup.RegistryLookup<Enchantment> enchants = this.registries.lookupOrThrow(Registries.ENCHANTMENT);

        // kit_glass
        biConsumer.accept(ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(DimDungeons.MOD_ID, "chest/kit_glass")), LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(27)).name("dimdungeons:kit_glass").
                        add(LootItem.lootTableItem(Items.TINTED_GLASS).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.WHITE_STAINED_GLASS).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.ORANGE_STAINED_GLASS).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.MAGENTA_STAINED_GLASS).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.LIGHT_BLUE_STAINED_GLASS).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.YELLOW_STAINED_GLASS).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.LIME_STAINED_GLASS).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.PINK_STAINED_GLASS).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.GRAY_STAINED_GLASS).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.LIGHT_GRAY_STAINED_GLASS).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.CYAN_STAINED_GLASS).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.PURPLE_STAINED_GLASS).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.BLUE_STAINED_GLASS).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.BROWN_STAINED_GLASS).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.GREEN_STAINED_GLASS).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.RED_STAINED_GLASS).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.BLACK_STAINED_GLASS).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.WHITE_STAINED_GLASS_PANE).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.ORANGE_STAINED_GLASS_PANE).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.MAGENTA_STAINED_GLASS_PANE).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.LIGHT_BLUE_STAINED_GLASS_PANE).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.YELLOW_STAINED_GLASS_PANE).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.LIME_STAINED_GLASS_PANE).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.PINK_STAINED_GLASS_PANE).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.GRAY_STAINED_GLASS_PANE).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.LIGHT_GRAY_STAINED_GLASS_PANE).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.CYAN_STAINED_GLASS_PANE).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.PURPLE_STAINED_GLASS_PANE).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.BLUE_STAINED_GLASS_PANE).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.BROWN_STAINED_GLASS_PANE).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.GREEN_STAINED_GLASS_PANE).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.RED_STAINED_GLASS_PANE).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.BLACK_STAINED_GLASS_PANE).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9))))
            )
        );

        // kit_concrete
        biConsumer.accept(ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(DimDungeons.MOD_ID, "chest/kit_concrete")), LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(27)).name("dimdungeons:kit_concrete").
                        add(LootItem.lootTableItem(Items.WHITE_CONCRETE).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.ORANGE_CONCRETE).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.MAGENTA_CONCRETE).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.LIGHT_BLUE_CONCRETE).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.YELLOW_CONCRETE).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.LIME_CONCRETE).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.PINK_CONCRETE).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.GRAY_CONCRETE).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.LIGHT_GRAY_CONCRETE).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.CYAN_CONCRETE).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.PURPLE_CONCRETE).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.BLUE_CONCRETE).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.BROWN_CONCRETE).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.GREEN_CONCRETE).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.RED_CONCRETE).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.BLACK_CONCRETE).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.WHITE_TERRACOTTA).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.ORANGE_TERRACOTTA).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.MAGENTA_TERRACOTTA).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.LIGHT_BLUE_TERRACOTTA).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.YELLOW_TERRACOTTA).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.LIME_TERRACOTTA).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.PINK_TERRACOTTA).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.GRAY_TERRACOTTA).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.LIGHT_GRAY_TERRACOTTA).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.CYAN_TERRACOTTA).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.PURPLE_TERRACOTTA).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.BLUE_TERRACOTTA).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.BROWN_TERRACOTTA).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.GREEN_TERRACOTTA).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.RED_TERRACOTTA).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.BLACK_TERRACOTTA).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9))))
                )
        );

        // kit_sherd, used by kit_pottery
        biConsumer.accept(ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(DimDungeons.MOD_ID, "chest/kit_sherd")), LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).name("dimdungeons:kit_sherd").
                        add(LootItem.lootTableItem(Items.ANGLER_POTTERY_SHERD)).
                        add(LootItem.lootTableItem(Items.ARCHER_POTTERY_SHERD)).
                        add(LootItem.lootTableItem(Items.ARMS_UP_POTTERY_SHERD)).
                        add(LootItem.lootTableItem(Items.BLADE_POTTERY_SHERD)).
                        add(LootItem.lootTableItem(Items.BREWER_POTTERY_SHERD)).
                        add(LootItem.lootTableItem(Items.BURN_POTTERY_SHERD)).
                        add(LootItem.lootTableItem(Items.DANGER_POTTERY_SHERD)).
                        add(LootItem.lootTableItem(Items.EXPLORER_POTTERY_SHERD)).
                        add(LootItem.lootTableItem(Items.FLOW_POTTERY_SHERD)).
                        add(LootItem.lootTableItem(Items.FRIEND_POTTERY_SHERD)).
                        add(LootItem.lootTableItem(Items.GUSTER_POTTERY_SHERD)).
                        add(LootItem.lootTableItem(Items.HEART_POTTERY_SHERD)).
                        add(LootItem.lootTableItem(Items.HEARTBREAK_POTTERY_SHERD)).
                        add(LootItem.lootTableItem(Items.HOWL_POTTERY_SHERD)).
                        add(LootItem.lootTableItem(Items.MINER_POTTERY_SHERD)).
                        add(LootItem.lootTableItem(Items.MOURNER_POTTERY_SHERD)).
                        add(LootItem.lootTableItem(Items.PRIZE_POTTERY_SHERD)).
                        add(LootItem.lootTableItem(Items.PLENTY_POTTERY_SHERD)).
                        add(LootItem.lootTableItem(Items.SCRAPE_POTTERY_SHERD)).
                        add(LootItem.lootTableItem(Items.SHEAF_POTTERY_SHERD)).
                        add(LootItem.lootTableItem(Items.SHELTER_POTTERY_SHERD)).
                        add(LootItem.lootTableItem(Items.SKULL_POTTERY_SHERD)).
                        add(LootItem.lootTableItem(Items.SNORT_POTTERY_SHERD))
                )
        );

        // kit_pottery
        biConsumer.accept(ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(DimDungeons.MOD_ID, "chest/kit_pottery")), LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(27)).name("dimdungeons:kit_pottery").
                        add(LootItem.lootTableItem(Items.BRICK).setWeight(30).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.CLAY_BALL).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.CLAY).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4)))).
                        add(LootItem.lootTableItem(Items.FLOWER_POT).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.DECORATED_POT).setWeight(10).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4)))).
                        add(LootItem.lootTableItem(Items.FURNACE).setWeight(10).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 1)))).
                        add(NestedLootTable.lootTableReference(ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(DimDungeons.MOD_ID, "chest/kit_sherd"))).setWeight(10))
                )
        );

        // kit_food
        biConsumer.accept(ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(DimDungeons.MOD_ID, "chest/kit_food")), LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(27)).name("dimdungeons:kit_food").
                        add(LootItem.lootTableItem(Items.BREAD).setWeight(3).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 1)))).
                        add(LootItem.lootTableItem(Items.GLOW_BERRIES).setWeight(3).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 1)))).
                        add(LootItem.lootTableItem(Items.HONEY_BOTTLE).setWeight(3).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 1)))).
                        add(LootItem.lootTableItem(Items.APPLE).setWeight(5).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 1)))).
                        add(LootItem.lootTableItem(Items.COOKIE).setWeight(2).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 1)))).
                        add(LootItem.lootTableItem(Items.COOKED_BEEF).setWeight(10).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 1)))).
                        add(LootItem.lootTableItem(Items.CARROT).setWeight(4).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 1)))).
                        add(LootItem.lootTableItem(Items.SWEET_BERRIES).setWeight(4).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 1)))).
                        add(LootItem.lootTableItem(Items.COOKED_COD).setWeight(4).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 1)))).
                        add(LootItem.lootTableItem(Items.COOKED_MUTTON).setWeight(4).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 1)))).
                        add(LootItem.lootTableItem(Items.BAKED_POTATO).setWeight(3).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 1)))).
                        add(LootItem.lootTableItem(Items.PUMPKIN_PIE).setWeight(2).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 1)))).
                        add(LootItem.lootTableItem(Items.RABBIT_STEW).setWeight(2).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 1))))
                )
        );

        // kit_wool
        biConsumer.accept(ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(DimDungeons.MOD_ID, "chest/kit_wool")), LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(27)).name("dimdungeons:kit_wool").
                        add(LootItem.lootTableItem(Items.WHITE_WOOL).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.ORANGE_WOOL).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.MAGENTA_WOOL).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.LIGHT_BLUE_WOOL).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.YELLOW_WOOL).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.LIME_WOOL).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.PINK_WOOL).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.GRAY_WOOL).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.LIGHT_GRAY_WOOL).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.CYAN_WOOL).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.PURPLE_WOOL).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.BLUE_WOOL).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.BROWN_WOOL).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.GREEN_WOOL).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.RED_WOOL).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.BLACK_WOOL).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.WHITE_BANNER).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.ORANGE_BANNER).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.MAGENTA_BANNER).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.LIGHT_BLUE_BANNER).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.YELLOW_BANNER).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.LIME_BANNER).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.PINK_BANNER).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.GRAY_BANNER).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.LIGHT_GRAY_BANNER).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.CYAN_BANNER).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.PURPLE_BANNER).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.BLUE_BANNER).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.BROWN_BANNER).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.GREEN_BANNER).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.RED_BANNER).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9)))).
                        add(LootItem.lootTableItem(Items.BLACK_BANNER).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 9))))
                )
        );

        // kit_redstone
        biConsumer.accept(ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(DimDungeons.MOD_ID, "chest/kit_redstone")), LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(27)).name("dimdungeons:kit_redstone").
                        add(LootItem.lootTableItem(Items.PISTON).setWeight(1).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 1)))).
                        add(LootItem.lootTableItem(Items.STICKY_PISTON).setWeight(1).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 1)))).
                        add(LootItem.lootTableItem(Items.REDSTONE_TORCH).setWeight(2).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 1)))).
                        add(LootItem.lootTableItem(Items.HOPPER).setWeight(4).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 1)))).
                        add(LootItem.lootTableItem(Items.REPEATER).setWeight(2).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 1)))).
                        add(LootItem.lootTableItem(Items.COMPARATOR).setWeight(1).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 1)))).
                        add(LootItem.lootTableItem(Items.DAYLIGHT_DETECTOR).setWeight(1).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 1)))).
                        add(LootItem.lootTableItem(Items.REDSTONE_LAMP).setWeight(1).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 1)))).
                        add(LootItem.lootTableItem(Items.OBSERVER).setWeight(2).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 1)))).
                        add(LootItem.lootTableItem(Items.REDSTONE).setWeight(4).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 1)))).
                        add(LootItem.lootTableItem(Items.DROPPER).setWeight(3).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 1)))).
                        add(LootItem.lootTableItem(Items.CRAFTER).setWeight(2).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 1)))).
                        add(LootItem.lootTableItem(Items.NOTE_BLOCK).setWeight(1).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 1))))
                )
        );

        // kit_random
        biConsumer.accept(ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(DimDungeons.MOD_ID, "chest/kit_random")), LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).name("dimdungeons:kit_random").
                        add(NestedLootTable.lootTableReference(ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(DimDungeons.MOD_ID, "chest/kit_food"))).setWeight(10)).
                        add(NestedLootTable.lootTableReference(ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(DimDungeons.MOD_ID, "chest/kit_concrete"))).setWeight(10)).
                        add(NestedLootTable.lootTableReference(ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(DimDungeons.MOD_ID, "chest/kit_glass"))).setWeight(10)).
                        add(NestedLootTable.lootTableReference(ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(DimDungeons.MOD_ID, "chest/kit_redstone"))).setWeight(10)).
                        add(NestedLootTable.lootTableReference(ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(DimDungeons.MOD_ID, "chest/kit_wool"))).setWeight(10)).
                        add(NestedLootTable.lootTableReference(ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(DimDungeons.MOD_ID, "chest/kit_pottery"))).setWeight(10))
                )
        );

        // trophy_1
        biConsumer.accept(ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(DimDungeons.MOD_ID, "chest/trophy_1")), LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).name("dimdungeons:trophy_1").
                        add(LootItem.lootTableItem(ItemRegistrar.ITEM_TROPHY_1).setWeight(10).
                            apply(SetNameFunction.setName(Component.translatable("item.dimdungeons.item_trophy_1").withStyle(ChatFormatting.YELLOW).withStyle(ChatFormatting.BOLD), SetNameFunction.Target.ITEM_NAME)).
                            apply(SetLoreFunction.setLore().setMode(ListOperation.Append.INSTANCE).setResolutionContext(LootContext.EntityTarget.THIS).
                                addLine(Component.translatable("item.dimdungeons.item_trophy_1_line1").withStyle(ChatFormatting.AQUA)).
                                addLine(Component.translatable("item.dimdungeons.item_trophy_1_line2").withStyle(ChatFormatting.DARK_AQUA).withStyle(ChatFormatting.ITALIC)).
                                addLine(Component.empty().append(Component.translatable("item.dimdungeons.item_trophy_found_by").withStyle(ChatFormatting.GOLD)).append(Component.selector(new SelectorPattern("@s", null), Optional.empty()).withStyle(ChatFormatting.LIGHT_PURPLE))))).
                        add(LootItem.lootTableItem(ItemRegistrar.ITEM_TROPHY_2).setWeight(10).
                            apply(SetNameFunction.setName(Component.translatable("item.dimdungeons.item_trophy_2").withStyle(ChatFormatting.YELLOW).withStyle(ChatFormatting.BOLD), SetNameFunction.Target.ITEM_NAME)).
                            apply(SetLoreFunction.setLore().setMode(ListOperation.Append.INSTANCE).setResolutionContext(LootContext.EntityTarget.THIS).
                                addLine(Component.translatable("item.dimdungeons.item_trophy_2_line1").withStyle(ChatFormatting.AQUA)).
                                addLine(Component.translatable("item.dimdungeons.item_trophy_2_line2").withStyle(ChatFormatting.DARK_AQUA).withStyle(ChatFormatting.ITALIC)).
                                addLine(Component.empty().append(Component.translatable("item.dimdungeons.item_trophy_found_by").withStyle(ChatFormatting.GOLD)).append(Component.selector(new SelectorPattern("@s", null), Optional.empty()).withStyle(ChatFormatting.LIGHT_PURPLE))))).
                        add(LootItem.lootTableItem(ItemRegistrar.ITEM_TROPHY_3).setWeight(10).
                            apply(SetNameFunction.setName(Component.translatable("item.dimdungeons.item_trophy_3").withStyle(ChatFormatting.YELLOW).withStyle(ChatFormatting.BOLD), SetNameFunction.Target.ITEM_NAME)).
                            apply(SetLoreFunction.setLore().setMode(ListOperation.Append.INSTANCE).setResolutionContext(LootContext.EntityTarget.THIS).
                                addLine(Component.translatable("item.dimdungeons.item_trophy_3_line1").withStyle(ChatFormatting.AQUA)).
                                addLine(Component.translatable("item.dimdungeons.item_trophy_3_line2").withStyle(ChatFormatting.DARK_AQUA).withStyle(ChatFormatting.ITALIC)).
                                addLine(Component.empty().append(Component.translatable("item.dimdungeons.item_trophy_found_by").withStyle(ChatFormatting.GOLD)).append(Component.selector(new SelectorPattern("@s", null), Optional.empty()).withStyle(ChatFormatting.LIGHT_PURPLE))))).
                        add(LootItem.lootTableItem(ItemRegistrar.ITEM_TROPHY_4).setWeight(10).
                            apply(SetNameFunction.setName(Component.translatable("item.dimdungeons.item_trophy_4").withStyle(ChatFormatting.YELLOW).withStyle(ChatFormatting.BOLD), SetNameFunction.Target.ITEM_NAME)).
                            apply(SetLoreFunction.setLore().setMode(ListOperation.Append.INSTANCE).setResolutionContext(LootContext.EntityTarget.THIS).
                                addLine(Component.translatable("item.dimdungeons.item_trophy_4_line1").withStyle(ChatFormatting.AQUA)).
                                addLine(Component.translatable("item.dimdungeons.item_trophy_4_line2").withStyle(ChatFormatting.DARK_AQUA).withStyle(ChatFormatting.ITALIC)).
                                addLine(Component.empty().append(Component.translatable("item.dimdungeons.item_trophy_found_by").withStyle(ChatFormatting.GOLD)).append(Component.selector(new SelectorPattern("@s", null), Optional.empty()).withStyle(ChatFormatting.LIGHT_PURPLE))))).
                        add(LootItem.lootTableItem(ItemRegistrar.ITEM_TROPHY_5).setWeight(10).
                            apply(SetNameFunction.setName(Component.translatable("item.dimdungeons.item_trophy_5").withStyle(ChatFormatting.YELLOW).withStyle(ChatFormatting.BOLD), SetNameFunction.Target.ITEM_NAME)).
                            apply(SetLoreFunction.setLore().setMode(ListOperation.Append.INSTANCE).setResolutionContext(LootContext.EntityTarget.THIS).
                                addLine(Component.translatable("item.dimdungeons.item_trophy_5_line1").withStyle(ChatFormatting.AQUA)).
                                addLine(Component.translatable("item.dimdungeons.item_trophy_5_line2").withStyle(ChatFormatting.DARK_AQUA).withStyle(ChatFormatting.ITALIC)).
                                addLine(Component.empty().append(Component.translatable("item.dimdungeons.item_trophy_found_by").withStyle(ChatFormatting.GOLD)).append(Component.selector(new SelectorPattern("@s", null), Optional.empty()).withStyle(ChatFormatting.LIGHT_PURPLE))))).
                        add(LootItem.lootTableItem(ItemRegistrar.ITEM_TROPHY_6).setWeight(10).
                            apply(SetNameFunction.setName(Component.translatable("item.dimdungeons.item_trophy_6").withStyle(ChatFormatting.YELLOW).withStyle(ChatFormatting.BOLD), SetNameFunction.Target.ITEM_NAME)).
                            apply(SetLoreFunction.setLore().setMode(ListOperation.Append.INSTANCE).setResolutionContext(LootContext.EntityTarget.THIS).
                                addLine(Component.translatable("item.dimdungeons.item_trophy_6_line1").withStyle(ChatFormatting.AQUA)).
                                addLine(Component.translatable("item.dimdungeons.item_trophy_6_line2").withStyle(ChatFormatting.DARK_AQUA).withStyle(ChatFormatting.ITALIC)).
                                addLine(Component.empty().append(Component.translatable("item.dimdungeons.item_trophy_found_by").withStyle(ChatFormatting.GOLD)).append(Component.selector(new SelectorPattern("@s", null), Optional.empty()).withStyle(ChatFormatting.LIGHT_PURPLE))))).
                        add(LootItem.lootTableItem(ItemRegistrar.ITEM_TROPHY_7).setWeight(10).
                            apply(SetNameFunction.setName(Component.translatable("item.dimdungeons.item_trophy_7").withStyle(ChatFormatting.YELLOW).withStyle(ChatFormatting.BOLD), SetNameFunction.Target.ITEM_NAME)).
                            apply(SetLoreFunction.setLore().setMode(ListOperation.Append.INSTANCE).setResolutionContext(LootContext.EntityTarget.THIS).
                                addLine(Component.translatable("item.dimdungeons.item_trophy_7_line1").withStyle(ChatFormatting.AQUA)).
                                addLine(Component.translatable("item.dimdungeons.item_trophy_7_line2").withStyle(ChatFormatting.DARK_AQUA).withStyle(ChatFormatting.ITALIC)).
                                addLine(Component.empty().append(Component.translatable("item.dimdungeons.item_trophy_found_by").withStyle(ChatFormatting.GOLD)).append(Component.selector(new SelectorPattern("@s", null), Optional.empty()).withStyle(ChatFormatting.LIGHT_PURPLE))))).
                        add(LootItem.lootTableItem(ItemRegistrar.ITEM_TROPHY_8).setWeight(10).
                            apply(SetNameFunction.setName(Component.translatable("item.dimdungeons.item_trophy_8").withStyle(ChatFormatting.YELLOW).withStyle(ChatFormatting.BOLD), SetNameFunction.Target.ITEM_NAME)).
                            apply(SetLoreFunction.setLore().setMode(ListOperation.Append.INSTANCE).setResolutionContext(LootContext.EntityTarget.THIS).
                                addLine(Component.translatable("item.dimdungeons.item_trophy_8_line1").withStyle(ChatFormatting.AQUA)).
                                addLine(Component.translatable("item.dimdungeons.item_trophy_8_line2").withStyle(ChatFormatting.DARK_AQUA).withStyle(ChatFormatting.ITALIC)).
                                addLine(Component.empty().append(Component.translatable("item.dimdungeons.item_trophy_found_by").withStyle(ChatFormatting.GOLD)).append(Component.selector(new SelectorPattern("@s", null), Optional.empty()).withStyle(ChatFormatting.LIGHT_PURPLE)))))
                )
        );

//        // chestloot_lucky - vanilla datagen is stupid and insists on me regenerating the vanilla table at the same time
//        biConsumer.accept(ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(DimDungeons.MOD_ID, "chest/chestloot_lucky")), LootTable.lootTable()
//                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).name("dimdungeons:chestloot_lucky").
//                        add(NestedLootTable.lootTableReference(BuiltInLootTables.ABANDONED_MINESHAFT).setWeight(10)).
//                        add(NestedLootTable.lootTableReference(BuiltInLootTables.DESERT_PYRAMID).setWeight(10)).
//                        add(NestedLootTable.lootTableReference(BuiltInLootTables.JUNGLE_TEMPLE).setWeight(10)).
//                        add(NestedLootTable.lootTableReference(BuiltInLootTables.WOODLAND_MANSION).setWeight(10)).
//                        add(NestedLootTable.lootTableReference(BuiltInLootTables.SIMPLE_DUNGEON).setWeight(10)).
//                        add(NestedLootTable.lootTableReference(BuiltInLootTables.STRONGHOLD_CROSSING).setWeight(10)).
//                        add(NestedLootTable.lootTableReference(BuiltInLootTables.STRONGHOLD_LIBRARY).setWeight(10)).
//                        add(NestedLootTable.lootTableReference(BuiltInLootTables.TRIAL_CHAMBERS_SUPPLY).setWeight(10)).
//                        add(NestedLootTable.lootTableReference(BuiltInLootTables.RUINED_PORTAL).setWeight(10)).
//                        add(NestedLootTable.lootTableReference(BuiltInLootTables.PILLAGER_OUTPOST).setWeight(10)).
//                        add(NestedLootTable.lootTableReference(BuiltInLootTables.SHIPWRECK_SUPPLY).setWeight(10))
//                )
//        );
//
//        // chestloot_crazy - vanilla datagen is stupid and insists on me regenerating the vanilla table at the same time
//        biConsumer.accept(ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(DimDungeons.MOD_ID, "chest/chestloot_crazy")), LootTable.lootTable()
//                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).name("dimdungeons:chestloot_crazy").
//                        add(NestedLootTable.lootTableReference(BuiltInLootTables.SHIPWRECK_TREASURE).setWeight(10)).
//                        add(NestedLootTable.lootTableReference(BuiltInLootTables.ANCIENT_CITY).setWeight(10)).
//                        add(NestedLootTable.lootTableReference(BuiltInLootTables.END_CITY_TREASURE).setWeight(10)).
//                        add(NestedLootTable.lootTableReference(BuiltInLootTables.NETHER_BRIDGE).setWeight(10)).
//                        add(NestedLootTable.lootTableReference(BuiltInLootTables.BASTION_OTHER).setWeight(10)).
//                        add(NestedLootTable.lootTableReference(BuiltInLootTables.BASTION_TREASURE).setWeight(5)).
//                        add(NestedLootTable.lootTableReference(BuiltInLootTables.TRIAL_CHAMBERS_REWARD_COMMON).setWeight(5)).
//                        add(NestedLootTable.lootTableReference(BuiltInLootTables.TRIAL_CHAMBERS_REWARD_OMINOUS).setWeight(5))
//                )
//        );

        // chestbooks_1
        biConsumer.accept(ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(DimDungeons.MOD_ID, "chest/chestbooks_1")), LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).name("dimdungeons:chestbooks_1").
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(2).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.SILK_TOUCH), ConstantValue.exactly(1.0F)))).
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(2).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.BLAST_PROTECTION), ConstantValue.exactly(4.0F)))).
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(2).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.FIRE_PROTECTION), ConstantValue.exactly(4.0F)))).
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(2).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.PROJECTILE_PROTECTION), ConstantValue.exactly(4.0F)))).
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(2).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.FEATHER_FALLING), ConstantValue.exactly(4.0F)))).
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(2).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.AQUA_AFFINITY), ConstantValue.exactly(1.0F)))).
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(2).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.FROST_WALKER), ConstantValue.exactly(2.0F)))).
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(2).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.SMITE), ConstantValue.exactly(5.0F)))).
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(2).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.BANE_OF_ARTHROPODS), ConstantValue.exactly(5.0F)))).
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(2).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.KNOCKBACK), ConstantValue.exactly(2.0F)))).
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(2).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.PUNCH), ConstantValue.exactly(2.0F)))).
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(2).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.FLAME), ConstantValue.exactly(1.0F)))).
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(2).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.INFINITY), ConstantValue.exactly(1.0F)))).
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(2).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.MULTISHOT), ConstantValue.exactly(1.0F)))).
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(2).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.LURE), ConstantValue.exactly(3.0F)))).
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(2).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.LUCK_OF_THE_SEA), ConstantValue.exactly(3.0F)))).
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(2).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.RIPTIDE), ConstantValue.exactly(3.0F))))
                )
        );

        // chestbooks_2
        biConsumer.accept(ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(DimDungeons.MOD_ID, "chest/chestbooks_2")), LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).name("dimdungeons:chestbooks_2").
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(2).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.EFFICIENCY), ConstantValue.exactly(5.0F)))).
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(2).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.UNBREAKING), ConstantValue.exactly(3.0F)))).
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(2).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.FORTUNE), ConstantValue.exactly(3.0F)))).
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(2).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.PROTECTION), ConstantValue.exactly(4.0F)))).
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(2).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.RESPIRATION), ConstantValue.exactly(3.0F)))).
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(2).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.THORNS), ConstantValue.exactly(3.0F)))).
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(2).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.DEPTH_STRIDER), ConstantValue.exactly(1.0F)))).
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(2).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.SHARPNESS), ConstantValue.exactly(5.0F)))).
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(2).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.FIRE_ASPECT), ConstantValue.exactly(2.0F)))).
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(2).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.LOOTING), ConstantValue.exactly(3.0F)))).
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(2).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.SWEEPING_EDGE), ConstantValue.exactly(3.0F)))).
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(2).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.POWER), ConstantValue.exactly(5.0F)))).
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(2).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.LOYALTY), ConstantValue.exactly(3.0F)))).
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(2).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.IMPALING), ConstantValue.exactly(5.0F)))).
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(2).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.PIERCING), ConstantValue.exactly(4.0F)))).
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(2).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.QUICK_CHARGE), ConstantValue.exactly(3.0F))))
                )
        );

        // chestbooks_3
        biConsumer.accept(ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(DimDungeons.MOD_ID, "chest/chestbooks_3")), LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).name("dimdungeons:chestbooks_3").
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(4).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.UNBREAKING), ConstantValue.exactly(3.0F)).withEnchantment(enchants.getOrThrow(Enchantments.MENDING), ConstantValue.exactly(1.0F)))).
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(2).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.UNBREAKING), ConstantValue.exactly(3.0F)).withEnchantment(enchants.getOrThrow(Enchantments.SILK_TOUCH), ConstantValue.exactly(1.0F)))).
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(2).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.FORTUNE), ConstantValue.exactly(3.0F)).withEnchantment(enchants.getOrThrow(Enchantments.EFFICIENCY), ConstantValue.exactly(5.0F)))).
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(2).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.PROTECTION), ConstantValue.exactly(4.0F)).withEnchantment(enchants.getOrThrow(Enchantments.FEATHER_FALLING), ConstantValue.exactly(4.0F)))).
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(2).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.RESPIRATION), ConstantValue.exactly(3.0F)).withEnchantment(enchants.getOrThrow(Enchantments.AQUA_AFFINITY), ConstantValue.exactly(1.0F)))).
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(2).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.THORNS), ConstantValue.exactly(3.0F)).withEnchantment(enchants.getOrThrow(Enchantments.PROTECTION), ConstantValue.exactly(4.0F)))).
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(2).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.SHARPNESS), ConstantValue.exactly(5.0F)).withEnchantment(enchants.getOrThrow(Enchantments.EFFICIENCY), ConstantValue.exactly(5.0F)))).
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(2).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.POWER), ConstantValue.exactly(5.0F)).withEnchantment(enchants.getOrThrow(Enchantments.MENDING), ConstantValue.exactly(1.0F)))).
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(2).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.POWER), ConstantValue.exactly(5.0F)).withEnchantment(enchants.getOrThrow(Enchantments.INFINITY), ConstantValue.exactly(1.0F)))).
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(2).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.SWEEPING_EDGE), ConstantValue.exactly(3.0F)).withEnchantment(enchants.getOrThrow(Enchantments.SHARPNESS), ConstantValue.exactly(5.0F)))).
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(2).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.IMPALING), ConstantValue.exactly(5.0F)).withEnchantment(enchants.getOrThrow(Enchantments.LOYALTY), ConstantValue.exactly(3.0F)))).
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(2).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.IMPALING), ConstantValue.exactly(5.0F)).withEnchantment(enchants.getOrThrow(Enchantments.RIPTIDE), ConstantValue.exactly(3.0F)))).
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(2).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.QUICK_CHARGE), ConstantValue.exactly(3.0F)).withEnchantment(enchants.getOrThrow(Enchantments.MULTISHOT), ConstantValue.exactly(1.0F)))).
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(2).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.QUICK_CHARGE), ConstantValue.exactly(3.0F)).withEnchantment(enchants.getOrThrow(Enchantments.PIERCING), ConstantValue.exactly(4.0F))))
                )
        );

        // chestbooks_4 - unused for now
        biConsumer.accept(ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(DimDungeons.MOD_ID, "chest/chestbooks_4")), LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).name("dimdungeons:chestbooks_4").
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(2).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.EFFICIENCY), ConstantValue.exactly(6.0F)))).
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(4).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.UNBREAKING), ConstantValue.exactly(4.0F)))).
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(3).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.FORTUNE), ConstantValue.exactly(4.0F)))).
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(3).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.SHARPNESS), ConstantValue.exactly(7.0F)))).
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(3).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.POWER), ConstantValue.exactly(6.0F)))).
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(3).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.BANE_OF_ARTHROPODS), ConstantValue.exactly(9.0F)))).
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(3).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.SMITE), ConstantValue.exactly(9.0F)))).
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(3).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.IMPALING), ConstantValue.exactly(9.0F)))).
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(2).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.SWEEPING_EDGE), ConstantValue.exactly(4.0F)))).
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(2).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.FIRE_ASPECT), ConstantValue.exactly(3.0F)))).
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(2).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.LOOTING), ConstantValue.exactly(4.0F)))).
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(2).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.RIPTIDE), ConstantValue.exactly(5.0F))))
                )
        );

        // chestloot_1
        biConsumer.accept(ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(DimDungeons.MOD_ID, "chest/chestloot_1")), LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).name("dimdungeons:chestloot_1").
                        add(LootItem.lootTableItem(Items.RAW_COPPER_BLOCK).setWeight(3).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4)))).
                        add(LootItem.lootTableItem(Items.RAW_GOLD_BLOCK).setWeight(3).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4)))).
                        add(LootItem.lootTableItem(Items.GLOWSTONE).setWeight(3).apply(SetItemCountFunction.setCount(UniformGenerator.between(8, 12)))).
                        add(LootItem.lootTableItem(Items.OBSIDIAN).setWeight(3).apply(SetItemCountFunction.setCount(UniformGenerator.between(8, 12)))).
                        add(LootItem.lootTableItem(Items.QUARTZ).setWeight(4).apply(SetItemCountFunction.setCount(UniformGenerator.between(16, 24)))).
                        add(LootItem.lootTableItem(Items.EXPERIENCE_BOTTLE).setWeight(3).apply(SetItemCountFunction.setCount(UniformGenerator.between(16, 24)))).
                        add(LootItem.lootTableItem(Items.POTION).apply(SetPotionFunction.setPotion(Potions.LUCK)).setWeight(3).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))).
                        add(LootItem.lootTableItem(Items.ECHO_SHARD).setWeight(3).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 3)))).
                        add(LootItem.lootTableItem(Items.POTION).apply(SetPotionFunction.setPotion(Potions.STRONG_SWIFTNESS)).setWeight(3).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))).
                        add(LootItem.lootTableItem(ItemRegistrar.ITEM_HOMEWARD_PEARL).setWeight(3).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))).
                        add(LootItem.lootTableItem(Items.FIREWORK_ROCKET).setWeight(3).apply(SetItemCountFunction.setCount(UniformGenerator.between(12, 40)))).
                        add(LootItem.lootTableItem(Items.MAP).setWeight(3).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))).
                        add(LootItem.lootTableItem(Items.SLIME_BALL).setWeight(3).apply(SetItemCountFunction.setCount(UniformGenerator.between(8, 12)))).
                        add(LootItem.lootTableItem(Items.ENDER_PEARL).setWeight(3).apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 8)))).
                        add(LootItem.lootTableItem(Items.NAME_TAG).setWeight(4).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))).
                        add(NestedLootTable.lootTableReference(ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(DimDungeons.MOD_ID, "chest/kit_random"))).setWeight(20)).
                        add(NestedLootTable.lootTableReference(ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(DimDungeons.MOD_ID, "chest/chestbooks_1"))).setWeight(32)).
                        add(NestedLootTable.lootTableReference(ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(DimDungeons.MOD_ID, "chest/trophy_1"))).setWeight(1))
                )
        );

        // chestloot_2
        biConsumer.accept(ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(DimDungeons.MOD_ID, "chest/chestloot_2")), LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).name("dimdungeons:chestloot_2").
                        add(LootItem.lootTableItem(Items.EMERALD).setWeight(3).apply(SetItemCountFunction.setCount(UniformGenerator.between(8, 16)))).
                        add(LootItem.lootTableItem(Items.DIAMOND).setWeight(3).apply(SetItemCountFunction.setCount(UniformGenerator.between(4, 8)))).
                        add(LootItem.lootTableItem(Items.ENDER_CHEST).setWeight(3).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 1)))).
                        add(LootItem.lootTableItem(Items.GHAST_TEAR).setWeight(3).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4)))).
                        add(LootItem.lootTableItem(Items.SCULK_CATALYST).setWeight(3).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))).
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(5).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.MENDING), ConstantValue.exactly(1.0F)))).
                        add(LootItem.lootTableItem(Items.DIAMOND_CHESTPLATE).setWeight(3).apply(EnchantWithLevelsFunction.enchantWithLevels(this.registries, UniformGenerator.between(30, 30)))).
                        add(LootItem.lootTableItem(Items.DIAMOND_HELMET).setWeight(3).apply(EnchantWithLevelsFunction.enchantWithLevels(this.registries, UniformGenerator.between(30, 30)))).
                        add(LootItem.lootTableItem(Items.DIAMOND_LEGGINGS).setWeight(3).apply(EnchantWithLevelsFunction.enchantWithLevels(this.registries, UniformGenerator.between(30, 30)))).
                        add(LootItem.lootTableItem(Items.DIAMOND_BOOTS).setWeight(3).apply(EnchantWithLevelsFunction.enchantWithLevels(this.registries, UniformGenerator.between(30, 30)))).
                        add(LootItem.lootTableItem(Items.DIAMOND_PICKAXE).setWeight(3).apply(EnchantWithLevelsFunction.enchantWithLevels(this.registries, UniformGenerator.between(30, 30)))).
                        add(LootItem.lootTableItem(Items.DIAMOND_SWORD).setWeight(3).apply(EnchantWithLevelsFunction.enchantWithLevels(this.registries, UniformGenerator.between(30, 30)))).
                        add(LootItem.lootTableItem(Items.TRIDENT).setWeight(3).apply(EnchantWithLevelsFunction.enchantWithLevels(this.registries, UniformGenerator.between(30, 30)))).
                        add(LootItem.lootTableItem(Items.CROSSBOW).setWeight(3).apply(EnchantWithLevelsFunction.enchantWithLevels(this.registries, UniformGenerator.between(30, 30)))).
                        add(LootItem.lootTableItem(Items.BOW).setWeight(3).apply(EnchantWithLevelsFunction.enchantWithLevels(this.registries, UniformGenerator.between(30, 30)))).
                        add(LootItem.lootTableItem(ItemRegistrar.ITEM_PORTAL_KEY).setWeight(7).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 1)))).
                        add(LootItem.lootTableItem(ItemRegistrar.ITEM_SECRET_BELL).setWeight(4).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 1)))).
                        add(LootItem.lootTableItem(ItemRegistrar.ITEM_CHARGER_FULL).setWeight(4).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 1)))).
                        add(LootItem.lootTableItem(ItemRegistrar.ITEM_PORTAL_CROWN).setWeight(5).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 1)))).
                        add(NestedLootTable.lootTableReference(ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(DimDungeons.MOD_ID, "chest/chestbooks_2"))).setWeight(32)).
                        add(NestedLootTable.lootTableReference(ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(DimDungeons.MOD_ID, "chest/trophy_1"))).setWeight(1))
                )
        );

        // chestloot_3
        biConsumer.accept(ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(DimDungeons.MOD_ID, "chest/chestloot_3")), LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).name("dimdungeons:chestloot_3").
                        add(LootItem.lootTableItem(Items.EMERALD).setWeight(3).apply(SetItemCountFunction.setCount(UniformGenerator.between(12, 24)))).
                        add(LootItem.lootTableItem(Items.LAPIS_BLOCK).setWeight(3).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 5)))).
                        add(LootItem.lootTableItem(Items.HONEY_BLOCK).setWeight(3).apply(SetItemCountFunction.setCount(UniformGenerator.between(6, 12)))).
                        add(LootItem.lootTableItem(Items.TNT).setWeight(3).apply(SetItemCountFunction.setCount(UniformGenerator.between(10, 20)))).
                        add(LootItem.lootTableItem(Items.GOLDEN_APPLE).setWeight(4).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))).
                        add(LootItem.lootTableItem(Items.TURTLE_EGG).setWeight(3).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))).
                        add(LootItem.lootTableItem(Items.POTION).apply(SetPotionFunction.setPotion(Potions.LUCK)).setWeight(3).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))).
                        add(LootItem.lootTableItem(Items.SPLASH_POTION).apply(SetPotionFunction.setPotion(Potions.STRONG_HARMING)).setWeight(3).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))).
                        add(LootItem.lootTableItem(Items.POTION).apply(SetPotionFunction.setPotion(Potions.STRONG_LEAPING)).setWeight(3).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))).
                        add(LootItem.lootTableItem(Items.POTION).apply(SetPotionFunction.setPotion(Potions.LONG_REGENERATION)).setWeight(3).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))).
                        add(LootItem.lootTableItem(Items.POTION).apply(SetPotionFunction.setPotion(Potions.INVISIBILITY)).setWeight(3).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))).
                        add(LootItem.lootTableItem(Items.FIREWORK_ROCKET).setWeight(3).apply(SetItemCountFunction.setCount(UniformGenerator.between(24, 40)))).
                        add(LootItem.lootTableItem(Items.DRAGON_BREATH).setWeight(3).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 6)))).
                        add(LootItem.lootTableItem(Items.ENDER_PEARL).setWeight(3).apply(SetItemCountFunction.setCount(UniformGenerator.between(6, 10)))).
                        add(LootItem.lootTableItem(ItemRegistrar.ITEM_PORTAL_KEY).setWeight(4).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 1)))).
                        add(NestedLootTable.lootTableReference(ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(DimDungeons.MOD_ID, "chest/kit_random"))).setWeight(20)).
                        add(NestedLootTable.lootTableReference(ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(DimDungeons.MOD_ID, "chest/chestbooks_2"))).setWeight(32)).
                        add(NestedLootTable.lootTableReference(ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(DimDungeons.MOD_ID, "chest/trophy_1"))).setWeight(1))
                )
        );

        // chestloot_4
        biConsumer.accept(ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(DimDungeons.MOD_ID, "chest/chestloot_4")), LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).name("dimdungeons:chestloot_4").
                        add(LootItem.lootTableItem(Items.ANCIENT_DEBRIS).setWeight(3).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))).
                        add(LootItem.lootTableItem(Items.TOTEM_OF_UNDYING).setWeight(3).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 1)))).
                        add(LootItem.lootTableItem(Items.SHULKER_BOX).setWeight(3).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 1)))).
                        add(LootItem.lootTableItem(Items.GHAST_TEAR).setWeight(3).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 6)))).
                        add(LootItem.lootTableItem(Items.HEAVY_CORE).setWeight(2).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 1)))).
                        add(LootItem.lootTableItem(Items.DIAMOND_BLOCK).setWeight(2).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))).
                        add(LootItem.lootTableItem(Items.ENCHANTED_GOLDEN_APPLE).setWeight(3).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 1)))).
                        add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(4).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchants.getOrThrow(Enchantments.MENDING), ConstantValue.exactly(1.0F)))).
                        add(LootItem.lootTableItem(Items.DIAMOND_CHESTPLATE).setWeight(3).apply(EnchantWithLevelsFunction.enchantWithLevels(this.registries, UniformGenerator.between(30, 40)))).
                        add(LootItem.lootTableItem(Items.DIAMOND_HELMET).setWeight(3).apply(EnchantWithLevelsFunction.enchantWithLevels(this.registries, UniformGenerator.between(30, 40)))).
                        add(LootItem.lootTableItem(Items.DIAMOND_LEGGINGS).setWeight(3).apply(EnchantWithLevelsFunction.enchantWithLevels(this.registries, UniformGenerator.between(30, 40)))).
                        add(LootItem.lootTableItem(Items.DIAMOND_BOOTS).setWeight(3).apply(EnchantWithLevelsFunction.enchantWithLevels(this.registries, UniformGenerator.between(30, 40)))).
                        add(LootItem.lootTableItem(Items.DIAMOND_PICKAXE).setWeight(3).apply(EnchantWithLevelsFunction.enchantWithLevels(this.registries, UniformGenerator.between(30, 40)))).
                        add(LootItem.lootTableItem(Items.DIAMOND_SWORD).setWeight(3).apply(EnchantWithLevelsFunction.enchantWithLevels(this.registries, UniformGenerator.between(30, 40)))).
                        add(LootItem.lootTableItem(Items.CROSSBOW).setWeight(3).apply(EnchantWithLevelsFunction.enchantWithLevels(this.registries, UniformGenerator.between(30, 40)))).
                        add(LootItem.lootTableItem(Items.BOW).setWeight(3).apply(EnchantWithLevelsFunction.enchantWithLevels(this.registries, UniformGenerator.between(30, 40)))).
                        add(LootItem.lootTableItem(ItemRegistrar.ITEM_PORTAL_KEY).setWeight(7).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 1)))).
                        add(LootItem.lootTableItem(ItemRegistrar.ITEM_SECRET_BELL).setWeight(4).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 1)))).
                        add(LootItem.lootTableItem(ItemRegistrar.ITEM_CHARGER_FULL).setWeight(5).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 1)))).
                        add(LootItem.lootTableItem(ItemRegistrar.ITEM_BLANK_TELEPORTER_KEY).setWeight(4).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 1)))).
                        add(NestedLootTable.lootTableReference(ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(DimDungeons.MOD_ID, "chest/chestbooks_3"))).setWeight(32)).
                        add(NestedLootTable.lootTableReference(ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(DimDungeons.MOD_ID, "chest/trophy_1"))).setWeight(1))
                )
        );

        // chestloot_basic_easy
        biConsumer.accept(ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(DimDungeons.MOD_ID, "chest/chestloot_basic_easy")), LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).name("dimdungeons:chestloot_basic_easy").
                        add(NestedLootTable.lootTableReference(ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(DimDungeons.MOD_ID, "chest/chestloot_1"))).setWeight(4)).
                        add(NestedLootTable.lootTableReference(ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(DimDungeons.MOD_ID, "chest/chestloot_2"))).setWeight(1))
                )
        );

        // chestloot_basic_hard
        biConsumer.accept(ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(DimDungeons.MOD_ID, "chest/chestloot_basic_hard")), LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).name("dimdungeons:chestloot_basic_hard").
                        add(NestedLootTable.lootTableReference(ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(DimDungeons.MOD_ID, "chest/chestloot_2"))).setWeight(1))
                )
        );

        // chestloot_advanced_easy
        biConsumer.accept(ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(DimDungeons.MOD_ID, "chest/chestloot_advanced_easy")), LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).name("dimdungeons:chestloot_advanced_easy").
                        add(NestedLootTable.lootTableReference(ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(DimDungeons.MOD_ID, "chest/chestloot_3"))).setWeight(4)).
                        add(NestedLootTable.lootTableReference(ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(DimDungeons.MOD_ID, "chest/chestloot_4"))).setWeight(1))
                )
        );

        // chestloot_advanced_hard
        biConsumer.accept(ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(DimDungeons.MOD_ID, "chest/chestloot_advanced_hard")), LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).name("dimdungeons:chestloot_advanced_hard").
                        add(NestedLootTable.lootTableReference(ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(DimDungeons.MOD_ID, "chest/chestloot_4"))).setWeight(1))
                )
        );
    }
}