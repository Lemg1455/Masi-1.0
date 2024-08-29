package com.lemg.masi.item.Magics;

import com.lemg.masi.network.ModMessage;
import com.lemg.masi.util.MagicUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class StealMagic extends Magic{
    public StealMagic(Settings settings) {
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
        MinecraftClient client = MinecraftClient.getInstance();
        HitResult hit = client.crosshairTarget;
        if (Objects.requireNonNull(hit.getType()) == HitResult.Type.ENTITY) {
            EntityHitResult entityHit = (EntityHitResult) hit;
            Entity entity = entityHit.getEntity();
            if(entity instanceof LivingEntity livingEntity){
                if(livingEntity.isAlive()){
                    if(world.isClient()){
                        PacketByteBuf buf = PacketByteBufs.create();
                        buf.writeUuid(livingEntity.getUuid());
                        buf.writeUuid(user.getUuid());
                        buf.writeItemStack(this.getDefaultStack());
                        ClientPlayNetworking.send(ModMessage.CROSSHAIR_ENTITY_ID, buf);
                    }
                }
            }
        }
        super.release(stack,world,user,singingTicks);
    }
    @Override
    public void onSinging(ItemStack stack, World world, LivingEntity user, float singingTicks){
        if(!user.getWorld().isClient()){
            MagicUtil.circleGround(4,user,user.getX(),user.getY(),user.getZ());
            if(user.getItemUseTime() >= singFinishTick()){
                double yawRadians = Math.toRadians(user.getYaw()+90);
                double x = user.getX() + Math.cos(yawRadians) * 1;
                double z = user.getZ() + Math.sin(yawRadians) * 1;
                double y = user.getY()+2;
                MagicUtil.circleForward(5,user,x,y,z);
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.masi.steal_magic.tooltip"));
        super.appendTooltip(stack,world,tooltip,context);
    }
}
