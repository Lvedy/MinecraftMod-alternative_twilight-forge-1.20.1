package com.lvedy.alternative_twilight.mixin.Minoshroom;

import com.lvedy.alternative_twilight.ATModFinal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import twilightforest.entity.boss.Minoshroom;
import twilightforest.entity.monster.Minotaur;

import java.util.List;
import java.util.function.Predicate;

@Mixin(value = Minoshroom.class, priority = 7)
public abstract class MinoshroomMixin extends Minotaur {
    @Unique
    private static final Predicate<Entity> IS_NOT_SELF = entity -> entity.isAlive();

    public MinoshroomMixin(EntityType<? extends Minotaur> type, Level world) {
        super(type, world);
    }

    @Inject(method = "hurt", at = @At("HEAD"))
    public void hurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir){
        if (ATModFinal.MinoshroomModify == 1) {
            CompoundTag pCompound = this.getPersistentData();
            if (source.getEntity() instanceof LivingEntity && !source.is(DamageTypes.PLAYER_ATTACK) && !source.is(DamageTypes.MOB_ATTACK))
                this.setTarget((LivingEntity) source.getEntity());
            if (this.getTarget() != null && source.getEntity() == this.getTarget())
                pCompound.putFloat("ExtraDamage", (1 - ATModFinal.MinoshroomReduceDamage/100F));
            else
                pCompound.putFloat("ExtraDamage", (1 + ATModFinal.MinoshroomAddDamage/100F));
            //this.addAdditionalSaveData(pCompound);
            //this.readAdditionalSaveData(pCompound);
        }
    }

    @ModifyArg(method = "hurt", at = @At(value = "INVOKE", target = "Ltwilightforest/entity/monster/Minotaur;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"), index = 1)
    public float Hurt(float par1){
        if (ATModFinal.MinoshroomModify == 1) {
            CompoundTag pCompound = this.getPersistentData();
            if (pCompound.contains("ExtraDamage")) {
                par1 *= pCompound.getFloat("ExtraDamage");
                pCompound.putFloat("ExtraDamage", 1.0F);
            }
            //this.addAdditionalSaveData(pCompound);
            //this.readAdditionalSaveData(pCompound);
            return par1;
        }
        return par1;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        if (ATModFinal.MinoshroomModify == 1) {
            CompoundTag pCompound = this.getPersistentData();
            if (this.getHealth() <= this.getMaxHealth() * 0.01F * ATModFinal.MinoshroomHealthThreshold && (!pCompound.contains("Treat") || !pCompound.getBoolean("Treat"))) {
                pCompound.putBoolean("Treat", true);
                this.getAttribute(Attributes.ARMOR).setBaseValue(this.getAttributeBaseValue(Attributes.ARMOR) + ATModFinal.MinoshroomArmor);
                this.getAttribute(Attributes.ARMOR_TOUGHNESS).setBaseValue(this.getAttributeBaseValue(Attributes.ARMOR_TOUGHNESS) + ATModFinal.MinoshroomArmorToughness);
                this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(this.getAttributeBaseValue(Attributes.MAX_HEALTH) + ATModFinal.MinoshroomHealth);
                this.setHealth(this.getMaxHealth());
                List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(5), IS_NOT_SELF);
                for (LivingEntity entity : list) {
                    Vec3 vec3 = this.position();
                    Vec3 vec32 = entity.position().subtract(vec3);
                    Vec3 vec33 = vec32.normalize();
                    entity.push(vec33.x() * 1.5D, 0.2D, vec33.z() * 1.5D);
                }
            }
            //this.addAdditionalSaveData(pCompound);
            //this.readAdditionalSaveData(pCompound);
        }
    }
}
