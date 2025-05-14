package com.lvedy.alternative_twilight.mixin.SnowQueen;

import com.lvedy.alternative_twilight.Render.SnowQueen.SnowQueenLayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import twilightforest.TwilightForestMod;
import twilightforest.client.model.entity.SnowQueenModel;
import twilightforest.client.renderer.entity.SnowQueenRenderer;
import twilightforest.entity.boss.SnowQueen;

@Mixin(value = SnowQueenRenderer.class, priority = 7)
public class SnowQueenRenderMixin extends HumanoidMobRenderer<SnowQueen, SnowQueenModel> {
    @Unique
    private static final ResourceLocation QUEEN_TEXTURE = TwilightForestMod.getModelTexture("snowqueen.png");

    public SnowQueenRenderMixin(EntityRendererProvider.Context pContext, SnowQueenModel pModel, float pShadowRadius) {
        super(pContext, pModel, pShadowRadius);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void InjectInit(EntityRendererProvider.Context manager, SnowQueenModel model, CallbackInfo ci){
        addLayer(new SnowQueenLayer<>(this, model));
    }

    @Override
    public ResourceLocation getTextureLocation(SnowQueen pEntity) {
        return QUEEN_TEXTURE;
    }
}
