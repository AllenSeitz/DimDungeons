package com.catastrophe573.dimdungeons.item;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.dimension.DungeonData;
import com.catastrophe573.dimdungeons.dimension.PersonalBuildData;
import com.catastrophe573.dimdungeons.structure.DungeonDesigner.DungeonType;
import com.catastrophe573.dimdungeons.utils.DungeonUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

public class ItemBlankBuildKey extends BaseItemKey
{
    public static final String REG_NAME = "item_blank_build_key";

    public ItemBlankBuildKey()
    {
	super(new Item.Properties().rarity(Rarity.COMMON).tab(ItemRegistrar.CREATIVE_TAB));
    }

    @Override
    public void performActivationRitual(Player player, ItemStack itemstack, Level worldIn, BlockPos pos)
    {
	worldIn.playSound((Player) null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0F, 1.0F);

	if (player == null)
	{
	    DimDungeons.logMessageError("Somehow activated a blank personal key without a player present. Do not do this.");
	    return;
	}

	if (!worldIn.isClientSide)
	{
	    // delete this item and replace it with a regular build key, but first remember which inventory slot it was in
	    int slot = player.getInventory().findSlotMatchingItem(itemstack);
	    itemstack.shrink(1);

	    // generate the activated key and try to insert it into the player's inventory multiple ways as a fail-safe
	    ItemStack newkey = new ItemStack(ItemRegistrar.ITEM_BUILD_KEY.get());
	    activateBuildKey(worldIn.getServer(), newkey, player);

	    if (!player.getInventory().add(slot, newkey))
	    {
		if (!player.addItem(newkey))
		{
		    player.drop(newkey, false);
		}
	    }
	}

	createActivationParticleEffects(worldIn, pos);
	createActivationParticleEffectsForBuildKey(worldIn, pos);
    }

    public void activateBuildKey(MinecraftServer server, ItemStack stack, LivingEntity player)
    {
	CompoundTag data = new CompoundTag();
	data.putBoolean(NBT_KEY_ACTIVATED, true);
	data.putBoolean(NBT_BUILT, false);
	data.putString(NBT_DUNGEON_TYPE, DungeonType.PERSONAL_BUILD.toString());

	ChunkPos dest = PersonalBuildData.get(DungeonUtils.getPersonalBuildWorld(server)).getPosForOwner(player);
	data.putInt(NBT_KEY_DESTINATION_X, dest.x);
	data.putInt(NBT_KEY_DESTINATION_Z, dest.z);

	// name the key after the player
	stack.setHoverName(Component.translatable("npart.dimdungeons.struct_8", " ", player.getName().getString()));

	stack.setTag(data);
	DungeonData.get(DungeonUtils.getDungeonWorld(server)).notifyOfNewKeyActivation();
    }

    // EVEN MORE particle effects for this special event!
    public void createActivationParticleEffectsForBuildKey(Level worldIn, BlockPos pos)
    {
	RandomSource random = worldIn.getRandom();
	for (int i = 0; i < 8; i++)
	{
	    double d0 = (double) ((float) pos.getX() + 0.5F);
	    double d1 = (double) ((float) pos.getY() + 0.8F);
	    double d2 = (double) ((float) pos.getZ() + 0.5F);
	    double xspeed = (random.nextFloat() * 0.04) * (random.nextBoolean() ? 1 : -1);
	    double yspeed = random.nextFloat() * 0.65;
	    double zspeed = (random.nextFloat() * 0.04) * (random.nextBoolean() ? 1 : -1);
	    worldIn.addParticle(ParticleTypes.PORTAL, d0, d1, d2, xspeed, yspeed, zspeed);
	}
    }
}
