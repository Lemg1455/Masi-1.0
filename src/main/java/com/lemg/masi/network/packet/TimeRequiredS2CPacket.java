package com.lemg.masi.network.packet;

import com.lemg.masi.item.Magics.Magic;
import com.lemg.masi.util.MagicUtil;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;

import java.util.List;

public class TimeRequiredS2CPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender sender){
        int time_required = buf.readInt();
        if(MagicUtil.TIME_REQUIRED.get(client.player)!=null) {
            List<Object> list = MagicUtil.TIME_REQUIRED.get(client.player);
            list.set(1,time_required);
            MagicUtil.TIME_REQUIRED.put(client.player,list);
        }
    }
}
