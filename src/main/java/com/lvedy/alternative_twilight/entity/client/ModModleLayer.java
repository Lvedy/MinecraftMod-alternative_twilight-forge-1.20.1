package com.lvedy.alternative_twilight.entity.client;

import com.lvedy.alternative_twilight.ATMod;
import com.lvedy.alternative_twilight.entity.ModEntityTypes;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;

public class ModModleLayer {
    public static final ModelLayerLocation SNOW_TRAP_LAYER = new ModelLayerLocation(
            new ResourceLocation(ATMod.MODID, "snow_trap_layer"), "main");


    public static void register() {
        EntityRenderers.register(ModEntityTypes.SNOW_TRAP.get(), SnowTrapRender::new);
    }
}
