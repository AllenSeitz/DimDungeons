package com.catastrophe573.dimdungeons.block;

import java.util.Random;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.item.ItemPortalKey;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.thread.EffectiveSide;

public class BlockPortalKeyhole extends Block
{
    public static final DirectionProperty FACING = BlockHorizontal.HORIZONTAL_FACING;
    public static final BooleanProperty FILLED = BooleanProperty.create("filled");
    public static final BooleanProperty LIT = BooleanProperty.create("lit");

    public static String REG_NAME = "block_portal_keyhole";

    public BlockPortalKeyhole()
    {
	super(Block.Builder.create(Material.PORTAL).hardnessAndResistance(2).sound(SoundType.METAL));
	this.setRegistryName(DimDungeons.MOD_ID, REG_NAME);
	this.setDefaultState(getMyCustomDefaultState());
    }

    public IBlockState getMyCustomDefaultState()
    {
	return this.stateContainer.getBaseState().with(FACING, EnumFacing.NORTH).with(FILLED, false).with(LIT, false);
    }

    // Called after the block is set in the Chunk data, but before the Tile Entity is set
    @Override
    public void onBlockAdded(IBlockState state, World worldIn, BlockPos pos, IBlockState oldState)
    {
	this.setDefaultFacing(worldIn, pos, state);
    }

    // copied from vanilla furnaces in 1.12
    private void setDefaultFacing(World worldIn, BlockPos pos, IBlockState state)
    {
	if (EffectiveSide.get() == LogicalSide.SERVER)
	{
	    IBlockState iblockstate = worldIn.getBlockState(pos.north());
	    IBlockState iblockstate1 = worldIn.getBlockState(pos.south());
	    IBlockState iblockstate2 = worldIn.getBlockState(pos.west());
	    IBlockState iblockstate3 = worldIn.getBlockState(pos.east());
	    EnumFacing enumfacing = (EnumFacing) state.get(FACING);

	    if (enumfacing == EnumFacing.NORTH && iblockstate.isFullCube() && !iblockstate1.isFullCube())
	    {
		enumfacing = EnumFacing.SOUTH;
	    }
	    else if (enumfacing == EnumFacing.SOUTH && iblockstate1.isFullCube() && !iblockstate.isFullCube())
	    {
		enumfacing = EnumFacing.NORTH;
	    }
	    else if (enumfacing == EnumFacing.WEST && iblockstate2.isFullCube() && !iblockstate3.isFullCube())
	    {
		enumfacing = EnumFacing.EAST;
	    }
	    else if (enumfacing == EnumFacing.EAST && iblockstate3.isFullCube() && !iblockstate2.isFullCube())
	    {
		enumfacing = EnumFacing.WEST;
	    }

	    worldIn.setBlockState(pos, state.with(FACING, enumfacing), 2);
	}
    }

