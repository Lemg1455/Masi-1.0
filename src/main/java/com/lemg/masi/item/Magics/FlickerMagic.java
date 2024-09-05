package com.lemg.masi.item.Magics;

import com.lemg.masi.Masi;
import com.lemg.masi.network.ModMessage;
import com.lemg.masi.util.MagicUtil;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

public class FlickerMagic extends Magic{
    public FlickerMagic(Settings settings) {
        super(settings);
    }
    @Override
    public int singFinishTick(){
        return 10;
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
        float yaw = user.getYaw();
        float pitch = user.getPitch();
        float ff = -MathHelper.sin(yaw * ((float)Math.PI / 180)) * MathHelper.cos(pitch * ((float)Math.PI / 180));
        float gg = -MathHelper.sin(pitch * ((float)Math.PI / 180));
        float hh = MathHelper.cos(yaw * ((float)Math.PI / 180)) * MathHelper.cos(pitch * ((float)Math.PI / 180));

        EnumSet<PositionFlag> set = EnumSet.noneOf(PositionFlag.class);
        set.add(PositionFlag.X);
        set.add(PositionFlag.Y);
        set.add(PositionFlag.Z);
        set.add(PositionFlag.X_ROT);
        set.add(PositionFlag.Y_ROT);

        if(!world.isClient()){
            user.teleport((ServerWorld) world, user.getX()+ff*15,user.getY()+gg*15,user.getZ()+hh*15, set, yaw, pitch);
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 40, 0,false,false,true));
        }

        super.release(stack,world,user,singingTicks);
    }
    @Override
    public void onSinging(ItemStack stack, World world, LivingEntity user, float singingTicks){
        if(!user.getWorld().isClient()){
            ((ServerWorld)user.getWorld()).spawnParticles(Masi.CIRCLE_GROUND_WHITE, user.getX(),user.getY(),user.getZ(), 0, 0, 0.0, 0, 0.0);

            if(user.getItemUseTime() >= singFinishTick()){
                float yaw = user.getYaw();
                float pitch = user.getPitch();
                float ff = -MathHelper.sin(yaw * ((float)Math.PI / 180)) * MathHelper.cos(pitch * ((float)Math.PI / 180));
                float gg = -MathHelper.sin(pitch * ((float)Math.PI / 180));
                float hh = MathHelper.cos(yaw * ((float)Math.PI / 180)) * MathHelper.cos(pitch * ((float)Math.PI / 180));

                double x = user.getX()+ff*15;
                double z = user.getZ()+hh*15;
                double y = user.getY()+gg*15;
                ((ServerWorld)user.getWorld()).spawnParticles(Masi.CIRCLE_FORWARD_WHITE, x,y,z, 0, 0, 0.0, 0, 0.0);

            }
        }
    }


    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.masi.flicker_magic.tooltip"));
        super.appendTooltip(stack,world,tooltip,context);
    }
}
