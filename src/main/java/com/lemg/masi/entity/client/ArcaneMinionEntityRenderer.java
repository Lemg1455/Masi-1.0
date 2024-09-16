package com.lemg.masi.entity.client;

import com.lemg.masi.Masi;
import com.lemg.masi.entity.entities.minions.ArcaneMinionEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class ArcaneMinionEntityRenderer extends MobEntityRenderer<ArcaneMinionEntity,ArcaneMinionEntityModel<ArcaneMinionEntity>> {
    private static final Identifier ARCANE_MINION_TEXTURE = new Identifier(Masi.MOD_ID,"textures/entity/arcane_minion_entity.png");
    public ArcaneMinionEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new ArcaneMinionEntityModel<>(context.getPart(ModModelLayers.ARCANE_MINION)),1.0f);
    }

    @Override
    public Identifier getTexture(ArcaneMinionEntity entity) {
        return ARCANE_MINION_TEXTURE;
    }
    @Override
    protected void scale(ArcaneMinionEntity arcaneMinionEntity, MatrixStack matrixStack, float f) {
        matrixStack.scale(1.5f,1.5f,1.5f);
        super.scale(arcaneMinionEntity, matrixStack, f);
    }

    @Override
    public void render(ArcaneMinionEntity mobEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}