package com.catastrophe573.dimdungeons.block;

import java.util.Random;
import javax.annotation.Nullable;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.block.BlockPortalKeyhole;
import com.catastrophe573.dimdungeons.command.CustomTeleporter;
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
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.tileentity.BannerTileEntity;
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
	if (!checkPortalIntegrity(state, worldIn, pos))
	{
	    worldIn.destroyBlock(pos, false);
	}
    }

    // this function was used in 1.12 to recalculate if the portal shape is still valid, and it is still called in a few places
    @Override
    public void updateNeighbors(BlockState stateIn, IWorld worldIn, BlockPos pos, int flags)
    {
	if (!checkPortalIntegrity(stateIn, worldIn, pos))
	{
	    worldIn.destroyBlock(pos, false);
	}
    }

    // this function seems to be the true 1.14 replacement for updateNeighbors(), and it cares about block sides now
    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
	if (checkPortalIntegrity(stateIn, worldIn, currentPos))
	{
	    return stateIn;
	}
	return Blocks.AIR.getDefaultState(); // destroy this block
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

	if (!entityIn.isPassenger() && !entityIn.isBeingRidden() && entityIn.isNonBoss() && entityIn.onGround)
	{
	    //DimDungeons.LOGGER.info("Entity " + entityIn.getName().getString() + " just entered a gold portal.");

	    TileEntityPortalKeyhole te = findKeyholeForThisPortal(state, worldIn, pos);
	    if (te != null)
	    {
		ItemStack item = te.getObjectInserted();
		if (!item.isEmpty())
		{
		    if (item.getItem() instanceof ItemPortalKey && worldIn.getDimension().getType() == DimensionType.OVERWORLD)
		    {
			ItemPortalKey key = (ItemPortalKey) item.getItem();
			float warpX = key.getWarpX(item);
			float warpZ = key.getWarpZ(item);

			if (warpX == -1 || warpZ == -1)
			{
			    System.out.println("Player somehow used an unactivated key? Doing nothing.");
			}
			else
			{
			    //System.out.println("Player used a key to teleport to dungeon at (" + warpX + ", " + warpZ + "). in dim...");
			    //IPlayerDungeonData data = (IPlayerDungeonData) entityIn.getCapability(PlayerDungeonDataProvider.DUNGEONDATA_CAPABILITY);
			    
			    /*
			    if (entityIn.getCapability(PlayerDungeonDataProvider.DUNGEONDATA_CAPABILITY).isPresent())
			    {
				IPlayerDungeonData data = entityIn.getCapability(PlayerDungeonDataProvider.DUNGEONDATA_CAPABILITY).orElse(new DefaultPlayerDungeonData());
				data.setLastOverworldPortalX((float) entityIn.prevPosX);
				data.setLastOverworldPortalY((float) entityIn.prevPosY);
				data.setLastOverworldPortalZ((float) entityIn.prevPosZ);
				data.setLastOverworldPortalYaw(entityIn.getPitchYaw().y);
				DimDungeons.LOGGER.info("DIMDUNGEONS: SAVED PLAYER CAPABILITY " + data.getLastOverworldPortalY());
			    }
			    else
			    {
				DimDungeons.LOGGER.info("DIMDUNGEONS: UNABLE TO SAVE PLAYER CAPABILITY");
			    }
			    */
			    
			    actuallyPerformTeleport((ServerPlayerEntity) entityIn, DungeonDimensionType.getDimensionType(), warpX, 55.1D, warpZ, 0);
			}
		    }
		    // three vanilla blocks will also open portals to the 3 vanilla dimensions?
		    else if (getBlockFromItem(item.getItem()) != null)
		    {
			//			Block b = getBlockFromItem(item.getItem());
			//			if (b == Blocks.NETHERRACK && worldIn.getDimension().getType() != DimensionType.THE_NETHER)
			//			{
			//			    actuallyPerformTeleport((ServerPlayerEntity) entityIn, DimensionType.THE_NETHER, entityIn.posX, entityIn.posY, entityIn.posZ);
			//			}
			//			if (b == Blocks.END_STONE && worldIn.getDimension().getType() != DimensionType.THE_END)
			//			{
			//			    actuallyPerformTeleport((ServerPlayerEntity) entityIn, DimensionType.THE_END, entityIn.posX, entityIn.posY, entityIn.posZ);
			//			}
			//			if (b == Blocks.GRASS_BLOCK && worldIn.getDimension().getType() != DimensionType.OVERWORLD)
			//			{
			//			    actuallyPerformTeleport((ServerPlayerEntity) entityIn, DimensionType.OVERWORLD, entityIn.posX, entityIn.posY, entityIn.posZ);
			//			}
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

    protected void actuallyPerformTeleport(ServerPlayerEntity player, DimensionType dim, double x, double y, double z, double yaw)
    {
	player.timeUntilPortal = 200; // 300 ticks, same as vanilla nether portal (hijacking this also affects nether portals, which is intentional) 

	// if the player just entered a dungeon then force them to face north 
	if (dim == DungeonDimensionType.getDimensionType())
	{
	    CustomTeleporter.teleportEntityToDimension(player, dim, false, x, y, z, 0.0f, 180.0f);
	}
	else
	{
	    CustomTeleporter.teleportEntityToDimension(player, dim, false, x, y, z, player.getPitchYaw().x, (float) yaw);
	}
    }

    protected void sendPlayerBackHome(ServerPlayerEntity player)
    {
	float lastX = 0;
	float lastY = 0;
	float lastZ = 0;
	float lastYaw = player.getPitchYaw().y;
	/*
	if (player.getCapability(PlayerDungeonDataProvider.DUNGEONDATA_CAPABILITY).isPresent())
	{
	    IPlayerDungeonData data = player.getCapability(PlayerDungeonDataProvider.DUNGEONDATA_CAPABILITY).orElse(new DefaultPlayerDungeonData());
	    lastX = data.getLastOverworldPortalX();
	    lastY = data.getLastOverworldPortalY();
	    lastZ = data.getLastOverworldPortalZ();
	    lastYaw = data.getLastOverworldPortalYaw();
	    DimDungeons.LOGGER.info("DIMDUNGEONS: LOADED PLAYER CAPABILITY " + data.getLastOverworldPortalY());
	}
	else
	{
	    DimDungeons.LOGGER.info("DIMDUNGEONS: PLAYER WENT HOME WITH NO CAPABILITY");
	}
	*/

	if (lastY < 2)
	{
	    // second fallback: use the bed position
	    BlockPos respawn = player.getBedLocation(DimensionType.OVERWORLD);
	    if (respawn != null)
	    {
		respawn.add(0, 3, 0); // stand on the bed
	    }
	    else
	    {
		// third fallback: use the world spawn point
		respawn = player.getServer().getWorld(DimensionType.OVERWORLD).getSpawnPoint();
		respawn.add(0, 2, 0);
	    }
	    lastX = respawn.getX();
	    lastY = respawn.getY();
	    lastZ = respawn.getZ();
	    lastYaw = player.getPitchYaw().y;
	}

	actuallyPerformTeleport(player, DimensionType.OVERWORLD, lastX, lastY, lastZ, lastYaw);
    }

    // this function returns boolean and relies on another function to actually destroy the block
    public boolean checkPortalIntegrity(BlockState state, IWorld worldIn, BlockPos pos)
    {
	// valid portal shapes are not needed for persistence in the dungeon dimension itself because of the return portal
	return worldIn.getDimension().getType() == DungeonDimensionType.getDimensionType() || isPortalShapeIntact(state, worldIn, pos);
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
	boolean frameLevel1 = false;
	if (keyholeBlock.get(BlockPortalKeyhole.FACING) == Direction.WEST || keyholeBlock.get(BlockPortalKeyhole.FACING) == Direction.EAST)
	{
	    frameLevel1 = checkPortalFrameNorthSouth(worldIn, te.getPos());
	}
	else
	{
	    frameLevel1 = checkPortalFrameWestEast(worldIn, te.getPos());
	}
	if ( !frameLevel1 )
	{
	    return false;
	}
	
	// step 4: if this is a level 2 key then check additional portal frame requirements
	ItemStack key = te.getObjectInserted();
	int keyLevel = ((ItemPortalKey)key.getItem()).getKeyLevel(key);
	if ( key.getItem() instanceof ItemPortalKey )
	{
	    if ( keyLevel >= 2 )
	    {
		boolean frameLevel2 = false;
		if (keyholeBlock.get(BlockPortalKeyhole.FACING) == Direction.WEST || keyholeBlock.get(BlockPortalKeyhole.FACING) == Direction.EAST)
		{
		    frameLevel2 = checkPortalFrameLevel2NorthSouth(worldIn, te.getPos());
		}
		else
		{
		    frameLevel2 = checkPortalFrameLevel2WestEast(worldIn, te.getPos());
		}
		if ( !frameLevel2 )
		{
		    return false;
		}
	    }
	}
	
	return true;
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
    
    private boolean checkPortalFrameLevel2WestEast(IWorld worldIn, BlockPos keyhole)
    {
	// main portal body - check for added crowns
	if (worldIn.getBlockState(keyhole.west(1)).getBlock() != BlockRegistrar.block_portal_crown || worldIn.getBlockState(keyhole.east(1)).getBlock() != BlockRegistrar.block_portal_crown)
	{
	    return false;
	}

	// left spire - check for banner
	int front = getBannerLevel(worldIn, keyhole.west(3).down(1).north(1));
	int back = getBannerLevel(worldIn, keyhole.west(3).down(1).south(1));
	if ( front < 2 && back < 2 )
	{
	    return false;
	}
	
	// right spire - check for banner
	front = getBannerLevel(worldIn, keyhole.east(3).down(1).north(1));
	back = getBannerLevel(worldIn, keyhole.east(3).down(1).south(1));
	if ( front < 2 && back < 2 )
	{
	    return false;
	}	
	
	return true;
    }

    // just get the block states and keep it simple
    private boolean checkPortalFrameLevel2NorthSouth(IWorld worldIn, BlockPos keyhole)
    {
	// main portal body - check for added crowns
	if (worldIn.getBlockState(keyhole.north(1)).getBlock() != BlockRegistrar.block_portal_crown || worldIn.getBlockState(keyhole.south(1)).getBlock() != BlockRegistrar.block_portal_crown)
	{
	    return false;
	}

	// left spire - check for banner
	int front = getBannerLevel(worldIn, keyhole.north(3).down(1).west(1));
	int back = getBannerLevel(worldIn, keyhole.north(3).down(1).east(1));
	if ( front < 2 && back < 2 )
	{
	    return false;
	}
	
	// right spire - check for banner
	front = getBannerLevel(worldIn, keyhole.south(3).down(1).west(1));
	back = getBannerLevel(worldIn, keyhole.south(3).down(1).east(1));
	if ( front < 2 && back < 2 )
	{
	    return false;
	}
	
	return true;
    }    

    // it's okay if the block here isn't a banner, I check for that too
    public int getBannerLevel(IWorld worldIn, BlockPos pos )
    {
	boolean level2 = false;
	boolean level3 = false;
	Block banner = worldIn.getBlockState(pos).getBlock();

	// first ensure that the tile entity is going to exist
	if ( !(banner == Blocks.WHITE_WALL_BANNER || banner == Blocks.PURPLE_WALL_BANNER) )
	{
	    return 0;
	}
	BannerTileEntity te = (BannerTileEntity)worldIn.getTileEntity(pos);
	if ( te == null )
	{
	    return 0;
	}
	
	// check the banner patterns, any matches are fine, any extras are ignored
	for ( int i = 0; i < te.getPatternList().size(); i++ )
	{
	    BannerPattern p = te.getPatternList().get(i);
	    DyeColor c = te.getColorList().get(i);
	    //DimDungeons.LOGGER.info("DIMDUNGEONS: pattern is " + p + ", " + c);
	    
	    if ( banner == Blocks.WHITE_WALL_BANNER && p == BannerPattern.DIAGONAL_RIGHT && c == DyeColor.PURPLE )
	    {
		level2 = true;
	    }
	    if ( banner == Blocks.PURPLE_WALL_BANNER && p == BannerPattern.DIAGONAL_LEFT && c == DyeColor.WHITE )
	    {
		level2 = true;
	    }
	    // TODO: check for my custom banner pattern for level 3
	}
	
	if ( level2 && level3 )
	{
	    return 3;
	}
	else if ( level2 )
	{
	    return 2;
	}
	return 0;
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