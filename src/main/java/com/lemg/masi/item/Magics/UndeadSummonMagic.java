package com.lemg.masi.item.Magics;

import com.lemg.masi.Masi;
import com.lemg.masi.entity.ArcaneMinionEntity;
import com.lemg.masi.entity.ModEntities;
import com.lemg.masi.util.MagicUtil;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.DustParticleEffect;
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

    public UndeadSummonMagic(Settings settings) {
        super(settings);
    }
    @Override
    public int singFinishTick(){
        return 30;
    }

    @Override
    public int energyConsume(){
        return 50;
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
    public static ConcurrentHashMap<LivingEntity, List<LivingEntity>> teams = new ConcurrentHashMap<>();

    @Override
    public void release(ItemStack stack, World world, LivingEntity user, float singingTicks){
        if(!world.isClient()){
            Random random = new Random();
            int i = random.nextInt(5);
            List<LivingEntity> list = new ArrayList<>();
            if(teams.get(user)!=null){
                list = teams.get(user);
            }
            if(i==0){
                for(int j=0;j<3;j++){
                    SkeletonEntity skeletonEntity = EntityType.SKELETON.spawn((ServerWorld) world,user.getBlockPos().add(i+j,2,i+j), SpawnReason.SPAWNER);
                    SkeletonHorseEntity skeletonHorseEntity = EntityType.SKELETON_HORSE.spawn((ServerWorld) world,user.getBlockPos().add(i+j,2,i+j), SpawnReason.SPAWNER);
                    if (skeletonEntity != null && skeletonHorseEntity!=null) {
                        skeletonEntity.startRiding(skeletonHorseEntity);
                        skeletonEntity.tryEquip(new ItemStack(Items.IRON_CHESTPLATE));
                        skeletonEntity.tryEquip(new ItemStack(Items.IRON_HELMET));
                        skeletonEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 1200, 1,false,true,true));
                        ((ServerWorld)user.getWorld()).spawnEntityAndPassengers(skeletonHorseEntity);
                        list.add(skeletonEntity);
                        list.add(skeletonHorseEntity);
                        teams.put(user,list);
                    }
                }
            }else if(i==1){
                for(int j=0;j<3;j++){
                    ZombieEntity zombieEntity = EntityType.ZOMBIE.spawn((ServerWorld) world,user.getBlockPos().add(i+j,2,i+j), SpawnReason.SPAWNER);
                    ZombieHorseEntity zombieHorseEntity = EntityType.ZOMBIE_HORSE.spawn((ServerWorld) world,user.getBlockPos().add(i+j,2,i+j), SpawnReason.SPAWNER);
                    if (zombieEntity != null && zombieHorseEntity!=null) {
                        zombieEntity.startRiding(zombieHorseEntity);
                        zombieEntity.tryEquip(new ItemStack(Items.CHAINMAIL_CHESTPLATE));
                        zombieEntity.tryEquip(new ItemStack(Items.CHAINMAIL_HELMET));
                        zombieEntity.setStackInHand(Hand.MAIN_HAND,new ItemStack(Items.IRON_SWORD));
                        zombieHorseEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 1200, 1,false,true,true));
                        zombieEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 1200, 1,false,true,true));
                        ((ServerWorld)user.getWorld()).spawnEntityAndPassengers(zombieHorseEntity);
                        list.add(zombieEntity);
                        list.add(zombieHorseEntity);
                        teams.put(user,list);
                    }
                }
            }else if(i==2){
                for(int j=0;j<5;j++){
                    ZombifiedPiglinEntity zombifiedPiglinEntity = EntityType.ZOMBIFIED_PIGLIN.spawn((ServerWorld) world,user.getBlockPos().add(i+j,2,i+j), SpawnReason.SPAWNER);
                    if (zombifiedPiglinEntity != null) {
                        zombifiedPiglinEntity.tryEquip(new ItemStack(Items.GOLDEN_CHESTPLATE));
                        zombifiedPiglinEntity.tryEquip(new ItemStack(Items.GOLDEN_HELMET));
                        zombifiedPiglinEntity.setStackInHand(Hand.MAIN_HAND,new ItemStack(Items.GOLDEN_SWORD));
                        zombifiedPiglinEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 1200, 1,false,true,true));
                        ((ServerWorld)user.getWorld()).spawnEntityAndPassengers(zombifiedPiglinEntity);
                        list.add(zombifiedPiglinEntity);
                        teams.put(user,list);
                    }
                }
            }else if(i==3){
                for(int j=0;j<2;j++){
                    WitherSkeletonEntity witherSkeletonEntity = EntityType.WITHER_SKELETON.spawn((ServerWorld) world,user.getBlockPos().add(i+j,2,i+j), SpawnReason.SPAWNER);
                    if (witherSkeletonEntity != null) {
                        ItemStack itemStack;
                        itemStack = new ItemStack(Items.DIAMOND_CHESTPLATE);
                        itemStack.setDamage(450);
                        witherSkeletonEntity.tryEquip(itemStack);
                        itemStack = new ItemStack(Items.DIAMOND_HELMET);
                        itemStack.setDamage(400);
                        witherSkeletonEntity.tryEquip(itemStack);
                        witherSkeletonEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 1200, 1,false,true,true));
                        ((ServerWorld)user.getWorld()).spawnEntityAndPassengers(witherSkeletonEntity);
                        list.add(witherSkeletonEntity);
                        teams.put(user,list);
                    }
                }
            }else {
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
                    for(int j=0;j<3;j++){
                        ZombieVillagerEntity zombieVillagerEntity = EntityType.ZOMBIE_VILLAGER.spawn((ServerWorld) world,user.getBlockPos().add(i+j,2,i+j), SpawnReason.SPAWNER);
                        DrownedEntity drownedEntity = EntityType.DROWNED.spawn((ServerWorld) world,user.getBlockPos().add(i-j,2,i-j), SpawnReason.SPAWNER);
                        if (zombieVillagerEntity != null && drownedEntity!=null) {
                            drownedEntity.tryEquip(new ItemStack(Items.LEATHER_CHESTPLATE));
                            drownedEntity.tryEquip(new ItemStack(Items.LEATHER_HELMET));
                            drownedEntity.setStackInHand(Hand.MAIN_HAND,new ItemStack(Items.TRIDENT));
                            int h = random.nextInt(4);
                            ItemStack itemStack;
                            if(h==0){
                                itemStack = new ItemStack(Items.IRON_AXE);
                            } else if (h==1) {
                                itemStack = new ItemStack(Items.IRON_PICKAXE);
                            }else if (h==2) {
                                itemStack = new ItemStack(Items.IRON_HOE);
                            }else {
                                itemStack = new ItemStack(Items.IRON_SHOVEL);
                            }
                            zombieVillagerEntity.setStackInHand(Hand.MAIN_HAND,itemStack);
                            list.add(zombieVillagerEntity);
                            list.add(drownedEntity);
                            teams.put(user,list);
                        }
                    }
                }

            if(teams.get(user)!=null){
                MagicUtil.putEffect(world,teams.get(user),user,this,3600);
            }
        }
    }
    @Override
    public void onSinging(ItemStack stack, World world, LivingEntity user, float singingTicks){
        if(!user.getWorld().isClient()){
            ((ServerWorld)user.getWorld()).spawnParticles(Masi.CIRCLE_GROUND_BLACK, user.getX(),user.getY(),user.getZ(), 0, 0, 0.0, 0, 0.0);

            if(user.getItemUseTime() >= singFinishTick()){
                double yawRadians = Math.toRadians(user.getYaw()+90);
                double x = user.getX() + Math.cos(yawRadians) * 2;
                double z = user.getZ() + Math.sin(yawRadians) * 2;
                double y = user.getY()+2;
                ((ServerWorld)user.getWorld()).spawnParticles(Masi.CIRCLE_FORWARD_BLACK, x,y,z, 0, 0, 0.0, 0, 0.0);
            }
        }
    }

    @Override
    public void magicEffect(ItemStack staffStack, World world, LivingEntity user, Object aim,float ticks){
        if(!world.isClient()){
            if(!user.isAlive()){
                teams.remove(user);
            }else if(aim instanceof List){
                List<LivingEntity> list = (List<LivingEntity>)aim;
                for(LivingEntity livingEntity : list){
                    if(livingEntity instanceof MobEntity mob && mob.isAlive()){
                        if(user.getAttacking()!=null && user.getAttacking().isAlive()){
                            mob.setTarget(user.getAttacking());
                        }else if(user.getLastAttackedTime()==user.age && user.getLastAttacker()!=null && user.getLastAttacker().isAlive()){
                            mob.setTarget(user.getAttacker());
                        }else if(mob.getLastAttackedTime()==mob.age && mob.getLastAttacker()!=null && mob.getLastAttacker().isAlive() && !list.contains(mob.getTarget())){
                            mob.setTarget(mob.getTarget());
                        }else {
                            mob.setTarget(null);
                        }
                        if(mob.getTarget()==user || list.contains(mob.getTarget())){
                            mob.setTarget(null);
                        }
                        if(mob.age%10==0){
                            if(!mob.getNavigation().isIdle() && !mob.hasVehicle()){
                                if (mob.squaredDistanceTo(user) >= 144.0) {
                                    tryTeleport(mob,user);
                                }else if(mob.getTarget()==null){
                                    mob.getLookControl().lookAt(user, 10.0f, mob.getMaxLookPitchChange());
                                    mob.getNavigation().startMovingTo(user, 1.5);
                                }
                            }
                        }
                        ((ServerWorld)livingEntity.getWorld()).spawnParticles(new DustParticleEffect(Vec3d.unpackRgb(0x000000).toVector3f(), 1.0f), livingEntity.getX(), livingEntity.getBodyY(1.0)+1, livingEntity.getZ(), 0, 0, 0.0, 0, 0.0);
                    }
                }
            }
            if(ticks<=2){
                teams.remove(user);
            }
        }
    }
    private void tryTeleport(MobEntity entity ,Entity aimEntity) {
        Random random = new Random();
        BlockPos blockPos = aimEntity.getBlockPos();
        for (int i = 0; i < 10; ++i) {
            int j = random.nextInt(-3, 3);
            int k = random.nextInt(-1, 1);
            int l = random.nextInt(-3, 3);
            boolean bl = this.tryTeleportTo(entity,aimEntity,blockPos.getX() + j, blockPos.getY() + k, blockPos.getZ() + l);
            if (!bl) continue;
            return;
        }
    }

    private boolean tryTeleportTo(MobEntity entity,Entity aimEntity ,int x, int y, int z) {
        if (Math.abs((double)x - aimEntity.getX()) < 2.0 && Math.abs((double)z - aimEntity.getZ()) < 2.0) {
            return false;
        }
        entity.refreshPositionAndAngles((double)x + 0.5, y, (double)z + 0.5, aimEntity.getYaw(), aimEntity.getPitch());
        entity.getNavigation().stop();
        return true;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.masi.undead_summon_magic.tooltip"));
        super.appendTooltip(stack,world,tooltip,context);
    }
}
