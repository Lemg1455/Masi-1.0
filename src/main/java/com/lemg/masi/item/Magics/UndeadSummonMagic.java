package com.lemg.masi.item.Magics;

import com.lemg.masi.Masi;
import com.lemg.masi.entity.ModEntities;
import com.lemg.masi.entity.entities.minions.*;
import com.lemg.masi.util.MagicUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.*;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ArmorItem;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class UndeadSummonMagic extends Magic{

    public UndeadSummonMagic(Settings settings,int singFinishTick,int energyConsume,int studyNeed) {
        super(settings,singFinishTick,energyConsume,studyNeed);
    }

    @Override
    public void release(ItemStack stack, World world, LivingEntity user, float singingTicks) {
        if (!world.isClient()) {
            for (int j = 0; j < 6; j++) {
                BlockPos pos = user.getBlockPos().add(j,1,0);
                randomSpawn(world,pos,user);
            }
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


    public void randomSpawn(World world,BlockPos pos,LivingEntity user){
        Random random = new Random();
        int i = random.nextInt(6);
        switch (i) {
            case 0: {
                MasiSkeletonEntity skeletonEntity = new MasiSkeletonEntity(ModEntities.MASI_SKELETON, world);
                skeletonEntity.setOwner(user);
                setRandomEquip(skeletonEntity);
                skeletonEntity.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.BOW));
                skeletonEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 2400, 1, false, false, true));

                SkeletonHorseEntity skeletonHorseEntity = EntityType.SKELETON_HORSE.spawn((ServerWorld) world,pos, SpawnReason.SPAWNER);

                if(skeletonHorseEntity!=null){
                    skeletonHorseEntity.tryEquip(new ItemStack(Items.IRON_HORSE_ARMOR));
                    skeletonHorseEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 1200, 1, false, true, true));
                    skeletonEntity.startRiding(skeletonHorseEntity);
                    ((ServerWorld) user.getWorld()).spawnEntityAndPassengers(skeletonHorseEntity);
                }

                break;
            }
            case 1: {
                MasiZombieEntity zombieEntity = new MasiZombieEntity(ModEntities.MASI_ZOMBIE, world);
                zombieEntity.setOwner(user);
                setRandomEquip(zombieEntity);
                zombieEntity.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.IRON_SWORD));
                zombieEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 2400, 1, false, false, true));

                ZombieHorseEntity zombieHorseEntity = EntityType.ZOMBIE_HORSE.spawn((ServerWorld) world, pos, SpawnReason.SPAWNER);
                if (zombieHorseEntity != null) {
                    zombieEntity.startRiding(zombieHorseEntity);
                    zombieHorseEntity.tryEquip(Items.IRON_HORSE_ARMOR.getDefaultStack());
                    zombieHorseEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 1200, 1, false, true, true));
                    ((ServerWorld) user.getWorld()).spawnEntityAndPassengers(zombieHorseEntity);

                }
                break;
            }
            case 2: {
                MasiZombifiedPiglinEntity zombifiedPiglinEntity = new MasiZombifiedPiglinEntity(ModEntities.MASI_ZOMBIE_PIGLIN, world);
                zombifiedPiglinEntity.setPosition(pos.toCenterPos());
                zombifiedPiglinEntity.setOwner(user);
                setRandomEquip(zombifiedPiglinEntity);
                zombifiedPiglinEntity.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.GOLDEN_SWORD));
                zombifiedPiglinEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 2400, 1, false, false, true));
                ((ServerWorld) user.getWorld()).spawnEntityAndPassengers(zombifiedPiglinEntity);
                break;
            }
            case 3: {
                MasiWitherSkeletonEntity witherSkeletonEntity = new MasiWitherSkeletonEntity(ModEntities.MASI_WITHER_SKELETON, world);
                witherSkeletonEntity.setPosition(pos.toCenterPos());
                witherSkeletonEntity.setOwner(user);
                setRandomEquip(witherSkeletonEntity);
                witherSkeletonEntity.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.STONE_SWORD));
                witherSkeletonEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 2400, 1, false, false, true));
                ((ServerWorld) user.getWorld()).spawnEntityAndPassengers(witherSkeletonEntity);
                break;
            }
            case 4: {
                MasiZombieVillagerEntity zombieVillagerEntity = new MasiZombieVillagerEntity(ModEntities.MASI_ZOMBIE_VILLAGER, world);
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
                zombieVillagerEntity.tryEquip(Items.LEATHER_HELMET.getDefaultStack());
                zombieVillagerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 2400, 0, false, false, true));
                zombieVillagerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 2400, 2, false, false, true));
                ((ServerWorld) user.getWorld()).spawnEntity(zombieVillagerEntity);
                break;
            }
            case 5: {
                MasiDrownedEntity drownedEntity = new MasiDrownedEntity(ModEntities.MASI_DROWNED, world);
                drownedEntity.setPosition(pos.toCenterPos());
                drownedEntity.setOwner(user);
                setRandomEquip(drownedEntity);
                drownedEntity.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.TRIDENT));
                drownedEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 2400, 1, false, false, true));
                ((ServerWorld) user.getWorld()).spawnEntity(drownedEntity);
                break;
            }
        }
        for(int c=0;c<3;c++){
            ((ServerWorld)world).spawnParticles(ParticleTypes.SOUL, pos.getX(),pos.getY()+1,pos.getZ(), 5, 0, 0.0, 0, 0.0);
            ((ServerWorld)world).spawnParticles(ParticleTypes.SOUL, pos.getX()+c/2.0f,pos.getY()+1,pos.getZ(), 5, 0, 0.0, 0, 0.0);
            ((ServerWorld)world).spawnParticles(ParticleTypes.SOUL, pos.getX()-c/2.0f,pos.getY()+1,pos.getZ(), 5, 0, 0.0, 0, 0.0);
            ((ServerWorld)world).spawnParticles(ParticleTypes.SOUL, pos.getX(),pos.getY()+1,pos.getZ()+c/2.0f, 5, 0, 0.0, 0, 0.0);
            ((ServerWorld)world).spawnParticles(ParticleTypes.SOUL, pos.getX(),pos.getY()+1,pos.getZ()-c/2.0f, 5, 0, 0.0, 0, 0.0);
        }
    }

    public void setRandomEquip(MobEntity mob){
        List<Item> head = List.of(Items.LEATHER_HELMET,Items.CHAINMAIL_HELMET,Items.IRON_HELMET,Items.GOLDEN_HELMET,Items.DIAMOND_HELMET,Items.NETHERITE_HELMET);
        List<Item> chest = List.of(Items.LEATHER_CHESTPLATE,Items.CHAINMAIL_CHESTPLATE,Items.IRON_CHESTPLATE,Items.GOLDEN_CHESTPLATE,Items.DIAMOND_CHESTPLATE,Items.NETHERITE_CHESTPLATE);
        List<Item> legs = List.of(Items.LEATHER_LEGGINGS,Items.CHAINMAIL_LEGGINGS,Items.IRON_LEGGINGS,Items.GOLDEN_LEGGINGS,Items.DIAMOND_LEGGINGS,Items.NETHERITE_LEGGINGS);
        List<Item> feet = List.of(Items.LEATHER_BOOTS,Items.CHAINMAIL_BOOTS,Items.IRON_BOOTS,Items.GOLDEN_BOOTS,Items.DIAMOND_BOOTS,Items.NETHERITE_BOOTS);

        mob.tryEquip(head.get(new Random().nextInt(head.size())).getDefaultStack());
        mob.tryEquip(chest.get(new Random().nextInt(chest.size())).getDefaultStack());
        mob.tryEquip(legs.get(new Random().nextInt(legs.size())).getDefaultStack());
        mob.tryEquip(feet.get(new Random().nextInt(feet.size())).getDefaultStack());
    }

    public static void tryRemoveMinion(Minion minion,World world){
        if(!world.isClient()){
            if(minion instanceof MobEntity mob){
                ((ServerWorld)world).spawnParticles(new DustParticleEffect(Vec3d.unpackRgb(0x000000).toVector3f(), 1.0f), mob.getX(), mob.getBodyY(1.0)+0.5, mob.getZ(), 0, 0, 0.0, 0, 0.0);
                if(mob.getStatusEffect(StatusEffects.RESISTANCE)==null){
                    mob.remove(Entity.RemovalReason.DISCARDED);
                    for(int c=0;c<3;c++){
                        ((ServerWorld)world).spawnParticles(ParticleTypes.SOUL, mob.getX(),mob.getY()+1,mob.getZ(), 5, 0, 0.0, 0, 0.0);
                        ((ServerWorld)world).spawnParticles(ParticleTypes.SOUL, mob.getX()+c,mob.getY()+1,mob.getZ(), 5, 0, 0.0, 0, 0.0);
                        ((ServerWorld)world).spawnParticles(ParticleTypes.SOUL, mob.getX()-c,mob.getY()+1,mob.getZ(), 5, 0, 0.0, 0, 0.0);
                        ((ServerWorld)world).spawnParticles(ParticleTypes.SOUL, mob.getX(),mob.getY()+1,mob.getZ()+c, 5, 0, 0.0, 0, 0.0);
                        ((ServerWorld)world).spawnParticles(ParticleTypes.SOUL, mob.getX(),mob.getY()+1,mob.getZ()-c, 5, 0, 0.0, 0, 0.0);
                    }
                }
            }
        }
    }
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.masi.undead_summon_magic.tooltip"));
        super.appendTooltip(stack,world,tooltip,context);
    }
}
