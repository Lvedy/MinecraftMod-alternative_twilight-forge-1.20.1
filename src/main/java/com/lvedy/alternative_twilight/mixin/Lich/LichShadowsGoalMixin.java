package com.lvedy.alternative_twilight.mixin.Lich;

import com.lvedy.alternative_twilight.ATModFinal;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import twilightforest.entity.ai.goal.LichShadowsGoal;
import twilightforest.entity.boss.Lich;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@Mixin(value = LichShadowsGoal.class, priority = 7)
public class LichShadowsGoalMixin extends Goal {
    @Unique
    private static final Predicate<Entity> IS_NOT_SELF = Entity::isAlive;
    @Shadow
    private Lich lich;
    
    @Unique
    private final Map<BlockPos, BlockState> originalBlocks = new HashMap<>();

    @Inject(method = "tick", at = @At(value = "HEAD"))
    public void Tick(CallbackInfo ci){
        if (ATModFinal.LichModify == 1) {
            CompoundTag pCompound = this.lich.getPersistentData();
            LivingEntity targetedEntity = this.lich.getTarget();
            if (targetedEntity == null)
                return;
            float dist = this.lich.distanceTo(targetedEntity);
            if (pCompound.contains("Obsidian") && pCompound.getFloat("Obsidian") == 1) {
                Lich Target = this.lich;
                if (this.lich.getMasterLich() != null)
                    Target = this.lich.getMasterLich();
                Target.getAttribute(Attributes.MAX_HEALTH).setBaseValue(this.lich.getAttribute(Attributes.MAX_HEALTH).getBaseValue() + ATModFinal.LichObsidianHealth);
                Target.setHealth(Target.getHealth() + ATModFinal.LichObsidianHealth);
                pCompound.putFloat("Obsidian", pCompound.getFloat("Obsidian") - 1);
            }
            if (pCompound.contains("Obsidian") && pCompound.getFloat("Obsidian") == 0) {
                // 恢复所有被替换的方块
                restoreOriginalBlocks();
            } else if (pCompound.contains("Obsidian") && pCompound.getFloat("Obsidian") > 1)
                pCompound.putFloat("Obsidian", pCompound.getFloat("Obsidian") - 1);
            if (this.lich.getSensing().hasLineOfSight(targetedEntity) && this.lich.getAttackCooldown() == 0 && dist < 20F) {
                int i = this.lich.getRandom().nextInt(10);
                System.out.print(i);
                if (i < 6) {
                    this.lich.setNextAttackType(0);
                } else if (i < 9) {
                    this.lich.setNextAttackType(1);    //黑曜石囚笼
                } else if ((!pCompound.contains("Obsidian") || pCompound.getFloat("Obsidian") <= 0) && ATModFinal.LichObsidianSwitch == 1) {
                    List<Player> list = this.lich.level().getEntitiesOfClass(Player.class, this.lich.getBoundingBox().inflate(40), IS_NOT_SELF);
                    for (Player player : list) {
                        Vec3 vec3 = player.blockPosition().getCenter();
                        player.teleportTo(vec3.x, vec3.y, vec3.z);
                        for (BlockPos blockpos : BlockPos.betweenClosed(Mth.floor(vec3.x - 1), Mth.floor(vec3.y - 1), Mth.floor(vec3.z - 1), Mth.floor(vec3.x + 1), Mth.floor(vec3.y + 2), Mth.floor(vec3.z + 1))) {
                            BlockState blockstate = player.level().getBlockState(blockpos);
                            Block block = blockstate.getBlock();
                            if (block.defaultDestroyTime() >= 0) {
                                // 保存原始方块状态
                                originalBlocks.put(blockpos.immutable(), blockstate);
                                player.level().setBlock(blockpos, Blocks.OBSIDIAN.defaultBlockState(), 3);
                            }
                        }
                        for (BlockPos blockpos : BlockPos.betweenClosed(Mth.floor(vec3.x), Mth.floor(vec3.y), Mth.floor(vec3.z), Mth.floor(vec3.x), Mth.floor(vec3.y + 1), Mth.floor(vec3.z))) {
                            BlockState blockstate = player.level().getBlockState(blockpos);
                            Block block = blockstate.getBlock();
                            if (block.defaultDestroyTime() >= 0) {
                                player.level().setBlock(blockpos, Blocks.AIR.defaultBlockState(), 3);
                            }
                        }
                    }
                    pCompound.putFloat("Obsidian", ATModFinal.LichObsidian);
                }
            }
        }
    }
    
    @Unique
    private void restoreOriginalBlocks() {
        if (!originalBlocks.isEmpty()) {
            for (Map.Entry<BlockPos, BlockState> entry : originalBlocks.entrySet()) {
                this.lich.level().setBlock(entry.getKey(), entry.getValue(), 3);
            }
            originalBlocks.clear();
        }
    }

    @Override
    public boolean canUse() {
        return this.lich.getPhase() == 1;
    }
    
    @Inject(method = "stop", at = @At("HEAD"))
    public void onStop(CallbackInfo ci) {
        // 当技能停止时恢复方块
        if (ATModFinal.LichModify == 1) {
            CompoundTag pCompound = this.lich.getPersistentData();
            if (pCompound.contains("Obsidian") && pCompound.getFloat("Obsidian") > 0) {
                pCompound.putFloat("Obsidian", 0);
                restoreOriginalBlocks();
            }
        }
    }
}
