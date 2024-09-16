package com.lemg.masi.entity.client;
import com.lemg.masi.entity.entities.minions.ArcaneMinionEntity;
import com.lemg.masi.entity.animation.ArcaneMinionEntityAnimation;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class ArcaneMinionEntityModel<T extends ArcaneMinionEntity> extends SinglePartEntityModel<T> {

    private final ModelPart minion;
    private final ModelPart left_arm;
    private final ModelPart right_arm;
    private final ModelPart head;
    private final ModelPart body;
    public ArcaneMinionEntityModel(ModelPart root) {
        this.minion = root.getChild("minion");
        this.left_arm = minion.getChild("left_arm");
        this.right_arm = minion.getChild("right_arm");
        this.head = minion.getChild("head");
        this.body = minion.getChild("body");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData minion = modelPartData.addChild("minion", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 19.0F, 0.0F));

        ModelPartData left_arm = minion.addChild("left_arm", ModelPartBuilder.create().uv(0, 51).cuboid(-3.0F, -4.0F, -3.0F, 6.0F, 8.0F, 6.0F, new Dilation(0.0F))
                .uv(22, 39).cuboid(-2.0F, 4.0F, -3.0F, 5.0F, 11.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(-12.0F, -24.0F, 0.0F));

        ModelPartData right_arm = minion.addChild("right_arm", ModelPartBuilder.create().uv(50, 12).cuboid(-3.75F, -3.75F, -3.0F, 6.0F, 8.0F, 6.0F, new Dilation(0.0F))
                .uv(0, 34).cuboid(-3.75F, 4.25F, -3.0F, 5.0F, 11.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(12.75F, -24.25F, 0.0F));

        ModelPartData head = minion.addChild("head", ModelPartBuilder.create().uv(44, 39).cuboid(-4.0F, -5.0F, -3.0F, 7.0F, 6.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -29.0F, 0.0F));

        ModelPartData body = minion.addChild("body", ModelPartBuilder.create().uv(38, 51).cuboid(-3.0F, -7.0F, -3.0F, 6.0F, 5.0F, 6.0F, new Dilation(0.0F))
                .uv(28, 26).cuboid(-4.0F, -12.0F, -4.0F, 8.0F, 5.0F, 8.0F, new Dilation(0.0F))
                .uv(0, 18).cuboid(-5.0F, -20.0F, -4.0F, 10.0F, 8.0F, 8.0F, new Dilation(0.0F))
                .uv(0, 0).cuboid(-9.0F, -28.0F, -5.0F, 18.0F, 8.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        return TexturedModelData.of(modelData, 128, 128);
    }


    @Override
    public void setAngles(ArcaneMinionEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.getPart().traverse().forEach(ModelPart::resetTransform);
        this.setHeadAngles(netHeadYaw,headPitch);

        this.animateMovement(ArcaneMinionEntityAnimation.walk,limbSwing,limbSwingAmount,2f,2.5f);
        this.updateAnimation(entity.idleAnimationState,ArcaneMinionEntityAnimation.idle,ageInTicks,1f);
        this.updateAnimation(entity.attackAnimationState,ArcaneMinionEntityAnimation.attack,ageInTicks,1f);

    }

    private void setHeadAngles(float headAngles, float headPitch) {
        headAngles = MathHelper.clamp(headAngles,-15.0F,15.0F);
        headPitch = MathHelper.clamp(headPitch, -15.0F, 25.0F);
        this.head.yaw = headAngles * 0.017453292F;
        this.head.pitch = headPitch * 0.017453292F;
    }
    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        minion.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart getPart() {
        return this.minion;
    }
}