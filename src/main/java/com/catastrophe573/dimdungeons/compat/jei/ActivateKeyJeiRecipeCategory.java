package com.catastrophe573.dimdungeons.compat.jei;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.utils.DungeonUtils;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ActivateKeyJeiRecipeCategory implements IRecipeCategory<ActivateKeyJeiRecipe>
{
	private static final ResourceLocation texture = new ResourceLocation(DimDungeons.MOD_ID, "textures/gui/jei/jei_activate_key.png");
	public static final ResourceLocation UID = new ResourceLocation(DimDungeons.MOD_ID, "activate_key");

	private final IDrawable background;
	private final IDrawable icon;

	public ActivateKeyJeiRecipeCategory(IGuiHelper guiHelper)
	{
		this.background = guiHelper.createDrawable(texture, 0, 0, 128, 74);
		this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, DungeonUtils.getExampleKey());
	}

	@Override
	public Component getTitle()
	{
		return Component.translatable(UID.toString());
	}

	@Override
	public IDrawable getBackground()
	{
		return background;
	}

	@Override
	public IDrawable getIcon()
	{
		return icon;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, ActivateKeyJeiRecipe recipe, IFocusGroup focuses)
	{
		builder.addSlot(RecipeIngredientRole.INPUT, 28 + 1, 10 + 1).addItemStack(recipe.getInput());
		builder.addSlot(RecipeIngredientRole.INPUT, 28 + 1, 46 + 1).addItemStack(recipe.getTargetBlock());
		builder.addSlot(RecipeIngredientRole.OUTPUT, 90 + 1, 28 + 1).addItemStack(recipe.getOutput());
	}

	@Override
	public RecipeType<ActivateKeyJeiRecipe> getRecipeType()
	{
		return JEIAddon.getActivateKeyRecipeType();
	}
}
