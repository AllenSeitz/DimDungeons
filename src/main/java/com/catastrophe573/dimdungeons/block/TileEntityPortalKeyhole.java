package com.catastrophe573.dimdungeons.block;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.item.ItemPortalKey;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class TileEntityPortalKeyhole extends BlockEntity
{
    public static final String REG_NAME = "tileentity_portal_keyhole";

    @ObjectHolder(DimDungeons.RESOURCE_PREFIX + REG_NAME)
    public static BlockEntityType<TileEntityPortalKeyhole> TYPE;

    public TileEntityPortalKeyhole(BlockPos pos, BlockState state)
    {
	super(TYPE, pos, state);
    }

    private ItemStack objectInserted = ItemStack.EMPTY;
    private static final String ITEM_PROPERTY_KEY = "objectInserted";

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

    // synchronize on chunk loading
    // I think if you do this in 1.18, you will spoil the whole chunk. It was good in 1..17 though.
    //	@Override
    //	public CompoundTag getUpdateTag()
    //	{
    //		 return save(new CompoundTag());
    //  }

    // synchronize on block updates
    // potentially not needed in 1.18?
    //	@Override
    //	public ClientboundBlockEntityDataPacket getUpdatePacket()
    //	{
    //		CompoundTag tag = save(new CompoundTag());
    //		return new ClientboundBlockEntityDataPacket(worldPosition);
    //	}

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