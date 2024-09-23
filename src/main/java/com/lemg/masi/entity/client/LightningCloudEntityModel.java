package com.lemg.masi.entity.client;
import com.lemg.masi.entity.entities.MeteoriteEntity;
import com.lemg.masi.entity.entities.minions.LightningCloudEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class LightningCloudEntityModel<T extends LightningCloudEntity> extends SinglePartEntityModel<T> {

    private final ModelPart cloud;
    public LightningCloudEntityModel(ModelPart root) {
        this.cloud = root.getChild("cloud");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData cloud = modelPartData.addChild("cloud", ModelPartBuilder.create().uv(0, 0).cuboid(-13.0F, -5.0F, -8.0F, 16.0F, 5.0F, 26.0F, new Dilation(0.0F)), ModelTransform.pivot(5.0F, 24.0F, -5.0F));
        return TexturedModelData.of(modelData, 128, 128);
    }


    @Override
    public void setAngles(LightningCloudEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        cloud.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart getPart() {
        return this.cloud;
    }
}