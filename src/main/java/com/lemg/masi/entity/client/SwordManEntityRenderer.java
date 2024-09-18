package com.lemg.masi.entity.client;

import com.lemg.masi.Masi;
import com.lemg.masi.entity.entities.minions.ArcaneMinionEntity;
import com.lemg.masi.entity.entities.minions.SwordManEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.ArmorEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.Identifier;

public class SwordManEntityRenderer extends MobEntityRenderer<SwordManEntity,SwordManEntityModel<SwordManEntity>> {

    private static final Identifier SWORD_MAN_TEXTURE = new Identifier(Masi.MOD_ID,"textures/entity/sword_man_entity.png");
    public SwordManEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new SwordManEntityModel<>(ctx.getPart(ModModelLayers.SWORD_MAN)),1.0f);
        this.addFeature(new ArmorFeatureRenderer(this, new ArmorEntityModel(ctx.getPart(EntityModelLayers.PLAYER_INNER_ARMOR)), new ArmorEntityModel(ctx.getPart(EntityModelLayers.PLAYER_OUTER_ARMOR)), ctx.getModelManager()));

    }

    @Override
    public Identifier getTexture(SwordManEntity entity) {
        return SWORD_MAN_TEXTURE;
    }
    @Override
    protected void scale(SwordManEntity entity, MatrixStack matrixStack, float f) {
        matrixStack.scale(1.0f,1.0f,1.0f);
        super.scale(entity, matrixStack, f);
    }

    @Override
    public void render(SwordManEntity mobEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}