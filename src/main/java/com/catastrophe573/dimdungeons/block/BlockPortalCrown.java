package com.catastrophe573.dimdungeons.block;

import com.catastrophe573.dimdungeons.DimDungeons;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.material.MapColor;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;

public class BlockPortalCrown extends WallBlock
{
	public static String REG_NAME = "block_portal_crown";

	public BlockPortalCrown()
	{
		super(BlockBehaviour.Properties.of().
				setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(DimDungeons.MOD_ID, REG_NAME))).
				mapColor(MapColor.STONE).instrument(NoteBlockInstrument.PLING).strength(3).sound(SoundType.METAL));
	}
}