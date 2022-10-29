package com.catastrophe573.dimdungeons.utils;

import java.util.ArrayList;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.DungeonConfig;
import com.catastrophe573.dimdungeons.block.BlockGoldPortal;
import com.catastrophe573.dimdungeons.block.BlockPortalKeyhole;
import com.catastrophe573.dimdungeons.block.BlockRegistrar;
import com.catastrophe573.dimdungeons.block.TileEntityGoldPortal;
import com.catastrophe573.dimdungeons.block.TileEntityPortalKeyhole;
import com.catastrophe573.dimdungeons.dimension.CustomTeleporter;
import com.catastrophe573.dimdungeons.item.BaseItemKey;
import com.catastrophe573.dimdungeons.item.ItemBuildKey;
import com.catastrophe573.dimdungeons.item.ItemPortalKey;
import com.catastrophe573.dimdungeons.item.ItemRegistrar;
import com.catastrophe573.dimdungeons.structure.DungeonPlacement;
import com.catastrophe573.dimdungeons.structure.DungeonDesigner.DungeonType;

import net.minecraft.server.MinecraftServer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.server.level.ServerLevel;

// basically just global functions
public class DungeonUtils
{
	public static boolean isDimensionOverworld(Level worldIn)
	{
		return worldIn.dimension() == Level.OVERWORLD;
	}

	public static boolean isDimensionDungeon(Level worldIn)
	{
		if (worldIn == null)
		{
			return false;
		}
		return worldIn.dimension().location().getPath() == DimDungeons.dungeon_dimension_regname;
	}

	public static boolean isDimensionPersonalBuild(Level worldIn)
	{
		if (worldIn == null)
		{
			return false;
		}
		return worldIn.dimension().location().getPath() == DimDungeons.build_dimension_regname;
	}

	// this is used by the dungeon building logic
	public static ServerLevel getDungeonWorld(MinecraftServer server)
	{
		return server.getLevel(DimDungeons.DUNGEON_DIMENSION);
	}

	// this is used by the personal key activating logic
	public static ServerLevel getPersonalBuildWorld(MinecraftServer server)
	{
		return server.getLevel(DimDungeons.BUILD_DIMENSION);
	}

	// returns 0 for false, or 1 or higher for the type of debug dungeon to build
	public static int doesKeyMatchDebugCheat(DungeonGenData genData)
	{
		if (!(genData.keyItem.getItem() instanceof ItemPortalKey))
		{
			DimDungeons.logMessageError("FATAL ERROR: Using a non-key item to build a dungeon? What happened?");
			return 0;
		}

		if (genData.keyItem.hasCustomHoverName() && DungeonConfig.enableDebugCheats)
		{
			String name = genData.keyItem.getHoverName().getContents();
			if (name.contentEquals("DebugOne"))
			{
				return 1;
			}
			if (name.contentEquals("DebugTwo"))
			{
				return 2;
			}
			if (name.contentEquals("DebugThree"))
			{
				return 3;
			}
			if (name.contentEquals("DebugFour"))
			{
				return 4;
			}
			if (name.contentEquals("bas-4"))
			{
				return 5;
			}
			if (name.contentEquals("bas-3"))
			{
				return 6;
			}
			if (name.contentEquals("bas-h"))
			{
				return 7;
			}
			if (name.contentEquals("bas-c"))
			{
				return 8;
			}
			if (name.contentEquals("bas-1"))
			{
				return 9;
			}
			if (name.contentEquals("adv-4"))
			{
				return 10;
			}
			if (name.contentEquals("adv-3"))
			{
				return 11;
			}
			if (name.contentEquals("adv-h"))
			{
				return 12;
			}
			if (name.contentEquals("adv-c"))
			{
				return 13;
			}
			if (name.contentEquals("adv-1"))
			{
				return 14;
			}
			if (name.contains("theme-"))
			{
				// String themeStr = name.replaceFirst("theme-", "");
				// genData.dungeonTheme = Integer.parseUnsignedInt(themeStr);
				return 15;
			}
		}
		return 0;
	}

