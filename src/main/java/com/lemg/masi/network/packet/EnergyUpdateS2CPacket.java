package com.lemg.masi.network.packet;

import com.lemg.masi.util.MagicUtil;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketByteBuf;

public class EnergyUpdateS2CPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender sender){
        if(client!=null && client.world!=null){
            int mode = buf.readInt();
            int id = buf.readInt();
            int energy = buf.readInt();
            LivingEntity livingEntity = (LivingEntity) client.world.getEntityById(id);
            if(mode==0){
                if(MagicUtil.MAX_ENERGY.get(livingEntity)!=null){
                    if(energy>=MagicUtil.MAX_ENERGY.get(livingEntity)){
                        MagicUtil.ENERGY.put(livingEntity,MagicUtil.MAX_ENERGY.get(livingEntity));
                    }else {
                        MagicUtil.ENERGY.put(livingEntity,energy);
                    }
                }else{
                    MagicUtil.ENERGY.put(livingEntity,energy);
                }
            }
            if (mode==1) {
                MagicUtil.MAX_ENERGY.put(livingEntity,energy);
            }
        }
    }
}
