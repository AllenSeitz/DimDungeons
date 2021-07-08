package com.catastrophe573.dimdungeons.structure;

import java.util.Random;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.DungeonConfig;
import com.catastrophe573.dimdungeons.block.BlockRegistrar;
import com.catastrophe573.dimdungeons.block.TileEntityGoldPortal;
import com.catastrophe573.dimdungeons.block.TileEntityLocalTeleporter;
import com.catastrophe573.dimdungeons.block.TileEntityPortalKeyhole;
import com.catastrophe573.dimdungeons.item.ItemPortalKey;
import com.catastrophe573.dimdungeons.item.ItemRegistrar;
import com.catastrophe573.dimdungeons.structure.DungeonBuilderLogic.DungeonRoom;
import com.catastrophe573.dimdungeons.structure.DungeonBuilderLogic.DungeonType;
import com.catastrophe573.dimdungeons.utils.DungeonGenData;
import com.catastrophe573.dimdungeons.utils.DungeonUtils;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.state.properties.StructureMode;
import net.minecraft.tileentity.BarrelTileEntity;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.DispenserTileEntity;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.server.ServerWorld;

// temporarily, make this not a Feature, because 1.16.2 is going to break it again
public class DungeonPlacementLogicBasic
{
    public static String FEATURE_ID = "feature_basic_dungeon";

    public DungeonPlacementLogicBasic()
    {
    }

    // this is the function that actually writes the 8x8 chunk structure to the world, and ALL AT ONCE
    //public static boolean place(IChunk chunk, IWorld world, ChunkGenerator chunkGenerator, Random rand, ChunkPos cpos, NoFeatureConfig config)
    public static boolean place(ServerWorld world, long x, long z, DungeonGenData genData)
    {
	long entranceChunkX = (x / 16) + 8;
	long entranceChunkZ = (z / 16) + 11;
	if (!isEntranceChunk(entranceChunkX, entranceChunkZ))
	{
	    DimDungeons.logMessageError("DIMDUNGEONS FATAL ERROR: basic dungeon does not start at " + x + ", " + z);
	    return false;
	}
	DimDungeons.logMessageInfo("DIMDUNGEONS START BASIC STRUCTURE at " + x + ", " + z);

	// this is the data structure for an entire dungeon
	DungeonBuilderLogic dbl = new DungeonBuilderLogic(world.getRandom(), entranceChunkX, entranceChunkZ, DungeonType.BASIC, genData.dungeonTheme);
	int dungeonSize = DungeonConfig.DEFAULT_BASIC_DUNGEON_SIZE;
	if (genData.dungeonTheme > 0)
	{
	    dungeonSize = DungeonConfig.themeSettings.get(genData.dungeonTheme-1).themeDungeonSize;
	}
	dbl.calculateDungeonShape(dungeonSize, false);

	// place all 64 rooms (many will be blank), for example the entrance room is at [4][7] in this array
	for (int i = 0; i < 8; i++)
	{
	    for (int j = 0; j < 8; j++)
	    {
		DungeonRoom nextRoom = dbl.finalLayout[i][j];
		if (!nextRoom.hasRoom())
		{
		    continue;
		}
		else
		{
		    // calculate the chunkpos of the room at 0,0 in the top left of the map
		    // I'm not sure what the +4 is for, but it is needed
		    ChunkPos cpos = new ChunkPos(((int) x / 16) + i + 4, ((int) z / 16) + j + 4);

		    if (!putRoomHere(cpos, world, nextRoom, genData))
		    {
			DimDungeons.logMessageError("DIMDUNGEONS ERROR UNABLE TO PLACE STRUCTURE: " + nextRoom.structure);
		    }
		}
	    }
	}

	return true;
    }

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

