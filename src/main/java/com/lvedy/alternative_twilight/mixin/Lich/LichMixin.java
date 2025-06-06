package com.lvedy.alternative_twilight.mixin.Lich;

import com.lvedy.alternative_twilight.ATModFinal;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import twilightforest.entity.boss.Lich;
import twilightforest.entity.monster.LichMinion;
import twilightforest.init.TFEntities;

import java.util.List;
import java.util.function.Predicate;

@Mixin(value = Lich.class, priority = 7)
public abstract class LichMixin extends Monster {
    @Unique
    private static final Predicate<Entity> IS_NOT_SELF = Entity::isAlive;
    @Unique
    int j = 0, k = 0;
    @Shadow public abstract int getPhase();

    @Shadow public abstract void setScepterTime();

    protected LichMixin(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "hurt", at = @At(value = "HEAD"))
    public void hurt(DamageSource src, float damage, CallbackInfoReturnable<Boolean> cir){
        if (ATModFinal.LichModify == 1) {
            CompoundTag pCompound = this.getPersistentData();        //被攻击取消黑曜石囚笼回血
            if (ATModFinal.LichObsidianSwitch == 1 && this.getPhase() == 1 && pCompound.contains("Obsidian") && pCompound.getFloat("Obsidian") > 0)
                pCompound.putFloat("Obsidian", 0);
            if (pCompound.contains("ExtraArmor") && pCompound.getFloat("ExtraArmor") > 0)//护体法术期间被攻击失去额外防御
                pCompound.putFloat("ExtraArmor", pCompound.getFloat("ExtraArmor") - 1);
            //this.addAdditionalSaveData(pCompound);
            //this.readAdditionalSaveData(pCompound);
        }
    }

