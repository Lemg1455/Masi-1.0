package com.lemg.masi.item.Magics;

import com.lemg.masi.MasiClient;
import com.lemg.masi.item.MagicGroups;
import com.lemg.masi.network.ModMessage;
import com.lemg.masi.util.MagicUtil;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FireBallMagic extends Magic{
    public FireBallMagic(Settings settings) {
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
        return 3;
    }

    @Override
    public void release(ItemStack stack, World world, LivingEntity user, float singingTicks){

        float f = user.getYaw();
        float g = user.getPitch();
        float h = -MathHelper.sin(f * ((float)Math.PI / 180)) * MathHelper.cos(g * ((float)Math.PI / 180));
        float k = -MathHelper.sin(g * ((float)Math.PI / 180));
        float l = MathHelper.cos(f * ((float)Math.PI / 180)) * MathHelper.cos(g * ((float)Math.PI / 180));
        float m = MathHelper.sqrt(h * h + k * k + l * l);
        float n = 3.0f * ((1.0f + (float)1) / 4.0f);

        double yawRadians = Math.toRadians(user.getYaw()+90);
        double forwardX = user.getX() + Math.cos(yawRadians) * 1;
        double forwardZ = user.getZ() + Math.sin(yawRadians) * 1;

        FireballEntity fireballEntity = new FireballEntity(world, user, h *= n / m, k *= n / m, l *= n / m, 1);
        fireballEntity.setPosition(forwardX, user.getY()+2, forwardZ);
        world.spawnEntity(fireballEntity);

        super.release(stack,world,user,singingTicks);
    }
    @Override
    public void onSinging(ItemStack stack, World world, LivingEntity user, float singingTicks){
        if(!user.getWorld().isClient()){
            if(!user.getWorld().isClient()){
                MagicUtil.circleGround(0,user);
                if(user.getItemUseTime() >= singFinishTick()){
                    MagicUtil.circleForward(1,user);
                }
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.masi.fire_ball_magic.tooltip"));
        super.appendTooltip(stack,world,tooltip,context);
    }
}
