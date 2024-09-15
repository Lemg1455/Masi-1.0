package com.lemg.masi.item.Magics;

import com.lemg.masi.Masi;
import com.lemg.masi.entity.MagicBulletEntity;
import com.lemg.masi.item.ModItems;
import com.lemg.masi.network.ModMessage;
import com.lemg.masi.util.MagicUtil;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.*;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.util.CaveSurface;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DeathDeclarationMagic extends Magic{


    public DeathDeclarationMagic(Settings settings,int singFinishTick,int energyConsume,int studyNeed) {
        super(settings,singFinishTick,energyConsume,studyNeed);
    }
    @Override
    public boolean Multiple(){
        return true;
    }

    @Override
    public void release(ItemStack stack, World world, LivingEntity user, float singingTicks){
        MagicBulletEntity magicBullet = new MagicBulletEntity(user.getWorld(), user);
        magicBullet.setItem(new ItemStack(ModItems.LIGHTNING_BULLET));
        magicBullet.setVelocity(user, user.getPitch(), user.getYaw(), 0.0f, 0.5f, 1.0f);
        magicBullet.setNoGravity(true);
        magicBullet.magic = this;
        user.getWorld().spawnEntity(magicBullet);
        super.release(stack,world,user,singingTicks);

    }
    @Override
    public void onSinging(ItemStack stack, World world, LivingEntity user, float singingTicks){
        if(!user.getWorld().isClient()){
            ((ServerWorld)user.getWorld()).spawnParticles(Masi.CIRCLE_GROUND_BLACK, user.getX(),user.getY(),user.getZ(), 0, 0, 0.0, 0, 0.0);

            if(user.getItemUseTime() >= singFinishTick()){
                double yawRadians = Math.toRadians(user.getYaw()+90);
                double x = user.getX() + Math.cos(yawRadians) * 1;
                double z = user.getZ() + Math.sin(yawRadians) * 1;
                double y = user.getY()+2;
                ((ServerWorld)user.getWorld()).spawnParticles(Masi.CIRCLE_FORWARD_BLACK, x,y,z, 0, 0, 0.0, 0, 0.0);
            }
        }
    }
    @Override
    public void BulletEffect(HitResult hitResult, LivingEntity livingEntity,MagicBulletEntity magicBullet){
        Entity entity = null;
        if(hitResult.getType()== HitResult.Type.ENTITY){
            entity = ((EntityHitResult)hitResult).getEntity();
        }
        magicBullet.discard();
        if(entity==null){
            return;
        }
        if(entity instanceof LivingEntity livingEntity1){
            livingEntity1.damage(livingEntity1.getWorld().getDamageSources().magic(),1);
            MagicUtil.putEffect(livingEntity1.getWorld(),livingEntity1,livingEntity,this,1200);
        }
    }
    @Override
    public void magicEffect(ItemStack staffStack, World world, LivingEntity user, Object aim,float ticks) {
        if (aim instanceof LivingEntity livingEntity) {
            if(!world.isClient()){
                ((ServerWorld)livingEntity.getWorld()).spawnParticles(new DustParticleEffect(Vec3d.unpackRgb(0x000000).toVector3f(), 1.0f), livingEntity.getX(), livingEntity.getBodyY(1.0)+1, livingEntity.getZ(), 0, 0, 0.0, 0, 0.0);
            }

            if(ticks==0){
                if(livingEntity.isAlive()){
                    if(livingEntity.getGroup()==EntityGroup.UNDEAD){
                        livingEntity.setHealth(livingEntity.getMaxHealth());
                    }else {
                        boolean b1 = true;
                        if(livingEntity instanceof PlayerEntity player){
                            if(player.getAbilities().creativeMode){
                                b1 = false;
                            }
                        }
                        if(b1){
                            livingEntity.damage(world.getDamageSources().magic(),100);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.masi.death_declaration_magic.tooltip"));
        super.appendTooltip(stack,world,tooltip,context);
    }
}
