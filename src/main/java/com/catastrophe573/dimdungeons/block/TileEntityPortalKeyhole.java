package com.catastrophe573.dimdungeons.block;

import com.catastrophe573.dimdungeons.item.ItemPortalKey;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public class TileEntityPortalKeyhole extends TileEntity
{
    public TileEntityPortalKeyhole(TileEntityType<?> tileEntityTypeIn)
    {
	super(tileEntityTypeIn);
    }

    private ItemStack objectInserted = ItemStack.EMPTY;
    private static final String ITEM_PROPERTY_KEY = "objectInserted";
    
    @Override
    public void read(NBTTagCompound compound)
    {
	super.read(compound);

	if (compound.hasKey(ITEM_PROPERTY_KEY))
	{
	    this.setContents(ItemStack.read(compound.getCompound(ITEM_PROPERTY_KEY)));
	}
    }

    @Override
    public NBTTagCompound write(NBTTagCompound compound)
    {
	super.write(compound);

	if (this.isFilled())
	{
	    NBTTagCompound itemNBT = new NBTTagCompound();
	    compound.setTag(ITEM_PROPERTY_KEY, objectInserted.write(itemNBT));
	}

	return compound;
    }

    // This controls whether the tile entity gets replaced whenever the block state is changed. Normally only want this when block actually is replaced.
//    @Override
//    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState)
//    {
//	return (oldState.getBlock() != newState.getBlock());
//    }

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
	// three vanilla blocks will also open portals to the 3 vanilla dimensions?
	else if (Block.getBlockFromItem(item.getItem()) != null)
	{
	    Block b = Block.getBlockFromItem(item.getItem());
	    return b == Blocks.NETHERRACK || b == Blocks.END_STONE || b == Blocks.GRASS;
	}

	return false;
    }

    public ItemStack getObjectInserted()
    {
	return this.objectInserted;
    }

    public void setContents(ItemStack item)
    {
	//System.out.println("Called setContents() with " + item.getDisplayName());
	this.objectInserted = item;
	this.objectInserted.setCount(1);
	this.markDirty();
    }

    public void removeContents()
    {
	this.objectInserted = ItemStack.EMPTY;
	this.markDirty();
    }
}