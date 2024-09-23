package com.lemg.masi.entity.client;

import com.lemg.masi.Masi;
import com.lemg.masi.entity.entities.MeteoriteEntity;
import com.lemg.masi.entity.entities.minions.LightningCloudEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class LightningCloudEntityRenderer extends MobEntityRenderer<LightningCloudEntity,LightningCloudEntityModel<LightningCloudEntity>> {
    private static final Identifier LIGHTNING_CLOUD_TEXTURE = new Identifier(Masi.MOD_ID,"textures/entity/lightning_cloud.png");
    private static final Identifier LIGHTNING_CLOUD_BLACK_TEXTURE = new Identifier(Masi.MOD_ID,"textures/entity/lightning_cloud_black.png");

    public LightningCloudEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new LightningCloudEntityModel<>(context.getPart(ModModelLayers.LIGHTNING_CLOUD)),1.0f);
    }

    @Override
    public Identifier getTexture(LightningCloudEntity entity) {
        if(entity.getTarget()!=null){
            return LIGHTNING_CLOUD_BLACK_TEXTURE;
        }
        return LIGHTNING_CLOUD_TEXTURE;
    }
    @Override
    protected void scale(LightningCloudEntity lightningCloudEntity, MatrixStack matrixStack, float f) {
        matrixStack.scale(2.5f,2.5f,2.5f);
        super.scale(lightningCloudEntity, matrixStack, f);
    }

    @Override
    public void render(LightningCloudEntity mobEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}