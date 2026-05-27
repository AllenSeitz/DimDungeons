package com.catastrophe573.dimdungeons.item;

import com.mojang.serialization.MapCodec;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

public record ModelPropertyLevel() implements RangeSelectItemModelProperty
{
    public static final MapCodec<ModelPropertyLevel> MAP_CODEC = MapCodec.unit(new ModelPropertyLevel());

    @Override
    public float get(@NonNull ItemStack itemstack, @org.jetbrains.annotations.Nullable ClientLevel levelin, @org.jetbrains.annotations.Nullable ItemOwner owner, int seed)
    {
        return getLevel(itemstack);
    }

    @Override
    public @NonNull MapCodec<ModelPropertyLevel> type()
    {
        return MAP_CODEC;
    }

    public static float getLevel(ItemStack stack)
    {
        if ( stack.getItem() instanceof ItemPortalKey )
        {
            return ItemPortalKey.getKeyLevelAsFloat(stack);
        }
        return 0.0F;
    }
}