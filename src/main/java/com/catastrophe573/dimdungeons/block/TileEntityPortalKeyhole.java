package com.catastrophe573.dimdungeons.block;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.DungeonConfig;
import com.catastrophe573.dimdungeons.item.ItemPortalKey;
import com.catastrophe573.dimdungeons.structure.DungeonPlacement;
import com.catastrophe573.dimdungeons.utils.DungeonGenData;
import com.catastrophe573.dimdungeons.utils.DungeonUtils;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class TileEntityPortalKeyhole extends BlockEntity
{
    public static final String REG_NAME = "tileentity_portal_keyhole";

    @ObjectHolder(DimDungeons.RESOURCE_PREFIX + REG_NAME)
    public static BlockEntityType<TileEntityPortalKeyhole> TYPE;

    public enum DungeonBuildSpeed
    {
	STOPPED, SLOW, NORMAL, FASTEST;
    };

    public TileEntityPortalKeyhole(BlockPos pos, BlockState state)
    {
	super(TYPE, pos, state);
    }

    private ItemStack objectInserted = ItemStack.EMPTY;
    private static final String ITEM_PROPERTY_KEY = "objectInserted";

    public static void buildTick(Level level, BlockPos pos, BlockState state, TileEntityPortalKeyhole self)
    {
	ItemPortalKey key = (ItemPortalKey) self.getObjectInserted().getItem();
	DungeonGenData genData = DungeonGenData.Create().setKeyItem(self.getObjectInserted()).setDungeonType(key.getDungeonType(self.getObjectInserted())).setTheme(key.getDungeonTheme(self.getObjectInserted())).setReturnPoint(BlockPortalKeyhole.getReturnPoint(state, pos), DungeonUtils.serializeDimensionKey(level.dimension()));
	long buildX = (long) key.getDungeonTopLeftX(genData.keyItem);
	long buildZ = (long) key.getDungeonTopLeftZ(genData.keyItem);
	ServerLevel dungeonWorld = DungeonUtils.getDungeonWorld(level.getServer());
	boolean placedRoom = false;

	if (!(genData.keyItem.getItem() instanceof ItemPortalKey))
	{
	    DimDungeons.logMessageError("FATAL ERROR: Using a non-key item to build a dungeon? What happened?");
	    return;
	}

	// where are we in the build process?
	int buildStep = state.getValue(BlockPortalKeyhole.BUILD_STEP);
	if (buildStep == 0)
	{
	    DimDungeons.logMessageError("DIMDUNGEONS ERROR: Keyhole block ticked when it should not have."); // unreachable code
	}
	else if (buildStep == 650)
	{
	    DungeonUtils.openPortalAfterBuild(level, pos, genData, self);
	}
	else if (buildStep % 10 == 0)
	{
	    int chunk = (buildStep - 10) / 10;
	    int i = chunk / 8;
	    int j = chunk % 8;
	    ChunkPos cpos = new ChunkPos(((int) buildX / 16) + i + 4, ((int) buildZ / 16) + j + 4); // the arbitrary +4 is to make the dungeons line up with vanilla maps

	    //DimDungeons.logMessageInfo("Ticking BUILD_STEP: " + buildStep + ", building chunk " + chunk);
	    placedRoom = DungeonPlacement.buildRoomAboveSign(dungeonWorld, cpos, genData);
	}

	// save the new buildStep
	BlockState newBlockState = state.setValue(BlockPortalKeyhole.BUILD_STEP, nextBuildStep(buildStep, DungeonConfig.getDungeonBuildSpeed()));
	newBlockState = newBlockState.setValue(BlockPortalKeyhole.BUILD_PARTICLE, placedRoom);
	level.setBlockAndUpdate(pos, newBlockState);
    }

    protected static int nextBuildStep(int currentStep, DungeonBuildSpeed speed)
    {
	// intentionally never finishes
	if (speed == DungeonBuildSpeed.STOPPED)
	{
	    return currentStep;
	}

	// the slow speed always runs for 650 ticks and therefore attempts to build in 2 chunks per second
	if (speed == DungeonBuildSpeed.SLOW)
	{
	    return currentStep >= 650 ? 0 : currentStep + 1;
	}

	// the normal speed skips 5 ticks and attempts to build in 10 chunks per second
	if (speed == DungeonBuildSpeed.NORMAL)
	{
	    if (currentStep < 5)
	    {
		return 5;
	    }
	    if (currentStep >= 650)
	    {
		return 0;
	    }
	    return currentStep + 5;
	}

	// the fastest speed skips 10 ticks and attempts to build in 20 chunks per second
	if (speed == DungeonBuildSpeed.FASTEST)
	{
	    if (currentStep < 10)
	    {
		return 5;
	    }
	    if (currentStep >= 650)
	    {
		return 0;
	    }
	    return currentStep + 10;
	}

	return 0;
    }

    @Override
    public void load(CompoundTag compound)
    {
	super.load(compound);
	if (compound.contains(ITEM_PROPERTY_KEY))
	{
	    setContents(ItemStack.of(compound.getCompound(ITEM_PROPERTY_KEY)));
	}
    }

    @Override
    protected void saveAdditional(CompoundTag compound)
    {
	super.saveAdditional(compound);

	// always send this, even if it is empty or air
	compound.put(ITEM_PROPERTY_KEY, objectInserted.save(new CompoundTag()));
    }

    public boolean isFilled()
    {
	return !objectInserted.isEmpty();
    }

    public boolean isActivated()
    {
	ItemStack item = getObjectInserted();

	if (item.isEmpty())
	{
	    return false;
	}
	// awakened ItemPortalKeys will open a portal to the dungeon dimension
	else if (item.getItem() instanceof ItemPortalKey)
	{
	    ItemPortalKey key = (ItemPortalKey) item.getItem();
	    return key.isActivated(item);
	}

	return false;
    }

    public ItemStack getObjectInserted()
    {
	return this.objectInserted;
    }

    //  be sure to notify the world of a block update after calling this
    public void setContents(ItemStack item)
    {
	this.objectInserted = item;
	this.objectInserted.setCount(1);
	this.setChanged();
    }

    //  be sure to notify the world of a block update after calling this
    public void removeContents()
    {
	this.objectInserted = ItemStack.EMPTY;
	this.setChanged();
    }
}