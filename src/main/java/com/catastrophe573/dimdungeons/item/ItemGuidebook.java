package com.catastrophe573.dimdungeons.item;

import com.catastrophe573.dimdungeons.DimDungeons;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.thread.EffectiveSide;

// this item is now unused
public class ItemGuidebook extends Item
{
    public static final String REG_NAME = "item_guidebook";

    public ItemGuidebook()
    {
	super(new Item.Properties().tab(ItemGroup.TAB_MISC).stacksTo(1).tab(ItemRegistrar.CREATIVE_TAB));
	this.setRegistryName(DimDungeons.MOD_ID, REG_NAME);
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn)
    {
	if (EffectiveSide.get() == LogicalSide.CLIENT)
	{
	    // just because
	    ITextComponent text1 = new TranslationTextComponent(new TranslationTextComponent("book.dimdungeons.open_guide_message").getString());
	    
	    playerIn.sendMessage(((TranslationTextComponent) text1).withStyle(TextFormatting.DARK_PURPLE), Util.NIL_UUID);
	    
	    ITextComponent text2a = new TranslationTextComponent("<");
	    ITextComponent text2b = new TranslationTextComponent(new TranslationTextComponent("book.dimdungeons.author").getString());
	    ITextComponent text2c = new TranslationTextComponent("> ");
	    ITextComponent text2d = new TranslationTextComponent(new TranslationTextComponent("book.dimdungeons.thank_you_message").getString());
	    ((TranslationTextComponent) text2a).withStyle(TextFormatting.WHITE);
	    ((TranslationTextComponent) text2b).withStyle(TextFormatting.AQUA);
	    ((TranslationTextComponent) text2c).withStyle(TextFormatting.WHITE);
	    ((TranslationTextComponent) text2d).withStyle(TextFormatting.WHITE);
	    playerIn.sendMessage(((TranslationTextComponent) text2a).append(text2b).append(text2c).append(text2d), Util.NIL_UUID);
	}
	else
	{
	    // create the new guidebook and give it to the player
	    ItemStack newbook = makeTempGuidebook();
	    if (playerIn.addItem(newbook))
	    {
		// delete this item
		ItemStack itemstack = playerIn.getItemInHand(handIn);
		itemstack.shrink(1);
	    }
	}

	return ActionResult.success(ItemStack.EMPTY); // return action result type success
    }

    public ItemStack makeTempGuidebook()
    {
	ItemStack stack = new ItemStack(Items.WRITTEN_BOOK);
	stack.setTag(new CompoundNBT());

	// create the complicated NBT tag list for the list of pages in the book
	ListNBT pages = new ListNBT();
	for (int i = 1; i < 10; i++)
	{
	    ITextComponent text = new TranslationTextComponent(new TranslationTextComponent("book.dimdungeons.guidebook_" + i).getString());
	    String json = ITextComponent.Serializer.toJson(text);
	    pages.add(i - 1, StringNBT.valueOf(json));
	}

	// actually set all the bookish NBT on the item
	stack.getTag().putBoolean("resolved", false);
	stack.getTag().putInt("generation", 0);
	stack.getTag().put("pages", pages);
	stack.getTag().putString("title", new TranslationTextComponent("book.dimdungeons.title_guidebook").getString());
	stack.getTag().putString("author", new TranslationTextComponent("book.dimdungeons.author").getString());
	return stack;
    }

    // intentionally always return false to hide the enchantment glint
    public boolean isFoil(ItemStack stack)
    {
	return false;
    }
}
