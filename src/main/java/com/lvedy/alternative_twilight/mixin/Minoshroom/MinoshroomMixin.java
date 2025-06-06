package com.lvedy.alternative_twilight.mixin.Minoshroom;

import com.lvedy.alternative_twilight.ATModFinal;
import com.lvedy.alternative_twilight.networking.MinoshroomS2CPacket;
import com.lvedy.alternative_twilight.networking.ModMessages;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import twilightforest.entity.boss.Minoshroom;
import twilightforest.entity.monster.Minotaur;
import twilightforest.init.TFSounds;

import java.util.List;
import java.util.function.Predicate;

@Mixin(value = Minoshroom.class, priority = 7)
public abstract class MinoshroomMixin extends Minotaur {
    @Unique
    private static final Predicate<Entity> IS_NOT_SELF = entity -> entity.isAlive();
    @Unique
    private static final String BASE_ATTACK_TAG = "BaseAttack";
    @Unique
    private static final String BASE_MOVEMENT_SPEED_TAG = "BaseMovementSpeed";
    @Unique
    private static final String TRACKING_TIME_TAG = "TrackingTime";
    @Unique
    private static final String CURRENT_TARGET_TAG = "CurrentTarget";
    @Unique
    private static final String TARGET_SWITCH_COOLDOWN_TAG = "TargetSwitchCooldown";
    @Unique
    private static final String CAN_SWITCH_TARGET_TAG = "CanSwitchTarget";

    public MinoshroomMixin(EntityType<? extends Minotaur> type, Level world) {
        super(type, world);
    }
    
    @Shadow
    public abstract void setMaxCharge(int charge);

