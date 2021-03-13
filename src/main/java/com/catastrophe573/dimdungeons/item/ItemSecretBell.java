package com.catastrophe573.dimdungeons.item;

import org.apache.commons.lang3.reflect.FieldUtils;

import com.catastrophe573.dimdungeons.DimDungeons;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.properties.NoteBlockInstrument;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemSecretBell extends Item //extends TieredItem implements IVanishable
{
    public static final String REG_NAME = "item_secret_bell";
    public static final int COOLDOWN_LENGTH = 100;

    // weapon properties (someday)
    //private final float attackDamage;
    //private final float attackSpeed;
    //private final Multimap<Attribute, AttributeModifier> attributeModifiers;

    // nbt properties
    public static final String NBT_UPGRADE = "upgrade";
    public static final String NBT_COOLDOWN = "cooldown";
    public static final String NBT_SECRET_X = "secret_x";
    public static final String NBT_SECRET_Y = "secret_y";
    public static final String NBT_SECRET_Z = "secret_z";

    public ItemSecretBell(/* IItemTier tier, */ Item.Properties builderIn)
    {
	//super(tier, builderIn);
	super(builderIn);
	this.setRegistryName(DimDungeons.MOD_ID, REG_NAME);

	// this item isn't really "tierable" I just want it to be a reparable weapon
	//this.attackDamage = 3; // appropriate for gold tier, but also will be configurable later
	//this.attackSpeed = -2.0f; // appropriate for gold tier, but also will be configurable later
	//Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
	//builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", (double) this.attackDamage, AttributeModifier.Operation.ADDITION));
	//builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", (double) attackSpeed, AttributeModifier.Operation.ADDITION));
	//this.attributeModifiers = builder.build();
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
	if (stack.hasTag())
	{
	    if (stack.getTag().contains(NBT_UPGRADE))
	    {
		return stack.getTag().getInt(NBT_UPGRADE);
	    }
	}
	return 1;
    }

    public int getCooldownTimer(ItemStack stack)
    {
	if (stack.hasTag())
	{
	    if (stack.getTag().contains(NBT_COOLDOWN))
	    {
		return stack.getTag().getInt(NBT_COOLDOWN);
	    }
	}
	return 0; // off cooldown, ready to use
    }

    public int getSecretX(ItemStack stack)
    {
	if (stack.hasTag())
	{
	    if (stack.getTag().contains(NBT_SECRET_X))
	    {
		return stack.getTag().getInt(NBT_SECRET_X);
	    }
	}
	return -1;
    }

    public int getSecretY(ItemStack stack)
    {
	if (stack.hasTag())
	{
	    if (stack.getTag().contains(NBT_SECRET_Y))
	    {
		return stack.getTag().getInt(NBT_SECRET_Y);
	    }
	}
	return -1;
    }

    public int getSecretZ(ItemStack stack)
    {
	if (stack.hasTag())
	{
	    if (stack.getTag().contains(NBT_SECRET_Z))
	    {
		return stack.getTag().getInt(NBT_SECRET_Z);
	    }
	}
	return -1;
    }

    public void setUpgradeLevel(ItemStack stack, int level)
    {
	CompoundNBT data = new CompoundNBT();
	data.putInt(NBT_UPGRADE, level);
	data.putInt(NBT_COOLDOWN, getCooldownTimer(stack));
	data.putInt(NBT_SECRET_X, getSecretX(stack));
	data.putInt(NBT_SECRET_Y, getSecretY(stack));
	data.putInt(NBT_SECRET_Z, getSecretZ(stack));
	stack.setTag(data);
    }

    public void setCooldownTimer(ItemStack stack, int value)
    {
	CompoundNBT data = new CompoundNBT();
	data.putInt(NBT_UPGRADE, getUpgradeLevel(stack));
	data.putInt(NBT_COOLDOWN, value);
	data.putInt(NBT_SECRET_X, getSecretX(stack));
	data.putInt(NBT_SECRET_Y, getSecretY(stack));
	data.putInt(NBT_SECRET_Z, getSecretZ(stack));
	stack.setTag(data);
    }

    public void decrementCooldownTimer(ItemStack stack)
    {
	setCooldownTimer(stack, Math.max(0, getCooldownTimer(stack) - 1));
    }

    public void setSecretLocation(ItemStack stack, int x, int y, int z)
    {
	CompoundNBT data = new CompoundNBT();
	data.putInt(NBT_UPGRADE, getUpgradeLevel(stack));
	data.putInt(NBT_COOLDOWN, getCooldownTimer(stack));
	data.putInt(NBT_SECRET_X, x);
	data.putInt(NBT_SECRET_Y, y);
	data.putInt(NBT_SECRET_Z, z);
	stack.setTag(data);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn)
    {
	ItemStack itemstack = playerIn.getHeldItem(handIn);

	if (getCooldownTimer(itemstack) <= 0 && handIn == Hand.MAIN_HAND)
	{
	    setCooldownTimer(itemstack, COOLDOWN_LENGTH);

	    BlockPos secret = findSecretChestNearby(playerIn.getPosition(), worldIn);
	    setSecretLocation(itemstack, secret.getX(), secret.getY(), secret.getZ());
	    return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
	}
	else
	{
	    return new ActionResult<>(ActionResultType.PASS, itemstack);
	}
    }

    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
    {
	int time = getCooldownTimer(stack);
	if (time == 0)
	{
	    return;
	}
	decrementCooldownTimer(stack);

	if (time == COOLDOWN_LENGTH)
	{
	    playSoundAtPosition(worldIn, entityIn.getPosition(), 13, false);
	}
	if (time == COOLDOWN_LENGTH - 5)
	{
	    playSoundAtPosition(worldIn, entityIn.getPosition(), 12, false);
	}
	if (time == COOLDOWN_LENGTH - 10)
	{
	    playSoundAtPosition(worldIn, entityIn.getPosition(), 9, false);
	}
	if (time == COOLDOWN_LENGTH - 15)
	{
	    playSoundAtPosition(worldIn, entityIn.getPosition(), 3, false);
	}

	if (getSecretY(stack) > -10000)
	{
	    BlockPos secretPos = new BlockPos(getSecretX(stack), getSecretY(stack), getSecretZ(stack));
	    if (time == COOLDOWN_LENGTH - 20)
	    {
		playSoundAtPosition(worldIn, secretPos, 2, false);
	    }
	    if (time == COOLDOWN_LENGTH - 25)
	    {
		playSoundAtPosition(worldIn, secretPos, 10, false);
	    }
	    if (time == COOLDOWN_LENGTH - 30)
	    {
		playSoundAtPosition(worldIn, secretPos, 14, false);
	    }
	    if (time == COOLDOWN_LENGTH - 35)
	    {
		playSoundAtPosition(worldIn, secretPos, 18, false);
	    }
	}

	// if the player puts the bell away then stop all ringing
	if (!isSelected)
	{
	    setCooldownTimer(stack, 0);
	}
    }

    // copied from NoteBlocks kind of
    public void playSoundAtPosition(World worldIn, BlockPos pos, int note, boolean secretParticles)
    {
	float pitch = (float) Math.pow(2.0D, (double) (note - 12) / 12.0D);
	worldIn.playSound((PlayerEntity) null, pos, NoteBlockInstrument.BELL.getSound(), SoundCategory.RECORDS, 3.0F, pitch);

	worldIn.addParticle(ParticleTypes.NOTE, (double) pos.getX() + 0.5D, (double) pos.getY() + 1.8D, (double) pos.getZ() + 0.5D, 0.0D, 2.0D, 0.0D);
	worldIn.addParticle(ParticleTypes.NOTE, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, 1.0D, 0.0D, 0.0D);
	worldIn.addParticle(ParticleTypes.NOTE, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, 1.0D, 0.0D, 0.0D);
	worldIn.addParticle(ParticleTypes.NOTE, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, 0.0D, 0.0D, 1.0D);
	worldIn.addParticle(ParticleTypes.NOTE, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, 0.0D, 0.0D, 1.0D);
    }

    /**
     * Called when the player Left Clicks (attacks) an entity. Processed before damage is done, if return value is true
     * further processing is canceled and the entity is not attacked.
     *
     * @param stack  The Item being used
     * @param player The player that is attacking
     * @param entity The entity being attacked
     * @return True to cancel the rest of the interaction.
     */
    @Override
    public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity)
    {
	// I might use this function for something someday, like playing a sound effect maybe?
	return false;
    }

    // Current implementations of this method in child classes do not use the entry argument beside ev. They just raise the damage on the stack.
    public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker)
    {
	stack.damageItem(1, attacker, (entity) ->
	{
	    entity.sendBreakAnimation(EquipmentSlotType.MAINHAND);
	});

	// play a loud CLANG because it's funny
	attacker.getEntityWorld().playSound((PlayerEntity) null, target.getPosition(), SoundEvents.BLOCK_BELL_USE, SoundCategory.BLOCKS, 2.0F, 1.0F);

	return true;
    }

    // Called when a Block is destroyed using this Item. Return true to trigger the "Use Item" statistic.
    // Players probably shouldn't be breaking things with a bell anyway, but they can.
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving)
    {
	if (state.getBlockHardness(worldIn, pos) != 0.0F)
	{
	    stack.damageItem(2, entityLiving, (entity) ->
	    {
		entity.sendBreakAnimation(EquipmentSlotType.MAINHAND);
	    });
	}
	return true;
    }

    public boolean canHarvestBlock(BlockState blockIn)
    {
	return false;
    }

    // Gets a map of item attribute modifiers, used by ItemSword to increase hit damage.
    //@SuppressWarnings("deprecation")
    //public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot)
    //{
    //	return equipmentSlot == EquipmentSlotType.MAINHAND ? this.attributeModifiers : super.getAttributeModifiers(equipmentSlot);
    //}

    private BlockPos findSecretChestNearby(BlockPos start, World worldIn)
    {
	// TODO: this math is wrong, but the item isn't being released until 1.1 so for 1.093 it can stay broken
	int startX = start.getX() > 0 ? start.getX() - (start.getX() % 16) : start.getX() + (start.getX() % 16);
	int startY = start.getY() - 8;
	int startZ = start.getZ() > 0 ? start.getZ() - (start.getZ() % 16) : start.getZ() + (start.getZ() % 16);

	// search the player's current chunk, 8 blocks up and 8 blocks down, for any inventory
	// if that inventory has a loot table then it counts as an "unopened chest" and it triggers the secret chime
	for (int x = startX; x < startX + 16; x++)
	{
	    for (int z = startZ; z < startZ + 16; z++)
	    {
		for (int y = startY; y < startY + 16; y++)
		{
		    TileEntity te = worldIn.getTileEntity(new BlockPos(x, y, z));
		    if (te != null && te instanceof LockableLootTileEntity)
		    {
			boolean hasLootTable = false;
			try
			{
			    hasLootTable = FieldUtils.readField(te, "lootTable", true) != null;
			}
			catch (IllegalAccessException e)
			{
			    // not a problem
			}

			if (hasLootTable)
			{
			    DimDungeons.LOGGER.info("FOUND A SECRET " + x + ", " + y + ", " + z);
			    return new BlockPos(x, y, z);
			}
		    }
		}
	    }
	}

	return new BlockPos(-1, -9999, -1);
    }
}