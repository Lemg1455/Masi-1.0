package com.lemg.masi.mixin;

import com.lemg.masi.item.ModItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.FishingBobberEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FishingBobberEntityRenderer.class)
public abstract class FishingBobberEntityRendererMixin extends EntityRenderer<FishingBobberEntity> {
	@Unique
	private static final Identifier TEXTURE = new Identifier("textures/entity/fishing_hook.png");
	@Unique
	private static final RenderLayer LAYER = RenderLayer.getEntityCutout(TEXTURE);
	protected FishingBobberEntityRendererMixin(EntityRendererFactory.Context ctx) {
		super(ctx);
	}

	@Inject(at = @At("HEAD"), method = "render*", cancellable = true)
	public void render(FishingBobberEntity fishingBobberEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
		double s;
		float r;
		double q;
		double p;
		double o;
		PlayerEntity playerEntity = fishingBobberEntity.getPlayerOwner();
		if (playerEntity == null) {
			ci.cancel();
		}
		matrixStack.push();
		matrixStack.push();
		matrixStack.scale(0.5f, 0.5f, 0.5f);
		matrixStack.multiply(this.dispatcher.getRotation());
		matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f));
		MatrixStack.Entry entry = matrixStack.peek();
		Matrix4f matrix4f = entry.getPositionMatrix();
		Matrix3f matrix3f = entry.getNormalMatrix();
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(LAYER);
		vertex(vertexConsumer, matrix4f, matrix3f, i, 0.0f, 0, 0, 1);
		vertex(vertexConsumer, matrix4f, matrix3f, i, 1.0f, 0, 1, 1);
		vertex(vertexConsumer, matrix4f, matrix3f, i, 1.0f, 1, 1, 0);
		vertex(vertexConsumer, matrix4f, matrix3f, i, 0.0f, 1, 0, 0);
		matrixStack.pop();
		int j = playerEntity.getMainArm() == Arm.RIGHT ? 1 : -1;
		ItemStack itemStack = playerEntity.getMainHandStack();
		ItemStack itemStack2 = playerEntity.getOffHandStack();
		if (itemStack2.isOf(ModItems.INHERIT_TOOL_ITEM) || itemStack2.isOf(Items.FISHING_ROD)) {
			j = -j;
		}
		if(!itemStack.isOf(ModItems.INHERIT_TOOL_ITEM) && !itemStack.isOf(Items.FISHING_ROD)){
			ci.cancel();
		}
		float h = playerEntity.getHandSwingProgress(g);
		float k = MathHelper.sin(MathHelper.sqrt(h) * (float)Math.PI);
		float l = MathHelper.lerp(g, playerEntity.prevBodyYaw, playerEntity.bodyYaw) * ((float)Math.PI / 180);
		double d = MathHelper.sin(l);
		double e = MathHelper.cos(l);
		double m = (double)j * 0.35;
		double n = 0.8;
		if (this.dispatcher.gameOptions != null && !this.dispatcher.gameOptions.getPerspective().isFirstPerson() || playerEntity != MinecraftClient.getInstance().player) {
			o = MathHelper.lerp((double)g, playerEntity.prevX, playerEntity.getX()) - e * m - d * 0.8;
			p = playerEntity.prevY + (double)playerEntity.getStandingEyeHeight() + (playerEntity.getY() - playerEntity.prevY) * (double)g - 0.45;
			q = MathHelper.lerp((double)g, playerEntity.prevZ, playerEntity.getZ()) - d * m + e * 0.8;
			r = playerEntity.isInSneakingPose() ? -0.1875f : 0.0f;
		} else {
			s = 960.0 / (double)this.dispatcher.gameOptions.getFov().getValue().intValue();
			Vec3d vec3d = this.dispatcher.camera.getProjection().getPosition((float)j * 0.525f, -0.1f);
			vec3d = vec3d.multiply(s);
			vec3d = vec3d.rotateY(k * 0.5f);
			vec3d = vec3d.rotateX(-k * 0.7f);
			o = MathHelper.lerp((double)g, playerEntity.prevX, playerEntity.getX()) + vec3d.x;
			p = MathHelper.lerp((double)g, playerEntity.prevY, playerEntity.getY()) + vec3d.y;
			q = MathHelper.lerp((double)g, playerEntity.prevZ, playerEntity.getZ()) + vec3d.z;
			r = playerEntity.getStandingEyeHeight();
		}
		s = MathHelper.lerp((double)g, fishingBobberEntity.prevX, fishingBobberEntity.getX());
		double t = MathHelper.lerp((double)g, fishingBobberEntity.prevY, fishingBobberEntity.getY()) + 0.25;
		double u = MathHelper.lerp((double)g, fishingBobberEntity.prevZ, fishingBobberEntity.getZ());
		float v = (float)(o - s);
		float w = (float)(p - t) + r;
		float x = (float)(q - u);
		VertexConsumer vertexConsumer2 = vertexConsumerProvider.getBuffer(RenderLayer.getLineStrip());
		MatrixStack.Entry entry2 = matrixStack.peek();
		int y = 16;
		for (int z = 0; z <= 16; ++z) {
			renderFishingLine(v, w, x, vertexConsumer2, entry2, percentage(z, 16), percentage(z + 1, 16));
		}
		matrixStack.pop();
		super.render(fishingBobberEntity, f, g, matrixStack, vertexConsumerProvider, i);
		ci.cancel();
	}
	@Unique
	private static float percentage(int value, int max) {
		return (float)value / (float)max;
	}

	@Unique
	private static void vertex(VertexConsumer buffer, Matrix4f matrix, Matrix3f normalMatrix, int light, float x, int y, int u, int v) {
		buffer.vertex(matrix, x - 0.5f, (float)y - 0.5f, 0.0f).color(255, 255, 255, 255).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normalMatrix, 0.0f, 1.0f, 0.0f).next();
	}

	@Unique
	private static void renderFishingLine(float x, float y, float z, VertexConsumer buffer, MatrixStack.Entry matrices, float segmentStart, float segmentEnd) {
		float f = x * segmentStart;
		float g = y * (segmentStart * segmentStart + segmentStart) * 0.5f + 0.25f;
		float h = z * segmentStart;
		float i = x * segmentEnd - f;
		float j = y * (segmentEnd * segmentEnd + segmentEnd) * 0.5f + 0.25f - g;
		float k = z * segmentEnd - h;
		float l = MathHelper.sqrt(i * i + j * j + k * k);
		buffer.vertex(matrices.getPositionMatrix(), f, g, h).color(0, 0, 0, 255).normal(matrices.getNormalMatrix(), i /= l, j /= l, k /= l).next();
	}
}