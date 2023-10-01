package com.catastrophe573.dimdungeons.item;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.DungeonConfig;
import com.catastrophe573.dimdungeons.block.BlockRegistrar;
import com.catastrophe573.dimdungeons.dimension.DungeonData;
import com.catastrophe573.dimdungeons.structure.DungeonDesigner.DungeonType;
import com.catastrophe573.dimdungeons.utils.DungeonUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EndPortalFrameBlock;
import net.minecraft.world.level.block.state.BlockState;

public class BaseItemKey extends Item
{
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

	public BaseItemKey(Item.Properties properties)
	{
		super(properties.stacksTo(1));
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
		CompoundTag data = new CompoundTag();
		data.putBoolean(NBT_KEY_ACTIVATED, true);
		data.putBoolean(NBT_BUILT, false);
		data.putInt(NBT_THEME, theme);
		data.putString(NBT_DUNGEON_TYPE, DungeonType.BASIC.toString());

		// where is this key going?
		long generation_limit = DungeonUtils.getLimitOfWorldBorder(server);
		long dungeonsPerLimit = generation_limit / BLOCKS_APART_PER_DUNGEON;
		int nextDungeonNumber = DungeonData.get(server.getLevel(DimDungeons.DUNGEON_DIMENSION)).getNumKeysRegistered() + 1;

		// go as far as possible on the z-axis, then the x-axis, staying in the positive x/z quadrant
		long destZ = nextDungeonNumber / dungeonsPerLimit;
		long destX = nextDungeonNumber % dungeonsPerLimit;
		data.putInt(NBT_KEY_DESTINATION_X, (int) destX);
		data.putInt(NBT_KEY_DESTINATION_Z, (int) destZ);

		// give it a funny random name
		RandomSource random = server.overworld().getRandom();
		int nameType = random.nextInt(3);
		if (theme > 0)
		{
			nameType = 2;
		}
		data.putInt(NBT_NAME_TYPE, nameType);
		if (nameType == 0 || nameType == 1)
		{
			data.putInt(NBT_NAME_PART_1, random.nextInt(32)); // key of noun & noun, key of finding noun in noun
			data.putInt(NBT_NAME_PART_2, random.nextInt(32));
		}
		else
		{
			data.putInt(NBT_NAME_PART_1, random.nextInt(20)); // key to the place of noun
			data.putInt(NBT_NAME_PART_2, random.nextInt(32));
		}

		stack.setTag(data);
		DungeonData.get(DungeonUtils.getDungeonWorld(server)).notifyOfNewKeyActivation();
	}

	public void activateKeyLevel2(MinecraftServer server, ItemStack stack)
	{
		CompoundTag data = new CompoundTag();
		data.putBoolean(NBT_KEY_ACTIVATED, true);
		data.putBoolean(NBT_BUILT, false);
		data.putInt(NBT_THEME, 0);
		data.putString(NBT_DUNGEON_TYPE, DungeonType.ADVANCED.toString());

		// where is this key going?
		long generation_limit = DungeonUtils.getLimitOfWorldBorder(server);
		long dungeonsPerLimit = generation_limit / BLOCKS_APART_PER_DUNGEON;
		long nextDungeonNumber = DungeonData.get(server.getLevel(DimDungeons.DUNGEON_DIMENSION)).getNumKeysRegistered() + 1;

		// go as far as possible on the z-axis, then the x-axis, staying in the positive x/z quadrant
		long destZ = nextDungeonNumber / dungeonsPerLimit;
		long destX = nextDungeonNumber % dungeonsPerLimit;
		data.putInt(NBT_KEY_DESTINATION_X, (int) destX);
		data.putInt(NBT_KEY_DESTINATION_Z, (int) destZ);

		// give it a funny random name like "Key to the [LARGE] [PLACE]"
		RandomSource random = server.overworld().getRandom();
		data.putInt(NBT_NAME_TYPE, 3);
		data.putInt(NBT_NAME_PART_1, random.nextInt(20)); // place
		data.putInt(NBT_NAME_PART_2, random.nextInt(12)); // largeness

		stack.setTag(data);
		DungeonData.get(DungeonUtils.getDungeonWorld(server)).notifyOfNewKeyActivation();
	}

