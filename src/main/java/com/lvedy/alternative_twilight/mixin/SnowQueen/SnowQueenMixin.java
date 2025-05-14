package com.lvedy.alternative_twilight.mixin.SnowQueen;

import com.lvedy.alternative_twilight.ATModFinal;
import com.lvedy.alternative_twilight.entity.ModEntityTypes;
import com.lvedy.alternative_twilight.entity.custom.SnowTrapEntity;
import com.lvedy.alternative_twilight.networking.ModMessages;
import com.lvedy.alternative_twilight.networking.SnowQueenS2CPacket;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import twilightforest.entity.boss.SnowQueen;
import twilightforest.init.TFMobEffects;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

@Mixin(value = SnowQueen.class, priority = 7)
public class SnowQueenMixin extends Monster {
    @Unique
    private static final Predicate<Entity> IS_NOT_SELF = entity -> entity.isAlive() && !(entity instanceof SnowQueen) && !(entity instanceof SnowTrapEntity);

    protected SnowQueenMixin(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    public void hurt(DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir){
        CompoundTag pCompound = this.getPersistentData();
        if ((pCompound.getInt("Invincible") > 0))
            cir.setReturnValue(false);
    }

    @ModifyArg(method = "hurt",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/Monster;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"), index = 1)
    public float HurtModify(float par2){
        CompoundTag pCompound = this.getPersistentData();
        if (pCompound.getInt("Frosty") > 0 && pCompound.getInt("IceArmor") == 0)
            return par2 * (1 - ATModFinal.SnowQueenIceArmor/100F);
        return par2;
    }

    @Inject(method = "aiStep", at = @At("HEAD"))
    public void aiStep(CallbackInfo ci) {
        if (ATModFinal.SnowQueenModify == 1) {
            CompoundTag pCompound = this.getPersistentData();
            aTMod_forge_1_20_1$snowTrap(pCompound);
            aTMod_forge_1_20_1$forest(pCompound);
            aTMod_forge_1_20_1$snowExplosion(pCompound);
            aTMod_forge_1_20_1$iceArmor(pCompound);
            aTMod_forge_1_20_1$forestDamage();
            if (pCompound.getInt("IceTrap") > 0)
                pCompound.putInt("IceTrap", pCompound.getInt("IceTrap") - 1);
            if (pCompound.getInt("Frosty") > 0)
                pCompound.putInt("Frosty", pCompound.getInt("Frosty") - 1);
            if (pCompound.getInt("Invincible") > 0)
                pCompound.putInt("Invincible", pCompound.getInt("Invincible") - 1);
            aTMod_forge_1_20_1$sendData(pCompound);
        }
    }

    @Unique  //冰雪陷阱
    public void aTMod_forge_1_20_1$snowTrap(CompoundTag pCompound){
        if (!pCompound.contains("IceTrap"))
            pCompound.putInt("IceTrap", ATModFinal.SnowQueenTrapCD);
        if(this.level().isClientSide())
            return;
        if (pCompound.getInt("IceTrap") == 0) {
            int i;
            if (pCompound.getInt("Frosty") <= 0)
                i = ATModFinal.SnowQueenTrapCount_1;
            else
                i = ATModFinal.SnowQueenTrapCount_2;
            while (i > 0) {
                List<ServerPlayer> list = this.level().getEntitiesOfClass(ServerPlayer.class, this.getBoundingBox().inflate(40), IS_NOT_SELF);
                for (ServerPlayer player : list) {
                    if (player.gameMode.isSurvival()) {
                        aTMod_forge_1_20_1$spawnTrap(pCompound, player.position());
                        i --;
                    }
                }
                if (i > 0) {
                    aTMod_forge_1_20_1$spawnTrap(pCompound, this.position());
                    i--;
                }
                while (i > 0){
                    Vec3 vec3 = aTMod_forge_1_20_1$findVecInLOSOf(this.getTarget());
                    aTMod_forge_1_20_1$spawnTrap(pCompound, vec3);
                    i--;
                }
            }
            if (pCompound.getInt("Frosty") <= 0)
                pCompound.putInt("IceTrap", ATModFinal.SnowQueenTrapCD);
            else
                pCompound.putInt("IceTrap", (int)(ATModFinal.SnowQueenTrapCD * ATModFinal.SnowQueenTrapCDCD));
        }
    }

    @Unique
    public void aTMod_forge_1_20_1$spawnTrap(CompoundTag pCompound, Vec3 vec3){
        SnowTrapEntity snowTrapEntity = ModEntityTypes.SNOW_TRAP.get().create(this.level());
        if (snowTrapEntity != null) {
            if(pCompound.getInt("Frosty") > 0)
                snowTrapEntity.setBig(true);
            snowTrapEntity.moveTo(vec3.x, vec3.y, vec3.z);
            snowTrapEntity.level().addFreshEntity(snowTrapEntity);
        }
    }

    @Unique  //冰霜战甲
    public void aTMod_forge_1_20_1$iceArmor(CompoundTag pCompound){
        if(pCompound.getInt("IceArmor") > 0) {
            this.setNoGravity(false);
            BlockState blockstate = this.level().getBlockState(this.blockPosition().below());
            Block block = blockstate.getBlock();
            if(block != Blocks.AIR)
                this.setNoAi(true);
            for (int i = 0; i < 5; i++) {
                Vec3 pos = new Vec3(this.getX(), this.getY() + 2.15D, this.getZ()).add(new Vec3(1.5D, 0, 0).yRot((float) Math.toRadians(this.getRandom().nextInt(360))));
                this.level().addParticle(ParticleTypes.CRIT, pos.x(), pos.y(), pos.z(), 0, 0, 0);
            }
            pCompound.putInt("IceArmor", pCompound.getInt("IceArmor") -1);
            if(pCompound.getInt("IceArmor") == 0) {
                this.setNoGravity(true);
                this.setNoAi(false);
            }
        }
    }

    @Unique //降温
    public void aTMod_forge_1_20_1$forest(CompoundTag pCompound){
        if (pCompound.getInt("Invincible") > ATModFinal.SnowQueenInvincible + 60) {
            this.setHealth(this.getMaxHealth() * 0.01F * ATModFinal.SnowQueenHealthThreshold);
            this.setNoAi(true);
        }else if(pCompound.getInt("Invincible") > ATModFinal.SnowQueenInvincible) {
            this.setHealth(this.getHealth() + (ATModFinal.SnowQueenTreat / 60F));
            if(random.nextInt(10) == 1)
                aTMod_forge_1_20_1$snowParticle(1);
        }
        if (pCompound.getInt("Invincible") == ATModFinal.SnowQueenInvincible) {
            aTMod_forge_1_20_1$snowParticle(0);
            this.setNoAi(false);
            List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(40), IS_NOT_SELF);
            for (LivingEntity entity : list)
                entity.addEffect(new MobEffectInstance(TFMobEffects.FROSTY.get(), ATModFinal.SnowQueenIceDuration, 0));
        }
        if (this.getHealth() <= this.getMaxHealth() * 0.01F * ATModFinal.SnowQueenHealthThreshold && pCompound.getInt("Frosty") <= 0 && this.isAlive()) {
            pCompound.putInt("Frosty", ATModFinal.SnowQueenIceCD);
            pCompound.putInt("Invincible", ATModFinal.SnowQueenInvincible + 120);
        }
    }

    @Unique //破冰
    public void aTMod_forge_1_20_1$forestDamage(){
        List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(40), IS_NOT_SELF);
        for (LivingEntity entity : list) {
            MobEffectInstance mobEffectInstance = entity.getEffect(TFMobEffects.FROSTY.get());
            if (mobEffectInstance != null)
                entity.hurt(this.damageSources().magic(), entity.getMaxHealth() * 0.01F * ATModFinal.SnowQueenDamage);
        }
    }

