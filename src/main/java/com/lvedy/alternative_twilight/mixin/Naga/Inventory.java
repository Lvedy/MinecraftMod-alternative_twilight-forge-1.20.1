package com.lvedy.alternative_twilight.mixin.Naga;

import net.minecraft.core.NonNullList;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import twilightforest.entity.boss.Naga;
import twilightforest.entity.boss.NagaSegment;

@Mixin(net.minecraft.world.entity.player.Inventory.class)
public class Inventory {
    @Shadow @Final public Player player;
    @Shadow @Final public NonNullList<ItemStack> armor;

    @Inject(method = "hurtArmor", at = @At("HEAD"), cancellable = true)
    public void NagaHurtArmor(DamageSource source, float amount, int[] slots, CallbackInfo ci){
        if ((source.getEntity() instanceof Naga || source.getEntity() instanceof NagaSegment) && this.player.getArmorValue() <= 18){
            if (!(amount <= 0.0F)) {
                amount /= 4.0F;
                if (amount < 2.0F) {
                    amount = 2.0F;
                }
                else
                    amount *= 1.5F;
                for (int i : slots) {
                    ItemStack armorItem = armor.get(i);
                    if ((!source.is(DamageTypeTags.IS_FIRE) || !armorItem.getItem().isFireResistant()) && armorItem.getItem() instanceof ArmorItem)  {
                        if (amount < 1.0F)  {amount = 1.0F;}
                        armorItem.hurtAndBreak((int) amount, ((Inventory) (Object) this).player, (ThePlayer) ->
                                ThePlayer.broadcastBreakEvent(EquipmentSlot.byTypeAndIndex(EquipmentSlot.Type.ARMOR, i)));
                    }
                }
            }
            ci.cancel();
        }
    }
}
