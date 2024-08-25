package com.lemg.masi.network;

import com.lemg.masi.Masi;
import com.lemg.masi.network.packet.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class ModMessage {

    public static final Identifier ADD_PARTICLE_ID = new Identifier(Masi.MOD_ID,"add_particle");//粒子
    public static final Identifier EQUIP_MAGICS_ID = new Identifier(Masi.MOD_ID,"equip_magics");//魔法装配同步
    public static final Identifier LEARNED_MAGICS_ID = new Identifier(Masi.MOD_ID,"learned_magics");//学习同步
    public static final Identifier ENERGY_UPDATE_ID = new Identifier(Masi.MOD_ID,"energy_update");//魔力同步
    public static final Identifier MAGIC_CHOOSE_ID = new Identifier(Masi.MOD_ID,"magic_choose");//选择魔法同步
    public static final Identifier CROSSHAIR_ENTITY_ID = new Identifier(Masi.MOD_ID,"crosshair_entity");//获取指向生物
    public static final Identifier CROSSHAIR_BLOCK_ID = new Identifier(Masi.MOD_ID,"crosshair_block");//获取指向方块
    public static final Identifier TIME_REQUIRED_ID = new Identifier(Masi.MOD_ID,"time_required");//获取指向方块

    public static void registerC2SPackets(){
        ServerPlayNetworking.registerGlobalReceiver(ADD_PARTICLE_ID, AddParticleC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(EQUIP_MAGICS_ID, EquipMagicsC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(LEARNED_MAGICS_ID, LearnedMagicsC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(MAGIC_CHOOSE_ID, MagicChooseC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(CROSSHAIR_ENTITY_ID, CrosshairEntityC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(CROSSHAIR_BLOCK_ID, CrosshairBlockC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(ENERGY_UPDATE_ID, EnergyUpdateC2SPacket::receive);

    }
    public static void registerS2CPackets(){
        ClientPlayNetworking.registerGlobalReceiver(ADD_PARTICLE_ID, AddParticleS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(ENERGY_UPDATE_ID, EnergyUpdateS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(TIME_REQUIRED_ID, TimeRequiredS2CPacket::receive);

    }
}
