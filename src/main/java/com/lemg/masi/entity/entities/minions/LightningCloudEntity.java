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
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class LightningCloudEntity extends PathAwareEntity implements Minion{
    public LightningCloudEntity(EntityType<? extends LightningCloudEntity> entityType, World world) {
        super((EntityType<? extends PathAwareEntity>) entityType, world);
    }

    public void setSitting(boolean bl) {}
    public boolean isSitting() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if(this.owner==null && this.ownerUuid!=null){
            this.setOwnerByUuid(this.ownerUuid);
        }
        LivingEntity target = this.getTarget();
        if(target!=null && target.isAlive()){
            double x = target.getX()-this.getX();
            double y = target.getY()+10-this.getY();
            double z = target.getZ()-this.getZ();
            this.setVelocity(x/15,y/15,z/15);
            if(this.age%60==0){
                if(!this.getWorld().isClient()){
                    System.out.println(this.squaredDistanceTo(target));
                    if(this.squaredDistanceTo(target)<=120){
                        LightningEntity lightningEntity;
                        if ((lightningEntity = EntityType.LIGHTNING_BOLT.create(this.getWorld())) != null) {
                            lightningEntity.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(target.getBlockPos()));
                            lightningEntity.setChanneler(target instanceof ServerPlayerEntity ? (ServerPlayerEntity)target : null);
                            this.getWorld().spawnEntity(lightningEntity);
                        }
                    }
                }
            }else {
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 60, 1,false,false,false));
            }
        }else {
            List<LivingEntity> list = this.getWorld().getNonSpectatingEntities(LivingEntity.class, this.getBoundingBox().expand(15, 15, 15));
            LivingEntity newTarget = null;
            for(LivingEntity livingEntity : list){
                if(livingEntity.isAlive() && livingEntity.canHit() && !MagicUtil.teamEntity(livingEntity,this.owner) && !(livingEntity instanceof LightningCloudEntity)){
                    newTarget = livingEntity;
                }
            }
            this.setVelocity(0.2,0.1,0.2);
            if(newTarget!=null){
                this.setTarget(newTarget);
            }else if(this.owner!=null){
                this.setVelocity(owner.getX()-this.getX(),owner.getY()+10-this.getY(),owner.getZ()-this.getZ());
            }
        }

        if(this.age>=2400){
            this.discard();
        }
    }

    @Override
    protected boolean shouldDropLoot() {
        return true;
    }

    @Override
    public boolean canHit() {
        return false;
    }

    @Override
    protected void initGoals() {
        this.targetSelector.add(1, new TrackMinionOwnerAttackerGoal(this));//保护主人
        this.targetSelector.add(2, new AttackWithMinionOwnerGoal(this));//攻击主人的目标
        this.targetSelector.add(3, new ActiveTargetGoal<LivingEntity>(this, LivingEntity.class, 5, false, false, entity -> !MagicUtil.teamEntity(entity,this.getOwner()) && !(entity instanceof LightningCloudEntity)));
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

    private Vec3d applyBodyRotations(Vec3d shootVector) {
        Vec3d vec3d = shootVector.rotateX(this.prevPitch * ((float)Math.PI / 180));
        vec3d = vec3d.rotateY(-this.prevBodyYaw * ((float)Math.PI / 180));
        return vec3d;
    }

    public static DefaultAttributeContainer.Builder createLightningCloudAttributes(){
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH,200)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED,0.30F)
                .add(EntityAttributes.GENERIC_ARMOR,0.5F)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE,10)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE,40)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE,1.0);
    }

}