package com.lvedy.alternative_twilight.entity.client;

import com.lvedy.alternative_twilight.ATMod;
import com.lvedy.alternative_twilight.ATModFinal;
import com.lvedy.alternative_twilight.entity.custom.SnowTrapEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class SnowTrapRender extends MobRenderer<SnowTrapEntity, SnowTrapModel<SnowTrapEntity>> {
    public SnowTrapRender(EntityRendererProvider.Context pContext) {
        super(pContext, new SnowTrapModel<>(pContext.bakeLayer(ModModleLayer.SNOW_TRAP_LAYER)), 1f);
    }

    @Override
    public ResourceLocation getTextureLocation(SnowTrapEntity pEntity) {
        return new ResourceLocation(ATMod.MODID, "textures/entity/snow_trap/snow_trap.png");
    }

    @Override
    public void render(SnowTrapEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack,
                       MultiBufferSource pBuffer, int pPackedLight) {
        if (pEntity.getBig())
            pPoseStack.scale(ATModFinal.SnowTrapScale,1,ATModFinal.SnowTrapScale);
        super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
    }
}
