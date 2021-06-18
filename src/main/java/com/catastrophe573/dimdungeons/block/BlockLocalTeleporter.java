package com.catastrophe573.dimdungeons.block;

import javax.annotation.Nullable;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.dimension.CustomTeleporter;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BreakableBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import net.minecraft.block.AbstractBlock;

public class BlockLocalTeleporter extends BreakableBlock
{
    public static String REG_NAME = "block_local_teleporter";

    public BlockLocalTeleporter()
    {
	super(AbstractBlock.Properties.of(Material.PORTAL).strength(50).sound(SoundType.GLASS).noCollission().lightLevel((p) -> 15));
	setRegistryName(DimDungeons.MOD_ID, REG_NAME);
    }

    // Called by ItemBlocks after a block is set in the world, to allow post-place logic
    @Override
    public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
    }

    // called by getItemsToDropCount() to determine what BlockItem or Item to drop
    // in this case, do not allow the player to obtain this block as an item
    @Override
    public ItemStack getCloneItemStack(IBlockReader worldIn, BlockPos pos, BlockState state)
    {
	return ItemStack.EMPTY;
    }

    @Deprecated
    @Override
    public boolean useShapeForLightOcclusion(BlockState state)
    {
	return true;
    }

    // called When an entity collides with the Block
    @Override
    public void entityInside(BlockState state, World worldIn, BlockPos pos, Entity entityIn)
    {
	// do not process this block on the client
	if (worldIn.isClientSide)
	{
	    return;
	}

	// only teleport players! items and mobs and who knows what else must stay behind
	if (!(entityIn instanceof ServerPlayerEntity))
	{
	    return;
	}

	if (entityIn.canChangeDimensions())
	{
	    TileEntity tile = worldIn.getBlockEntity(pos);

	    if (tile != null && tile instanceof TileEntityLocalTeleporter)
	    {
		TileEntityLocalTeleporter te = (TileEntityLocalTeleporter) worldIn.getBlockEntity(pos);

		BlockPos destination = te.getDestination();
		float warpX = destination.getX();
		float warpY = destination.getY();
		float warpZ = destination.getZ();
		float newPitch = (float)te.getPitch();
		float newYaw = (float)te.getYaw();
		
		actuallyPerformTeleport((ServerPlayerEntity) entityIn, worldIn.getServer().getLevel(entityIn.getCommandSenderWorld().dimension()), warpX, warpY, warpZ, newYaw, newPitch);
	    }
	}
    }

    @Override
    public boolean hasTileEntity(BlockState state)
    {
	return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
    {
	return new TileEntityLocalTeleporter();
    }

    protected Entity actuallyPerformTeleport(ServerPlayerEntity player, ServerWorld dim, double x, double y, double z, float destYaw, float destPitch)
    {
	CustomTeleporter tele = new CustomTeleporter(dim);
	tele.setDestPos(x, y, z, destYaw, destPitch);
	player.changeDimension(dim, tele); // changing within the same dimension, but still teleport safely anyways
	//player.teleport(dim, x, y, z, destYaw, destPitch);
	return player;
    }    
}