    @Inject(method = "hurt", at = @At("HEAD"))
    public void hurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir){
        if (ATModFinal.MinoshroomModify == 1) {
            CompoundTag pCompound = this.getPersistentData();
            
            // 处理远程攻击目标转移逻辑
            if (source.getEntity() instanceof LivingEntity && !source.is(DamageTypes.PLAYER_ATTACK) && !source.is(DamageTypes.MOB_ATTACK)) {
                // 检查是否可以转移目标（冷却时间已结束）
                boolean canSwitchTarget = pCompound.contains(CAN_SWITCH_TARGET_TAG) ? 
                                         pCompound.getBoolean(CAN_SWITCH_TARGET_TAG) : true;
                
                if (canSwitchTarget) {
                    // 设置新目标
                    this.setTarget((LivingEntity) source.getEntity());
                    // 设置冷却时间
                    pCompound.putInt(TARGET_SWITCH_COOLDOWN_TAG, ATModFinal.MinoshroomTargetSwitchCooldown);
                    // 设置不能再次转移目标
                    pCompound.putBoolean(CAN_SWITCH_TARGET_TAG, false);
                }
            }
            
            // 原有的伤害调整逻辑
            if (this.getTarget() != null && source.getEntity() == this.getTarget())
                pCompound.putFloat("ExtraDamage", (1 - ATModFinal.MinoshroomReduceDamage/100F));
            else
                pCompound.putFloat("ExtraDamage", (1 + ATModFinal.MinoshroomAddDamage/100F));
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
            return par1;
        }
        return par1;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        if (ATModFinal.MinoshroomModify == 1) {
            CompoundTag pCompound = this.getPersistentData();
            // 初始化基础属性
            aTMod_forge_1_20_1$initializeBaseAttributes(pCompound);
            
            // 战吼
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
                
                // 战吼破坏方块效果
                if (ATModFinal.MinoshroomWarcryBreakBlocks == 1 && !this.level().isClientSide())
                    aTMod_forge_1_20_1$breakBlocksAround();
            }
            aTMod_forge_1_20_1$updateTargetSwitchCooldown(pCompound);
            // 客户端效果
            if (this.level().isClientSide()) {
                // 根据追踪时间生成粒子效果
                aTMod_forge_1_20_1$spawnParticlesBasedOnTrackingTime(pCompound.getInt(TRACKING_TIME_TAG));
                
                // 检查是否刚刚可以转移目标，如果是，播放愤怒粒子效果
                if (pCompound.contains(CAN_SWITCH_TARGET_TAG) && pCompound.getBoolean(CAN_SWITCH_TARGET_TAG) && 
                    pCompound.contains(TARGET_SWITCH_COOLDOWN_TAG) && pCompound.getInt(TARGET_SWITCH_COOLDOWN_TAG) == 1) {
                    aTMod_forge_1_20_1$spawnAngerParticles();
                }
            }else{
                aTMod_forge_1_20_1$sendDataToClient(pCompound);
                aTMod_forge_1_20_1$updateTrackingBuffs(pCompound);
            }
        }
    }
    
    @Unique
    private void aTMod_forge_1_20_1$initializeBaseAttributes(CompoundTag pCompound) {
        // 只在第一次初始化基础属性
        if (!pCompound.contains(BASE_ATTACK_TAG)) {
            AttributeInstance attackAttribute = this.getAttribute(Attributes.ATTACK_DAMAGE);
            AttributeInstance movementSpeedAttribute = this.getAttribute(Attributes.MOVEMENT_SPEED);
            if (attackAttribute != null && movementSpeedAttribute != null) {
                double baseAttack = attackAttribute.getBaseValue();
                double baseMovementSpeed = movementSpeedAttribute.getBaseValue();
                pCompound.putDouble(BASE_ATTACK_TAG, baseAttack);
                pCompound.putDouble(BASE_MOVEMENT_SPEED_TAG, baseMovementSpeed);
                pCompound.putInt(TRACKING_TIME_TAG, 0);
            }
        }
    }
    
    @Unique
    private void aTMod_forge_1_20_1$updateTrackingBuffs(CompoundTag pCompound) {
        LivingEntity currentTarget = this.getTarget();
        
        // 如果没有目标，重置追踪时间
        if (currentTarget == null)
            aTMod_forge_1_20_1$resetTrackingBuffs(pCompound, true);
        else {
            // 检查目标是否变更
            if (pCompound.contains(CURRENT_TARGET_TAG)) {
                String savedTargetUUID = pCompound.getString(CURRENT_TARGET_TAG);
                String currentTargetUUID = currentTarget.getStringUUID();

                if (!savedTargetUUID.equals(currentTargetUUID)) {
                    // 目标变更，减少80%的增益
                    aTMod_forge_1_20_1$resetTrackingBuffs(pCompound, false);
                    pCompound.putString(CURRENT_TARGET_TAG, currentTargetUUID);
                }
            } else {
                // 首次设置目标
                pCompound.putString(CURRENT_TARGET_TAG, currentTarget.getStringUUID());
            }

            // 增加追踪时间，最大不超过配置的最大值
            int currentTrackingTime = pCompound.getInt(TRACKING_TIME_TAG);
            if (currentTrackingTime < ATModFinal.MinoshroomTrackingMaxTime) {
                currentTrackingTime++;
                pCompound.putInt(TRACKING_TIME_TAG, currentTrackingTime);

                // 更新属性
                aTMod_forge_1_20_1$updateAttributesBasedOnTrackingTime(pCompound, currentTrackingTime);
            }
        }
    }
    
    @Unique
    private void aTMod_forge_1_20_1$resetTrackingBuffs(CompoundTag pCompound, boolean fullReset) {
        int currentTrackingTime = pCompound.getInt(TRACKING_TIME_TAG);
        
        if (fullReset) {
            // 完全重置
            pCompound.putInt(TRACKING_TIME_TAG, 0);
            if (pCompound.contains(CURRENT_TARGET_TAG)) {
                pCompound.remove(CURRENT_TARGET_TAG);
            }
        } else {
            // 减少80%的追踪时间
            int newTrackingTime = (int) (currentTrackingTime * (1 - ATModFinal.MinoshroomBuffLossPercent / 100.0));
            pCompound.putInt(TRACKING_TIME_TAG, newTrackingTime);
        }
        
        // 重置属性到基础值
        aTMod_forge_1_20_1$updateAttributesBasedOnTrackingTime(pCompound, pCompound.getInt(TRACKING_TIME_TAG));
    }
    
    @Unique
    private void aTMod_forge_1_20_1$updateAttributesBasedOnTrackingTime(CompoundTag pCompound, int trackingTime) {
        if (!pCompound.contains(BASE_ATTACK_TAG)) {
            return; // 基础属性未初始化
        }
        
        double baseAttack = pCompound.getDouble(BASE_ATTACK_TAG);
        double baseMovementSpeed = pCompound.getDouble(BASE_MOVEMENT_SPEED_TAG);
        
        // 计算线性增长的属性值
        double progressFactor = Math.min(1.0, trackingTime / (double) ATModFinal.MinoshroomTrackingMaxTime);
        
        double attackMultiplier = 1.0 + (ATModFinal.MinoshroomAttackMultiplier - 100) / 100.0 * progressFactor;
        double movementSpeedMultiplier = 1.0 + (ATModFinal.MinoshroomSpeedMultiplier - 100) / 100.0 * progressFactor;
        
        // 更新属性
        AttributeInstance attackAttribute = this.getAttribute(Attributes.ATTACK_DAMAGE);
        AttributeInstance movementSpeedAttribute = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attackAttribute != null) {
            attackAttribute.setBaseValue(((baseAttack + aTMod_forge_1_20_1$getWeaponAttackDamage(this.getMainHandItem())) * attackMultiplier) - aTMod_forge_1_20_1$getWeaponAttackDamage(this.getMainHandItem()));
        }
        if (movementSpeedAttribute != null) {
            movementSpeedAttribute.setBaseValue(baseMovementSpeed * movementSpeedMultiplier);
        }
    }
    
    @Unique
    private void aTMod_forge_1_20_1$spawnParticlesBasedOnTrackingTime(int trackingTime) {
        // 根据追踪时间生成越来越多的暗红色药水粒子
        if (trackingTime > 0) {
            int particleCount = (int) (trackingTime / 100.0) + 1; // 每100tick增加一个粒子
            
            for (int i = 0; i < particleCount; i++) {
                double offsetX = this.random.nextGaussian() * 0.2;
                double offsetY = this.random.nextGaussian() * 0.2 + 1.0;
                double offsetZ = this.random.nextGaussian() * 0.2;
                
                this.level().addParticle(
                    ParticleTypes.ENTITY_EFFECT,
                    this.getX() + offsetX,
                    this.getY() + offsetY,
                    this.getZ() + offsetZ,
                    0.5, // 红色
                    0.0, // 绿色
                    0.0  // 蓝色
                );
            }
        }
    }
    
    @Unique
    private void aTMod_forge_1_20_1$updateTargetSwitchCooldown(CompoundTag pCompound) {
        // 初始化目标转移冷却时间
        if (!pCompound.contains(TARGET_SWITCH_COOLDOWN_TAG)) {
            pCompound.putInt(TARGET_SWITCH_COOLDOWN_TAG, 0);
            pCompound.putBoolean(CAN_SWITCH_TARGET_TAG, true);
        }
        
        // 处理冷却时间
        int cooldown = pCompound.getInt(TARGET_SWITCH_COOLDOWN_TAG);
        boolean canSwitchTarget = pCompound.getBoolean(CAN_SWITCH_TARGET_TAG);
        
        if (cooldown > 0) {
            cooldown--;
            pCompound.putInt(TARGET_SWITCH_COOLDOWN_TAG, cooldown);
            
            // 冷却结束时
            if (cooldown == 0 && !canSwitchTarget) {
                pCompound.putBoolean(CAN_SWITCH_TARGET_TAG, true);
                
                // 在服务端播放声音
                if (!this.level().isClientSide()) {
                    this.level().playSound(null, this.getX(), this.getY(), this.getZ(), 
                                         TFSounds.MINOSHROOM_SLAM.get(), SoundSource.HOSTILE,
                                         1.0F, 0.5F);
                }
            }
        }
    }
    
    @Unique
    private void aTMod_forge_1_20_1$spawnAngerParticles() {
        // 生成愤怒粒子效果
        for (int i = 0; i < 20; i++) { // 生成20个粒子
            double offsetX = this.random.nextGaussian() * 0.3;
            double offsetY = this.random.nextGaussian() * 0.3 + 1.0;
            double offsetZ = this.random.nextGaussian() * 0.3;
            
            this.level().addParticle(
                ParticleTypes.ANGRY_VILLAGER, // 愤怒粒子
                this.getX() + offsetX,
                this.getY() + offsetY,
                this.getZ() + offsetZ,
                0.0, 0.0, 0.0
            );
        }
    }
    
    @Unique
    private void aTMod_forge_1_20_1$sendDataToClient(CompoundTag pCompound) {
        // 只在服务端执行，将数据发送到所有客户端
        if (!this.level().isClientSide()) {
            int trackingTime = pCompound.getInt(TRACKING_TIME_TAG);
            int targetSwitchCooldown = pCompound.getInt(TARGET_SWITCH_COOLDOWN_TAG);
            ModMessages.sendToAllPlayers(new MinoshroomS2CPacket(trackingTime, targetSwitchCooldown, this.getUUID()));
        }
    }
    
    @Unique
    private void aTMod_forge_1_20_1$breakBlocksAround() {
        // 战吼破坏方块效果
        // 定义破坏范围
        int radius = ATModFinal.MinoshroomBreakBlocksRadius; // 水平方向的半径
        int height = ATModFinal.MinoshroomBreakBlocksHeight; // 垂直方向的高度
        BlockPos center = this.blockPosition();
        
        // 遍历范围内的所有方块
        for (int x = -radius; x <= radius; x++) {
            for (int y = -1; y <= height; y++) {
                for (int z = -radius; z <= radius; z++) {
                    // 计算当前方块的位置
                    BlockPos pos = center.offset(x, y, z);
                    
                    // 计算到中心的距离（只考虑水平方向）
                    double distSq = (x * x) + (z * z);
                    
                    // 只破坏在圆形范围内的方块
                    if (distSq <= radius * radius) {
                        // 获取方块状态
                        BlockState state = this.level().getBlockState(pos);
                        
                        // 检查方块是否可以被破坏（不是基岩、不是空气等）
                        // 这里可以根据需要添加更多条件
                        if (!state.isAir() && 
                            state.getDestroySpeed(this.level(), pos) >= 0 && 
                            state.getDestroySpeed(this.level(), pos) < 50) { // 不破坏太硬的方块
                            
                            // 破坏方块并播放效果
                            this.level().destroyBlock(pos, true); // true表示掉落物品
                        }
                    }
                }
            }
        }
        
        // 播放战吼音效
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), 
                             TFSounds.MINOSHROOM_SLAM.get(), SoundSource.HOSTILE,
                             1.5F, 0.8F);
    }

    @Unique
    private static float aTMod_forge_1_20_1$getWeaponAttackDamage(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof SwordItem sword) {
            return sword.getDamage(); // 剑的基础伤害
        } else if (item instanceof AxeItem axe) {
            return axe.getAttackDamage(); // 斧的基础伤害
        }
        return 0.0F; // 不是武器，返回 0
    }
}
