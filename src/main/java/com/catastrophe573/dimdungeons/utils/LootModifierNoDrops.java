package com.catastrophe573.dimdungeons.utils;

import java.util.function.Supplier;

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
	// public static final RegistryObject<Codec<LootModifierNoDrops>> CODEC = DimDungeons.GLM_REGISTRAR.register("no_dungeon_drops", () -> RecordCodecBuilder.create(inst -> codecStart(inst).apply(inst, LootModifierNoDrops::new)));
	public static final Supplier<Codec<LootModifierNoDrops>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(inst -> codecStart(inst).apply(inst, LootModifierNoDrops::new)));

	public LootModifierNoDrops(LootItemCondition[] conditionsIn)
	{
		super(conditionsIn);
	}

	@SuppressWarnings("deprecation")
	@NotNull
	@Override
	public ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context)
	{
		// is this a block breaking-type of loot table? (probably yes if this code is running, but make sure the target block isn't null anyway)
		BlockState blockState = context.getParamOrNull(LootContextParams.BLOCK_STATE);
		if (blockState == null)
		{
			return generatedLoot;
		}

		// check if the block broken is on the "no drops in this dimension" list
		String id = blockState.getBlock().builtInRegistryHolder().key().location().toString();
		if ( DungeonConfig.SERVER.blockDropBlacklist.get().contains(id))
		{
			generatedLoot.removeAll(generatedLoot);
		}

		return generatedLoot;
	}

	@Override
	public Codec<? extends IGlobalLootModifier> codec()
	{
		return CODEC.get();
	}
}