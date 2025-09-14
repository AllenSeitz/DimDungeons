package com.catastrophe573.dimdungeons.datagen;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.block.*;
import com.catastrophe573.dimdungeons.item.*;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.*;
import net.minecraft.client.data.models.model.*;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.stream.Stream;

public class DimDungeonsModelProvider extends ModelProvider
{
    public DimDungeonsModelProvider(PackOutput output)
    {
        super(output, DimDungeons.MOD_ID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels)
    {
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //
        // items
        //
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        itemModels.generateFlatItem(ItemRegistrar.ITEM_PORTAL_KEY.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ItemRegistrar.ITEM_BLANK_ADVANCED_KEY.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ItemRegistrar.ITEM_BLANK_THEME_KEY.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ItemRegistrar.ITEM_BLANK_BUILD_KEY.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ItemRegistrar.ITEM_BUILD_KEY.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ItemRegistrar.ITEM_BLANK_TELEPORTER_KEY.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ItemRegistrar.ITEM_SECRET_BELL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ItemRegistrar.ITEM_HOMEWARD_PEARL.get(), ModelTemplates.FLAT_ITEM);

        itemModels.generateFlatItem(ItemRegistrar.ITEM_TROPHY_1.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ItemRegistrar.ITEM_TROPHY_2.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ItemRegistrar.ITEM_TROPHY_3.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ItemRegistrar.ITEM_TROPHY_4.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ItemRegistrar.ITEM_TROPHY_5.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ItemRegistrar.ITEM_TROPHY_6.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ItemRegistrar.ITEM_TROPHY_7.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ItemRegistrar.ITEM_TROPHY_8.get(), ModelTemplates.FLAT_ITEM);

        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //
        // blocks
        //
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        blockModels.createTrivialCube(BlockRegistrar.BLOCK_GILDED_PORTAL.get());
        blockModels.createTrivialCube(BlockRegistrar.BLOCK_LOCAL_TELEPORTER.get());

        // the Gold Portal borrows block states from the vanilla nether portal (see: BlockModelGenerators.createNetherPortalBlock)
        // but an actual model is still used in assets/dimdungeons/models/block_gold_portal_model
        Block portalBlock = BlockRegistrar.BLOCK_GOLD_PORTAL.get();
        TextureMapping portalTextures = blockModels.texturedModels.getOrDefault(portalBlock, TexturedModel.CUBE.get(portalBlock)).getMapping();
        portalTextures.put(TextureSlot.LAYER0, ResourceLocation.fromNamespaceAndPath("dimdungeons", "block/block_gold_portal"));
        MultiVariantGenerator gportal = MultiVariantGenerator.multiVariant(portalBlock).with(
                PropertyDispatch.property(BlockStateProperties.HORIZONTAL_AXIS)
                        .select(Direction.Axis.X, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(portalBlock, "_ns")))
                        .select(Direction.Axis.Z, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(portalBlock, "_ew"))));
        blockModels.blockStateOutput.accept(gportal);
        ResourceLocation portalItem = ModelTemplates.FLAT_ITEM.create(portalBlock, portalTextures, blockModels.modelOutput); // create an inventory item just in case
        blockModels.registerSimpleItemModel(portalBlock, portalItem);

        // crown block borrows from vanilla wall (see: BlockModelGenerators.BlockFamilyProvider wall)
        Block crownBlock = BlockRegistrar.BLOCK_PORTAL_CROWN.get();
        TextureMapping crownTextures = blockModels.texturedModels.getOrDefault(crownBlock, TexturedModel.CUBE.get(crownBlock)).getMapping();
        ResourceLocation wall0 = ModelTemplates.WALL_POST.create(crownBlock, crownTextures, blockModels.modelOutput);
        ResourceLocation wall1 = ModelTemplates.WALL_LOW_SIDE.create(crownBlock, crownTextures, blockModels.modelOutput);
        ResourceLocation wall2 = ModelTemplates.WALL_TALL_SIDE.create(crownBlock, crownTextures, blockModels.modelOutput);
        BlockStateGenerator wall = BlockModelGenerators.createWall(BlockRegistrar.BLOCK_PORTAL_CROWN.get(), wall0, wall1, wall2);
        blockModels.blockStateOutput.accept(wall);
        ResourceLocation wall3 = ModelTemplates.WALL_INVENTORY.create(crownBlock, crownTextures, blockModels.modelOutput);
        blockModels.registerSimpleItemModel(crownBlock, wall3);