    @ModifyArg(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/Monster;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"), index = 1)
    public float Hurt(float par2){
        if (ATModFinal.LichModify == 1) {
            CompoundTag pCompound = this.getPersistentData();
            if (pCompound.contains("resistance"))
                par2 = (float) (par2 * (1 - Math.max(Math.min(0.2 * pCompound.getInt("resistance"), 0.8), -0.4)));
            if (this.getPhase() != 1 && pCompound.contains("ExtraArmor") && pCompound.getFloat("ExtraArmor") > 0)
                par2 = par2 * (1 - ATModFinal.MagicReduceDamage/100F);
            return par2;
        }
        return par2;
    }

    @Inject(method = "aiStep", at = @At(value = "HEAD"))
    public void aiStep(CallbackInfo ci) {
        if (ATModFinal.LichModify == 1) {
            CompoundTag pCompound = this.getPersistentData();
            //护体法术
            if (this.getPhase() != 1 && this.getHealth() <= (this.getMaxHealth() * (ATModFinal.MagicHealth/100F))) {
                if (!pCompound.contains("ArmorTime") || pCompound.getFloat("ArmorTime") <= 0) {
                    pCompound.putFloat("ExtraArmor", ATModFinal.ExtraArmor);
                    pCompound.putFloat("ArmorTime", ATModFinal.MagicCD);
                }
            }
            if (pCompound.contains("ArmorTime") && pCompound.getFloat("ArmorTime") > ATModFinal.MagicCD - ATModFinal.ExtraTime) {
                this.getAttribute(Attributes.ARMOR).setBaseValue(pCompound.getFloat("ExtraArmor") + j);
                this.getAttribute(Attributes.ARMOR_TOUGHNESS).setBaseValue(pCompound.getFloat("ExtraArmor") + k);
            } else if (pCompound.contains("ArmorTime") && pCompound.getFloat("ExtraArmor") > 0) {
                this.setHealth(this.getMaxHealth());
                if (pCompound.contains("resistance"))
                    pCompound.putInt("resistance", pCompound.getInt("resistance") + 1);
                else
                    pCompound.putInt("resistance", 1);
                pCompound.putFloat("ExtraArmor", 0);
            }
            if (pCompound.contains("ArmorTime") && pCompound.getFloat("ExtraArmor") == 1 && pCompound.getFloat("ArmorTime") > 2400) {
                if (pCompound.contains("resistance"))
                    pCompound.putInt("resistance", pCompound.getInt("resistance") - 1);
                else
                    pCompound.putInt("resistance", -1);
                pCompound.putFloat("ExtraArmor", 0);
            }
            if (pCompound.contains("ArmorTime") && pCompound.getFloat("ArmorTime") > 0)
                pCompound.putFloat("ArmorTime", pCompound.getFloat("ArmorTime") - 1);
            //第三阶段
            if (this.getPhase() == 3 && pCompound.getFloat("Obsidian") >= 0) {
                j = ATModFinal.LichArmor;
                k = ATModFinal.LichArmorToughness;
                this.getAttribute(Attributes.ARMOR).setBaseValue(j);
                this.getAttribute(Attributes.ARMOR_TOUGHNESS).setBaseValue(k);
                this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(this.getAttribute(Attributes.MAX_HEALTH).getBaseValue() + ATModFinal.LichMaxHealth);
                this.setHealth(this.getMaxHealth());
                pCompound.putFloat("Obsidian", -20);
                pCompound.putInt("SummonMinion", 100);
                pCompound.putInt("TreatTime", 2200);
            }  //献上心脏
            if (this.getPhase() != 1 && pCompound.getInt("TreatTime") > 0) {
                pCompound.putInt("TreatTime", pCompound.getInt("TreatTime") - 1);
                if (pCompound.getInt("TreatTime") == 0) {
                    List<LichMinion> list = this.level().getEntitiesOfClass(LichMinion.class, this.getBoundingBox().inflate(40), IS_NOT_SELF);
                    for (LichMinion minion : list)
                        minion.kill();
                    this.setHealth(Math.min(this.getHealth() + (list.size() * (this.getMaxHealth() * 0.01F * ATModFinal.MinionTreatLich)), this.getMaxHealth()));
                    this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() + list.size());
                /*List<Player> list2 = this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(20), IS_NOT_SELF);
                for(Player player:list2) {
                    if (player.level().isClientSide()) {
                        player.sendSystemMessage(Component.literal("巫妖吸收了" + list.size() + "个仆从").withStyle(ChatFormatting.RED));
                        player.sendSystemMessage(Component.literal("为其增加了" + list.size() + "点攻击").withStyle(ChatFormatting.RED));
                    }
                }*/
                    pCompound.putInt("TreatTime", 2200);
                }
            }
            if (this.getPhase() != 1 && pCompound.getInt("SummonMinion") > 0) {
                pCompound.putInt("SummonMinion", pCompound.getInt("SummonMinion") - 1);
                if (pCompound.getInt("SummonMinion") == 0) {
                    List<Lich> list = this.level().getEntitiesOfClass(Lich.class, this.getBoundingBox().inflate(2), IS_NOT_SELF);
                    for (Lich lich : list) {
                        if (lich.getTarget() != null && lich.level() instanceof ServerLevelAccessor accessor) {
                            Vec3 minionSpot = aTMod_forge_1_20_1$findVec(lich, lich.getTarget());
                            LichMinion minion = new LichMinion(TFEntities.LICH_MINION.get(), this.level());
                            minion.setPos(minionSpot.x(), minionSpot.y(), minionSpot.z());
                            minion.getAttribute(Attributes.ARMOR).setBaseValue(ATModFinal.MinionArmor);
                            minion.getAttribute(Attributes.ARMOR_TOUGHNESS).setBaseValue(ATModFinal.MinionArmorToughness);
                            minion.getAttribute(Attributes.MAX_HEALTH).setBaseValue(ATModFinal.MinionHealth);
                            minion.setHealth(minion.getMaxHealth());
                            CompoundTag pCompound2 = minion.getPersistentData();
                            pCompound2.putBoolean("Stronger", true);
                            minion.addAdditionalSaveData(pCompound2);
                            minion.readAdditionalSaveData(pCompound2);
                            ForgeEventFactory.onFinalizeSpawn(minion, accessor, lich.level().getCurrentDifficultyAt(BlockPos.containing(minionSpot)), MobSpawnType.MOB_SUMMONED, null, null);
                            this.level().addFreshEntity(minion);
                        }
                    }
                    pCompound.putInt("SummonMinion", ATModFinal.MinionSummon);
                }
            } else if (pCompound.contains("SummonMinion") && pCompound.getInt("SummonMinion") <= 0)
                pCompound.putInt("SummonMinion", 1000);
            //this.addAdditionalSaveData(pCompound);
            //this.readAdditionalSaveData(pCompound);
        }
    }

    @Unique
    public Vec3 aTMod_forge_1_20_1$findVec(Lich lich, LivingEntity targetEntity){
        double origX = lich.getX();
        double origY = lich.getY();
        double origZ = lich.getZ();
        int tries = 100;
        for (int i = 0; i < tries; i++) {
            // we abuse LivingEntity.attemptTeleport, which does all the collision/ground checking for us, then teleport back to our original spot
            double tx = targetEntity.getX() + lich.getRandom().nextGaussian() * 16D;
            double ty = targetEntity.getY();
            double tz = targetEntity.getZ() + lich.getRandom().nextGaussian() * 16D;

            boolean destClear = lich.randomTeleport(tx, ty, tz, true);
            boolean canSeeTargetAtDest = lich.hasLineOfSight(targetEntity); // Don't use senses cache because we're in a temporary position
            lich.teleportTo(origX, origY, origZ);

            if (destClear && canSeeTargetAtDest) {
                return new Vec3(tx, ty, tz);
            }
        }
        return new Vec3(origX, origY, origZ);
    }
}
