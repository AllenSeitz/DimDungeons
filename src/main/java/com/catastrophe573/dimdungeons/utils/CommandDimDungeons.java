package com.catastrophe573.dimdungeons.utils;

import java.util.Collection;
import java.util.Collections;

import com.catastrophe573.dimdungeons.item.ItemPortalKey;
import com.catastrophe573.dimdungeons.item.ItemRegistrar;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class CommandDimDungeons
{
    public static void register(CommandDispatcher<CommandSource> dispatcher)
    {
	// the first half of the /givekey cheat
	LiteralArgumentBuilder<CommandSource> givekeyArgumentBuilder = Commands.literal("givekey").requires((cmd) ->
	{
	    return cmd.hasPermission(2);
	});

	// make a different cheat for "givekey basic", "givekey advanced", etc
	String[] keytypes = { "blank", "basic", "advanced" };
	for (int i = 0; i < keytypes.length; i++)
	{
	    String type = keytypes[i];
	    givekeyArgumentBuilder.then(Commands.literal(type).executes((cmd) ->
	    {
		return giveKey(cmd, Collections.singleton(cmd.getSource().getPlayerOrException()), type);
	    }).then(Commands.argument("target", EntityArgument.players()).executes((cmd) ->
	    {
		return giveKey(cmd, EntityArgument.getPlayers(cmd, "target"), type);
	    })));
	}

	// register the /givekey cheat
	dispatcher.register(givekeyArgumentBuilder);

	// make the /gendungeon cheat
	LiteralArgumentBuilder<CommandSource> gendungeonArgumentBuilder = Commands.literal("gendungeon").requires((cmd) ->
	{
	    return cmd.hasPermission(2);
	});
	gendungeonArgumentBuilder.then(Commands.argument("x", IntegerArgumentType.integer(0, ItemPortalKey.RANDOM_COORDINATE_RANGE))
		.then(Commands.argument("z", IntegerArgumentType.integer(ItemPortalKey.RANDOM_COORDINATE_RANGE * -1, ItemPortalKey.RANDOM_COORDINATE_RANGE)).executes((cmd) ->
		{
		    return generateDungeon(cmd, IntegerArgumentType.getInteger(cmd, "x"), IntegerArgumentType.getInteger(cmd, "z"));
		})));

	// register the /gendungeon cheat
	dispatcher.register(gendungeonArgumentBuilder);
    }

    private static int giveKey(CommandContext<CommandSource> cmd, Collection<ServerPlayerEntity> targets, String type) throws CommandSyntaxException
    {
	TextComponent keyName = new TranslationTextComponent("item.dimdungeons.item_portal_key"); // for use with the logging at the end of the function

	for (ServerPlayerEntity serverplayerentity : targets)
	{
	    // make a new and different key for each player
	    ItemStack stack = new ItemStack(ItemRegistrar.item_portal_key);

	    // which type of key was requested
	    if ("blank".equals(type))
	    {
		keyName = new TranslationTextComponent("item.dimdungeons.item_portal_key");
	    }
	    else if ("basic".equals(type))
	    {
		((ItemPortalKey) (ItemRegistrar.item_portal_key.asItem())).activateKey(stack);
		keyName = new TranslationTextComponent("item.dimdungeons.item_portal_key_basic");
	    }
	    else if ("advanced".equals(type))
	    {
		((ItemPortalKey) (ItemRegistrar.item_portal_key.asItem())).activateKeyLevel2(stack);
		keyName = new TranslationTextComponent("item.dimdungeons.item_portal_key_advanced");
	    }
	    else
	    {
		// unreachable code as long as register and this else-if chain are kept in sync
	    }

	    // try to give the player the item
	    boolean flag = serverplayerentity.inventory.add(stack);

	    // if that fails then throw it on the ground at the player's feet
	    if (flag && stack.isEmpty())
	    {
		stack.setCount(1);
		ItemEntity itementity = serverplayerentity.drop(stack, false);
		if (itementity != null)
		{
		    itementity.makeFakeItem();
		}

		serverplayerentity.level.playSound((PlayerEntity) null, serverplayerentity.getX(), serverplayerentity.getY(), serverplayerentity.getZ(), SoundEvents.ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F,
			((serverplayerentity.getRandom().nextFloat() - serverplayerentity.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
		serverplayerentity.inventoryMenu.broadcastChanges();
	    }
	    else
	    {
		// keys don't normally stack, but just in case this block of code gives a stack of keys
		ItemEntity itementity = serverplayerentity.drop(stack, false);
		if (itementity != null)
		{
		    itementity.setNoPickUpDelay();
		    itementity.setOwner(serverplayerentity.getUUID());
		}
	    }
	}

	// print either "Gave one [key] to Dev" or "Gave one [key] to X players"
	if (targets.size() == 1)
	{
	    cmd.getSource().sendSuccess(new TranslationTextComponent("commands.give.success.single", 1, keyName, targets.iterator().next().getDisplayName()), true);
	}
	else
	{
	    cmd.getSource().sendSuccess(new TranslationTextComponent("commands.give.success.single", 1, keyName, targets.size()), true);
	}

	return targets.size();
    }

    private static int generateDungeon(CommandContext<CommandSource> cmd, int destX, int destZ) throws CommandSyntaxException
    {
	// set the return point to the the position of whatever entity ran this command. Don't over think this?
	DungeonGenData fakeData = new DungeonGenData();
	if (cmd.getSource() != null)
	{
	    fakeData.setReturnPoint(new BlockPos(cmd.getSource().getPosition().x, cmd.getSource().getPosition().y + 2, cmd.getSource().getPosition().z), "minecraft:overworld");
	}
	else
	{
	    fakeData.setReturnPoint(new BlockPos(0, 100, 0), "minecraft:overworld");
	}

	// generate a fake key and use that to run the normal generation function
	ItemStack fakeKey = new ItemStack(ItemRegistrar.item_portal_key);
	((ItemPortalKey) (ItemRegistrar.item_portal_key.asItem())).forceCoordinates(fakeKey, destX, destZ);
	fakeData.setKeyItem(fakeKey);

	if (DungeonUtils.buildDungeon(cmd.getSource().getLevel(), fakeData))
	{
	    cmd.getSource().sendSuccess(new TranslationTextComponent("commands.gendungeon.success", fakeKey.getHoverName()), true);
	    return 1;
	}

	cmd.getSource().sendSuccess(new TranslationTextComponent("commands.gendungeon.failed"), true);
	return 0;
    }
}