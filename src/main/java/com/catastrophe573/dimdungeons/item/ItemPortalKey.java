package com.catastrophe573.dimdungeons.item;

import com.catastrophe573.dimdungeons.DimDungeons;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.EndPortalFrameBlock;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemPortalKey extends Item
{
    public static final String REG_NAME = "item_portal_key";

    public static final String NBT_KEY_ACTIVATED = "key_activated";
    public static final String NBT_KEY_DESTINATION_X = "dest_x";
    public static final String NBT_KEY_DESTINATION_Z = "dest_z";
    public static final String NBT_NAME_TYPE = "name_type";
    public static final String NBT_NAME_PART_1 = "name_part_1";
    public static final String NBT_NAME_PART_2 = "name_part_2";

    public static final int BLOCKS_APART_PER_DUNGEON = 256; // 16 chunks to try to keep "noise" or other interference from neighbors to a minimum (also makes maps work)
    public static final int RANDOM_COORDINATE_RANGE = 8192; // (0-8192 * 256) = 0 to 2,097,152
    public static final float ENTRANCE_OFFSET_X = 8.0f + (8 * 16); // applied when the player teleports in, centered on the two-block-wide return portal
    public static final float ENTRANCE_OFFSET_Z = 12.5f + (11 * 16); // applied when the player teleports in, centered on the two-block-wide return portal

    public ItemPortalKey()
    {
	super(new Item.Properties().group(ItemGroup.MISC).maxStackSize(1).group(ItemRegistrar.CREATIVE_TAB));
	this.setRegistryName(DimDungeons.MOD_ID, REG_NAME);
    }

    // used in the item model json to change the graphic based on the dimdungeons:keytype property
    public static float getKeyLevelAsFloat(ItemStack stack)
    {
	if (((ItemPortalKey) stack.getItem()).isActivated(stack))
	{
	    if (((ItemPortalKey) stack.getItem()).getWarpZ(stack) < 0)
	    {
		return 0.2f; // level 2 key
	    }
	    return 0.1f; // level 1 key
	}

	return 0.0f; // unactivated key	
    }
    
    public int getKeyLevel(ItemStack stack)
    {
	if ( !isActivated(stack) )
	{
	    return 0;
	}
	if ( getWarpZ(stack) < 0 )
	{
	    return 2;
	}
	return 1;
    }
    
    public void activateKey(ItemStack stack)
    {
	CompoundNBT data = new CompoundNBT();
	data.putBoolean(NBT_KEY_ACTIVATED, true);

	// where is this key going?
	int destX = random.nextInt(RANDOM_COORDINATE_RANGE);
	int destZ = random.nextInt(RANDOM_COORDINATE_RANGE);
	data.putInt(NBT_KEY_DESTINATION_X, destX);
	data.putInt(NBT_KEY_DESTINATION_Z, destZ);

	// give it a funny random name
	int nameType = random.nextInt(3);
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
    }

    // the only way to obtain level 2 keys is to find them already activated
    public void activateKeyLevel2(ItemStack stack)
    {
	CompoundNBT data = new CompoundNBT();
	data.putBoolean(NBT_KEY_ACTIVATED, true);

	// where is this key going?
	int destX = random.nextInt(RANDOM_COORDINATE_RANGE);
	int destZ = random.nextInt(RANDOM_COORDINATE_RANGE);
	data.putInt(NBT_KEY_DESTINATION_X, destX);
	data.putInt(NBT_KEY_DESTINATION_Z, destZ * -1);

	// give it a funny random name like "Key to the [LARGE] [PLACE]"
	data.putInt(NBT_NAME_TYPE, 3);
	data.putInt(NBT_NAME_PART_1, random.nextInt(20)); // place
	data.putInt(NBT_NAME_PART_2, random.nextInt(12)); // largeness

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

    @OnlyIn(Dist.CLIENT)
    @Override
    public ITextComponent getDisplayName(ItemStack stack)
    {
	// no NBT data on this item at all? well then return a blank key
	if (stack.hasTag())
	{
	    CompoundNBT itemData = stack.getTag();

	    if (itemData.contains(NBT_KEY_ACTIVATED))
	    {
		int nameType = itemData.contains(NBT_NAME_TYPE) ? itemData.getInt(NBT_NAME_TYPE) : 0;
		int word_index_1 = itemData.contains(NBT_NAME_PART_1) ? itemData.getInt(NBT_NAME_PART_1) : 2;
		int word_index_2 = itemData.contains(NBT_NAME_PART_2) ? itemData.getInt(NBT_NAME_PART_2) : 1;
		String retval = "";

		if (nameType == 0)
		{
		    String start = I18n.format("npart.dimdungeons.struct_1");
		    String preposition = I18n.format("npart.dimdungeons.struct_2");
		    String noun1 = I18n.format("npart.dimdungeons.noun_" + word_index_1);
		    String noun2 = I18n.format("npart.dimdungeons.noun_" + word_index_2);
		    if (word_index_1 == word_index_2)
		    {
			retval = start + " " + noun1;
		    }
		    else
		    {
			retval = start + " " + noun1 + " " + preposition + " " + noun2;
		    }
		}
		else if (nameType == 1)
		{
		    String start = I18n.format("npart.dimdungeons.struct_3");
		    String preposition = I18n.format("npart.dimdungeons.struct_4");
		    String noun1 = I18n.format("npart.dimdungeons.noun_" + word_index_1);
		    String noun2 = I18n.format("npart.dimdungeons.noun_" + word_index_2);
		    if (word_index_1 == word_index_2)
		    {
			retval = start + " " + noun1;
		    }
		    else
		    {
			retval = start + " " + noun1 + " " + preposition + " " + noun2;
		    }
		}
		else if (nameType == 2)
		{
		    String start = I18n.format("npart.dimdungeons.struct_5");
		    String preposition = I18n.format("npart.dimdungeons.struct_6");
		    String place = I18n.format("npart.dimdungeons.place_" + word_index_1);
		    String noun = I18n.format("npart.dimdungeons.noun_" + word_index_2);
		    retval = start + " " + place + " " + preposition + " " + noun;
		}
		else if (nameType == 3)
		{
		    String start = I18n.format("npart.dimdungeons.struct_7");
		    String place = I18n.format("npart.dimdungeons.place_" + word_index_1);
		    String largeness = I18n.format("npart.dimdungeons.large_" + word_index_2);
		    retval = start + " " + largeness + " " + place;
		}

		return new StringTextComponent(retval);
	    }
	}

	// basically return "Blank Portal Key"
	//return I18n.format(stack.getTranslationKey());
	return new TranslationTextComponent(this.getTranslationKey(stack), new Object[0]);
    }

    public float getWarpX(ItemStack stack)
    {
	if (stack != null && !stack.isEmpty())
	{
	    CompoundNBT itemData = stack.getTag();
	    if (itemData != null && itemData.contains(NBT_KEY_DESTINATION_X))
	    {
		return (itemData.getInt(NBT_KEY_DESTINATION_X) * BLOCKS_APART_PER_DUNGEON) + ENTRANCE_OFFSET_X;
	    }
	}
	return -1;
    }

    public float getWarpZ(ItemStack stack)
    {
	if (stack != null && !stack.isEmpty())
	{
	    CompoundNBT itemData = stack.getTag();
	    if (itemData != null && itemData.contains(NBT_KEY_DESTINATION_Z))
	    {
		float z = (itemData.getInt(NBT_KEY_DESTINATION_Z) * BLOCKS_APART_PER_DUNGEON) + ENTRANCE_OFFSET_Z;
		return z;
	    }
	}
	return -1;
    }

    @Override
    //public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    public ActionResultType onItemUse(ItemUseContext parameters)
    {
	// break down the one 1.13 parameter to get the half dozen 1.12 parameters because I need most of them
	World worldIn = parameters.getWorld();
	BlockPos pos = parameters.getPos();
	Direction facing = parameters.getFace();
	double hitX = parameters.getHitVec().getX();
	//float hitY = parameters.getHitY();
	double hitZ = parameters.getHitVec().getZ();
	//EntityPlayer player = parameters.getPlayer();	

	BlockState iblockstate = worldIn.getBlockState(pos);
	ItemStack itemstack = parameters.getItem();

	// new in 1.13 the hit vector contains world coordinates in the integer part, and I would like just the decimal part
	hitX = Math.abs((int) hitX - hitX);
	hitZ = Math.abs((int) hitZ - hitZ);

	if (worldIn.getBlockState(pos) != null)
	{
	    //System.out.println("Used a key on some block: " + worldIn.getBlockState(pos).getBlock().getRegistryName());
	    //System.out.println("Hit it here: " + hitX + ", " + hitZ + ", facing=" + facing.getName());

	    // did they use the key on an end portal frame?
	    if (worldIn.getBlockState(pos).getBlock() == Blocks.END_PORTAL_FRAME)
	    {
		boolean isFilled = ((Boolean) worldIn.getBlockState(pos).get(EndPortalFrameBlock.EYE)).booleanValue();

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
				//System.out.println("Key already activated!");
				worldIn.playSound((PlayerEntity) null, pos, SoundEvents.BLOCK_METAL_HIT, SoundCategory.BLOCKS, 1.0F, 1.0F);
			    }
			    else
			    {
				//System.out.println("Triggered special event to initialize key!");
				worldIn.playSound((PlayerEntity) null, pos, SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.BLOCKS, 1.0F, 1.0F);
				if (pos.getX() == 0 && pos.getZ() == 0)
				{
				    activateKeyLevel2(itemstack); // for debugging only, End Portal Frames should never appear at (0,0) in the Overworld and this is not intended
				}
				else
				{
				    activateKey(itemstack);
				}

				// more particle effects for this special event!
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
		    }
		    else
		    {
			worldIn.setBlockState(pos, iblockstate.with(EndPortalFrameBlock.EYE, Boolean.valueOf(false)), 2);
			worldIn.updateComparatorOutputLevel(pos, Blocks.END_PORTAL_FRAME);

			// do this if you want the key to break, too
			//itemstack.shrink(1);

			// dramatic effect for what you just did!
			worldIn.playSound((PlayerEntity) null, pos, SoundEvents.ENTITY_ENDER_EYE_DEATH, SoundCategory.BLOCKS, 1.5F, 1.0F);
			worldIn.playSound((PlayerEntity) null, pos, SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.BLOCKS, 0.4F, 1.5F);

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
		    //System.out.println("Just missed the center area...");
		    worldIn.playSound((PlayerEntity) null, pos, SoundEvents.BLOCK_GLASS_HIT, SoundCategory.BLOCKS, 1.0F, 1.0F);
		}
	    }
	    else
	    {
		// hit the side of the block
		worldIn.playSound((PlayerEntity) null, pos, SoundEvents.BLOCK_GLASS_HIT, SoundCategory.BLOCKS, 1.0F, 1.0F);
	    }
	}

	return ActionResultType.PASS;
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
	return false;
    }
}
