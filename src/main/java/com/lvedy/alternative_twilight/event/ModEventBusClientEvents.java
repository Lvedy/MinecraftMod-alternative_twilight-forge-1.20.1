package com.lvedy.alternative_twilight.event;

import com.lvedy.alternative_twilight.ATMod;
import com.lvedy.alternative_twilight.entity.client.ModModleLayer;
import com.lvedy.alternative_twilight.entity.client.SnowTrapModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ATMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEventBusClientEvents {

    @SubscribeEvent
    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event){
        event.registerLayerDefinition(ModModleLayer.SNOW_TRAP_LAYER, SnowTrapModel::createBodyLayer);
    }
}
