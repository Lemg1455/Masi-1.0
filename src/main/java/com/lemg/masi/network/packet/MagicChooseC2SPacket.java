package com.lemg.masi.network.packet;


import com.lemg.masi.util.MagicUtil;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class MagicChooseC2SPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player,
                               ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender){
        MagicUtil.MAGIC_CHOOSE.put(player,buf.readInt());

    }
}
