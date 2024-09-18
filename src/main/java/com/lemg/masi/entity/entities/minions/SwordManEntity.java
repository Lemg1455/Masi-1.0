package com.lemg.masi.entity.entities.minions;

import com.lemg.masi.entity.ai.*;
import com.lemg.masi.item.Magics.UndeadSummonMagic;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class SwordManEntity extends PathAwareEntity implements Minion{
    public SwordManEntity(EntityType<? extends SwordManEntity> entityType, World world) {
        super((EntityType<? extends PathAwareEntity>) entityType, world);
    }
    //待机动作
    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeOut = 0;
    //攻击动作
    public final AnimationState attackAnimationState = new AnimationState();
    public int attackAnimationTimeOut = 0;

    public void setUpAnimationState(){
        //待机动作计时器
        if (this.idleAnimationTimeOut <= 0){
            this.idleAnimationTimeOut = this.random.nextInt(40) + 80;
            this.idleAnimationState.start(this.age);
        }else {
            --this.idleAnimationTimeOut;
        }
        //攻击动作计时器
        if (this.isAttacking() && this.attackAnimationTimeOut <= 0){
            attackAnimationTimeOut = 40;
            attackAnimationState.start(this.age);
        }else {
            --this.attackAnimationTimeOut;
        }
        if (!this.isAttacking()){
            attackAnimationState.stop();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if(this.getWorld().isClient()){
            setUpAnimationState();
        }
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if(player==this.owner){
            ItemStack itemStack = player.getStackInHand(hand);
            if(itemStack.getItem() instanceof ArmorItem armorItem){
                if(!this.getEquippedStack(armorItem.getSlotType()).isEmpty()){
                    this.dropStack(this.getEquippedStack(armorItem.getSlotType()));
                }
                this.tryEquip(itemStack);
            }
            return ActionResult.success(this.getWorld().isClient);
        }
        return super.interactMob(player, hand);
    }

    @Override
    protected boolean shouldDropLoot() {
        return false;
    }
    @Override
    protected void initGoals() {

        this.goalSelector.add(0,new SwimGoal(this));
        this.goalSelector.add(1, new FollowMinionOwnerGoal(this, 1.0, 10.0f, 2.0f, false));//跟随主人

        this.goalSelector.add(4,new WanderAroundFarGoal(this,1.0D));//徘徊
        this.goalSelector.add(5,new LookAtEntityGoal(this, PlayerEntity.class,5f));
        this.goalSelector.add(6,new LookAroundGoal(this));

        this.goalSelector.add(3,new SwordManAttackGoal(this,1D,true));
        this.targetSelector.add(1, new TrackMinionOwnerAttackerGoal(this));//保护主人
        this.targetSelector.add(2, new AttackWithMinionOwnerGoal(this));//攻击主人的目标
        this.targetSelector.add(3,new MinionRevengeGoal(this));//攻击仇恨目标

    }

    @Nullable
    private UUID ownerUuid;
    private LivingEntity owner;


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
                this.setOwnerByUuid(uUID);
            }
        }
    }


}