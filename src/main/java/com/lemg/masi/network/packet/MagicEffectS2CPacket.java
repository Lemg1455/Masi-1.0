package com.lemg.masi.network.packet;

import com.lemg.masi.item.Magics.ElementalBlessingMagic;
import com.lemg.masi.item.Magics.ImprisonMagic;
import com.lemg.masi.item.Magics.IngestionMagic;
import com.lemg.masi.item.Magics.Magic;
import com.lemg.masi.network.ModMessage;
import com.lemg.masi.util.MagicUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;

import java.util.concurrent.ConcurrentHashMap;

public class MagicEffectS2CPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender sender){
        if(client!=null && client.player!=null){
            Magic magic = (Magic) buf.readItemStack().getItem();
            if(magic instanceof ElementalBlessingMagic){
                if (MagicUtil.ENERGY.get(client.player) >= 7) {
                    int energy = MagicUtil.ENERGY.get(client.player) - 7;
                    MagicUtil.ENERGY.put(client.player,energy);
                    PacketByteBuf buf2 = PacketByteBufs.create();
                    buf2.writeInt(0);
                    buf2.writeUuid(client.player.getUuid());
                    buf2.writeInt(energy);
                    ClientPlayNetworking.send(ModMessage.ENERGY_UPDATE_ID, buf2);
                }
            }
        }

    }
}
