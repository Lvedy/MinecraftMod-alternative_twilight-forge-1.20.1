package com.lvedy.alternative_twilight.mixin.AlphaYeti;


import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import twilightforest.entity.boss.AlphaYeti;
import twilightforest.init.TFMobEffects;
import twilightforest.potions.FrostedEffect;

import java.util.List;
import java.util.function.Predicate;

@Mixin(AlphaYeti.class)
public abstract class AlphaYetiMixin extends Monster {
    @Shadow public abstract boolean isTired();

    @Shadow public abstract boolean isRampaging();

    @Unique
    private static final Predicate<Entity> IS_NOT_SELF = Entity::isAlive;

    protected AlphaYetiMixin(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "aiStep", at = @At("HEAD"))
    public void aiStep(CallbackInfo ci){
        CompoundTag pCompound = this.getPersistentData();
        if (this.isRampaging()) {
            Vec3 vec3 = this.position();
            for (BlockPos blockpos : BlockPos.betweenClosed(Mth.floor(vec3.x-18), Mth.floor(vec3.y), Mth.floor(vec3.z-18), Mth.floor(vec3.x+18), Mth.floor(vec3.y+15), Mth.floor(vec3.z+18))) {
                BlockState blockstate = this.level().getBlockState(blockpos);
                Block block = blockstate.getBlock();
                if (block == Blocks.AIR && this.level().getBlockState(blockpos.below()).getBlock() != Blocks.AIR && this.getRandom().nextInt(1000) == 0) {
                    this.level().setBlock(blockpos, Blocks.POWDER_SNOW.defaultBlockState(), 3);
                }
            }
        }
        if (!pCompound.contains("IceTime"))
            pCompound.putInt("IceTime", 600);
        List<Player> list = this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(40), IS_NOT_SELF);
        for (Player player : list){
            if(pCompound.getInt("IceTime") == 0) {
                player.addEffect(new MobEffectInstance(TFMobEffects.FROSTY.get(), 60, 0));
                pCompound.putInt("IceTime", 600);
            }
            else if(player.isInPowderSnow){
                pCompound.putInt("IceTime", pCompound.getInt("IceTime") - 1);
                break;
            }
        }
    }
}
