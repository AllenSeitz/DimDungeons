package com.catastrophe573.dimdungeons.item;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.DungeonConfig;
import com.catastrophe573.dimdungeons.block.BlockRegistrar;
import com.catastrophe573.dimdungeons.dimension.DungeonData;
import com.catastrophe573.dimdungeons.structure.DungeonDesigner;
import com.catastrophe573.dimdungeons.structure.DungeonDesigner.DungeonType;
import com.catastrophe573.dimdungeons.utils.DungeonUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemFrameItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Spawner;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EndPortalFrameBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BaseItemKey extends Item
{
	// DCR means "DataComponentRecord"
	public record DCR_KEY_ACTIVATED(int value1, boolean value2, boolean value3, String value4) {}
	public record DCR_BUILT(int value1, boolean value2) {}

	// NBT means "Named Binary Tag" and refers to Minecraft's old way of storing arbitrary data in an ItemStack
	public static final String NBT_KEY_ACTIVATED = "key_activated";
	public static final String NBT_BUILT = "built";
	public static final String NBT_KEY_DESTINATION_X = "dest_x";
	public static final String NBT_KEY_DESTINATION_Z = "dest_z";
	public static final String NBT_NAME_TYPE = "name_type";
	public static final String NBT_NAME_PART_1 = "name_part_1";
	public static final String NBT_NAME_PART_2 = "name_part_2";
	public static final String NBT_THEME = "theme";
	public static final String NBT_DUNGEON_TYPE = "dungeon_type";

	public static final int BLOCKS_APART_PER_DUNGEON = 256; // 16 chunks to try to keep "noise" or other interference from neighbors to a minimum (also makes maps work)
	public static final float ENTRANCE_OFFSET_X = 8.0f + (8 * 16); // applied when the player teleports in, centered on the two-block-wide return portal
	public static final float ENTRANCE_OFFSET_Z = 12.5f + (11 * 16); // applied when the player teleports in, centered on the two-block-wide return portal

	public static final TagKey<Block> tag_alternate_activation_blocks = BlockTags.create(ResourceLocation.fromNamespaceAndPath(DimDungeons.MOD_ID, "key_activation_blocks"));

	public BaseItemKey(Item.Properties properties)
	{
		super(properties.stacksTo(1).component(DimDungeons.DUNGEON_KEY_DATA.get(), new DungeonKeyDataComponentRecord(false, false, -1, -1, 0, 0, 0, 0, String.valueOf(DungeonDesigner.DungeonType.valueOf(String.valueOf(DungeonDesigner.DungeonType.BASIC))))));
	}

	public int getKeyLevel(ItemStack stack)
	{
		if (!isActivated(stack))
		{
			return 0;
		}
		if (getWarpZ(stack) < 0 || getDungeonType(stack) == DungeonType.ADVANCED)
		{
			return 2;
		}
		return 1;
	}

	public void activateKeyLevel1(MinecraftServer server, ItemStack stack, int theme)
	{
		boolean activated = true;
		boolean built = false;
		long destX = 0;
		long destZ = 0;
		int nameType = 0;
		int namePart1 = 0;
		int namePart2 = 0;
		String dungeonType = DungeonType.BASIC.toString();

		// where is this key going?
		long generation_limit = DungeonUtils.getLimitOfWorldBorder(server);
		long dungeonsPerLimit = generation_limit / BLOCKS_APART_PER_DUNGEON;
		int nextDungeonNumber = DungeonData.get(server.getLevel(DimDungeons.DUNGEON_DIMENSION)).getNumKeysRegistered() + 1;

		// go as far as possible on the z-axis, then the x-axis, staying in the positive x/z quadrant
		destZ = nextDungeonNumber / dungeonsPerLimit;
		destX = nextDungeonNumber % dungeonsPerLimit;

		// give it a funny random name
		RandomSource random = server.overworld().getRandom();
		nameType = random.nextInt(3);
		if (theme > 0)
		{
			nameType = 2;
		}
		if (nameType == 0 || nameType == 1)
		{
			namePart1 = random.nextInt(32); // key of noun & noun, key of finding noun in noun
			namePart2 = random.nextInt(32);
		}
		else
		{
			namePart1 = random.nextInt(20); // key to the place of noun
			namePart2 = random.nextInt(32);
		}

		stack.set(DimDungeons.DUNGEON_KEY_DATA, new DungeonKeyDataComponentRecord(activated, built, destX, destZ, nameType, namePart1, namePart2, theme, dungeonType));

		DungeonData.get(DungeonUtils.getDungeonWorld(server)).notifyOfNewKeyActivation();
	}

	public void activateKeyLevel2(MinecraftServer server, ItemStack stack)
	{
		boolean activated = true;
		boolean built = false;
		long destX = 0;
		long destZ = 0;
		int nameType = 3; // advanced key format
		int namePart1 = 0;
		int namePart2 = 0;
		int theme = 0;
		String dungeonType = DungeonType.ADVANCED.toString();

		// where is this key going?
		long generation_limit = DungeonUtils.getLimitOfWorldBorder(server);
		long dungeonsPerLimit = generation_limit / BLOCKS_APART_PER_DUNGEON;
		long nextDungeonNumber = DungeonData.get(server.getLevel(DimDungeons.DUNGEON_DIMENSION)).getNumKeysRegistered() + 1;

		// go as far as possible on the z-axis, then the x-axis, staying in the positive x/z quadrant
		destZ = nextDungeonNumber / dungeonsPerLimit;
		destX = nextDungeonNumber % dungeonsPerLimit;

		// give it a funny random name like "Key to the [LARGE] [PLACE]"
		RandomSource random = server.overworld().getRandom();
		namePart1 = random.nextInt(20); // place
		namePart2 = random.nextInt(12); // largeness

		stack.set(DimDungeons.DUNGEON_KEY_DATA, new DungeonKeyDataComponentRecord(activated, built, destX, destZ, nameType, namePart1, namePart2, theme, dungeonType));

		DungeonData.get(DungeonUtils.getDungeonWorld(server)).notifyOfNewKeyActivation();
	}

	public void activateKeyForNewTeleporterHub(MinecraftServer server, ItemStack stack)
	{
		boolean activated = true;
		boolean built = false;
		long destX = 0;
		long destZ = 0;
		int nameType = 4; // teleporter hub format
		int namePart1 = 0;
		int namePart2 = 0;
		int theme = 0; // the first door
		String dungeonType = DungeonType.TELEPORTER_HUB.toString();

		// where is this key going?
		long generation_limit = DungeonUtils.getLimitOfWorldBorder(server);
		long dungeonsPerLimit = generation_limit / BLOCKS_APART_PER_DUNGEON;
		int nextDungeonNumber = DungeonData.get(server.getLevel(DimDungeons.DUNGEON_DIMENSION)).getNumKeysRegistered() + 1;

		// go as far as possible on the z-axis, then the x-axis, staying in the positive x/z quadrant
		destZ = nextDungeonNumber / dungeonsPerLimit;
		destX = nextDungeonNumber % dungeonsPerLimit;

		// give it a funny random name
		RandomSource random = server.overworld().getRandom();
		namePart1 = random.nextInt(32);

		stack.set(DimDungeons.DUNGEON_KEY_DATA, new DungeonKeyDataComponentRecord(activated, built, destX, destZ, nameType, namePart1, namePart2, theme, dungeonType));

		DungeonData.get(DungeonUtils.getDungeonWorld(server)).notifyOfNewKeyActivation();
	}

	public static void activateKeyForExistingTeleporterHub(MinecraftServer server, ItemStack stack, int destX, int destZ, int doorIndex)
	{
		boolean activated = true;
		boolean built = true;

		int nameType = 5;
		int namePart1 = doorIndex;
		int namePart2 = 0;
		int theme = doorIndex;
		String dungeonType = DungeonType.TELEPORTER_HUB.toString();

		stack.set(DimDungeons.DUNGEON_KEY_DATA, new DungeonKeyDataComponentRecord(activated, built, destX, destZ, nameType, namePart1, namePart2, theme, dungeonType));
	}

	public static void setTheme(ItemStack stack, int theme)
	{
		if (!stack.has(DimDungeons.DUNGEON_KEY_DATA))
		{
			return;
		}

		// records are immutable, so make a new record just to change one field? am I doing this right?
		// TODO: nope, I'm not doing it right. Make DungeonKeyDataComponentRecord more like the vanilla classes. It doesn't have to be immutable everywhere.
		DungeonKeyDataComponentRecord data = stack.get(DimDungeons.DUNGEON_KEY_DATA);
		DungeonKeyDataComponentRecord newData = new DungeonKeyDataComponentRecord(
				false, // activated
				false, // built
				data.dest_x(),
				data.dest_z(),
				data.name_type(),
				data.name_part_1(),
				data.name_part_2(),
				theme,
				data.dungeon_type()
		);

		stack.set(DimDungeons.DUNGEON_KEY_DATA, newData);
	}

	public boolean isActivated(ItemStack stack)
	{
		if (stack.has(DimDungeons.DUNGEON_KEY_DATA))
		{
			return stack.get(DimDungeons.DUNGEON_KEY_DATA).key_activated();
		}
		return false;
	}

	public static boolean hasLegacyData(ItemStack stack)
	{
		return stack.has(DataComponents.CUSTOM_DATA);
	}

	public static void convertLegacyData(ItemStack stack)
	{
		if (!stack.has(DataComponents.CUSTOM_DATA))
		{
			return;
		}

		//CustomData cd = stack.getComponents().get(DataComponents.CUSTOM_DATA);
		TypedEntityData<BlockEntityType<?>> cd = stack.get(DataComponents.BLOCK_ENTITY_DATA);

		DungeonKeyDataComponentRecord newData = new DungeonKeyDataComponentRecord(
				cd.contains(NBT_KEY_ACTIVATED) ? cd.getUnsafe().getBoolean(NBT_KEY_ACTIVATED).get() : false,
				cd.contains(NBT_BUILT) ? cd.getUnsafe().getBoolean(NBT_BUILT).get() : false,
				cd.contains(NBT_KEY_DESTINATION_X) ? cd.getUnsafe().getLong(NBT_KEY_DESTINATION_X).get() : 0,
				cd.contains(NBT_KEY_DESTINATION_Z) ? cd.getUnsafe().getLong(NBT_KEY_DESTINATION_Z).get() : 0,
				cd.contains(NBT_NAME_TYPE) ? cd.getUnsafe().getInt(NBT_NAME_TYPE).get() : 0,
				cd.contains(NBT_NAME_PART_1) ? cd.getUnsafe().getInt(NBT_NAME_PART_1).get() : 0,
				cd.contains(NBT_NAME_PART_2) ? cd.getUnsafe().getInt(NBT_NAME_PART_2).get() : 0,
				cd.contains(NBT_THEME) ? cd.getUnsafe().getInt(NBT_THEME).get() : 0,
				cd.contains(NBT_DUNGEON_TYPE) ? cd.getUnsafe().getString(NBT_DUNGEON_TYPE).get() : "BASIC"
		);

		stack.set(DimDungeons.DUNGEON_KEY_DATA, newData);
		stack.remove(DataComponents.CUSTOM_DATA);
	}

	public float getWarpX(ItemStack stack)
	{
		if (stack != null && !stack.isEmpty())
		{
			DungeonKeyDataComponentRecord itemData = stack.get(DimDungeons.DUNGEON_KEY_DATA);
			if (itemData != null)
			{
				// teleporter keys make everything weird!
				if ( itemData.dungeon_type().isEmpty() )
				{
					DimDungeons.logMessageError("DIMDUNGEONS: Key item is missing dungeon type. Key is corrupt and will be removed.");
					stack.shrink(1);
					return -1;
				}
				DungeonType dtype = DungeonType.valueOf(itemData.dungeon_type());
				if (dtype == DungeonType.TELEPORTER_HUB)
				{
					float tempx = (itemData.dest_x() * BLOCKS_APART_PER_DUNGEON) + ENTRANCE_OFFSET_X;
					int doornum = itemData.theme();
					int[] x_offset = { 0, -16, -21, -21, -16, 0, 5, 5 };
					return tempx + x_offset[doornum];
				}

				return (itemData.dest_x() * BLOCKS_APART_PER_DUNGEON) + ENTRANCE_OFFSET_X;
			}
		}
		return -1;
	}

	public float getWarpZ(ItemStack stack)
	{
		if (stack != null && !stack.isEmpty())
		{
			DungeonKeyDataComponentRecord itemData = stack.get(DimDungeons.DUNGEON_KEY_DATA);
			if (itemData != null)
			{
				// teleporter keys make everything weird!
				if ( itemData.dungeon_type().isEmpty() )
				{
					DimDungeons.logMessageError("DIMDUNGEONS: Key item is missing dungeon type. Key is corrupt and will be removed.");
					stack.shrink(1);
					return -1;
				}
				DungeonType dtype = DungeonType.valueOf(itemData.dungeon_type());
				if (dtype == DungeonType.TELEPORTER_HUB)
				{
					float tempz = (itemData.dest_z() * BLOCKS_APART_PER_DUNGEON) + ENTRANCE_OFFSET_Z;
					int doornum = itemData.theme();
					int[] z_offset = { 0, 0, -5, -21, -26, -26, -21, -5 };
					return tempz + z_offset[doornum];
				}

				return (itemData.dest_z() * BLOCKS_APART_PER_DUNGEON) + ENTRANCE_OFFSET_Z;
			}
		}
		return -1;
	}

	public long getDungeonTopLeftX(ItemStack stack)
	{
		if (stack != null && !stack.isEmpty())
		{
			DungeonKeyDataComponentRecord itemData = stack.get(DimDungeons.DUNGEON_KEY_DATA);
			if (itemData != null)
			{
				return (long)((long) itemData.dest_x() * BLOCKS_APART_PER_DUNGEON);
			}
		}
		return -1;
	}

	public long getDungeonTopLeftZ(ItemStack stack)
	{
		if (stack != null && !stack.isEmpty())
		{
			DungeonKeyDataComponentRecord itemData = stack.get(DimDungeons.DUNGEON_KEY_DATA);
			if (itemData != null)
			{
				return ((long) itemData.dest_z() * BLOCKS_APART_PER_DUNGEON);
			}
		}
		return -1;
	}

	public int getDungeonTheme(ItemStack stack)
	{
		if (stack != null && !stack.isEmpty())
		{
			DungeonKeyDataComponentRecord itemData = stack.get(DimDungeons.DUNGEON_KEY_DATA);
			if (itemData != null)
			{
				return itemData.theme();
			}
		}
		return -1;
	}

	public DungeonType getDungeonType(ItemStack stack)
	{
		if (stack != null && !stack.isEmpty())
		{
			DungeonKeyDataComponentRecord itemData = stack.get(DimDungeons.DUNGEON_KEY_DATA);

			// keys created prior to version 153 will not have this field
			if (itemData != null && !itemData.dungeon_type().isEmpty())
			{
				return DungeonType.valueOf(itemData.dungeon_type());
			}

			// this is for legacy keys that relied on a -Z coordinate to signal advanced dungeons
			if (getWarpZ(stack) < 0)
			{
				return DungeonType.ADVANCED;
			}
		}

		return DungeonType.BASIC;
	}

	public boolean isBlockKeyCharger(BlockState state)
	{
		if (state.getBlock() == BlockRegistrar.BLOCK_CHARGER_FULL.get() || state.getBlock() == BlockRegistrar.BLOCK_CHARGER_USED.get()
		        || state.getBlock() == BlockRegistrar.BLOCK_CHARGER_DAMAGED.get())
		{
			return true;
		}

		return false;
	}

	@SuppressWarnings("deprecation")
	@Override
	public @NotNull InteractionResult useOn(UseOnContext parameters)
	{
		// break down the one 1.13 parameter to get the half dozen 1.12 parameters because I need most of them
		Level worldIn = parameters.getLevel();
		BlockPos pos = parameters.getClickedPos();
		Direction facing = parameters.getClickedFace();
		double hitX = parameters.getClickLocation().x();
		double hitZ = parameters.getClickLocation().z();
		Player player = parameters.getPlayer();
		RandomSource random = worldIn.getRandom();

		BlockState iblockstate = worldIn.getBlockState(pos);
		ItemStack itemstack = player.getItemInHand(parameters.getHand());

		// this check fixes slot logic
		if ( parameters.getHand() != InteractionHand.MAIN_HAND )
		{
			return InteractionResult.PASS;
		}
		int slot = player.getInventory().getSelectedSlot();

		// new in 1.13 the hit vector contains world coordinates in the integer part, and I would like just the decimal part
		hitX = Math.abs((int) hitX - hitX);
		hitZ = Math.abs((int) hitZ - hitZ);

		if (worldIn.getBlockState(pos) != null)
		{
			// System.out.println("Used a key on some block: " + worldIn.getBlockState(pos).getBlock().getRegistryName());
			// System.out.println("Hit it here: " + hitX + ", " + hitZ + ", facing=" + facing.getName());

			// did they use the key on an end portal frame?
			if (worldIn.getBlockState(pos).getBlock() == Blocks.END_PORTAL_FRAME)
			{
				boolean isFilled = ((Boolean) worldIn.getBlockState(pos).getValue(EndPortalFrameBlock.HAS_EYE)).booleanValue();

				// did they hit precisely the black area in the middle?
				if (hitX > 0.3f && hitX < 0.7f && hitZ > 0.3f && hitZ < 0.8f)
				{
					if (!isFilled)
					{
						// did they use it on the top?
						if (facing == Direction.UP)
						{
							if (isActivated(itemstack))
							{
								// System.out.println("Key already activated!");
								worldIn.playSound((Player) null, pos, SoundEvents.TRIDENT_HIT_GROUND, SoundSource.BLOCKS, 1.0F, 1.0F);
							}
							else
							{
								performActivationHelper(player, itemstack, worldIn, pos, slot);
								return InteractionResult.SUCCESS;
							}
						}
					}
					else
					{
						worldIn.setBlock(pos, iblockstate.setValue(EndPortalFrameBlock.HAS_EYE, Boolean.valueOf(false)), 2);
						worldIn.updateNeighbourForOutputSignal(pos, Blocks.END_PORTAL_FRAME);

						// dramatic effect for what you just did!
						worldIn.playSound(null, pos, SoundEvents.ENDER_EYE_DEATH, SoundSource.BLOCKS, 1.5F, 1.0F);
						worldIn.playSound(null, pos, SoundEvents.IRON_BREAK, SoundSource.BLOCKS, 0.4F, 1.5F);

						// launch a ring of particles up and outwards from the center
						for (int i = 0; i < 32; i++)
						{
							double d0 = (double) ((float) pos.getX() + 0.5F);
							double d1 = (double) ((float) pos.getY() + 0.8F);
							double d2 = (double) ((float) pos.getZ() + 0.5F);
							double xspeed = (random.nextFloat() * 0.08) * (random.nextBoolean() ? 1 : -1);
							double yspeed = random.nextFloat() * 0.4;
							double zspeed = (random.nextFloat() * 0.08) * (random.nextBoolean() ? 1 : -1);
							worldIn.addParticle(ParticleTypes.END_ROD, d0, d1, d2, xspeed, yspeed, zspeed);
						}
					}
				}
				else
				{
					// System.out.println("Just missed the center area...");
					worldIn.playSound((Player) null, pos, SoundEvents.GLASS_HIT, SoundSource.BLOCKS, 1.0F, 1.0F);
				}
			}
			else if (worldIn.getBlockState(pos).getBlock().builtInRegistryHolder().key().location().getNamespace().equals("endrem"))
			{
				// compatibility for End:Remastered
				String blockid = worldIn.getBlockState(pos).getBlock().builtInRegistryHolder().key().location().getPath();
				if (isActivated(itemstack))
				{
					// System.out.println("Key already activated!");
					worldIn.playSound((Player) null, pos, SoundEvents.METAL_HIT, SoundSource.BLOCKS, 1.0F, 1.0F);
				}
				else if (blockid.equals("end_creator") || blockid.equals("end_creator_activated") || blockid.equals("ancient_portal_frame"))
				{
					performActivationHelper(player, itemstack, worldIn, pos, slot);
					return InteractionResult.SUCCESS;
				}
			}
			else if (isAlternateKeyActivationBlock(worldIn.getBlockState(pos).getBlock()))
			{
				// implement the block tag for alternate key chargers
				if (!isActivated(itemstack))
				{
					performActivationHelper(player, itemstack, worldIn, pos, slot);
					return InteractionResult.SUCCESS;
				}
				else
				{
					worldIn.playSound((Player) null, pos, SoundEvents.GLASS_HIT, SoundSource.BLOCKS, 1.0F, 1.0F);
				}
			}			
			else if (isBlockKeyCharger(worldIn.getBlockState(pos)))
			{
				// did they hit precisely the black area in the middle?
				if (hitX > 0.3f && hitX < 0.7f && hitZ > 0.3f && hitZ < 0.8f)
				{
					if (isActivated(itemstack))
					{
						// System.out.println("Key already activated!");
						worldIn.playSound((Player) null, pos, SoundEvents.METAL_HIT, SoundSource.BLOCKS, 1.0F, 1.0F);
					}
					else
					{
						performActivationHelper(player, itemstack, worldIn, pos, slot);

						// handle possible damage to the key activation station, similar to an anvil
						// running this block of code on the client can cause a flicker
						if (!worldIn.isClientSide())
						{
							String blockid = worldIn.getBlockState(pos).getBlock().builtInRegistryHolder().key().location().getPath();
							int roll = worldIn.getRandom().nextInt(100);
							if (blockid.equals(BlockRegistrar.REG_NAME_CHARGER_FULL))
							{
								if (roll < DungeonConfig.keyEnscriberDowngradeChanceFull)
								{
									worldIn.playSound((Player) null, pos, SoundEvents.ANVIL_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
									worldIn.setBlockAndUpdate(pos, BlockRegistrar.BLOCK_CHARGER_USED.get().defaultBlockState());
								}
								else
								{
									worldIn.playSound((Player) null, pos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
								}
							}
							else if (blockid.equals(BlockRegistrar.REG_NAME_CHARGER_USED))
							{
								if (roll < DungeonConfig.keyEnscriberDowngradeChanceUsed)
								{
									worldIn.playSound((Player) null, pos, SoundEvents.ANVIL_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
									worldIn.setBlockAndUpdate(pos, BlockRegistrar.BLOCK_CHARGER_DAMAGED.get().defaultBlockState());
								}
								else
								{
									worldIn.playSound((Player) null, pos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
								}
							}
							else if (blockid.equals(BlockRegistrar.REG_NAME_CHARGER_DAMAGED))
							{
								if (roll < DungeonConfig.keyEnscriberDowngradeChanceDamaged)
								{
									worldIn.playSound((Player) null, pos, SoundEvents.ANVIL_DESTROY, SoundSource.BLOCKS, 1.0F, 1.0F);
									worldIn.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
								}
								else
								{
									worldIn.playSound((Player) null, pos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
								}
							}
							return InteractionResult.SUCCESS;
						}
					}
				}
				else
				{
					// System.out.println("Just missed the center area...");
					worldIn.playSound((Player) null, pos, SoundEvents.GLASS_HIT, SoundSource.BLOCKS, 1.0F, 1.0F);
				}
			}
			else
			{
				// hit the side of the block
				worldIn.playSound((Player) null, pos, SoundEvents.GLASS_HIT, SoundSource.BLOCKS, 1.0F, 1.0F);
			}
		}

		return InteractionResult.PASS;
	}

	public ItemStack performActivationRitual(Player player, ItemStack itemstack, Level worldIn, BlockPos pos)
	{
		// System.out.println("Triggered special event to initialize key!");
		worldIn.playSound((Player) null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0F, 1.0F);
		if (!worldIn.isClientSide())
		{
			activateKeyLevel1(worldIn.getServer(), itemstack, 0);

			// since the condition is minecraft:impossible, this is the only way to trigger it
			ServerPlayer sp = worldIn.getServer().getPlayerList().getPlayer(player.getUUID());
			sp.getAdvancements().award(worldIn.getServer().getAdvancements().get(ResourceLocation.fromNamespaceAndPath(DimDungeons.MOD_ID, "dungeons/activate_basic_key")), "getkey");
		}

		createActivationParticleEffects(worldIn, pos);
		return null;
	}

	private void performActivationHelper(Player player, ItemStack itemstack, Level worldIn, BlockPos pos, int slot)
	{
		ItemStack newkey = performActivationRitual(player, itemstack, worldIn, pos);
		if ( newkey != null )
		{
			itemstack.shrink(1);
			if ( player.hasInfiniteMaterials() )
			{
				player.getInventory().setItem(slot, ItemStack.EMPTY); // no seriously, feels like a bug otherwise, even in creative mode
			}

			if (!player.getInventory().add(slot, newkey))
			{
				if (!player.addItem(newkey))
				{
					player.drop(newkey, false);
				}
			}
		}
	}

	// more particle effects for this special event!
	public void createActivationParticleEffects(Level worldIn, BlockPos pos)
	{
		RandomSource random = worldIn.getRandom();
		for (int i = 0; i < 32; i++)
		{
			double d0 = (double) ((float) pos.getX() + 0.5F);
			double d1 = (double) ((float) pos.getY() + 0.8F);
			double d2 = (double) ((float) pos.getZ() + 0.5F);
			double xspeed = (random.nextFloat() * 0.04) * (random.nextBoolean() ? 1 : -1);
			double yspeed = random.nextFloat() * 0.125;
			double zspeed = (random.nextFloat() * 0.04) * (random.nextBoolean() ? 1 : -1);
			worldIn.addParticle(ParticleTypes.FIREWORK, d0, d1, d2, xspeed, yspeed, zspeed);
		}
	}
	
	static public boolean isAlternateKeyActivationBlock(Block b)
	{
		return  b.builtInRegistryHolder().is(tag_alternate_activation_blocks);
	}
}