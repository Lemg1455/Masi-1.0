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
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector3f;

import java.util.concurrent.ConcurrentHashMap;

public class MagicEffectS2CPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender sender){
        if(client!=null && client.player!=null && client.world!=null){
            Object object = null;
            int type = buf.readInt();
            if(type==1){
                object = client.world.getEntityById(buf.readInt());
            }
            if(type==2){
                object = buf.readBlockPos();
            }
            if(type==3){
                object = buf.readBlockHitResult();
            }
            if(type==4){
                object = buf.readVector3f();
            }
            if(type==5){
                object = buf.readItemStack();
            }

            Magic magic = (Magic)buf.readItemStack().getItem();

            LivingEntity user = (LivingEntity) client.world.getEntityById(buf.readInt());
            int time = buf.readInt();
            magic.magicEffect(user.getMainHandStack(), client.world, user, object, time);
        }
    }
}