    @Unique //冰爆
    public void aTMod_forge_1_20_1$snowExplosion(CompoundTag pCompound){
        if (!pCompound.contains("Explosion"))
            pCompound.putInt("Explosion", 0);
        else if (pCompound.getInt("Explosion") == 1) {
            aTMod_forge_1_20_1$snowParticle(0);
            List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(10), IS_NOT_SELF);
            for (LivingEntity entity : list) {
                MobEffectInstance mobEffectInstance = entity.getEffect(TFMobEffects.FROSTY.get());
                if (mobEffectInstance != null)
                    entity.hurt(this.damageSources().magic(), (float) (Objects.requireNonNull(this.getAttribute(Attributes.ATTACK_DAMAGE)).getBaseValue() * ATModFinal.SnowQueenExplosionDamage));
                else
                    entity.addEffect(new MobEffectInstance(TFMobEffects.FROSTY.get(), 40, 0));
            }
        }
        if (pCompound.getInt("Explosion") > 0)
            pCompound.putInt("Explosion", pCompound.getInt("Explosion") - 1);
    }

    @Unique
    public void aTMod_forge_1_20_1$snowParticle(int type) {
        if (type == 0) {
            for (int i = 0; i < 200; i++)
                this.level().addParticle(ParticleTypes.SNOWFLAKE, this.getX(), this.getY() + 0.1 * random.nextInt(11), this.getZ(), 0.5 - (0.1 * random.nextInt(11)), 0.5 - (0.15 * random.nextInt(11)), 0.5 - (0.1 * random.nextInt(11)));
        }
        if (type == 1) {
            for (int i = 0; i < 40; i++) {
                Vec3 vec3 = new Vec3(this.getX() + 0.5 * random.nextInt(11) - 0.5, this.getY() + 0.5 * random.nextInt(11), this.getZ() + 0.5 * random.nextInt(11) - 0.5);
                this.level().addParticle(ParticleTypes.ITEM_SNOWBALL, vec3.x, vec3.y, vec3.z, 0.5 - (0.1 * random.nextInt(11)), 0.5 - (0.1 * random.nextInt(11)), 0.5 - (0.1 * random.nextInt(11)));
            }
        }
    }

    @Unique  //同步NBT数据
    public void aTMod_forge_1_20_1$sendData(CompoundTag pCompound){
        if(!this.level().isClientSide()) {
            ModMessages.sendToAllPlayers(new SnowQueenS2CPacket(
                pCompound.getInt("Invincible"),
                pCompound.getInt("Frosty"),
                pCompound.getInt("IceArmor"),
                pCompound.getInt("Explosion"),
                0,
                false,
                this.getUUID()
            ));
        }
    }

    /**
     * Returns coords that would be good to teleport to.
     * Returns null if we can't find anything
     */
    @Unique
    @Nullable
    public Vec3 aTMod_forge_1_20_1$findVecInLOSOf(@Nullable Entity targetEntity) {
        if (targetEntity == null) return this.position().add(1-random.nextInt(7),0,1-random.nextInt(7));
        double origX = this.getX();
        double origY = this.getY();
        double origZ = this.getZ();

        int tries = 100;
        for (int i = 0; i < tries; i++) {
            // we abuse LivingEntity.attemptTeleport, which does all the collision/ground checking for us, then teleport back to our original spot
            double tx = targetEntity.getX() + this.getRandom().nextGaussian() * 16D;
            double ty = targetEntity.getY();
            double tz = targetEntity.getZ() + this.getRandom().nextGaussian() * 16D;

            boolean destClear = this.randomTeleport(tx, ty, tz, true);
            boolean canSeeTargetAtDest = this.hasLineOfSight(targetEntity); // Don't use senses cache because we're in a temporary position
            this.teleportTo(origX, origY, origZ);

            if (destClear && canSeeTargetAtDest) {
                return new Vec3(tx, ty, tz);
            }
        }
        return this.position().add(1+random.nextInt(7),0,1+random.nextInt(7));
    }
}
