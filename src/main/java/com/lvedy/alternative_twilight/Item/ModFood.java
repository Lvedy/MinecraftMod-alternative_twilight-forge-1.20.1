package com.lvedy.alternative_twilight.Item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class ModFood {
    public static final FoodProperties HONEY_FISH = (new FoodProperties.Builder()).nutrition(8).saturationMod(1.5F).effect(new MobEffectInstance(MobEffects.LUCK, 6000, 0), 1.0F).alwaysEat().build();
    public static final FoodProperties LUXURY_FEAST = (new FoodProperties.Builder()).nutrition(16).saturationMod(2F).effect(new MobEffectInstance(MobEffects.LUCK, 7200, 1), 1.0F).alwaysEat().build();
}
