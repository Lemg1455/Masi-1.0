package com.lemg.masi.util;

import com.lemg.masi.item.MagicGroups;
import com.lemg.masi.item.Magics.Magic;
import com.lemg.masi.network.ModMessage;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import java.util.*;

/**
 *用于记录一些信息
 */
public class MagicUtil {
    private MagicUtil(){

    }
    public static final Map<PlayerEntity, Integer> MAX_ENERGY = new HashMap<>();//魔力上限
    public static final Map<PlayerEntity, Integer> ENERGY = new HashMap<>();//当前魔力
    public static final Map<PlayerEntity, Integer> ENERGY_RESTORED = new HashMap<>();//每1秒的魔力回复
    public static final Map<PlayerEntity, Integer> MAGIC_CHOOSE = new HashMap<>();//当前选择的魔法
    public static final Map<PlayerEntity, List<ItemStack>> LEARNED_MAGICS = new HashMap<>();//已经学会的魔法
    public static final Map<PlayerEntity, List<ItemStack>> EQUIP_MAGICS = new HashMap<>();//装备在快捷栏的魔法

    public static final List<Item> magicStudy999 = studyNeed(999);//解锁要999级的魔法
    public static List<Item> studyNeed(int need){
        List<Item> items = new ArrayList<>();
        List<Item> items2 = new ArrayList<>();
        for(List<Object> magicGroup : MagicGroups.magicGroups){
            items.addAll((Collection<? extends Item>) magicGroup.get(0));
        }
        for(Item item : items){
            if(item instanceof Magic magic){
                if(magic.studyNeed()==need){
                    items2.add(item);
                }
            }
        }
        return items2;
    }

    public static List<ItemStack> getMagicStacks(List<Object> group){
        List<ItemStack> stacks = new ArrayList<>();
        List<Item> items = (List<Item>) group.get(0);
        for(Item item : items){
            stacks.add(item.getDefaultStack());
        }
        return stacks;
    }

    public static List<Item> getStacksItems(List<ItemStack> itemStacks){
        List<Item> items = new ArrayList<>();
        for(ItemStack itemStack : itemStacks){
            items.add(itemStack.getItem());
        }
        return items;
    }
    //添加地面法阵的特效
    public static void circleGround(int mode, LivingEntity user){
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(mode);
        buf.writeDouble(user.getX());
        buf.writeDouble(user.getY());
        buf.writeDouble(user.getZ());
        for (ServerPlayerEntity players : PlayerLookup.tracking((ServerWorld) user.getWorld(), user.getBlockPos())) {
            ServerPlayNetworking.send((ServerPlayerEntity) players, ModMessage.ADD_PARTICLE_ID, buf);
        }
    }

    //添加面前法阵的特效
    public static void circleForward(int mode, LivingEntity user){
        double yawRadians = Math.toRadians(user.getYaw()+90);
        double forwardX = user.getX() + Math.cos(yawRadians) * 1;
        double forwardZ = user.getZ() + Math.sin(yawRadians) * 1;
        //world.addParticle(Masi.CIRCLE_FORWARD_BLUE, forwardX, player.getY()+1.5, forwardZ, 0, 0, 0);
        PacketByteBuf buf2 = PacketByteBufs.create();
        buf2.writeInt(mode);
        buf2.writeDouble(forwardX);
        buf2.writeDouble(user.getY()+2);
        buf2.writeDouble(forwardZ);
        for (ServerPlayerEntity players : PlayerLookup.tracking((ServerWorld) user.getWorld(), user.getBlockPos())) {
            ServerPlayNetworking.send((ServerPlayerEntity) players, ModMessage.ADD_PARTICLE_ID, buf2);
        }
    }

}
