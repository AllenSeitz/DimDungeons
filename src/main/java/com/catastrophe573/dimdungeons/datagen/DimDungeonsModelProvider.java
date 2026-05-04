package com.catastrophe573.dimdungeons.datagen;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.block.*;
import com.catastrophe573.dimdungeons.item.*;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.*;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.Optional;
import java.util.stream.Stream;

import static net.minecraft.client.data.models.BlockModelGenerators.plainVariant;

public class DimDungeonsModelProvider extends ModelProvider
{
    // hold the bell by the handle and swing it by the handle
    public static final ModelTemplate HANDHELD_BELL_ITEM = ModelTemplates.FLAT_HANDHELD_ITEM.extend().
            transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, t -> t.rotation(0f, .0f, 180.0f).translation(1.0f, 0.0f, 0.0f)).
            transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, t -> t.rotation(0.0f, 10.0f, 180.0f).translation(0.0f, 3.0f, 1.0f)).build();

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
        generatePortalKeyItem(itemModels, ItemRegistrar.ITEM_PORTAL_KEY.get());
        itemModels.generateFlatItem(ItemRegistrar.ITEM_BLANK_ADVANCED_KEY.get(), ModelTemplates.FLAT_ITEM);
        generateBlankThemeKeyItem(itemModels, ItemRegistrar.ITEM_BLANK_THEME_KEY.get());
        itemModels.generateFlatItem(ItemRegistrar.ITEM_BLANK_BUILD_KEY.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ItemRegistrar.ITEM_BUILD_KEY.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ItemRegistrar.ITEM_BLANK_TELEPORTER_KEY.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ItemRegistrar.ITEM_SECRET_BELL.get(), HANDHELD_BELL_ITEM);
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
        blockModels.createGenericCube(BlockRegistrar.BLOCK_LOCAL_TELEPORTER.get());

        // the Gold Portal borrows block states from the vanilla nether portal (see: BlockModelGenerators.createNetherPortalBlock)
        // but an actual model is still used in assets/dimdungeons/models/block_gold_portal_model
        Block portalBlock = BlockRegistrar.BLOCK_GOLD_PORTAL.get();
        TextureMapping portalTextures = blockModels.TEXTURED_MODELS.getOrDefault(portalBlock, TexturedModel.CUBE.get(portalBlock)).getMapping();
        portalTextures.put(TextureSlot.LAYER0, ResourceLocation.fromNamespaceAndPath("dimdungeons", "block/block_gold_portal"));
        MultiVariantGenerator gportal = MultiVariantGenerator.dispatch(portalBlock).with(
                PropertyDispatch.initial(BlockStateProperties.HORIZONTAL_AXIS)
                        .select(Direction.Axis.X, plainVariant(ModelLocationUtils.getModelLocation(portalBlock, "_ns")))
                        .select(Direction.Axis.Z, plainVariant(ModelLocationUtils.getModelLocation(portalBlock, "_ew"))));
        blockModels.blockStateOutput.accept(gportal);
        ResourceLocation portalItem = ModelTemplates.FLAT_ITEM.create(portalBlock, portalTextures, blockModels.modelOutput); // create an inventory item just in case
        blockModels.registerSimpleItemModel(portalBlock, portalItem);

        // crown block borrows from vanilla wall (see: BlockModelGenerators.BlockFamilyProvider wall)
        Block crownBlock = BlockRegistrar.BLOCK_PORTAL_CROWN.get();
        TextureMapping crownTextures = blockModels.TEXTURED_MODELS.getOrDefault(crownBlock, TexturedModel.CUBE.get(crownBlock)).getMapping();
        MultiVariant wall0 = BlockModelGenerators.plainVariant(ModelTemplates.WALL_POST.create(crownBlock, crownTextures, blockModels.modelOutput));
        MultiVariant wall1 = BlockModelGenerators.plainVariant(ModelTemplates.WALL_LOW_SIDE.create(crownBlock, crownTextures, blockModels.modelOutput));
        MultiVariant wall2 = BlockModelGenerators.plainVariant(ModelTemplates.WALL_TALL_SIDE.create(crownBlock, crownTextures, blockModels.modelOutput));

        BlockModelDefinitionGenerator wall = BlockModelGenerators.createWall(BlockRegistrar.BLOCK_PORTAL_CROWN.get(), wall0, wall1, wall2);
        blockModels.blockStateOutput.accept(wall);
        ResourceLocation wall3 = ModelTemplates.WALL_INVENTORY.create(crownBlock, crownTextures, blockModels.modelOutput);
        blockModels.registerSimpleItemModel(crownBlock, wall3);

        // the Key Inscribing Station has only basic block states and an actual model is still used in assets/dimdungeons/models/block_key_charger
        // for updates, refer to: BlockModelGenerators.createEndPortalFrame
        Block chargerDamaged = BlockRegistrar.BLOCK_CHARGER_DAMAGED.get();
        ResourceLocation damagedModel = ResourceLocation.fromNamespaceAndPath("dimdungeons", "block/block_key_charger_damaged");
        MultiVariantGenerator damagedState = MultiVariantGenerator.dispatch(chargerDamaged, plainVariant(damagedModel));
        blockModels.blockStateOutput.accept(damagedState);

        Block chargerUsed = BlockRegistrar.BLOCK_CHARGER_USED.get();
        ResourceLocation usedModel = ResourceLocation.fromNamespaceAndPath("dimdungeons", "block/block_key_charger_used");
        MultiVariantGenerator usedState = MultiVariantGenerator.dispatch(chargerUsed, plainVariant(usedModel));
        blockModels.blockStateOutput.accept(usedState);

        Block chargerFull = BlockRegistrar.BLOCK_CHARGER_FULL.get();
        ResourceLocation fullModel = ResourceLocation.fromNamespaceAndPath("dimdungeons", "block/block_key_charger");
        MultiVariantGenerator fullState = MultiVariantGenerator.dispatch(chargerFull, plainVariant(fullModel));
        blockModels.blockStateOutput.accept(fullState);

        // The Portal Keyhole Block is too much of a pain to data-generate. Seriously, this incomplete snippet is already more lines than the result itself.
