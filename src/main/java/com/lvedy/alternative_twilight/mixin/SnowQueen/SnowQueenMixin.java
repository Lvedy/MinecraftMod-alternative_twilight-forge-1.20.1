package com.lvedy.alternative_twilight.mixin.SnowQueen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import twilightforest.entity.boss.SnowQueen;
import twilightforest.init.TFMobEffects;

import java.util.List;
import java.util.function.Predicate;

@Mixin(SnowQueen.class)
public class SnowQueenMixin extends Monster {
    @Unique
    private static final Predicate<Entity> IS_NOT_SELF = entity -> entity.isAlive() && !(entity instanceof SnowQueen);

    protected SnowQueenMixin(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "aiStep", at = @At("HEAD"))
    public void aiStep(CallbackInfo ci){
        CompoundTag pCompound = this.getPersistentData();   //冰冻陷阱
        if(!pCompound.contains("IceTrap"))
            pCompound.putInt("IceTrap", 800);
        else if (pCompound.getInt("IceTrap") == 700){
            LivingEntity entity = null;
            List<ServerPlayer> list = this.level().getEntitiesOfClass(ServerPlayer.class, this.getBoundingBox().inflate(40), IS_NOT_SELF);
            for (ServerPlayer player : list){
                if(player.gameMode.isSurvival())
                    entity = player;
                break;
            }
            if(entity == null)
                entity = this;
            int[] arr = {(int) entity.getX(), (int) entity.getY(), (int) entity.getZ()};
            pCompound.putIntArray("TrapLocation", arr);
            pCompound.putInt("IceTrap", 699);
        }
        else if (560 < pCompound.getInt("IceTrap") && pCompound.getInt("IceTrap") < 700){
            int[] arr = pCompound.getIntArray("TrapLocation");
            Vec3 vec3 =new Vec3(arr[0], arr[1], arr[2]);
            RandomSource random = this.getRandom();
            for(int i = 0 ; i < (pCompound.getInt("IceTrap") - 560) * 0.15 ; i++)
                this.level().addParticle(ParticleTypes.SNOWFLAKE, vec3.x, vec3.y, vec3.z, 0.1*random.nextInt(5), 0.4*random.nextInt(5), 0.1*random.nextInt(5));
            pCompound.putInt("IceTrap", pCompound.getInt("IceTrap") - 1);
        }
        else if (pCompound.getInt("IceTrap") == 560){
            int[] arr = pCompound.getIntArray("TrapLocation");
            Vec3 vec3 =new Vec3(arr[0], arr[1], arr[2]);
            List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, new AABB(vec3.add(-1,0,-1), vec3.add(1, 8, 1)), IS_NOT_SELF);
            for (LivingEntity entity : list){
                entity.addEffect(new MobEffectInstance(TFMobEffects.FROSTY.get(), 100, 0));
            }
            pCompound.putInt("IceTrap", 559);
        }
        else if (pCompound.getInt("IceTrap") == 0)
            pCompound.putInt("IceTrap", 800);
        else
            pCompound.putInt("IceTrap", pCompound.getInt("IceTrap") - 1);
        //降温
        if(this.getHealth() <= this.getMaxHealth() * 0.5F && (!pCompound.contains("Frosty") || pCompound.getInt("Frosty") <= 0)){
            pCompound.putInt("Frosty", 2400);
            this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 4));
            List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(40), IS_NOT_SELF);
            for (LivingEntity entity : list)
                entity.addEffect(new MobEffectInstance(TFMobEffects.FROSTY.get(), 140, 0));
        }
        if (pCompound.getInt("Frosty") > 0)
            pCompound.putInt("Frosty", pCompound.getInt("Frosty") - 1);   //破冰
        List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(40), IS_NOT_SELF);
        for (LivingEntity entity : list) {
            MobEffectInstance mobEffectInstance = entity.getEffect(TFMobEffects.FROSTY.get());
            if (mobEffectInstance != null)
                entity.hurt(this.damageSources().magic(), entity.getMaxHealth() * 0.02F);
        }
        this.addAdditionalSaveData(pCompound);
        this.readAdditionalSaveData(pCompound);
    }
}
