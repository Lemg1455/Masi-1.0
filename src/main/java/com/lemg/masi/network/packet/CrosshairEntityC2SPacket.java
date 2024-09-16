package com.lemg.masi.network.packet;


import com.lemg.masi.item.items.MageCertificate;
import com.lemg.masi.item.Magics.*;
import com.lemg.masi.network.ModMessage;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Random;
import java.util.UUID;

public class CrosshairEntityC2SPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player,
                               ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender){
        UUID uuid = buf.readUuid();
        Entity entity = server.getOverworld().getEntity(uuid);
        uuid = buf.readUuid();
        PlayerEntity playerEntity = server.getPlayerManager().getPlayer(uuid);
        ItemStack itemStack = buf.readItemStack();
        if(entity instanceof LivingEntity livingEntity && playerEntity!=null){
            if(itemStack.getItem() instanceof HealMagic){
                float health = livingEntity.getHealth()+6;
                if(health >= livingEntity.getMaxHealth()){
                    livingEntity.setHealth(livingEntity.getMaxHealth());
                }else {
                    livingEntity.setHealth(health);
                }
                Vec3d direction = livingEntity.getPos().add(0,1,0).subtract(playerEntity.getPos()).normalize();
                double length = livingEntity.getPos().add(0,1,0).distanceTo(playerEntity.getPos());
                for (int i = 0; i <= 10; i++) {
                    double fraction = (double) i / 10;
                    Vec3d particlePos = playerEntity.getPos().add(direction.multiply(fraction * length));
                    ((ServerWorld)livingEntity.getWorld()).spawnParticles(new DustParticleEffect(Vec3d.unpackRgb(0x00FF90).toVector3f(), 1.0f), particlePos.x, particlePos.y, particlePos.z, 0, 0, 0.0, 0, 0.0);

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
            }else if(itemStack.getItem() instanceof ImprisonMagic){
                livingEntity.setVelocity(0,0,0);
                playerEntity.setVelocity(0,0,0);

                Vec3d Pos1 = livingEntity.getPos().add(0,1,0);
                Vec3d Pos2 = playerEntity.getPos().add(0,1,0);
                Vec3d direction = Pos1.subtract(Pos2).normalize();
                double length = Pos1.distanceTo(Pos2);

                for (int i = 0; i <= 10; i++) {
                    double fraction = (double) i / 10;
                    Vec3d particlePos = Pos2.add(direction.multiply(fraction * length));
                    ((ServerWorld)livingEntity.getWorld()).spawnParticles(new DustParticleEffect(Vec3d.unpackRgb(0xFFFFFF).toVector3f(), 1.0f), particlePos.x, particlePos.y, particlePos.z, 0, 0, 0.0, 0, 0.0);

                }

                if(livingEntity instanceof PlayerEntity player1){
                    PacketByteBuf buf2 = PacketByteBufs.create();
                    buf2.writeItemStack(itemStack);
                    buf2.writeDouble(0);
                    buf2.writeDouble(0);
                    buf2.writeDouble(0);
                    ServerPlayNetworking.send((ServerPlayerEntity) player1, ModMessage.VELOCITY_UPDATE_ID, buf2);
                }

            }else if(itemStack.getItem() instanceof StealMagic){
                if(livingEntity instanceof PlayerEntity playerEntity1){
                    if(!playerEntity1.getInventory().isEmpty()){
                        Random random = new Random();
                        ItemStack itemStack1 = ItemStack.EMPTY;
                        int solt = 0;
                        while (itemStack1.isEmpty()){
                            solt = random.nextInt(playerEntity1.getInventory().size());
                            itemStack1 = playerEntity1.getInventory().getStack(solt);
                            if(itemStack1.getItem() instanceof MageCertificate){
                                itemStack1 = ItemStack.EMPTY;
                            }
                        }
                        playerEntity1.getInventory().removeStack(solt);
                        if(playerEntity.getInventory().getEmptySlot()!=-1){
                            playerEntity.getInventory().insertStack(itemStack1);
                        }else {
                            playerEntity.dropStack(itemStack1);
                        }
                    }
                /*} else if (livingEntity instanceof PiglinEntity piglin) {
                    if(!piglin.getInventory().isEmpty()){
                        Random random = new Random();
                        ItemStack itemStack1 = ItemStack.EMPTY;
                        int solt = 0;
                        while (itemStack1.isEmpty()){
                            solt = random.nextInt(piglin.getInventory().size());
                            itemStack1 = piglin.getInventory().getStack(solt);
                        }
                        piglin.getInventory().removeStack(solt);
                        if(playerEntity.getInventory().getEmptySlot()!=-1){
                            playerEntity.getInventory().insertStack(itemStack1);
                        }else {
                            playerEntity.dropStack(itemStack1);
                        }
                    }*/
                }else {
                    ItemStack itemStack1;
                    for(EquipmentSlot equipmentSlot : EquipmentSlot.values()){
                        if(livingEntity.hasStackEquipped(equipmentSlot)){
                            itemStack1 = livingEntity.getEquippedStack(equipmentSlot);
                            livingEntity.equipStack(equipmentSlot,ItemStack.EMPTY);
                            if(playerEntity.getInventory().getEmptySlot()!=-1){
                                playerEntity.getInventory().insertStack(itemStack1);
                            }else {
                                playerEntity.dropStack(itemStack1);
                            }
                            break;
                        }
                    }
                }
            }
        }
    }
}
