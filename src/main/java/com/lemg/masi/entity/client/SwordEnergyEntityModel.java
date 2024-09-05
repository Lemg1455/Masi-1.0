package com.lemg.masi.entity.client;
import com.lemg.masi.entity.ArcaneMinionEntity;
import com.lemg.masi.entity.SwordEnergyEntity;
import com.lemg.masi.entity.animation.ArcaneMinionEntityAnimation;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.util.math.MathHelper;

public class SwordEnergyEntityModel<T extends SwordEnergyEntity> extends SinglePartEntityModel<T> {

    private final ModelPart sword_energy;
    public SwordEnergyEntityModel(ModelPart root) {
        this.sword_energy = root.getChild("sword_energy");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData sword_energy = modelPartData.addChild("sword_energy", ModelPartBuilder.create().uv(2, 0).cuboid(-14.0F, -1.0F, -3.0F, 30.0F, 1.0F, 7.0F, new Dilation(0.0F))
                .uv(6, 8).cuboid(-7.0F, -1.0F, -5.0F, 16.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(13, 12).cuboid(-17.0F, -1.0F, -1.0F, 3.0F, 1.0F, 7.0F, new Dilation(0.0F))
                .uv(12, 20).cuboid(-19.0F, -1.0F, 1.0F, 2.0F, 1.0F, 7.0F, new Dilation(0.0F))
                .uv(9, 28).cuboid(-21.0F, -1.0F, 3.0F, 2.0F, 1.0F, 7.0F, new Dilation(0.0F))
                .uv(1, 27).cuboid(-22.0F, -1.0F, 5.0F, 1.0F, 1.0F, 7.0F, new Dilation(0.0F))
                .uv(22, 21).cuboid(23.0F, -1.0F, 5.0F, 1.0F, 1.0F, 7.0F, new Dilation(0.0F))
                .uv(26, 11).cuboid(21.0F, -1.0F, 3.0F, 2.0F, 1.0F, 7.0F, new Dilation(0.0F))
                .uv(0, 19).cuboid(19.0F, -1.0F, 1.0F, 2.0F, 1.0F, 7.0F, new Dilation(0.0F))
                .uv(0, 11).cuboid(16.0F, -1.0F, -1.0F, 3.0F, 1.0F, 7.0F, new Dilation(0.0F)), ModelTransform.pivot(-1.0F, 24.0F, -6.0F));
        return TexturedModelData.of(modelData, 128, 128);
    }


    @Override
    public void setAngles(SwordEnergyEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }


    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        sword_energy.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart getPart() {
        return this.sword_energy;
    }
}