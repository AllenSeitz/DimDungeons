package com.catastrophe573.dimdungeons.utils;

import java.util.function.Supplier;

import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import com.catastrophe573.dimdungeons.DungeonConfig;
import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;

public class LootModifierNoDrops extends net.neoforged.neoforge.common.loot.LootModifier
{
	public static final MapCodec<LootModifierNoDrops> CODEC = RecordCodecBuilder.mapCodec(inst ->
		 LootModifier.codecStart(inst).apply(inst, LootModifierNoDrops::new)
	);

	public LootModifierNoDrops(LootItemCondition[] conditionsIn, int priority)
	{
		super(conditionsIn, priority);
	}

	@SuppressWarnings("deprecation")
	@NotNull
	@Override
	public ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context)
	{
		// is this a block breaking-type of loot table? (probably yes if this code is running, but make sure the target block isn't null anyway)
		BlockState blockState = context.getOptionalParameter(LootContextParams.BLOCK_STATE);
		if (blockState == null)
		{
			return generatedLoot;
		}

		// check if the block broken is on the "no drops in this dimension" list
		String id = blockState.getBlock().builtInRegistryHolder().key().identifier().toString();
		if ( DungeonConfig.SERVER.blockDropBlacklist.get().contains(id))
		{
			generatedLoot.removeAll(generatedLoot);
		}

		return generatedLoot;
	}

	@Override
	public MapCodec<? extends IGlobalLootModifier> codec()
	{
		return CODEC;
	}
}