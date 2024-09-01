package com.lemg.masi.network.packet;


import com.lemg.masi.item.Magics.CreatingWaterMagic;
import com.lemg.masi.item.Magics.HealMagic;
import com.lemg.masi.util.MagicUtil;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

public class EnergyUpdateC2SPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player,
                               ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender){
        int mode = buf.readInt();
        UUID uuid = buf.readUuid();
        int energy = buf.readInt();
        LivingEntity livingEntity = (LivingEntity) server.getOverworld().getEntity(uuid);
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
