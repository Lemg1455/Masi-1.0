package com.lemg.masi.item.Magics;

import com.lemg.masi.Masi;
import com.lemg.masi.entity.ModEntities;
import com.lemg.masi.entity.entities.minions.*;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SkeletonHorseEntity;
import net.minecraft.entity.mob.ZombieHorseEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class SwordManSummonMagic extends Magic{

    public SwordManSummonMagic(Settings settings, int singFinishTick, int energyConsume, int studyNeed) {
        super(settings,singFinishTick,energyConsume,studyNeed);
    }

    @Override
    public void release(ItemStack stack, World world, LivingEntity user, float singingTicks) {
        if(!world.isClient()){
            SwordManEntity swordManEntity = new SwordManEntity(ModEntities.SWORD_MAN,world);
            swordManEntity.setPosition(user.getPos());
            swordManEntity.setOwner(user);
            world.spawnEntity(swordManEntity);
        }
        user.dropStack(new ItemStack(Items.NAME_TAG));
    }
    @Override
    public void onSinging(ItemStack stack, World world, LivingEntity user, float singingTicks) {
        if (!user.getWorld().isClient()) {
            ((ServerWorld) user.getWorld()).spawnParticles(Masi.CIRCLE_GROUND_YELLOW, user.getX(), user.getY(), user.getZ(), 0, 0, 0.0, 0, 0.0);

            if (user.getItemUseTime() >= singFinishTick()) {
                double yawRadians = Math.toRadians(user.getYaw() + 90);
                double x = user.getX() + Math.cos(yawRadians) * 1;
                double z = user.getZ() + Math.sin(yawRadians) * 1;
                double y = user.getY() + 2;
                ((ServerWorld) user.getWorld()).spawnParticles(Masi.CIRCLE_FORWARD_YELLOW, x, y, z, 0, 0, 0.0, 0, 0.0);
            }
        }
    }
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.masi.sword_man_summon_magic.tooltip"));
        tooltip.add(Text.translatable("item.masi.sword_man_summon_magic.tooltip2"));
        tooltip.add(Text.translatable("item.masi.sword_man_summon_magic.tooltip3"));
        super.appendTooltip(stack,world,tooltip,context);
    }
}
