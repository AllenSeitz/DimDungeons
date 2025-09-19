package com.catastrophe573.dimdungeons.item;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public record ModelPropertyTheme() implements RangeSelectItemModelProperty
{
    public static final MapCodec<ModelPropertyTheme> MAP_CODEC = MapCodec.unit(new ModelPropertyTheme());

    @Override
    public float get(ItemStack itemstack, @Nullable ClientLevel levelin, @Nullable LivingEntity entity, int p_386612_)
    {
        return getTheme(itemstack, entity);
    }

    @Override
    public MapCodec<ModelPropertyTheme> type()
    {
        return MAP_CODEC;
    }

    public static float getTheme(ItemStack stack, LivingEntity entity)
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