package com.lemg.masi.item.Magics;

import com.lemg.masi.entity.MagicBulletEntity;
import com.lemg.masi.item.ModItems;
import com.lemg.masi.network.ModMessage;
import com.lemg.masi.util.MagicUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
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
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class LightningMagic extends Magic{

    public LightningMagic(Settings settings) {
        super(settings);
    }
    @Override
    public int singFinishTick(){
        return 15;
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
    public boolean Multiple(){
        return true;
    }
    @Override
    public int releaseContinueTime(){
        return 0;
    }
    @Override
    public void release(ItemStack stack, World world, LivingEntity user, float singingTicks){
        MagicBulletEntity magicBullet = new MagicBulletEntity(user.getWorld(), user);
        magicBullet.setItem(new ItemStack(ModItems.LIGHTNING_BULLET));
        magicBullet.setVelocity(user, user.getPitch(), user.getYaw(), 0.0f, 1.0f, 1.0f);
        magicBullet.setNoGravity(true);
        magicBullet.magic = this;
        user.getWorld().spawnEntity(magicBullet);
        super.release(stack,world,user,singingTicks);

    }
    @Override
    public void onSinging(ItemStack stack, World world, LivingEntity user, float singingTicks){
        if(!user.getWorld().isClient()){
            MagicUtil.circleGround(24,user);
            if(user.getItemUseTime() >= singFinishTick()){
                MagicUtil.circleForward(25,user);
            }
        }
    }
    @Override
    public void BulletEffect(HitResult hitResult, PlayerEntity player,MagicBulletEntity magicBullet){
        BlockPos blockPos = null;
        if(hitResult.getType()== HitResult.Type.ENTITY){
            blockPos = ((EntityHitResult)hitResult).getEntity().getBlockPos();
        }else if(hitResult.getType()== HitResult.Type.BLOCK){
            blockPos = ((BlockHitResult)hitResult).getBlockPos();
        }
        magicBullet.discard();
        if(blockPos==null){
            return;
        }

        LightningEntity lightningEntity;
        if ((lightningEntity = EntityType.LIGHTNING_BOLT.create(player.getWorld())) != null) {
            lightningEntity.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(blockPos));
            lightningEntity.setChanneler(player instanceof ServerPlayerEntity ? (ServerPlayerEntity)player : null);
            player.getWorld().spawnEntity(lightningEntity);
        }
    }


    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.masi.lightning_magic.tooltip"));
        super.appendTooltip(stack,world,tooltip,context);
    }
}
