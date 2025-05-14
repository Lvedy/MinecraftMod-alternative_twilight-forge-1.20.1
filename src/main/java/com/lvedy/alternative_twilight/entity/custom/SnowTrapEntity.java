package com.lvedy.alternative_twilight.entity.custom;

import com.lvedy.alternative_twilight.ATModFinal;
import com.lvedy.alternative_twilight.networking.ModMessages;
import com.lvedy.alternative_twilight.networking.SnowQueenS2CPacket;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import twilightforest.entity.boss.SnowQueen;
import twilightforest.init.TFMobEffects;

import java.util.List;
import java.util.function.Predicate;

public class SnowTrapEntity extends Mob {
    private static final Predicate<Entity> IS_NOT_SNOW = entity -> entity.isAlive() && !(entity instanceof SnowQueen) && !(entity instanceof SnowTrapEntity);
    private static final Predicate<Entity> IS_NOT_SELF = entity -> entity.isAlive() && !(entity instanceof SnowTrapEntity);
    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;
    private static final String LIFETIME_TAG = "LifeTime";
    private static final String BIG_TAG = "Big";

    public SnowTrapEntity(EntityType<? extends Mob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setNoGravity(false);
        CompoundTag tag = this.getPersistentData();
        tag.putInt(LIFETIME_TAG, 140);
        tag.putBoolean(BIG_TAG, false);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) {
            setupAnimationStates();
            particle();
        } else {
            CompoundTag tag = this.getPersistentData();
            if(this.getLifeTime() > 0)
                this.setLifetime(this.getLifeTime() - 1);
            frosty(tag);
            sendData(tag);
        }

    }

    private void setupAnimationStates(){
        if (this.idleAnimationTimeout <= 0){
            this.idleAnimationTimeout = 100;
            this.idleAnimationState.start(this.tickCount);
        }else{
            --this.idleAnimationTimeout;
        }
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (!pSource.is(DamageTypes.GENERIC_KILL))
            return false;
        super.hurt(pSource, pAmount);
        return true;
    }

    public static AttributeSupplier.Builder createAttributes(){
        return Mob.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 1000D)
                .add(Attributes.MOVEMENT_SPEED, 0D)
                .add(Attributes.FOLLOW_RANGE, 0D);
    }

    @Override
    public boolean isInvulnerable() {
        return true;
    }

    @Override
    public boolean canCollideWith(Entity pEntity) {
        return false;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void doPush(Entity p_20971_) {
    }

    @Override
    public void knockback(double strength, double xRatio, double zRatio) {
    }

    public void particle() {
        if (this.level().isClientSide) {
            ClientLevel level = (ClientLevel) this.level();

            double offsetX = this.random.nextGaussian() * 0.5;
            double offsetZ = this.random.nextGaussian() * 0.5;

            ParticleOptions particle = ParticleTypes.SNOWFLAKE;
            level.addParticle(particle,
                    this.getX() + offsetX,
                    this.getY() + 0.1,
                    this.getZ() + offsetZ,
                    0, 0.1, 0);
        }
        if (this.getLifeTime() == 1)
            for (int i = 0; i < 200; i++)
                this.level().addParticle(ParticleTypes.SNOWFLAKE, this.getX(), this.getY() + 0.1 * random.nextInt(11), this.getZ(), 0.2 - (0.04 * random.nextInt(11)), (0.08 * random.nextInt(11)), 0.2 - (0.04 * random.nextInt(11)));
    }

    public void setLifetime(int lifetime) {
        CompoundTag tag = new CompoundTag();
        tag.putInt(LIFETIME_TAG, lifetime);
        this.getPersistentData().merge(tag);
    }

    public int getLifeTime() {
        return this.getPersistentData().getInt(LIFETIME_TAG);
    }

    public void setBig(boolean big) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean(BIG_TAG, big);
        this.getPersistentData().merge(tag);
    }

    public boolean getBig() {
        return this.getPersistentData().getBoolean(BIG_TAG);
    }

    public void frosty(CompoundTag pCompound){
        int i = 1;
        if (this.getBig())
            i = ATModFinal.SnowTrapScale;
        if (pCompound.getInt(LIFETIME_TAG) == 0){
            List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, new AABB(this.position().add(-i, 0, -i), this.position().add(i, 8, i)), IS_NOT_SELF);
            for (LivingEntity entity : list) {
                if (entity instanceof SnowQueen){
                    CompoundTag tag = entity.getPersistentData();
                    tag.putInt("Explosion", 10);
                    if(tag.getInt("Frosty") > 0)
                        tag.putInt("IceArmor", 100);
                }else
                    entity.addEffect(new MobEffectInstance(TFMobEffects.FROSTY.get(), 100, 0));
            }
            this.discard();
        }
    }

    public void sendData(CompoundTag pCompound){
        if(!this.level().isClientSide()) {
            ModMessages.sendToAllPlayers(new SnowQueenS2CPacket(
                    0,
                    0,
                    0,
                    0,
                    pCompound.getInt(LIFETIME_TAG),
                    pCompound.getBoolean(BIG_TAG),
                    this.getUUID()
            ));
        }
    }

}
