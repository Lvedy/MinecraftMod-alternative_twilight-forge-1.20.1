package com.lvedy.alternative_twilight.event;

import com.lvedy.alternative_twilight.ATMod;
import com.lvedy.alternative_twilight.entity.ModEntityTypes;
import com.lvedy.alternative_twilight.entity.custom.SnowTrapEntity;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ATMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntityTypes.SNOW_TRAP.get(), SnowTrapEntity.createAttributes().build());
    }
}