    // based on code from vanilla furnaces, which also play a sound effect and make particles when their TileEntity is being productive
    @OnlyIn(Dist.CLIENT)
    @Override
    //public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
    public void animateTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
	if (stateIn.get(LIT))
	{
	    EnumFacing enumfacing = (EnumFacing) stateIn.get(FACING);
	    double d0 = (double) pos.getX() + 0.5D;
	    double d1 = (double) pos.getY() + rand.nextDouble() * 6.0D / 16.0D;
	    double d2 = (double) pos.getZ() + 0.5D;
	    double d4 = rand.nextDouble() * 0.6D - 0.3D;

	    // play sound effects randomly
	    if (rand.nextDouble() < 0.1D)
	    {
		worldIn.playSound((double) pos.getX() + 0.5D, (double) pos.getY(), (double) pos.getZ() + 0.5D, SoundEvents.BLOCK_PORTAL_AMBIENT, SoundCategory.BLOCKS, 1.0F, 3.0F, false);
	    }

	    switch (enumfacing)
	    {
	    case EAST:
		worldIn.spawnParticle(Particles.PORTAL, d0 - 0.52D, d1 - 1, d2 + d4, 1.0D, 1.0D, 0.0D);
		worldIn.spawnParticle(Particles.FIREWORK, d0 + 0.12D, d1 + 1.5D, d2 + d4, 0.0D, 0.0D, 0.23D);
		worldIn.spawnParticle(Particles.FIREWORK, d0 + 0.12D, d1 + 1.5D, d2 + d4, 0.0D, 0.0D, -0.23D);
		break;
	    case WEST:
		worldIn.spawnParticle(Particles.PORTAL, d0 + 0.52D, d1 - 1, d2 + d4, -1.0D, 1.0D, 0.0D);
		worldIn.spawnParticle(Particles.FIREWORK, d0 - 0.12D, d1 + 1.5D, d2 + d4, 0.0D, 0.0D, 0.23D);
		worldIn.spawnParticle(Particles.FIREWORK, d0 - 0.12D, d1 + 1.5D, d2 + d4, 0.0D, 0.0D, -0.23D);
		break;
	    case NORTH:
		worldIn.spawnParticle(Particles.PORTAL, d0 + d4, d1, d2 - 0.52D, 0.0D, 1.0D, -1.0D);
		worldIn.spawnParticle(Particles.FIREWORK, d0 + d4, d1 + 1.5D, d2 - 0.12D, 0.23D, 0.0D, 0.0D);
		worldIn.spawnParticle(Particles.FIREWORK, d0 + d4, d1 + 1.5D, d2 - 0.12D, -0.23D, 0.0D, 0.0D);
		break;
	    default:
	    case SOUTH:
		worldIn.spawnParticle(Particles.PORTAL, d0 + d4, d1, d2 + 0.52D, 0.0D, 1.0D, 1.0D);
		worldIn.spawnParticle(Particles.FIREWORK, d0 + d4, d1 + 1.5D, d2 + 0.12D, 0.23D, 0.0D, 0.0D);
		worldIn.spawnParticle(Particles.FIREWORK, d0 + d4, d1 + 1.5D, d2 + 0.12D, -0.23D, 0.0D, 0.0D);
	    }
	}
    }

    // called when the player right clicks this block
    @Override
    public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
	ItemStack playerItem = player.getHeldItem(hand);
	TileEntity tileEntity = worldIn.getTileEntity(pos);
	TileEntityPortalKeyhole myEntity = (TileEntityPortalKeyhole) tileEntity;

	// client side
	if (EffectiveSide.get() == LogicalSide.CLIENT)
	{
	    // do nothing and print debug information
	    if (playerItem.getItem() instanceof net.minecraft.item.ItemCompass)
	    {
		// speak to the player
		String message = "Contents: " + myEntity.getObjectInserted().getDisplayName();
		message += ", Filled = " + myEntity.isFilled() + ", Active = " + myEntity.isActivated();
		player.sendMessage(new TextComponentString(message));
	    }
	}

	// if the above debugging happened, then also do not insert the compass either
	if (playerItem.getItem() instanceof net.minecraft.item.ItemCompass)
	{
	    return true;
	}

	// insert or remove an item from this block
	if (tileEntity instanceof TileEntityPortalKeyhole)
	{
	    ItemStack insideItem = myEntity.getObjectInserted();

	    // if the keyhole is currently empty
	    if (insideItem.isEmpty())
	    {
		if (!playerItem.isEmpty())
		{
		    //System.out.println("Putting thing inside keyhole...");
		    myEntity.setContents(playerItem.copy());
		    //if (!playerIn.capabilities.isCreativeMode) // intentionally commented out
		    //{
		    playerItem.shrink(1);
		    //}
		}
	    }
	    // if the keyhole is currently full
	    else
	    {
		//System.out.println("Taking thing out of keyhole...");
		if (playerItem.isEmpty())
		{
		    player.setHeldItem(hand, insideItem); // hand it to the player
		}
		else if (!player.addItemStackToInventory(insideItem)) // okay put it in their inventory
		{
		    player.dropItem(insideItem, false); // whatever drop it on the ground
		}

		myEntity.removeContents();
	    }

	    // recalculate the boolean block states
	    IBlockState newBlockState = state.with(FACING, state.get(FACING)).with(FILLED, myEntity.isFilled()).with(LIT, myEntity.isActivated());
	    worldIn.setBlockState(pos, newBlockState, 3);
	    //worldIn.notifyBlockUpdate(pos, state, newBlockState, 3); // the above line of code causes a block update anyway, but otherwise this is needed
	    myEntity.markDirty();

	    // should portal blocks be spawned?
	    if (isOkayToSpawnPortalBlocks(worldIn, pos, state, myEntity))
	    {
		worldIn.setBlockState(pos.down(), Block.getBlockFromName(BlockGoldPortal.REG_NAME).getDefaultState());
		worldIn.setBlockState(pos.down(2), Block.getBlockFromName(BlockGoldPortal.REG_NAME).getDefaultState());

		// TODO: is this needed?
		// get a block on the destination side to pregen the chunk where this portal goes
		//worldIn.getMinecraftServer().getWorld(573).getBlockState(pos.down());
	    }

	    return true;
	}

	return true;
    }

    protected boolean isOkayToSpawnPortalBlocks(World worldIn, BlockPos pos, IBlockState state, TileEntityPortalKeyhole myEntity)
    {
	// if the portal is not lit, then that is a fast "no"
	if (!myEntity.isActivated())
	{
	    return false;
	}

	// check for air or existing portal blocks below this keyhole
	Block b1 = worldIn.getBlockState(pos.down()).getBlock();
	Block b2 = worldIn.getBlockState(pos.down(2)).getBlock();
	if (!(b1 == Blocks.AIR || b1 == Block.getBlockFromName(BlockGoldPortal.REG_NAME)))
	{
	    return false;
	}
	if (!(b2 == Blocks.AIR || b2 == Block.getBlockFromName(BlockGoldPortal.REG_NAME)))
	{
	    return false;
	}

	// awakened ItemPortalKeys will open a portal to the dungeon dimension
	ItemStack item = myEntity.getObjectInserted();
	if (item.getItem() instanceof ItemPortalKey)
	{
	    ItemPortalKey key = (ItemPortalKey) item.getItem();
	    return key.isActivated(item);
	}
	// three vanilla blocks will also open portals to the 3 vanilla dimensions?
	else if (getBlockFromItem(item.getItem()) != null)
	{
	    Block b = getBlockFromItem(item.getItem());
	    if (b == Blocks.NETHERRACK && worldIn.getDimension().getId() != -1)
	    {
		return true;
	    }
	    if (b == Blocks.END_STONE && worldIn.getDimension().getId() != 1)
	    {
		return true;
	    }
	    if (b == Blocks.GRASS_BLOCK && worldIn.getDimension().getId() != 0)
	    {
		return true;
	    }
	}

	return false;
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
	return true;
    }

    @Override
    public TileEntity createTileEntity(IBlockState state, IBlockReader world)
    {
	return new TileEntityPortalKeyhole(null);
    }

    // Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the IBlockstate
    //public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    @Override
    public IBlockState getStateForPlacement(BlockItemUseContext context)
    {
	IBlockState retval = getMyCustomDefaultState();
	return retval.with(FACING, context.getPlacementHorizontalFacing().getOpposite());
    }

    // Called by ItemBlocks after a block is set in the world, to allow post-place logic
    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
	worldIn.setBlockState(pos, state.with(FACING, placer.getHorizontalFacing().getOpposite()), 2);
    }
    
    // Called server side after this block is replaced with another in Chunk, but before the Tile Entity is updated
    @SuppressWarnings("deprecation")
    @Override
    //public void replaceBlock(IBlockState oldState, IBlockState newState, IWorld worldIn, BlockPos pos, int flags)
    public void onReplaced(IBlockState state, World worldIn, BlockPos pos, IBlockState newState, boolean isMoving)
    {
	TileEntity tileentity = worldIn.getTileEntity(pos);

	if (tileentity instanceof TileEntityPortalKeyhole)
	{
	    ItemStack item = ((TileEntityPortalKeyhole) tileentity).getObjectInserted();
	    if (!item.isEmpty())
	    {
		InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), item);
	    }
	    worldIn.updateComparatorOutputLevel(pos, this);
	}

	super.onReplaced(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state)
    {
	return true;
    }

    // returns 1 if the block is filled with an object that could open a portal given other conditions, and 0 otherwise
    @Override
    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos)
    {
	return blockState.get(LIT) ? 1 : 0;
    }

    // returns the ItemStack that represents this block - this has nothing to do with the item placed inside
    @Override
    public ItemStack getItem(IBlockReader worldIn, BlockPos pos, IBlockState state)
    {
	return new ItemStack(this);
    }

    /**
     * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only, LIQUID
     * for vanilla liquids, INVISIBLE to skip all rendering
     */
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
	return EnumBlockRenderType.MODEL;
    }

    // Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed blockstate.
    @Override
    public IBlockState rotate(IBlockState state, Rotation rot)
    {
	return state.with(FACING, rot.rotate((EnumFacing) state.get(FACING)));
    }

    // Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed blockstate.
    @Override
    public IBlockState mirror(IBlockState state, Mirror mirrorIn)
    {
	return state.with(FACING, mirrorIn.mirror(((EnumFacing) state.get(FACING))));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder)
    {
	builder.add(FACING, FILLED, LIT);
    }
}