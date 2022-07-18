package com.catastrophe573.dimdungeons.utils;

import java.util.Collection;
import java.util.Collections;

import com.catastrophe573.dimdungeons.item.ItemBlankBuildKey;
import com.catastrophe573.dimdungeons.item.ItemPortalKey;
import com.catastrophe573.dimdungeons.item.ItemRegistrar;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class CommandDimDungeons
{
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
	// register all commands "under" a single cheat "/dimdungeons". This is a more polite way of doing things. (/givekey interferes with /give anyway)

	// the current cheat structures are:
	// /dimdungeons givekey [player recipient] [string type] [int theme, optional]
	// /dimdungeons givepersonal [player recipient] [player target, optional]

	// the first half of the /givekey cheat
	LiteralArgumentBuilder<CommandSourceStack> argumentBuilder = Commands.literal("dimdungeons").requires((cmd) ->
	{
	    return cmd.hasPermission(2);
	});

	// make a different cheat for "givekey basic", "givekey advanced", etc
	String[] keytypes = { "blank", "basic", "advanced" };
	for (int i = 0; i < keytypes.length; i++)
	{
	    String type = keytypes[i];
	    argumentBuilder.then(Commands.literal("givekey").then(Commands.literal(type).executes((cmd) ->
	    {
		return giveKey(cmd, Collections.singleton(cmd.getSource().getPlayerOrException()), type, 0);
	    }).then(Commands.argument("recipient", EntityArgument.players()).executes((cmd) ->
	    {
		return giveKey(cmd, EntityArgument.getPlayers(cmd, "recipient"), type, 0);
	    }).then(Commands.argument("theme", IntegerArgumentType.integer(0)).executes((cmd) ->
	    {
		return giveKey(cmd, EntityArgument.getPlayers(cmd, "recipient"), type, IntegerArgumentType.getInteger(cmd, "theme"));
	    })))));
	}

	// make a cheat for getting a personal dimension key
	argumentBuilder.then(Commands.literal("givepersonal").then(Commands.argument("recipient", EntityArgument.players()).then(Commands.argument("target_player", EntityArgument.player()).executes((cmd) ->
	{
	    return givePersonalKey(cmd, EntityArgument.getPlayers(cmd, "recipient"), EntityArgument.getPlayer(cmd, "target_player"));
	}))));

	// register the /givekey cheat
	dispatcher.register(argumentBuilder);
    }

    private static int giveKey(CommandContext<CommandSourceStack> cmd, Collection<ServerPlayer> recipients, String type, int theme) throws CommandSyntaxException
    {
	BaseComponent keyName = new TranslatableComponent("item.dimdungeons.item_portal_key"); // for use with the logging at the end of the function

	for (ServerPlayer serverplayerentity : recipients)
	{
	    // make a new and different key for each player
	    ItemStack stack = new ItemStack(ItemRegistrar.item_portal_key);

	    // which type of key was requested
	    if ("blank".equals(type))
	    {
		keyName = new TranslatableComponent("item.dimdungeons.item_portal_key");
	    }
	    else if ("basic".equals(type))
	    {
		((ItemPortalKey) (ItemRegistrar.item_portal_key.asItem())).activateKeyLevel1(cmd.getSource().getServer(), stack, theme);
		keyName = new TranslatableComponent("item.dimdungeons.item_portal_key_basic");
	    }
	    else if ("advanced".equals(type))
	    {
		((ItemPortalKey) (ItemRegistrar.item_portal_key.asItem())).activateKeyLevel2(cmd.getSource().getServer(), stack);
		keyName = new TranslatableComponent("item.dimdungeons.item_portal_key_advanced");
	    }
	    else
	    {
		// unreachable code as long as register and this else-if chain are kept in sync
	    }

	    // try to give the player the item
	    boolean flag = serverplayerentity.getInventory().add(stack);

	    // if that fails then throw it on the ground at the player's feet
	    if (flag && stack.isEmpty())
	    {
		stack.setCount(1);
		ItemEntity itementity = serverplayerentity.drop(stack, false);
		if (itementity != null)
		{
		    itementity.makeFakeItem();
		}

		serverplayerentity.level.playSound((Player) null, serverplayerentity.getX(), serverplayerentity.getY(), serverplayerentity.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((serverplayerentity.getRandom().nextFloat() - serverplayerentity.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
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
	if (recipients.size() == 1)
	{
	    cmd.getSource().sendSuccess(new TranslatableComponent("commands.give.success.single", 1, keyName, recipients.iterator().next().getDisplayName()), true);
	}
	else
	{
	    cmd.getSource().sendSuccess(new TranslatableComponent("commands.give.success.single", 1, keyName, recipients.size()), true);
	}

	return recipients.size();
    }

    private static int givePersonalKey(CommandContext<CommandSourceStack> cmd, Collection<ServerPlayer> recipients, ServerPlayer targetPlayer) throws CommandSyntaxException
    {
	TranslatableComponent keyName = new TranslatableComponent("item.dimdungeons.item_build_key"); // for use with the logging at the end of the function

	for (ServerPlayer serverplayerentity : recipients)
	{
	    // make a new and different key for each player
	    ItemStack newkey = new ItemStack(ItemRegistrar.ITEM_BUILD_KEY.get());
	    ((ItemBlankBuildKey) (ItemRegistrar.ITEM_BLANK_BUILD_KEY.get())).activateBuildKey(cmd.getSource().getServer(), newkey, targetPlayer);

	    // try to give the player the item
	    boolean flag = serverplayerentity.getInventory().add(newkey);

	    // if that fails then throw it on the ground at the player's feet
	    if (flag && newkey.isEmpty())
	    {
		newkey.setCount(1);
		ItemEntity itementity = serverplayerentity.drop(newkey, false);
		if (itementity != null)
		{
		    itementity.makeFakeItem();
		}

		serverplayerentity.level.playSound((Player) null, serverplayerentity.getX(), serverplayerentity.getY(), serverplayerentity.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((serverplayerentity.getRandom().nextFloat() - serverplayerentity.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
		serverplayerentity.inventoryMenu.broadcastChanges();
	    }
	    else
	    {
		// keys don't normally stack, but just in case this block of code gives a stack of keys
		ItemEntity itementity = serverplayerentity.drop(newkey, false);
		if (itementity != null)
		{
		    itementity.setNoPickUpDelay();
		    itementity.setOwner(serverplayerentity.getUUID());
		}
	    }
	}

	// print either "Gave one [key] to Dev" or "Gave one [key] to X players"
	if (recipients.size() == 1)
	{
	    cmd.getSource().sendSuccess(new TranslatableComponent("commands.give.success.single", 1, keyName, recipients.iterator().next().getDisplayName()), true);
	}
	else
	{
	    cmd.getSource().sendSuccess(new TranslatableComponent("commands.give.success.single", 1, keyName, recipients.size()), true);
	}

	return recipients.size();
    }
}