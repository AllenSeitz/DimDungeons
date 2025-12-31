package com.catastrophe573.dimdungeons.block;

import javax.annotation.Nullable;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.InsideBlockEffectApplier;
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
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.Vec3;

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
		super(BlockBehaviour.Properties.of().
				setId(ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(DimDungeons.MOD_ID, REG_NAME))).
				pushReaction(PushReaction.BLOCK).randomTicks().strength(9999).sound(SoundType.GLASS).noCollision().lightLevel((p) -> 15));
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
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, @Nullable Orientation orientation, boolean movedByPiston)
	{
	}

	@Override
	protected ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData)
	{
		return ItemStack.EMPTY;
	}

	@Deprecated
	@Override
	public boolean useShapeForLightOcclusion(BlockState state)
	{
		return true;
	}

	@Override
	// called When an entity collides with the Block
	protected void entityInside(BlockState state, Level level, BlockPos blockpos, Entity entity, InsideBlockEffectApplier effectApplier, boolean intersects)
	{
		//effectApplier.apply(InsideBlockEffectType.FIRE_IGNITE); // example code of using normal effect Appliers
		actuallyTeleportEntityInside(state, level, blockpos, entity);
	}

	public void actuallyTeleportEntityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn)
	{
		// do not process this block on the client
		if (worldIn.isClientSide())
		{
			return;
		}

		// only teleport players! items and mobs and who knows what else must stay behind
		if (!(entityIn instanceof ServerPlayer))
		{
			return;
		}

		if (!entityIn.isPassenger() && !entityIn.isVehicle() && entityIn.canUsePortal(false))
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

				actuallyPerformTeleport((ServerPlayer) entityIn, worldIn.getServer().getLevel(entityIn.level().dimension()), warpX, warpY, warpZ, newYaw, newPitch);
			}
		}
	}

	protected Entity actuallyPerformTeleport(ServerPlayer player, ServerLevel dim, double x, double y, double z, float destYaw, float destPitch)
	{
		TeleportTransition tt = new TeleportTransition(dim, new Vec3(x, y, z), new Vec3(0, 0, 0), destYaw, destPitch, TeleportTransition.DO_NOTHING);

		player.teleport(tt);

		return player;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return new TileEntityLocalTeleporter(pos, state);
	}
}