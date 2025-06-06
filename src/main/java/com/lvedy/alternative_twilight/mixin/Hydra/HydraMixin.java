package com.lvedy.alternative_twilight.mixin.Hydra;

import com.lvedy.alternative_twilight.ATModFinal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import twilightforest.entity.boss.Hydra;

import java.util.List;
import java.util.function.Predicate;

@Mixin(value = Hydra.class, priority = 7)
public abstract class HydraMixin extends Mob {
    @Shadow public abstract boolean hurt(DamageSource src, float damage);

    @Unique
    private static final Predicate<Entity> IS_NOT_SELF = entity -> entity.isAlive();

    protected HydraMixin(EntityType<? extends Mob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "aiStep", at=@At("HEAD"))
    public void aiStep(CallbackInfo ci){
        if (ATModFinal.HydraModify == 1) {
            CompoundTag pCompound = this.getPersistentData();
            if (!pCompound.contains("BombTime"))
                pCompound.putInt("BombTime", ATModFinal.HydraBomb);
            List<Player> list = this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(40), IS_NOT_SELF);
            for (Player player : list) {
                if (pCompound.getInt("BombTime") == 0 && player.isOnFire()) {
                    player.level().explode(this, player.getX(), player.getY(), player.getZ(), 2.0F, false, Level.ExplosionInteraction.MOB);
                    pCompound.putInt("BombTime", ATModFinal.HydraBomb);
                } else if (player.isOnFire()) {
                    pCompound.putInt("BombTime", pCompound.getInt("BombTime") - 1);
                    break;
                }
            }
            int i = pCompound.getByte("NumHeads");
            if (i >= ATModFinal.HydraHead)
                this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 140, i - ATModFinal.HydraHead));
        }
    }

    @Inject(method = "hurt", at = @At("HEAD"))
    public void hurt(DamageSource src, float damage, CallbackInfoReturnable<Boolean> cir){
        if (ATModFinal.HydraModify == 1) {
            CompoundTag pCompound = this.getPersistentData();
            if (src.getEntity() instanceof Player) {
                float luck = ((Player) src.getEntity()).getLuck();
                float i = 0.01F * ATModFinal.HydraAddDamage * luck;
                pCompound.putFloat("ExtraDamage", (1 + i));
            }
        }
    }

    @ModifyArg(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"), index = 1)
    public float Hurt(float par1) {
        if (ATModFinal.HydraModify == 1) {
            CompoundTag pCompound = this.getPersistentData();
            if (pCompound.contains("ExtraDamage")) {
                par1 *= pCompound.getFloat("ExtraDamage");
                pCompound.putFloat("ExtraDamage", 1.0F);
            }
            return par1;
        }
        return par1;
    }
}
