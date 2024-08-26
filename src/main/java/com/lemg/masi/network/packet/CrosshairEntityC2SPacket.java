package com.lemg.masi.network.packet;


import com.lemg.masi.item.Magics.CreatingWaterMagic;
import com.lemg.masi.item.Magics.DimensionExileMagic;
import com.lemg.masi.item.Magics.HealMagic;
import com.lemg.masi.util.MagicUtil;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

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
                livingEntity.getWorld().setBlockState(livingEntity.getBlockPos().add(0,1,0), Blocks.WATER.getDefaultState());
            }else if(itemStack.getItem() instanceof DimensionExileMagic dimensionExileMagic){
                if (livingEntity.getWorld() instanceof ServerWorld serverWorld) {
                    ServerWorld serverWorld2 = serverWorld.getServer().getWorld(World.NETHER);
                    if (serverWorld2 == null) {
                        return;
                    }
                    dimensionExileMagic.moveToWorld(serverWorld,livingEntity);
                    livingEntity.playSound(SoundEvents.ENTITY_ENDERMAN_DEATH, 1.0f, livingEntity.getSoundPitch());
                }
            }
        }

    }
}