    // a test/debugging function that shouldn't be used in the final version
    public static void putTestStructureHere(long x, long z, IWorld world)
    {
	ChunkPos cpos = new ChunkPos((int) x, (int) z);
	MinecraftServer minecraftserver = ((World) world).getServer();
	TemplateManager templatemanager = DungeonUtils.getDungeonWorld(minecraftserver).getStructureManager();

	Template template = templatemanager.get(new ResourceLocation(DimDungeons.RESOURCE_PREFIX + "basic_template"));
	PlacementSettings placementsettings = (new PlacementSettings()).setMirror(Mirror.NONE).setRotation(Rotation.NONE).setIgnoreEntities(false).setChunkPos(cpos);
	placementsettings.setBoundingBox(placementsettings.getBoundingBox());
	placementsettings.setRotation(Rotation.NONE);
	BlockPos position = new BlockPos(cpos.getMinBlockX(), 50, cpos.getMinBlockZ());
	BlockPos sizeRange = new BlockPos(16, 13, 16);

	// I assume this function is addBlocksToWorld()	
	template.placeInWorld((IServerWorld) world, position, sizeRange, placementsettings, world.getRandom(), 2);
    }

    // used by the place() function to actually place rooms
    public static boolean putRoomHere(ChunkPos cpos, IWorld world, DungeonRoom room, DungeonGenData genData)
    {
	MinecraftServer minecraftserver = ((World) world).getServer();
	TemplateManager templatemanager = DungeonUtils.getDungeonWorld(minecraftserver).getStructureManager();

	Template template = templatemanager.get(new ResourceLocation(room.structure));
	PlacementSettings placementsettings = (new PlacementSettings()).setMirror(Mirror.NONE).setRotation(Rotation.NONE).setIgnoreEntities(false).setChunkPos(cpos);
	placementsettings.setBoundingBox(placementsettings.getBoundingBox());

	placementsettings.setRotation(room.rotation);
	BlockPos position = new BlockPos(cpos.getMinBlockX(), 50, cpos.getMinBlockZ());
	BlockPos sizeRange = new BlockPos(16, 13, 16);

	if (template == null)
	{
	    DimDungeons.logMessageInfo("DIMDUNGEONS FATAL ERROR: Structure does not exist (" + room.structure + ")");
	    return false;
	}

	// next if the structure is to be rotated then it must also be offset, because rotating a structure also moves it
	if (room.rotation == Rotation.COUNTERCLOCKWISE_90)
	{
	    // west: rotate CCW and push +Z
	    placementsettings.setRotation(Rotation.COUNTERCLOCKWISE_90);
	    position = position.offset(0, 0, template.getSize().getZ() - 1);
	}
	else if (room.rotation == Rotation.CLOCKWISE_90)
	{
	    // east rotate CW and push +X
	    placementsettings.setRotation(Rotation.CLOCKWISE_90);
	    position = position.offset(template.getSize().getX() - 1, 0, 0);
	}
	else if (room.rotation == Rotation.CLOCKWISE_180)
	{
	    // south: rotate 180 and push both +X and +Z
	    placementsettings.setRotation(Rotation.CLOCKWISE_180);
	    position = position.offset(template.getSize().getX() - 1, 0, template.getSize().getZ() - 1);
	}
	else //if (nextRoom.rotation == Rotation.NONE)
	{
	    // north: no rotation
	    placementsettings.setRotation(Rotation.NONE);
	}

	// formerly: call Template.addBlocksToWorld()
	DimDungeons.logMessageInfo("Placing a room: " + room.structure);
	boolean success = template.placeInWorld((IServerWorld) world, position, sizeRange, placementsettings, world.getRandom(), 2);

	// handle data blocks - this code block is copied from TemplateStructurePiece
	//Map<BlockPos, String> map = template.getDataBlocks(position, placementsettings); // 1.12 / 1.13 version
	//List<Template.BlockInfo> dblocks = template.filterBlocks(position, placementsettings, Blocks.STRUCTURE_BLOCK, true); // my old 1.14.2 method
	for (Template.BlockInfo template$blockinfo : template.filterBlocks(position, placementsettings, Blocks.STRUCTURE_BLOCK))
	{
	    if (template$blockinfo.nbt != null)
	    {
		StructureMode structuremode = StructureMode.valueOf(template$blockinfo.nbt.getString("mode"));
		if (structuremode == StructureMode.DATA)
		{
		    handleDataBlock(template$blockinfo.nbt.getString("metadata"), template$blockinfo.pos, world, world.getRandom(), placementsettings.getBoundingBox(), genData);
		}
	    }
	}
	return success;
    }

