package com.lemg.masi.entity;


import com.lemg.masi.Masi;
import com.lemg.masi.entity.ai.ArcaneMinionAttackGoal;
import com.lemg.masi.entity.ai.AttackWithMinionOwnerGoal;
import com.lemg.masi.entity.ai.FollowMinionOwnerGoal;
import com.lemg.masi.entity.ai.TrackMinionOwnerAttackerGoal;
import com.lemg.masi.item.MagicGroups;
import com.lemg.masi.item.Magics.Magic;
import com.lemg.masi.item.ModItems;
import com.lemg.masi.util.MagicUtil;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;


public class SwordEnergyEntity extends AnimalEntity {
    private static final TrackedData<Integer> MAGIC =
            DataTracker.registerData(SwordEnergyEntity.class, TrackedDataHandlerRegistry.INTEGER);

    @Nullable
    private UUID ownerUuid;
    private PlayerEntity owner;
    public float size;
    public static float size1 = 1.0f;
    Magic magic = null;

    public SwordEnergyEntity(EntityType<? extends SwordEnergyEntity> entityType, World world) {
        super((EntityType<? extends AnimalEntity>) entityType, world);
        this.noClip=true;
        this.size = size1;
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

    public void setSize(float size) {
        this.size=size;
    }

    public float getSize() {
        return this.size;
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
        nbt.putFloat("size",this.size);
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
        if (nbt.contains("size")) {
            this.size=nbt.getFloat("size");
        }
    }

    @Override
    public void tick() {
        super.tick();
        if(this.age>=40){
            this.remove(RemovalReason.DISCARDED);
        }
        double width = size - 1;
        double height = size - 1;
        float amount = (float) (5 + Math.pow(2,size));
        List<Entity> list = this.getWorld().getOtherEntities(this, this.getBoundingBox().expand(width,height,width));
        if(!list.isEmpty()){
            for(Entity entity : list) {
                if (entity instanceof LivingEntity livingEntity) {
                    if(livingEntity!=this.owner){
                        livingEntity.damage(this.getWorld().getDamageSources().magic(),amount);
                    }
                }
            }
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

    public static DefaultAttributeContainer.Builder createSwordEnergyAttributes(){

        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH,50)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED,0.20F)
                .add(EntityAttributes.GENERIC_ARMOR,0.5F)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE,10)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE,40)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE,1.5);
    }
}