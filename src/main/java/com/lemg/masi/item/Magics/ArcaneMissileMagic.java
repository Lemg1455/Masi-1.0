package com.lemg.masi.item.Magics;

import com.lemg.masi.entity.ArcaneMinionEntity;
import com.lemg.masi.entity.MagicBulletEntity;
import com.lemg.masi.item.ModItems;
import com.lemg.masi.network.ModMessage;
import com.lemg.masi.util.MagicUtil;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ArcaneMissileMagic extends Magic{

    public ArcaneMissileMagic(Settings settings) {
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
        return true;
    }
    @Override
    public int releaseContinueTime(){
        return 0;
    }

    @Override
    public void release(ItemStack stack, World world, LivingEntity user, float singingTicks){
        List<Entity> list = world.getOtherEntities(user, user.getBoundingBox().expand(20,20,20));
        if(!list.isEmpty()){
            for(Entity entity : list){
                if(entity instanceof LivingEntity livingEntity){
                    if(entity instanceof PlayerEntity player && user instanceof ArcaneMinionEntity arcaneMinionEntity && player==arcaneMinionEntity.getOwner()){
                        continue;
                    }
                    if(!world.isClient()){
                        PacketByteBuf buf2 = PacketByteBufs.create();
                        buf2.writeInt(21);
                        buf2.writeDouble(livingEntity.getX());
                        buf2.writeDouble(livingEntity.getY()+2);
                        buf2.writeDouble(livingEntity.getZ());
                        for (ServerPlayerEntity players : PlayerLookup.tracking((ServerWorld) world, livingEntity.getBlockPos())) {
                            ServerPlayNetworking.send((ServerPlayerEntity) players, ModMessage.ADD_PARTICLE_ID, buf2);
                        }
                    }

                    Vec3d vec3d3 = user.getRotationVec(1.0f);
                    double l = user.getX() - vec3d3.x;
                    double m = user.getBodyY(1);
                    double n = user.getZ() - vec3d3.z;

                    double o = entity.getX() - l;
                    double p = entity.getBodyY(0.5) - m;
                    double q = entity.getZ() - n;
                    MagicBulletEntity magicBullet = null;
                    if(user instanceof PlayerEntity player){
                        magicBullet = new MagicBulletEntity(user.getWorld(), player);
                    }else if(user instanceof ArcaneMinionEntity arcaneMinionEntity){
                        if(arcaneMinionEntity.getOwner()!=null){
                            magicBullet = new MagicBulletEntity(user.getWorld(), arcaneMinionEntity.getOwner());
                        }
                    }
                    if(magicBullet!=null){
                        magicBullet.setItem(new ItemStack(ModItems.ARCANE_BULLET));
                        magicBullet.setVelocity(o/15,p/15,q/15);
                        magicBullet.setPos(l, m, n);

                        magicBullet.setNoGravity(true);
                        magicBullet.magic = this;
                        user.getWorld().spawnEntity(magicBullet);
                    }
                }
            }
        }
    }
    @Override
    public void onSinging(ItemStack stack, World world, LivingEntity user, float singingTicks){
        if(!user.getWorld().isClient()){
            MagicUtil.circleGround(22,user,user.getX(),user.getY(),user.getZ());
            if(user.getItemUseTime() >= singFinishTick()){
                double yawRadians = Math.toRadians(user.getYaw()+90);
                double x = user.getX() + Math.cos(yawRadians) * 2;
                double z = user.getZ() + Math.sin(yawRadians) * 2;
                double y = user.getY()+4;
                MagicUtil.circleForward(23,user,x,y,z);
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
    public void BulletEffect(HitResult hitResult, PlayerEntity player,MagicBulletEntity magicBullet){
        List<Entity> hit_list = magicBullet.getWorld().getOtherEntities(magicBullet, magicBullet.getBoundingBox().expand(2,2,2));
        int amount = 10;
        if(MagicUtil.MAX_ENERGY.get(player)!=null){
            amount = MagicUtil.MAX_ENERGY.get(player) / 10;
        }
        for(Entity entity : hit_list){
            if(entity instanceof LivingEntity livingEntity){
                livingEntity.damage(magicBullet.getWorld().getDamageSources().playerAttack(player), amount);
            }
        }
    }


    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.masi.arcane_missile_magic.tooltip"));
        super.appendTooltip(stack,world,tooltip,context);
    }
}
