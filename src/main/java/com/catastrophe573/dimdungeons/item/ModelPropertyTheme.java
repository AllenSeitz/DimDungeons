package com.catastrophe573.dimdungeons.item;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;

public record ModelPropertyTheme() implements RangeSelectItemModelProperty
{
    public static final MapCodec<ModelPropertyTheme> MAP_CODEC = MapCodec.unit(new ModelPropertyTheme());

    @Override
    public float get(ItemStack itemstack, @org.jetbrains.annotations.Nullable ClientLevel level, @org.jetbrains.annotations.Nullable ItemOwner owner, int seed)
    {
        return getTheme(itemstack);
    }

    @Override
    public MapCodec<ModelPropertyTheme> type()
    {
        return MAP_CODEC;
    }

    public static float getTheme(ItemStack stack)
    {
        if ( stack.getItem() instanceof ItemBlankThemeKey )
        {
            return ItemBlankThemeKey.getKeyThemeAsFloat(stack);
        }
        if ( stack.getItem() instanceof ItemPortalKey )
        {
            return ItemPortalKey.getKeyThemeAsFloat(stack);
        }
        return 0.0F;
    }
}