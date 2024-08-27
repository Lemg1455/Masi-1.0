package com.lemg.masi.util;

import com.lemg.masi.item.MagicGroups;
import com.lemg.masi.item.Magics.Magic;
import com.lemg.masi.item.TrialCard;
import com.lemg.masi.network.ModMessage;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import javax.swing.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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
    public static final Map<PlayerEntity, List<Object>> TIME_REQUIRED = new HashMap<>();//魔法释放后持续的时间，如果它是持续攻击的话

    //如果你希望魔法的效果不是立即生效的，或者应该生效一段时间的，Map<释放者，Map<目标，Map<魔法，持续的时间>>>
    public static final ConcurrentHashMap<PlayerEntity, ConcurrentHashMap<Object,ConcurrentHashMap<Magic,Integer>>> EFFECT = new ConcurrentHashMap<>();

    public static final Map<PlayerEntity, List<ItemStack>> LEARNED_MAGICS = new HashMap<>();//已经学会的魔法
    public static final Map<PlayerEntity, List<ItemStack>> EQUIP_MAGICS = new HashMap<>();//装备在快捷栏的魔法

    public static final List<Item> magicStudy999 = studyNeed(999);//解锁要999级的魔法

    public static void putEffect(PlayerEntity player,Object object,Magic magic,int tick){
        ConcurrentHashMap<Magic,Integer> map1 = new ConcurrentHashMap<>();
        map1.put(magic,tick);
        ConcurrentHashMap<Object, ConcurrentHashMap<Magic,Integer>> map2 = new ConcurrentHashMap<>();
        map2.put(object,map1);
        if(MagicUtil.EFFECT.get(player)==null){
            MagicUtil.EFFECT.put(player,map2);
        }else if(MagicUtil.EFFECT.get(player).get(object)==null){
            MagicUtil.EFFECT.get(player).put(object,map1);
        }else {
            MagicUtil.EFFECT.get(player).get(object).put(magic,tick);
        }
    }
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
    public static boolean isTrial(PlayerEntity player){
        if(MagicUtil.EFFECT.get(player)!=null) {
            ConcurrentHashMap<Object, ConcurrentHashMap<Magic, Integer>> map2 = MagicUtil.EFFECT.get(player);
            if(map2!=null){
                ConcurrentHashMap<Magic, Integer> map1 = map2.get(player);
                if(map1!=null){
                    for(Magic magic1 : map1.keySet()){
                        if(magic1 instanceof TrialCard){
                            if(map1.get(magic1)!=null){
                                if(map1.get(magic1)>=0){
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    //获取当前选择的魔法
    public static ItemStack MagicNow(PlayerEntity player){
        List<ItemStack> inventory = MagicUtil.EQUIP_MAGICS.get(player);
        if(inventory==null){
            return null;
        }
        List<ItemStack> magics = new ArrayList<>();

        for(ItemStack itemStack : inventory){
            if(!itemStack.isEmpty()){
                magics.add(itemStack);
            }
        }

        if(!magics.isEmpty()){
            return magics.get(MagicUtil.MAGIC_CHOOSE.get(player));
        }
        return null;
    }

    public static List<StatusEffect> beneficial = Arrays.asList(StatusEffects.SPEED,StatusEffects.HASTE,StatusEffects.STRENGTH,StatusEffects.INSTANT_HEALTH,StatusEffects.JUMP_BOOST,StatusEffects.REGENERATION,StatusEffects.RESISTANCE,StatusEffects.FIRE_RESISTANCE,StatusEffects.WATER_BREATHING,StatusEffects.INVISIBILITY,StatusEffects.NIGHT_VISION,StatusEffects.HEALTH_BOOST,StatusEffects.ABSORPTION,StatusEffects.SATURATION,StatusEffects.LUCK,StatusEffects.SLOW_FALLING,StatusEffects.CONDUIT_POWER,StatusEffects.DOLPHINS_GRACE);
    public static List<StatusEffect> harmful = Arrays.asList(StatusEffects.SLOWNESS,StatusEffects.MINING_FATIGUE,StatusEffects.INSTANT_DAMAGE,StatusEffects.NAUSEA,StatusEffects.BLINDNESS,StatusEffects.HUNGER,StatusEffects.WEAKNESS,StatusEffects.POISON,StatusEffects.WITHER,StatusEffects.LEVITATION,StatusEffects.UNLUCK);

}
