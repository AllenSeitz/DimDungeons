package com.catastrophe573.dimdungeons.block;

import java.util.ArrayList;
import java.util.Random;

import javax.annotation.Nullable;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.DungeonConfig;
import com.catastrophe573.dimdungeons.item.BaseItemKey;
import com.catastrophe573.dimdungeons.item.ItemBuildKey;
import com.catastrophe573.dimdungeons.item.ItemPortalKey;
import com.catastrophe573.dimdungeons.structure.DungeonPlacement;
import com.catastrophe573.dimdungeons.structure.DungeonDesigner.DungeonType;
import com.catastrophe573.dimdungeons.utils.DungeonGenData;
import com.catastrophe573.dimdungeons.utils.DungeonUtils;

import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Containers;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.network.chat.TextColor;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class BlockPortalKeyhole extends BaseEntityBlock
{
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty FILLED = BooleanProperty.create("filled");
    public static final BooleanProperty LIT = BooleanProperty.create("lit");
    public static final IntegerProperty BUILD_STEP = IntegerProperty.create("build_step", 0, 651);
    public static final BooleanProperty BUILD_PARTICLE = BooleanProperty.create("build_particle");

    public static final String REG_NAME = "block_portal_keyhole";

    public BlockPortalKeyhole()
    {
	super(BlockBehaviour.Properties.of(Material.STONE).strength(3).explosionResistance(1200).sound(SoundType.METAL));
	this.registerDefaultState(getMyCustomDefaultState());
    }

    // used by the constructor and I'm not sure where else anymore?
    public BlockState getMyCustomDefaultState()
    {
	return this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(FILLED, false).setValue(LIT, false).setValue(BUILD_STEP, 0).setValue(BUILD_PARTICLE, false);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
    {
	if (level.isClientSide)
	{
	    return null;
	}
	else
	{
	    if (state.getValue(BUILD_STEP) > 0 && type == TileEntityPortalKeyhole.TYPE)
	    {
		return createTickerHelper(type, TileEntityPortalKeyhole.TYPE, TileEntityPortalKeyhole::buildTick);
	    }
	}
	return null;
    }

    // based on code from vanilla furnaces, which also play a sound effect and make particles when their TileEntity is being productive
    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, Random rand)
    {
	boolean hasPortalBlockBelow = worldIn.getBlockState(pos.below()).getBlock() == BlockRegistrar.block_gold_portal;

	Direction enumfacing = (Direction) stateIn.getValue(FACING);
	double d0 = (double) pos.getX() + 0.5D;
	double d1 = (double) pos.getY() + rand.nextDouble() * 6.0D / 16.0D;
	double d2 = (double) pos.getZ() + 0.5D;
	double d4 = rand.nextDouble() * 0.6D - 0.3D;

	if (stateIn.getValue(LIT) && hasPortalBlockBelow)
	{
	    // play sound effects randomly
	    if (rand.nextDouble() < 0.1D && DungeonConfig.playPortalSounds)
	    {
		worldIn.playLocalSound((double) pos.getX() + 0.5D, (double) pos.getY(), (double) pos.getZ() + 0.5D, SoundEvents.PORTAL_AMBIENT, SoundSource.BLOCKS, 1.0F, 2.5F, false);
	    }

	    if (DungeonConfig.showParticles)
	    {
		switch (enumfacing)
		{
		case WEST:
		    worldIn.addParticle(ParticleTypes.PORTAL, d0 - 0.52D, d1, d2 + d4, -1.0D, 1.0D, 0.0D);
		    break;
		case EAST:
		    worldIn.addParticle(ParticleTypes.PORTAL, d0 + 0.52D, d1, d2 + d4, 1.0D, 1.0D, 0.0D);
		    break;
		case NORTH:
		    worldIn.addParticle(ParticleTypes.PORTAL, d0 + d4, d1, d2 - 0.52D, 0.0D, 1.0D, -1.0D);
		    break;
		default:
		    worldIn.addParticle(ParticleTypes.PORTAL, d0 + d4, d1, d2 + 0.52D, 0.0D, 1.0D, 1.0D);
		    break;
		}
	    }
	}

	// this happens on ticks when the keyhole places a structure
	if (stateIn.getValue(BUILD_PARTICLE) || stateIn.getValue(BUILD_STEP) > 0)
	{
	    if (DungeonConfig.playPortalSounds)
	    {
		float randomPitch = worldIn.getRandom().nextFloat() * 2.0f;
		worldIn.playLocalSound((double) pos.getX() + 0.5D, (double) pos.getY(), (double) pos.getZ() + 0.5D, SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.BLOCKS, 0.7F, randomPitch, false);
	    }

	    if (DungeonConfig.showParticles)
	    {
		switch (enumfacing)
		{
		case WEST:
		    worldIn.addParticle(ParticleTypes.FIREWORK, d0 - 0.12D, d1 + 1.5D, d2 + d4, 0.0D, 0.0D, 0.23D);
		    worldIn.addParticle(ParticleTypes.FIREWORK, d0 - 0.12D, d1 + 1.5D, d2 + d4, 0.0D, 0.0D, -0.23D);

		    worldIn.addParticle(ParticleTypes.FLAME, d0 + d4, d1 + 0.5D, d2 - 0.12D, -0. - 0.08D, 0.0D, 0.0D);
		    break;
		case EAST:
		    worldIn.addParticle(ParticleTypes.FIREWORK, d0 + 0.12D, d1 + 1.5D, d2 + d4, 0.0D, 0.0D, 0.23D);
		    worldIn.addParticle(ParticleTypes.FIREWORK, d0 + 0.12D, d1 + 1.5D, d2 + d4, 0.0D, 0.0D, -0.23D);

		    worldIn.addParticle(ParticleTypes.FLAME, d0 + d4, d1 + 0.5D, d2 - 0.12D, 0.08D, 0.0D, 0.0D);
		    break;
		case NORTH:
		    worldIn.addParticle(ParticleTypes.FIREWORK, d0 + d4, d1 + 1.5D, d2 - 0.12D, 0.23D, 0.0D, 0.0D);
		    worldIn.addParticle(ParticleTypes.FIREWORK, d0 + d4, d1 + 1.5D, d2 - 0.12D, -0.23D, 0.0D, 0.0D);

		    worldIn.addParticle(ParticleTypes.FLAME, d0 - 0.12D, d1 + 0.5D, d2 + d4, 0.0D, 0.0D, -0.08D);
		    break;
		default:
		case SOUTH:
		    worldIn.addParticle(ParticleTypes.FIREWORK, d0 + d4, d1 + 1.5D, d2 + 0.12D, 0.23D, 0.0D, 0.0D);
		    worldIn.addParticle(ParticleTypes.FIREWORK, d0 + d4, d1 + 1.5D, d2 + 0.12D, -0.23D, 0.0D, 0.0D);

		    worldIn.addParticle(ParticleTypes.FLAME, d0 - 0.12D, d1 + 0.5D, d2 + d4, 0.0D, 0.0D, 0.08D);
		    break;
		}
	    }
	}
    }

    // called when the player right clicks this block
    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit)
    {
	ItemStack playerItem = player.getItemInHand(handIn);
	BlockEntity tileEntity = worldIn.getBlockEntity(pos);
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
		    // DimDungeons.LOGGER.info("Putting " + playerItem.getDisplayName().getString() + " inside keyhole...");
		    int buildStep = 0;

		    myEntity.setContents(playerItem.copy());

		    // should we begin to build the dungeon on the other side?
		    if (playerItem.getItem() instanceof ItemPortalKey && !worldIn.isClientSide)
		    {
			// all this math just to figure out where the coordinates of the dungeon are
			ItemPortalKey key = (ItemPortalKey) playerItem.getItem();
			long buildX = (long) key.getDungeonTopLeftX(playerItem);
			long buildZ = (long) key.getDungeonTopLeftZ(playerItem);
			long entranceX = buildX + (8 * 16);
			long entranceZ = buildZ + (11 * 16);

			// this data structure is used for both building the layout and updating the exit portal
			DungeonGenData genData = DungeonGenData.Create().setKeyItem(playerItem).setDungeonType(key.getDungeonType(playerItem)).setTheme(key.getDungeonTheme(playerItem)).setReturnPoint(BlockPortalKeyhole.getReturnPoint(state, pos), DungeonUtils.serializeDimensionKey(worldIn.dimension()));

			// should the key be marked as used?
			if (shouldBuildDungeon(playerItem))
			{
			    if (!DungeonUtils.dungeonAlreadyExistsHere(worldIn, entranceX, entranceZ))
			    {
				//DimDungeons.LOGGER.info("BUILDING A NEW DUNGEON!");
				playerItem.getTag().putBoolean(ItemPortalKey.NBT_BUILT, true);
				myEntity.setContents(playerItem.copy()); // do this again to solve a bug
				DungeonPlacement.beginDesignAndBuild(DungeonUtils.getDungeonWorld(worldIn.getServer()), buildX, buildZ, genData);
			    }

			    // it's slow, but run through the build steps regardless of if the dungeon already exists
			    // this will catch dungeons that are partially built and finish them
			    // dungeon rooms will never be overwritten or built twice
			    buildStep = 1;
			}
			else
			{
			    buildStep = 650;
			}
		    }
		    else if (playerItem.getItem() instanceof ItemBuildKey && !worldIn.isClientSide)
		    {
			// building a personal build space is different
			ItemBuildKey key = (ItemBuildKey) playerItem.getItem();
			long buildX = (long) key.getDungeonTopLeftX(playerItem);
			long buildZ = (long) key.getDungeonTopLeftZ(playerItem);

			// this is used for updating the exist portal
			DungeonGenData genData = DungeonGenData.Create().setKeyItem(playerItem).setReturnPoint(BlockPortalKeyhole.getReturnPoint(state, pos), DungeonUtils.serializeDimensionKey(worldIn.dimension()));

			if (key.isActivated(playerItem) && !key.isPlotBuilt(playerItem))
			{
			    if (!DungeonUtils.personalPortalAlreadyExistsHere(worldIn, buildX, buildZ))
			    {
				DimDungeons.logMessageInfo("DIMENSIONAL DUNGEONS: building a new personal dimension.");
				playerItem.getTag().putBoolean(ItemPortalKey.NBT_BUILT, true);
				DungeonUtils.buildSuperflatPersonalSpace(buildX, buildZ, player.getServer());
			    }
			}

			// buildStep must ALWAYS be 0 when using an ItemBuildKey, or else the keyhole might start ticking
			buildStep = 0;
			DungeonUtils.openPortalAfterBuild(worldIn, pos, genData, myEntity);
		    }

		    // recalculate the block states
		    BlockState newBlockState = state.setValue(FACING, state.getValue(FACING)).setValue(FILLED, myEntity.isFilled()).setValue(LIT, myEntity.isActivated()).setValue(BUILD_STEP, buildStep);
		    worldIn.setBlockAndUpdate(pos, newBlockState);

		    playerItem.shrink(1);

		    return InteractionResult.SUCCESS;
		}
	    }
	    // if the keyhole is currently full
	    else
	    {
		// DimDungeons.LOGGER.info("Taking thing out of keyhole...");
		if (playerItem.isEmpty())
		{
		    player.setItemInHand(handIn, insideItem); // hand it to the player
		}
		else if (!player.addItem(insideItem)) // okay put it in their inventory
		{
		    player.drop(insideItem, false); // whatever drop it on the ground
		}

		myEntity.removeContents();

		// recalculate the boolean block states
		BlockState newBlockState = state.setValue(FACING, state.getValue(FACING)).setValue(FILLED, myEntity.isFilled()).setValue(LIT, myEntity.isActivated()).setValue(BUILD_STEP, 0);
		worldIn.setBlock(pos, newBlockState, 3);

		return InteractionResult.SUCCESS;
	    }
	}

	return InteractionResult.PASS;
    }

    public static void addGoldenPortalBlock(Level worldIn, BlockPos pos, ItemStack keyStack, Direction.Axis axis)
    {
	worldIn.setBlockAndUpdate(pos, BlockRegistrar.block_gold_portal.defaultBlockState().setValue(BlockGoldPortal.AXIS, axis));
	TileEntityGoldPortal te = (TileEntityGoldPortal) worldIn.getBlockEntity(pos);
	if (te != null && te instanceof TileEntityGoldPortal)
	{
	    BaseItemKey key = (BaseItemKey) keyStack.getItem();
	    if (key != null)
	    {
		if (key instanceof ItemPortalKey)
		{
		    ItemPortalKey keyItem = (ItemPortalKey) keyStack.getItem();
		    Direction enterFacing = Direction.NORTH;

		    // teleporter hubs are currently the only way a player can enter the dungeon dimension facing not-north
		    if (keyItem.getDungeonType(keyStack) == DungeonType.TELEPORTER_HUB)
		    {
			enterFacing = getTeleporterHubEntranceDirection(keyItem.getDungeonTheme(keyStack));
		    }
		    te.setDestination(key.getWarpX(keyStack), 55.1D, key.getWarpZ(keyStack), DungeonUtils.serializeDimensionKey(DimDungeons.DUNGEON_DIMENSION), enterFacing);
		}
		else if (key instanceof ItemBuildKey)
		{
		    te.setDestination(key.getWarpX(keyStack), 51.1D, key.getWarpZ(keyStack), DungeonUtils.serializeDimensionKey(DimDungeons.BUILD_DIMENSION), Direction.NORTH);
		}
	    }
	}
    }

    public static Direction getTeleporterHubEntranceDirection(int doornum)
    {
	Direction exitDirection = Direction.NORTH;
	if (doornum == 2 || doornum == 3)
	{
	    exitDirection = Direction.EAST;
	}
	if (doornum == 4 || doornum == 5)
	{
	    exitDirection = Direction.SOUTH;
	}
	if (doornum == 6 || doornum == 7)
	{
	    exitDirection = Direction.WEST;
	}
	return exitDirection;
    }

    public static BlockPos getReturnPoint(BlockState state, BlockPos pos)
    {
	Direction dir = (Direction) state.getValue(FACING);
	switch (dir)
	{
	case WEST:
	    return pos.west().below(2);
	case EAST:
	    return pos.east().below(2);
	case NORTH:
	    return pos.north().below(2);
	case SOUTH:
	    return pos.south().below(2);
	default:
	    return pos.below(2);
	}
    }

    // helper function for checkForPortalCreation
    public static boolean isOkayToSpawnPortalBlocks(Level worldIn, BlockPos pos, BlockState state, TileEntityPortalKeyhole myEntity)
    {
	// check for air or existing portal blocks below this keyhole
	Block b1 = worldIn.getBlockState(pos.below()).getBlock();
	Block b2 = worldIn.getBlockState(pos.below(2)).getBlock();
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
	if (item.getItem() instanceof BaseItemKey)
	{
	    BaseItemKey key = (BaseItemKey) item.getItem();
	    return key.isActivated(item);
	}

	return false;
    }

    protected boolean shouldBuildDungeon(ItemStack stack)
    {
	if (stack.getItem() instanceof ItemPortalKey)
	{
	    ItemPortalKey key = (ItemPortalKey) stack.getItem();
	    if (key.isActivated(stack))
	    {
		return !key.isDungeonBuilt(stack);
	    }
	}
	return false;
    }

    // Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the IBlockstate
    //public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
	BlockState retval = getMyCustomDefaultState();
	return retval.setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    // Called by ItemBlocks after a block is set in the world, to allow post-place logic
    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
	worldIn.setBlock(pos, state.setValue(FACING, placer.getDirection().getOpposite()), 2);
    }

    // Called server side after this block is replaced with another in Chunk, but before the TileEntity is updated
    // this function is now in charge of preserving TileEntities across block updates, too, instead of the former TileEntity->shouldRefresh()
    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving)
    {
	BlockEntity tileentity = worldIn.getBlockEntity(pos);

	// DO NOT call super.onReplaced() unless this block has no TileEntity, or unless the block was deleted/changed to another block of course
	if (state.getBlock() != newState.getBlock())
	{
	    // if the block was destroyed and it held an item then spit the item out somewhere
	    if (tileentity instanceof TileEntityPortalKeyhole)
	    {
		ItemStack item = ((TileEntityPortalKeyhole) tileentity).getObjectInserted();
		if (!item.isEmpty())
		{
		    Containers.dropItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), item);
		}
		worldIn.updateNeighbourForOutputSignal(pos, this);
	    }

	    super.onRemove(state, worldIn, pos, newState, isMoving);
	    worldIn.removeBlockEntity(pos);
	}
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state)
    {
	return true;
    }

    // return 3 if a build is in progress, 2 if a usable key is inside, 1 if the block is filled with any item stack, and 0 otherwise
    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos)
    {
	if (blockState.getValue(BUILD_STEP) > 0)
	{
	    return 3;
	}
	else if (blockState.getValue(LIT))
	{
	    return 2;
	}
	return blockState.getValue(FILLED) ? 1 : 0;
    }

    // returns the ItemStack that represents this block - this has nothing to do with the item placed inside
    @Override
    public ItemStack getCloneItemStack(BlockGetter worldIn, BlockPos pos, BlockState state)
    {
	return new ItemStack(this);
    }

    // The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only, LIQUID for vanilla liquids, INVISIBLE to skip all rendering
    @Override
    public RenderShape getRenderShape(BlockState state)
    {
	return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
	builder.add(FACING, FILLED, LIT, BUILD_STEP, BUILD_PARTICLE);
    }

    public int predictPortalError(Level worldIn, Player playerIn)
    {
	return 0;
    }

    public static void checkForProblemsAndLiterallySpeakToPlayer(Level worldIn, BlockPos pos, BlockState state, TileEntityPortalKeyhole tileEntity, Player player, boolean dungeonExistsHere)
    {
	// only run this function once, either on the client or on the server
	// this runs on the server now because some errors happen exclusively on the server's side
	if (worldIn.isClientSide || player == null)
	{
	    return;
	}

	// error #1: the key is not activated
	ItemStack item = tileEntity.getObjectInserted();
	int keyLevel = 0;
	if (item.getItem() instanceof ItemPortalKey)
	{
	    ItemPortalKey key = (ItemPortalKey) item.getItem();
	    if (!key.isActivated(item))
	    {
		speakLiterallyToPlayerAboutProblems(worldIn, player, 1, null);
		return;
	    }
	    keyLevel = key.getKeyLevel(item);
	}
	else
	{
	    return; // item inserted is not even a key!
	}

	// error #2: no room to spawn portal
	if (!isOkayToSpawnPortalBlocks(worldIn, pos, state, tileEntity))
	{
	    speakLiterallyToPlayerAboutProblems(worldIn, player, 2, null);
	    return;
	}

	// error #3, error #4: the frame is not complete or contains an invalid block
	ArrayList<BlockState> blocks;
	if (state.getValue(BlockPortalKeyhole.FACING) == Direction.WEST || state.getValue(BlockPortalKeyhole.FACING) == Direction.EAST)
	{
	    blocks = BlockGoldPortal.getPortalFrameMaterialsNorthSouth(worldIn, pos);
	}
	else
	{
	    blocks = BlockGoldPortal.getPortalFrameMaterialsWestEast(worldIn, pos);
	}
	for (int i = 0; i < 5; i++)
	{
	    BlockState b = blocks.get(i);
	    if (b.isAir())
	    {
		speakLiterallyToPlayerAboutProblems(worldIn, player, 3, null);
		return;
	    }
	    if (!BlockGoldPortal.isValidPortalFrameBlock(b.getBlock()))
	    {
		speakLiterallyToPlayerAboutProblems(worldIn, player, 4, b);
		return;
	    }
	}

	// error #5, error #6: the spires are incomplete
	for (int i = 5; i < 9; i++)
	{
	    BlockState b = blocks.get(i);
	    if (b.isAir())
	    {
		speakLiterallyToPlayerAboutProblems(worldIn, player, 5, null);
		return;
	    }
	    if (!BlockGoldPortal.isValidPortalFrameBlock(b.getBlock()))
	    {
		speakLiterallyToPlayerAboutProblems(worldIn, player, 6, b);
		return;
	    }
	}

	// error #7: the gilded portal blocks on top of the spires are missing
	for (int i = 9; i < 11; i++)
	{
	    BlockState b = blocks.get(i);
	    if (b.getBlock() != BlockRegistrar.block_gilded_portal)
	    {
		speakLiterallyToPlayerAboutProblems(worldIn, player, 7, b);
		return;
	    }
	}

	// error #11: the dungeon on the other side of the portal was deleted
	if (!dungeonExistsHere)
	{
	    speakLiterallyToPlayerAboutProblems(worldIn, player, 11, null);
	    return;
	}

	// error (warning) #12: this key is a duplicate of another key (this is not a fatal or serious error, it'll happen sometimes)	
	//if (anotherKeyWasFirst)
	//{
	//    speakLiterallyToPlayerAboutProblems(worldIn, player, 12, null);
	//}

	// only continue performing advanced checks for advanced portal keys
	if (keyLevel < 2)
	{
	    return; // success!
	}

	// error #8: missing crowns
	for (int i = 11; i < 13; i++)
	{
	    BlockState b = blocks.get(i);
	    if (b.getBlock() != BlockRegistrar.block_portal_crown)
	    {
		speakLiterallyToPlayerAboutProblems(worldIn, player, 8, null);
		return;
	    }
	}

	// error #9: missing banners
	BlockState leftSpireA = blocks.get(13);
	BlockState leftSpireB = blocks.get(14);
	BlockState rightSpireA = blocks.get(15);
	BlockState rightSpireB = blocks.get(16);

	if (!(leftSpireA.getBlock() == Blocks.WHITE_WALL_BANNER || leftSpireA.getBlock() == Blocks.PURPLE_WALL_BANNER || leftSpireB.getBlock() == Blocks.WHITE_WALL_BANNER || leftSpireB.getBlock() == Blocks.PURPLE_WALL_BANNER))
	{
	    speakLiterallyToPlayerAboutProblems(worldIn, player, 9, null);
	    return;
	}
	if (!(rightSpireA.getBlock() == Blocks.WHITE_WALL_BANNER || rightSpireA.getBlock() == Blocks.PURPLE_WALL_BANNER || rightSpireB.getBlock() == Blocks.WHITE_WALL_BANNER || rightSpireB.getBlock() == Blocks.PURPLE_WALL_BANNER))
	{
	    speakLiterallyToPlayerAboutProblems(worldIn, player, 9, null);
	    return;
	}

	return; // success!
    }

    // assumes that playerIn is not null
    public static void speakLiterallyToPlayerAboutProblems(Level worldIn, Player playerIn, int problemID, @Nullable BlockState problemBlock)
    {
	TranslatableComponent text1 = new TranslatableComponent(new TranslatableComponent("error.dimdungeons.portal_error_" + problemID).getString());

	// a message that does not call out a specific block
	if (problemBlock != null)
	{
	    // this version of the error message expects a block name to be concatenated
	    text1 = new TranslatableComponent(new TranslatableComponent("error.dimdungeons.portal_error_" + problemID).getString() + problemBlock.getBlock().getRegistryName() + ".");
	}
	text1.withStyle(text1.getStyle().withItalic(true));
	text1.withStyle(text1.getStyle().withColor(TextColor.fromLegacyFormat(ChatFormatting.BLUE)));
	playerIn.sendMessage(text1, Util.NIL_UUID);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
	return new TileEntityPortalKeyhole(pos, state);
    }
}