package com.catastrophe573.dimdungeons.block;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.dimension.DungeonData;
import com.catastrophe573.dimdungeons.item.BaseItemKey;
import com.catastrophe573.dimdungeons.item.DungeonKeyDataComponentRecord;
import com.catastrophe573.dimdungeons.item.ItemPortalKey;
import com.catastrophe573.dimdungeons.utils.DungeonGenData;
import com.catastrophe573.dimdungeons.utils.DungeonUtils;

import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.SharedConstants;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.ItemStackComponentizationFix;
import net.minecraft.util.datafix.schemas.V3818_5;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class TileEntityPortalKeyhole extends BlockEntity
{
	public static final String REG_NAME = "tileentity_portal_keyhole";

	public enum DungeonBuildSpeed
	{
		STOPPED, SLOW, NORMAL, FASTEST;
	};

	public TileEntityPortalKeyhole(BlockPos pos, BlockState state)
	{
		super(BlockRegistrar.BE_PORTAL_KEYHOLE.get(), pos, state);
	}

	// properties that persist
	private ItemStack objectInserted = ItemStack.EMPTY;
	private static final String ITEM_PROPERTY_KEY = "objectInserted";

	public static <T> void tick(Level level, BlockPos pos, BlockState state, T blockEntity)
	{
		TileEntityPortalKeyhole self = (TileEntityPortalKeyhole) blockEntity;

		ItemPortalKey key = (ItemPortalKey) self.getObjectInserted().getItem();
		DungeonGenData genData = DungeonGenData.Create().setKeyItem(self.getObjectInserted()).setDungeonType(key.getDungeonType(self.getObjectInserted()))
		        .setTheme(key.getDungeonTheme(self.getObjectInserted()))
		        .setReturnPoint(BlockPortalKeyhole.getReturnPoint(state, pos), DungeonUtils.serializeDimensionKey(level.dimension()));

		if (!(genData.keyItem.getItem() instanceof ItemPortalKey))
		{
			DimDungeons.logMessageError("FATAL ERROR: Using a non-key item to build a dungeon? What happened?");
			return;
		}

		// where are we in the build process?
		if (!state.getValue(BlockPortalKeyhole.IS_BUILDING))
		{
			DimDungeons.logMessageError("DIMDUNGEONS ERROR: Keyhole block ticked when it should not have."); // unreachable code
		}
		else
		{
			// wait until all keys on the whole server are done building (this is the easiest way, I don't think this will be a problem)
			if (!DungeonData.get(DungeonUtils.getDungeonWorld(level.getServer())).hasMoreRoomsToBuild())
			{
				DungeonUtils.openPortalAfterBuild(level, pos, genData, self);

				// stop building
				BlockState newBlockState = state.setValue(BlockPortalKeyhole.FACING, state.getValue(BlockPortalKeyhole.FACING)).setValue(BlockPortalKeyhole.FILLED, self.isFilled())
				        .setValue(BlockPortalKeyhole.LIT, self.isActivated()).setValue(BlockPortalKeyhole.IS_BUILDING, false);
				level.setBlockAndUpdate(pos, newBlockState);
			}
		}		
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
				return 10;
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
	protected void loadAdditional(ValueInput input)
	{
		super.loadAdditional(input);
		ItemStack itemstack = input.read(ITEM_PROPERTY_KEY, ItemStack.CODEC).orElse(ItemStack.EMPTY);

		this.objectInserted = itemstack;
	}

	@Override
	protected void saveAdditional(ValueOutput output)
	{
		super.saveAdditional(output);
		if (!this.getObjectInserted().isEmpty())
		{
			output.store(ITEM_PROPERTY_KEY, ItemStack.CODEC, this.getObjectInserted());
		}
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
		// awakened keys will open a portal to the dungeon dimension
		else if (item.getItem() instanceof BaseItemKey key)
		{
            return key.isActivated(item);
		}

		return false;
	}

	public ItemStack getObjectInserted()
	{
		// repair legacy items before extracting them - this is the last chance before data is lost
		if ( BaseItemKey.hasLegacyData(this.objectInserted) )
		{
			BaseItemKey.convertLegacyData(this.objectInserted);
		}

		return this.objectInserted;
	}

	// be sure to notify the world of a block update after calling this
	public void setContents(ItemStack item)
	{
		this.objectInserted = item;
		this.objectInserted.setCount(1);
		this.setChanged();
	}

	// be sure to notify the world of a block update after calling this
	public void removeContents()
	{
		this.objectInserted = ItemStack.EMPTY;
		this.setChanged();
	}

	@Override
	public void preRemoveSideEffects(BlockPos pos, BlockState state)
	{
		if (this instanceof TileEntityPortalKeyhole keyhole && this.level != null)
		{
			ItemStack item = ((TileEntityPortalKeyhole) this).getObjectInserted();
			if (!item.isEmpty())
			{
				Containers.dropItemStack(this.level, pos.getX(), pos.getY(), pos.getZ(), item);
			}
		}
	}
}