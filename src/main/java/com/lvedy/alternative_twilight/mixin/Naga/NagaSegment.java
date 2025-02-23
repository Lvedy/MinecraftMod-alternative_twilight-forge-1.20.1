package com.lvedy.alternative_twilight.mixin.Naga;

import com.lvedy.alternative_twilight.ATModFinal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import twilightforest.entity.TFPart;
import twilightforest.entity.boss.Naga;

@Mixin(value = twilightforest.entity.boss.NagaSegment.class, priority = 7)
public class NagaSegment extends TFPart<Naga> {

    public NagaSegment(Naga parent) {
        super(parent);
    }

    @Inject(method = "hurt", at = @At("HEAD"))
    public void hurt(DamageSource src, float damage, CallbackInfoReturnable<Boolean> cir) {
        if (ATModFinal.NagaModify == 1) {
            damage = damage * 2.0F / 3.0F;
            CompoundTag pCompound = this.getPersistentData();
            if ((!pCompound.contains("BreakTime") || pCompound.getFloat("BreakTime") <= 0) && !this.getParent().level().isClientSide()) {
                if (pCompound.contains("BreakHead"))
                    pCompound.putFloat("BreakHead", pCompound.getFloat("BreakHead") - damage);
                else
                    pCompound.putFloat("BreakHead", -damage);
                this.getParent().addAdditionalSaveData(pCompound);
                this.getParent().readAdditionalSaveData(pCompound);
            }
        }
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {

    }
}
