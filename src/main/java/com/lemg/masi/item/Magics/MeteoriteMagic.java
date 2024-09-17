package com.lemg.masi.item.Magics;

import com.lemg.masi.Masi;
import com.lemg.masi.entity.ModEntities;
import com.lemg.masi.entity.entities.MeteoriteEntity;
import com.lemg.masi.util.MagicUtil;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MeteoriteMagic extends Magic{
    public MeteoriteMagic(Settings settings, int singFinishTick, int energyConsume, int studyNeed) {
        super(settings,singFinishTick,energyConsume,studyNeed);
    }
    @Override
    public boolean Multiple(){
        return true;
    }

    @Override
    public void release(ItemStack stack, World world, LivingEntity user, float singingTicks){
        if(!world.isClient()){
            MeteoriteEntity meteoriteEntity = ModEntities.METEORITE.create(world);
            if(meteoriteEntity!=null){
                float yaw = user.getYaw();
                float pitch = user.getPitch();
                float ff = -MathHelper.sin(yaw * ((float)Math.PI / 180)) * MathHelper.cos(pitch * ((float)Math.PI / 180));
                float gg = -MathHelper.sin(pitch * ((float)Math.PI / 180));
                float hh = MathHelper.cos(yaw * ((float)Math.PI / 180)) * MathHelper.cos(pitch * ((float)Math.PI / 180));

                double x = user.getX()+ff*8;
                double z = user.getZ()+hh*8;
                double y = user.getY()+gg*8+2;
                Vec3d pos = new Vec3d(x,y,z);
                meteoriteEntity.refreshPositionAndAngles(pos.getX(),pos.getY()+15,pos.getZ(),0,0);
                world.spawnEntity(meteoriteEntity);
                MagicUtil.putEffect(world,pos,user,this,100);
            }
        }
    }
    @Override
    public void onSinging(ItemStack stack, World world, LivingEntity user, float singingTicks){
        if(!user.getWorld().isClient()){
            ((ServerWorld)user.getWorld()).spawnParticles(Masi.CIRCLE_GROUND_RED, user.getX(),user.getY(),user.getZ(), 0, 0, 0.0, 0, 0.0);

            if(user.getItemUseTime() >= singFinishTick()){
                float yaw = user.getYaw();
                float pitch = user.getPitch();
                float ff = -MathHelper.sin(yaw * ((float)Math.PI / 180)) * MathHelper.cos(pitch * ((float)Math.PI / 180));
                float gg = -MathHelper.sin(pitch * ((float)Math.PI / 180));
                float hh = MathHelper.cos(yaw * ((float)Math.PI / 180)) * MathHelper.cos(pitch * ((float)Math.PI / 180));

                double x = user.getX()+ff*8;
                double z = user.getZ()+hh*8;
                double y = user.getY()+gg*8+2;

                Box box = new Box(x-3,y-1,z-3,x+3,y+10,z+3);
                int mx = (int) box.minX;
                int my = (int) box.minY;
                int mz = (int) box.minZ;
                ParticleEffect particleEffect = ParticleTypes.FLAME;
                for(;mx<=(int) box.maxX;mx++){
                    ((ServerWorld)user.getWorld()).spawnParticles(particleEffect, mx,y,(int) box.minZ, 0, 0, 0.0, 0, 0.0);
                    ((ServerWorld)user.getWorld()).spawnParticles(particleEffect, mx,y,(int) box.maxZ, 0, 0, 0.0, 0, 0.0);
                }
                for(;mz<=(int) box.maxZ;mz++){
                    ((ServerWorld)user.getWorld()).spawnParticles(particleEffect, (int) box.minX,y+0.2,mz, 0, 0, 0.0, 0, 0.0);
                    ((ServerWorld)user.getWorld()).spawnParticles(particleEffect, (int) box.maxX,y+0.2,mz, 0, 0, 0.0, 0, 0.0);
                }
            }
        }
    }

    @Override
    public void magicEffect(ItemStack staffStack, World world, LivingEntity user, Object aim,float ticks){
        if (aim instanceof Vec3d pos) {
            if(!world.isClient()){
                double x = pos.getX();
                double y = pos.getY();
                double z = pos.getZ();

                Box box = new Box(x-3,y-1,z-3,x+3,y+10,z+3);
                int mx = (int) box.minX;
                int my = (int) box.minY;
                int mz = (int) box.minZ;
                ParticleEffect particleEffect = ParticleTypes.FLAME;
                for(;mx<=(int) box.maxX;mx++){
                    ((ServerWorld)user.getWorld()).spawnParticles(particleEffect, mx,y,(int) box.minZ, 0, 0, 0.0, 0, 0.0);
                    ((ServerWorld)user.getWorld()).spawnParticles(particleEffect, mx,y,(int) box.maxZ, 0, 0, 0.0, 0, 0.0);
                }
                for(;mz<=(int) box.maxZ;mz++){
                    ((ServerWorld)user.getWorld()).spawnParticles(particleEffect, (int) box.minX,y+0.2,mz, 0, 0, 0.0, 0, 0.0);
                    ((ServerWorld)user.getWorld()).spawnParticles(particleEffect, (int) box.maxX,y+0.2,mz, 0, 0, 0.0, 0, 0.0);
                }
            }
        }
    }
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.masi.meteorite_magic.tooltip"));
        super.appendTooltip(stack,world,tooltip,context);
    }
}
