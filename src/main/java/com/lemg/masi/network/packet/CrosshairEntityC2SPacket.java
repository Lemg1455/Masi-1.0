package com.lemg.masi.network.packet;


import com.lemg.masi.item.Magics.CreatingWaterMagic;
import com.lemg.masi.item.Magics.HealMagic;
import com.lemg.masi.util.MagicUtil;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

public class CrosshairEntityC2SPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player,
                               ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender){
        UUID uuid = buf.readUuid();
        Entity entity = server.getOverworld().getEntity(uuid);
        ItemStack itemStack = buf.readItemStack();
        if(entity instanceof LivingEntity livingEntity){
            if(itemStack.getItem() instanceof HealMagic){
                float health = livingEntity.getHealth()+6;
                if(health >= livingEntity.getMaxHealth()){
                    livingEntity.setHealth(livingEntity.getMaxHealth());
                }else {
                    livingEntity.setHealth(health);
                }
            }else if(itemStack.getItem() instanceof CreatingWaterMagic){
                livingEntity.setAir(0);
            }
        }

    }
}
