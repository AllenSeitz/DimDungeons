package com.catastrophe573.dimdungeons.block;

import java.util.Random;
import javax.annotation.Nullable;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.block.BlockPortalKeyhole;
import com.catastrophe573.dimdungeons.item.ItemPortalKey;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBreakable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Particles;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockGoldPortal extends BlockBreakable
{
    public static String REG_NAME = "block_gold_portal";

    public BlockGoldPortal()
    {
	super(Block.Builder.create(Material.PORTAL).hardnessAndResistance(50).sound(SoundType.GLASS).doesNotBlockMovement().lightValue(15));
	setRegistryName(DimDungeons.MOD_ID, REG_NAME);
    }

    // Called by ItemBlocks after a block is set in the world, to allow post-place logic
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, @Nullable EntityLivingBase placer, ItemStack stack)
    {
	checkPortalIntegrity(state, worldIn, pos);
    }

    // this function is used to recalculate if the portal shape is still valid
    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
	checkPortalIntegrity(state, worldIn, pos);
    }

    // called by getItemsToDropCount() to determine what BlockItem or Item to drop
    // in this case, do not allow the player to obtain this block as an item
    public int quantityDropped(Random random)
    {
	return 0;
    }

    // called by getItemsToDropCount() to determine what BlockItem or Item to drop
    // in this case, do not allow the player to obtain this block as an item
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
    {
	return ItemStack.EMPTY;
    }

    public boolean isFullCube(IBlockState state)
    {
	return false;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public BlockRenderLayer getRenderLayer()
    {
	return BlockRenderLayer.TRANSLUCENT;
    }

    // called When an entity collides with the Block
    @SuppressWarnings("deprecation")
    @Override
    public void onEntityCollision(IBlockState state, World worldIn, BlockPos pos, Entity entityIn)
    {
	super.onEntityCollision(state, worldIn, pos, entityIn);

	// only teleport players! items and mobs and who knows what else must stay behind
	if (!(entityIn instanceof EntityPlayerMP))
	{
	    return;
	}

	if (entityIn.timeUntilPortal > 0)
	{
	    return; // not yet
	}

	if (!entityIn.isPassenger() && !entityIn.isBeingRidden() && entityIn.isNonBoss())
	{
	    DimDungeons.LOGGER.info("Entity " + entityIn.getName() + " just entered a gold portal.");

	    //*
	    TileEntityPortalKeyhole te = findKeyholeForThisPortal(state, worldIn, pos);
	    if (te != null)
	    {
		ItemStack item = te.getObjectInserted();
		if (item.getItem() instanceof ItemPortalKey && worldIn.getDimension().getId() == 0)
		{
		    ItemPortalKey key = (ItemPortalKey) item.getItem();
		    float warpX = key.getWarpX(item);
		    float warpZ = key.getWarpZ(item);

		    if (warpX == -1 || warpZ == -1)
		    {
			System.out.println("Player somehow used an unactivated key? Doing nothing.");
			//actuallyPerformTeleport(pos, (EntityPlayerMP) entityIn, MyFirstMod.dungeonDimensionId, 7.5f, 52, 12.0f);
		    }
		    else
		    {
			// TODO: remove this print
			System.out.println("Player used a key to teleport to dungeon at (" + warpX + ", " + warpZ + ").");
			actuallyPerformTeleport((EntityPlayerMP) entityIn, DimDungeons.getDungeonDimensionID(), warpX, 55.1D, warpZ);
		    }
		}
		// three vanilla blocks will also open portals to the 3 vanilla dimensions?
		else if (getBlockFromItem(item.getItem()) != null)
		{
		    Block b = getBlockFromItem(item.getItem());
		    if (b == Blocks.NETHERRACK && worldIn.getDimension().getId() != -1)
		    {
			actuallyPerformTeleport((EntityPlayerMP) entityIn, -1, entityIn.posX, entityIn.posY, entityIn.posZ);
		    }
		    if (b == Blocks.END_STONE && worldIn.getDimension().getId() != 1)
		    {
			actuallyPerformTeleport((EntityPlayerMP) entityIn, 1, entityIn.posX, entityIn.posY, entityIn.posZ);
		    }
		    if (b == Blocks.GRASS && worldIn.getDimension().getId() != 0)
		    {
			actuallyPerformTeleport((EntityPlayerMP) entityIn, 0, entityIn.posX, entityIn.posY, entityIn.posZ);
		    }
		}
	    }
	    else
	    {
		// no keyhole? this could be a return portal
		if (worldIn.getDimension().getId() == DimDungeons.getDungeonDimensionID())
		{
		    sendPlayerBackHome((EntityPlayerMP) entityIn);
		}
	    }
	    //*/
	}
    }

    protected void actuallyPerformTeleport(EntityPlayerMP player, int dimid, double x, double y, double z)
    {
	//TeleporterGoldPortal.teleportToDimension(player, dimid, x, y, z);
	player.timeUntilPortal = 300; // 300 ticks, same as vanilla nether portal (hijacking this also affects nether portals, which is intentional) 

	if (dimid == DimDungeons.getDungeonDimensionID())
	{
	    // if the player just entered a dungeon then force them to face north 
	    player.setRotationYawHead(2);
	}
    }

    protected void sendPlayerBackHome(EntityPlayerMP player)
    {
	BlockPos respawn = player.getBedLocation(0);
	if (respawn == null)
	{
	    // the fallback is to simply use the world spawn point
	    respawn = player.getServer().getWorld(0).getSpawnPoint();
	}
	if (respawn == null)
	{
	    // okay so we're using mods to not have respawns on the overworld or something?
	    respawn = new BlockPos(0f, 100f, 0f);
	    DimDungeons.LOGGER.info("WARNING: Player " + player.getName() + " could not return to their spawn point after exiting their dungeon.");
	}

	actuallyPerformTeleport(player, 0, respawn.getX(), respawn.getY(), respawn.getZ());
    }

    // this function returns void because the block deletes itself if the check fails
    public void checkPortalIntegrity(IBlockState state, World worldIn, BlockPos pos)
    {
	// valid portal shapes are not needed for persistence in the dungeon dimension itself because of the return portal
	if (!isPortalShapeIntact(state, worldIn, pos) && worldIn.dimension.getId() != DimDungeons.getDungeonDimensionID())
	{
	    worldIn.destroyBlock(pos, false);
	}
    }

    private boolean isPortalShapeIntact(IBlockState state, World worldIn, BlockPos pos)
    {
	// step 1: look for the keyhole block 1 or 2 tiles up
	TileEntityPortalKeyhole te = findKeyholeForThisPortal(state, worldIn, pos);
	if (te == null)
	{
	    return false;
	}

	// step 2: make sure the keyhole is lit
	if (!te.isActivated())
	{
	    return false;
	}

	// step 3: look for the other structure blocks on either the X or Z axis, depending on how the keyhole is facing
	IBlockState keyholeBlock = worldIn.getBlockState(te.getPos());
	
	if (keyholeBlock.get(BlockPortalKeyhole.FACING) == EnumFacing.WEST || keyholeBlock.get(BlockPortalKeyhole.FACING) == EnumFacing.EAST)
	{
	    return checkPortalFrameNorthSouth(worldIn, te.getPos());
	}
	else
	{
	    return checkPortalFrameWestEast(worldIn, te.getPos());
	}
    }

    // return the tile entity if it can be found, or NULL otherwise (in which case this portal block will soon vanish)
    private TileEntityPortalKeyhole findKeyholeForThisPortal(IBlockState state, World worldIn, BlockPos pos)
    {
	BlockPos p = pos.up();

	// look 1-2 blocks up for a BlockPortalKeyhole
	for (int i = 0; i < 2; i++)
	{
	    IBlockState keyhole = worldIn.getBlockState(p);
	    if (keyhole.getBlock() == BlockRegistrar.block_portal_keyhole )
	    {
		return (TileEntityPortalKeyhole)worldIn.getTileEntity(p);
	    }
	    p = p.up();
	}

	return null;
    }

    private boolean isValidPortalFrameBlock(Block b)
    {
	return b == Blocks.STONE_BRICKS || b == Blocks.CRACKED_STONE_BRICKS || b == Blocks.MOSSY_STONE_BRICKS || b == Blocks.CHISELED_STONE_BRICKS;
    }

    // just get the block states and keep it simple
    private boolean checkPortalFrameWestEast(World worldIn, BlockPos keyhole)
    {
	// main portal body
	if (!isValidPortalFrameBlock(worldIn.getBlockState(keyhole.west().down()).getBlock()) || !isValidPortalFrameBlock(worldIn.getBlockState(keyhole.west().down(2)).getBlock())
		|| !isValidPortalFrameBlock(worldIn.getBlockState(keyhole.east().down()).getBlock()) || !isValidPortalFrameBlock(worldIn.getBlockState(keyhole.east().down(2)).getBlock()) 
		|| !isValidPortalFrameBlock(worldIn.getBlockState(keyhole.down(3)).getBlock()))
	{
	    return false;
	}

	// left spire
	if (worldIn.getBlockState(keyhole.west(3).down(1)).getBlock() != BlockRegistrar.block_gilded_portal || !isValidPortalFrameBlock(worldIn.getBlockState(keyhole.west(3).down(2)).getBlock())
		|| !isValidPortalFrameBlock(worldIn.getBlockState(keyhole.west(3).down(3)).getBlock()))
	{
	    return false;
	}

	// right spire
	if (worldIn.getBlockState(keyhole.east(3).down(1)).getBlock() != BlockRegistrar.block_gilded_portal || !isValidPortalFrameBlock(worldIn.getBlockState(keyhole.east(3).down(2)).getBlock())
		|| !isValidPortalFrameBlock(worldIn.getBlockState(keyhole.east(3).down(3)).getBlock()))
	{
	    return false;
	}

	return true;
    }

    // just get the block states and keep it simple
    private boolean checkPortalFrameNorthSouth(World worldIn, BlockPos keyhole)
    {
	// main portal body
	if (!isValidPortalFrameBlock(worldIn.getBlockState(keyhole.north().down()).getBlock()) || !isValidPortalFrameBlock(worldIn.getBlockState(keyhole.north().down(2)).getBlock())
		|| !isValidPortalFrameBlock(worldIn.getBlockState(keyhole.south().down()).getBlock()) || !isValidPortalFrameBlock(worldIn.getBlockState(keyhole.south().down(2)).getBlock())
		|| !isValidPortalFrameBlock(worldIn.getBlockState(keyhole.down(3)).getBlock()))
	{
	    return false;
	}

	// left spire
	if (worldIn.getBlockState(keyhole.north(3).down(1)).getBlock() != BlockRegistrar.block_gilded_portal || !isValidPortalFrameBlock(worldIn.getBlockState(keyhole.north(3).down(2)).getBlock())
		|| !isValidPortalFrameBlock(worldIn.getBlockState(keyhole.north(3).down(3)).getBlock()))
	{
	    return false;
	}

	// right spire
	if (worldIn.getBlockState(keyhole.south(3).down(1)).getBlock() != BlockRegistrar.block_gilded_portal || !isValidPortalFrameBlock(worldIn.getBlockState(keyhole.south(3).down(2)).getBlock())
		|| !isValidPortalFrameBlock(worldIn.getBlockState(keyhole.south(3).down(3)).getBlock()))
	{
	    return false;
	}

	return true;
    }

    /**
     * Called periodically clientside on blocks near the player to show effects (like furnace fire particles). Note that
     * this method is unrelated to {@link randomTick} and {@link #needsRandomTick}, and will always be called regardless of
     * whether the block can receive random update ticks
     */
    @OnlyIn(Dist.CLIENT)
    public void animateTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
	double d0 = (double) ((float) pos.getX() + rand.nextFloat());
	double d1 = (double) ((float) pos.getY() + 0.8F);
	double d2 = (double) ((float) pos.getZ() + rand.nextFloat());
	double xspeed = rand.nextFloat() * (rand.nextInt(3) - 1) / 9;
	double zspeed = rand.nextFloat() * (rand.nextInt(3) - 1) / 9;
	worldIn.spawnParticle(Particles.END_ROD, d0, d1, d2, xspeed, 0.0D, zspeed);
    }

    /**
     * Get the geometry of the queried face at the given position and state. This is used to decide whether things like
     * buttons are allowed to be placed on the face, or how glass panes connect to the face, among other things. Common
     * values are {@code SOLID}, which is the default, and {@code UNDEFINED}, which represents something that does not fit
     * the other descriptions and will generally cause other things not to connect to the face.
     * 
     * @return an approximation of the form of the given face
     */
    public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
	return BlockFaceShape.UNDEFINED;
    }
}