	public void activateKeyForNewTeleporterHub(MinecraftServer server, ItemStack stack)
	{
		CompoundTag data = new CompoundTag();
		data.putBoolean(NBT_KEY_ACTIVATED, true);
		data.putBoolean(NBT_BUILT, false);
		data.putInt(NBT_THEME, 0); // the first door
		data.putString(NBT_DUNGEON_TYPE, DungeonType.TELEPORTER_HUB.toString());

		// where is this key going?
		long generation_limit = DungeonUtils.getLimitOfWorldBorder(server);
		long dungeonsPerLimit = generation_limit / BLOCKS_APART_PER_DUNGEON;
		int nextDungeonNumber = DungeonData.get(server.getLevel(DimDungeons.DUNGEON_DIMENSION)).getNumKeysRegistered() + 1;

		// go as far as possible on the z-axis, then the x-axis, staying in the positive x/z quadrant
		long destZ = nextDungeonNumber / dungeonsPerLimit;
		long destX = nextDungeonNumber % dungeonsPerLimit;
		data.putInt(NBT_KEY_DESTINATION_X, (int) destX);
		data.putInt(NBT_KEY_DESTINATION_Z, (int) destZ);

		// give it a funny random name
		RandomSource random = server.overworld().getRandom();
		data.putInt(NBT_NAME_TYPE, 4); // teleporter hub format
		data.putInt(NBT_NAME_PART_1, random.nextInt(32));

		stack.setTag(data);
		DungeonData.get(DungeonUtils.getDungeonWorld(server)).notifyOfNewKeyActivation();
	}

	public static void activateKeyForExistingTeleporterHub(MinecraftServer server, ItemStack stack, int destX, int destZ, int doorIndex)
	{
		CompoundTag data = new CompoundTag();
		data.putBoolean(NBT_KEY_ACTIVATED, true);
		data.putBoolean(NBT_BUILT, true); // the original key built this space
		data.putInt(NBT_THEME, doorIndex); // the eight doors are numbered 0-7 clockwise from the entrance
		data.putString(NBT_DUNGEON_TYPE, DungeonType.TELEPORTER_HUB.toString());

		// re-use the destX and destZ from the original key
		data.putInt(NBT_KEY_DESTINATION_X, destX);
		data.putInt(NBT_KEY_DESTINATION_Z, destZ);

		// give it a new name based on its door color
		data.putInt(NBT_NAME_TYPE, 5);
		data.putInt(NBT_NAME_PART_1, doorIndex);

		stack.setTag(data);
	}

	public boolean isActivated(ItemStack stack)
	{
		if (stack.hasTag())
		{
			if (stack.getTag().contains(NBT_KEY_ACTIVATED))
			{
				return true;
			}
		}
		return false;
	}

	public float getWarpX(ItemStack stack)
	{
		if (stack != null && !stack.isEmpty())
		{
			CompoundTag itemData = stack.getTag();
			if (itemData != null && itemData.contains(NBT_KEY_DESTINATION_X))
			{
				// teleporter keys make everything weird!
				if (itemData.contains(NBT_DUNGEON_TYPE))
				{
					DungeonType dtype = DungeonType.valueOf(itemData.getString(NBT_DUNGEON_TYPE));
					if (dtype == DungeonType.TELEPORTER_HUB)
					{
						float tempx = (itemData.getInt(NBT_KEY_DESTINATION_X) * BLOCKS_APART_PER_DUNGEON) + ENTRANCE_OFFSET_X;
						int doornum = itemData.getInt(NBT_THEME);
						int[] x_offset = { 0, -16, -21, -21, -16, 0, 5, 5 };
						return tempx + x_offset[doornum];
					}
				}

				return (itemData.getInt(NBT_KEY_DESTINATION_X) * BLOCKS_APART_PER_DUNGEON) + ENTRANCE_OFFSET_X;
			}
		}
		return -1;
	}

