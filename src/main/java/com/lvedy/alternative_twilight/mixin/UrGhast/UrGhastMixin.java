package com.lvedy.alternative_twilight.mixin.UrGhast;

import com.lvedy.alternative_twilight.ATModFinal;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import twilightforest.entity.boss.UrGhast;
import twilightforest.entity.monster.CarminiteGhastguard;
import twilightforest.network.ParticlePacket;

import java.util.List;
import java.util.function.Predicate;

@Mixin(value = UrGhast.class, priority = 7)
public class UrGhastMixin extends CarminiteGhastguard {
    @Unique
    private final Predicate<Entity> IS_NOT_SELF = entity -> entity instanceof Player || entity instanceof Animal || (entity instanceof Mob && ((Mob)entity).getMaxHealth()<=this.getMaxHealth()*0.5F);

    public UrGhastMixin(EntityType<? extends CarminiteGhastguard> type, Level world) {
        super(type, world);
    }

    @Inject(method = "hurt", at = @At(value = "HEAD"))
    public void Hurt(DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir){
        if (ATModFinal.UrGhastModify == 1) {
            CompoundTag pCompound = this.getPersistentData();
            if (source.is(DamageTypes.EXPLOSION))
                pCompound.putBoolean("WasExplosion", true);
            this.addAdditionalSaveData(pCompound);
            this.readAdditionalSaveData(pCompound);
        }
    }

    @ModifyArg(method = "hurt", at = @At(value = "INVOKE", target = "Ltwilightforest/entity/monster/CarminiteGhastguard;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"), index = 1)
    public float hurt(float par2){
        if (ATModFinal.UrGhastModify == 1) {
            CompoundTag pCompound = this.getPersistentData();
            if (pCompound.contains("WasExplosion") && pCompound.getBoolean("WasExplosion")) {
                pCompound.putBoolean("WasExplosion", false);
                par2 = Math.max(ATModFinal.UrGhastDamageMin, par2) * (1 + (0.01F * ATModFinal.UrGhastAddDamage));
            }
            this.addAdditionalSaveData(pCompound);
            this.readAdditionalSaveData(pCompound);
            return par2;
        }
        return par2;
    }

    @Inject(method = "aiStep", at = @At(value = "HEAD"))
    public void aiStep(CallbackInfo ci) {
        if (ATModFinal.UrGhastModify == 1) {
            CompoundTag pCompound = this.getPersistentData();
            if (this.isAlive())
                pCompound.putInt("DeadTime", 0);
            if (!this.isAlive() && pCompound.contains("DeadTime")) {
                int DeadTime = pCompound.getInt("DeadTime");
                pCompound.putInt("DeadTime", DeadTime + 1);
                ParticlePacket particlePacket = new ParticlePacket();
                for (int i = 0; i < 360; i++) {
                    Vec3 vec3 = this.position();
                    Vec3 vec31 = vec3.add(Math.cos(i) * (15 - 0.3 * DeadTime), 8, Math.sin(i) * (15 - 0.2 * DeadTime));
                    Vec3 vec32 = vec3.add(Math.cos(i) * (10 - 0.2 * DeadTime), 16, Math.sin(i) * (10 - 0.2 * DeadTime));
                    Vec3 vec33 = vec3.add(Math.cos(i) * (10 - 0.2 * DeadTime), 0, Math.sin(i) * (10 - 0.2 * DeadTime));
                    this.level().addParticle(ParticleTypes.FLAME, vec31.x, vec31.y, vec31.z, 0, 0, 0);
                    this.level().addParticle(ParticleTypes.FLAME, vec32.x, vec32.y, vec32.z, 0, 0, 0);
                    this.level().addParticle(ParticleTypes.FLAME, vec33.x, vec33.y, vec33.z, 0, 0, 0);
                }
                if (DeadTime >= 70) {
                    List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(140), IS_NOT_SELF);
                    for (LivingEntity entity : list) {
                        for (int i = 0; i < 35; i++) {
                            Vec3 vec3 = entity.position().add(0.15 * (this.getRandom().nextInt(11) - 5), 0.2 * (this.getRandom().nextInt(11) - 5), 0.15 * (this.getRandom().nextInt(11) - 5));
                            this.level().addParticle(ParticleTypes.FLAME, vec3.x, vec3.y, vec3.z, 0, 0, 0);
                        }
                        entity.hurt(this.damageSources().magic(), entity.getMaxHealth() * 0.01F * ATModFinal.UrGhastBomb);
                    }
                }
            }
            this.addAdditionalSaveData(pCompound);
            this.readAdditionalSaveData(pCompound);
        }
    }
}
