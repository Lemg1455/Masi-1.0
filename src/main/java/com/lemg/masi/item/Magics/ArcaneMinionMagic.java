package com.lemg.masi.item.Magics;

import com.lemg.masi.entity.ArcaneMinionEntity;
import com.lemg.masi.entity.MagicBulletEntity;
import com.lemg.masi.entity.ModEntities;
import com.lemg.masi.item.ModItems;
import com.lemg.masi.network.ModMessage;
import com.lemg.masi.util.MagicUtil;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
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

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class ArcaneMinionMagic extends Magic{

    public ArcaneMinionMagic(Settings settings) {
        super(settings);
    }
    @Override
    public int singFinishTick(){
        return 40;
    }

    @Override
    public int energyConsume(){
        return 80;
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

    @Override
    public void release(ItemStack stack, World world, LivingEntity user, float singingTicks){
        ArcaneMinionEntity arcaneMinionEntity = ModEntities.ARCANE_MINION.create(user.getWorld());
        if (arcaneMinionEntity != null) {
            if(user instanceof PlayerEntity player){
                arcaneMinionEntity.setOwner(player);
            }
            arcaneMinionEntity.refreshPositionAndAngles(user.getPos().getX(), user.getPos().getY(), user.getPos().getZ(), 0.0f, 0.0f);
            if(!user.getWorld().isClient()){
                ((ServerWorld)user.getWorld()).spawnEntityAndPassengers(arcaneMinionEntity);
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
    }


    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.masi.arcane_minion_magic.tooltip"));
        super.appendTooltip(stack,world,tooltip,context);
    }
}
