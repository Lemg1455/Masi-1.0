package com.lemg.masi.particles;

import com.lemg.masi.item.Magics.ArcaneMissileMagic;
import com.lemg.masi.util.MagicUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

@Environment(value= EnvType.CLIENT)
public class Circle_Forward_Particle extends SonicBoomParticle {

    protected Circle_Forward_Particle(ClientWorld clientWorld, double d, double e, double f, double g, SpriteProvider spriteProvider) {
        super(clientWorld, d, e, f, g, spriteProvider);

        this.maxAge = 1;
        this.scale = 0.6f;

        this.setSpriteForAge(spriteProvider);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_LIT;
    }

    @Environment(value=EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }


        @Override
        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            Circle_Forward_Particle circleForwardParticle = new Circle_Forward_Particle(clientWorld, d, e, f, g, this.spriteProvider);
            circleForwardParticle.setSprite(this.spriteProvider);
            return circleForwardParticle;
        }

        /*@Override
        public  synthetic  Particle createParticle(ParticleEffect particleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return this.createParticle((DefaultParticleType)particleEffect, clientWorld, d, e, f, g, h, i);
        }*/
    }

    @Environment(value=EnvType.CLIENT)
    public static class LargeFactory
            implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public LargeFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            Circle_Forward_Particle circleForwardParticle = new Circle_Forward_Particle(clientWorld, d, e, f, g, this.spriteProvider);
            circleForwardParticle.scale(5.0f);
            circleForwardParticle.setSprite(this.spriteProvider);
            return circleForwardParticle;
        }

    }
}