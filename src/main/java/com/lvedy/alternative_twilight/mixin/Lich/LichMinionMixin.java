package com.lvedy.alternative_twilight.mixin.Lich;

import com.lvedy.alternative_twilight.ATModFinal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import twilightforest.entity.boss.Lich;
import twilightforest.entity.monster.LichMinion;

import java.util.List;
import java.util.function.Predicate;

@Mixin(value = LichMinion.class, priority = 7)
public class LichMinionMixin extends Zombie {
    @Unique
    private static final Predicate<Entity> IS_NOT_SELF = Entity::isAlive;

    @Inject(method = "aiStep", at = @At(value = "HEAD"), cancellable = true)
    public void aiStep(CallbackInfo ci){
        if (ATModFinal.LichModify == 1) {
            CompoundTag pCompound = this.getPersistentData();
            if (this.isAlive())
                pCompound.putBoolean("Alife", true);
            if (this.getAttribute(Attributes.MAX_HEALTH).getBaseValue() >= 40 && !this.isAlive() && pCompound.contains("Alife") && pCompound.getBoolean("Alife")) {
                pCompound.putBoolean("Alife", false);
                List<Lich> list = this.level().getEntitiesOfClass(Lich.class, this.getBoundingBox().inflate(40), IS_NOT_SELF);
                for (Lich master : list) {
                    master.hurt(this.level().damageSources().generic(), 1);
                    master.setHealth((float) Math.max(1, master.getHealth() - (master.getMaxHealth() * 0.01 * ATModFinal.MinionDamageLich * (1 + Math.max(Math.min(0.2 * pCompound.getInt("resistance"), 0.8), -0.4)))));
                }
            }
            //this.addAdditionalSaveData(pCompound);
            //this.readAdditionalSaveData(pCompound);
            if (pCompound.contains("Stronger") && pCompound.getBoolean("Stronger")) {
                super.aiStep();
                ci.cancel();
            }
        }
    }

    public LichMinionMixin(EntityType<? extends Zombie> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
}
