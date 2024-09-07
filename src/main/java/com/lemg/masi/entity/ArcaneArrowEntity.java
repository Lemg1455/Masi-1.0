package com.lemg.masi.entity;


import com.lemg.masi.item.Magics.Magic;
import com.lemg.masi.util.MagicUtil;
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
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;


public class ArcaneArrowEntity extends PersistentProjectileEntity {
    public int count;
    public ArcaneArrowEntity(EntityType<? extends ArcaneArrowEntity> entityType, World world) {
        super((EntityType<? extends PersistentProjectileEntity>)entityType, world);
    }

    public ArcaneArrowEntity(World world, double x, double y, double z) {
        super(ModEntities.ARCANE_ARROW, x, y, z, world);
    }

    public ArcaneArrowEntity(World world, LivingEntity owner,int count) {
        super(ModEntities.ARCANE_ARROW, owner, world);
        this.count = count;
    }
    @Override
    protected void onCollision(HitResult hitResult) {
        if(hitResult.getType()== HitResult.Type.MISS){
            return;
        }
        super.onCollision(hitResult);
        if(!(this.getOwner() instanceof LivingEntity)){
            return;
        }
        if(hitResult.getType()==HitResult.Type.ENTITY && ((EntityHitResult)hitResult).getEntity() instanceof LivingEntity livingEntity){
            int amount = 5;
            if(this.getOwner()!=null){
                if(MagicUtil.MAX_ENERGY.get((LivingEntity)this.getOwner())!=null){
                    amount = 5 + MagicUtil.MAX_ENERGY.get((LivingEntity)this.getOwner())/10;
                }
            }
            livingEntity.damage(this.getWorld().getDamageSources().magic(),amount);
        }
        if(!this.getWorld().isClient() && count>0){
            List<Entity> list = this.getWorld().getOtherEntities(this.getOwner(), this.getBoundingBox().expand(15,15,15));
            List<LivingEntity> livingEntities = new ArrayList<>();
            if(!list.isEmpty()){
                for(Entity entity : list) {
                    if (entity instanceof LivingEntity livingEntity) {
                        if(livingEntity.isAlive()){
                            livingEntities.add(livingEntity);
                        }
                    }
                }
            }

            if(!livingEntities.isEmpty()){
                for(LivingEntity livingEntity : livingEntities){
                    if(count>0){
                        ArcaneArrowEntity arcaneArrowEntity = new ArcaneArrowEntity(this.getWorld(), (LivingEntity) this.getOwner(), Math.max((count - livingEntities.size()), 0));
                        arcaneArrowEntity.setPosition(hitResult.getPos().add(0,5,0));
                        double x = livingEntity.getX() - arcaneArrowEntity.getX();
                        double y = livingEntity.getBodyY(0.5f) - arcaneArrowEntity.getY();
                        double z = livingEntity.getZ() - arcaneArrowEntity.getZ();
                        arcaneArrowEntity.setVelocity(x/10,y/10,z/10);
                        arcaneArrowEntity.setNoGravity(true);
                        this.getWorld().spawnEntity(arcaneArrowEntity);
                        count--;
                    }
                }
            }

            if(count>0){
                for(int i=0;i<count;i++){
                    Random random = new Random();
                    ArcaneArrowEntity arcaneArrowEntity = new ArcaneArrowEntity(this.getWorld(), (LivingEntity) this.getOwner(), 0);
                    arcaneArrowEntity.setPosition(hitResult.getPos().add(random.nextDouble(-1.5,1.5),6,random.nextDouble(-1.5,1.5)));
                    double x = hitResult.getPos().getX() - arcaneArrowEntity.getX();
                    double y = hitResult.getPos().getY()+1 - arcaneArrowEntity.getY();
                    double z = hitResult.getPos().getZ() - arcaneArrowEntity.getZ();
                    arcaneArrowEntity.setVelocity(x/10,y/10,z/10);
                    arcaneArrowEntity.setNoGravity(true);
                    this.getWorld().spawnEntity(arcaneArrowEntity);
                }
                count=0;
            }
        }
    }

    @Override
    public boolean canHit() {
        return false;
    }

    @Override
    protected ItemStack asItemStack() {
        return null;
    }

    @Override
    public void tick() {
        super.tick();
        if(!this.getWorld().isClient()){
            ((ServerWorld)this.getWorld()).spawnParticles(new DustParticleEffect(Vec3d.unpackRgb(0x00D1FF).toVector3f(), 1.0f), this.getX(), this.getY(), this.getZ(), 0, 0, 0.0, 0, 0.0);
        }
        if(this.age>=60){
            this.remove(RemovalReason.DISCARDED);
        }
    }
    @Override
    protected boolean updateWaterState() {
        return true;
    }

    protected boolean tryPickup(PlayerEntity player) {
        return false;
    }

}