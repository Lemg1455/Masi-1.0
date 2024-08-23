package com.lemg.masi.network.packet;

import com.lemg.masi.Masi;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class AddParticleS2CPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender sender){
        if(client.world!=null){
            int mode = buf.readInt();
            //用于生成法阵的粒子特效
            if(mode==0){
                double x= buf.readDouble();
                double y = buf.readDouble();
                double z = buf.readDouble();
                client.world.addParticle(Masi.CIRCLE_GROUND_BLUE, x, y, z, 0, 0, 0);
            }
            if (mode==1) {
                double x= buf.readDouble();
                double y = buf.readDouble();
                double z = buf.readDouble();
                client.world.addParticle(Masi.CIRCLE_FORWARD_BLUE, x, y, z, 0, 0, 0);
            }
        }
    }
}
