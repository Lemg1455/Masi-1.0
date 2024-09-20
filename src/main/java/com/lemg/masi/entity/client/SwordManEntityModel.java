package com.lemg.masi.entity.client;

import com.lemg.masi.entity.animation.ArcaneMinionEntityAnimation;
import com.lemg.masi.entity.animation.SwordManEntityAnimation;
import com.lemg.masi.entity.entities.minions.ArcaneMinionEntity;
import com.lemg.masi.entity.entities.minions.SwordManEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.animation.Animation;

import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.AnimationState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector3f;

import java.util.Optional;

public class SwordManEntityModel<T extends SwordManEntity> extends BipedEntityModel<T> {
    private static final Vector3f ZERO = new Vector3f();

    private final ModelPart Sword;
    private final ModelPart root;
    public SwordManEntityModel(ModelPart root) {
        super(root);
        this.Sword = root.getChild("right_arm").getChild("Sword");
        this.root=root;
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData head = modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData body = modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 16).cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData left_arm = modelPartData.addChild("left_arm", ModelPartBuilder.create().uv(24, 16).cuboid(-1.0792F, -2.0F, -2.2083F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(5.0F, 2.0F, 0.0F));

        ModelPartData right_arm = modelPartData.addChild("right_arm", ModelPartBuilder.create().uv(0, 32).cuboid(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(-5.0F, 2.0F, 0.0F));

        ModelPartData Sword = right_arm.addChild("Sword", ModelPartBuilder.create().uv(24, 0).cuboid(-0.2296F, 0.967F, -0.4732F, 1.0F, 7.0F, 1.0F, new Dilation(0.0F))
                .uv(32, 32).cuboid(-1.2296F, -17.033F, -0.4732F, 3.0F, 17.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 4).cuboid(-1.2296F, 7.967F, -0.4732F, 3.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(36, 16).cuboid(-3.2296F, -0.033F, -0.4732F, 7.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(6.5F, 1.5F, 5.0F, 0.0F, 0.0F, -2.2253F));

        ModelPartData cube_r1 = Sword.addChild("cube_r1", ModelPartBuilder.create().uv(0, 0).cuboid(-2.7F, -2.0F, -2.0F, 3.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.7704F, -16.033F, 1.5268F, 0.0F, 0.0F, 0.7854F));

        ModelPartData left_leg = modelPartData.addChild("left_leg", ModelPartBuilder.create().uv(16, 32).cuboid(-1.9F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(1.9F, 12.0F, 0.0F));

        ModelPartData right_leg = modelPartData.addChild("right_leg", ModelPartBuilder.create().uv(32, 0).cuboid(-2.1F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(-1.9F, 12.0F, 0.0F));

        ModelPartData hat = modelPartData.addChild("hat", ModelPartBuilder.create().uv(40, 18).cuboid(-5.0F, -9.0F, -5.0F, 10.0F, 10.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        return TexturedModelData.of(modelData, 128, 128);
    }


    @Override
    public void setAngles(SwordManEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.getPart().traverse().forEach(ModelPart::resetTransform);
        this.setHeadAngles(netHeadYaw,headPitch);
        this.animateMovement(this,SwordManEntityAnimation.walk,limbSwing,limbSwingAmount,2f,2.5f);
        this.updateAnimation(this,entity.idleAnimationState,SwordManEntityAnimation.idle,ageInTicks,1f);
        this.updateAnimation(this,entity.sittingAnimationState,SwordManEntityAnimation.sitting,ageInTicks,1f);
        this.updateAnimation(this,entity.attackAnimation1,SwordManEntityAnimation.common_attack_1,ageInTicks,1f);
        this.updateAnimation(this,entity.attackAnimation2,SwordManEntityAnimation.common_attack_2,ageInTicks,1f);
        this.updateAnimation(this,entity.attackAnimation3,SwordManEntityAnimation.common_attack_3,ageInTicks,1f);
        this.updateAnimation(this,entity.attackAnimation4,SwordManEntityAnimation.common_attack_4,ageInTicks,1f);
        this.updateAnimation(this,entity.jump_hit,SwordManEntityAnimation.jump_hit,ageInTicks,1f);
        this.updateAnimation(this,entity.sword_ground,SwordManEntityAnimation.sword_ground,ageInTicks,1f);

    }

    private void setHeadAngles(float headAngles, float headPitch) {
        headAngles = MathHelper.clamp(headAngles,-15.0F,15.0F);
        headPitch = MathHelper.clamp(headPitch, -15.0F, 25.0F);
        this.head.yaw = headAngles * 0.017453292F;
        this.head.pitch = headPitch * 0.017453292F;
    }
    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        super.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        //Sword.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }


    public ModelPart getPart() {
        return this.root;
    }

    public Optional<ModelPart> getChild(String name) {
        if (name.equals("root")) {
            return Optional.of(this.getPart());
        }
        return this.getPart().traverse().filter(part -> part.hasChild(name)).findFirst().map(part -> part.getChild(name));
    }

    protected void updateAnimation(SwordManEntityModel<?> model, AnimationState animationState, Animation animation, float animationProgress) {
        this.updateAnimation(model,animationState, animation, animationProgress, 1.0f);
    }

    protected void animateMovement(SwordManEntityModel<?> model,Animation animation, float limbAngle, float limbDistance, float f, float g) {
        long l = (long)(limbAngle * 50.0f * f);
        float h = Math.min(limbDistance * g, 1.0f);
        AnimationHelper.animate(model, animation, l, h, ZERO);
    }

    protected void updateAnimation(SwordManEntityModel<?> model,AnimationState animationState, Animation animation, float animationProgress, float speedMultiplier) {
        animationState.update(animationProgress, speedMultiplier);
        animationState.run(state -> AnimationHelper.animate(model, animation, state.getTimeRunning(), 1.0f, ZERO));
    }

    protected void animate(SwordManEntityModel<?> model,Animation animation) {
        AnimationHelper.animate(model, animation, 0L, 1.0f, ZERO);
    }
}