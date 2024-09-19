package com.lemg.masi.util;

import com.lemg.masi.entity.entities.minions.Minion;
import com.lemg.masi.item.MagicGroups;
import com.lemg.masi.item.Magics.Magic;
import com.lemg.masi.item.items.TrialCard;
import com.lemg.masi.network.ModMessage;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 *用于维持、处理数据
 */
public class MagicUtil {
    private MagicUtil(){

    }
    public static final Map<LivingEntity, Integer> MAX_ENERGY = new HashMap<>();//魔力上限
    public static final Map<LivingEntity, Integer> ENERGY = new HashMap<>();//当前魔力
    public static final Map<LivingEntity, Integer> ENERGY_RESTORED = new HashMap<>();//每1秒的魔力回复
    public static final Map<PlayerEntity, Integer> MAGIC_CHOOSE = new HashMap<>();//当前选择的魔法
    public static final Map<PlayerEntity, List<Object>> TIME_REQUIRED = new HashMap<>();//魔法释放后持续的时间，如果它是持续攻击的话

    //如果你希望魔法的效果不是立即生效的，或者应该生效一段时间的，Map<世界,Map<目标，Map<施加者，Map<魔法，持续的时间>>>>
    public static final ConcurrentHashMap<World,ConcurrentHashMap<Object, ConcurrentHashMap<LivingEntity,ConcurrentHashMap<Magic,Integer>>>> EFFECT = new ConcurrentHashMap<>();

    public static final Map<PlayerEntity, List<ItemStack>> LEARNED_MAGICS = new HashMap<>();//已经学会的魔法
    public static final Map<PlayerEntity, List<Item>> EQUIP_MAGICS = new HashMap<>();//装备在快捷栏的魔法

    public static final List<Item> magicStudy999 = studyNeed(999);//解锁要999级的魔法

    public static void putEffect(World world,Object object,LivingEntity livingEntity,Magic magic,int tick){
        ConcurrentHashMap<Magic,Integer> map1 = new ConcurrentHashMap<>();
        map1.put(magic,tick);
        ConcurrentHashMap<LivingEntity, ConcurrentHashMap<Magic,Integer>> map2 = new ConcurrentHashMap<>();
        map2.put(livingEntity,map1);
        ConcurrentHashMap<Object,ConcurrentHashMap<LivingEntity, ConcurrentHashMap<Magic,Integer>>> map3 = new ConcurrentHashMap<>();
        map3.put(object,map2);
        if(MagicUtil.EFFECT.get(world)==null){
            MagicUtil.EFFECT.put(world,map3);
        }else if(MagicUtil.EFFECT.get(world).get(object)==null){
            MagicUtil.EFFECT.get(world).put(object,map2);
        }else if(MagicUtil.EFFECT.get(world).get(object).get(livingEntity)==null){
            MagicUtil.EFFECT.get(world).get(object).put(livingEntity,map1);
        }else {
            MagicUtil.EFFECT.get(world).get(object).get(livingEntity).put(magic,tick);
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

    public static List<ItemStack> getItemsStacks(List<Item> items){

        if(items!=null && !items.isEmpty()){
            List<ItemStack> stacks = new ArrayList<>();
            for(Item item : items){
                stacks.add(item.getDefaultStack());
            }
            return stacks;
        }
        return null;
    }

    public static List<Item> getStacksItems(List<ItemStack> itemStacks){
        List<Item> items = new ArrayList<>();
        for(ItemStack itemStack : itemStacks){
            items.add(itemStack.getItem());
        }
        return items;
    }

    public static boolean isTrial(PlayerEntity player){
        if(MagicUtil.EFFECT.get(player.getWorld())!=null) {
            ConcurrentHashMap<LivingEntity, ConcurrentHashMap<Magic, Integer>> map2 = MagicUtil.EFFECT.get(player.getWorld()).get(player);
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
        List<ItemStack> inventory = getItemsStacks(MagicUtil.EQUIP_MAGICS.get(player));
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
            int i = MagicUtil.MAGIC_CHOOSE.get(player);
            if(i<magics.size()){
                return magics.get(i);
            }
        }
        return null;
    }

    public static void energyUpdate(LivingEntity aim,int energy,Boolean maxEnergy){
        if(!aim.getWorld().isClient()){
            int mode = 0;
            if(maxEnergy){
                mode = 1;
                MagicUtil.MAX_ENERGY.put(aim,energy);
            }else {
                MagicUtil.ENERGY.put(aim,energy);
            }

            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(mode);
            buf.writeInt(aim.getId());
            buf.writeInt(energy);
            for(ServerPlayerEntity player : ((ServerWorld)(aim.getWorld())).getPlayers()){
                ServerPlayNetworking.send((ServerPlayerEntity) player,ModMessage.ENERGY_UPDATE_ID,buf);
            }
        }
    }

    public static boolean teamEntity(LivingEntity livingEntity,LivingEntity Owner){
        if(livingEntity instanceof Minion minion){
            if(minion.getOwner()==Owner){
                return true;
            }
        }
        if(livingEntity instanceof Tameable tameable){
            if(tameable.getOwner()==Owner){
                return true;
            }
        }
        if(livingEntity instanceof TameableEntity tameable){
            if(tameable.getOwner()==Owner){
                return true;
            }
        }
        NbtCompound nbt = livingEntity.writeNbt(new NbtCompound());
        if(nbt.contains("Owner") && nbt.getUuid("Owner")!=null){
            if(nbt.getUuid("Owner")==Owner.getUuid()){
                return true;
            }
        }
        return false;
    }
    public static List<StatusEffect> beneficial = Arrays.asList(StatusEffects.SPEED,StatusEffects.HASTE,StatusEffects.STRENGTH,StatusEffects.INSTANT_HEALTH,StatusEffects.JUMP_BOOST,StatusEffects.REGENERATION,StatusEffects.RESISTANCE,StatusEffects.FIRE_RESISTANCE,StatusEffects.WATER_BREATHING,StatusEffects.INVISIBILITY,StatusEffects.NIGHT_VISION,StatusEffects.HEALTH_BOOST,StatusEffects.ABSORPTION,StatusEffects.SATURATION,StatusEffects.LUCK,StatusEffects.SLOW_FALLING,StatusEffects.CONDUIT_POWER,StatusEffects.DOLPHINS_GRACE);
    public static List<StatusEffect> harmful = Arrays.asList(StatusEffects.SLOWNESS,StatusEffects.MINING_FATIGUE,StatusEffects.INSTANT_DAMAGE,StatusEffects.NAUSEA,StatusEffects.BLINDNESS,StatusEffects.HUNGER,StatusEffects.WEAKNESS,StatusEffects.POISON,StatusEffects.WITHER,StatusEffects.LEVITATION,StatusEffects.UNLUCK);

}
