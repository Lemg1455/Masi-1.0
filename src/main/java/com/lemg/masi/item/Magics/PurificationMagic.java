package com.lemg.masi.item.Magics;

import com.lemg.masi.network.ModMessage;
import com.lemg.masi.util.MagicUtil;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class PurificationMagic extends Magic{
    public PurificationMagic(Settings settings) {
        super(settings);
    }
    @Override
    public int singFinishTick(){
        return 20;
    }

    @Override
    public int energyConsume(){
        return 20;
    }
    @Override
    public int studyNeed(){
        return 5;
    }

    @Override
    public void release(ItemStack stack, World world, LivingEntity user, float singingTicks){
        List<Entity> list = world.getOtherEntities(null,new Box(user.getX()-3,user.getY(),user.getZ()-3,user.getX()+3,user.getY()+3,user.getZ()+3));
        for(Entity entity : list){
            if(entity instanceof LivingEntity livingEntity) {
                if (livingEntity.getGroup() == EntityGroup.UNDEAD) {
                    livingEntity.damage(livingEntity.getWorld().getDamageSources().playerAttack((PlayerEntity) user), 10);
                    livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 10, 2, false, false, false));
                    livingEntity.setVelocity(0, 1, 0);
                } else {
                    if (livingEntity.getStatusEffects() != null && !livingEntity.getStatusEffects().isEmpty()) {
                        for (StatusEffectInstance effects : livingEntity.getStatusEffects().stream().toList()) {
                            if (MagicUtil.harmful.contains(effects.getEffectType())) {
                                livingEntity.removeStatusEffect(effects.getEffectType());
                            }
                        }
                    }
                    livingEntity.setFrozenTicks(0);
                }
                if (MagicUtil.EFFECT.get(world)!=null) {
                    //目标受到的所有魔法效果和它的施加者
                    ConcurrentHashMap<LivingEntity, ConcurrentHashMap<Magic, Integer>> map2 = MagicUtil.EFFECT.get(world).get(livingEntity);
                    if(map2 !=null){
                        //魔法效果的施加者
                        for (LivingEntity livingEntity1 : map2.keySet()) {
                            //如果该效果不是被净化者自己施加的,且不是净化者施加的
                            if (!livingEntity1.equals(livingEntity) && !livingEntity1.equals(user)) {
                                //移除该施加者的施加的全部效果
                                MagicUtil.EFFECT.get(world).get(livingEntity).remove(livingEntity1);
                            }
                        }
                    }
                }
            }
        }
        super.release(stack,world,user,singingTicks);
    }
    @Override
    public void onSinging(ItemStack stack, World world, LivingEntity user, float singingTicks){
        if(!user.getWorld().isClient()){
            MagicUtil.circleGround(18,user,user.getX(),user.getY(),user.getZ());
            if(user.getItemUseTime() >= singFinishTick()){
                double yawRadians = Math.toRadians(user.getYaw()+90);
                double x = user.getX() + Math.cos(yawRadians) * 1;
                double z = user.getZ() + Math.sin(yawRadians) * 1;
                double y = user.getY()+2;
                MagicUtil.circleForward(17,user,x,y,z);
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.masi.purification_magic.tooltip"));
        super.appendTooltip(stack,world,tooltip,context);
    }
}
