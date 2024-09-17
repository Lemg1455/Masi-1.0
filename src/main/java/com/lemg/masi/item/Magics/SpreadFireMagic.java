package com.lemg.masi.item.Magics;

import com.lemg.masi.Masi;
import com.lemg.masi.util.MagicUtil;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SpreadFireMagic extends Magic{

    public SpreadFireMagic(Settings settings, int singFinishTick, int energyConsume, int studyNeed) {
        super(settings,singFinishTick,energyConsume,studyNeed);
    }

    @Override
    public boolean passive(){
        return true;
    }

    @Override
    public void release(ItemStack stack, World world, LivingEntity user, float singingTicks){
        List<Entity> list = world.getOtherEntities(user,user.getBoundingBox().expand(10));
        for(Entity entity : list){
            if(entity instanceof LivingEntity livingEntity){
                if(livingEntity.deathTime==1 && livingEntity.wasOnFire && !MagicUtil.teamEntity(livingEntity,user)){
                    List<Entity> list2 = world.getOtherEntities(user,livingEntity.getBoundingBox().expand(10));
                    for(Entity entity1 : list2){
                        if(entity1 instanceof LivingEntity livingEntity1){
                            if(livingEntity1.isAlive() && !MagicUtil.teamEntity(livingEntity1,user)){
                                livingEntity1.setFireTicks(livingEntity1.getFireTicks()+livingEntity.getFireTicks());
                                livingEntity1.damage(world.getDamageSources().inFire(),2);

                                Vec3d direction = livingEntity1.getPos().add(0,1,0).subtract(livingEntity.getPos()).normalize();
                                double length = livingEntity1.getPos().add(0,1,0).distanceTo(livingEntity.getPos());
                                for (int i = 0; i <= 10; i++) {
                                    double fraction = (double) i / 10;
                                    Vec3d particlePos = livingEntity.getPos().add(direction.multiply(fraction * length));
                                    ((ServerWorld)livingEntity.getWorld()).spawnParticles(ParticleTypes.FLAME, particlePos.x, particlePos.y, particlePos.z, 0, 0, 0.0, 0, 0.0);
                                }
                            }
                        }
                    }
                }
            }
        }


    }
    @Override
    public void onSinging(ItemStack stack, World world, LivingEntity user, float singingTicks){

    }


    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.masi.spread_fire_magic.tooltip"));
        super.appendTooltip(stack,world,tooltip,context);
    }
}