	public float getWarpZ(ItemStack stack)
	{
		if (stack != null && !stack.isEmpty())
		{
			CompoundTag itemData = stack.getTag();
			if (itemData != null && itemData.contains(NBT_KEY_DESTINATION_Z))
			{
				// teleporter keys make everything weird!
				if (itemData.contains(NBT_DUNGEON_TYPE))
				{
					DungeonType dtype = DungeonType.valueOf(itemData.getString(NBT_DUNGEON_TYPE));
					if (dtype == DungeonType.TELEPORTER_HUB)
					{
						float tempz = (itemData.getInt(NBT_KEY_DESTINATION_Z) * BLOCKS_APART_PER_DUNGEON) + ENTRANCE_OFFSET_Z;
						int doornum = itemData.getInt(NBT_THEME);
						int[] z_offset = { 0, 0, -5, -21, -26, -26, -21, -5 };
						return tempz + z_offset[doornum];
					}
				}

				return (itemData.getInt(NBT_KEY_DESTINATION_Z) * BLOCKS_APART_PER_DUNGEON) + ENTRANCE_OFFSET_Z;
			}
		}
		return -1;
	}

	public long getDungeonTopLeftX(ItemStack stack)
	{
		if (stack != null && !stack.isEmpty())
		{
			CompoundTag itemData = stack.getTag();
			if (itemData != null && itemData.contains(NBT_KEY_DESTINATION_X))
			{
				return (itemData.getInt(NBT_KEY_DESTINATION_X) * BLOCKS_APART_PER_DUNGEON);
			}
		}
		return -1;
	}

	public long getDungeonTopLeftZ(ItemStack stack)
	{
		if (stack != null && !stack.isEmpty())
		{
			CompoundTag itemData = stack.getTag();
			if (itemData != null && itemData.contains(NBT_KEY_DESTINATION_Z))
			{
				return (itemData.getInt(NBT_KEY_DESTINATION_Z) * BLOCKS_APART_PER_DUNGEON);
			}
		}
		return -1;
	}

	public int getDungeonTheme(ItemStack stack)
	{
		if (stack != null && !stack.isEmpty())
		{
			CompoundTag itemData = stack.getTag();
			if (itemData != null && itemData.contains(NBT_THEME))
			{
				return itemData.getInt(NBT_THEME);
			}
		}
		return -1;
	}

	public DungeonType getDungeonType(ItemStack stack)
	{
		if (stack != null && !stack.isEmpty())
		{
			CompoundTag itemData = stack.getTag();

			// keys created prior to version 153 will not have this field
			if (itemData != null && itemData.contains(NBT_DUNGEON_TYPE))
			{
				return DungeonType.valueOf(itemData.getString(NBT_DUNGEON_TYPE));
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
	public InteractionResult useOn(UseOnContext parameters)
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
		ItemStack itemstack = parameters.getItemInHand();

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
								performActivationRitual(player, itemstack, worldIn, pos);
								return InteractionResult.SUCCESS;
							}
						}
					}
					else
					{
						worldIn.setBlock(pos, iblockstate.setValue(EndPortalFrameBlock.HAS_EYE, Boolean.valueOf(false)), 2);
						worldIn.updateNeighbourForOutputSignal(pos, Blocks.END_PORTAL_FRAME);

						// do this if you want the key to break, too itemstack.shrink(1);

						// dramatic effect for what you just did!
						worldIn.playSound((Player) null, pos, SoundEvents.ENDER_EYE_DEATH, SoundSource.BLOCKS, 1.5F, 1.0F);
						worldIn.playSound((Player) null, pos, SoundEvents.ITEM_BREAK, SoundSource.BLOCKS, 0.4F, 1.5F);

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
					performActivationRitual(player, itemstack, worldIn, pos);
					return InteractionResult.SUCCESS;
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
						performActivationRitual(player, itemstack, worldIn, pos);

						// handle possible damage to the key activation station, similar to an anvil running this block of code on the client can cause a flicker
						if (!worldIn.isClientSide)
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

	public void performActivationRitual(Player player, ItemStack itemstack, Level worldIn, BlockPos pos)
	{
		// System.out.println("Triggered special event to initialize key!");
		worldIn.playSound((Player) null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0F, 1.0F);
		if (!worldIn.isClientSide)
		{
			activateKeyLevel1(worldIn.getServer(), itemstack, 0);
		}

		createActivationParticleEffects(worldIn, pos);
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
}
