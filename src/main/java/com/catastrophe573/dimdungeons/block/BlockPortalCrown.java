package com.catastrophe573.dimdungeons.block;

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
		super(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.PLING).strength(3).sound(SoundType.METAL));
	}
}