        // the Key Inscribing Station has no block states and an actual model is still used in assets/dimdungeons/models/block_key_charger
        Block chargerDamaged = BlockRegistrar.BLOCK_CHARGER_DAMAGED.get();
        ResourceLocation damagedModel = ResourceLocation.fromNamespaceAndPath("dimdungeons", "block/block_key_charger_damaged");
        MultiVariantGenerator damagedState = MultiVariantGenerator.multiVariant(chargerDamaged, Variant.variant().with(VariantProperties.MODEL, damagedModel));
        blockModels.blockStateOutput.accept(damagedState);

        Block chargerUsed = BlockRegistrar.BLOCK_CHARGER_USED.get();
        ResourceLocation usedModel = ResourceLocation.fromNamespaceAndPath("dimdungeons", "block/block_key_charger_used");
        MultiVariantGenerator usedState = MultiVariantGenerator.multiVariant(chargerUsed, Variant.variant().with(VariantProperties.MODEL, usedModel));
        blockModels.blockStateOutput.accept(usedState);

        Block chargerFull = BlockRegistrar.BLOCK_CHARGER_FULL.get();
        ResourceLocation fullModel = ResourceLocation.fromNamespaceAndPath("dimdungeons", "block/block_key_charger");
        MultiVariantGenerator fullState = MultiVariantGenerator.multiVariant(chargerFull, Variant.variant().with(VariantProperties.MODEL, fullModel));
        blockModels.blockStateOutput.accept(fullState);

        // the Portal Keyhole Block is a basic cube with some unique blockstates
        Block keyholeBlock = BlockRegistrar.BLOCK_PORTAL_KEYHOLE.get();
        TextureMapping keyholeTextures = blockModels.texturedModels.getOrDefault(keyholeBlock, TexturedModel.CUBE.get(keyholeBlock)).getMapping();
        keyholeTextures.put(TextureSlot.PARTICLE, ResourceLocation.fromNamespaceAndPath("dimdungeons", "block/block_gilded_portal"));
        keyholeTextures.put(TextureSlot.UP, ResourceLocation.fromNamespaceAndPath("dimdungeons", "block/block_gilded_portal"));
        keyholeTextures.put(TextureSlot.DOWN, ResourceLocation.fromNamespaceAndPath("dimdungeons", "block/block_gilded_portal"));
        keyholeTextures.put(TextureSlot.BACK, ResourceLocation.fromNamespaceAndPath("dimdungeons", "block/block_gilded_portal"));
        keyholeTextures.put(TextureSlot.FRONT, ResourceLocation.fromNamespaceAndPath("dimdungeons", "block/block_keyhole_front"));
        keyholeTextures.put(TextureSlot.WEST, ResourceLocation.fromNamespaceAndPath("dimdungeons", "block/block_gilded_portal"));
        keyholeTextures.put(TextureSlot.EAST, ResourceLocation.fromNamespaceAndPath("dimdungeons", "block/block_gilded_portal"));
        MultiVariantGenerator keyholeState = MultiVariantGenerator.multiVariant(keyholeBlock)
                .with(PropertyDispatch.property(BlockStateProperties.HORIZONTAL_FACING)
                        .select(Direction.NORTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R0))
                        .select(Direction.SOUTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
                        .select(Direction.WEST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                        .select(Direction.EAST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)))
                .with(PropertyDispatch.property(BlockPortalKeyhole.FILLED)
                        .select(true, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(keyholeBlock, "_full")))
                        .select(false, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(keyholeBlock, ""))))
                .with(PropertyDispatch.property(BlockPortalKeyhole.LIT)
                        .select(true, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(keyholeBlock, "_lit")))
                        .select(false, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(keyholeBlock, ""))));
        blockModels.blockStateOutput.accept(keyholeState);
        ResourceLocation keyholeItem = ModelTemplates.CUBE.create(keyholeBlock, keyholeTextures, blockModels.modelOutput);
        blockModels.registerSimpleItemModel(keyholeBlock, keyholeItem);
    }

    @Override
    public Stream<? extends Holder<Block>> getKnownBlocks()
    {
        return BlockRegistrar.BLOCKS.getEntries().stream();
    }

    @Override
    public Stream<? extends Holder<Item>> getKnownItems()
    {
        return ItemRegistrar.ITEMS.getEntries().stream();
    }
}