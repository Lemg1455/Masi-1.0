package com.lemg.masi.entity.client;
import com.lemg.masi.entity.entities.MeteoriteEntity;
import com.lemg.masi.entity.entities.SwordEnergyEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class MeteoriteEntityModel<T extends MeteoriteEntity> extends SinglePartEntityModel<T> {

    private final ModelPart bb_main;
    public MeteoriteEntityModel(ModelPart root) {
        this.bb_main = root.getChild("bb_main");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData bb_main = modelPartData.addChild("bb_main", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -25.0F, -8.0F, 16.0F, 16.0F, 16.0F, new Dilation(0.0F))
                .uv(34, 52).cuboid(8.0F, -23.0F, -7.0F, 3.0F, 13.0F, 14.0F, new Dilation(0.0F))
                .uv(0, 49).cuboid(-11.0F, -23.0F, -7.0F, 3.0F, 13.0F, 14.0F, new Dilation(0.0F))
                .uv(40, 35).cuboid(-6.0F, -28.0F, -7.0F, 13.0F, 3.0F, 14.0F, new Dilation(0.0F))
                .uv(0, 32).cuboid(-6.0F, -9.0F, -7.0F, 13.0F, 3.0F, 14.0F, new Dilation(0.0F))
                .uv(64, 17).cuboid(-6.0F, -23.0F, -12.0F, 13.0F, 13.0F, 4.0F, new Dilation(0.0F))
                .uv(64, 0).cuboid(-6.0F, -23.0F, 8.0F, 13.0F, 13.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
        return TexturedModelData.of(modelData, 128, 128);
    }


    @Override
    public void setAngles(MeteoriteEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }


    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        bb_main.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart getPart() {
        return this.bb_main;
    }
}