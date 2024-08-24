package com.lemg.masi.item.Magics;

import com.lemg.masi.network.ModMessage;
import com.lemg.masi.util.MagicUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidFillable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

public class HealMagic extends Magic{
    public HealMagic(Settings settings) {
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
        MinecraftClient client = MinecraftClient.getInstance();
        HitResult hit = client.crosshairTarget;
        if (Objects.requireNonNull(hit.getType()) == HitResult.Type.ENTITY) {
            EntityHitResult entityHit = (EntityHitResult) hit;
            Entity entity = entityHit.getEntity();
            if(entity instanceof LivingEntity livingEntity){
                if(livingEntity.isAlive()){
                    float health = livingEntity.getHealth()+6;
                    if(health >= livingEntity.getMaxHealth()){
                        livingEntity.setHealth(livingEntity.getMaxHealth());
                    }else {
                        livingEntity.setHealth(health);
                    }
                    if(world.isClient()){
                        PacketByteBuf buf = PacketByteBufs.create();
                        buf.writeUuid(livingEntity.getUuid());
                        buf.writeItemStack(this.getDefaultStack());
                        ClientPlayNetworking.send(ModMessage.CROSSHAIR_ENTITY_ID, buf);
                    }
                }
            }
        }else{
            if(user.isAlive()){
                float health = user.getHealth()+6;
                if(health >= user.getMaxHealth()){
                    user.setHealth(user.getMaxHealth());
                }else {
                    user.setHealth(health);
                }
            }
        }

        super.release(stack,world,user,singingTicks);
    }
    @Override
    public void onSinging(ItemStack stack, World world, LivingEntity user, float singingTicks){
        super.onSinging(stack,world,user,singingTicks);
    }


    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.masi.heal_magic.tooltip"));
        super.appendTooltip(stack,world,tooltip,context);
    }
}
