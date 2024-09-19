package com.lemg.masi.entity.entities.minions;

import com.lemg.masi.entity.ai.AttackWithMinionOwnerGoal;
import com.lemg.masi.entity.ai.FollowMinionOwnerGoal;
import com.lemg.masi.entity.ai.MinionRevengeGoal;
import com.lemg.masi.entity.ai.TrackMinionOwnerAttackerGoal;
import com.lemg.masi.item.Magics.UndeadSummonMagic;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class MasiDrownedEntity extends DrownedEntity implements Minion,RangedAttackMob{
    @Nullable
    private UUID ownerUuid;
    private LivingEntity owner;

    public MasiDrownedEntity(EntityType<? extends MasiDrownedEntity> entityType, World world) {
        super((EntityType<? extends DrownedEntity>) entityType, world);
    }
    public void setOwner(LivingEntity owner) {
        this.owner=owner;
        this.ownerUuid=owner.getUuid();
    }

    @Nullable
    public UUID getOwnerUuid() {
        return this.ownerUuid;
    }

    public void setOwnerUuid(@Nullable UUID ownerUuid) {
        this.ownerUuid = ownerUuid;
    }

    public void setOwnerByUuid(UUID uuid){
        this.owner = getOwnerByUuid(uuid);
    }
    @Nullable
    public LivingEntity getOwnerByUuid(UUID uuid) {
        if(!this.getWorld().isClient()){
            ServerWorld serverWorld = (ServerWorld) this.getWorld();
            Entity entity = serverWorld.getEntity(uuid);
            if(entity instanceof LivingEntity livingEntity){
                return livingEntity;
            }
        }
        return null;
    }

    @Override
    public boolean isMinion() {
        return false;
    }

    @Nullable
    public LivingEntity getOwner() {
        UUID uUID = this.getOwnerUuid();
        if (uUID == null) {
            return null;
        }
        return this.getOwnerByUuid(uUID);
    }

    public boolean canAttackWithOwner(LivingEntity target, LivingEntity owner) {
        return true;
    }

    @Override
    public void setSitting(boolean bl) {

    }

    @Override
    public boolean isSitting() {
        return false;
    }


    //读写NBT
    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (this.getOwnerUuid() != null) {
            nbt.putUuid("Owner", this.getOwnerUuid());
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        UUID uUID;
        super.readCustomDataFromNbt(nbt);
        if (nbt.containsUuid("Owner")) {
            uUID = nbt.getUuid("Owner");
            if (uUID != null) {
                this.setOwnerUuid(uUID);
                this.setOwnerByUuid(uUID);

            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        UndeadSummonMagic.tryRemoveMinion(this,this.getWorld());
    }

    @Override
    protected boolean shouldDropLoot() {
        return false;
    }

    @Override
    public void attack(LivingEntity target, float pullProgress) {
        TridentEntity tridentEntity = new TridentEntity(this.getWorld(), (LivingEntity)this, new ItemStack(Items.TRIDENT));
        double d = target.getX() - this.getX();
        double e = target.getBodyY(0.3333333333333333) - tridentEntity.getY();
        double f = target.getZ() - this.getZ();
        double g = Math.sqrt(d * d + f * f);
        tridentEntity.setVelocity(d, e + g * (double)0.2f, f, 1.6f, 14 - this.getWorld().getDifficulty().getId() * 4);
        this.playSound(SoundEvents.ENTITY_DROWNED_SHOOT, 1.0f, 1.0f / (this.getRandom().nextFloat() * 0.4f + 0.8f));
        this.getWorld().spawnEntity(tridentEntity);
    }

    static class TridentAttackGoal
            extends ProjectileAttackGoal {
        private final MasiDrownedEntity drowned;

        public TridentAttackGoal(RangedAttackMob rangedAttackMob, double d, int i, float f) {
            super(rangedAttackMob, d, i, f);
            this.drowned = (MasiDrownedEntity)rangedAttackMob;
        }

        @Override
        public boolean canStart() {
            return super.canStart() && this.drowned.getMainHandStack().isOf(Items.TRIDENT);
        }

        @Override
        public void start() {
            super.start();
            this.drowned.setAttacking(true);
            this.drowned.setCurrentHand(Hand.MAIN_HAND);
        }

        @Override
        public void stop() {
            super.stop();
            this.drowned.clearActiveItem();
            this.drowned.setAttacking(false);
        }
    }

    static class DrownedAttackGoal
            extends ZombieAttackGoal {
        private final MasiDrownedEntity drowned;

        public DrownedAttackGoal(MasiDrownedEntity drowned, double speed, boolean pauseWhenMobIdle) {
            super(drowned, speed, pauseWhenMobIdle);
            this.drowned = drowned;
        }

        @Override
        public boolean canStart() {
            return super.canStart() && this.drowned.canDrownedAttackTarget(this.drowned.getTarget());
        }

        @Override
        public boolean shouldContinue() {
            return super.shouldContinue() && this.drowned.canDrownedAttackTarget(this.drowned.getTarget());
        }
    }

    public boolean canDrownedAttackTarget(@Nullable LivingEntity target) {
        if (target != null) {
            return !this.getWorld().isDay() || target.isTouchingWater();
        }
        return false;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(2, new TridentAttackGoal(this, 1.0, 40, 10.0f));
        this.goalSelector.add(2, new DrownedAttackGoal(this, 1.0, false));

        this.goalSelector.add(1, new FollowMinionOwnerGoal(this, 1.0, 10.0f, 2.0f, false));//跟随主人
        this.targetSelector.add(1, new TrackMinionOwnerAttackerGoal(this));//保护主人
        this.targetSelector.add(2, new AttackWithMinionOwnerGoal(this));//攻击主人的目标
        this.targetSelector.add(3,new MinionRevengeGoal(this));//攻击仇恨目标

    }
}