	// assume that if a sign was placed in the entrance chunk that the build must be
	// either started or finished
	public static boolean dungeonAlreadyExistsHere(Level worldIn, long entranceX, long entranceZ)
	{
		ChunkPos cpos = new ChunkPos((int) entranceX / 16, (int) entranceZ / 16);
		return DungeonPlacement.wasRoomBuiltAtChunk(worldIn, cpos);
	}

	// assume that if a portal was placed, that the other 64 chunks are already done
	public static boolean personalPortalAlreadyExistsHere(Level worldIn, long entranceX, long entranceZ)
	{
		Level buildDim = DungeonUtils.getPersonalBuildWorld(worldIn.getServer());

		ChunkPos cpos = new ChunkPos(((int) entranceX / 16) + 4, ((int) entranceZ / 16) + 4);
		BlockPos bpos = new BlockPos(cpos.getMinBlockX() + ItemBuildKey.PLOT_ENTRANCE_OFFSET_X, 50, cpos.getMinBlockZ() + +ItemBuildKey.PLOT_ENTRANCE_OFFSET_Z);

		BlockState block = buildDim.getBlockState(bpos);
		if (block.getBlock() == Blocks.BEDROCK)
		{
			return true;
		}
		return false;
	}

	public static void openPortalAfterBuild(Level worldIn, BlockPos pos, DungeonGenData genData, TileEntityPortalKeyhole myEntity)
	{
		// should portal blocks be spawned?
		if (!worldIn.isClientSide)
		{
			BlockState state = worldIn.getBlockState(pos);
			BaseItemKey key = (BaseItemKey) genData.keyItem.getItem();

			Direction keyholeFacing = state.getValue(BlockPortalKeyhole.FACING);

			// regardless of if this is a new or old dungeon, reprogram the exit door
			float entranceX = key.getWarpX(genData.keyItem);
			float entranceZ = key.getWarpZ(genData.keyItem);
			boolean dungeonExistsHere = false;
			if (key instanceof ItemBuildKey)
			{
				dungeonExistsHere = DungeonUtils.reprogramPersonalPortal(worldIn, (long) entranceX, (long) entranceZ, genData, keyholeFacing);
			}
			else if (genData.dungeonType == DungeonType.TELEPORTER_HUB)
			{
				dungeonExistsHere = DungeonUtils.reprogramTeleporterHubDoorway(worldIn, (long) entranceX, (long) entranceZ, genData, keyholeFacing);
			}
			else
			{
				dungeonExistsHere = DungeonUtils.reprogramExistingExitDoorway(worldIn, (long) entranceX, (long) entranceZ, genData, keyholeFacing);
			}

			// this function only checks for the air blocks below the keyhole and the
			// keyhole blockstate
			if (BlockPortalKeyhole.isOkayToSpawnPortalBlocks(worldIn, pos, state, myEntity) && dungeonExistsHere)
			{
				Direction.Axis axis = (keyholeFacing == Direction.NORTH || keyholeFacing == Direction.SOUTH) ? Direction.Axis.X : Direction.Axis.Z;

				BlockPortalKeyhole.addGoldenPortalBlock(worldIn, pos.below(), genData.keyItem, axis);
				BlockPortalKeyhole.addGoldenPortalBlock(worldIn, pos.below(2), genData.keyItem, axis);
			}

			// this function prints no message on success
			Player player = worldIn.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), -1.0f, false);
			if (player != null)
			{
				BlockPortalKeyhole.checkForProblemsAndLiterallySpeakToPlayer(worldIn, pos, state, myEntity, player, dungeonExistsHere);
			}
		}
	}

	// returns false if this function fails because the dungeon on the other side
	// was reset
	public static boolean reprogramExistingExitDoorway(Level worldIn, long entranceX, long entranceZ, DungeonGenData genData, Direction keyholeFacing)
	{
		Level dim = DungeonUtils.getDungeonWorld(worldIn.getServer());
		BlockPos portalStart = new BlockPos(entranceX, 55, entranceZ + 2);

		return actuallyReprogramGoldPortalBlocks(portalStart, dim, genData, keyholeFacing);
	}

	// returns false if this function fails because the dungeon on the other side
	// was reset
	public static boolean reprogramPersonalPortal(Level worldIn, long entranceX, long entranceZ, DungeonGenData genData, Direction keyholeFacing)
	{
		Level dim = DungeonUtils.getPersonalBuildWorld(worldIn.getServer());
		BlockPos portalStart = new BlockPos(entranceX + 1, 51, entranceZ + 1);

		if (genData.returnDimension.equals(DimDungeons.BUILD_DIMENSION.location().toString()))
		{
			return false; // for now, do not allow this
		}

		return actuallyReprogramGoldPortalBlocks(portalStart, dim, genData, keyholeFacing);
	}

	// this function creates the gold portal blocks if they do not exist, which the
	// other similar functions do not!
	public static boolean reprogramTeleporterHubDoorway(Level worldIn, long entranceX, long entranceZ, DungeonGenData genData, Direction keyholeFacing)
	{
		Level dim = DungeonUtils.getDungeonWorld(worldIn.getServer());
		BlockPos portalStart = new BlockPos(entranceX, 55, entranceZ);

		// figure out which of the 8 portals we're using and "move" the "entrance" there
		// this math is awkward because when we call getWarpX() or Z() on the key, the
		// coordinates are already transformed
		int[] undo_x_offset = { 0, -16, -21, -21, -16, 0, 5, 5 };
		int[] undo_z_offset = { 0, 0, -5, -21, -26, -26, -21, -5 };

		int[] x_offset = { 0, -16, -23, -23, -16, 0, 6, 6 };
		int[] z_offset = { -2, -2, -6, -22, -25, -25, -22, -6 };
		int doornum = genData.dungeonTheme;
		portalStart = portalStart.west(undo_x_offset[doornum]).east(x_offset[doornum]);
		portalStart = portalStart.south(undo_z_offset[doornum]).north(z_offset[doornum]);

		// figure out which direction the player will be facing in the dungeon dimension

		// create the gold portal blocks
		for (int xz = 0; xz < 2; xz++)
		{
			for (int y = 0; y < 3; y++)
			{
				BlockPos nextBlock = portalStart;
				Direction.Axis face = Direction.Axis.X;

				if (doornum == 0 || doornum == 1 || doornum == 4 || doornum == 5)
				{
					nextBlock = nextBlock.west(xz).above(y);
				}
				else
				{
					nextBlock = nextBlock.north(xz).above(y);
					face = Direction.Axis.Z;
				}

				dim.setBlock(nextBlock, BlockRegistrar.block_gold_portal.defaultBlockState().setValue(BlockGoldPortal.AXIS, face), 2);

				TileEntityGoldPortal te = (TileEntityGoldPortal) dim.getBlockEntity(nextBlock);
				if (te != null)
				{
					te.setDestination(genData.returnPoint.getX(), genData.returnPoint.getY(), genData.returnPoint.getZ(), genData.returnDimension, keyholeFacing);
					DimDungeons.logMessageInfo("DIMDUNGEONS INFO: Reprogrammed exit door at (" + nextBlock.getX() + ", " + nextBlock.getY() + ", " + nextBlock.getZ() + ") in dim "
					        + dim.dimension().location().getPath());
				}
				else
				{
					DimDungeons.logMessageWarn(
					        "DIMDUNGEONS WARNING: why is there no exit portal here? (" + nextBlock.getX() + ", " + nextBlock.getY() + ", " + nextBlock.getZ() + ")");
				}
			}
		}

		return true;
	}

	// this function only works for "normal" north/south entrances, it does not
	// handle east/west entrances at all
	protected static boolean actuallyReprogramGoldPortalBlocks(BlockPos bottomLeft, Level dim, DungeonGenData genData, Direction keyholeFacing)
	{
		for (int z = 0; z < 2; z++)
		{
			for (int y = 0; y < 3; y++)
			{
				BlockPos pos = bottomLeft.west(z).above(y);

				TileEntityGoldPortal te = (TileEntityGoldPortal) dim.getBlockEntity(pos);
				if (te != null)
				{
					te.setDestination(genData.returnPoint.getX(), genData.returnPoint.getY(), genData.returnPoint.getZ(), genData.returnDimension, keyholeFacing);
					DimDungeons.logMessageInfo("DIMDUNGEONS INFO: Reprogrammed exit door at (" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ") in dim "
					        + dim.dimension().location().getPath());
				}
				else
				{
					// this is now expected behavior if a server admin resets the dungeon dimension
					DimDungeons.logMessageWarn("DIMDUNGEONS WARNING: why is there no exit portal here? (" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")");
					return false;
				}
			}
		}

		return true;
	}

	// takes the chunkpos of the top left corner
	public static void buildSuperflatPersonalSpace(long buildX, long buildZ, MinecraftServer server)
	{
		BlockState[] layers = { Blocks.GRASS_BLOCK.defaultBlockState(), Blocks.DIRT.defaultBlockState(), Blocks.DIRT.defaultBlockState(), Blocks.DIRT.defaultBlockState(),
		        Blocks.STONE.defaultBlockState(), Blocks.STONE.defaultBlockState(), Blocks.DEEPSLATE.defaultBlockState(), Blocks.DEEPSLATE.defaultBlockState() };
		ServerLevel dim = getPersonalBuildWorld(server);
		ChunkPos cpos = new ChunkPos(((int) buildX / 16) + 4, ((int) buildZ / 16) + 4); // the +4 offset is for lining up with maps
		BlockPos bpos = new BlockPos(cpos.getMinBlockX(), 50, cpos.getMinBlockZ());

		// for each layer calculate the 128x1x128 area to be filled with the block from
		// that layer and do it just like the FillCommand
		for (int y = 0; y < 8; y++)
		{
			BoundingBox pArea = new BoundingBox(bpos.getX(), 50 - y, bpos.getZ(), bpos.getX() + (8 * 16) - 1, 50 - y, bpos.getZ() + (8 * 16) - 1);

			for (BlockPos blockpos : BlockPos.betweenClosed(pArea.minX(), pArea.minY(), pArea.minZ(), pArea.maxX(), pArea.maxY(), pArea.maxZ()))
			{
				dim.setBlock(blockpos, layers[y], 2);
			}
		}

		// finally, hand build the portal just outside the center of the south side
		BlockPos portalStart = new BlockPos(cpos.getMinBlockX() + ItemBuildKey.PLOT_ENTRANCE_OFFSET_X, 50, cpos.getMinBlockZ() + ItemBuildKey.PLOT_ENTRANCE_OFFSET_Z);
		dim.setBlock(portalStart, Blocks.BEDROCK.defaultBlockState(), 2);
		dim.setBlock(portalStart.east(), Blocks.BEDROCK.defaultBlockState(), 2);
		dim.setBlock(portalStart.west(), Blocks.BEDROCK.defaultBlockState(), 2);
		dim.setBlock(portalStart.east(2), Blocks.BEDROCK.defaultBlockState(), 2);

		// floor
		dim.setBlock(portalStart.south().west(), Blocks.BEDROCK.defaultBlockState(), 2);
		dim.setBlock(portalStart.south(), Blocks.BEDROCK.defaultBlockState(), 2);
		dim.setBlock(portalStart.south().east(), Blocks.BEDROCK.defaultBlockState(), 2);
		dim.setBlock(portalStart.south().east(2), Blocks.BEDROCK.defaultBlockState(), 2);

		// portal frame
		dim.setBlock(portalStart.south().west().above(), Blocks.STONE_BRICKS.defaultBlockState(), 2);
		dim.setBlock(portalStart.south().east(2).above(), Blocks.STONE_BRICKS.defaultBlockState(), 2);
		dim.setBlock(portalStart.south().west().above(2), Blocks.STONE_BRICKS.defaultBlockState(), 2);
		dim.setBlock(portalStart.south().east(2).above(2), Blocks.STONE_BRICKS.defaultBlockState(), 2);
		dim.setBlock(portalStart.south().west().above(3), Blocks.STONE_BRICKS.defaultBlockState(), 2);
		dim.setBlock(portalStart.south().east(2).above(3), Blocks.STONE_BRICKS.defaultBlockState(), 2);
		dim.setBlock(portalStart.south().west().above(4), BlockRegistrar.block_gilded_portal.defaultBlockState(), 2);
		dim.setBlock(portalStart.south().east(2).above(4), BlockRegistrar.block_gilded_portal.defaultBlockState(), 2);
		dim.setBlock(portalStart.south().above(4), Blocks.STONE_BRICKS.defaultBlockState(), 2);
		dim.setBlock(portalStart.south().east().above(4), Blocks.STONE_BRICKS.defaultBlockState(), 2);

		// spires too because why not
		dim.setBlock(portalStart.south().west(3), Blocks.BEDROCK.defaultBlockState(), 2);
		dim.setBlock(portalStart.south().east(4), Blocks.BEDROCK.defaultBlockState(), 2);
		dim.setBlock(portalStart.south().west(3).above(), Blocks.STONE_BRICKS.defaultBlockState(), 2);
		dim.setBlock(portalStart.south().east(4).above(), Blocks.STONE_BRICKS.defaultBlockState(), 2);
		dim.setBlock(portalStart.south().west(3).above(2), Blocks.STONE_BRICKS.defaultBlockState(), 2);
		dim.setBlock(portalStart.south().east(4).above(2), Blocks.STONE_BRICKS.defaultBlockState(), 2);
		dim.setBlock(portalStart.south().west(3).above(3), BlockRegistrar.block_gilded_portal.defaultBlockState(), 2);
		dim.setBlock(portalStart.south().east(4).above(3), BlockRegistrar.block_gilded_portal.defaultBlockState(), 2);

		// place the actual 6 portal blocks
		dim.setBlock(portalStart.south().above(), BlockRegistrar.block_gold_portal.defaultBlockState(), 2);
		dim.setBlock(portalStart.south().east().above(), BlockRegistrar.block_gold_portal.defaultBlockState(), 2);
		dim.setBlock(portalStart.south().above(2), BlockRegistrar.block_gold_portal.defaultBlockState(), 2);
		dim.setBlock(portalStart.south().east().above(2), BlockRegistrar.block_gold_portal.defaultBlockState(), 2);
		dim.setBlock(portalStart.south().above(3), BlockRegistrar.block_gold_portal.defaultBlockState(), 2);
		dim.setBlock(portalStart.south().east().above(3), BlockRegistrar.block_gold_portal.defaultBlockState(), 2);
	}

	// takes World.OVERWORLD and returns "minecraft:overworld"
	public static String serializeDimensionKey(ResourceKey<Level> dimension)
	{
		return dimension.location().getNamespace() + ":" + dimension.location().getPath();
	}

	// returns the limit of the dungeon space not in blocks, but in dungeon widths
	// (which is BLOCKS_APART_PER_DUNGEON)
	public static long getLimitOfWorldBorder(MinecraftServer server)
	{
		ResourceKey<Level> configkey = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(DungeonConfig.worldborderToRespect));
		ServerLevel world = server.getLevel(configkey);
		double size = world.getWorldBorder().getSize() / 2;

		return Math.round(size);
	}

	// returns the limit of the dungeon space not in blocks, but in dungeon widths
	// (which is BLOCKS_APART_PER_DUNGEON)
	public static long getLimitOfPersonalBuildDimension(MinecraftServer server)
	{
		ServerLevel world = getPersonalBuildWorld(server);
		double size = world.getWorldBorder().getSize() / 2;

		return Math.round(size);
	}

	// takes a block pos (not a chunk pos) and returns true if this space is
	// potentially buildable or false if it is void
	public static boolean isPersonalBuildChunk(BlockPos pos)
	{
		ChunkPos chunk = new ChunkPos(pos);

		if (chunk.x < 4 || chunk.z < 4)
		{
			return false;
		}

		int nx = (chunk.x - 4) % (ItemBuildKey.BLOCKS_APART_PER_PLOT / 16);
		int nz = (chunk.z - 4) % (ItemBuildKey.BLOCKS_APART_PER_PLOT / 16);

		// remember the +4 offset was for map art
		return (nx) < 8 && (nz) < 8;
	}

	// assumes this is called on the server only (and only on entities already in
	// this dimension)
	public static void sendEntityHomeInBuildWorld(Entity entity)
	{
		// figure out what the x/z of this key would be
		double topLeftX = Math.floor((entity.position().x) / ItemBuildKey.BLOCKS_APART_PER_PLOT);
		topLeftX = (topLeftX * ItemBuildKey.BLOCKS_APART_PER_PLOT) + ItemBuildKey.ENTRANCE_OFFSET_X;

		double topLeftZ = Math.floor((entity.position().z) / ItemBuildKey.BLOCKS_APART_PER_PLOT);
		topLeftZ = (topLeftZ * ItemBuildKey.BLOCKS_APART_PER_PLOT) + ItemBuildKey.ENTRANCE_OFFSET_Z;

		ServerLevel dim = DungeonUtils.getPersonalBuildWorld(entity.getServer());
		CustomTeleporter tele = new CustomTeleporter(dim);
		tele.setDestPos(topLeftX - 7, 51, topLeftZ + 4, 180.0f, 0);
		entity.resetFallDistance();
		entity.changeDimension(dim, tele);
	}

	// THIS MUST ONLY BE USED for the purposes of displaying an activated key in a
	// gui (such as for the JEI compat)
	// the key returned by this function is not valid
	public static ItemStack getExampleKey()
	{
		ItemStack icon = new ItemStack(ItemRegistrar.item_portal_key);
		CompoundTag data = new CompoundTag();
		data.putBoolean(ItemPortalKey.NBT_KEY_ACTIVATED, true);
		data.putString(ItemPortalKey.NBT_DUNGEON_TYPE, DungeonType.BASIC.toString());
		data.putInt(ItemPortalKey.NBT_KEY_DESTINATION_Z, 0);
		data.putInt(ItemPortalKey.NBT_NAME_TYPE, 2); // key to the
		data.putInt(ItemPortalKey.NBT_NAME_PART_1, 0); // dungeon of
		data.putInt(ItemPortalKey.NBT_NAME_PART_2, 17); // catastrophe

		icon.setTag(data);
		return icon;
	}

	public static void giveSecuritySystemPrompt(Player playerIn, String transkey)
	{
		TranslatableComponent text1 = new TranslatableComponent(new TranslatableComponent(transkey).getString());

		text1.withStyle(text1.getStyle().withItalic(true));
		text1.withStyle(text1.getStyle().withColor(TextColor.fromLegacyFormat(ChatFormatting.BLUE)));
		playerIn.displayClientMessage(text1, false);
	}

	public static void displayGuestList(Player playerIn, ArrayList<String> guestList)
	{
		TranslatableComponent text1 = new TranslatableComponent(new TranslatableComponent("security.dimdungeons.use_book").getString());

		for (int i = 0; i < guestList.size(); i++)
		{
			if (i != 0)
			{
				text1.append(", ");
			}
			text1.append(guestList.get(i));
		}

		text1.withStyle(text1.getStyle().withItalic(true));
		text1.withStyle(text1.getStyle().withColor(TextColor.fromLegacyFormat(ChatFormatting.BLUE)));
		playerIn.displayClientMessage(text1, false);
	}

	// this is related to the build dimension security system
	public static void notifyGuestListChange(Player playerIn, String transkey, String playerName)
	{
		TranslatableComponent text1 = new TranslatableComponent(playerName);
		text1.append(new TranslatableComponent(transkey).getString());

		text1.withStyle(text1.getStyle().withItalic(true));
		text1.withStyle(text1.getStyle().withColor(TextColor.fromLegacyFormat(ChatFormatting.BLUE)));
		playerIn.displayClientMessage(text1, false);
	}
}
