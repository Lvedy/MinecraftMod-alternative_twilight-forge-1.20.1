package com.lvedy.alternative_twilight.mixin.Minoshroom;

import com.lvedy.alternative_twilight.ATModFinal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import twilightforest.entity.ai.goal.GroundAttackGoal;
import twilightforest.entity.boss.Minoshroom;

@Mixin(value = GroundAttackGoal.class)
public class GroundAttackGoalMixin{
    @Shadow
    public Minoshroom attacker;
    @Shadow
    public int attackTick;

    @Inject(method = "start", at = @At(value = "INVOKE", target = "Ltwilightforest/entity/boss/Minoshroom;setMaxCharge(I)V"))
    public void start(CallbackInfo ci){
        if(this.attacker.getHealth() <= this.attacker.getMaxHealth() * 0.01F * ATModFinal.MinoshroomAngry) {
            this.attackTick = (int)(this.attackTick * ATModFinal.MinoshroomChargeTimeReduction * 0.01);
        }
    }
}
