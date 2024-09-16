package com.lemg.masi.entity.entities.minions;

import com.lemg.masi.entity.ai.AttackWithMinionOwnerGoal;
import com.lemg.masi.entity.ai.FollowMinionOwnerGoal;
import com.lemg.masi.entity.ai.MinionRevengeGoal;
import com.lemg.masi.entity.ai.TrackMinionOwnerAttackerGoal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class MasiZombieVillagerEntity extends ZombieVillagerEntity implements Minion{
    @Nullable
    private UUID ownerUuid;
    private LivingEntity owner;

    public MasiZombieVillagerEntity(EntityType<? extends ZombieVillagerEntity> entityType, World world) {
        super((EntityType<? extends ZombieVillagerEntity>) entityType, world);
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
        return true;
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
            }
        }
    }

    @Override
    public void tick() {
        super.tick();

    }


    @Override
    protected void initGoals() {
        this.goalSelector.add(2, new ZombieAttackGoal(this, 1.0, false));

        this.goalSelector.add(1, new FollowMinionOwnerGoal(this, 1.0, 10.0f, 2.0f, false));//跟随主人
        this.targetSelector.add(1, new TrackMinionOwnerAttackerGoal(this));//保护主人
        this.targetSelector.add(2, new AttackWithMinionOwnerGoal(this));//攻击主人的目标
        this.targetSelector.add(3,new MinionRevengeGoal(this));//攻击仇恨目标

    }
}