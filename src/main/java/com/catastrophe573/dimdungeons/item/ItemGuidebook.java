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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.thread.EffectiveSide;

public class ItemGuidebook extends Item
{
    public static final String REG_NAME = "item_guidebook";

    public ItemGuidebook()
    {
	super(new Item.Properties().group(ItemGroup.MISC).maxStackSize(1).group(ItemRegistrar.CREATIVE_TAB));
	this.setRegistryName(DimDungeons.MOD_ID, REG_NAME);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn)
    {
	if (EffectiveSide.get() == LogicalSide.CLIENT)
	{
	    // just because
	    ITextComponent text1 = new TranslationTextComponent(new TranslationTextComponent("book.dimdungeons.open_guide_message").getString());
	    playerIn.sendMessage(text1.setStyle(new Style().setBold(true).setColor(TextFormatting.DARK_PURPLE)));
	    ITextComponent text2a = new TranslationTextComponent("<");
	    ITextComponent text2b = new TranslationTextComponent(new TranslationTextComponent("book.dimdungeons.author").getString());
	    ITextComponent text2c = new TranslationTextComponent("> ");
	    ITextComponent text2d = new TranslationTextComponent(new TranslationTextComponent("book.dimdungeons.thank_you_message").getString());
	    text2a.setStyle(new Style().setColor(TextFormatting.WHITE));
	    text2b.setStyle(new Style().setColor(TextFormatting.AQUA));
	    text2c.setStyle(new Style().setColor(TextFormatting.WHITE));
	    text2d.setStyle(new Style().setColor(TextFormatting.WHITE));
	    playerIn.sendMessage(text2a.appendSibling(text2b).appendSibling(text2c).appendSibling(text2d));
	}
	else
	{
	    // delete this item
	    ItemStack itemstack = playerIn.getHeldItem(handIn);
	    itemstack.shrink(1);

	    // create the new guidebook and give it to the player
	    ItemStack newbook = makeTempGuidebook();
	    playerIn.addItemStackToInventory(newbook);
	}

	return ActionResult.func_226248_a_(ItemStack.EMPTY); // return action result type success
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
    public boolean hasEffect(ItemStack stack)
    {
	return false;
    }
}
