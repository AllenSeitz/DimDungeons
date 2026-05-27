package com.catastrophe573.dimdungeons.compat.jei;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

public class ActivateKeyJeiRecipe implements Recipe<RecipeInput>
{
	private final ItemStack input;
	private final ItemStack target;
	private final ItemStack output;

	public ActivateKeyJeiRecipe(ItemStack blankKey, ItemStack targetBlock, ItemStack result)
	{
		input = blankKey;
		target = targetBlock;
		output = result;
	}

	public ItemStack getInput()
	{
		return input;
	}

	public ItemStack getTargetBlock()
	{
		return target;
	}

	public ItemStack getOutput()
	{
		return output;
	}

	@Override
	// "fake" JEI display recipes never match real inputs
	public boolean matches(@NonNull RecipeInput input, @NonNull Level level)
	{
		return false;
	}

	@Override
	// show the output item
	public @NonNull ItemStack assemble(@NonNull RecipeInput input)
	{
		return output.copy();
	}

	@Override
	// no toasts
	public boolean showNotification()
	{
		return false;
	}

	@Override
	// no recipe book group (no recipe book)
	public @NonNull String group()
	{
		return "";
	}

	@Override
	// these "fake" recipes will never be serialized and sent to the client, so null is okay
	public @NonNull RecipeSerializer<? extends Recipe<RecipeInput>> getSerializer()
	{
		return null;
	}

	@Override
	// this a non-standard JEI-only recipe, not a normal Minecraft recipe
	public @NonNull RecipeType<? extends Recipe<RecipeInput>> getType()
	{
		return null;
	}

	@Override
	// the goal here is to return an empty list because there are no placement constraints for a display recipe
	public @NonNull PlacementInfo placementInfo()
	{
		return PlacementInfo.NOT_PLACEABLE;
	}

	@Override
	// have to pick a category I guess, but it still should not show up in the book
	public @NonNull RecipeBookCategory recipeBookCategory()
	{
		return RecipeBookCategories.CRAFTING_MISC;
	}
}