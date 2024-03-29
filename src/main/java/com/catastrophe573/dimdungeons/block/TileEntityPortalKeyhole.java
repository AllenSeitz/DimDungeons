package com.catastrophe573.dimdungeons.block;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.item.ItemPortalKey;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class TileEntityPortalKeyhole extends TileEntity
{
    public static final String REG_NAME = "tileentity_portal_keyhole";

    @ObjectHolder(DimDungeons.RESOURCE_PREFIX + REG_NAME)
    public static TileEntityType<TileEntityPortalKeyhole> TYPE;

    public TileEntityPortalKeyhole()
    {
	super(TYPE);
    }

    public TileEntityPortalKeyhole(TileEntityType<?> tileEntityTypeIn)
    {
	super(tileEntityTypeIn);
    }

    private ItemStack objectInserted = ItemStack.EMPTY;
    private static final String ITEM_PROPERTY_KEY = "objectInserted";

    @Override
    public void load(BlockState stateIn, CompoundNBT compound)
    {
	super.load(stateIn, compound);
	readMyNBTData(compound);
    }

    @Override
    public CompoundNBT save(CompoundNBT compound)
    {
	writeMyNBTData(compound);
	return super.save(compound);
    }

    // synchronize on chunk loading
    @Override
    public CompoundNBT getUpdateTag()
    {
	return save(new CompoundNBT());
    }

    // synchronize on block updates
    @Override
    public SUpdateTileEntityPacket getUpdatePacket()
    {
	CompoundNBT tag = save(new CompoundNBT());
	return new SUpdateTileEntityPacket(worldPosition, 1, tag); // Forge recommends putting -1 as the second parameter
    }

    // business logic for this object
    public void writeMyNBTData(CompoundNBT compound)
    {
	// always send this, even if it is empty or air
	CompoundNBT itemNBT = new CompoundNBT();
	compound.put(ITEM_PROPERTY_KEY, objectInserted.save(itemNBT));
    }

    // business logic for this object
    public void readMyNBTData(CompoundNBT compound)
    {
	if (compound.contains(ITEM_PROPERTY_KEY))
	{
	    this.objectInserted = (ItemStack.of(compound.getCompound(ITEM_PROPERTY_KEY)));
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