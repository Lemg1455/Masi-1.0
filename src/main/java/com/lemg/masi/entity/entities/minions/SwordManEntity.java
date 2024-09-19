package com.lemg.masi.entity.entities.minions;

import com.lemg.masi.entity.ai.*;
import com.lemg.masi.util.MagicUtil;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class SwordManEntity extends PathAwareEntity implements Minion{
    public SwordManEntity(EntityType<? extends SwordManEntity> entityType, World world) {
        super((EntityType<? extends PathAwareEntity>) entityType, world);
    }
    private static final TrackedData<Integer> SKILLS =
            DataTracker.registerData(SwordManEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> SITTING =
            DataTracker.registerData(SwordManEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SKILLS,0);
        this.dataTracker.startTracking(SITTING,false);

    }

    public void setSkill(int i) {
        this.dataTracker.set(SKILLS,i);
    }
    public int getSkill() {
        return this.dataTracker.get(SKILLS);
    }
    public void setSitting(boolean bl) {
        this.sitting=bl;
    }
    public boolean isSitting() {
        return this.sitting;
    }
    public boolean isInSittingPose() {
        return (this.dataTracker.get(SITTING));
    }

    public void setInSittingPose(boolean inSittingPose) {
        this.dataTracker.set(SITTING, inSittingPose);
    }
    public boolean sitting = false;

    //待机动作
    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeOut = 0;
    //攻击动作
    public final AnimationState attackAnimation1 = new AnimationState();
    public final AnimationState attackAnimation2 = new AnimationState();
    public final AnimationState attackAnimation3 = new AnimationState();
    public final AnimationState attackAnimation4 = new AnimationState();
    public final AnimationState jump_hit = new AnimationState();
    public int clientAttackTimeOut = 0;
    public int serverAttackTimeOut = 0;
    public int JumpHitTimeOut = 0;

    public void setUpAnimationState(){
        //待机动作计时器
        if (this.idleAnimationTimeOut <= 0){
            this.idleAnimationTimeOut = this.random.nextInt(40) + 80;
            this.idleAnimationState.start(this.age);
        }else {
            --this.idleAnimationTimeOut;
        }
        AnimationState common = new AnimationState();
        //攻击动作计时器
        if (this.isAttacking() && this.clientAttackTimeOut <= 0 && this.getSkill()==0){
            clientAttackTimeOut = 40;
            int i = new Random().nextInt(4);
            switch (i){
                case 0:{
                    common = attackAnimation1;
                    break;
                }
                case 1:{
                    common = attackAnimation2;
                    break;
                }
                case 2:{
                    common = attackAnimation3;
                    break;
                }
                case 3:{
                    common = attackAnimation4;
                    break;
                }
            }
            common.start(this.age);
        }else if(this.clientAttackTimeOut <= 0 && this.getSkill()==1){
            clientAttackTimeOut = 50;
            jump_hit.start(this.age);
        }
        else if(clientAttackTimeOut>0){
            --this.clientAttackTimeOut;
        }
        if (clientAttackTimeOut<=0){
            common.stop();
        }
        if(this.getSkill()==0){
            jump_hit.stop();
        }
    }

    @Override
    public void tick() {
        super.tick();

        if(this.getWorld().isClient()){
            setUpAnimationState();
        }else {
            if (this.isAttacking() && this.serverAttackTimeOut <= 0 && this.getSkill()==0){
                serverAttackTimeOut = 40;
            }
            else if(this.getTarget()!=null && this.getTarget().isAlive()){
                if(JumpHitTimeOut<=0 && this.serverAttackTimeOut<=0){
                    this.setSkill(1);
                    this.JumpHitTimeOut=400;
                    serverAttackTimeOut = 50;
                }
            }

            if(this.serverAttackTimeOut>=0){
                this.useSkills(this.getTarget());
                serverAttackTimeOut--;
            }

            if(this.JumpHitTimeOut>0){
                JumpHitTimeOut--;
            }

        }
    }

    public void useSkills(LivingEntity target){

        int i = this.getSkill();
        switch (i){
            case 1:{
                if(target==null){
                    this.setSkill(0);
                    this.serverAttackTimeOut=0;
                    return;
                }
                if(this.serverAttackTimeOut==40){
                    Vec3d vec3d3 = this.getRotationVec(1.0f);
                    double l = this.getX() - vec3d3.x;
                    double m = this.getBodyY(1)+2;
                    double n = this.getZ() - vec3d3.z;
                    double o = target.getX() - l;
                    double p = target.getBodyY(0.5);
                    double q = target.getZ() - n;
                    //this.setPos(l, m, n);
                    this.setVelocity(o/15,1,q/15);
                }
                else if(this.serverAttackTimeOut==20){
                    this.teleport(target.getX(),target.getY(),target.getZ(),false);
                    List<LivingEntity> list = this.getWorld().getNonSpectatingEntities(LivingEntity.class, this.getBoundingBox().expand(2.5, 2, 2.5));
                    for(LivingEntity livingEntity : list){
                        livingEntity.damage(this.getWorld().getDamageSources().mobAttack(this),10.0f);
                        livingEntity.setVelocity(0,0.8,0);
                    }
                    ((ServerWorld)this.getWorld()).spawnParticles(ParticleTypes.EXPLOSION_EMITTER, this.getX(), this.getY(), this.getZ(), 0, 0, 0.0, 0, 0.0);
                }
                else if(this.serverAttackTimeOut==0){
                    this.setSkill(0);
                }
            }
        }

    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        /*if (this.getWorld().isClient) {
            boolean bl = this.getOwner()==player;
            return bl ? ActionResult.CONSUME : ActionResult.PASS;
        }*/

        if(player==this.owner){
            ItemStack itemStack = player.getStackInHand(hand);
            if(itemStack.getItem() instanceof ArmorItem armorItem){
                if(!this.getEquippedStack(armorItem.getSlotType()).isEmpty()){
                    this.dropStack(this.getEquippedStack(armorItem.getSlotType()));
                }
                this.tryEquip(itemStack);
            }else {
                this.setSitting(!this.isSitting());
                this.navigation.stop();
                this.setTarget(null);
                if(this.isInSittingPose()){
                    this.setPose(EntityPose.SLEEPING);
                }else {
                    this.setPose(EntityPose.STANDING);
                }
                System.out.println(this.getWorld()+"---"+this.isInSittingPose());
            }
            return ActionResult.SUCCESS;
        }
        return super.interactMob(player, hand);
    }

    @Override
    protected boolean shouldDropLoot() {
        return true;
    }
    @Override
    protected void initGoals() {

        this.goalSelector.add(0,new SwimGoal(this));
        this.goalSelector.add(1, new FollowMinionOwnerGoal(this, 1.0, 10.0f, 2.0f, false));//跟随主人
        this.goalSelector.add(2, new SwordManSitGoal(this));

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

    @Override
    public boolean tryAttack(Entity target) {
        List<LivingEntity> list = this.getWorld().getNonSpectatingEntities(LivingEntity.class, target.getBoundingBox().expand(1.0, 0.25, 1.0));
        for (LivingEntity livingEntity : list) {
            if (livingEntity == this || livingEntity == target || this.isTeammate(livingEntity) || livingEntity instanceof ArmorStandEntity && ((ArmorStandEntity)livingEntity).isMarker() || !(this.squaredDistanceTo(livingEntity) < 9.0)) continue;
            if(MagicUtil.teamEntity(livingEntity,this.owner)){continue;}
            livingEntity.takeKnockback(0.4f, MathHelper.sin(this.getYaw() * ((float)Math.PI / 180)), -MathHelper.cos(this.getYaw() * ((float)Math.PI / 180)));
            livingEntity.damage(this.getDamageSources().mobAttack(this), 5);
        }
        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, this.getSoundCategory(), 1.0f, 1.0f);
        this.spawnSweepAttackParticles();
        return super.tryAttack(target);
    }

    public void spawnSweepAttackParticles() {
        double d = -MathHelper.sin(this.getYaw() * ((float)Math.PI / 180));
        double e = MathHelper.cos(this.getYaw() * ((float)Math.PI / 180));
        if (this.getWorld() instanceof ServerWorld) {
            ((ServerWorld)this.getWorld()).spawnParticles(ParticleTypes.SWEEP_ATTACK, this.getX() + d, this.getBodyY(0.5), this.getZ() + e, 0, d, 0.0, e, 0.0);
        }
    }

    public static DefaultAttributeContainer.Builder createSwordManAttributes(){
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH,50)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED,0.30F)
                .add(EntityAttributes.GENERIC_ARMOR,0.5F)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE,10)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE,40)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE,0.8);
    }

}