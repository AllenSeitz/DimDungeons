package com.catastrophe573.dimdungeons.dimension;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.biome.BiomeProviderDungeon;
import com.catastrophe573.dimdungeons.block.BlockRegistrar;
import com.catastrophe573.dimdungeons.structure.DungeonBuilderLogic;
import com.catastrophe573.dimdungeons.structure.DungeonBuilderLogic.DungeonRoom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityDrowned;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.monster.EntityHusk;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityStray;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IWorld;
import net.minecraft.world.LockCode;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.GenerationStage.Carving;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.AbstractChunkGenerator;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class DungeonChunkGenerator extends AbstractChunkGenerator<IChunkGenSettings>
{
    private final Random rand;
    protected IBlockState defaultBlock = Blocks.STONE.getDefaultState();

    public DungeonChunkGenerator(IWorld worldIn, BiomeProvider biomeProviderIn)
    {
	super(worldIn, biomeProviderIn);
	rand = worldIn.getRandom();
    }

    // Generates the chunk at the specified position, from scratch
    public Chunk generateChunk(int x, int z)
    {
	//System.out.println("Calling generateChunk() with " + x + ", " + z);
	long worldSeed = world.getSeed();
	rand.setSeed((worldSeed + (long) (x * x * 4987142) + (long) (x * 5947611) + (long) (z * z) * 4392871L + (long) (z * 389711) ^ worldSeed));
	ChunkPrimer chunkprimer = new ChunkPrimer(null, null);

	// first generate a superflat world - sandstone where dungeons can appear, and void otherwise
	if (isDungeonChunk(x, z))
	{
	    for (int px = 0; px < 16; px++)
	    {
		for (int py = 1; py < 255; py++)
		{
		    for (int pz = 0; pz < 16; pz++)
		    {
			if (py < 2)
			{
			    chunkprimer.setBlockState(new BlockPos(px, py, pz), Blocks.BEDROCK.getDefaultState(), false);
			}
			else if (py < 50)
			{
			    // for debugging
			    if (isEntranceChunk(x, z))
			    {
				chunkprimer.setBlockState(new BlockPos(px, py, pz), Blocks.COBBLESTONE.getDefaultState(), false);

			    }
			    else
			    {
				chunkprimer.setBlockState(new BlockPos(px, py, pz), Blocks.SANDSTONE.getDefaultState(), false);
			    }
			}
		    }
		}
	    }
	}

	Chunk chunk = new Chunk((World) world, chunkprimer, x, z);

	chunk.generateSkylightMap();
	return chunk;
    }

    protected boolean isDungeonChunk(long x, long z)
    {
	if (x < 0 || z < 0)
	{
	    return false; // dungeons only spawn in the +x/+z quadrant
	}

	long plotX = x % 16;
	long plotZ = z % 16;
	return plotX > 3 && plotX < 12 && plotZ > 3 && plotZ < 12;
    }

    protected boolean isEntranceChunk(long x, long z)
    {
	if (x < 0 || z < 0)
	{
	    return false; // dungeons only spawn in the +x/+z quadrant
	}

	long plotX = x % 16;
	long plotZ = z % 16;
	return plotX == 8 && plotZ == 11;
    }

    protected void generateDungeonAroundChunk(long x, long z)
    {
	MinecraftServer minecraftserver = world.getWorld().getServer();
	TemplateManager templatemanager = minecraftserver.getWorld(DimensionRegistrar.dungeon_dimension_type).getStructureTemplateManager();

	// x,z is the position of the entrance room, which is located at (4,7) in this map
	DungeonBuilderLogic dbl = new DungeonBuilderLogic(this.world.getSeed(), x, z);
	dbl.calculateDungeonShape(5);

	// debug - print map
	/*
	 * System.out.println("Making a dungeon at " + x + ", " + z + "!"); for ( int j = 0; j < 8; j++ ) { String
	 * dungeonRowShape = ""; for ( int i = 0; i < 8; i++ ) { dungeonRowShape += dbl.finalLayout[i][j].hasRoom() ? "*" : ".";
	 * } System.out.println(dungeonRowShape); }
	 */

	// for each structure, put them onto the map
	for (int i = 0; i < 8; i++)
	{
	    for (int j = 0; j < 8; j++)
	    {
		DungeonRoom nextRoom = dbl.finalLayout[i][j];
		if (!nextRoom.hasRoom())
		{
		    continue; // do nothing for blank chunks. Not every cell in the 7x7 will be filled
		}

		// get the position of the top left corner of the corner in block coordinates
		ChunkPos chunkpos = new ChunkPos((int) x + i, (int) z + j);
		BlockPos position = new BlockPos(chunkpos.getXStart(), 50, chunkpos.getZStart());

		// default placement settings
		Template template = templatemanager.getTemplate(new ResourceLocation(DimDungeons.RESOURCE_PREFIX + nextRoom.structure));
		PlacementSettings placementsettings = (new PlacementSettings()).setMirror(Mirror.NONE).setRotation(Rotation.NONE).setIgnoreEntities(false).setChunk((ChunkPos) null).setReplacedBlock((Block) null).setIgnoreStructureBlock(false);

		// next if the structure is to be rotated then it must also be offset, because rotating a structure also moves it
		if (nextRoom.rotation == Rotation.COUNTERCLOCKWISE_90)
		{
		    // west: rotate CCW and push +Z
		    placementsettings.setRotation(Rotation.COUNTERCLOCKWISE_90);
		    position = position.add(0, 0, template.getSize().getZ() - 1);
		    //System.out.println("template placement CCW: " + position.toString() + " " + nextRoom.structure);
		    template.addBlocksToWorld(world, position, placementsettings);
		}
		else if (nextRoom.rotation == Rotation.CLOCKWISE_90)
		{
		    // east rotate CW and push +X
		    placementsettings.setRotation(Rotation.CLOCKWISE_90);
		    position = position.add(template.getSize().getX() - 1, 0, 0);
		    //System.out.println("template placement CW: " + position.toString() + " " + nextRoom.structure);
		    template.addBlocksToWorld(world, position, placementsettings);
		}
		else if (nextRoom.rotation == Rotation.CLOCKWISE_180)
		{
		    // south: rotate 180 and push both +X and +Z
		    placementsettings.setRotation(Rotation.CLOCKWISE_180);
		    position = position.add(template.getSize().getX() - 1, 0, template.getSize().getZ() - 1);
		    //System.out.println("template placement 180: " + position.toString() + " " + nextRoom.structure);
		    template.addBlocksToWorld(world, position, placementsettings);
		}
		else //if (nextRoom.rotation == Rotation.NONE)
		{
		    // north: no rotation
		    placementsettings.setRotation(Rotation.NONE);
		    //System.out.println("template placement NONE: " + position.toString() + " " + nextRoom.structure);
		    template.addBlocksToWorld(world, position, placementsettings);
		}

		// handle data blocks
		Map<BlockPos, String> map = template.getDataBlocks(position, placementsettings);
		for (Entry<BlockPos, String> entry : map.entrySet())
		{
		    if ("LockIt".equals(entry.getValue()))
		    {
			BlockPos blockpos2 = entry.getKey();
			LockDispensersAround(this.world, blockpos2);
			world.setBlockState(blockpos2, Blocks.AIR.getDefaultState(), 3); // erase this data block 
		    }
		    else if ("LockItStoneBrick".equals(entry.getValue()))
		    {
			BlockPos blockpos2 = entry.getKey();
			LockDispensersAround(this.world, blockpos2);
			world.setBlockState(blockpos2, Blocks.STONE_BRICKS.getDefaultState(), 3); // erase this data block 
		    }
		    else if ("ReturnPortal".equals(entry.getValue()))
		    {
			BlockPos blockpos2 = entry.getKey();
			LockDispensersAround(this.world, blockpos2);
			world.setBlockState(blockpos2, BlockRegistrar.block_gold_portal.getDefaultState(), 3); // erase this data block 
		    }
		    else if ("FortuneTeller".equals(entry.getValue()))
		    {
			BlockPos blockpos2 = entry.getKey();
			world.setBlockState(blockpos2, Blocks.DISPENSER.getDefaultState(), 3); // erase this data block 
			faceContainerTowardsAir(world, blockpos2);
			LockDispensersAround(this.world, blockpos2.up());

			// put a message inside the dispenser
			TileEntityDispenser te = (TileEntityDispenser) world.getTileEntity(blockpos2);
			if (te != null)
			{
			    te.clear();
			    ItemStack message = generateLuckyMessage();
			    te.addItemStack(message);
			}
		    }
		    else if ("ChestLoot1".equals(entry.getValue()))
		    {
			// 80% loot_1, 20% loot_2
			int lucky = rand.nextInt(100);
			if (lucky < 80)
			{
			    putChestHere(entry.getKey(), new ResourceLocation(DimDungeons.RESOURCE_PREFIX + "chests/chestloot_1"));
			}
			else
			{
			    putChestHere(entry.getKey(), new ResourceLocation(DimDungeons.RESOURCE_PREFIX + "chests/chestloot_2"));
			}
		    }
		    else if ("ChestLoot2".equals(entry.getValue()))
		    {
			// 50% loot_2, 50% loot_1
			int lucky = rand.nextInt(100);
			if (lucky < 50)
			{
			    putChestHere(entry.getKey(), new ResourceLocation(DimDungeons.RESOURCE_PREFIX + "chests/chestloot_1"));
			}
			else
			{
			    putChestHere(entry.getKey(), new ResourceLocation(DimDungeons.RESOURCE_PREFIX + "chests/chestloot_2"));
			}
		    }
		    else if ("ChestLootLucky".equals(entry.getValue()))
		    {
			// 70% nothing, 30% random minecraft loot table that isn't an end city
			int lucky = rand.nextInt(100);
			if (lucky < 30)
			{
			    putChestHere(entry.getKey(), new ResourceLocation(DimDungeons.RESOURCE_PREFIX + "chests/chestloot_lucky"));
			}
			else
			{
			    world.setBlockState(entry.getKey(), Blocks.AIR.getDefaultState(), 3); // erase this data block 
			}
		    }
		    else if ("SetTrappedLoot".equals(entry.getValue()))
		    {
			BlockPos blockpos2 = entry.getKey();
			world.setBlockState(blockpos2, Blocks.AIR.getDefaultState(), 3); // erase this data block
			TileEntityChest te = (TileEntityChest) world.getTileEntity(blockpos2.down());
			if (te != null)
			{
			    te.clear();
			    te.setLootTable(new ResourceLocation(DimDungeons.RESOURCE_PREFIX + "chests/chestloot_1"), rand.nextLong());
			}
		    }
		    else if ("SummonWitch".equals(entry.getValue()))
		    {
			BlockPos blockpos2 = entry.getKey();
			world.setBlockState(blockpos2, Blocks.AIR.getDefaultState(), 3); // erase this data block
			spawnEnemyHere(blockpos2, "witch");
		    }
		    else if ("SummonWaterEnemy".equals(entry.getValue()))
		    {
			BlockPos blockpos2 = entry.getKey();
			world.setBlockState(blockpos2, Blocks.AIR.getDefaultState(), 3); // erase this data block
			int chance = rand.nextInt(100);
			if (chance < 80)
			{
			    spawnEnemyHere(blockpos2, "guardian");
			}
			else
			{
			    spawnEnemyHere(blockpos2, "drowned");
			}
		    }
		    else if ("SummonEnderman".equals(entry.getValue()))
		    {
			BlockPos blockpos2 = entry.getKey();
			world.setBlockState(blockpos2, Blocks.AIR.getDefaultState(), 3); // erase this data block
			spawnEnemyHere(blockpos2, "enderman");
		    }
		    else if ("SummonEnemy1".equals(entry.getValue()))
		    {
			// 50% chance of a weak enemy
			BlockPos blockpos2 = entry.getKey();
			world.setBlockState(blockpos2, Blocks.AIR.getDefaultState(), 3); // erase this data block
			int chance = rand.nextInt(100);
			if (chance < 50)
			{
			    switch (dbl.enemyVariation1)
			    {
			    case 0:
				spawnEnemyHere(blockpos2, "zombie");
				break;
			    case 1:
				spawnEnemyHere(blockpos2, "husk");
				break;
			    default:
				spawnEnemyHere(blockpos2, "drowned");
				break;
			    }
			}
		    }
		    else if ("SummonEnemy2".equals(entry.getValue()))
		    {
			// 80% chance of a strong enemy
			BlockPos blockpos2 = entry.getKey();
			world.setBlockState(blockpos2, Blocks.AIR.getDefaultState(), 3); // erase this data block
			int chance = rand.nextInt(100);
			if (chance < 80)
			{
			    switch (dbl.enemyVariation1)
			    {
			    case 0:
				spawnEnemyHere(blockpos2, "skeleton");
				break;
			    case 1:
				spawnEnemyHere(blockpos2, "wither_skeleton");
				break;
			    default:
				spawnEnemyHere(blockpos2, "stray");
				break;
			    }
			}
		    }
		}
	    }
	}
    }

    private void spawnEnemyHere(BlockPos pos, String casualName)
    {
	EntityMob mob = null;

	if ("witch".contentEquals(casualName))
	{
	    mob = new EntityWitch((World) world);
	    mob.setPosition(pos.getX(), pos.getY() + 1, pos.getZ());
	}
	else if ("enderman".contentEquals(casualName))
	{
	    mob = new EntityEnderman((World) world);
	    mob.setPosition(pos.getX(), pos.getY() + 2, pos.getZ());
	}
	else if ("guardian".contentEquals(casualName))
	{
	    mob = new EntityGuardian((World) world);
	    mob.setPosition(pos.getX(), pos.getY() + 1, pos.getZ());
	}
	else if ("zombie".contentEquals(casualName))
	{
	    mob = new EntityZombie((World) world);
	    mob.setPosition(pos.getX(), pos.getY() + 1, pos.getZ());
	}
	else if ("husk".contentEquals(casualName))
	{
	    mob = new EntityHusk((World) world);
	    mob.setPosition(pos.getX(), pos.getY() + 1, pos.getZ());
	}
	else if ("drowned".contentEquals(casualName))
	{
	    mob = new EntityDrowned((World) world);
	    mob.setPosition(pos.getX(), pos.getY() + 1, pos.getZ());
	}
	else if ("skeleton".contentEquals(casualName))
	{
	    mob = new EntitySkeleton((World) world);
	    mob.setPosition(pos.getX(), pos.getY() + 1, pos.getZ());
	}
	else if ("wither_skeleton".contentEquals(casualName))
	{
	    mob = new EntityWitherSkeleton((World) world);
	    mob.setPosition(pos.getX(), pos.getY() + 1, pos.getZ());
	}
	else if ("stray".contentEquals(casualName))
	{
	    mob = new EntityStray((World) world);
	    mob.setPosition(pos.getX(), pos.getY() + 1, pos.getZ());
	}
	else
	{
	    System.out.println("DungeonChunkGenerator: Attempting to spawn unrecognized enemy: " + casualName);
	    return;
	}

	mob.setCanPickUpLoot(false);
	mob.setCustomName(new TextComponentString(I18n.format("enemy.dimdungeons:" + casualName)));
	mob.setHomePosAndDistance(pos, 16);
	mob.enablePersistence();
	world.spawnEntity(mob);
    }

    private void putChestHere(BlockPos pos, ResourceLocation lootTable)
    {
	world.setBlockState(pos, Blocks.CHEST.getDefaultState(), 3);
	//faceContainerTowardsAir(world, pos);

	// set the loot table
	TileEntityChest te = (TileEntityChest) world.getTileEntity(pos);
	if (te != null)
	{
	    te.clear();
	    te.setLootTable(lootTable, rand.nextLong());
	}
    }

    // I was originally thinking that this would contain direct hints about the dungeon, but that would involve a post generation step
    private ItemStack generateLuckyMessage()
    {
	ItemStack stack = new ItemStack(Items.WRITTEN_BOOK);
	stack.setTag(new NBTTagCompound());

	// randomize book contents
	int bookType = rand.nextInt(3);
	int messageVariation = rand.nextInt(8) + 1;
	String body = "";
	String title = "";

	if (bookType == 0)
	{
	    title = I18n.format("book.myfirstmod:title_1");
	    body = I18n.format("book.myfirstmod:fun_message_" + messageVariation);
	}
	else if (bookType == 1)
	{
	    title = I18n.format("book.myfirstmod:title_2");
	    body = I18n.format("book.myfirstmod:helpful_message_" + messageVariation);
	}
	else
	{
	    title = I18n.format("book.myfirstmod:title_3");
	    body = I18n.format("book.myfirstmod:dangerous_message_" + messageVariation);
	}

	// create the complicated NBT tag list for the list of pages in the book
	NBTTagList pages = new NBTTagList();
	ITextComponent text = new TextComponentString(body);
	String json = ITextComponent.Serializer.toJson(text);
	//pages.appendTag(new NBTTagString(json)); // 1.12
	pages.add(0, new NBTTagString(json)); // 1.13

	// actually set all the bookish NBT on the item
	stack.getTag().setBoolean("resolved", false);
	stack.getTag().setInt("generation", 0);
	stack.getTag().setTag("pages", pages);
	stack.getTag().setString("title", title);
	stack.getTag().setString("author", I18n.format("book.myfirstmod:author"));

	return stack;
    }

    private void LockDispensersAround(IWorld world, BlockPos pos)
    {
	Random r = new Random((world.getSeed() + (long) (pos.getX() * pos.getX() * 4987142) + (long) (pos.getX() * 5947611) + (long) (pos.getZ() * pos.getZ()) * 4392871L + (long) (pos.getZ() * 389711) ^ world.getSeed()));

	// make sure the player cannot be holding an item with this name
	LockCode code = new LockCode("ThisIsIntentionallyLongerThanCanNormallyBePossiblePlus" + r.nextLong());

	if (world.getBlockState(pos.up()).getBlock() == Blocks.DISPENSER)
	{
	    ((TileEntityDispenser) world.getTileEntity(pos.up())).setLockCode(code);
	}
	if (world.getBlockState(pos.down()).getBlock() == Blocks.DISPENSER)
	{
	    ((TileEntityDispenser) world.getTileEntity(pos.down())).setLockCode(code);
	}
	if (world.getBlockState(pos.north()).getBlock() == Blocks.DISPENSER)
	{
	    ((TileEntityDispenser) world.getTileEntity(pos.north())).setLockCode(code);
	}
	if (world.getBlockState(pos.south()).getBlock() == Blocks.DISPENSER)
	{
	    ((TileEntityDispenser) world.getTileEntity(pos.south())).setLockCode(code);
	}
	if (world.getBlockState(pos.west()).getBlock() == Blocks.DISPENSER)
	{
	    ((TileEntityDispenser) world.getTileEntity(pos.west())).setLockCode(code);
	}
	if (world.getBlockState(pos.east()).getBlock() == Blocks.DISPENSER)
	{
	    ((TileEntityDispenser) world.getTileEntity(pos.east())).setLockCode(code);
	}
    }

    // used on dispensers and chests, particularly ones created by data blocks
    private void faceContainerTowardsAir(IWorld world, BlockPos pos)
    {
	IBlockState bs = world.getBlockState(pos);

	if (bs.getBlock() == Blocks.DISPENSER || bs.getBlock() == Blocks.CHEST)
	{
	    if (world.getBlockState(pos.north()).getBlock() == Blocks.AIR)
	    {
		bs.with(BlockDispenser.FACING, EnumFacing.NORTH);
	    }
	    if (world.getBlockState(pos.south()).getBlock() == Blocks.AIR)
	    {
		bs.with(BlockDispenser.FACING, EnumFacing.SOUTH);
	    }
	    if (world.getBlockState(pos.west()).getBlock() == Blocks.AIR)
	    {
		bs.with(BlockDispenser.FACING, EnumFacing.WEST);
	    }
	    if (world.getBlockState(pos.east()).getBlock() == Blocks.AIR)
	    {
		bs.with(BlockDispenser.FACING, EnumFacing.EAST);
	    }
	    world.setBlockState(pos, bs, 3);
	}
    }

    public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos)
    {
	Biome biome = this.world.getBiome(pos);
	return biome.getSpawns(creatureType);
    }

    @Override
    public void makeBase(IChunk chunkIn)
    {
	// generate (technically useless) sandstone base for dungeon
	generateChunk(chunkIn.getPos().x, chunkIn.getPos().z);
    }

    @Override
    public void carve(WorldGenRegion region, Carving carvingStage)
    {
	// intentionally do nothing
    }
    
    @Override
    protected void makeBedrock(IChunk chunkIn, Random random)
    {
	// intentionally do nothing
    }

    @Override
    public void decorate(WorldGenRegion region)
    {
	long x = region.getMainChunkX();
	long z = region.getMainChunkZ();
	
	//System.out.println("Populating Dungeon Chunk: " + x + ", " + z);

	BlockFalling.fallInstantly = true;
	this.rand.setSeed(this.world.getSeed());
	long k = this.rand.nextLong() / 2L * 2L + 1L;
	long l = this.rand.nextLong() / 2L * 2L + 1L;
	this.rand.setSeed((long) x * k + (long) z * l ^ this.world.getSeed());

	// if this is an entrance chunk then generate a dungeon here
	if (isEntranceChunk(x, z))
	{
	    DimDungeons.LOGGER.info("DIM DUNGEONS: Putting a dungeon at " + x + ", " + z);
	    generateDungeonAroundChunk(x - 4, z - 7); // the topleft corner of this 8x8 chunk area
	}

	BlockFalling.fallInstantly = false;	
    }

    @Override
    public void spawnMobs(WorldGenRegion region)
    {
	// intentionally do nothing
    }

    @Override
    public BlockPos findNearestStructure(World worldIn, String name, BlockPos pos, int radius, boolean p_211403_5_)
    {
	// intentionally do nothing
	return null;
    }

    @Override
    public IChunkGenSettings getSettings()
    {
	return null;
    }

    @Override
    public int spawnMobs(World worldIn, boolean spawnHostileMobs, boolean spawnPeacefulMobs)
    {
	return 0;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean hasStructure(Biome biomeIn, Structure structureIn)
    {
	return false;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public IFeatureConfig getStructureConfig(Biome biomeIn, Structure structureIn)
    {
	return null;
    }

    @Override
    public BiomeProvider getBiomeProvider()
    {
	return new BiomeProviderDungeon();
    }

    @Override
    public long getSeed()
    {
	return seed;
    }

    @Override
    public int getGroundHeight()
    {
	return 50;
    }

    @Override
    public int getMaxHeight()
    {
	return 255;
    }

    @Override
    public double[] generateNoiseRegion(int x, int z)
    {
	return null;
    }
}