package com.catastrophe573.dimdungeons.item;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.DungeonConfig;
import com.catastrophe573.dimdungeons.utils.DungeonUtils;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;

public class ItemSecretBell extends Item // extends TieredItem implements IVanishable
{
	public static final String REG_NAME = "item_secret_bell";

	// weapon properties (someday)
	// private final float attackDamage;
	// private final float attackSpeed;
	// private final Multimap<Attribute, AttributeModifier> attributeModifiers;

	// nbt properties
	public static final String NBT_UPGRADE = "upgrade";
	public static final String NBT_SECRET_X = "secret_x";
	public static final String NBT_SECRET_Y = "secret_y";
	public static final String NBT_SECRET_Z = "secret_z";

	public static final int BELL_COOLDOWN_TICKS = 60;

	public static final TagKey<Block> tag_secret_chime = BlockTags.create(ResourceLocation.fromNamespaceAndPath(DimDungeons.MOD_ID, "secret_chime_blocks"));

	public ItemSecretBell(/* IItemTier tier, */ Item.Properties builderIn)
	{
		// super(tier, builderIn);
		super(builderIn.setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(DimDungeons.MOD_ID, REG_NAME))));
	}

	// used in the item model json to change the graphic based on the dimdungeons:keytype property
	public static float getUpgradeLevelAsFloat(ItemStack stack)
	{
		if (((ItemSecretBell) stack.getItem()).getUpgradeLevel(stack) == 2)
		{
			return 0.2f; // level 2 bell
		}
		return 0.1f;
	}

	public int getUpgradeLevel(ItemStack stack)
	{
		if (stack != null && !stack.isEmpty() && stack.has(DimDungeons.SECRET_BELL_DATA))
		{
			return stack.get(DimDungeons.SECRET_BELL_DATA).upgrade();
		}
		return 1;
	}

	public int getSecretX(ItemStack stack)
	{
		if (stack != null && !stack.isEmpty() && stack.has(DimDungeons.SECRET_BELL_DATA))
		{
			return stack.get(DimDungeons.SECRET_BELL_DATA).secret_x();
		}
		return -1;
	}

	public int getSecretY(ItemStack stack)
	{
		if (stack != null && !stack.isEmpty() && stack.has(DimDungeons.SECRET_BELL_DATA))
		{
			return stack.get(DimDungeons.SECRET_BELL_DATA).secret_y();
		}
		return -1;
	}

	public int getSecretZ(ItemStack stack)
	{
		if (stack != null && !stack.isEmpty() && stack.has(DimDungeons.SECRET_BELL_DATA))
		{
			return stack.get(DimDungeons.SECRET_BELL_DATA).secret_z();
		}
		return -1;
	}

	public void setUpgradeLevel(ItemStack stack, int level)
	{
		SecretBellDataComponentRecord newData = new SecretBellDataComponentRecord(
				level,
				-1,
				-1,
				-1
		);

		stack.set(DimDungeons.SECRET_BELL_DATA, newData);
	}

	public void setSecretLocation(ItemStack stack, int x, int y, int z)
	{
		int upgradeLevel = this.getUpgradeLevel(stack);

		SecretBellDataComponentRecord newData = new SecretBellDataComponentRecord(
				upgradeLevel,
				x,
				y,
				z
		);

		stack.set(DimDungeons.SECRET_BELL_DATA, newData);
	}

	@SuppressWarnings("resource")
	@Override
	public InteractionResult use(Level worldIn, Player playerIn, InteractionHand handIn)
	{
		ItemStack itemstack = playerIn.getItemInHand(handIn);

		// do nothing on the client, let the server do the chest searching logic
		if (playerIn.getCommandSenderWorld().isClientSide)
		{
			return InteractionResult.PASS;
		}

		// Only the level 2 bell may be used in any dimension. The level 1 bell works exclusively in the dungeon dimension.
		if (getUpgradeLevel(itemstack) < 2 && !DungeonUtils.isDimensionDungeon((Level) playerIn.getCommandSenderWorld()))
		{
			return InteractionResult.PASS;
		}

		if (handIn == InteractionHand.MAIN_HAND)
		{
			playerIn.getCooldowns().addCooldown(ItemRegistrar.ITEM_SECRET_BELL.getId(), BELL_COOLDOWN_TICKS);

			BlockPos secret = findSecretChestNearby(playerIn.blockPosition(), worldIn);
			setSecretLocation(itemstack, secret.getX(), secret.getY(), secret.getZ());
			return InteractionResult.PASS;
		}
		else
		{
			return InteractionResult.PASS;
		}
	}

	public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected)
	{
		if (!(entityIn instanceof Player) || !isSelected)
		{
			return;
		}
		Player playerIn = (Player) entityIn;

		// convert from percentage back to raw ticks
		int time = (int) (playerIn.getCooldowns().getCooldownPercent(stack, 0) * BELL_COOLDOWN_TICKS);
		if (time == 0)
		{
			return;
		}

		if (time == BELL_COOLDOWN_TICKS)
		{
			playSoundAtPosition(worldIn, entityIn.getX(), entityIn.getY() + 1.0d, entityIn.getZ(), 13);
		}
		if (time == BELL_COOLDOWN_TICKS - 3)
		{
			playSoundAtPosition(worldIn, entityIn.getX(), entityIn.getY() + 1.0d, entityIn.getZ(), 12);
		}

		// assume that worlds won't go beyond -10000, even in the upcoming 1.17
		if (getSecretY(stack) > -10000)
		{
			BlockPos secretPos = new BlockPos(getSecretX(stack), getSecretY(stack) + 1, getSecretZ(stack));
			if (time == BELL_COOLDOWN_TICKS - 6)
			{
				playSoundAtPosition(worldIn, entityIn.getX(), entityIn.getY() + 1.0d, entityIn.getZ(), 9);
			}
			if (time == BELL_COOLDOWN_TICKS - 9)
			{
				playSoundAtPosition(worldIn, entityIn.getX(), entityIn.getY() + 1.0d, entityIn.getZ(), 3);
			}
			if (time == BELL_COOLDOWN_TICKS - 12)
			{
				playSoundAtPosition(worldIn, secretPos.getX() + 0.5d, secretPos.getY() + 0.2d, secretPos.getZ() + 0.5d, 2);
			}
			if (time == BELL_COOLDOWN_TICKS - 15)
			{
				playSoundAtPosition(worldIn, secretPos.getX() + 0.5d, secretPos.getY() + 0.2d, secretPos.getZ() + 0.5d, 10);
			}
			if (time == BELL_COOLDOWN_TICKS - 18)
			{
				playSoundAtPosition(worldIn, secretPos.getX() + 0.5d, secretPos.getY() + 0.2d, secretPos.getZ() + 0.5d, 14);
			}
			if (time == BELL_COOLDOWN_TICKS - 21)
			{
				playSoundAtPosition(worldIn, secretPos.getX() + 0.5d, secretPos.getY() + 0.2d, secretPos.getZ() + 0.5d, 18);
			}
		}
	}

	// copied from NoteBlocks kind of
	public void playSoundAtPosition(Level worldIn, double x, double y, double z, int note)
	{
		float pitch = (float) Math.pow(2.0D, (double) (note - 12) / 12.0D);

		worldIn.playLocalSound(x, y, z, NoteBlockInstrument.BELL.getSoundEvent().value(), SoundSource.PLAYERS, note, pitch, false);

		if (DungeonConfig.showParticles)
		{
			worldIn.addParticle(ParticleTypes.NOTE, x + 1.0d, y, z + 0.0d, (double) note / 24.0D, 0.0D, 0.0D);
			worldIn.addParticle(ParticleTypes.NOTE, x - 1.0d, y, z + 0.0d, (double) note / 24.0D, 0.0D, 0.0D);
			worldIn.addParticle(ParticleTypes.NOTE, x + 0.0d, y, z + 1.0d, (double) note / 24.0D, 0.0D, 0.0D);
			worldIn.addParticle(ParticleTypes.NOTE, x + 0.0d, y, z - 1.0d, (double) note / 24.0D, 0.0D, 0.0D);
		}
	}

	/**
	 * Called when the player Left Clicks (attacks) an entity. Processed before damage is done, if return value is true further processing is canceled and the entity is not attacked.
	 *
	 * @param stack  The Item being used
	 * @param player The player that is attacking
	 * @param entity The entity being attacked
	 * @return True to cancel the rest of the interaction.
	 */
	@Override
	public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity)
	{
		// I might use this function for something someday, like playing a sound effect maybe?
		return false;
	}

	// Current implementations of this method in child classes do not use the entry argument beside ev. They just raise the damage on the stack.
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker)
	{
		// play a loud CLANG because it's funny
		attacker.getCommandSenderWorld().playSound((Player) null, target.blockPosition(), SoundEvents.BELL_BLOCK, SoundSource.BLOCKS, 2.0F, 1.0F);

		return true;
	}

	public boolean isCorrectToolForDrops(BlockState blockIn)
	{
		return false;
	}

	@SuppressWarnings("deprecation")
	private BlockPos findSecretChestNearby(BlockPos start, Level worldIn)
	{
		int startX = Math.floorDiv(start.getX(), 16) * 16;
		int startZ = Math.floorDiv(start.getZ(), 16) * 16;
		int startY = start.getY() - 8;

		// search the player's current chunk, 8 blocks up and 8 blocks down, for any inventory if that block is tagged then trigger the extended chime
		for (int x = startX; x < startX + 16; x++)
		{
			for (int z = startZ; z < startZ + 16; z++)
			{
				for (int y = startY; y < startY + 16; y++)
				{
					BlockState bs = worldIn.getBlockState(new BlockPos(x, y, z));

					if (bs != null)
					{
						boolean tagged = bs.getBlock().builtInRegistryHolder().is(tag_secret_chime);

						if (tagged)
						{
							BlockEntity te = worldIn.getBlockEntity(new BlockPos(x, y, z));
							if (te instanceof RandomizableContainerBlockEntity)
							{
								ResourceKey<LootTable> lt = ((RandomizableContainerBlockEntity) te).getLootTable();
								if ( lt == null )
								{
									continue;
								}
							}

							// if the tagged block isn't a container that doesn't do loot tables (like a Lootr container) then just ring anyway
							return new BlockPos(x, y, z);
						}
					}
				}
			}
		}

		return new BlockPos(-1, -10000, -1);
	}
}