package com.catastrophe573.dimdungeons.block;

import java.util.Random;

import javax.annotation.Nullable;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.item.ItemPortalKey;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.thread.EffectiveSide;

public class BlockPortalKeyhole extends Block
{
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
    public static final BooleanProperty FILLED = BooleanProperty.create("filled");
    public static final BooleanProperty LIT = BooleanProperty.create("lit");

    public static final String REG_NAME = "block_portal_keyhole";

    public BlockPortalKeyhole()
    {
	super(Block.Properties.create(Material.PORTAL).hardnessAndResistance(2).sound(SoundType.METAL));
	this.setRegistryName(DimDungeons.MOD_ID, REG_NAME);
	this.setDefaultState(getMyCustomDefaultState());
    }

    // used by the constructor and I'm not sure where else anymore?
    public BlockState getMyCustomDefaultState()
    {
	return this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(FILLED, false).with(LIT, false);
    }

    // based on code from vanilla furnaces, which also play a sound effect and make particles when their TileEntity is being productive
    @OnlyIn(Dist.CLIENT)
    @Override
    //public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
	if (stateIn.get(LIT))
	{
	    Direction enumfacing = (Direction) stateIn.get(FACING);
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
		worldIn.addParticle(ParticleTypes.PORTAL, d0 - 0.52D, d1 - 1, d2 + d4, 1.0D, 1.0D, 0.0D);
		worldIn.addParticle(ParticleTypes.FIREWORK, d0 + 0.12D, d1 + 1.5D, d2 + d4, 0.0D, 0.0D, 0.23D);
		worldIn.addParticle(ParticleTypes.FIREWORK, d0 + 0.12D, d1 + 1.5D, d2 + d4, 0.0D, 0.0D, -0.23D);
		break;
	    case WEST:
		worldIn.addParticle(ParticleTypes.PORTAL, d0 + 0.52D, d1 - 1, d2 + d4, -1.0D, 1.0D, 0.0D);
		worldIn.addParticle(ParticleTypes.FIREWORK, d0 - 0.12D, d1 + 1.5D, d2 + d4, 0.0D, 0.0D, 0.23D);
		worldIn.addParticle(ParticleTypes.FIREWORK, d0 - 0.12D, d1 + 1.5D, d2 + d4, 0.0D, 0.0D, -0.23D);
		break;
	    case NORTH:
		worldIn.addParticle(ParticleTypes.PORTAL, d0 + d4, d1, d2 - 0.52D, 0.0D, 1.0D, -1.0D);
		worldIn.addParticle(ParticleTypes.FIREWORK, d0 + d4, d1 + 1.5D, d2 - 0.12D, 0.23D, 0.0D, 0.0D);
		worldIn.addParticle(ParticleTypes.FIREWORK, d0 + d4, d1 + 1.5D, d2 - 0.12D, -0.23D, 0.0D, 0.0D);
		break;
	    default:
	    case SOUTH:
		worldIn.addParticle(ParticleTypes.PORTAL, d0 + d4, d1, d2 + 0.52D, 0.0D, 1.0D, 1.0D);
		worldIn.addParticle(ParticleTypes.FIREWORK, d0 + d4, d1 + 1.5D, d2 + 0.12D, 0.23D, 0.0D, 0.0D);
		worldIn.addParticle(ParticleTypes.FIREWORK, d0 + d4, d1 + 1.5D, d2 + 0.12D, -0.23D, 0.0D, 0.0D);
	    }
	}
    }

    // called when the player right clicks this block
    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
	// client side
	if (EffectiveSide.get() == LogicalSide.CLIENT)
	{
	    return true;
	}

	ItemStack playerItem = player.getHeldItem(handIn);
	TileEntity tileEntity = worldIn.getTileEntity(pos);
	TileEntityPortalKeyhole myEntity = (TileEntityPortalKeyhole) tileEntity;

	// insert or remove an item from this block
	if (myEntity != null)
	{
	    ItemStack insideItem = myEntity.getObjectInserted();

	    // if the keyhole is currently empty
	    if (insideItem.isEmpty())
	    {
		if (!playerItem.isEmpty())
		{
		    DimDungeons.LOGGER.info("Putting " + playerItem.getDisplayName().getString() + " inside keyhole...");
		    myEntity.setContents(playerItem.copy());
		    //if (!playerIn.capabilities.isCreativeMode) // intentionally commented out
		    //{
		    playerItem.shrink(1);
		    //}

		    // recalculate the boolean block states	    
		    BlockState newBlockState = state.with(FACING, state.get(FACING)).with(FILLED, myEntity.isFilled()).with(LIT, myEntity.isActivated());
		    worldIn.setBlockState(pos, newBlockState);

		    // should portal blocks be spawned?
		    if (isOkayToSpawnPortalBlocks(worldIn, pos, state, myEntity))
		    {
			DimDungeons.LOGGER.info("DimDungeons: CREATING GOLD PORTAL BLOCKS!");
			worldIn.setBlockState(pos.down(), BlockRegistrar.block_gold_portal.getDefaultState());
			worldIn.setBlockState(pos.down(2), BlockRegistrar.block_gold_portal.getDefaultState());

			// TODO: is this needed?
			// get a block on the destination side to pregen the chunk where this portal goes
			//worldIn.getMinecraftServer().getWorld(573).getBlockState(pos.down());
		    }

		    return true;
		}
	    }
	    // if the keyhole is currently full
	    else
	    {
		DimDungeons.LOGGER.info("Taking thing out of keyhole...");
		if (playerItem.isEmpty())
		{
		    player.setHeldItem(handIn, insideItem); // hand it to the player
		}
		else if (!player.addItemStackToInventory(insideItem)) // okay put it in their inventory
		{
		    player.dropItem(insideItem, false); // whatever drop it on the ground
		}

		myEntity.removeContents();

		// recalculate the boolean block states	    
		BlockState newBlockState = state.with(FACING, state.get(FACING)).with(FILLED, myEntity.isFilled()).with(LIT, myEntity.isActivated());
		worldIn.setBlockState(pos, newBlockState, 3);

		return true;
	    }
	}

	return true;
    }

    // helper function for checkForPortalCreation
    protected boolean isOkayToSpawnPortalBlocks(World worldIn, BlockPos pos, BlockState state, TileEntityPortalKeyhole myEntity)
    {
	// if the portal is not lit, then that is a fast "no"
	if (!myEntity.isActivated())
	{
	    return false;
	}

	// check for air or existing portal blocks below this keyhole
	Block b1 = worldIn.getBlockState(pos.down()).getBlock();
	Block b2 = worldIn.getBlockState(pos.down(2)).getBlock();
	if (!(b1 == Blocks.AIR || b1 == BlockRegistrar.block_gold_portal))
	{
	    return false;
	}
	if (!(b2 == Blocks.AIR || b2 == BlockRegistrar.block_gold_portal))
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
	    if (b == Blocks.NETHERRACK && worldIn.getDimension().getType() != DimensionType.THE_NETHER)
	    {
		return true;
	    }
	    if (b == Blocks.END_STONE && worldIn.getDimension().getType() != DimensionType.THE_END)
	    {
		return true;
	    }
	    if (b == Blocks.GRASS_BLOCK && worldIn.getDimension().getType() != DimensionType.OVERWORLD)
	    {
		return true;
	    }
	}

	return false;
    }

    @Override
    public boolean hasTileEntity(BlockState state)
    {
	return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
    {
	return new TileEntityPortalKeyhole();
    }

    // Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the IBlockstate
    //public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
	BlockState retval = getMyCustomDefaultState();
	return retval.with(FACING, context.getPlacementHorizontalFacing().getOpposite());
    }

    // Called by ItemBlocks after a block is set in the world, to allow post-place logic
    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
	worldIn.setBlockState(pos, state.with(FACING, placer.getHorizontalFacing().getOpposite()), 2);
    }

    // Called server side after this block is replaced with another in Chunk, but before the TileEntity is updated
    // this function is now in charge of preserving TileEntities across block updates, too, instead of the former TileEntity->shouldRefresh()
    @SuppressWarnings("deprecation")
    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving)
    {
	TileEntity tileentity = worldIn.getTileEntity(pos);

	// DO NOT call super.onReplaced() unless this block has no TileEntity, or unless the block was deleted/changed to another block of course
	if (state.getBlock() != newState.getBlock())
	{
	    // if the block was destroyed and it held an item then spit the item out somewhere
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
	    worldIn.removeTileEntity(pos);
	}
    }

    @Override
    public boolean hasComparatorInputOverride(BlockState state)
    {
	return true;
    }

    // returns 1 number if the block is filled with any item stack, and 0 otherwise
    @Override
    public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos)
    {
	return blockState.get(FILLED) ? 1 : 0;
    }

    // returns the ItemStack that represents this block - this has nothing to do with the item placed inside
    @Override
    public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state)
    {
	return new ItemStack(this);
    }

    /**
     * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only, LIQUID
     * for vanilla liquids, INVISIBLE to skip all rendering
     */
    @Override
    public BlockRenderType getRenderType(BlockState state)
    {
	return BlockRenderType.MODEL;
    }

    @Override
    // this replaces all metadata in 1.13 forwards
    //public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    public BlockState getExtendedState(BlockState state, IBlockReader world, BlockPos pos)
    {
	BlockState retval = state;
	TileEntity te = world.getTileEntity(pos);
	if (te instanceof TileEntityPortalKeyhole)
	{
	    retval.with(FILLED, ((TileEntityPortalKeyhole) te).isFilled());
	    retval.with(LIT, ((TileEntityPortalKeyhole) te).isActivated());
	}

	return retval;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
	builder.add(FACING, FILLED, LIT);
    }

    // Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed blockstate.
    @Override
    public BlockState rotate(BlockState state, Rotation rot)
    {
	return state.with(FACING, rot.rotate((Direction) state.get(FACING)));
    }

    // Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed blockstate.
    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn)
    {
	return state.with(FACING, mirrorIn.mirror(((Direction) state.get(FACING))));
    }
}