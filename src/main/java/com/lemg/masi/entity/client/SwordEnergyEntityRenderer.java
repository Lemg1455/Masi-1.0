package com.lemg.masi.entity.client;

import com.lemg.masi.Masi;
import com.lemg.masi.entity.entities.SwordEnergyEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class SwordEnergyEntityRenderer extends MobEntityRenderer<SwordEnergyEntity,SwordEnergyEntityModel<SwordEnergyEntity>> {
    private static final Identifier SWORD_ENERGY_TEXTURE = new Identifier(Masi.MOD_ID,"textures/entity/sword_energy_entity.png");
    public SwordEnergyEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new SwordEnergyEntityModel<>(context.getPart(ModModelLayers.SWORD_ENERGY)),1.0f);
    }

    @Override
    public Identifier getTexture(SwordEnergyEntity entity) {
        return SWORD_ENERGY_TEXTURE;
    }
    @Override
    protected void scale(SwordEnergyEntity swordEnergyEntity, MatrixStack matrixStack, float f) {
        float size = 1.5f * swordEnergyEntity.getSize();
        matrixStack.scale(size,size,size);
        super.scale(swordEnergyEntity, matrixStack, f);
    }

    @Override
    public void render(SwordEnergyEntity mobEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}