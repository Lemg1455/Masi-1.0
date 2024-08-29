package com.lemg.masi.network.packet;

import com.lemg.masi.Masi;
import com.lemg.masi.item.Magics.ImprisonMagic;
import com.lemg.masi.item.Magics.IngestionMagic;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.util.math.Vec3d;

public class VelocityUpdateS2CPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender sender){
        if(client.world!=null && client.player!=null){
            ItemStack magic = buf.readItemStack();
            double x= buf.readDouble();
            double y = buf.readDouble();
            double z = buf.readDouble();

            if(magic.getItem() instanceof IngestionMagic){
                client.player.setVelocity(x,y,z);
            }
            if(magic.getItem() instanceof ImprisonMagic){
                client.player.setVelocity(x,y,z);
            }
        }
    }
}
