package com.catastrophe573.dimdungeons.block;

import javax.annotation.Nullable;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.dimension.CustomTeleporter;

import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
//import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import net.minecraft.world.level.block.state.BlockBehaviour;

public class BlockLocalTeleporter extends BaseEntityBlock
{
    public static String REG_NAME = "block_local_teleporter";

    public BlockLocalTeleporter()
    {
	super(BlockBehaviour.Properties.of(Material.PORTAL).strength(50).sound(SoundType.GLASS).noCollission().lightLevel((p) -> 15));
	setRegistryName(DimDungeons.MOD_ID, REG_NAME);
    }

    // Called by ItemBlocks after a block is set in the world, to allow post-place logic
    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
    }

    @Override
    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
    }

    // called by getItemsToDropCount() to determine what BlockItem or Item to drop
    // in this case, do not allow the player to obtain this block as an item
    @Override
    public ItemStack getCloneItemStack(BlockGetter worldIn, BlockPos pos, BlockState state)
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
    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn)
    {
	// do not process this block on the client
	if (worldIn.isClientSide)
	{
	    return;
	}

	// only teleport players! items and mobs and who knows what else must stay behind
	if (!(entityIn instanceof ServerPlayer))
	{
	    return;
	}

	if (entityIn.canChangeDimensions())
	{
	    BlockEntity tile = worldIn.getBlockEntity(pos);

	    if (tile != null && tile instanceof TileEntityLocalTeleporter)
	    {
		TileEntityLocalTeleporter te = (TileEntityLocalTeleporter) worldIn.getBlockEntity(pos);

		BlockPos destination = te.getDestination();
		float warpX = destination.getX();
		float warpY = destination.getY();
		float warpZ = destination.getZ();
		float newPitch = (float) te.getPitch();
		float newYaw = (float) te.getYaw();

		actuallyPerformTeleport((ServerPlayer) entityIn, worldIn.getServer().getLevel(entityIn.getCommandSenderWorld().dimension()), warpX, warpY, warpZ, newYaw, newPitch);
	    }
	}
    }

    protected Entity actuallyPerformTeleport(ServerPlayer player, ServerLevel dim, double x, double y, double z, float destYaw, float destPitch)
    {
	CustomTeleporter tele = new CustomTeleporter(dim);
	tele.setDestPos(x, y, z, destYaw, destPitch);
	player.changeDimension(dim, tele); // changing within the same dimension, but still teleport safely anyways
	//player.teleport(dim, x, y, z, destYaw, destPitch);
	return player;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
	return new TileEntityLocalTeleporter(pos, state);
    }
}
