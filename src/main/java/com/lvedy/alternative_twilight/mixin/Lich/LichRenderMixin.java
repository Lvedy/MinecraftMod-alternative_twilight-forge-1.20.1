package com.lvedy.alternative_twilight.mixin.Lich;

import com.lvedy.alternative_twilight.Render.Lich.LichPowerLayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import twilightforest.TwilightForestMod;
import twilightforest.client.model.entity.LichModel;
import twilightforest.client.renderer.entity.LichRenderer;
import twilightforest.entity.boss.Lich;

@Mixin(LichRenderer.class)
public class LichRenderMixin extends HumanoidMobRenderer<Lich, LichModel> {
    private static final ResourceLocation LICH_TEXTURE = TwilightForestMod.getModelTexture("twilightlich64.png");

    public LichRenderMixin(EntityRendererProvider.Context pContext, LichModel pModel, float pShadowRadius) {
        super(pContext, pModel, pShadowRadius);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void InjectInit(EntityRendererProvider.Context manager, LichModel modelbiped, float shadowSize, CallbackInfo ci){
        addLayer(new LichPowerLayer<>(this, modelbiped));
    }

    @Override
    public ResourceLocation getTextureLocation(Lich pEntity) {
        return LICH_TEXTURE;
    }
}
