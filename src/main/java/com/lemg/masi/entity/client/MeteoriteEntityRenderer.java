package com.lemg.masi.entity.client;

import com.lemg.masi.Masi;
import com.lemg.masi.entity.entities.MeteoriteEntity;
import com.lemg.masi.entity.entities.SwordEnergyEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class MeteoriteEntityRenderer extends MobEntityRenderer<MeteoriteEntity,MeteoriteEntityModel<MeteoriteEntity>> {
    private static final Identifier METEORITE_ENERGY_TEXTURE = new Identifier(Masi.MOD_ID,"textures/entity/meteorite_entity.png");
    public MeteoriteEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new MeteoriteEntityModel<>(context.getPart(ModModelLayers.METEORITE)),1.0f);
    }

    @Override
    public Identifier getTexture(MeteoriteEntity entity) {
        return METEORITE_ENERGY_TEXTURE;
    }
    @Override
    protected void scale(MeteoriteEntity meteoriteEntity, MatrixStack matrixStack, float f) {
        float size = 1.5f * meteoriteEntity.getSize();
        matrixStack.scale(size,size,size);
        super.scale(meteoriteEntity, matrixStack, f);
    }

    @Override
    public void render(MeteoriteEntity mobEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}