package com.lvedy.alternative_twilight.mixin.KnightPhantom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import twilightforest.entity.boss.KnightPhantom;
import twilightforest.entity.boss.Lich;

import java.util.List;
import java.util.function.Predicate;

@Mixin(KnightPhantom.class)
public abstract class KnightPhantomMixin extends FlyingMob {
    @Unique
    private static final Predicate<Entity> IS_NOT_SELF = Entity::isAlive;

    @Shadow public abstract boolean isChargingAtPlayer();

    protected KnightPhantomMixin(EntityType<? extends FlyingMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "hurt", at = @At("HEAD"))
    public void hurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir){
        if (source.getEntity() instanceof Player && !this.isChargingAtPlayer()) {
            this.setHealth(Math.min(this.getHealth() + (this.getMaxHealth()*0.2F), this.getMaxHealth()));
            if(source.getEntity() != null)
                source.getEntity().hurt(this.damageSources().magic(), (float) (this.getAttributeBaseValue(Attributes.ATTACK_DAMAGE) * 0.5));
        }
    }

    @Inject(method = "aiStep", at = @At(value = "HEAD"))
    public void aiStep(CallbackInfo ci){
        CompoundTag pCompound = this.getPersistentData();
        if (this.isAlive())
            pCompound.putBoolean("Alife", true);
        if (!this.isAlive() && pCompound.contains("Alife") && pCompound.getBoolean("Alife")) {
            pCompound.putBoolean("Alife", false);
            List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(2.3), IS_NOT_SELF);
            for(LivingEntity entity:list)
                entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 1200, 0));
        }
        this.addAdditionalSaveData(pCompound);
        this.readAdditionalSaveData(pCompound);
    }
}
