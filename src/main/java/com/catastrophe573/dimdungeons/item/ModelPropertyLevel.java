package com.catastrophe573.dimdungeons.item;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public record ModelPropertyLevel() implements RangeSelectItemModelProperty
{
    public static final MapCodec<ModelPropertyLevel> MAP_CODEC = MapCodec.unit(new ModelPropertyLevel());

    @Override
    public float get(ItemStack itemstack, @org.jetbrains.annotations.Nullable ClientLevel levelin, @org.jetbrains.annotations.Nullable ItemOwner owner, int seed)
    {
        return getLevel(itemstack, owner.asLivingEntity());
    }

    @Override
    public MapCodec<ModelPropertyLevel> type()
    {
        return MAP_CODEC;
    }

    public static float getLevel(ItemStack stack, LivingEntity entity)
    {
        if ( stack.getItem() instanceof ItemPortalKey )
        {
            return ItemPortalKey.getKeyLevelAsFloat(stack);
        }
        return 0.0F;
    }
}