package com.lemg.masi.entity.entities;


import com.lemg.masi.item.Magics.Magic;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;


public class MeteoriteEntity extends AnimalEntity {
    private static final TrackedData<Integer> MAGIC =
            DataTracker.registerData(MeteoriteEntity.class, TrackedDataHandlerRegistry.INTEGER);

    @Nullable
    private UUID ownerUuid;
    private PlayerEntity owner;

    Magic magic = null;

    public MeteoriteEntity(EntityType<? extends MeteoriteEntity> entityType, World world) {
        super((EntityType<? extends AnimalEntity>) entityType, world);
        this.noClip=true;
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(MAGIC,-2);
    }
    @Nullable
    public UUID getOwnerUuid() {
        return this.ownerUuid;
    }

    public void setOwnerUuid(@Nullable UUID ownerUuid) {
        this.ownerUuid = ownerUuid;
    }
    public void setOwner(PlayerEntity player) {
        this.owner=player;
        this.ownerUuid=player.getUuid();
    }


    @Override
    public boolean canHit() {
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
        } else {
            String string = nbt.getString("Owner");
            uUID = ServerConfigHandler.getPlayerUuidByName(this.getServer(), string);
        }
        if (uUID != null) {
            this.setOwnerUuid(uUID);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if(this.age<10){
            this.setVelocity(0,-0.1,0);
        }else {
            this.noClip=false;
        }

        this.setFireTicks(20);
        if(!this.getWorld().isClient()){
            ((ServerWorld)this.getWorld()).spawnParticles(ParticleTypes.LARGE_SMOKE, this.getX(),this.getY(),this.getZ(), 5, 0, 0.0, 0, 0.0);
        }
        if(this.groundCollision || this.horizontalCollision || this.verticalCollision){
            this.getWorld().createExplosion(this,this.getX(),this.getY(),this.getZ(),5, World.ExplosionSourceType.TNT);
            List<Entity> list = this.getWorld().getOtherEntities(this,this.getBoundingBox().expand(5,1,5));
            if(list!=null){
                for(Entity entity:list){
                    if(entity instanceof LivingEntity livingEntity){
                        if(livingEntity!=this.owner){
                            livingEntity.setFireTicks(300);
                        }
                    }
                }
            }
            this.remove(RemovalReason.DISCARDED);
        }

        if(this.age>=400){
            this.remove(RemovalReason.DISCARDED);
        }

    }
    @Override
    protected boolean updateWaterState() {
        return true;
    }
    @Override
    protected void updateLimbs(float posDelta) {
        float f = this.getPose() == EntityPose.STANDING ? Math.min(posDelta * 6.0f,1.0f) :0.0f;
        this.limbAnimator.updateLimbs(f,0.2f);
    }

    public void setMagic(int i) {
        this.dataTracker.set(MAGIC,i);
    }


    public int getMagic() {
        return this.dataTracker.get(MAGIC);
    }

    @Override
    protected void initGoals() {

    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }

}