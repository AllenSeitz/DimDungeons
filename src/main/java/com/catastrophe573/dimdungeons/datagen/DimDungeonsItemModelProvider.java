package com.catastrophe573.dimdungeons.datagen;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.block.BlockRegistrar;
import com.catastrophe573.dimdungeons.item.ItemRegistrar;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.properties.numeric.UseDuration;
import net.minecraft.data.PackOutput;

public class DimDungeonsItemModelProvider extends ModelProvider
{
    public DimDungeonsItemModelProvider(PackOutput output)
    {
        super(output, DimDungeons.MOD_ID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        // item/generated with the layer0 texture as the item name
        itemModels.generateFlatItem(ItemRegistrar.ITEM_PORTAL_KEY.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ItemRegistrar.ITEM_BLANK_ADVANCED_KEY.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ItemRegistrar.ITEM_BLANK_THEME_KEY.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ItemRegistrar.ITEM_BLANK_BUILD_KEY.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ItemRegistrar.ITEM_BUILD_KEY.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ItemRegistrar.ITEM_BLANK_TELEPORTER_KEY.get(), ModelTemplates.FLAT_ITEM);

        itemModels.generateFlatItem(ItemRegistrar.ITEM_SECRET_BELL.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(ItemRegistrar.ITEM_HOMEWARD_PEARL.get(), ModelTemplates.FLAT_HANDHELD_ITEM);

        itemModels.generateFlatItem(ItemRegistrar.ITEM_TROPHY_1.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ItemRegistrar.ITEM_TROPHY_2.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ItemRegistrar.ITEM_TROPHY_3.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ItemRegistrar.ITEM_TROPHY_4.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ItemRegistrar.ITEM_TROPHY_5.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ItemRegistrar.ITEM_TROPHY_6.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ItemRegistrar.ITEM_TROPHY_7.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ItemRegistrar.ITEM_TROPHY_8.get(), ModelTemplates.FLAT_ITEM);

        blockModels.createGenericCube(BlockRegistrar.BLOCK_GILDED_PORTAL.get());
        blockModels.createGenericCube(BlockRegistrar.BLOCK_LOCAL_TELEPORTER.get());
        blockModels.createGenericCube(BlockRegistrar.BLOCK_PORTAL_KEYHOLE.get());
        blockModels.createGenericCube(BlockRegistrar.BLOCK_PORTAL_CROWN.get());
        blockModels.createGenericCube(BlockRegistrar.BLOCK_CHARGER_FULL.get());
        blockModels.createGenericCube(BlockRegistrar.BLOCK_CHARGER_USED.get());
        blockModels.createGenericCube(BlockRegistrar.BLOCK_CHARGER_DAMAGED.get());

        /*
        // A bow-like item
        ItemModel.Unbaked bow = ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(MyItemsClass.EXAMPLE_ITEM.get()));
        ItemModel.Unbaked pullingBow0 = ItemModelUtils.plainModel(this.createFlatItemModel(MyItemsClass.EXAMPLE_ITEM.get(), "_pulling_0", ModelTemplates.BOW));
        ItemModel.Unbaked pullingBow1 = ItemModelUtils.plainModel(this.createFlatItemModel(MyItemsClass.EXAMPLE_ITEM.get(), "_pulling_1", ModelTemplates.BOW));
        ItemModel.Unbaked pullingBow2 = ItemModelUtils.plainModel(this.createFlatItemModel(MyItemsClass.EXAMPLE_ITEM.get(), "_pulling_2", ModelTemplates.BOW));
        this.itemModelOutput.accept(
                MyItemsClass.EXAMPLE_ITEM.get(),
                // Conditional model for item
                ItemModelUtils.conditional(
                        // Checks if item is being used
                        ItemModelUtils.isUsingItem(),
                        // When true, select model based on use duration
                        ItemModelUtils.rangeSelect(
                                new UseDuration(false),
                                // Scalar to apply to the thresholds
                                0.05F,
                                pullingBow0,
                                // Threshold when 0.65
                                ItemModelUtils.override(pullingBow1, 0.65F),
                                // Threshold when 0.9
                                ItemModelUtils.override(pullingBow2, 0.9F)
                        ),
                        // When false, use the base bow model
                        bow
                )
        );
        */
    }
}