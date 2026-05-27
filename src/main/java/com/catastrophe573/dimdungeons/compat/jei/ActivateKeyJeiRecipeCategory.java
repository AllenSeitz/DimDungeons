package com.catastrophe573.dimdungeons.compat.jei;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.utils.DungeonUtils;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jspecify.annotations.NonNull;

public class ActivateKeyJeiRecipeCategory implements IRecipeCategory<RecipeHolder<ActivateKeyJeiRecipe>>
{
    public static String CATEGORY_NAME = "key_recipes";
    public static final Identifier CATEGORY_ID = Identifier.fromNamespaceAndPath(DimDungeons.MOD_ID, CATEGORY_NAME);

    private static final Identifier texture = Identifier.fromNamespaceAndPath(DimDungeons.MOD_ID, "textures/gui/jei/jei_activate_key.png");
    public static final Identifier TITLE_STRING = Identifier.fromNamespaceAndPath(DimDungeons.MOD_ID, JEIAddon.ACTIVATE_RECIPE_ID);

    private final IDrawable background;
    private final IDrawable icon;

    public ActivateKeyJeiRecipeCategory(IGuiHelper guiHelper)
    {
        this.background = guiHelper.createDrawable(texture, 0, 0, 128, 74);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, DungeonUtils.getExampleKey());
    }

    @Override
    public @NonNull IRecipeType<RecipeHolder<ActivateKeyJeiRecipe>> getRecipeType()
    {
        return JEIAddon.RECIPE_TYPE;
    }

    @Override
    public @NonNull Component getTitle()
    {
        return Component.translatable(TITLE_STRING.toString());
    }

    @Override
    public int getWidth()
    {
        return 128;
    }

    @Override
    public int getHeight()
    {
        return 74;
    }

    @Override
    public IDrawable getIcon()
    {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<ActivateKeyJeiRecipe> recipe, @NonNull IFocusGroup focuses)
    {
        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 0, 0).setBackground(background, 0, 0);
        builder.addSlot(RecipeIngredientRole.INPUT, 28 + 1, 10 + 1).add(recipe.value().getInput());
        builder.addSlot(RecipeIngredientRole.INPUT, 28 + 1, 46 + 1).add(recipe.value().getTargetBlock());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 90 + 1, 28 + 1).add(recipe.value().getOutput());
    }
}