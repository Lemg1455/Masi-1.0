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
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.world.EntityView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;


public class ArcaneMinionEntity extends AnimalEntity {
    private static final TrackedData<Boolean> ATTACKING =
            DataTracker.registerData(ArcaneMinionEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> MAGIC =
            DataTracker.registerData(ArcaneMinionEntity.class, TrackedDataHandlerRegistry.INTEGER);

    //待机动作
    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeOut = 0;
    //攻击动作
    public final AnimationState attackAnimationState = new AnimationState();
    public int attackAnimationTimeOut = 0;
    @Nullable
    private UUID ownerUuid;
    private PlayerEntity owner;
    public int energy=0;
    private int releaseContinueTime = 0;
    Magic magic = null;

    public ArcaneMinionEntity(EntityType<? extends ArcaneMinionEntity> entityType, World world) {
        super((EntityType<? extends AnimalEntity>) entityType, world);
        this.setStepHeight(1.0f);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ATTACKING,false);
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
    public void setEnergy(int energy1) {
        this.energy=energy1;
    }
    public List<? extends PlayerEntity> getPlayers() {
        return this.getWorld().getPlayers();
    }
    @Nullable
    public PlayerEntity getPlayerByUuid(UUID uuid) {
        for (int i = 0; i < this.getPlayers().size(); ++i) {
            PlayerEntity playerEntity = this.getPlayers().get(i);
            if (!uuid.equals(playerEntity.getUuid())) continue;
            return playerEntity;
        }
        return null;
    }

    @Nullable
    public LivingEntity getOwner() {
        UUID uUID = this.getOwnerUuid();
        if (uUID == null) {
            return null;
        }
        return this.getPlayerByUuid(uUID);
    }

    public int getEnergy() {
        return this.energy;
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
        nbt.putInt("energy",this.energy);
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
        if (nbt.containsUuid("energy")) {
            this.energy=nbt.getInt("energy");
        }
    }


    public void setUpAnimationState(){
        //待机动作计时器
        if (this.idleAnimationTimeOut <= 0){
            this.idleAnimationTimeOut = this.random.nextInt(40) + 80;
            this.idleAnimationState.start(this.age);
        }else {
            --this.idleAnimationTimeOut;
        }
        //攻击动作计时器
        if (this.isAttacking() && this.attackAnimationTimeOut <= 0 && releaseContinueTime <= 0){
            attackAnimationTimeOut = 100;
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
        if(!this.getWorld().isClient()){
            if(MagicUtil.ENERGY.get(this)!=null){
                if(MagicUtil.ENERGY.get(this)<=0){
                    this.damage(this.getWorld().getDamageSources().magic(),999);
                }
            }else {
                MagicUtil.ENERGY.put(this,this.energy);
            }
        }
        //动作的计时器
        if (this.getWorld().isClient()){
            setUpAnimationState();
            List<Item> list = getArcaneMagics();
            int i = this.getMagic();
            if(i!=-2){
                magic = (Magic) list.get(i);
                if (magic.releaseContinueTime() > 0) {
                    this.releaseContinueTime = magic.releaseContinueTime();
                }
            }
            if(releaseContinueTime>0){
                releaseContinueTime--;
            }
        }
        if(!this.getWorld().isClient()) {
            if(this.getTarget()!=null){
                ((ServerWorld)this.getWorld()).spawnParticles(new DustParticleEffect(Vec3d.unpackRgb(0xDC71E8).toVector3f(), 5.0f), this.getX(), this.getY()+0.5, this.getZ(), 0, 0, 0.0, 0, 0.0);
                ((ServerWorld)this.getWorld()).spawnParticles(new DustParticleEffect(Vec3d.unpackRgb(0xDC71E8).toVector3f(), 5.0f), this.getX()-0.5, this.getY()+1, this.getZ(), 0, 0, 0.0, 0, 0.0);
                ((ServerWorld)this.getWorld()).spawnParticles(new DustParticleEffect(Vec3d.unpackRgb(0xDC71E8).toVector3f(), 5.0f), this.getX()+0.5, this.getY()+1, this.getZ(), 0, 0, 0.0, 0, 0.0);
                ((ServerWorld)this.getWorld()).spawnParticles(new DustParticleEffect(Vec3d.unpackRgb(0xDC71E8).toVector3f(), 5.0f), this.getX(), this.getY()+1, this.getZ()-0.5, 0, 0, 0.0, 0, 0.0);
                ((ServerWorld)this.getWorld()).spawnParticles(new DustParticleEffect(Vec3d.unpackRgb(0xDC71E8).toVector3f(), 5.0f), this.getX(), this.getY()+1, this.getZ()+0.5, 0, 0, 0.0, 0, 0.0);

            }else {
                ((ServerWorld)this.getWorld()).spawnParticles(new DustParticleEffect(Vec3d.unpackRgb(0x00D1FF).toVector3f(), 5.0f), this.getX(), this.getY()+0.5, this.getZ(), 0, 0, 0.0, 0, 0.0);
                ((ServerWorld)this.getWorld()).spawnParticles(new DustParticleEffect(Vec3d.unpackRgb(0x00D1FF).toVector3f(), 5.0f), this.getX()-0.5, this.getY()+1, this.getZ(), 0, 0, 0.0, 0, 0.0);
                ((ServerWorld)this.getWorld()).spawnParticles(new DustParticleEffect(Vec3d.unpackRgb(0x00D1FF).toVector3f(), 5.0f), this.getX()+0.5, this.getY()+1, this.getZ(), 0, 0, 0.0, 0, 0.0);
                ((ServerWorld)this.getWorld()).spawnParticles(new DustParticleEffect(Vec3d.unpackRgb(0x00D1FF).toVector3f(), 5.0f), this.getX(), this.getY()+1, this.getZ()-0.5, 0, 0, 0.0, 0, 0.0);
                ((ServerWorld)this.getWorld()).spawnParticles(new DustParticleEffect(Vec3d.unpackRgb(0x00D1FF).toVector3f(), 5.0f), this.getX(), this.getY()+1, this.getZ()+0.5, 0, 0, 0.0, 0, 0.0);

            }
            if (this.isAttacking() && this.attackAnimationTimeOut <= 0 && releaseContinueTime <= 0) {
                attackAnimationTimeOut = 100;
                MagicUtil.ENERGY.put(this,MagicUtil.ENERGY.get(this)-10);
                if (releaseContinueTime <= 0) {
                    List<Item> list = getArcaneMagics();
                    if (list != null) {
                        Random random1 = new Random();
                        int i = random1.nextInt(list.size());
                        magic = (Magic) list.get(i);
                        this.setMagic(i);
                        if (magic.releaseContinueTime() > 0) {
                            this.releaseContinueTime = magic.releaseContinueTime();
                        }
                    }
                }
            }else if(this.attackAnimationTimeOut>0){
                double yawRadians = Math.toRadians(this.getYaw() + 90);
                double x = this.getX() + Math.cos(yawRadians) * 2;
                double z = this.getZ() + Math.sin(yawRadians) * 2;
                double y = this.getBodyY(1.0) + 2;
                if(this.attackAnimationTimeOut>50){
                    ((ServerWorld)this.getWorld()).spawnParticles(Masi.CIRCLE_FORWARD_PURPLE, x, y, z, 0, 0, 0.0, 0, 0.0);

                }else {
                    ((ServerWorld)this.getWorld()).spawnParticles(Masi.LARGE_CIRCLE_FORWARD_PURPLE, x, y, z, 0, 0, 0.0, 0, 0.0);

                }

                attackAnimationTimeOut--;
            }
            if(releaseContinueTime>0){
                releaseContinueTime--;
            }
        }
        if (releaseContinueTime <= 0) {
            if (attackAnimationTimeOut == 50) {
                magic.release(ItemStack.EMPTY, this.getWorld(), this, magic.singFinishTick());
            }
        } else {
            magic.release(ItemStack.EMPTY, this.getWorld(), this, magic.singFinishTick());
        }
        if (attackAnimationTimeOut>50) {
            magic.onSinging(ItemStack.EMPTY, this.getWorld(), this, 100-attackAnimationTimeOut);
        }
    }

    public List<Item> getArcaneMagics(){
        for(List<Object> list : MagicGroups.magicGroups){
            if(list.get(1).equals(Text.translatable("magicGroup.arcane"))){
                ArrayList<Item> list1 = new ArrayList<>();
                list1.addAll((List<Item>)list.get(0));
                list1.remove(ModItems.ARCANE_MINION_MAGIC);
                return list1;
            }
        }
        return null;
    }
    @Override
    protected void updateLimbs(float posDelta) {
        float f = this.getPose() == EntityPose.STANDING ? Math.min(posDelta * 6.0f,1.0f) :0.0f;
        this.limbAnimator.updateLimbs(f,0.2f);
    }

    @Override
    public void setAttacking(boolean attacking) {
        this.dataTracker.set(ATTACKING,attacking);
    }

    @Override
    public boolean isAttacking() {
        return this.dataTracker.get(ATTACKING);
    }


    public void setMagic(int i) {
        this.dataTracker.set(MAGIC,i);
    }


    public int getMagic() {
        return this.dataTracker.get(MAGIC);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0,new SwimGoal(this));
        this.goalSelector.add(1, new FollowMinionOwnerGoal(this, 1.0, 12.0f, 4.0f, false));//跟随主人

        this.goalSelector.add(4,new WanderAroundFarGoal(this,1.0D));//徘徊
        this.goalSelector.add(5,new LookAtEntityGoal(this, PlayerEntity.class,5f));
        this.goalSelector.add(6,new LookAroundGoal(this));

        this.goalSelector.add(3,new ArcaneMinionAttackGoal(this,1D,true));
        this.targetSelector.add(1, new TrackMinionOwnerAttackerGoal(this));//保护主人
        this.targetSelector.add(2, new AttackWithMinionOwnerGoal(this));//攻击主人的目标
        this.targetSelector.add(3,new RevengeGoal(this));//攻击仇恨目标

    }
    public static DefaultAttributeContainer.Builder createArcaneMinionAttributes(){

        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH,50)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED,0.20F)
                .add(EntityAttributes.GENERIC_ARMOR,0.5F)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE,10)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE,40)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE,1.5);
    }
    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }
}