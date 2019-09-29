package com.catastrophe573.dimdungeons.feature;

import java.util.List;
import java.util.Random;
import java.util.function.Function;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.block.BlockRegistrar;
import com.catastrophe573.dimdungeons.dimension.DungeonDimensionType;
import com.catastrophe573.dimdungeons.structure.DungeonBuilderLogic;
import com.catastrophe573.dimdungeons.structure.DungeonBuilderLogic.DungeonRoom;
import com.mojang.datafixers.Dynamic;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.DrownedEntity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.monster.GuardianEntity;
import net.minecraft.entity.monster.HuskEntity;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.entity.monster.StrayEntity;
import net.minecraft.entity.monster.WitchEntity;
import net.minecraft.entity.monster.WitherSkeletonEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.DispenserTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.LockCode;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.gen.feature.template.Template.BlockInfo;

public class BasicDungeonFeature extends Feature<NoFeatureConfig>
{
    public static String FEATURE_ID = "feature_basic_dungeon";

    public BasicDungeonFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> function)
    {
	super(function);
    }

    @Override
    public boolean place(IWorld world, ChunkGenerator<? extends GenerationSettings> chunkGenerator, Random rand, BlockPos pos, NoFeatureConfig config)
    {
	// only put dungeons on the right chunks, and only in the dungeon dimension
	if ( world.getDimension().getType() != DungeonDimensionType.getDimensionType() )
	{
	    DimDungeons.LOGGER.info("DIMDUNGEONS WEIRD ERROR: why is there a dungeon biome outside of the dungeon dimension?");
	    return false;
	}	
	ChunkPos cpos = new ChunkPos(pos);
	if ( isEntranceChunk(cpos.x, cpos.z) )
	{
	    DimDungeons.LOGGER.info("MyFeature: PLACING DUNGEON FEATURE " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ());
	    //world.setBlockState(pos.add(0, 150, 0), Blocks.MAGENTA_GLAZED_TERRACOTTA.getDefaultState(), 2);
	    generateDungeonAroundChunk(cpos.x - 4, cpos.z - 7, world, rand);
	    
	    return true;
	}
	return false;
    }

    // also used to by the DungeonChunkGenerator, note that the dimension check is not done here
    public static boolean isDungeonChunk(long x, long z)
    {
	if (x < 0 || z < 0)
	{
	    return false; // dungeons only spawn in the +x/+z quadrant
	}

	long plotX = x % 16;
	long plotZ = z % 16;
	return plotX > 3 && plotX < 12 && plotZ > 3 && plotZ < 12;
    }

    // also used to by the DungeonChunkGenerator, note that the dimension check is not done here
    public static boolean isEntranceChunk(long x, long z)
    {
	if (x < 0 || z < 0)
	{
	    return false; // dungeons only spawn in the +x/+z quadrant
	}

	long plotX = x % 16;
	long plotZ = z % 16;
	return plotX == 8 && plotZ == 11;
    }
    
    public void generateDungeonAroundChunk(long x, long z, IWorld world, Random rand)
    {
	MinecraftServer minecraftserver = world.getWorld().getServer();
	TemplateManager templatemanager = minecraftserver.getWorld(world.getDimension().getType()).getStructureTemplateManager();

	// x,z is the position of the entrance room, which is located at (4,7) in this map
	DungeonBuilderLogic dbl = new DungeonBuilderLogic(world.getSeed(), x, z);
	dbl.calculateDungeonShape(5);

	// debug - print map
	//*
	System.out.println("Making a dungeon at " + x + ", " + z + "!");
	for (int j = 0; j < 8; j++)
	{
	    String dungeonRowShape = "";
	    for (int i = 0; i < 8; i++)
	    {
		dungeonRowShape += dbl.finalLayout[i][j].hasRoom() ? "*" : ".";
	    }
	    System.out.println(dungeonRowShape);
	}
	//*/

	// for each structure, put them onto the map
	for (int i = 0; i < 8; i++)
	{
	    for (int j = 0; j < 8; j++)
	    {
		DungeonRoom nextRoom = dbl.finalLayout[i][j];
		if (!nextRoom.hasRoom())
		{
		    System.out.println("NO ROOM AT " + i + ", " + j + "!");
		    continue; // do nothing for blank chunks. Not every cell in the 7x7 will be filled
		}

		// get the position of the top left corner of the corner in block coordinates
		ChunkPos chunkpos = new ChunkPos((int) x + i, (int) z + j);
		BlockPos position = new BlockPos(chunkpos.getXStart(), 50, chunkpos.getZStart());

		// default placement settings
		Template template = templatemanager.getTemplate(new ResourceLocation(DimDungeons.RESOURCE_PREFIX + nextRoom.structure));
		PlacementSettings placementsettings = (new PlacementSettings()).setMirror(Mirror.NONE).setRotation(Rotation.NONE).setIgnoreEntities(false).setChunk(chunkpos);
		placementsettings.setBoundingBox(placementsettings.getBoundingBox());

		// next if the structure is to be rotated then it must also be offset, because rotating a structure also moves it
		if (nextRoom.rotation == Rotation.COUNTERCLOCKWISE_90)
		{
		    // west: rotate CCW and push +Z
		    placementsettings.setRotation(Rotation.COUNTERCLOCKWISE_90);
		    position = position.add(0, 0, template.getSize().getZ() - 1);
		    System.out.println("template placement CCW: " + position.toString() + " " + nextRoom.structure);
		    template.addBlocksToWorld(world, position, placementsettings);
		}
		else if (nextRoom.rotation == Rotation.CLOCKWISE_90)
		{
		    // east rotate CW and push +X
		    placementsettings.setRotation(Rotation.CLOCKWISE_90);
		    position = position.add(template.getSize().getX() - 1, 0, 0);
		    System.out.println("template placement CW: " + position.toString() + " " + nextRoom.structure);
		    template.addBlocksToWorld(world, position, placementsettings);
		}
		else if (nextRoom.rotation == Rotation.CLOCKWISE_180)
		{
		    // south: rotate 180 and push both +X and +Z
		    placementsettings.setRotation(Rotation.CLOCKWISE_180);
		    position = position.add(template.getSize().getX() - 1, 0, template.getSize().getZ() - 1);
		    System.out.println("template placement 180: " + position.toString() + " " + nextRoom.structure);
		    template.addBlocksToWorld(world, position, placementsettings);
		}
		else //if (nextRoom.rotation == Rotation.NONE)
		{
		    // north: no rotation
		    placementsettings.setRotation(Rotation.NONE);
		    System.out.println("template placement NONE: " + position.toString() + " " + nextRoom.structure);
		    template.addBlocksToWorld(world, position, placementsettings);
		}

		// handle data blocks
		DimDungeons.LOGGER.info("DIMDUNGEONS ABOUT TO START DATA BLOCKS");
		//Map<BlockPos, String> map = template.getDataBlocks(position, placementsettings); // 1.12 / 1.13 version
		List<Template.BlockInfo> dblocks = template.func_215386_a(position, placementsettings, Blocks.STRUCTURE_BLOCK, true);

		//for (Entry<BlockPos, String> entry : map.entrySet())
		for (int blockIndex = 0; blockIndex < dblocks.size(); blockIndex++)
		{
		    BlockPos blockpos2 = dblocks.get(blockIndex).pos;
		    String name = dblocks.get(blockIndex).nbt.getString("name");

		    if (name == null)
		    {
			DimDungeons.LOGGER.info("DUNGEON CHUNK GENERATOR - NOT A DATA BLOCK? " + dblocks.get(blockIndex).nbt.toString());
			continue;
		    }

		    if ("LockIt".equals(name))
		    {
			LockDispensersAround(world, blockpos2);
			world.setBlockState(blockpos2, Blocks.AIR.getDefaultState(), 3); // erase this data block 
		    }
		    else if ("LockItStoneBrick".equals(name))
		    {
			LockDispensersAround(world, blockpos2);
			world.setBlockState(blockpos2, Blocks.STONE_BRICKS.getDefaultState(), 3); // erase this data block 
		    }
		    else if ("ReturnPortal".equals(name))
		    {
			LockDispensersAround(world, blockpos2);
			world.setBlockState(blockpos2, BlockRegistrar.block_gold_portal.getDefaultState(), 3); // erase this data block 
		    }
		    else if ("FortuneTeller".equals(name))
		    {
			world.setBlockState(blockpos2, Blocks.DISPENSER.getDefaultState(), 3); // erase this data block 
			faceContainerTowardsAir(world, blockpos2);
			LockDispensersAround(world, blockpos2.up());

			// put a message inside the dispenser
			DispenserTileEntity te = (DispenserTileEntity) world.getTileEntity(blockpos2);
			if (te != null)
			{
			    te.clear();
			    ItemStack message = generateLuckyMessage(rand);
			    te.addItemStack(message);
			}
		    }
		    else if ("ChestLoot1".equals(name))
		    {
			// 80% loot_1, 20% loot_2
			int lucky = rand.nextInt(100);
			if (lucky < 80)
			{
			    putChestHere(blockpos2, new ResourceLocation(DimDungeons.RESOURCE_PREFIX + "chests/chestloot_1"), world, rand);
			}
			else
			{
			    putChestHere(blockpos2, new ResourceLocation(DimDungeons.RESOURCE_PREFIX + "chests/chestloot_2"), world, rand);
			}
		    }
		    else if ("ChestLoot2".equals(name))
		    {
			// 50% loot_2, 50% loot_1
			int lucky = rand.nextInt(100);
			if (lucky < 50)
			{
			    putChestHere(blockpos2, new ResourceLocation(DimDungeons.RESOURCE_PREFIX + "chests/chestloot_1"), world, rand);
			}
			else
			{
			    putChestHere(blockpos2, new ResourceLocation(DimDungeons.RESOURCE_PREFIX + "chests/chestloot_2"), world, rand);
			}
		    }
		    else if ("ChestLootLucky".equals(name))
		    {
			// 70% nothing, 30% random minecraft loot table that isn't an end city
			int lucky = rand.nextInt(100);
			if (lucky < 30)
			{
			    putChestHere(blockpos2, new ResourceLocation(DimDungeons.RESOURCE_PREFIX + "chests/chestloot_lucky"), world, rand);
			}
			else
			{
			    world.setBlockState(blockpos2, Blocks.AIR.getDefaultState(), 3); // erase this data block 
			}
		    }
		    else if ("SetTrappedLoot".equals(name))
		    {
			world.setBlockState(blockpos2, Blocks.AIR.getDefaultState(), 3); // erase this data block
			ChestTileEntity te = (ChestTileEntity) world.getTileEntity(blockpos2.down());
			if (te != null)
			{
			    te.clear();
			    te.setLootTable(new ResourceLocation(DimDungeons.RESOURCE_PREFIX + "chests/chestloot_1"), rand.nextLong());
			}
		    }
		    else if ("SummonWitch".equals(name))
		    {
			world.setBlockState(blockpos2, Blocks.AIR.getDefaultState(), 3); // erase this data block
			spawnEnemyHere(blockpos2, "witch", world);
		    }
		    else if ("SummonWaterEnemy".equals(name))
		    {
			world.setBlockState(blockpos2, Blocks.AIR.getDefaultState(), 3); // erase this data block
			int chance = rand.nextInt(100);
			if (chance < 80)
			{
			    spawnEnemyHere(blockpos2, "guardian", world);
			}
			else
			{
			    spawnEnemyHere(blockpos2, "drowned", world);
			}
		    }
		    else if ("SummonEnderman".equals(name))
		    {
			world.setBlockState(blockpos2, Blocks.AIR.getDefaultState(), 3); // erase this data block
			spawnEnemyHere(blockpos2, "enderman", world);
		    }
		    else if ("SummonEnemy1".equals(name))
		    {
			// 50% chance of a weak enemy
			world.setBlockState(blockpos2, Blocks.AIR.getDefaultState(), 3); // erase this data block
			int chance = rand.nextInt(100);
			if (chance < 50)
			{
			    switch (dbl.enemyVariation1)
			    {
			    case 0:
				spawnEnemyHere(blockpos2, "zombie", world);
				break;
			    case 1:
				spawnEnemyHere(blockpos2, "husk", world);
				break;
			    default:
				spawnEnemyHere(blockpos2, "drowned", world);
				break;
			    }
			}
		    }
		    else if ("SummonEnemy2".equals(name))
		    {
			// 80% chance of a strong enemy
			world.setBlockState(blockpos2, Blocks.AIR.getDefaultState(), 3); // erase this data block
			int chance = rand.nextInt(100);
			if (chance < 80)
			{
			    switch (dbl.enemyVariation1)
			    {
			    case 0:
				spawnEnemyHere(blockpos2, "skeleton", world);
				break;
			    case 1:
				spawnEnemyHere(blockpos2, "wither_skeleton", world);
				break;
			    default:
				spawnEnemyHere(blockpos2, "stray", world);
				break;
			    }
			}
		    }
		}
	    }
	}
	DimDungeons.LOGGER.info("DIMDUNGEONS FINISHED BUILDING DUNGEON");
    }

    private void spawnEnemyHere(BlockPos pos, String casualName, IWorld world)
    {
	MobEntity mob = null;

	if ("witch".contentEquals(casualName))
	{
	    mob = new WitchEntity(null, (World) world);
	    mob.setPosition(pos.getX(), pos.getY() + 1, pos.getZ());
	}
	else if ("enderman".contentEquals(casualName))
	{
	    mob = new EndermanEntity(null, (World) world);
	    mob.setPosition(pos.getX(), pos.getY() + 2, pos.getZ());
	}
	else if ("guardian".contentEquals(casualName))
	{
	    mob = new GuardianEntity(null, (World) world);
	    mob.setPosition(pos.getX(), pos.getY() + 1, pos.getZ());
	}
	else if ("zombie".contentEquals(casualName))
	{
	    mob = new ZombieEntity((World) world);
	    mob.setPosition(pos.getX(), pos.getY() + 1, pos.getZ());
	}
	else if ("husk".contentEquals(casualName))
	{
	    mob = new HuskEntity(null, (World) world);
	    mob.setPosition(pos.getX(), pos.getY() + 1, pos.getZ());
	}
	else if ("drowned".contentEquals(casualName))
	{
	    mob = new DrownedEntity(null, (World) world);
	    mob.setPosition(pos.getX(), pos.getY() + 1, pos.getZ());
	}
	else if ("skeleton".contentEquals(casualName))
	{
	    mob = new SkeletonEntity(null, (World) world);
	    mob.setPosition(pos.getX(), pos.getY() + 1, pos.getZ());
	}
	else if ("wither_skeleton".contentEquals(casualName))
	{
	    mob = new WitherSkeletonEntity(null, (World) world);
	    mob.setPosition(pos.getX(), pos.getY() + 1, pos.getZ());
	}
	else if ("stray".contentEquals(casualName))
	{
	    mob = new StrayEntity(null, (World) world);
	    mob.setPosition(pos.getX(), pos.getY() + 1, pos.getZ());
	}
	else
	{
	    System.out.println("DungeonChunkGenerator: Attempting to spawn unrecognized enemy: " + casualName);
	    return;
	}

	mob.setCanPickUpLoot(false);
	mob.setCustomName(new StringTextComponent(I18n.format("enemy.dimdungeons:" + casualName)));
	mob.setHomePosAndDistance(pos, 16);
	mob.enablePersistence();
	world.addEntity(mob);
    }

    private void putChestHere(BlockPos pos, ResourceLocation lootTable, IWorld world, Random rand)
    {
	world.setBlockState(pos, Blocks.CHEST.getDefaultState(), 3);
	//faceContainerTowardsAir(world, pos);

	// set the loot table
	ChestTileEntity te = (ChestTileEntity) world.getTileEntity(pos);
	if (te != null)
	{
	    te.clear();
	    te.setLootTable(lootTable, rand.nextLong());
	}
    }

    // I was originally thinking that this would contain direct hints about the dungeon, but that would involve a post generation step
    private ItemStack generateLuckyMessage(Random rand)
    {
	ItemStack stack = new ItemStack(Items.WRITTEN_BOOK);
	stack.setTag(new CompoundNBT());

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
	ListNBT pages = new ListNBT();
	ITextComponent text = new StringTextComponent(body);
	String json = ITextComponent.Serializer.toJson(text);
	//pages.appendTag(new NBTTagString(json)); // 1.12
	//pages.add(0, new NBTTagString(json)); // 1.13
	pages.add(0, new StringNBT(json)); // 1.14

	// actually set all the bookish NBT on the item
	stack.getTag().putBoolean("resolved", false);
	stack.getTag().putInt("generation", 0);
	stack.getTag().put("pages", pages);
	stack.getTag().putString("title", title);
	stack.getTag().putString("author", I18n.format("book.myfirstmod:author"));

	return stack;
    }

    private void LockDispensersAround(IWorld world, BlockPos pos)
    {
	Random r = new Random((world.getSeed() + (long) (pos.getX() * pos.getX() * 4987142) + (long) (pos.getX() * 5947611) + (long) (pos.getZ() * pos.getZ()) * 4392871L + (long) (pos.getZ() * 389711) ^ world.getSeed()));

	// make sure the player cannot be holding an item with this name
	LockCode code = new LockCode("ThisIsIntentionallyLongerThanCanNormallyBePossiblePlus" + r.nextLong());

	if (world.getBlockState(pos.up()).getBlock() == Blocks.DISPENSER)
	{
	    //((DispenserTileEntity) world.getTileEntity(pos.up())).setLockCode(code);
	}
	if (world.getBlockState(pos.down()).getBlock() == Blocks.DISPENSER)
	{
	    //((DispenserTileEntity) world.getTileEntity(pos.down())).setLockCode(code);
	}
	if (world.getBlockState(pos.north()).getBlock() == Blocks.DISPENSER)
	{
	    //((DispenserTileEntity) world.getTileEntity(pos.north())).setLockCode(code);
	}
	if (world.getBlockState(pos.south()).getBlock() == Blocks.DISPENSER)
	{
	    //((DispenserTileEntity) world.getTileEntity(pos.south())).setLockCode(code);
	}
	if (world.getBlockState(pos.west()).getBlock() == Blocks.DISPENSER)
	{
	    //((DispenserTileEntity) world.getTileEntity(pos.west())).setLockCode(code);
	}
	if (world.getBlockState(pos.east()).getBlock() == Blocks.DISPENSER)
	{
	    //((DispenserTileEntity) world.getTileEntity(pos.east())).setLockCode(code);
	}
    }

    // used on dispensers and chests, particularly ones created by data blocks
    private void faceContainerTowardsAir(IWorld world, BlockPos pos)
    {
	BlockState bs = world.getBlockState(pos);

	if (bs.getBlock() == Blocks.DISPENSER || bs.getBlock() == Blocks.CHEST)
	{
	    if (world.getBlockState(pos.north()).getBlock() == Blocks.AIR)
	    {
		bs.with(DispenserBlock.FACING, Direction.NORTH);
	    }
	    if (world.getBlockState(pos.south()).getBlock() == Blocks.AIR)
	    {
		bs.with(DispenserBlock.FACING, Direction.SOUTH);
	    }
	    if (world.getBlockState(pos.west()).getBlock() == Blocks.AIR)
	    {
		bs.with(DispenserBlock.FACING, Direction.WEST);
	    }
	    if (world.getBlockState(pos.east()).getBlock() == Blocks.AIR)
	    {
		bs.with(DispenserBlock.FACING, Direction.EAST);
	    }
	    world.setBlockState(pos, bs, 3);
	}
    }    
}