    // another debugging function
    public void printMap(DungeonBuilderLogic dbl)
    {
	for (int j = 0; j < 8; j++)
	{
	    String dungeonRowShape = "";
	    for (int i = 0; i < 8; i++)
	    {
		dungeonRowShape += dbl.finalLayout[i][j].hasRoom() ? "*" : ".";
	    }
	    System.out.println(dungeonRowShape);
	}
    }

    // resembles TemplateStructurePiece.handleDataMarker()
    protected static void handleDataBlock(String name, BlockPos pos, IWorld world, Random rand, MutableBoundingBox bb, DungeonGenData genData)
    {
	//DimDungeons.LOGGER.info("DATA BLOCK NAME: " + name);

	if ("ReturnPortal".equals(name))
	{
	    world.setBlock(pos, BlockRegistrar.block_gold_portal.defaultBlockState(), 2); // erase this data block
	    TileEntityGoldPortal te = (TileEntityGoldPortal) world.getBlockEntity(pos);
	    if (te != null)
	    {
		te.setDestination(genData.returnPoint.getX() + 0.5D, genData.returnPoint.getY() + 0.1D, genData.returnPoint.getZ() + 0.5D, genData.returnDimension);
	    }
	}
	else if ("BackToEntrance".equals(name))
	{
	    world.setBlock(pos, BlockRegistrar.block_local_teleporter.defaultBlockState(), 2); // erase this data block
	    TileEntityLocalTeleporter te = (TileEntityLocalTeleporter) world.getBlockEntity(pos);
	    if (te != null)
	    {
		ItemPortalKey key = (ItemPortalKey) genData.keyItem.getItem();
		double entranceX = key.getWarpX(genData.keyItem);
		double entranceZ = key.getWarpZ(genData.keyItem);
		te.setDestination(entranceX, 55.1D, entranceZ, 0.0f, 180.0f);
	    }
	}
	else if ("LockItStoneBrick".equals(name))
	{
	    world.setBlock(pos, Blocks.STONE_BRICKS.defaultBlockState(), 2); // erase this data block 
	}
	else if ("LockIt".equals(name))
	{
	    // do nothing!
	}
	else if ("FortuneTeller".equals(name))
	{
	    world.setBlock(pos, Blocks.STONE_BRICKS.defaultBlockState(), 2); // erase this data block 
	    faceContainerTowardsAir(world, pos.below());

	    // put a message inside the dispenser
	    TileEntity te = world.getBlockEntity(pos.below());
	    if (te instanceof DispenserTileEntity)
	    {
		((DispenserTileEntity) te).clearContent();
		ItemStack message = generateLuckyMessage(rand);
		((DispenserTileEntity) te).addItem(message);
	    }
	    else
	    {
		DimDungeons.logMessageWarn("DIMDUNGEONS TILE ENTITY ERROR: unable to place a fortune teller block.");
	    }
	}
	else if ("ChestLoot1".equals(name))
	{
	    fillChestBelow(pos, new ResourceLocation(DimDungeons.RESOURCE_PREFIX + "chests/chestloot_basic_easy"), world, rand);
	}
	else if ("ChestLoot2".equals(name))
	{
	    fillChestBelow(pos, new ResourceLocation(DimDungeons.RESOURCE_PREFIX + "chests/chestloot_basic_hard"), world, rand);
	}
	else if ("ChestLootLucky".equals(name))
	{
	    // 70% nothing, 30% random minecraft loot table that isn't an end city
	    int lucky = rand.nextInt(100);
	    if (lucky < 30)
	    {
		fillChestBelow(pos, new ResourceLocation(DimDungeons.RESOURCE_PREFIX + "chests/chestloot_lucky"), world, rand);
	    }
	    else
	    {
		// actually within that 70% of nothing, if Artifacts is installed, then have a 10% chance of a mimic!
		if (DungeonConfig.isModInstalled("artifacts"))
		{
		    if (lucky < 40)
		    {
			spawnMimicFromArtifactsMod(pos, "mimic", world);
		    }
		}

		world.setBlock(pos, Blocks.AIR.defaultBlockState(), 2); // erase this data block 
		world.setBlock(pos.below(), Blocks.AIR.defaultBlockState(), 2); // and erase the chest below it
	    }
	}
	else if ("SetTrappedLoot".equals(name))
	{
	    world.setBlock(pos, Blocks.AIR.defaultBlockState(), 2); // erase this data block
	    LockableLootTileEntity.setLootTable(world, rand, pos.below(), new ResourceLocation(DimDungeons.RESOURCE_PREFIX + "chests/chestloot_basic_easy"));
	}
	else if ("BarrelLoot1".equals(name))
	{
	    fillBarrelBelow(pos, new ResourceLocation(DimDungeons.RESOURCE_PREFIX + "chests/chestloot_basic_easy"), world, rand);
	}
	else if ("PlaceL2Key".equals(name))
	{
	    world.setBlock(pos, Blocks.AIR.defaultBlockState(), 2); // erase this data block
	    TileEntityPortalKeyhole te = (TileEntityPortalKeyhole) world.getBlockEntity(pos.below());
	    if (te != null)
	    {
		ItemStack key = te.getObjectInserted();
		if (key.getItem() instanceof ItemPortalKey)
		{
		    ((ItemPortalKey) key.getItem()).activateKeyLevel2(key);
		    te.setContents(key);
		}
	    }
	}
	else if ("SummonWitch".equals(name))
	{
	    world.setBlock(pos, Blocks.AIR.defaultBlockState(), 2); // erase this data block
	    spawnEnemyHere(pos, "minecraft:witch", world, genData.dungeonTheme);
	}
	else if ("SummonWaterEnemy".equals(name))
	{
	    world.setBlock(pos, Blocks.AIR.defaultBlockState(), 2); // erase this data block
	    spawnEnemyHere(pos, "minecraft:guardian", world, genData.dungeonTheme);
	}
	else if ("SummonEnderman".equals(name))
	{
	    world.setBlock(pos, Blocks.AIR.defaultBlockState(), 2); // erase this data block
	    spawnEnemyHere(pos, "minecraft:enderman", world, genData.dungeonTheme);
	}
	else if ("SummonEnemy1".equals(name))
	{
	    world.setBlock(pos, Blocks.AIR.defaultBlockState(), 2); // erase this data block

	    int poolSize = DungeonConfig.basicEnemySet1.size();
	    String mobid = DungeonConfig.basicEnemySet1.get(rand.nextInt(poolSize));
	    if (genData.dungeonTheme > 0)
	    {
		poolSize = DungeonConfig.themeSettings.get(genData.dungeonTheme - 1).themeEnemySet1.size();
		mobid = DungeonConfig.themeSettings.get(genData.dungeonTheme - 1).themeEnemySet1.get(rand.nextInt(poolSize));
	    }

	    spawnEnemyHere(pos, mobid, world, genData.dungeonTheme);
	}
	else if ("SummonEnemy2".equals(name))
	{
	    world.setBlock(pos, Blocks.AIR.defaultBlockState(), 2); // erase this data block

	    int poolSize = DungeonConfig.basicEnemySet2.size();
	    String mobid = DungeonConfig.basicEnemySet2.get(rand.nextInt(poolSize));
	    if (genData.dungeonTheme > 0)
	    {
		poolSize = DungeonConfig.themeSettings.get(genData.dungeonTheme - 1).themeEnemySet2.size();
		mobid = DungeonConfig.themeSettings.get(genData.dungeonTheme - 1).themeEnemySet2.get(rand.nextInt(poolSize));
	    }

	    spawnEnemyHere(pos, mobid, world, genData.dungeonTheme);
	}
	else
	{
	    DimDungeons.logMessageWarn("UNHANDLED DATA BLOCK WITH name = " + name);
	    world.setBlock(pos, Blocks.AIR.defaultBlockState(), 2); // erase this data block
	}
    }

