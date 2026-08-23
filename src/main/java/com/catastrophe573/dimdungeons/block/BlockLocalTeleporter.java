package com.catastrophe573.dimdungeons.block;

import javax.annotation.Nullable;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.mojang.serialization.MapCodec;
import net.minecraft.server.TickTask;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;

import static net.minecraft.world.level.portal.DimensionTransition.DO_NOTHING;
import static net.minecraft.world.level.portal.DimensionTransition.PLAY_PORTAL_SOUND;

public class BlockLocalTeleporter extends BaseEntityBlock
{
	@Override
	protected MapCodec<? extends BlockLocalTeleporter> codec()
	{
		throw new AssertionError("Implement block codec!");
	}

	public static String REG_NAME = "block_local_teleporter";

	public BlockLocalTeleporter()
	{
		super(BlockBehaviour.Properties.of().pushReaction(PushReaction.IGNORE).randomTicks().strength(9999).sound(SoundType.GLASS).noCollission().lightLevel((p) -> 15));
	}

	@Override
	public RenderShape getRenderShape(BlockState p_49232_)
	{
		return RenderShape.MODEL;
	}

	// Called by ItemBlocks after a block is set in the world, to allow post-place
	// logic
	@Override
	public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
	{
	}

	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
	{
	}

	@Override
	public ItemStack getCloneItemStack(LevelReader pLevel, BlockPos pPos, BlockState pState)
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

		if (entityIn.canUsePortal(false))
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
		if ( player.level() == dim )
		{
			// "player moved wrongly" is the dumbest thing in modded Minecraft
			// schedule the teleport to happen later, to avoid Minecraft's built in anticheat
			var server = dim.getServer();
			if (server != null)
			{
				server.tell(new TickTask(server.getTickCount(), () ->
				{
					if (player.connection != null)
					{
						player.teleportTo(dim, x, y, z, destYaw, destPitch);
					}
				}));
			}
		}
		else
		{
			// somehow we're using a "local teleporter" to go to another dimension? idk, I'll allow it to work I guess
			DimensionTransition dt = new DimensionTransition(dim, new Vec3(x, y, z), Vec3.ZERO, destYaw, destPitch, false, PLAY_PORTAL_SOUND);
			player.changeDimension(dt);
		}

		return player;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return new TileEntityLocalTeleporter(pos, state);
	}
}