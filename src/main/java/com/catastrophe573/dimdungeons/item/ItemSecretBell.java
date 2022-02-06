package com.catastrophe573.dimdungeons.item;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.utils.DungeonUtils;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class ItemSecretBell extends Item //extends TieredItem implements IVanishable
{
    public static final String REG_NAME = "item_secret_bell";

    // weapon properties (someday)
    //private final float attackDamage;
    //private final float attackSpeed;
    //private final Multimap<Attribute, AttributeModifier> attributeModifiers;

    // nbt properties
    public static final String NBT_UPGRADE = "upgrade";
    public static final String NBT_SECRET_X = "secret_x";
    public static final String NBT_SECRET_Y = "secret_y";
    public static final String NBT_SECRET_Z = "secret_z";

    public static final int BELL_COOLDOWN_TICKS = 80;

    public static final Tag.Named<Block> tag_secret_chime = BlockTags.bind("dimdungeons:dimdungeons_secret_chime_blocks");

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
	CompoundTag data = new CompoundTag();
	data.putInt(NBT_UPGRADE, level);
	data.putInt(NBT_SECRET_X, getSecretX(stack));
	data.putInt(NBT_SECRET_Y, getSecretY(stack));
	data.putInt(NBT_SECRET_Z, getSecretZ(stack));
	stack.setTag(data);
    }

    public void setSecretLocation(ItemStack stack, int x, int y, int z)
    {
	CompoundTag data = new CompoundTag();
	data.putInt(NBT_UPGRADE, getUpgradeLevel(stack));
	data.putInt(NBT_SECRET_X, x);
	data.putInt(NBT_SECRET_Y, y);
	data.putInt(NBT_SECRET_Z, z);
	stack.setTag(data);
    }

    @SuppressWarnings("resource")
    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn)
    {
	ItemStack itemstack = playerIn.getItemInHand(handIn);

	// do nothing on the client, let the server do the chest searching logic
	if (playerIn.getCommandSenderWorld().isClientSide)
	{
	    return new InteractionResultHolder<>(InteractionResult.PASS, itemstack);
	}

	// Only the level 2 bell may be used in any dimension. The level 1 bell works exclusively in the dungeon dimension.
	if (getUpgradeLevel(itemstack) < 2 && !DungeonUtils.isDimensionDungeon((Level) playerIn.getCommandSenderWorld()))
	{
	    return new InteractionResultHolder<>(InteractionResult.PASS, itemstack);
	}

	if (handIn == InteractionHand.MAIN_HAND)
	{
	    playerIn.getCooldowns().addCooldown(this, BELL_COOLDOWN_TICKS);

	    BlockPos secret = findSecretChestNearby(playerIn.blockPosition(), worldIn);
	    setSecretLocation(itemstack, secret.getX(), secret.getY(), secret.getZ());
	    return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemstack);
	}
	else
	{
	    return new InteractionResultHolder<>(InteractionResult.PASS, itemstack);
	}
    }

    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected)
    {
	if (!(entityIn instanceof ServerPlayer) || !isSelected)
	{
	    return;
	}
	ServerPlayer playerIn = (ServerPlayer) entityIn;

	// convert from percentage back to raw ticks
	int time = (int) (playerIn.getCooldowns().getCooldownPercent(this, 0) * BELL_COOLDOWN_TICKS);
	if (time == 0)
	{
	    return;
	}

	if (time == BELL_COOLDOWN_TICKS)
	{
	    playSoundAtPosition(worldIn, entityIn.blockPosition(), 13);
	}
	if (time == BELL_COOLDOWN_TICKS - 3)
	{
	    playSoundAtPosition(worldIn, entityIn.blockPosition(), 12);
	}

	// assume that worlds won't go beyond -10000, even in the upcoming 1.17
	if (getSecretY(stack) > -10000)
	{
	    BlockPos secretPos = new BlockPos(getSecretX(stack), getSecretY(stack), getSecretZ(stack));
	    if (time == BELL_COOLDOWN_TICKS - 6)
	    {
		playSoundAtPosition(worldIn, entityIn.blockPosition(), 9);
	    }
	    if (time == BELL_COOLDOWN_TICKS - 9)
	    {
		playSoundAtPosition(worldIn, entityIn.blockPosition(), 3);
	    }
	    if (time == BELL_COOLDOWN_TICKS - 12)
	    {
		playSoundAtPosition(worldIn, secretPos, 2);
	    }
	    if (time == BELL_COOLDOWN_TICKS - 15)
	    {
		playSoundAtPosition(worldIn, secretPos, 10);
	    }
	    if (time == BELL_COOLDOWN_TICKS - 18)
	    {
		playSoundAtPosition(worldIn, secretPos, 14);
	    }
	    if (time == BELL_COOLDOWN_TICKS - 21)
	    {
		playSoundAtPosition(worldIn, secretPos, 18);
	    }
	}
    }

    // copied from NoteBlocks kind of
    public void playSoundAtPosition(Level worldIn, BlockPos pos, int note)
    {
	float pitch = (float) Math.pow(2.0D, (double) (note - 12) / 12.0D);
	worldIn.playSound((Player) null, pos, NoteBlockInstrument.BELL.getSoundEvent(), SoundSource.PLAYERS, 3.0F, pitch);

	worldIn.addParticle(ParticleTypes.NOTE, (double) pos.getX() + 0.5D, (double) pos.getY() + 1.6D, (double) pos.getZ() + 0.5D, (double) note / 24.0D, 0.0D, 0.0D);
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
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity)
    {
	// I might use this function for something someday, like playing a sound effect maybe?
	return false;
    }

    // Current implementations of this method in child classes do not use the entry argument beside ev. They just raise the damage on the stack.
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker)
    {
	stack.hurtAndBreak(1, attacker, (entity) ->
	{
	    entity.broadcastBreakEvent(EquipmentSlot.MAINHAND);
	});

	// play a loud CLANG because it's funny
	attacker.getCommandSenderWorld().playSound((Player) null, target.blockPosition(), SoundEvents.BELL_BLOCK, SoundSource.BLOCKS, 2.0F, 1.0F);

	return true;
    }

    // Called when a Block is destroyed using this Item. Return true to trigger the "Use Item" statistic.
    // Players probably shouldn't be breaking things with a bell anyway, but they can.
    public boolean mineBlock(ItemStack stack, Level worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving)
    {
	if (state.getDestroySpeed(worldIn, pos) != 0.0F)
	{
	    stack.hurtAndBreak(2, entityLiving, (entity) ->
	    {
		entity.broadcastBreakEvent(EquipmentSlot.MAINHAND);
	    });
	}
	return true;
    }

    public boolean isCorrectToolForDrops(BlockState blockIn)
    {
	return false;
    }

    // Gets a map of item attribute modifiers, used by ItemSword to increase hit damage.
    //@SuppressWarnings("deprecation")
    //public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot)
    //{
    //	return equipmentSlot == EquipmentSlotType.MAINHAND ? this.attributeModifiers : super.getAttributeModifiers(equipmentSlot);
    //}

    private BlockPos findSecretChestNearby(BlockPos start, Level worldIn)
    {
	int startX = Math.floorDiv(start.getX(), 16) * 16;
	int startZ = Math.floorDiv(start.getZ(), 16) * 16;
	int startY = start.getY() - 8;

	// search the player's current chunk, 8 blocks up and 8 blocks down, for any inventory
	// if that block is tagged  then trigger the extended chime
	for (int x = startX; x < startX + 16; x++)
	{
	    for (int z = startZ; z < startZ + 16; z++)
	    {
		for (int y = startY; y < startY + 16; y++)
		{
		    BlockState bs = worldIn.getBlockState(new BlockPos(x, y, z));
		    if (!bs.isAir())
		    {
			DimDungeons.logMessageInfo("BLOCK: " + bs.getBlock().getRegistryName().getPath());
		    }
		    if (bs != null && bs.getBlock().getTags().contains(new ResourceLocation(DimDungeons.MOD_ID, "dimdungeons_secret_chime_blocks")))
		    {
			// the bell just rings for any tagged blocks (chests, barrels, etc)
			// no more checking for loot tables
			return new BlockPos(x, y, z);
		    }
		}
	    }
	}

	return new BlockPos(-1, -10000, -1);
    }
}
