package com.lemg.masi.network.packet;

import com.lemg.masi.Masi;
import com.lemg.masi.item.EnergyBottle;
import com.lemg.masi.util.MagicUtil;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

public class EnergyUpdateS2CPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender sender){
        int mode = buf.readInt();
        if(mode==0){
            MagicUtil.ENERGY.put(client.player,buf.readInt());
        } else if (mode==1) {
            MagicUtil.MAX_ENERGY.put(client.player,buf.readInt());
        }
    }
}
