package com.lvedy.alternative_twilight.Render.SnowQueen;

import com.lvedy.alternative_twilight.ATMod;
import com.lvedy.alternative_twilight.ATModFinal;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.SlimeModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import twilightforest.entity.boss.SnowQueen;

@OnlyIn(Dist.CLIENT)
public class SnowQueenLayer<T extends SnowQueen, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private static final ResourceLocation POWER_LOCATION = new ResourceLocation(ATMod.MODID,"textures/entity/snow_queen/snow_queen_armor.png");
    private static final ResourceLocation ICE_ARMOR_LOCATION = new ResourceLocation("textures/entity/creeper/creeper_armor.png");
    private final EntityModel<T> model;
    CompoundTag pCompound = new CompoundTag();

    public SnowQueenLayer(RenderLayerParent<T, M> pRenderer, EntityModel<T> model) {
        super(pRenderer);
        this.model = model;
    }

    @Override
    public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, T pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        this.pCompound = pLivingEntity.getPersistentData();
        if((pCompound.getInt("Invincible") > 0) || (pCompound.getInt("Frosty") > 0 && pCompound.getInt("IceArmor") == 0)) {
            float f = (float) pLivingEntity.tickCount + pPartialTicks;
            EntityModel<T> entitymodel = this.model();
            entitymodel.prepareMobModel(pLivingEntity, pLimbSwing, pLimbSwingAmount, pPartialTicks);
            this.getParentModel().copyPropertiesTo(entitymodel);
            //VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.energySwirl(this.getTextureLocation(), this.xOffset(f) % 1.0F, f * 0.01F % 1.0F));
            VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.energySwirl(this.getTextureLocation(), this.xOffset(f) % 1.0F, 0));
            entitymodel.setupAnim(pLivingEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
            entitymodel.renderToBuffer(pPoseStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, 0.5F, 0.5F, 0.5F, 1F);
        }
    }

    protected float xOffset(float pTickCount) {
        return pTickCount * 0.01F;
    }

    protected ResourceLocation getTextureLocation() {
        if(pCompound.getInt("Invincible") > 0)
            return POWER_LOCATION;
        else
            return ICE_ARMOR_LOCATION;
    }

    protected EntityModel<T> model() {
        return this.model;
    }
}
