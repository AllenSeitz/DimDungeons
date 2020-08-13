package com.catastrophe573.dimdungeons.block;

import java.util.ArrayList;
import java.util.Optional;
//import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.block.BlockPortalKeyhole;
import com.catastrophe573.dimdungeons.dimension.DimensionRegistrar;
import com.catastrophe573.dimdungeons.item.ItemPortalKey;
//import com.google.common.collect.Lists;
//import com.mojang.datafixers.util.Pair;

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
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.BannerPattern;
//import net.minecraft.tileentity.BannerTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
//import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.thread.EffectiveSide;

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

    public boolean isOpaqueCube(BlockState state)
    {
	return false;
    }

    public boolean isFullCube(BlockState state)
    {
	return false; // it is but it isn't idk
    }

    // this is the best idea I have for unmapped 1.16.1
    public boolean isDimensionDungeon(World worldIn)
    {
	return worldIn.func_234923_W_().func_240901_a_() == new ResourceLocation(DimDungeons.MOD_ID, DimensionRegistrar.dungeon_basic_regname);
    }

    public static ServerWorld getDungeonWorld(MinecraftServer server)
    {
	ResourceLocation resourceLocation = new ResourceLocation(DimDungeons.MOD_ID, DimensionRegistrar.dungeon_basic_regname);
	RegistryKey<World> regkey = RegistryKey.func_240903_a_(Registry.WORLD_KEY, resourceLocation);
	return server.getWorld(regkey);
    }
    
    // World.field_234918_g_ is the Overworld. This block has different behavior in the Overworld than in the Dungeon Dimension

    public boolean isDimensionOverworld(World worldIn)
    {
	return worldIn.func_234923_W_() == World.field_234918_g_;
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
	    //DimDungeons.LOGGER.info("Entity " + entityIn.getName().getString() + " just entered a gold portal.");

	    TileEntityPortalKeyhole te = findKeyholeForThisPortal(state, worldIn, pos);
	    if (te != null)
	    {
		ItemStack item = te.getObjectInserted();
		if (!item.isEmpty())
		{
		    // World.field_234918_g_ is the Overworld. This block has different behavior in the Overworld than in the Dungeon Dimension
		    if (item.getItem() instanceof ItemPortalKey && isDimensionOverworld(worldIn))
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
			    actuallyPerformTeleport((ServerPlayerEntity) entityIn, getDungeonWorld(worldIn.getServer()), warpX + 0.5f, 55.1D, warpZ + 0.5f, 0);
			}
		    }
		}
	    }
	    else
	    {
		// no keyhole? this could be a return portal
		if (worldIn.func_234923_W_().func_240901_a_() == new ResourceLocation(DimDungeons.MOD_ID, DimensionRegistrar.dungeon_basic_regname))
		{
		    sendPlayerBackHome((ServerPlayerEntity) entityIn);
		}
	    }
	}
    }

    protected Entity actuallyPerformTeleport(ServerPlayerEntity player, ServerWorld dim, double x, double y, double z, double yaw)
    {
	player.timeUntilPortal = 200; // 300 ticks, same as vanilla nether portal (hijacking this also affects nether portals, which is intentional) 

	float destPitch = player.getPitchYaw().x;
	float destYaw = player.getPitchYaw().y;

	// if the player just entered a dungeon then force them to face north 
	if (isDimensionDungeon(dim))
	{
	    destPitch = 0;
	    destYaw = 180;
	}

	player.teleport(dim, x, y, z, destYaw, destPitch);
	return player;
    }

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
	    lastX = player.getServer().getWorld(World.field_234918_g_).getWorldInfo().getSpawnX();
	    lastY = player.getServer().getWorld(World.field_234918_g_).getWorldInfo().getSpawnY() + 2; // plus 2 to stand on the ground I guess
	    lastZ = player.getServer().getWorld(World.field_234918_g_).getWorldInfo().getSpawnZ();
	}

	// the second parameter is a crazy way to obtain the Overworld
	actuallyPerformTeleport(player, player.getServer().getWorld(World.field_234918_g_).getWorldServer(), lastX, lastY, lastZ, lastYaw);
    }

    // this function returns boolean and relies on another function to actually destroy the block
    public boolean checkPortalIntegrity(BlockState state, IWorld worldIn, BlockPos pos)
    {
	// valid portal shapes are not needed for persistence in the dungeon dimension
	return isDimensionDungeon(worldIn.getWorld()) || isPortalShapeIntact(state, worldIn, pos);
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

    //    // it's okay if the block here isn't a banner, I check for that too
    //    public int oldGetBannerLevel(IWorld worldIn, BlockPos pos)
    //    {
    //	boolean level2 = false;
    //	boolean level3 = false;
    //	Block banner = worldIn.getBlockState(pos).getBlock();
    //
    //	// first ensure that the tile entity is going to exist
    //	if (!(banner == Blocks.WHITE_WALL_BANNER || banner == Blocks.PURPLE_WALL_BANNER))
    //	{
    //	    return 0;
    //	}
    //	BannerTileEntity te = (BannerTileEntity) worldIn.getTileEntity(pos);
    //	if (te == null)
    //	{
    //	    return 0;
    //	}
    //	List<Pair<BannerPattern, DyeColor>> patterns = null;
    //
    //	// starting in 1.15 the getPatternList() function was removed from the dedicated server environment
    //	if (EffectiveSide.get() == LogicalSide.CLIENT)
    //	{
    //	    patterns = te.getPatternList(); // the correct way
    //	}
    //	else
    //	{
    //	    // the gross hack to access private data that Mojang doesn't need for the vanilla game, but I do
    //	    Object tempList = ObfuscationReflectionHelper.getPrivateValue(BannerTileEntity.class, te, "field_175118_f");
    //	    ListNBT nbt = (ListNBT) tempList;
    //	    if (nbt == null)
    //	    {
    //		return 0; // legit possible
    //	    }
    //
    //	    // rewrite and steal func_230138_a_() and getPatternList(). Thanks Mojang!
    //	    patterns = Lists.newArrayList();
    //	    //patterns.add(Pair.of(BannerPattern.BASE, baseColor)); // we already know the banner is white or purple at this point
    //	    for (int i = 0; i < nbt.size(); ++i)
    //	    {
    //		CompoundNBT compoundnbt = nbt.getCompound(i);
    //		BannerPattern bannerpattern = getBannerForHash(compoundnbt.getString("Pattern"));
    //		if (bannerpattern != null)
    //		{
    //		    int j = compoundnbt.getInt("Color");
    //		    patterns.add(Pair.of(bannerpattern, DyeColor.byId(j)));
    //		}
    //	    }
    //	}
    //
    //	// check the banner patterns, any matches are fine, any extras are ignored
    //	for (int i = 0; i < patterns.size(); i++)
    //	{
    //	    Pair<BannerPattern, DyeColor> p = patterns.get(i);
    //	    //DimDungeons.LOGGER.info("DIMDUNGEONS: pattern is " + p + ", " + c);
    //
    //	    if (banner == Blocks.WHITE_WALL_BANNER && p.getFirst() == BannerPattern.DIAGONAL_RIGHT && p.getSecond() == DyeColor.PURPLE)
    //	    {
    //		level2 = true;
    //	    }
    //	    if (banner == Blocks.PURPLE_WALL_BANNER && p.getFirst() == BannerPattern.DIAGONAL_LEFT && p.getSecond() == DyeColor.WHITE)
    //	    {
    //		level2 = true;
    //	    }
    //	    // TODO: check for my custom banner pattern for level 3
    //	}
    //
    //	if (level2 && level3)
    //	{
    //	    return 3;
    //	}
    //	else if (level2)
    //	{
    //	    return 2;
    //	}
    //	return 0;
    //    }

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