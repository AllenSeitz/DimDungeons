package com.catastrophe573.dimdungeons.compat.jei;

/*
import java.util.List;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.DungeonConfig;
import com.catastrophe573.dimdungeons.block.BlockRegistrar;
import com.catastrophe573.dimdungeons.item.ItemRegistrar;
import com.catastrophe573.dimdungeons.utils.DungeonUtils;
import com.google.common.collect.Lists;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;
*/

/*
@JeiPlugin
public class JEIAddon implements IModPlugin
{
    @Override
    public ResourceLocation getPluginUid()
    {
	return new ResourceLocation(DimDungeons.MOD_ID, "jei");
    }

    static public RecipeType<ActivateKeyJeiRecipe> getActivateKeyRecipeType()
    {
	return new RecipeType<>(ActivateKeyJeiRecipeCategory.UID, ActivateKeyJeiRecipe.class);
    }

    @Override
    // this is the new JEI 9.5.0 way of registering things
    public void registerRecipes(IRecipeRegistration registry)
    {
	List<ActivateKeyJeiRecipe> allRecipes = Lists.newArrayList();

	// this type of 'recipe' is not in json or configurable in any way
	ItemStack blank_key = new ItemStack(ItemRegistrar.ITEM_PORTAL_KEY.get());
	ItemStack end_frame = new ItemStack(Items.END_PORTAL_FRAME);
	ItemStack key_charger = new ItemStack(BlockRegistrar.BLOCK_CHARGER_FULL.get());

	allRecipes.add(new ActivateKeyJeiRecipe(blank_key, end_frame, DungeonUtils.getExampleKey()));
	allRecipes.add(new ActivateKeyJeiRecipe(blank_key, key_charger, DungeonUtils.getExampleKey()));

	// this allows end:remastered portal frames to work too
	if (DungeonConfig.isModInstalled("endrem"))
	{
	    ResourceLocation id = new ResourceLocation("endrem", "ancient_portal_frame");
	    ItemStack ancient_frame = new ItemStack(ForgeRegistries.BLOCKS.getValue(id));
	    allRecipes.add(new ActivateKeyJeiRecipe(blank_key, ancient_frame, DungeonUtils.getExampleKey()));
	}

	registry.addRecipes(getActivateKeyRecipeType(), allRecipes);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry)
    {
	registry.addRecipeCategories(new ActivateKeyJeiRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration)
    {
	//registration.addRecipeCatalyst(new ItemStack(Items.END_PORTAL_FRAME), getActivateKeyRecipeType());
    }
}
*/