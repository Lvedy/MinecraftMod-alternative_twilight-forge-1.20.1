package com.lvedy.alternative_twilight.mixin.Minoshroom;

import com.lvedy.alternative_twilight.ATModFinal;
import net.minecraft.world.entity.PathfinderMob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import twilightforest.entity.ai.goal.ChargeAttackGoal;
import twilightforest.entity.boss.Minoshroom;

@Mixin(value = ChargeAttackGoal.class)
public class ChargeAttackGoalMixin {
    @Shadow
    private PathfinderMob charger;
    @Shadow
    private int windup;

    @Inject(method = "start", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/PathfinderMob;setSprinting(Z)V"))
    public void start(CallbackInfo ci){
        if(charger instanceof Minoshroom && (this.charger.getHealth() <= this.charger.getMaxHealth() * 0.01F * ATModFinal.MinoshroomAngry)) {
            this.windup = (int)(this.windup * ATModFinal.MinoshroomChargeTimeReduction * 0.01);
        }
    }
}
