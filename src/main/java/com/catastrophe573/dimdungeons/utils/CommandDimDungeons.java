package com.catastrophe573.dimdungeons.utils;

import java.util.Collection;
import java.util.Collections;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.dimension.DungeonData;
import com.catastrophe573.dimdungeons.dimension.PersonalBuildData;
import com.catastrophe573.dimdungeons.item.*;
import com.catastrophe573.dimdungeons.structure.DungeonRoom;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.neoforged.fml.loading.FMLEnvironment;

public class CommandDimDungeons
{
	public static final Permission COMMANDS_ADMINS = new Permission.HasCommandLevel(PermissionLevel.ADMINS);

	// copied from the vanilla PlaceCommand
	private static final SuggestionProvider<CommandSourceStack> SUGGEST_TEMPLATES = (context, builder) -> {
		StructureTemplateManager structureManager = context.getSource().getLevel().getStructureManager();
		return SharedSuggestionProvider.suggestResource(structureManager.listTemplates(), builder);
	};

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
	{
		// register all commands "under" a single cheat "/dimdungeons". This is a more
		// polite way of doing things. (/givekey interferes with /give anyway)

		// the current cheat structures are:
		// /dimdungeons givekey [player recipient] [string type] [int theme, optional]
		// /dimdungeons givepersonal [player recipient] [player target, optional]
		// /dimdungeons getroom [player target]
		// /dimdungeons devbuild [structure]

		// the first part of the /dimdungeons cheat
		LiteralArgumentBuilder<CommandSourceStack> argumentBuilder = Commands.literal("dimdungeons").requires((cmd) ->
		{
			return cmd.permissions().hasPermission(COMMANDS_ADMINS);
		});

		// make a different cheat for "givekey basic", "givekey advanced", etc
		String[] keytypes = { "blank", "basic", "advanced", "theme" };
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

		// make a cheat for getting the current room, for debugging in live environments
		argumentBuilder.then(Commands.literal("getroom").then(Commands.argument("target_player", EntityArgument.player()).executes((cmd) ->
		{
			return printRoomName(cmd, EntityArgument.getPlayer(cmd, "target_player"));
		})));

		// make a cheat for placing a single dimdungeons room, chunk aligned, with convenient structure blocks, and the name above it in a display entity
		// refer to the vanilla Place command for reference
		if (!FMLEnvironment.isProduction())
		{
			argumentBuilder.then(Commands.literal("devbuild").then(Commands.argument("template", IdentifierArgument.id()).suggests(SUGGEST_TEMPLATES).executes((cmd) ->
			{
				return devBuildRoom(cmd, IdentifierArgument.getId(cmd, "template"));
			})));
		}

		// this is a debugging hack that must not ship
		// argumentBuilder.then(Commands.literal("debugpersonal").then(Commands.argument("recipient",
		// EntityArgument.players()).then(Commands.argument("target_player",
		// EntityArgument.entity()).executes((cmd) ->
		// {
		// return givePersonalKeyForAnyEntity(cmd, EntityArgument.getPlayers(cmd,
		// "recipient"), EntityArgument.getEntity(cmd, "target_player"));
		// }))));
		// argumentBuilder.then(Commands.literal("erasepersonalmap").executes((cmd) ->
		// {
		// return erasePersonalMap(cmd);
		// }));

		// register the /dimdungeons cheat
		dispatcher.register(argumentBuilder);
	}

