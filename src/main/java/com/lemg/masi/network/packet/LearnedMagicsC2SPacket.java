package com.lemg.masi.network.packet;


import com.lemg.masi.util.MagicUtil;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;

public class LearnedMagicsC2SPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player,
                               ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender){
        List<ItemStack> itemStacks = MagicUtil.LEARNED_MAGICS.get(player);
        if(itemStacks==null){
            itemStacks = new ArrayList<>();
        }
        ItemStack itemStack = buf.readItemStack();

        itemStacks.add(itemStack);

        MagicUtil.LEARNED_MAGICS.put(player,itemStacks);

        if(!player.getAbilities().creativeMode){
            player.addExperienceLevels(-buf.readInt());
        }
    }
}
