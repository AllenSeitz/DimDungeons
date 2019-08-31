package com.catastrophe573.dimdungeons.block;

import java.util.Random;
import javax.annotation.Nullable;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.block.BlockPortalKeyhole;
import com.catastrophe573.dimdungeons.dimension.DungeonDimensionType;
import com.catastrophe573.dimdungeons.item.ItemPortalKey;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BreakableBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.thread.EffectiveSide;

public class BlockGoldPortal extends BreakableBlock
{
    public static String REG_NAME = "block_gold_portal";

    public BlockGoldPortal()
    {
	super(Block.Properties.create(Material.PORTAL).hardnessAndResistance(50).sound(SoundType.GLASS).doesNotBlockMovement().lightValue(15));
	setRegistryName(DimDungeons.MOD_ID, REG_NAME);
    }

    // Called by ItemBlocks after a block is set in the world, to allow post-place logic
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
	checkPortalIntegrity(state, worldIn, pos);
    }

    // this function is used to recalculate if the portal shape is still valid
    @Override
    public void updateNeighbors(BlockState stateIn, IWorld worldIn, BlockPos pos, int flags)
    {
	checkPortalIntegrity(stateIn, worldIn, pos);
    }

    // called by getItemsToDropCount() to determine what BlockItem or Item to drop
    // in this case, do not allow the player to obtain this block as an item
    public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state)
    {
	return ItemStack.EMPTY;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public BlockRenderLayer getRenderLayer()
    {
	return BlockRenderLayer.TRANSLUCENT;
    }

    // called When an entity collides with the Block
    @Override
    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn)
    {
	//super.onEntityCollision(state, worldIn, pos, entityIn);

	// do not process this block on the client
	if (EffectiveSide.get() == LogicalSide.CLIENT)
	{
	    return;
	}

	// only teleport players! items and mobs and who knows what else must stay behind
	if (!(entityIn instanceof ServerPlayerEntity))
	{
	    return;
	}

	if (entityIn.timeUntilPortal > 0)
	{
	    return; // not yet
	}

	if (!entityIn.isPassenger() && !entityIn.isBeingRidden() && entityIn.isNonBoss())
	{
	    DimDungeons.LOGGER.info("Entity " + entityIn.getName().getString() + " just entered a gold portal.");

	    TileEntityPortalKeyhole te = findKeyholeForThisPortal(state, worldIn, pos);
	    if (te != null)
	    {
		ItemStack item = te.getObjectInserted();
		if (item.getItem() instanceof ItemPortalKey && worldIn.getDimension().getType() == DimensionType.OVERWORLD)
		{
		    ItemPortalKey key = (ItemPortalKey) item.getItem();
		    float warpX = key.getWarpX(item);
		    float warpZ = key.getWarpZ(item);

		    if (warpX == -1 || warpZ == -1)
		    {
			System.out.println("Player somehow used an unactivated key? Doing nothing.");
			//actuallyPerformTeleport((EntityPlayerMP) entityIn, DimensionRegistrar.getDungeonDimensionID(), 7.5f, 52, 12.0f);
		    }
		    else
		    {
			// TODO: remove this print
			System.out.println("Player used a key to teleport to dungeon at (" + warpX + ", " + warpZ + "). in dim...");

			actuallyPerformTeleport((ServerPlayerEntity) entityIn, DungeonDimensionType.getDimensionType(), warpX, 55.1D, warpZ);
		    }
		}
		// three vanilla blocks will also open portals to the 3 vanilla dimensions?
		else if (getBlockFromItem(item.getItem()) != null)
		{
		    Block b = getBlockFromItem(item.getItem());
		    if (b == Blocks.NETHERRACK && worldIn.getDimension().getType() != DimensionType.THE_NETHER)
		    {
			actuallyPerformTeleport((ServerPlayerEntity) entityIn, DimensionType.THE_NETHER, entityIn.posX, entityIn.posY, entityIn.posZ);
		    }
		    if (b == Blocks.END_STONE && worldIn.getDimension().getType() != DimensionType.THE_END)
		    {
			actuallyPerformTeleport((ServerPlayerEntity) entityIn, DimensionType.THE_END, entityIn.posX, entityIn.posY, entityIn.posZ);
		    }
		    if (b == Blocks.GRASS_BLOCK && worldIn.getDimension().getType() != DimensionType.OVERWORLD)
		    {
			actuallyPerformTeleport((ServerPlayerEntity) entityIn, DimensionType.OVERWORLD, entityIn.posX, entityIn.posY, entityIn.posZ);
		    }
		}
	    }
	    else
	    {
		// no keyhole? this could be a return portal
		if (worldIn.getDimension().getType() == DungeonDimensionType.getDimensionType())
		{
		    sendPlayerBackHome((ServerPlayerEntity) entityIn);
		}
	    }
	}
    }

    protected void actuallyPerformTeleport(ServerPlayerEntity player, DimensionType dim, double x, double y, double z)
    {
	DimDungeons.LOGGER.info("INSIDE actuallyPerformTeleport: newDim = " + dim.toString());
	player.timeUntilPortal = 300; // 300 ticks, same as vanilla nether portal (hijacking this also affects nether portals, which is intentional) 
	player.changeDimension(dim);

	if (dim == DungeonDimensionType.getDimensionType())
	{
	    // if the player just entered a dungeon then force them to face north 
	    player.setRotationYawHead(2);
	}
    }

    protected void sendPlayerBackHome(ServerPlayerEntity player)
    {
	BlockPos respawn = player.getBedLocation(DimensionType.OVERWORLD);
	if (respawn == null)
	{
	    // the fallback is to simply use the world spawn point
	    respawn = player.getServer().getWorld(DimensionType.OVERWORLD).getSpawnPoint();
	}
	if (respawn == null)
	{
	    // okay so we're using mods to not have respawns on the overworld or something?
	    respawn = new BlockPos(0f, 100f, 0f);
	    DimDungeons.LOGGER.info("WARNING: Player " + player.getName() + " could not return to their spawn point after exiting their dungeon.");
	}

	actuallyPerformTeleport(player, DimensionType.OVERWORLD, respawn.getX(), respawn.getY(), respawn.getZ());
    }

    // this function returns void because the block deletes itself if the check fails
    public void checkPortalIntegrity(BlockState state, IWorld worldIn, BlockPos pos)
    {
	// valid portal shapes are not needed for persistence in the dungeon dimension itself because of the return portal
	if (!isPortalShapeIntact(state, worldIn, pos) && worldIn.getDimension().getType() != DungeonDimensionType.getDimensionType())
	{
	    worldIn.destroyBlock(pos, false);
	}
    }

    private boolean isPortalShapeIntact(BlockState state, IWorld worldIn, BlockPos pos)
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
	BlockState keyholeBlock = worldIn.getBlockState(te.getPos());

	if (keyholeBlock.get(BlockPortalKeyhole.FACING) == Direction.WEST || keyholeBlock.get(BlockPortalKeyhole.FACING) == Direction.EAST)
	{
	    return checkPortalFrameNorthSouth(worldIn, te.getPos());
	}
	else
	{
	    return checkPortalFrameWestEast(worldIn, te.getPos());
	}
    }

    // return the tile entity if it can be found, or NULL otherwise (in which case this portal block will soon vanish)
    private TileEntityPortalKeyhole findKeyholeForThisPortal(BlockState state, IWorld worldIn, BlockPos pos)
    {
	BlockPos p = pos.up();

	// look 1-2 blocks up for a BlockPortalKeyhole
	for (int i = 0; i < 2; i++)
	{
	    BlockState keyhole = worldIn.getBlockState(p);
	    if (keyhole.getBlock() == BlockRegistrar.block_portal_keyhole)
	    {
		return (TileEntityPortalKeyhole) worldIn.getTileEntity(p);
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
    private boolean checkPortalFrameWestEast(IWorld worldIn, BlockPos keyhole)
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
    private boolean checkPortalFrameNorthSouth(IWorld worldIn, BlockPos keyhole)
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
     * Called periodically client side on blocks near the player to show effects (like furnace fire particles). Note that
     * this method is unrelated to {@link randomTick} and {@link #needsRandomTick}, and will always be called regardless of
     * whether the block can receive random update ticks
     */
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
	double d0 = (double) ((float) pos.getX() + rand.nextFloat());
	double d1 = (double) ((float) pos.getY() + 0.8F);
	double d2 = (double) ((float) pos.getZ() + rand.nextFloat());
	double xspeed = rand.nextFloat() * (rand.nextInt(3) - 1) / 9;
	double zspeed = rand.nextFloat() * (rand.nextInt(3) - 1) / 9;
	worldIn.addParticle(ParticleTypes.END_ROD, d0, d1, d2, xspeed, 0.0D, zspeed);
    }
}