    private static void spawnEnemyHere(BlockPos pos, String resourceLocation, IWorld world, int theme)
    {
	EntityType<?> entitytype = EntityType.byString(resourceLocation).orElse(EntityType.CHICKEN);

	Entity mob = entitytype.spawn((ServerWorld) world, null, null, pos, SpawnReason.STRUCTURE, true, true);

	TranslationTextComponent fancyName = new TranslationTextComponent("enemy.dimdungeons." + resourceLocation);
	mob.setCustomName(fancyName);
	mob.moveTo(pos, 0.0F, 0.0F);

	if (mob instanceof MobEntity)
	{
	    ((MobEntity) mob).setCanPickUpLoot(false);
	    ((MobEntity) mob).restrictTo(pos, 8);
	    ((MobEntity) mob).setPersistenceRequired();

	    // health scaling
	    double healthScaling = DungeonConfig.basicEnemyHealthScaling;
	    if (theme > 0)
	    {
		healthScaling = DungeonConfig.themeSettings.get(theme - 1).themeEnemyHealthScaling;
	    }
	    ModifiableAttributeInstance tempHealth = ((MobEntity) mob).getAttribute(Attributes.MAX_HEALTH);
	    ((MobEntity) mob).getAttribute(Attributes.MAX_HEALTH).setBaseValue(tempHealth.getBaseValue() * healthScaling);
	    ((MobEntity) mob).setHealth((float) ((MobEntity) mob).getAttribute(Attributes.MAX_HEALTH).getBaseValue());
	    
	    // randomly put a themed key into a mob's offhand
	    if ( world.getRandom().nextInt(100) < DungeonConfig.chanceForThemeKeys && DungeonConfig.themeSettings.size() > 0 )
	    {
		// if the mob's offhand slot is occupied then just skip it
		if ( !((MobEntity) mob).hasItemInSlot(EquipmentSlotType.OFFHAND))
		{
		    int numThemes = DungeonConfig.themeSettings.size();
		    ItemStack stack = new ItemStack(ItemRegistrar.item_portal_key);
		    ((ItemPortalKey) (ItemRegistrar.item_portal_key.asItem())).activateKeyLevel1(stack, world.getRandom().nextInt(numThemes) + 1);
		    
		    ((MobEntity) mob).setItemInHand(Hand.OFF_HAND, stack);
		    ((MobEntity) mob).setDropChance(EquipmentSlotType.OFFHAND, 1.0f);
		}
	    }

	    // not needed with the new spawn() above
	    //((MobEntity)mob).onInitialSpawn((IServerWorld) world, world.getDifficultyForLocation(pos), SpawnReason.STRUCTURE, (ILivingEntityData) null, (CompoundNBT) null);
	}
    }

