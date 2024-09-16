package com.lemg.masi.item.Magics;

import com.lemg.masi.Masi;
import com.lemg.masi.entity.entities.MagicBulletEntity;
import com.lemg.masi.item.ModItems;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LightningMagic extends Magic{

    public LightningMagic(Settings settings,int singFinishTick,int energyConsume,int studyNeed) {
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
        magicBullet.setVelocity(user, user.getPitch(), user.getYaw(), 0.0f, 1.0f, 1.0f);
        magicBullet.setNoGravity(true);
        magicBullet.magic = this;
        user.getWorld().spawnEntity(magicBullet);
        super.release(stack,world,user,singingTicks);

    }
    @Override
    public void onSinging(ItemStack stack, World world, LivingEntity user, float singingTicks){
        if(!user.getWorld().isClient()){
            ((ServerWorld)user.getWorld()).spawnParticles(Masi.CIRCLE_GROUND_YELLOW, user.getX(),user.getY(),user.getZ(), 0, 0, 0.0, 0, 0.0);

            if(user.getItemUseTime() >= singFinishTick()){
                double yawRadians = Math.toRadians(user.getYaw()+90);
                double x = user.getX() + Math.cos(yawRadians) * 1;
                double z = user.getZ() + Math.sin(yawRadians) * 1;
                double y = user.getY()+2;
                ((ServerWorld)user.getWorld()).spawnParticles(Masi.CIRCLE_FORWARD_YELLOW, x,y,z, 0, 0, 0.0, 0, 0.0);

            }
        }
    }
    @Override
    public void BulletEffect(HitResult hitResult, LivingEntity livingEntity,MagicBulletEntity magicBullet){
        BlockPos blockPos = null;
        if(hitResult.getType()== HitResult.Type.ENTITY){
            blockPos = ((EntityHitResult)hitResult).getEntity().getBlockPos();
        }else if(hitResult.getType()== HitResult.Type.BLOCK){
            blockPos = ((BlockHitResult)hitResult).getBlockPos();
        }
        magicBullet.discard();
        if(blockPos==null){
            return;
        }

        LightningEntity lightningEntity;
        if ((lightningEntity = EntityType.LIGHTNING_BOLT.create(livingEntity.getWorld())) != null) {
            lightningEntity.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(blockPos));
            lightningEntity.setChanneler(livingEntity instanceof ServerPlayerEntity ? (ServerPlayerEntity)livingEntity : null);
            livingEntity.getWorld().spawnEntity(lightningEntity);
        }
    }


    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.masi.lightning_magic.tooltip"));
        super.appendTooltip(stack,world,tooltip,context);
    }
}
