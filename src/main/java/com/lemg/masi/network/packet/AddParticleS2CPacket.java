package com.lemg.masi.network.packet;

import com.lemg.masi.Masi;
import com.lemg.masi.particles.Circle_Forward_Particle;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class AddParticleS2CPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender sender){
        if(client.world!=null){
            int mode = buf.readInt();
            double x= buf.readDouble();
            double y = buf.readDouble();
            double z = buf.readDouble();
            //用于生成法阵的粒子特效
            if(mode==0){
                client.world.addParticle(Masi.CIRCLE_GROUND_BLUE,x,y,z,0,0,0);
            }
            if (mode==1) {
                client.world.addParticle(Masi.CIRCLE_FORWARD_BLUE,x,y,z,0,0,0);
            }
            if(mode==2){
                client.world.addParticle(Masi.LARGE_CIRCLE_GROUND_BLUE, x, y, z,0,0,0);
            }
            if (mode==3) {
                client.world.addParticle(Masi.LARGE_CIRCLE_FORWARD_BLUE, x, y, z,0,0,0);
            }

            if(mode==4){
                client.world.addParticle(Masi.CIRCLE_GROUND_BLACK,x,y,z,0,0,0);
            }
            if (mode==5) {
                client.world.addParticle(Masi.CIRCLE_FORWARD_BLACK,x,y,z,0,0,0);
            }
            if(mode==6){
                client.world.addParticle(Masi.LARGE_CIRCLE_GROUND_BLACK, x, y, z,0,0,0);
            }
            if (mode==7) {
                client.world.addParticle(Masi.LARGE_CIRCLE_FORWARD_BLACK, x, y, z,0,0,0);
            }

            if(mode==8){
                client.world.addParticle(Masi.CIRCLE_GROUND_WHITE,x,y,z,0,0,0);
            }
            if (mode==9) {
                client.world.addParticle(Masi.CIRCLE_FORWARD_WHITE,x,y,z,0,0,0);
            }
            if(mode==10){
                client.world.addParticle(Masi.LARGE_CIRCLE_GROUND_WHITE, x, y, z,0,0,0);
            }
            if (mode==11) {
                client.world.addParticle(Masi.LARGE_CIRCLE_FORWARD_WHITE, x, y, z,0,0,0);
            }

            if(mode==12){
                client.world.addParticle(Masi.CIRCLE_GROUND_RED,x,y,z,0,0,0);
            }
            if (mode==13) {
                client.world.addParticle(Masi.CIRCLE_FORWARD_RED,x,y,z,0,0,0);
            }
            if(mode==14){
                client.world.addParticle(Masi.LARGE_CIRCLE_GROUND_RED, x, y, z,0,0,0);
            }
            if (mode==15) {
                client.world.addParticle(Masi.LARGE_CIRCLE_FORWARD_RED, x, y, z,0,0,0);
            }

            if(mode==16){
                client.world.addParticle(Masi.CIRCLE_GROUND_GREEN,x,y,z,0,0,0);
            }
            if (mode==17) {
                client.world.addParticle(Masi.CIRCLE_FORWARD_GREEN,x,y,z,0,0,0);
            }
            if(mode==18){
                client.world.addParticle(Masi.LARGE_CIRCLE_GROUND_GREEN, x, y, z,0,0,0);
            }
            if (mode==19) {
                client.world.addParticle(Masi.LARGE_CIRCLE_FORWARD_GREEN, x, y, z,0,0,0);
            }

            if(mode==20){
                client.world.addParticle(Masi.CIRCLE_GROUND_PURPLE,x,y,z,0,0,0);
            }
            if (mode==21) {
                client.world.addParticle(Masi.CIRCLE_FORWARD_PURPLE,x,y,z,0,0,0);
            }
            if(mode==22){
                client.world.addParticle(Masi.LARGE_CIRCLE_GROUND_PURPLE, x, y, z,0,0,0);
            }
            if (mode==23) {
                client.world.addParticle(Masi.LARGE_CIRCLE_FORWARD_PURPLE, x, y, z,0,0,0);
            }

            if(mode==24){
                client.world.addParticle(Masi.CIRCLE_GROUND_YELLOW,x,y,z,0,0,0);
            }
            if (mode==25) {
                client.world.addParticle(Masi.CIRCLE_FORWARD_YELLOW,x,y,z,0,0,0);
            }
            if(mode==26){
                client.world.addParticle(Masi.LARGE_CIRCLE_GROUND_YELLOW, x, y, z,0,0,0);
            }
            if (mode==27) {
                client.world.addParticle(Masi.LARGE_CIRCLE_FORWARD_YELLOW, x, y, z,0,0,0);
            }
            if (mode==100) {
                client.world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(0xDC71E8).toVector3f(), 5.0f), x, y, z,0,0,0);
                client.world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(0xDC71E8).toVector3f(), 5.0f), x-1,y,z,0,0,0);
                client.world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(0xDC71E8).toVector3f(), 5.0f), x+1,y,z,0,0,0);
                client.world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(0xDC71E8).toVector3f(), 5.0f), x, y, z-1,0,0,0);
                client.world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(0xDC71E8).toVector3f(), 5.0f), x, y, z+1,0,0,0);
                client.world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(0xDC71E8).toVector3f(), 5.0f), x, y-1,z,0,0,0);
                client.world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(0xDC71E8).toVector3f(), 5.0f), x, y+1,z,0,0,0);
            }
            if (mode==101) {
                client.world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(0xFFFFFF).toVector3f(), 1.0f), x, y, z,0,0,0);
            }
            if (mode==102) {
                client.world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(0x000000).toVector3f(), 1.0f), x, y, z,0,0,0);
            }
            if (mode==103) {
                client.world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(0xDC71E8).toVector3f(), 5.0f), x, y+0.5, z,0,0,0);
                client.world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(0xDC71E8).toVector3f(), 5.0f), x-0.5,y+1,z,0,0,0);
                client.world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(0xDC71E8).toVector3f(), 5.0f), x+0.5,y+1,z,0,0,0);
                client.world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(0xDC71E8).toVector3f(), 5.0f), x, y+1, z-0.5,0,0,0);
                client.world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(0xDC71E8).toVector3f(), 5.0f), x, y+1, z+0.5,0,0,0);
            }
            if (mode==104) {
                client.world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(0x00D1FF).toVector3f(), 5.0f), x, y+0.5, z,0,0,0);
                client.world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(0x00D1FF).toVector3f(), 5.0f), x-0.5,y+1,z,0,0,0);
                client.world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(0x00D1FF).toVector3f(), 5.0f), x+0.5,y+1,z,0,0,0);
                client.world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(0x00D1FF).toVector3f(), 5.0f), x, y+1, z-0.5,0,0,0);
                client.world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(0x00D1FF).toVector3f(), 5.0f), x, y+1, z+0.5,0,0,0);
            }

            if (mode==105) {
                client.world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(0xFFFD6D).toVector3f(), 5.0f), x, y, z,0,0,0);
                client.world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(0xFFFD6D).toVector3f(), 5.0f), x-1, y, z,0,0,0);
                client.world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(0xFFFD6D).toVector3f(), 5.0f), x+1, y, z,0,0,0);
                client.world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(0xFFFD6D).toVector3f(), 5.0f), x, y, z-1,0,0,0);
                client.world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(0xFFFD6D).toVector3f(), 5.0f), x, y, z+1,0,0,0);
                client.world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(0xFFFD6D).toVector3f(), 5.0f), x-1, y, z-1,0,0,0);
                client.world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(0xFFFD6D).toVector3f(), 5.0f), x+1, y, z+1,0,0,0);
                client.world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(0xFFFD6D).toVector3f(), 5.0f), x-1, y, z+1,0,0,0);
                client.world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(0xFFFD6D).toVector3f(), 5.0f), x+1, y, z-1,0,0,0);

            }
            if (mode==106) {
                client.world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(0x00D1FF).toVector3f(), 5.0f), x, y, z,0,0,0);
                client.world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(0x00D1FF).toVector3f(), 5.0f), x-1, y, z,0,0,0);
                client.world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(0x00D1FF).toVector3f(), 5.0f), x+1, y, z,0,0,0);
                client.world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(0x00D1FF).toVector3f(), 5.0f), x, y, z-1,0,0,0);
                client.world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(0x00D1FF).toVector3f(), 5.0f), x, y, z+1,0,0,0);
                client.world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(0x00D1FF).toVector3f(), 5.0f), x-1, y, z-1,0,0,0);
                client.world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(0x00D1FF).toVector3f(), 5.0f), x+1, y, z+1,0,0,0);
                client.world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(0x00D1FF).toVector3f(), 5.0f), x-1, y, z+1,0,0,0);
                client.world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(0x00D1FF).toVector3f(), 5.0f), x+1, y, z-1,0,0,0);
            }
            if (mode==107) {
                client.world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(0xFF6D80).toVector3f(), 5.0f), x, y, z,0,0,0);
                client.world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(0xFF6D80).toVector3f(), 5.0f), x-1, y, z,0,0,0);
                client.world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(0xFF6D80).toVector3f(), 5.0f), x+1, y, z,0,0,0);
                client.world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(0xFF6D80).toVector3f(), 5.0f), x, y, z-1,0,0,0);
                client.world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(0xFF6D80).toVector3f(), 5.0f), x, y, z+1,0,0,0);
                client.world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(0xFF6D80).toVector3f(), 5.0f), x-1, y, z-1,0,0,0);
                client.world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(0xFF6D80).toVector3f(), 5.0f), x+1, y, z+1,0,0,0);
                client.world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(0xFF6D80).toVector3f(), 5.0f), x-1, y, z+1,0,0,0);
                client.world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(0xFF6D80).toVector3f(), 5.0f), x+1, y, z-1,0,0,0);

            }
            if (mode==108) {
                client.world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(0xFFFFFF).toVector3f(), 5.0f), x, y, z,0,0,0);
                client.world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(0xFFFFFF).toVector3f(), 5.0f), x-1, y, z,0,0,0);
                client.world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(0xFFFFFF).toVector3f(), 5.0f), x+1, y, z,0,0,0);
                client.world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(0xFFFFFF).toVector3f(), 5.0f), x, y, z-1,0,0,0);
                client.world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(0xFFFFFF).toVector3f(), 5.0f), x, y, z+1,0,0,0);
                client.world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(0xFFFFFF).toVector3f(), 5.0f), x-1, y, z-1,0,0,0);
                client.world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(0xFFFFFF).toVector3f(), 5.0f), x+1, y, z+1,0,0,0);
                client.world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(0xFFFFFF).toVector3f(), 5.0f), x-1, y, z+1,0,0,0);
                client.world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(0xFFFFFF).toVector3f(), 5.0f), x+1, y, z-1,0,0,0);

            }
            if (mode==109) {
                client.world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(0xDC71E8).toVector3f(), 1.0f), x, y, z,0,0,0);
            }
            if (mode==110) {
                client.world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(0x00D1FF).toVector3f(), 1.0f), x, y, z,0,0,0);
            }
            if (mode==111) {
                client.world.addParticle(new DustParticleEffect(Vec3d.unpackRgb(0x00FF90).toVector3f(), 1.0f), x, y, z,0,0,0);
            }
        }
    }
}
