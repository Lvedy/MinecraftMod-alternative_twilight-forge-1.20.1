package com.lvedy.alternative_twilight.mixin.Naga;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import twilightforest.entity.ai.goal.NagaMovementPattern;
import twilightforest.init.TFItems;

import java.util.List;
import java.util.function.Predicate;


@Mixin(twilightforest.entity.boss.Naga.class)
public abstract class Naga extends Monster {
    @Shadow
    public abstract void addAdditionalSaveData(CompoundTag compound);
    @Shadow
    public abstract NagaMovementPattern getMovementAI();
    @Unique
    private static final Predicate<Entity> IS_NOT_SELF = entity -> entity.isAlive() && !(entity instanceof Naga);

    @Inject(method = "hurt", at = @At("HEAD"))
    public void Hurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir){
        if(!this.level().isClientSide()) {
            CompoundTag pCompound = this.getPersistentData();
            if(!pCompound.contains("BreakTime") || pCompound.getFloat("BreakTime") <= 0) {
                amount *= 0.4F;
                if (pCompound.contains("BreakHead"))
                    pCompound.putFloat("BreakHead", pCompound.getFloat("BreakHead") + amount);
                else
                    pCompound.putFloat("BreakHead", amount);
                this.addAdditionalSaveData(pCompound);
                this.readAdditionalSaveData(pCompound);
            }
        }
    }

    @ModifyArg(method = "hurt",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/Monster;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"), index = 1)
    public float HurtModify(float par2){
        CompoundTag pCompound = this.getPersistentData();
        if(!pCompound.contains("BreakTime") || pCompound.getFloat("BreakTime") <= 0)
            return  par2 * 0.4F;
        return par2 * 0.8F;
    }

    @ModifyArg(method = "doHurtTarget",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;push(DDD)V"), index = 0)
    public double PushModifyX(double pX){
        Entity entity = this.getTarget();
        double i = 1;
        if(entity instanceof Player){
            for (ItemStack itemStack : entity.getArmorSlots()){
                if (itemStack.is(Items.AIR) || (itemStack.isDamageableItem() && itemStack.getMaxDamage() - itemStack.getDamageValue() <= 120))
                    i += 1.8D;
            }
        }
        return pX * i;
    }

    @ModifyArg(method = "doHurtTarget",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;push(DDD)V"), index = 1)
    public double PushModifyY(double pY){
        Entity entity = this.getTarget();
        double i = 1;
        if(entity instanceof Player){
            for (ItemStack itemStack : entity.getArmorSlots()){
                if (itemStack.is(Items.AIR) || (itemStack.isDamageableItem() && itemStack.getMaxDamage() - itemStack.getDamageValue() <= 120))
                    i += 1.8D;
            }
        }
        return pY * i;
    }

    @ModifyArg(method = "doHurtTarget",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;push(DDD)V"), index = 2)
    public double PushModifyZ(double pZ){
        Entity entity = this.getTarget();
        double i = 1;
        if(entity instanceof Player){
            for (ItemStack itemStack : entity.getArmorSlots()){
                if (itemStack.is(Items.AIR) || (itemStack.isDamageableItem() && itemStack.getMaxDamage() - itemStack.getDamageValue() <= 120))
                    i += 1.8D;
            }
        }
        return pZ * i;
    }

    @ModifyArg(method = "doHurtTarget",at = @At(value = "INVOKE", target = "Ltwilightforest/network/ThrowPlayerPacket;<init>(DDD)V"), index = 0)
    public double SendModifyX(double pX){
        Entity entity = this.getTarget();
        double i = 1;
        if(entity instanceof Player){
            for (ItemStack itemStack : entity.getArmorSlots()){
                if (itemStack.is(Items.AIR) || (itemStack.isDamageableItem() && itemStack.getMaxDamage() - itemStack.getDamageValue() <= 120))
                    i += 1.2D;
            }
        }
        return pX * i;
    }

    @ModifyArg(method = "doHurtTarget",at = @At(value = "INVOKE", target = "Ltwilightforest/network/ThrowPlayerPacket;<init>(DDD)V"), index = 1)
    public double SendModifyY(double pY){
        Entity entity = this.getTarget();
        double i = 1;
        if(entity instanceof Player){
            for (ItemStack itemStack : entity.getArmorSlots()){
                if (itemStack.is(Items.AIR) || (itemStack.isDamageableItem() && itemStack.getMaxDamage() - itemStack.getDamageValue() <= 120))
                    i += 1.4D;
            }
        }
        return pY * i;
    }

    @ModifyArg(method = "doHurtTarget",at = @At(value = "INVOKE", target = "Ltwilightforest/network/ThrowPlayerPacket;<init>(DDD)V"), index = 2)
    public double SendModifyZ(double pZ){
        Entity entity = this.getTarget();
        double i = 1;
        if(entity instanceof Player){
            for (ItemStack itemStack : entity.getArmorSlots()){
                if (itemStack.is(Items.AIR) || (itemStack.isDamageableItem() && itemStack.getMaxDamage() - itemStack.getDamageValue() <= 120))
                    i += 1.2D;
            }
        }
        return pZ * i;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void Tick(CallbackInfo ci){
        if(!this.level().isClientSide()){
            CompoundTag pCompound = this.getPersistentData();
            if((!pCompound.contains("BreakTime") || pCompound.getFloat("BreakTime") <= 0) && pCompound.contains("BreakHead") && pCompound.getFloat("BreakHead") >= this.getMaxHealth() * 0.4F){
                this.getMovementAI().doDaze();
                List<Player> list = this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(40), IS_NOT_SELF);
                for (Player player : list) {
                    player.sendSystemMessage(Component.translatable("nagaMixinsend1").withStyle(ChatFormatting.RED));
                    player.sendSystemMessage(Component.translatable("nagaMixinsend2").withStyle(ChatFormatting.RED));
                }
                Item item = TFItems.NAGA_SCALE.get();
                ItemStack itemStack = item.getDefaultInstance();
                itemStack.setCount(5);
                ItemEntity entity = new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), itemStack);
                entity.setDefaultPickUpDelay();
                this.level().addFreshEntity(entity);
                pCompound.putFloat("BreakTime", 6000);
                pCompound.putFloat("BreakHead", 0);
                this.addAdditionalSaveData(pCompound);
                this.readAdditionalSaveData(pCompound);
            }
            if (pCompound.contains("BreakTime")) {
                float i = pCompound.getFloat("BreakTime");
                if (i > 0) {
                    if (i == 1) {
                        List<Player> list = this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(40), IS_NOT_SELF);
                        for (Player player : list)
                            player.sendSystemMessage(Component.translatable("nagaMixinsend3").withStyle(ChatFormatting.RED));
                    }
                    pCompound.putFloat("BreakTime", i - 1);
                    if (this.getMovementAI().getState() == NagaMovementPattern.MovementState.STUNLESS_CHARGE) {
                        int j = this.getRandom().nextInt(100);
                        if (j <= 4)
                            this.getMovementAI().doDaze();
                        else if (j <= 24) {
                            this.getMovementAI().stop();
                        }
                    }
                }
            }
        }
    }

    protected Naga(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
}
