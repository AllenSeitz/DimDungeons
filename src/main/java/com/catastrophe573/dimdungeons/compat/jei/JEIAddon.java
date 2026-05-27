package com.catastrophe573.dimdungeons.compat.jei;

import java.util.List;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.DungeonConfig;
import com.catastrophe573.dimdungeons.block.BlockRegistrar;
import com.catastrophe573.dimdungeons.item.ItemRegistrar;
import com.catastrophe573.dimdungeons.utils.DungeonUtils;
import com.google.common.collect.Lists;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jspecify.annotations.NonNull;

@JeiPlugin
public class JEIAddon implements IModPlugin
{
	public static String ACTIVATE_RECIPE_ID = "activate_key";
	public static String PLUGIN_ID = "jei";

	@SuppressWarnings("unchecked")
	public static IRecipeType<RecipeHolder<ActivateKeyJeiRecipe>> RECIPE_TYPE =
			IRecipeType.create(Identifier.fromNamespaceAndPath(DimDungeons.MOD_ID, ACTIVATE_RECIPE_ID), (Class<RecipeHolder<ActivateKeyJeiRecipe>>) (Class<?>) RecipeHolder.class);

	@Override
	public @NonNull Identifier getPluginUid()
	{
		return Identifier.fromNamespaceAndPath(DimDungeons.MOD_ID, PLUGIN_ID);
	}

	static public IRecipeType<RecipeHolder<ActivateKeyJeiRecipe>> getActivateKeyRecipeType()
	{
		return RECIPE_TYPE;
	}

	@Override
	// this is the new JEI 9.5.0 way of registering things
	public void registerRecipes(@NonNull IRecipeRegistration registry)
	{
		List<ActivateKeyJeiRecipe> allRecipes = Lists.newArrayList();

		// this type of 'recipe' is not in json or configurable in any way
		ItemStack blank_key = new ItemStack(ItemRegistrar.ITEM_PORTAL_KEY.get());
		ItemStack teleporter_key = new ItemStack(ItemRegistrar.ITEM_BLANK_TELEPORTER_KEY.get());
		ItemStack build_key = new ItemStack(ItemRegistrar.ITEM_BLANK_BUILD_KEY.get());
		ItemStack end_frame = new ItemStack(Items.END_PORTAL_FRAME);
		ItemStack key_charger = new ItemStack(BlockRegistrar.BLOCK_CHARGER_FULL.get());

		allRecipes.add(new ActivateKeyJeiRecipe(blank_key, end_frame, DungeonUtils.getExampleKey()));
		allRecipes.add(new ActivateKeyJeiRecipe(blank_key, key_charger, DungeonUtils.getExampleKey()));
		allRecipes.add(new ActivateKeyJeiRecipe(teleporter_key, end_frame, DungeonUtils.getExampleTeleporterHubKey()));
		allRecipes.add(new ActivateKeyJeiRecipe(teleporter_key, key_charger, DungeonUtils.getExampleTeleporterHubKey()));
		allRecipes.add(new ActivateKeyJeiRecipe(build_key, end_frame, DungeonUtils.getExampleBuildKey()));
		allRecipes.add(new ActivateKeyJeiRecipe(build_key, key_charger, DungeonUtils.getExampleBuildKey()));

		// this allows end:remastered portal frames to work too
		if (DungeonConfig.isModInstalled("endrem"))
		{
			Identifier id = Identifier.fromNamespaceAndPath("endrem", "ancient_portal_frame");
			ItemStack ancient_frame = new ItemStack(BuiltInRegistries.BLOCK.getValue(id));
			if ( !ancient_frame.isEmpty() )
			{
				allRecipes.add(new ActivateKeyJeiRecipe(blank_key, ancient_frame, DungeonUtils.getExampleKey()));
				allRecipes.add(new ActivateKeyJeiRecipe(teleporter_key, ancient_frame, DungeonUtils.getExampleTeleporterHubKey()));
				allRecipes.add(new ActivateKeyJeiRecipe(build_key, ancient_frame, DungeonUtils.getExampleBuildKey()));
			}
		}

		// Wrap recipes in RecipeHolder for JEI 9.5.0+
		List<RecipeHolder<ActivateKeyJeiRecipe>> wrappedRecipes = Lists.newArrayList();
		for (int i = 0; i < allRecipes.size(); i++)
		{
			ActivateKeyJeiRecipe recipe = allRecipes.get(i);
			Identifier recipeId = Identifier.fromNamespaceAndPath(DimDungeons.MOD_ID, ACTIVATE_RECIPE_ID + "_" + i);
			wrappedRecipes.add(new RecipeHolder<>(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.RECIPE, recipeId), recipe));
		}
		registry.addRecipes(getActivateKeyRecipeType(), wrappedRecipes);
	}

	@Override
	// this is for things like recipe types (furnace for smelting recipes, specifically the "smelting recipes" part)
	public void registerCategories(IRecipeCategoryRegistration registry)
	{
		registry.addRecipeCategories(new ActivateKeyJeiRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
	}

	@Override
	// this is for things like recipe types (furnace for smelting recipes, specifically the "furnace" part)
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration)
	{
		registration.addCraftingStation(getActivateKeyRecipeType(), new ItemStack(Items.END_PORTAL_FRAME));
	}
}