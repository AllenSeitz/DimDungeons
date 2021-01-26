package com.catastrophe573.dimdungeons.block;

import java.util.ArrayList;
import java.util.Optional;
//import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.DungeonConfig;
import com.catastrophe573.dimdungeons.block.BlockPortalKeyhole;
import com.catastrophe573.dimdungeons.dimension.CustomTeleporter;
import com.catastrophe573.dimdungeons.item.ItemPortalKey;
//import com.google.common.collect.Lists;
//import com.mojang.datafixers.util.Pair;
import com.catastrophe573.dimdungeons.utils.DungeonUtils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BreakableBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
//import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
//import net.minecraft.nbt.CompoundNBT;
//import net.minecraft.nbt.ListNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.BannerPattern;
//import net.minecraft.tileentity.BannerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
//import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class BlockGoldPortal extends BreakableBlock
{
    public static String REG_NAME = "block_gold_portal";

    public BlockGoldPortal()
    {
	super(Block.Properties.create(Material.PORTAL).hardnessAndResistance(50).sound(SoundType.GLASS).doesNotBlockMovement().setLightLevel((p) -> 15));
	setRegistryName(DimDungeons.MOD_ID, REG_NAME);
    }

    // Called by ItemBlocks after a block is set in the world, to allow post-place logic
    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
	if (!checkPortalIntegrity(state, worldIn, pos))
	{
	    worldIn.destroyBlock(pos, false);
	}
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
	if (!this.checkPortalIntegrity(state, worldIn, pos))
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
    @Override
    public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state)
    {
	return ItemStack.EMPTY;
    }

    @Deprecated
    @Override
    public boolean isTransparent(BlockState state)
    {
	return true;
    }

    // called When an entity collides with the Block
    @Override
    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn)
    {
	// do not process this block on the client
	if (worldIn.isRemote)
	{
	    return;
	}

	// only teleport players! items and mobs and who knows what else must stay behind
	if (!(entityIn instanceof ServerPlayerEntity))
	{
	    return;
	}

	// manually check/use portal
	if (entityIn.func_242280_ah()) // unmapped name of isEntityPortalCooldownActive()
	{
	    return;
	}

	if (!entityIn.isPassenger() && !entityIn.isBeingRidden() && entityIn.isNonBoss())
	{
	    //DimDungeons.LOGGER.info("Entity " + entityIn.getName().getString() + " just entered a gold portal.");

	    //TileEntityPortalKeyhole te = findKeyholeForThisPortal(state, worldIn, pos);
	    TileEntity tile = worldIn.getTileEntity(pos);

	    if (tile != null && tile instanceof TileEntityGoldPortal)
	    {
		TileEntityGoldPortal te = (TileEntityGoldPortal) worldIn.getTileEntity(pos);

		BlockPos destination = te.getDestination();
		float warpX = destination.getX();
		float warpY = destination.getY();
		float warpZ = destination.getZ();

		if (DungeonUtils.isDimensionOverworld(worldIn))
		{
		    // intentionally don't add 0.5f to the X, so the player is centered between the two blocks of the doorway
		    System.out.println("Player used a key to teleport to dungeon at (" + warpX + ", " + warpZ + ").");
		    actuallyPerformTeleport((ServerPlayerEntity) entityIn, DungeonUtils.getDungeonWorld(worldIn.getServer()), warpX, 55.1D, warpZ + 0.5f, 0);
		}
		else if (worldIn.getDimensionKey() == DungeonUtils.getDungeonWorld(worldIn.getServer()).getDimensionKey())
		{
		    // first check for an unassigned gold portal block
		    if (destination.getX() == 0 && destination.getZ() == 0)
		    {
			sendPlayerBackHome((ServerPlayerEntity) entityIn);
		    }
		    else
		    {
			//System.out.println("Player is returning from a dungeon at (" + warpX + " " + warpY + " " + warpZ + ").");
			ServerPlayerEntity player = (ServerPlayerEntity) entityIn;
			actuallyPerformTeleport(player, player.getServer().getWorld(World.OVERWORLD), warpX + 0.5f, warpY + 0.5f, warpZ + 0.5f, 0);
		    }
		}
	    }
	}
    }

    @Override
    public boolean hasTileEntity(BlockState state)
    {
	return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
    {
	return new TileEntityGoldPortal();
    }

    protected Entity actuallyPerformTeleport(ServerPlayerEntity player, ServerWorld dim, double x, double y, double z, double yaw)
    {
	// manually set portal cooldown
	player.func_242279_ag(); // unmapped version of startEntityPortalCooldown()

	float destPitch = player.getPitchYaw().x;
	float destYaw = player.getPitchYaw().y;

	// if the player just entered a dungeon then force them to face north
	if (DungeonUtils.isDimensionDungeon(dim))
	{
	    destPitch = 0;
	    destYaw = 180;
	}

	CustomTeleporter tele = new CustomTeleporter(dim);
	player.changeDimension(dim, tele);
	player.teleport(dim, x, y, z, destYaw, destPitch);
	return player;
    }

    // this is now only used a fail safe in case a BlockGoldPortal somehow ends up 'unassigned' (such as a world being imported from 1.15)
    protected void sendPlayerBackHome(ServerPlayerEntity player)
    {
	float lastX = 0;
	float lastY = 0;
	float lastZ = 0;
	float lastYaw = player.getPitchYaw().y;

	// send the player to their bed
	Optional<BlockPos> respawn = player.getBedPosition();
	if (respawn.isPresent())
	{
	    lastX = respawn.get().getX();
	    lastY = respawn.get().getY() + 3; // plus 3 to stand on the bed
	    lastZ = respawn.get().getZ();
	}
	else
	{
	    // fallback: send the player to the overworld spawn
	    lastX = player.getServer().getWorld(World.OVERWORLD).getWorldInfo().getSpawnX();
	    lastY = player.getServer().getWorld(World.OVERWORLD).getWorldInfo().getSpawnY() + 2; // plus 2 to stand on the ground I guess
	    lastZ = player.getServer().getWorld(World.OVERWORLD).getWorldInfo().getSpawnZ();
	}

	actuallyPerformTeleport(player, player.getServer().getWorld(World.OVERWORLD).getWorldServer(), lastX, lastY, lastZ, lastYaw);
    }

    // this function returns boolean and relies on another function to actually destroy the block
    public boolean checkPortalIntegrity(BlockState state, IWorld worldIn, BlockPos pos)
    {
	// valid portal shapes are not needed for persistence in the dungeon dimension
	return DungeonUtils.isDimensionDungeon((World) worldIn) || isPortalShapeIntact(state, worldIn, pos);
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
	boolean frameLevel1 = checkPortalFrameLevel1(worldIn, te.getPos());
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
		if (keyholeBlock.get(BlockPortalKeyhole.FACING) == Direction.WEST || keyholeBlock.get(BlockPortalKeyhole.FACING) == Direction.EAST)
		{
		    frameLevel2 = checkPortalFrameLevel2NorthSouth(worldIn, te.getPos());
		}
		else
		{
		    frameLevel2 = checkPortalFrameLevel2WestEast(worldIn, te.getPos());
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

    static public boolean isValidPortalFrameBlock(Block b)
    {
	return b == Blocks.STONE_BRICKS || b == Blocks.CRACKED_STONE_BRICKS || b == Blocks.MOSSY_STONE_BRICKS || b == Blocks.CHISELED_STONE_BRICKS;
    }

    // just get the block states and keep it simple
    private boolean checkPortalFrameLevel1(IWorld worldIn, BlockPos keyhole)
    {
	BlockState keyholeState = worldIn.getBlockState(keyhole);
	ArrayList<BlockState> blocks;

	if (keyholeState.get(BlockPortalKeyhole.FACING) == Direction.WEST || keyholeState.get(BlockPortalKeyhole.FACING) == Direction.EAST)
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
    static public ArrayList<BlockState> getPortalFrameMaterialsWestEast(IWorld worldIn, BlockPos keyhole)
    {
	ArrayList<BlockState> retval = new ArrayList<BlockState>();

	// the first 5 elements are for the main portal frame
	retval.add(worldIn.getBlockState(keyhole.west().down()));
	retval.add(worldIn.getBlockState(keyhole.west().down(2)));
	retval.add(worldIn.getBlockState(keyhole.east().down()));
	retval.add(worldIn.getBlockState(keyhole.east().down(2)));
	retval.add(worldIn.getBlockState(keyhole.down(3)));

	// the next 4 elements are for the bricks in the left and right spires
	retval.add(worldIn.getBlockState(keyhole.west(3).down(2)));
	retval.add(worldIn.getBlockState(keyhole.west(3).down(3)));
	retval.add(worldIn.getBlockState(keyhole.east(3).down(2)));
	retval.add(worldIn.getBlockState(keyhole.east(3).down(3)));

	// the next 2 elements are for the gilded portal blocks on top of each spire
	retval.add(worldIn.getBlockState(keyhole.west(3).down(1)));
	retval.add(worldIn.getBlockState(keyhole.east(3).down(1)));

	// the next 2 elements are for the crowns, if there are any
	retval.add(worldIn.getBlockState(keyhole.west(1)));
	retval.add(worldIn.getBlockState(keyhole.east(1)));

	// the next 4 elements are for the banners, if there are any (side doesn't matter, any two can pass)
	retval.add(worldIn.getBlockState(keyhole.west(3).down(1).north(1)));
	retval.add(worldIn.getBlockState(keyhole.west(3).down(1).south(1)));
	retval.add(worldIn.getBlockState(keyhole.east(3).down(1).north(1)));
	retval.add(worldIn.getBlockState(keyhole.east(3).down(1).south(1)));

	return retval;
    }

    // also used by the keyhole when telling the player what went wrong
    static public ArrayList<BlockState> getPortalFrameMaterialsNorthSouth(IWorld worldIn, BlockPos keyhole)
    {
	ArrayList<BlockState> retval = new ArrayList<BlockState>();

	// the first 5 elements are for the main portal frame
	retval.add(worldIn.getBlockState(keyhole.north().down()));
	retval.add(worldIn.getBlockState(keyhole.north().down(2)));
	retval.add(worldIn.getBlockState(keyhole.south().down()));
	retval.add(worldIn.getBlockState(keyhole.south().down(2)));
	retval.add(worldIn.getBlockState(keyhole.down(3)));

	// the next 4 elements are for the bricks in the left and right spires
	retval.add(worldIn.getBlockState(keyhole.north(3).down(2)));
	retval.add(worldIn.getBlockState(keyhole.north(3).down(3)));
	retval.add(worldIn.getBlockState(keyhole.south(3).down(2)));
	retval.add(worldIn.getBlockState(keyhole.south(3).down(3)));

	// the next 2 elements are for the gilded portal blocks on top of each spire
	retval.add(worldIn.getBlockState(keyhole.north(3).down(1)));
	retval.add(worldIn.getBlockState(keyhole.south(3).down(1)));

	// the next 2 elements are for the crowns, if there are any
	retval.add(worldIn.getBlockState(keyhole.north(1)));
	retval.add(worldIn.getBlockState(keyhole.south(1)));

	// the next 4 elements are for the banners, if there are any (side doesn't matter, any two can pass)
	retval.add(worldIn.getBlockState(keyhole.north(3).down(1).east(1)));
	retval.add(worldIn.getBlockState(keyhole.north(3).down(1).west(1)));
	retval.add(worldIn.getBlockState(keyhole.south(3).down(1).east(1)));
	retval.add(worldIn.getBlockState(keyhole.south(3).down(1).west(1)));

	return retval;
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
	if (front < 2 && back < 2)
	{
	    return false;
	}

	// right spire - check for banner
	front = getBannerLevel(worldIn, keyhole.east(3).down(1).north(1));
	back = getBannerLevel(worldIn, keyhole.east(3).down(1).south(1));
	if (front < 2 && back < 2)
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
	if (front < 2 && back < 2)
	{
	    return false;
	}

	// right spire - check for banner
	front = getBannerLevel(worldIn, keyhole.south(3).down(1).west(1));
	back = getBannerLevel(worldIn, keyhole.south(3).down(1).east(1));
	if (front < 2 && back < 2)
	{
	    return false;
	}

	return true;
    }

    // I had to basically remove this check because all methods relating to getBannerPattern() were removed in recent versions of both 1.14 and 1.15
    static public int getBannerLevel(IWorld worldIn, BlockPos pos)
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
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand)
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
}