    private static void fillChestBelow(BlockPos pos, ResourceLocation lootTable, IWorld world, Random rand)
    {
	world.setBlock(pos, Blocks.AIR.defaultBlockState(), 2); // erase this data block
	faceContainerTowardsAir(world, pos.below());

	// set the loot table
	LockableLootTileEntity.setLootTable(world, rand, pos.below(), lootTable);
	if (!(world.getBlockEntity(pos.below()) instanceof ChestTileEntity))
	{
	    DimDungeons.logMessageWarn("DIMDUNGEONS: FAILED TO PLACE CHEST IN DUNGEON. pos = " + pos.getX() + ", " + pos.getZ());
	}
    }

    private static void fillBarrelBelow(BlockPos pos, ResourceLocation lootTable, IWorld world, Random rand)
    {
	world.setBlock(pos, Blocks.AIR.defaultBlockState(), 2); // erase this data block

	// set the loot table
	LockableLootTileEntity.setLootTable(world, rand, pos.below(), lootTable);
	if (!(world.getBlockEntity(pos.below()) instanceof BarrelTileEntity))
	{
	    DimDungeons.logMessageWarn("DIMDUNGEONS: FAILED TO PLACE BARREL IN DUNGEON. pos = " + pos.getX() + ", " + pos.getZ());
	}
    }

