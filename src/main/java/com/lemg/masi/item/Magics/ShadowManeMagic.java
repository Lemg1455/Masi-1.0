package com.lemg.masi.item.Magics;

import com.lemg.masi.entity.MagicBulletEntity;
import com.lemg.masi.item.ModItems;
import com.lemg.masi.network.ModMessage;
import com.lemg.masi.util.MagicUtil;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ShadowManeMagic extends Magic{

    public ShadowManeMagic(Settings settings) {
        super(settings);
    }
    @Override
    public int singFinishTick(){
        return 40;
    }

    @Override
    public int energyConsume(){
        return 40;
    }
    @Override
    public int studyNeed(){
        return 10;
    }
    @Override
    public boolean Multiple(){
        return false;
    }
    @Override
    public int releaseContinueTime(){
        return 0;
    }

    public ConcurrentHashMap<LivingEntity,List<LivingEntity>> Missile = new ConcurrentHashMap<>();

    @Override
    public void release(ItemStack stack, World world, LivingEntity user, float singingTicks){
        if(world.isClient()){
            return;
        }
        List<Entity> list = world.getOtherEntities(user, user.getBoundingBox().expand(20,20,20));
        List<LivingEntity> list2 = new ArrayList<>();
        if(!list.isEmpty()){
            for(Entity entity : list){
                if(entity instanceof LivingEntity livingEntity){
                    list2.add(livingEntity);
                }
            }
            Missile.put(user,list2);
            //MagicUtil.putEffect(user,user,this,list.size()*10);
        }
    }
    @Override
    public void onSinging(ItemStack stack, World world, LivingEntity user, float singingTicks){
        if(!user.getWorld().isClient()){
            MagicUtil.circleGround(10,user,user.getX(),user.getY(),user.getZ());
            if(user.getItemUseTime() >= singFinishTick()){
                double yawRadians = Math.toRadians(user.getYaw()+90);
                double x = user.getX() + Math.cos(yawRadians) * 2;
                double z = user.getZ() + Math.sin(yawRadians) * 2;
                double y = user.getY()+4;
                MagicUtil.circleForward(11,user,x,y,z);
            }
        }
        List<Entity> list = world.getOtherEntities(user, user.getBoundingBox().expand(20,20,20));
        if(!list.isEmpty()){
            for(Entity entity : list) {
                if (entity instanceof LivingEntity livingEntity) {
                    livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 10, 2,false,false,false));
                }
            }
        }
    }
    @Override
    public void magicEffect(ItemStack staffStack, World world, LivingEntity user, Object aim,float ticks){
        if(ticks%10==0 && user instanceof PlayerEntity player){
            List<LivingEntity> list = Missile.get(player);
            if(list!=null && !list.isEmpty()){
                LivingEntity livingEntity = list.get(0);
                if(livingEntity.isAlive()){
                    user.teleport(livingEntity.getX(),livingEntity.getY(),livingEntity.getZ(),true);
                    float amount = 10;
                    livingEntity.damage(player.getWorld().getDamageSources().mobAttack(player), amount);
                }
                list.remove(0);
                Missile.put(player,list);
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.shadow_mane_magic.tooltip"));
        super.appendTooltip(stack,world,tooltip,context);
    }
}
