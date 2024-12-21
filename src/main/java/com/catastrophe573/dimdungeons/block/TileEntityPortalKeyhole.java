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
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.ItemStackComponentizationFix;
import net.minecraft.util.datafix.schemas.V3818_5;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

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
	public void loadAdditional(@NotNull CompoundTag compound, HolderLookup.@NotNull Provider registries)
	{
		super.loadAdditional(compound, registries);

		if (compound.contains(ITEM_PROPERTY_KEY, 10))
		{
			if (compound.getCompound(ITEM_PROPERTY_KEY).contains("id") && compound.getCompound(ITEM_PROPERTY_KEY).getString("id").equals("minecraft:air"))
			{
				DimDungeons.logMessageInfo("DIMDUNGEONS: Found a legacy keyhole with an air block inside of it. Fixing to be empty.");
				this.objectInserted = ItemStack.EMPTY;
				return;
			}

			this.objectInserted = (ItemStack)ItemStack.parse(registries, compound.getCompound(ITEM_PROPERTY_KEY)).orElse(ItemStack.EMPTY);

			// is this a legacy 1.20 world being upgraded to 1.21? The item inside may have data components that need to be saved here.
			if ( compound.getCompound(ITEM_PROPERTY_KEY).contains("tag") )
			{
				if ( this.objectInserted.getItem() instanceof BaseItemKey )
				{
					DimDungeons.logMessageInfo("DIMDUNGEONS: Found a legacy key inside a keyhole at load time. Trying to upgrade it now.");
					DungeonKeyDataComponentRecord keydata = this.objectInserted.get(DimDungeons.DUNGEON_KEY_DATA);
					assert keydata != null;
					boolean key_activated = keydata.key_activated();
					boolean built = keydata.built();
					long dest_x = keydata.dest_x();
					long dest_z = keydata.dest_z();
					int name_type = keydata.name_type();
					int name_part_1 = keydata.name_part_1();
					int name_part_2 = keydata.name_part_2();
					int theme = keydata.theme();
					String dungeon_type = keydata.dungeon_type();
					CompoundTag oldData = compound.getCompound(ITEM_PROPERTY_KEY).getCompound("tag");

					if (oldData.contains(BaseItemKey.NBT_KEY_ACTIVATED))
					{
						key_activated = oldData.getBoolean(BaseItemKey.NBT_KEY_ACTIVATED);
					}
					if (oldData.contains(BaseItemKey.NBT_BUILT))
					{
						built = oldData.getBoolean(BaseItemKey.NBT_BUILT);
					}
					if (oldData.contains(BaseItemKey.NBT_KEY_DESTINATION_X))
					{
						dest_x = oldData.getLong(BaseItemKey.NBT_KEY_DESTINATION_X);
					}
					if (oldData.contains(BaseItemKey.NBT_KEY_DESTINATION_Z))
					{
						dest_z = oldData.getLong(BaseItemKey.NBT_KEY_DESTINATION_Z);
					}
					if (oldData.contains(BaseItemKey.NBT_NAME_TYPE))
					{
						name_type = oldData.getInt(BaseItemKey.NBT_NAME_TYPE);
					}
					if (oldData.contains(BaseItemKey.NBT_NAME_PART_1))
					{
						name_part_1 = oldData.getInt(BaseItemKey.NBT_NAME_PART_1);
					}
					if (oldData.contains(BaseItemKey.NBT_NAME_PART_2))
					{
						name_part_2 = oldData.getInt(BaseItemKey.NBT_NAME_PART_2);
					}
					if (oldData.contains(BaseItemKey.NBT_THEME))
					{
						theme = oldData.getInt(BaseItemKey.NBT_THEME);
					}
					if (oldData.contains(BaseItemKey.NBT_DUNGEON_TYPE))
					{
						dungeon_type = oldData.getString(BaseItemKey.NBT_DUNGEON_TYPE);
					}

					this.objectInserted.set(DimDungeons.DUNGEON_KEY_DATA, new DungeonKeyDataComponentRecord(key_activated, built, dest_x, dest_z, name_type, name_part_1, name_part_2, theme, dungeon_type));
				}
				else
				{
					DimDungeons.logMessageError("DIMDUNGEONS: Found a legacy item ("+compound.getCompound(ITEM_PROPERTY_KEY).getString("id")+") with NBT inside of keyhole while upgrading worlds. This is not supported.");
					this.objectInserted = (ItemStack)ItemStack.parseOptional(registries, compound.getCompound(ITEM_PROPERTY_KEY));
				}
			}
		}
		else
		{
			this.objectInserted = ItemStack.EMPTY;
		}
	}

	@Override
	protected void saveAdditional(@NotNull CompoundTag compound, HolderLookup.@NotNull Provider registries)
	{
		super.saveAdditional(compound, registries);

		// as for Neoforge 1.21, encoding empty ItemStacks is no longer allowed for some reason?
		if ( isFilled() && !getObjectInserted().isEmpty() )
		{
			compound.put(ITEM_PROPERTY_KEY, getObjectInserted().save(registries));
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
}