    // I was originally thinking that this would contain direct hints about the dungeon, but that would involve a post generation step
    private static ItemStack generateLuckyMessage(Random rand)
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
	    //title = I18n.format("book.dimdungeons.title_1");
	    //body = I18n.format("book.dimdungeons.fun_message_" + messageVariation);
	    title = new TranslationTextComponent("book.dimdungeons.title_1").getString();
	    body = new TranslationTextComponent("book.dimdungeons.fun_message_" + messageVariation).getString();

	}
	else if (bookType == 1)
	{
	    //title = I18n.format("book.dimdungeons.title_2");
	    //body = I18n.format("book.dimdungeons.helpful_message_" + messageVariation);
	    title = new TranslationTextComponent("book.dimdungeons.title_2").getString();
	    body = new TranslationTextComponent("book.dimdungeons.helpful_message_" + messageVariation).getString();
	}
	else
	{
	    //title = I18n.format("book.dimdungeons.title_3");
	    //body = I18n.format("book.dimdungeons.dangerous_message_" + messageVariation);
	    title = new TranslationTextComponent("book.dimdungeons.title_3").getString();
	    body = new TranslationTextComponent("book.dimdungeons.dangerous_message_" + messageVariation).getString();
	}

	// create the complicated NBT tag list for the list of pages in the book
	ListNBT pages = new ListNBT();
	ITextComponent text = new TranslationTextComponent(body);
	String json = ITextComponent.Serializer.toJson(text);
	//pages.appendTag(new NBTTagString(json)); // 1.12
	//pages.add(0, new NBTTagString(json)); // 1.13
	//pages.add(0, new StringNBT(json)); // 1.14
	pages.add(0, StringNBT.valueOf(json)); // 1.15

	// actually set all the bookish NBT on the item
	stack.getTag().putBoolean("resolved", false);
	stack.getTag().putInt("generation", 0);
	stack.getTag().put("pages", pages);
	stack.getTag().putString("title", title);
	stack.getTag().putString("author", new TranslationTextComponent("book.dimdungeons.author").getString());
	return stack;
    }

    // used on dispensers and chests, particularly ones created by data blocks
    // this function might not be needed in versions later thaN 1.13
    private static void faceContainerTowardsAir(IWorld world, BlockPos pos)
    {
	BlockState bs = world.getBlockState(pos);

	if (bs.getBlock() == Blocks.DISPENSER || bs.getBlock() == Blocks.CHEST)
	{
	    if (world.getBlockState(pos.north()).getBlock() == Blocks.AIR)
	    {
		//bs.with(DispenserBlock.FACING, Direction.NORTH);
	    }
	    if (world.getBlockState(pos.south()).getBlock() == Blocks.AIR)
	    {
		//bs.with(DispenserBlock.FACING, Direction.SOUTH);
	    }
	    if (world.getBlockState(pos.west()).getBlock() == Blocks.AIR)
	    {
		//bs.with(DispenserBlock.FACING, Direction.WEST);
	    }
	    if (world.getBlockState(pos.east()).getBlock() == Blocks.AIR)
	    {
		//bs.with(DispenserBlock.FACING, Direction.EAST);
	    }
	    world.setBlock(pos, bs, 2);
	}
    }

    private static void spawnMimicFromArtifactsMod(BlockPos pos, String casualName, IWorld world)
    {
	MobEntity mob = null;

	if (!DungeonConfig.isModInstalled("artifacts"))
	{
	    return; // fail safe
	}

	mob = (MobEntity) EntityType.byString("artifacts:mimic").get().create((World) world);
	mob.setPos(pos.getX(), pos.getY(), pos.getZ());

	mob.setCanPickUpLoot(false);
	mob.moveTo(pos, 0.0F, 0.0F);
	mob.setPersistenceRequired();

	mob.finalizeSpawn((IServerWorld) world, world.getCurrentDifficultyAt(pos), SpawnReason.STRUCTURE, (ILivingEntityData) null, (CompoundNBT) null);
	world.addFreshEntity(mob);
    }
}