//        Block keyholeBlock = BlockRegistrar.BLOCK_PORTAL_KEYHOLE.get();
//        TextureMapping keyholeTextures_default = blockModels.texturedModels.getOrDefault(keyholeBlock, TexturedModel.CUBE.get(keyholeBlock)).getMapping();
//        keyholeTextures_default.put(TextureSlot.PARTICLE, ResourceLocation.fromNamespaceAndPath("dimdungeons", "block/block_gilded_portal"));
//        keyholeTextures_default.put(TextureSlot.UP, ResourceLocation.fromNamespaceAndPath("dimdungeons", "block/block_gilded_portal"));
//        keyholeTextures_default.put(TextureSlot.DOWN, ResourceLocation.fromNamespaceAndPath("dimdungeons", "block/block_gilded_portal"));
//        keyholeTextures_default.put(TextureSlot.BACK, ResourceLocation.fromNamespaceAndPath("dimdungeons", "block/block_gilded_portal"));
//        keyholeTextures_default.put(TextureSlot.FRONT, ResourceLocation.fromNamespaceAndPath("dimdungeons", "block/block_keyhole_front"));
//        keyholeTextures_default.put(TextureSlot.WEST, ResourceLocation.fromNamespaceAndPath("dimdungeons", "block/block_gilded_portal"));
//        keyholeTextures_default.put(TextureSlot.EAST, ResourceLocation.fromNamespaceAndPath("dimdungeons", "block/block_gilded_portal"));
//        MultiVariantGenerator keyholeState = MultiVariantGenerator.multiVariant(keyholeBlock)
//                .with(PropertyDispatch.property(BlockStateProperties.HORIZONTAL_FACING)
//                        .select(Direction.NORTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R0))
//                        .select(Direction.SOUTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
//                        .select(Direction.WEST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
//                        .select(Direction.EAST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)))
//                .with(PropertyDispatch.property(BlockPortalKeyhole.FILLED)
//                        .select(true, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(keyholeBlock, "_full")))
//                        .select(false, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(keyholeBlock, ""))))
//                .with(PropertyDispatch.property(BlockPortalKeyhole.LIT)
//                        .select(true, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(keyholeBlock, "_lit")))
//                        .select(false, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(keyholeBlock, ""))));
//        blockModels.blockStateOutput.accept(keyholeState);
//        ResourceLocation keyholeItem = ModelTemplates.CUBE.create(keyholeBlock, keyholeTextures_default, blockModels.modelOutput); // create inventory item for keyhole
//        blockModels.registerSimpleItemModel(keyholeBlock, keyholeItem);
    }

    @Override
    public Stream<? extends Holder<Block>> getKnownBlocks()
    {
        return BlockRegistrar.BLOCKS.getEntries().stream().filter(x -> !x.is(BlockRegistrar.BLOCK_PORTAL_KEYHOLE));
    }

    @Override
    public Stream<? extends Holder<Item>> getKnownItems()
    {
        return ItemRegistrar.ITEMS.getEntries().stream();
        //return ItemRegistrar.ITEMS.getEntries().stream().filter(x -> x.get() != ItemRegistrar.ITEM_PORTAL_KEY.asItem() && x.get() != ItemRegistrar.ITEM_BLANK_THEME_KEY.asItem());
    }

    private static void generateBlankThemeKeyItem(ItemModelGenerators itemModels, Item item)
    {
        ItemModel.Unbaked itemmodel$unbaked = ItemModelUtils.plainModel(itemModels.createFlatItemModel(item, "", new ModelTemplate(Optional.of(ResourceLocation.fromNamespaceAndPath("minecraft", "item/generated")), Optional.of(""), TextureSlot.LAYER0)));
        ItemModel.Unbaked itemmodel$unbaked1 = ItemModelUtils.plainModel(itemModels.createFlatItemModel(item, "_01", new ModelTemplate(Optional.of(ResourceLocation.fromNamespaceAndPath(DimDungeons.MOD_ID, "item/item_blank_theme_key")), Optional.of("_01"), TextureSlot.LAYER0)));
        ItemModel.Unbaked itemmodel$unbaked2 = ItemModelUtils.plainModel(itemModels.createFlatItemModel(item, "_02", new ModelTemplate(Optional.of(ResourceLocation.fromNamespaceAndPath(DimDungeons.MOD_ID, "item/item_blank_theme_key")), Optional.of("_02"), TextureSlot.LAYER0)));
        ItemModel.Unbaked itemmodel$unbaked3 = ItemModelUtils.plainModel(itemModels.createFlatItemModel(item, "_03", new ModelTemplate(Optional.of(ResourceLocation.fromNamespaceAndPath(DimDungeons.MOD_ID, "item/item_blank_theme_key")), Optional.of("_03"), TextureSlot.LAYER0)));
        itemModels.itemModelOutput
                .accept(
                        item,
                        ItemModelUtils.conditional(
                                // if the item has key data
                                ItemModelUtils.hasComponent(DimDungeons.DUNGEON_KEY_DATA.get()),
                                // select based on the theme property
                                ItemModelUtils.rangeSelect(
                                        new ModelPropertyTheme(),
                                        1, // unused scalar - must be 1
                                        itemmodel$unbaked,
                                        ItemModelUtils.override(itemmodel$unbaked, 0.00F),
                                        ItemModelUtils.override(itemmodel$unbaked1, 0.01F),
                                        ItemModelUtils.override(itemmodel$unbaked2, 0.02F),
                                        ItemModelUtils.override(itemmodel$unbaked3, 0.03F),
                                        ItemModelUtils.override(itemmodel$unbaked, 0.04F)
                                ),
                                // default item model if hasComponent fails
                                itemmodel$unbaked
                        )
                );
    }

    private static void generatePortalKeyItem(ItemModelGenerators itemModels, Item item)
    {
        ItemModel.Unbaked itemmodel$unbaked = ItemModelUtils.plainModel(itemModels.createFlatItemModel(item, "", new ModelTemplate(Optional.of(ResourceLocation.fromNamespaceAndPath("minecraft", "item/generated")), Optional.of(""), TextureSlot.LAYER0)));
        ItemModel.Unbaked itemmodel$unbaked1 = ItemModelUtils.plainModel(itemModels.createFlatItemModel(item, "_001", new ModelTemplate(Optional.of(ResourceLocation.fromNamespaceAndPath(DimDungeons.MOD_ID, "item/item_portal_key")), Optional.of("_001"), TextureSlot.LAYER0)));
        ItemModel.Unbaked itemmodel$unbaked2 = ItemModelUtils.plainModel(itemModels.createFlatItemModel(item, "_002", new ModelTemplate(Optional.of(ResourceLocation.fromNamespaceAndPath(DimDungeons.MOD_ID, "item/item_portal_key")), Optional.of("_002"), TextureSlot.LAYER0)));
        ItemModel.Unbaked itemmodel$unbaked3 = ItemModelUtils.plainModel(itemModels.createFlatItemModel(item, "_101", new ModelTemplate(Optional.of(ResourceLocation.fromNamespaceAndPath(DimDungeons.MOD_ID, "item/item_portal_key")), Optional.of("_101"), TextureSlot.LAYER0)));
        ItemModel.Unbaked itemmodel$unbaked4 = ItemModelUtils.plainModel(itemModels.createFlatItemModel(item, "_102", new ModelTemplate(Optional.of(ResourceLocation.fromNamespaceAndPath(DimDungeons.MOD_ID, "item/item_portal_key")), Optional.of("_102"), TextureSlot.LAYER0)));
        ItemModel.Unbaked itemmodel$unbaked5 = ItemModelUtils.plainModel(itemModels.createFlatItemModel(item, "_103", new ModelTemplate(Optional.of(ResourceLocation.fromNamespaceAndPath(DimDungeons.MOD_ID, "item/item_portal_key")), Optional.of("_103"), TextureSlot.LAYER0)));
        ItemModel.Unbaked itemmodel$unbaked6 = ItemModelUtils.plainModel(itemModels.createFlatItemModel(item, "_999", new ModelTemplate(Optional.of(ResourceLocation.fromNamespaceAndPath(DimDungeons.MOD_ID, "item/item_portal_key")), Optional.of("_999"), TextureSlot.LAYER0)));
        itemModels.itemModelOutput
                .accept(
                        item,
                        ItemModelUtils.conditional(
                                // if the item has key data
                                ItemModelUtils.hasComponent(DimDungeons.DUNGEON_KEY_DATA.get()),
                                // select based on the theme property
                                ItemModelUtils.rangeSelect(
                                        new ModelPropertyLevel(),
                                        1, // unused scalar - must be 1
                                        itemmodel$unbaked,
                                        ItemModelUtils.override(itemmodel$unbaked, 0.0f),
                                        ItemModelUtils.override(itemmodel$unbaked1, 0.001f),
                                        ItemModelUtils.override(itemmodel$unbaked2, 0.002f),
                                        ItemModelUtils.override(itemmodel$unbaked3, 0.101f),
                                        ItemModelUtils.override(itemmodel$unbaked4, 0.102f),
                                        ItemModelUtils.override(itemmodel$unbaked5, 0.103f),
                                        ItemModelUtils.override(itemmodel$unbaked, 0.104f),
                                        ItemModelUtils.override(itemmodel$unbaked6, 0.999f)
                                ),
                                // default item model if hasComponent fails
                                itemmodel$unbaked
                        )
                );
    }
}