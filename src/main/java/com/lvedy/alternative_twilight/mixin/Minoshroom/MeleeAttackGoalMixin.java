package com.lvedy.alternative_twilight.mixin.Minoshroom;

import com.lvedy.alternative_twilight.ATModFinal;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import twilightforest.entity.boss.Minoshroom;

@Mixin(value = MeleeAttackGoal.class)
public class MeleeAttackGoalMixin{
    @Shadow
    protected PathfinderMob mob;

    @Inject(method = "checkAndPerformAttack", at = @At("HEAD"))
    public void checkAndPerformAttack(CallbackInfo ci){
        if (ATModFinal.MinoshroomModify == 1) {
            if (this.mob instanceof Minoshroom) {
                LivingEntity livingentity = this.mob.getTarget();
                if (livingentity instanceof Player && livingentity.isBlocking() && this.mob.getMainHandItem().getItem() instanceof AxeItem)
                    this.mob.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, ATModFinal.MinoshroomDuration, ATModFinal.MinoshroomAmplifier));
            }
        }
    }
}
