package com.catastrophe573.dimdungeons.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

public class BlockGildedPortal extends Block
{
	public static String REG_NAME = "block_gilded_portal";

	public BlockGildedPortal()
	{
		super(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(2).sound(SoundType.METAL));
	}

	@Override
	public RenderShape getRenderShape(BlockState iBlockState)
	{
		return RenderShape.MODEL;
	}
}