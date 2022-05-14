package com.catastrophe573.dimdungeons.compat.jei;

import net.minecraft.world.item.ItemStack;

public class ActivateKeyJeiRecipe
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
}
