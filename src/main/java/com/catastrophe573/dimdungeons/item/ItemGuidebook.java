package com.catastrophe573.dimdungeons.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.util.thread.EffectiveSide;

// this item is now unused
public class ItemGuidebook extends Item
{
    public static final String REG_NAME = "item_guidebook";

    public ItemGuidebook()
    {
	super(new Item.Properties().tab(CreativeModeTab.TAB_MISC).stacksTo(1).tab(ItemRegistrar.CREATIVE_TAB));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn)
    {
	if (EffectiveSide.get() == LogicalSide.CLIENT)
	{
	    // just because
	    Component text1 = new TranslatableComponent(new TranslatableComponent("book.dimdungeons.open_guide_message").getString());

	    playerIn.sendMessage(((TranslatableComponent) text1).withStyle(ChatFormatting.DARK_PURPLE), Util.NIL_UUID);

	    Component text2a = new TranslatableComponent("<");
	    Component text2b = new TranslatableComponent(new TranslatableComponent("book.dimdungeons.author").getString());
	    Component text2c = new TranslatableComponent("> ");
	    Component text2d = new TranslatableComponent(new TranslatableComponent("book.dimdungeons.thank_you_message").getString());
	    ((TranslatableComponent) text2a).withStyle(ChatFormatting.WHITE);
	    ((TranslatableComponent) text2b).withStyle(ChatFormatting.AQUA);
	    ((TranslatableComponent) text2c).withStyle(ChatFormatting.WHITE);
	    ((TranslatableComponent) text2d).withStyle(ChatFormatting.WHITE);
	    playerIn.sendMessage(((TranslatableComponent) text2a).append(text2b).append(text2c).append(text2d), Util.NIL_UUID);
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

	return InteractionResultHolder.success(ItemStack.EMPTY); // return action result type success
    }

    public ItemStack makeTempGuidebook()
    {
	ItemStack stack = new ItemStack(Items.WRITTEN_BOOK);
	stack.setTag(new CompoundTag());

	// create the complicated NBT tag list for the list of pages in the book
	ListTag pages = new ListTag();
	for (int i = 1; i < 10; i++)
	{
	    Component text = new TranslatableComponent(new TranslatableComponent("book.dimdungeons.guidebook_" + i).getString());
	    String json = Component.Serializer.toJson(text);
	    pages.add(i - 1, StringTag.valueOf(json));
	}

	// actually set all the bookish NBT on the item
	stack.getTag().putBoolean("resolved", false);
	stack.getTag().putInt("generation", 0);
	stack.getTag().put("pages", pages);
	stack.getTag().putString("title", new TranslatableComponent("book.dimdungeons.title_guidebook").getString());
	stack.getTag().putString("author", new TranslatableComponent("book.dimdungeons.author").getString());
	return stack;
    }

    // intentionally always return false to hide the enchantment glint
    public boolean isFoil(ItemStack stack)
    {
	return false;
    }
}
