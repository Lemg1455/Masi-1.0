package com.lemg.masi.item.Magics;

import com.lemg.masi.Masi;
import com.lemg.masi.entity.entities.minions.*;
import com.lemg.masi.util.MagicUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.VillagerEntity;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class UndeadSummonMagic extends Magic{

    public UndeadSummonMagic(Settings settings,int singFinishTick,int energyConsume,int studyNeed) {
        super(settings,singFinishTick,energyConsume,studyNeed);
    }
    public static ConcurrentHashMap<LivingEntity, List<LivingEntity>> teams = new ConcurrentHashMap<>();

    @Override
    public void release(ItemStack stack, World world, LivingEntity user, float singingTicks) {
        if (!world.isClient()) {
            int n = 6;
            if (teams.get(user) == null) {
                List<LivingEntity> list = new ArrayList<>();
                teams.put(user,list);
            }else {
                List<LivingEntity> list = teams.get(user);
                for(LivingEntity livingEntity : list){
                    if(livingEntity instanceof Minion){
                        n--;
                    }
                }
            }
            if(n>0){
                for (int j = 0; j < n; j++) {
                    BlockPos pos = user.getBlockPos().add(j,1,j);
                    randomSpawn(world,pos,user);
                }
            }
            if (teams.get(user) != null) {
                MagicUtil.putEffect(world,user, user, this, 1200);
            }
            /*int k = random.nextInt(3);
            if(k==0){
                WitherEntity witherEntity = EntityType.WITHER.spawn((ServerWorld) world,user.getBlockPos().add(i,2,i), SpawnReason.SPAWNER);
                if (witherEntity != null) {
                    witherEntity.setHealth(50);
                    ((ServerWorld)user.getWorld()).spawnEntityAndPassengers(witherEntity);
                    list.add(witherEntity);
                    teams.put(user,list);
                }
            }*/
        }
    }
    @Override
    public void onSinging(ItemStack stack, World world, LivingEntity user, float singingTicks){
        if(!user.getWorld().isClient()){
            ((ServerWorld)user.getWorld()).spawnParticles(Masi.LARGE_CIRCLE_GROUND_BLACK, user.getX(),user.getY(),user.getZ(), 0, 0, 0.0, 0, 0.0);

            if(user.getItemUseTime() >= singFinishTick()){
                double yawRadians = Math.toRadians(user.getYaw()+90);
                double x = user.getX() + Math.cos(yawRadians) * 2;
                double z = user.getZ() + Math.sin(yawRadians) * 2;
                double y = user.getY()+4;
                ((ServerWorld)user.getWorld()).spawnParticles(Masi.LARGE_CIRCLE_FORWARD_BLACK, x,y,z, 0, 0, 0.0, 0, 0.0);
            }
        }
    }

    @Override
    public void magicEffect(ItemStack staffStack, World world, LivingEntity user, Object aim,float ticks){
        if(!world.isClient()){
            if(teams.get(user)==null){
                return;
            }
            if(!user.isAlive()){
                teams.remove(user);
            }else {
                List<LivingEntity> list = new ArrayList<>(teams.get(user));
                for(LivingEntity livingEntity : list){
                    if(livingEntity instanceof Minion minion&& livingEntity.isAlive()){
                        if(((MobEntity)livingEntity).getTarget() instanceof Minion target && minion.getOwner()==target.getOwner()){
                            ((MobEntity)livingEntity).setTarget(null);
                        }
                        ((ServerWorld)livingEntity.getWorld()).spawnParticles(new DustParticleEffect(Vec3d.unpackRgb(0x000000).toVector3f(), 1.0f), livingEntity.getX(), livingEntity.getBodyY(1.0)+1, livingEntity.getZ(), 0, 0, 0.0, 0, 0.0);
                    }else if(livingEntity instanceof AbstractHorseEntity abstractHorse && livingEntity.isAlive()){
                        if(!abstractHorse.hasPassengers()){
                            livingEntity.kill();
                            teams.get(user).remove(livingEntity);
                        }
                    }else if(!livingEntity.isAlive()){
                        if(livingEntity instanceof Minion){
                            randomSpawn(world,livingEntity.getBlockPos(),user);
                            for(int i=0;i<3;i++){
                                ((ServerWorld)livingEntity.getWorld()).spawnParticles(ParticleTypes.SOUL, livingEntity.getX(),livingEntity.getY()+1,livingEntity.getZ(), 5, 0, 0.0, 0, 0.0);
                                ((ServerWorld)livingEntity.getWorld()).spawnParticles(ParticleTypes.SOUL, livingEntity.getX()+i/2.0f,livingEntity.getY()+1,livingEntity.getZ(), 5, 0, 0.0, 0, 0.0);
                                ((ServerWorld)livingEntity.getWorld()).spawnParticles(ParticleTypes.SOUL, livingEntity.getX()-i/2.0f,livingEntity.getY()+1,livingEntity.getZ(), 5, 0, 0.0, 0, 0.0);
                                ((ServerWorld)livingEntity.getWorld()).spawnParticles(ParticleTypes.SOUL, livingEntity.getX(),livingEntity.getY()+1,livingEntity.getZ()+i/2.0f, 5, 0, 0.0, 0, 0.0);
                                ((ServerWorld)livingEntity.getWorld()).spawnParticles(ParticleTypes.SOUL, livingEntity.getX(),livingEntity.getY()+1,livingEntity.getZ()-i/2.0f, 5, 0, 0.0, 0, 0.0);

                            }
                        }
                        teams.get(user).remove(livingEntity);
                    }
                }
            }
            if(ticks<=2){
                teams.remove(user);
            }
        }
    }

    public void randomSpawn(World world,BlockPos pos,LivingEntity user){
        Random random = new Random();
        int i = random.nextInt(6);
        switch (i) {
            case 0: {
                MasiSkeletonEntity skeletonEntity = new MasiSkeletonEntity(EntityType.SKELETON, world);
                skeletonEntity.setOwner(user);
                skeletonEntity.tryEquip(new ItemStack(Items.IRON_CHESTPLATE));
                skeletonEntity.tryEquip(new ItemStack(Items.IRON_HELMET));
                skeletonEntity.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.BOW));
                skeletonEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 1200, 1, false, true, true));

                SkeletonHorseEntity skeletonHorseEntity = EntityType.SKELETON_HORSE.spawn((ServerWorld) world,pos, SpawnReason.SPAWNER);

                if(skeletonHorseEntity!=null){
                    skeletonHorseEntity.tryEquip(new ItemStack(Items.IRON_HORSE_ARMOR));
                    skeletonHorseEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 1200, 1, false, true, true));
                    skeletonEntity.startRiding(skeletonHorseEntity);
                    ((ServerWorld) user.getWorld()).spawnEntityAndPassengers(skeletonHorseEntity);
                }

                teams.get(user).add(skeletonEntity);
                teams.get(user).add(skeletonHorseEntity);

                break;
            }
            case 1: {
                MasiZombieEntity zombieEntity = new MasiZombieEntity(EntityType.ZOMBIE, world);
                zombieEntity.setOwner(user);
                zombieEntity.tryEquip(new ItemStack(Items.CHAINMAIL_CHESTPLATE));
                zombieEntity.tryEquip(new ItemStack(Items.CHAINMAIL_HELMET));
                zombieEntity.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.IRON_SWORD));
                zombieEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 1200, 1, false, true, true));

                ZombieHorseEntity zombieHorseEntity = EntityType.ZOMBIE_HORSE.spawn((ServerWorld) world, pos, SpawnReason.SPAWNER);
                if (zombieHorseEntity != null) {
                    zombieEntity.startRiding(zombieHorseEntity);
                    zombieHorseEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 1200, 1, false, true, true));
                    ((ServerWorld) user.getWorld()).spawnEntityAndPassengers(zombieHorseEntity);
                    teams.get(user).add(zombieEntity);
                    teams.get(user).add(zombieHorseEntity);
                }
                break;
            }
            case 2: {
                MasiZombifiedPiglinEntity zombifiedPiglinEntity = new MasiZombifiedPiglinEntity(EntityType.ZOMBIFIED_PIGLIN, world);
                zombifiedPiglinEntity.setPosition(pos.toCenterPos());
                zombifiedPiglinEntity.setOwner(user);
                zombifiedPiglinEntity.tryEquip(new ItemStack(Items.GOLDEN_CHESTPLATE));
                zombifiedPiglinEntity.tryEquip(new ItemStack(Items.GOLDEN_HELMET));
                zombifiedPiglinEntity.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.GOLDEN_SWORD));
                zombifiedPiglinEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 1200, 1, false, true, true));
                ((ServerWorld) user.getWorld()).spawnEntityAndPassengers(zombifiedPiglinEntity);
                teams.get(user).add(zombifiedPiglinEntity);
                break;
            }
            case 3: {
                MasiWitherSkeletonEntity witherSkeletonEntity = new MasiWitherSkeletonEntity(EntityType.WITHER_SKELETON, world);
                witherSkeletonEntity.setPosition(pos.toCenterPos());
                witherSkeletonEntity.setOwner(user);
                ItemStack itemStack;
                itemStack = new ItemStack(Items.DIAMOND_CHESTPLATE);
                itemStack.setDamage(450);
                witherSkeletonEntity.tryEquip(itemStack);
                itemStack = new ItemStack(Items.DIAMOND_HELMET);
                itemStack.setDamage(400);
                witherSkeletonEntity.tryEquip(itemStack);
                witherSkeletonEntity.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.STONE_SWORD));
                witherSkeletonEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 1200, 1, false, true, true));
                ((ServerWorld) user.getWorld()).spawnEntityAndPassengers(witherSkeletonEntity);
                teams.get(user).add(witherSkeletonEntity);
                break;
            }
            case 4: {
                MasiZombieVillagerEntity zombieVillagerEntity = new MasiZombieVillagerEntity(EntityType.ZOMBIE_VILLAGER, world);
                zombieVillagerEntity.setPosition(pos.toCenterPos());
                zombieVillagerEntity.setOwner(user);
                int h = random.nextInt(4);
                ItemStack itemStack;
                if (h == 0) {
                    itemStack = new ItemStack(Items.IRON_AXE);
                } else if (h == 1) {
                    itemStack = new ItemStack(Items.IRON_PICKAXE);
                } else if (h == 2) {
                    itemStack = new ItemStack(Items.IRON_HOE);
                } else {
                    itemStack = new ItemStack(Items.IRON_SHOVEL);
                }
                zombieVillagerEntity.setStackInHand(Hand.MAIN_HAND, itemStack);
                ((ServerWorld) user.getWorld()).spawnEntity(zombieVillagerEntity);
                teams.get(user).add(zombieVillagerEntity);
                break;
            }
            case 5: {
                MasiDrownedEntity drownedEntity = new MasiDrownedEntity(EntityType.DROWNED, world);
                drownedEntity.setPosition(pos.toCenterPos());
                drownedEntity.setOwner(user);
                drownedEntity.tryEquip(new ItemStack(Items.LEATHER_CHESTPLATE));
                drownedEntity.tryEquip(new ItemStack(Items.LEATHER_HELMET));
                drownedEntity.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.TRIDENT));
                ((ServerWorld) user.getWorld()).spawnEntity(drownedEntity);
                teams.get(user).add(drownedEntity);
                break;
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.masi.undead_summon_magic.tooltip"));
        super.appendTooltip(stack,world,tooltip,context);
    }
}
