package com.lemg.masi.item.Magics;

import com.lemg.masi.Masi;
import com.lemg.masi.entity.MagicBulletEntity;
import com.lemg.masi.item.ModItems;
import com.lemg.masi.network.ModMessage;
import com.lemg.masi.util.MagicUtil;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Blocks;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class ColdDisasterMagic extends Magic{

    public ColdDisasterMagic(Settings settings,int singFinishTick,int energyConsume,int studyNeed) {
        super(settings,singFinishTick,energyConsume,studyNeed);
    }

    @Override
    public void release(ItemStack stack, World world, LivingEntity user, float singingTicks){
        List<Entity> list = world.getOtherEntities(user, user.getBoundingBox().expand(30,20,30));
        if(!list.isEmpty()){
            for(Entity entity : list){
                if(entity instanceof LivingEntity livingEntity){
                    if(!world.isClient()){
                        ((ServerWorld)user.getWorld()).spawnParticles(Masi.CIRCLE_FORWARD_BLUE, livingEntity.getX(),livingEntity.getY()+2,livingEntity.getZ(), 0, 0, 0.0, 0, 0.0);
                    }

                    livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 400, 10,false,false,false));
                    livingEntity.getWorld().setBlockState(livingEntity.getBlockPos(), Blocks.ICE.getDefaultState());
                    livingEntity.getWorld().setBlockState(livingEntity.getBlockPos().add(0,1,0), Blocks.ICE.getDefaultState());
                    livingEntity.setFrozenTicks(1000);
                    livingEntity.damage(livingEntity.getWorld().getDamageSources().freeze(), 10);
                }
            }
        }
        user.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 200, 10,false,false,false));
        user.getWorld().setBlockState(user.getBlockPos(), Blocks.ICE.getDefaultState());
        user.getWorld().setBlockState(user.getBlockPos().add(0,1,0), Blocks.ICE.getDefaultState());

    }
    @Override
    public void onSinging(ItemStack stack, World world, LivingEntity user, float singingTicks){
        if(!user.getWorld().isClient()){
            ((ServerWorld)user.getWorld()).spawnParticles(Masi.LARGE_CIRCLE_GROUND_BLUE, user.getX(),user.getY(),user.getZ(), 0, 0, 0.0, 0, 0.0);

            if(user.getItemUseTime() >= singFinishTick()){
                double yawRadians = Math.toRadians(user.getYaw()+90);
                double x = user.getX() + Math.cos(yawRadians) * 2;
                double z = user.getZ() + Math.sin(yawRadians) * 2;
                double y = user.getY()+4;

                ((ServerWorld)user.getWorld()).spawnParticles(Masi.LARGE_CIRCLE_FORWARD_BLUE, x,y,z, 0, 0, 0.0, 0, 0.0);
            }
        }
        List<Entity> list = world.getOtherEntities(user, user.getBoundingBox().expand(30,20,30));
        if(!list.isEmpty()){
            for(Entity entity : list) {
                if (entity instanceof LivingEntity livingEntity) {
                    livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 10, 2,false,false,false));
                }
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.masi.cold_disaster_magic.tooltip"));
        super.appendTooltip(stack,world,tooltip,context);
    }
}