	private static int giveKey(CommandContext<CommandSourceStack> cmd, Collection<ServerPlayer> recipients, String type, int theme) throws CommandSyntaxException
	{
		MutableComponent keyName = Component.translatable("item.dimdungeons.item_portal_key"); // for use with the logging at the end of the function

		for (ServerPlayer serverplayerentity : recipients)
		{
			// make a new and different key for each player
			ItemStack stack = new ItemStack(ItemRegistrar.ITEM_PORTAL_KEY.get());

			// which type of key was requested
			if ("blank".equals(type))
			{
				keyName = Component.translatable("item.dimdungeons.item_portal_key");
			}
			else if ("basic".equals(type))
			{
				((ItemPortalKey) (ItemRegistrar.ITEM_PORTAL_KEY.get())).activateKeyLevel1(cmd.getSource().getServer(), stack, theme);
				keyName = Component.translatable("item.dimdungeons.item_portal_key_basic");
			}
			else if ("advanced".equals(type))
			{
				((ItemPortalKey) (ItemRegistrar.ITEM_PORTAL_KEY.get())).activateKeyLevel2(cmd.getSource().getServer(), stack);
				keyName = Component.translatable("item.dimdungeons.item_portal_key_advanced");
			}
			else if ("theme".equals(type))
			{
				stack = new ItemStack(ItemRegistrar.ITEM_BLANK_THEME_KEY.get());
				BaseItemKey.setTheme(stack, theme);
				keyName = Component.translatable("item.dimdungeons.item_portal_key");
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

				serverplayerentity.level().playSound((Player) null, serverplayerentity.getX(), serverplayerentity.getY(), serverplayerentity.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((serverplayerentity.getRandom().nextFloat() - serverplayerentity.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
				serverplayerentity.inventoryMenu.broadcastChanges();
			}
			else
			{
				// keys don't normally stack, but just in case this block of code gives a stack of keys
				ItemEntity itementity = serverplayerentity.drop(stack, false);
				if (itementity != null)
				{
					itementity.setNoPickUpDelay();
					//itementity.setOwner(serverplayerentity.getUUID());
				}
			}
		}

		// print either "Gave one [key] to Dev" or "Gave one [key] to X players"
		MutableComponent finalKeyName = Component.literal(keyName.getString());
		if (recipients.size() == 1)
		{
			cmd.getSource().sendSuccess(() -> Component.translatable("commands.give.success.single", 1, finalKeyName, recipients.iterator().next().getDisplayName()), true);
		}
		else
		{
			cmd.getSource().sendSuccess(() -> Component.translatable("commands.give.success.single", 1, finalKeyName, recipients.size()), true);
		}

		return recipients.size();
	}

	private static int givePersonalKey(CommandContext<CommandSourceStack> cmd, Collection<ServerPlayer> recipients, ServerPlayer targetPlayer) throws CommandSyntaxException
	{
		MutableComponent keyName = Component.translatable("item.dimdungeons.item_build_key"); // for use with the logging at the end of the function

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

				serverplayerentity.level().playSound((Player) null, serverplayerentity.getX(), serverplayerentity.getY(), serverplayerentity.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((serverplayerentity.getRandom().nextFloat() - serverplayerentity.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
				serverplayerentity.inventoryMenu.broadcastChanges();
			}
			else
			{
				// keys don't normally stack, but just in case this block of code gives a stack of keys
				ItemEntity itementity = serverplayerentity.drop(newkey, false);
				if (itementity != null)
				{
					itementity.setNoPickUpDelay();
					//itementity.setOwner(serverplayerentity.getUUID());
				}
			}
		}

		// print either "Gave one [key] to Dev" or "Gave one [key] to X players"
		if (recipients.size() == 1)
		{
			cmd.getSource().sendSuccess(() -> Component.translatable("commands.give.success.single", 1, keyName, recipients.iterator().next().getDisplayName()), true);
		}
		else
		{
			cmd.getSource().sendSuccess(() -> Component.translatable("commands.give.success.single", 1, keyName, recipients.size()), true);
		}

		return recipients.size();
	}

	// debug cheat - do not allow keys for non-players
	@SuppressWarnings("unused")
	private static int givePersonalKeyForAnyEntity(CommandContext<CommandSourceStack> cmd, Collection<ServerPlayer> recipients, Entity targetPlayer) throws CommandSyntaxException
	{
		String keyName = "Personal Dimension Key"; // set later, used for command output

		if (!(targetPlayer instanceof LivingEntity))
		{
			cmd.getSource().sendFailure(Component.literal("Target entity is not a LivingEntity."));
			return 0;
		}

		for (ServerPlayer serverplayerentity : recipients)
		{
			// make a new and different key for each player
			ItemStack newkey = new ItemStack(ItemRegistrar.ITEM_BUILD_KEY.get());
			((ItemBlankBuildKey) (ItemRegistrar.ITEM_BLANK_BUILD_KEY.get())).activateBuildKey(cmd.getSource().getServer(), newkey, (LivingEntity) targetPlayer);

			// since we're debugging, rename the key with the dest_x and dest_z
			long dest_x = newkey.get(DimDungeons.DUNGEON_KEY_DATA).dest_x();
			long dest_z = newkey.get(DimDungeons.DUNGEON_KEY_DATA).dest_z();
			keyName = "Personal Key: (" + dest_x + ", " + dest_z + ")";
			newkey.update(DataComponents.CUSTOM_NAME, Component.literal(keyName), component -> component);

			serverplayerentity.getInventory().add(newkey);
		}

		// print either "Gave one [key] to Dev" or "Gave one [key] to X players"
		MutableComponent finalKeyName = Component.literal(keyName);
		if (recipients.size() == 1)
		{
			cmd.getSource().sendSuccess(() -> Component.translatable("commands.give.success.single", 1, finalKeyName, recipients.iterator().next().getDisplayName()), true);
		}
		else
		{
			cmd.getSource().sendSuccess(() -> Component.translatable("commands.give.success.single", 1, finalKeyName, recipients.size()), true);
		}

		return recipients.size();
	}

	private static int printRoomName(CommandContext<CommandSourceStack> cmd, Entity targetPlayer) throws CommandSyntaxException
	{
		if (!(targetPlayer instanceof LivingEntity))
		{
			cmd.getSource().sendFailure(Component.literal("Target entity is not a LivingEntity."));
			return 0;
		}

		// do not run this function outside of the dungeon dimension
		Level dungeonWorld = targetPlayer.level();
		if (!DungeonUtils.isDimensionDungeon(dungeonWorld))
		{
			cmd.getSource().sendFailure(Component.literal("This command only works in the dungeon dimension."));
			return 0;
		}

		// make sure a room exists here (it's possible for this to fail in legacy worlds or in other ways)
		DungeonRoom room = DungeonData.get(dungeonWorld).getRoomAtPos(targetPlayer.chunkPosition());
		if (room == null)
		{
			cmd.getSource().sendFailure(Component.literal("No room found at current position."));
			return 0; // possible
		}
		
		MutableComponent text = Component.literal("room: " + room.structure + " rot: " + room.rotation);
		//MutableComponent text = Component.literal("room: " + room.structure + " rot: " + room.rotation + " code: " + DungeonPlacement.makeChunkCode(targetPlayer.chunkPosition()));
		text.withStyle(text.getStyle().withItalic(true));
		text.withStyle(text.getStyle().withColor(TextColor.fromLegacyFormat(ChatFormatting.BLUE)));
		cmd.getSource().sendSuccess(() -> text, true);

		return 1;
	}

	// debug cheat - definitely don't do this to anyone's world
	@SuppressWarnings("unused")
	private static int erasePersonalMap(CommandContext<CommandSourceStack> cmd) throws CommandSyntaxException
	{
		((PersonalBuildData)PersonalBuildData.get(DungeonUtils.getPersonalBuildWorld(cmd.getSource().getServer()))).debugClearKnownOwners();
		cmd.getSource().sendSuccess(() -> Component.literal("Deleted all known personal key associations."), true);
		return 0;
	}

	private static int devBuildRoom(CommandContext<CommandSourceStack> cmd, Identifier templateID) throws CommandSyntaxException
	{
		ServerPlayer player = cmd.getSource().getPlayer();

		if (player == null)
		{
			cmd.getSource().sendFailure(Component.literal("Called devbuild with a non-player source."));
			return 0;
		}

		// get the structure that was passed in and make sure it exists
		StructureTemplateManager templateManager = player.level().getServer().getStructureManager();
		StructureTemplate template = templateManager.getOrCreate(templateID);
		int structureHeight = template.getSize().getY();
		if (structureHeight == 0)
		{
			cmd.getSource().sendFailure(Component.literal("Could not find a structure named " + templateID + "."));
			return 0;
		}

		// assume a superflat world, and get the sky height of the southwest corner
		ChunkPos cpos = player.chunkPosition();
		Level level = player.level();
		int y = level.getHeight(Heightmap.Types.WORLD_SURFACE, cpos.getBlockX(0), cpos.getBlockZ(0));

		// place two structure blocks in the southwest corner
		BlockPos baseCorner = new BlockPos(cpos.getBlockX(0), y, cpos.getBlockZ(0));
		level.setBlock(baseCorner, Blocks.STRUCTURE_BLOCK.defaultBlockState(), Block.UPDATE_ALL);
		level.setBlock(baseCorner.above(1), Blocks.STRUCTURE_BLOCK.defaultBlockState(), Block.UPDATE_ALL);
		StructureBlockEntity blankBox = (StructureBlockEntity) level.getBlockEntity(baseCorner);
		StructureBlockEntity loadBox = (StructureBlockEntity) level.getBlockEntity(baseCorner.above(1));

		// initialize the values in these structure blocks
		blankBox.setMode(StructureMode.LOAD);
		blankBox.setStructureName("dimdungeons:blank");
		blankBox.setStructureSize(template.getSize());
		blankBox.setStructurePos(BlockPos.ZERO.above(2));

		loadBox.setMode(StructureMode.LOAD);
		loadBox.setStructureName(templateID.toString());
		loadBox.setStructureSize(template.getSize());
		loadBox.setStructurePos(BlockPos.ZERO.above(1));
		loadBox.setIgnoreEntities(false);

		// place a corner block outside the southeast corner and a save block above the northwest corner
		BlockPos cornerPos = baseCorner.south(16).west(1).above(1);
		BlockPos savePos = baseCorner.east(16).north(1).above(structureHeight+2);
		level.setBlock(cornerPos, Blocks.STRUCTURE_BLOCK.defaultBlockState(), Block.UPDATE_ALL);
		level.setBlock(savePos, Blocks.STRUCTURE_BLOCK.defaultBlockState(), Block.UPDATE_ALL);
		StructureBlockEntity cornerBox = (StructureBlockEntity) level.getBlockEntity(cornerPos);
		StructureBlockEntity saveBox = (StructureBlockEntity) level.getBlockEntity(savePos);

		// also initialize the values in these structure blocks
		cornerBox.setMode(StructureMode.CORNER);
		cornerBox.setStructureName(templateID.toString());

		saveBox.setMode(StructureMode.SAVE);
		saveBox.setStructureName(templateID.toString());
		saveBox.setStructureSize(template.getSize());
		saveBox.setStructurePos(new BlockPos(template.getSize().getX() * -1, template.getSize().getY() * -1, 1));
		saveBox.setIgnoreEntities(false);

//		StructurePlaceSettings placementsettings = (new StructurePlaceSettings()).setMirror(Mirror.NONE).setRotation(Rotation.NONE).setIgnoreEntities(false);
//		placementsettings.setBoundingBox(placementsettings.getBoundingBox());

//		BlockPos position = new BlockPos(cpos.getMinBlockX(), 50, cpos.getMinBlockZ());
//		BlockPos sizeRange = new BlockPos(16, 13, 16);

//		boolean success = template.placeInWorld((ServerLevelAccessor) world, position, sizeRange, placementsettings, world.getRandom(), 2);


		return 1; // 1 room built
	}
}