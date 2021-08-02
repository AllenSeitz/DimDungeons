package com.catastrophe573.dimdungeons.block;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.DungeonConfig;
import com.catastrophe573.dimdungeons.block.BlockPortalKeyhole;
import com.catastrophe573.dimdungeons.dimension.CustomTeleporter;
import com.catastrophe573.dimdungeons.item.ItemPortalKey;
import com.catastrophe573.dimdungeons.utils.DungeonUtils;

import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
//import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class BlockGoldPortal extends BaseEntityBlock
{
    public static String REG_NAME = "block_gold_portal";

    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    protected static final VoxelShape X_AABB = Block.box(0.0D, 0.0D, 6.0D, 16.0D, 16.0D, 10.0D);
    protected static final VoxelShape Z_AABB = Block.box(6.0D, 0.0D, 0.0D, 10.0D, 16.0D, 16.0D);

    public BlockGoldPortal()
    {
	super(BlockBehaviour.Properties.of(Material.PORTAL).strength(50).sound(SoundType.GLASS).noCollission().lightLevel((p) -> 15));
	setRegistryName(DimDungeons.MOD_ID, REG_NAME);
	this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.X));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
	switch ((Direction.Axis) state.getValue(AXIS))
	{
	case Z:
	    return Z_AABB;
	default:
	    return X_AABB;
	}
    }

    public BlockState rotate(BlockState state, Rotation rot)
    {
	switch (rot)
	{
	case COUNTERCLOCKWISE_90:
	case CLOCKWISE_90:
	    switch ((Direction.Axis) state.getValue(AXIS))
	    {
	    case Z:
		return state.setValue(AXIS, Direction.Axis.X);
	    case X:
		return state.setValue(AXIS, Direction.Axis.Z);
	    default:
		return state;
	    }
	default:
	    return state;
	}
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
	builder.add(AXIS);
    }

    // Called by ItemBlocks after a block is set in the world, to allow post-place logic
    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
	if (!checkPortalIntegrity(state, worldIn, pos))
	{
	    worldIn.destroyBlock(pos, false);
	}
    }

    @Override
    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
	if (!this.checkPortalIntegrity(state, worldIn, pos))
	{
	    worldIn.destroyBlock(pos, false);
	}
    }

    // this function seems to be the true 1.14 replacement for updateNeighbors(), and it cares about block sides now
    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos)
    {
	if (checkPortalIntegrity(stateIn, worldIn, currentPos))
	{
	    return stateIn;
	}
	return Blocks.AIR.defaultBlockState(); // destroy this block
    }

    // called by getItemsToDropCount() to determine what BlockItem or Item to drop
    // in this case, do not allow the player to obtain this block as an item
    @Override
    public ItemStack getCloneItemStack(BlockGetter worldIn, BlockPos pos, BlockState state)
    {
	return ItemStack.EMPTY;
    }

    @Deprecated
    @Override
    public boolean useShapeForLightOcclusion(BlockState state)
    {
	return true;
    }

    // called When an entity collides with the Block
    @Override
    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn)
    {
	// do not process this block on the client
	if (worldIn.isClientSide)
	{
	    return;
	}

	// only teleport players! items and mobs and who knows what else must stay behind
	if (!(entityIn instanceof ServerPlayer))
	{
	    return;
	}

	if (!entityIn.isPassenger() && !entityIn.isVehicle() && entityIn.canChangeDimensions())
	{
	    //DimDungeons.LOGGER.info("Entity " + entityIn.getName().getString() + " just entered a gold portal.");

	    BlockEntity tile = worldIn.getBlockEntity(pos);

	    if (tile != null && tile instanceof TileEntityGoldPortal)
	    {
		TileEntityGoldPortal te = (TileEntityGoldPortal) worldIn.getBlockEntity(pos);

		BlockPos destination = te.getDestination();
		float warpX = destination.getX();
		float warpY = destination.getY();
		float warpZ = destination.getZ();
		int cooldown = te.getCooldown();

		// implement the cooldown on the portal block itself
		int currentTick = worldIn.getServer().getTickCount();
		if (!te.needsUpdateThisTick(currentTick))
		{
		    return;
		}
		if (cooldown > 0)
		{
		    //DimDungeons.LOGGER.info("PORTAL BLOCK COOLDOWN: " + cooldown);
		    te.setCooldown(cooldown - 1, worldIn, pos, currentTick);
		    return;
		}
		else
		{
		    //DimDungeons.LOGGER.info("RESETTING COOLDOWN ON PORTAL");
		    te.setCooldown(DungeonConfig.portalCooldownTicks, worldIn, pos, currentTick);
		}

		if (!DungeonUtils.isDimensionDungeon(worldIn))
		{
		    // implement hardcore mode
		    if (DungeonConfig.hardcoreMode)
		    {
			TileEntityPortalKeyhole keyhole = findKeyholeForThisPortal(state, worldIn, pos);
			if (keyhole != null)
			{
			    keyhole.removeContents();
			    BlockState emptyState = worldIn.getBlockState(keyhole.getBlockPos());
			    worldIn.setBlockAndUpdate(keyhole.getBlockPos(), emptyState.setValue(BlockPortalKeyhole.FILLED, false).setValue(BlockPortalKeyhole.LIT, false));
			}
		    }

		    // intentionally don't add 0.5f to the X, so the player is centered between the two blocks of the doorway
		    DimDungeons.logMessageInfo("Player used a key to teleport to dungeon at (" + warpX + ", " + warpZ + ").");
		    actuallyPerformTeleport((ServerPlayer) entityIn, DungeonUtils.getDungeonWorld(worldIn.getServer()), warpX, 55.1D, warpZ + 0.5f, 0);
		}
		else
		{
		    // first check for an unassigned gold portal block
		    if (destination.getX() == 0 && destination.getZ() == 0)
		    {
			sendPlayerBackHome((ServerPlayer) entityIn);
		    }
		    else
		    {
			//System.out.println("Player is returning from a dungeon at (" + warpX + " " + warpY + " " + warpZ + ").");
			ServerPlayer player = (ServerPlayer) entityIn;
			actuallyPerformTeleport(player, player.getServer().getLevel(te.getDestinationDimension()), warpX + 0.5f, warpY + 0.5f, warpZ + 0.5f, 0);
		    }
		}
	    }
	}
    }

    protected Entity actuallyPerformTeleport(ServerPlayer player, ServerLevel dim, double x, double y, double z, double yaw)
    {
	float destPitch = player.getRotationVector().x;
	float destYaw = player.getRotationVector().y;

	// if the player just entered a dungeon then force them to face north
	if (DungeonUtils.isDimensionDungeon(dim))
	{
	    destPitch = 0;
	    destYaw = 180;
	}

	CustomTeleporter tele = new CustomTeleporter(dim);
	tele.setDestPos(x, y, z, destYaw, destPitch);
	player.changeDimension(dim, tele);
	//player.teleport(dim, x, y, z, destYaw, destPitch);
	return player;
    }

    // this is now only used a fail safe in case a BlockGoldPortal somehow ends up 'unassigned' (such as a world being imported from 1.15)
    protected void sendPlayerBackHome(ServerPlayer player)
    {
	float lastX = 0;
	float lastY = 0;
	float lastZ = 0;
	float lastYaw = player.getRotationVector().y;

	// send the player to their bed
	Optional<BlockPos> respawn = player.getSleepingPos();
	if (respawn.isPresent())
	{
	    lastX = respawn.get().getX();
	    lastY = respawn.get().getY() + 3; // plus 3 to stand on the bed
	    lastZ = respawn.get().getZ();
	}
	else
	{
	    // fallback: send the player to the overworld spawn
	    lastX = player.getServer().getLevel(Level.OVERWORLD).getLevelData().getXSpawn();
	    lastY = player.getServer().getLevel(Level.OVERWORLD).getLevelData().getYSpawn() + 2; // plus 2 to stand on the ground I guess
	    lastZ = player.getServer().getLevel(Level.OVERWORLD).getLevelData().getZSpawn();
	}

	actuallyPerformTeleport(player, player.getServer().getLevel(Level.OVERWORLD), lastX, lastY, lastZ, lastYaw);
    }

    // this function returns boolean and relies on another function to actually destroy the block
    public boolean checkPortalIntegrity(BlockState state, LevelAccessor worldIn, BlockPos pos)
    {
	// valid portal shapes are not needed for persistence in the dungeon dimension
	return DungeonUtils.isDimensionDungeon((Level) worldIn) || isPortalShapeIntact(state, worldIn, pos);
    }

    private boolean isPortalShapeIntact(BlockState state, LevelAccessor worldIn, BlockPos pos)
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
	BlockState keyholeBlock = worldIn.getBlockState(te.getBlockPos());
	boolean frameLevel1 = checkPortalFrameLevel1(worldIn, te.getBlockPos());
	if (!frameLevel1)
	{
	    return false;
	}

	// step 4: if this is a level 2 key then check additional portal frame requirements
	ItemStack key = te.getObjectInserted();
	int keyLevel = ((ItemPortalKey) key.getItem()).getKeyLevel(key);
	if (key.getItem() instanceof ItemPortalKey)
	{
	    if (keyLevel >= 2)
	    {
		boolean frameLevel2 = false;
		if (keyholeBlock.getValue(BlockPortalKeyhole.FACING) == Direction.WEST || keyholeBlock.getValue(BlockPortalKeyhole.FACING) == Direction.EAST)
		{
		    frameLevel2 = checkPortalFrameLevel2NorthSouth(worldIn, te.getBlockPos());
		}
		else
		{
		    frameLevel2 = checkPortalFrameLevel2WestEast(worldIn, te.getBlockPos());
		}
		if (!frameLevel2)
		{
		    return false;
		}
	    }
	}

	return true;
    }

    // return the tile entity if it can be found, or NULL otherwise (in which case this portal block will soon vanish)
    private TileEntityPortalKeyhole findKeyholeForThisPortal(BlockState state, LevelAccessor worldIn, BlockPos pos)
    {
	BlockPos p = pos.above();

	// look 1-2 blocks up for a BlockPortalKeyhole
	for (int i = 0; i < 2; i++)
	{
	    BlockState keyhole = worldIn.getBlockState(p);
	    if (keyhole.getBlock() == BlockRegistrar.block_portal_keyhole)
	    {
		return (TileEntityPortalKeyhole) worldIn.getBlockEntity(p);
	    }
	    p = p.above();
	}

	return null;
    }

    static public boolean isValidPortalFrameBlock(Block b)
    {
	//Tag<Block> tag = BlockTags.getAllTags().getTagOrEmpty(new ResourceLocation(DimDungeons.MOD_ID, "dimdungeons_portal_frame_blocks"));
	return b.getTags().contains(new ResourceLocation(DimDungeons.MOD_ID, "dimdungeons_portal_frame_blocks"));
    }

    // just get the block states and keep it simple
    private boolean checkPortalFrameLevel1(LevelAccessor worldIn, BlockPos keyhole)
    {
	BlockState keyholeState = worldIn.getBlockState(keyhole);
	ArrayList<BlockState> blocks;

	if (keyholeState.getValue(BlockPortalKeyhole.FACING) == Direction.WEST || keyholeState.getValue(BlockPortalKeyhole.FACING) == Direction.EAST)
	{
	    blocks = getPortalFrameMaterialsNorthSouth(worldIn, keyhole);
	}
	else
	{
	    blocks = getPortalFrameMaterialsWestEast(worldIn, keyhole);
	}

	for (int i = 0; i < 9; i++)
	{
	    if (!isValidPortalFrameBlock(blocks.get(i).getBlock()))
	    {
		return false;
	    }
	}

	if (blocks.get(9).getBlock() != BlockRegistrar.block_gilded_portal || blocks.get(10).getBlock() != BlockRegistrar.block_gilded_portal)
	{
	    return false;
	}

	return true;
    }

    // also used by the keyhole when telling the player what went wrong
    static public ArrayList<BlockState> getPortalFrameMaterialsWestEast(LevelAccessor worldIn, BlockPos keyhole)
    {
	ArrayList<BlockState> retval = new ArrayList<BlockState>();

	// the first 5 elements are for the main portal frame
	retval.add(worldIn.getBlockState(keyhole.west().below()));
	retval.add(worldIn.getBlockState(keyhole.west().below(2)));
	retval.add(worldIn.getBlockState(keyhole.east().below()));
	retval.add(worldIn.getBlockState(keyhole.east().below(2)));
	retval.add(worldIn.getBlockState(keyhole.below(3)));

	// the next 4 elements are for the bricks in the left and right spires
	retval.add(worldIn.getBlockState(keyhole.west(3).below(2)));
	retval.add(worldIn.getBlockState(keyhole.west(3).below(3)));
	retval.add(worldIn.getBlockState(keyhole.east(3).below(2)));
	retval.add(worldIn.getBlockState(keyhole.east(3).below(3)));

	// the next 2 elements are for the gilded portal blocks on top of each spire
	retval.add(worldIn.getBlockState(keyhole.west(3).below(1)));
	retval.add(worldIn.getBlockState(keyhole.east(3).below(1)));

	// the next 2 elements are for the crowns, if there are any
	retval.add(worldIn.getBlockState(keyhole.west(1)));
	retval.add(worldIn.getBlockState(keyhole.east(1)));

	// the next 4 elements are for the banners, if there are any (side doesn't matter, any two can pass)
	retval.add(worldIn.getBlockState(keyhole.west(3).below(1).north(1)));
	retval.add(worldIn.getBlockState(keyhole.west(3).below(1).south(1)));
	retval.add(worldIn.getBlockState(keyhole.east(3).below(1).north(1)));
	retval.add(worldIn.getBlockState(keyhole.east(3).below(1).south(1)));

	return retval;
    }

    // also used by the keyhole when telling the player what went wrong
    static public ArrayList<BlockState> getPortalFrameMaterialsNorthSouth(LevelAccessor worldIn, BlockPos keyhole)
    {
	ArrayList<BlockState> retval = new ArrayList<BlockState>();

	// the first 5 elements are for the main portal frame
	retval.add(worldIn.getBlockState(keyhole.north().below()));
	retval.add(worldIn.getBlockState(keyhole.north().below(2)));
	retval.add(worldIn.getBlockState(keyhole.south().below()));
	retval.add(worldIn.getBlockState(keyhole.south().below(2)));
	retval.add(worldIn.getBlockState(keyhole.below(3)));

	// the next 4 elements are for the bricks in the left and right spires
	retval.add(worldIn.getBlockState(keyhole.north(3).below(2)));
	retval.add(worldIn.getBlockState(keyhole.north(3).below(3)));
	retval.add(worldIn.getBlockState(keyhole.south(3).below(2)));
	retval.add(worldIn.getBlockState(keyhole.south(3).below(3)));

	// the next 2 elements are for the gilded portal blocks on top of each spire
	retval.add(worldIn.getBlockState(keyhole.north(3).below(1)));
	retval.add(worldIn.getBlockState(keyhole.south(3).below(1)));

	// the next 2 elements are for the crowns, if there are any
	retval.add(worldIn.getBlockState(keyhole.north(1)));
	retval.add(worldIn.getBlockState(keyhole.south(1)));

	// the next 4 elements are for the banners, if there are any (side doesn't matter, any two can pass)
	retval.add(worldIn.getBlockState(keyhole.north(3).below(1).east(1)));
	retval.add(worldIn.getBlockState(keyhole.north(3).below(1).west(1)));
	retval.add(worldIn.getBlockState(keyhole.south(3).below(1).east(1)));
	retval.add(worldIn.getBlockState(keyhole.south(3).below(1).west(1)));

	return retval;
    }

    private boolean checkPortalFrameLevel2WestEast(LevelAccessor worldIn, BlockPos keyhole)
    {
	// main portal body - check for added crowns
	if (worldIn.getBlockState(keyhole.west(1)).getBlock() != BlockRegistrar.block_portal_crown || worldIn.getBlockState(keyhole.east(1)).getBlock() != BlockRegistrar.block_portal_crown)
	{
	    return false;
	}

	// left spire - check for banner
	int front = getBannerLevel(worldIn, keyhole.west(3).below(1).north(1));
	int back = getBannerLevel(worldIn, keyhole.west(3).below(1).south(1));
	if (front < 2 && back < 2)
	{
	    return false;
	}

	// right spire - check for banner
	front = getBannerLevel(worldIn, keyhole.east(3).below(1).north(1));
	back = getBannerLevel(worldIn, keyhole.east(3).below(1).south(1));
	if (front < 2 && back < 2)
	{
	    return false;
	}

	return true;
    }

    // just get the block states and keep it simple
    private boolean checkPortalFrameLevel2NorthSouth(LevelAccessor worldIn, BlockPos keyhole)
    {
	// main portal body - check for added crowns
	if (worldIn.getBlockState(keyhole.north(1)).getBlock() != BlockRegistrar.block_portal_crown || worldIn.getBlockState(keyhole.south(1)).getBlock() != BlockRegistrar.block_portal_crown)
	{
	    return false;
	}

	// left spire - check for banner
	int front = getBannerLevel(worldIn, keyhole.north(3).below(1).west(1));
	int back = getBannerLevel(worldIn, keyhole.north(3).below(1).east(1));
	if (front < 2 && back < 2)
	{
	    return false;
	}

	// right spire - check for banner
	front = getBannerLevel(worldIn, keyhole.south(3).below(1).west(1));
	back = getBannerLevel(worldIn, keyhole.south(3).below(1).east(1));
	if (front < 2 && back < 2)
	{
	    return false;
	}

	return true;
    }

    // I had to basically remove this check because all methods relating to getBannerPattern() were removed in recent versions of both 1.14 and 1.15
    static public int getBannerLevel(LevelAccessor worldIn, BlockPos pos)
    {
	Block banner = worldIn.getBlockState(pos).getBlock();

	// first ensure that the tile entity is going to exist
	if (!(banner == Blocks.WHITE_WALL_BANNER || banner == Blocks.PURPLE_WALL_BANNER))
	{
	    return 0;
	}

	return 2; // so now any white or purple banner is acceptable
    }

    /**
     * Called periodically client side on blocks near the player to show effects (like furnace fire particles). Note that
     * this method is unrelated to randomTick and needsRandomTick, and will always be called regardless of whether the block
     * can receive random update ticks
     */
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, Random rand)
    {
	if (DungeonConfig.showParticles)
	{
	    double d0 = (double) ((float) pos.getX() + rand.nextFloat());
	    double d1 = (double) ((float) pos.getY() + 0.8F);
	    double d2 = (double) ((float) pos.getZ() + rand.nextFloat());
	    double xspeed = rand.nextFloat() * (rand.nextInt(3) - 1) / 9;
	    double zspeed = rand.nextFloat() * (rand.nextInt(3) - 1) / 9;
	    worldIn.addParticle(ParticleTypes.END_ROD, d0, d1, d2, xspeed, 0.0D, zspeed);
	}
    }

    // copy/pasted from BannerPattern.byHash() because it was needed for a 1.15 workaround
    public static BannerPattern getBannerForHash(String hash)
    {
	for (BannerPattern bannerpattern : BannerPattern.values())
	{
	    if (bannerpattern.getHashname().equals(hash))
	    {
		return bannerpattern;
	    }
	}

	return null;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
	return new TileEntityGoldPortal(pos, state);
    }
}