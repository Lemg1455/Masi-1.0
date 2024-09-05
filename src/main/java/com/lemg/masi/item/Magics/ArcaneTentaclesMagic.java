package com.lemg.masi.item.Magics;

import com.lemg.masi.Masi;
import com.lemg.masi.entity.ArcaneMinionEntity;
import com.lemg.masi.entity.ModEntities;
import com.lemg.masi.network.ModMessage;
import com.lemg.masi.util.MagicUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class ArcaneTentaclesMagic extends Magic{

    public ArcaneTentaclesMagic(Settings settings) {
        super(settings);
    }
    @Override
    public int singFinishTick(){
        return 200;
    }

    @Override
    public int energyConsume(){
        return 0;
    }
    @Override
    public int studyNeed(){
        return 5;
    }
    @Override
    public boolean Multiple(){
        return false;
    }
    @Override
    public int releaseContinueTime(){
        return 0;
    }

    @Override
    public void release(ItemStack stack, World world, LivingEntity user, float singingTicks){

    }
    @Override
    public void onSinging(ItemStack stack, World world, LivingEntity user, float singingTicks){
        if (!user.getWorld().isClient()) {
            ((ServerWorld)user.getWorld()).spawnParticles(Masi.CIRCLE_GROUND_PURPLE, user.getX(),user.getY(),user.getZ(), 0, 0, 0.0, 0, 0.0);
        }
        float yaw = user.getYaw();
        float pitch = user.getPitch();
        float ff = -MathHelper.sin(yaw * ((float)Math.PI / 180)) * MathHelper.cos(pitch * ((float)Math.PI / 180));
        float gg = -MathHelper.sin(pitch * ((float)Math.PI / 180));
        float hh = MathHelper.cos(yaw * ((float)Math.PI / 180)) * MathHelper.cos(pitch * ((float)Math.PI / 180));

        Vec3d Pos1 = new Vec3d(user.getX()+ff*9,user.getY()+gg*9,user.getZ()+hh*9);
        Vec3d Pos2 = user.getPos().add(0,2,0);

        LivingEntity livingEntity = null;
        Box box = new Box(Pos1,Pos2);
        List<Entity> list = user.getWorld().getOtherEntities(user,box);
        for(Entity entity : list){
            if(entity instanceof LivingEntity livingEntity1){
                if(entity.isAlive()){
                    livingEntity = livingEntity1;
                }
            }
        }

        if(livingEntity!=null){
            Vec3d direction = livingEntity.getPos().add(0,1,0).subtract(Pos2).normalize();
            double length = livingEntity.getPos().add(0,1,0).distanceTo(Pos2);
            int mode = 109;
            if(user instanceof PlayerEntity player){
                if(player.getPose()==EntityPose.CROUCHING){
                    mode = 110;
                }
            }
            if(user instanceof ArcaneMinionEntity arcaneMinionEntity){
                if(arcaneMinionEntity.getOwner()==livingEntity){
                    mode = 110;
                }
            }
            if(singingTicks%20==0){
                //吸取目标魔力
                if(mode==109){
                    int energy_change = 0;
                    if(MagicUtil.ENERGY.get(livingEntity)!=null){
                        //如果对方不是创造，且拥有魔力
                        if(livingEntity instanceof PlayerEntity playerEntity){
                            if (playerEntity.getAbilities().creativeMode || MagicUtil.isTrial(playerEntity)) {
                                return;
                            }
                        }
                        //如果目标魔力够5点
                        int energy = MagicUtil.ENERGY.get(livingEntity);
                        if(energy>=10){
                            if(!world.isClient()){
                                MagicUtil.energyUpdate(livingEntity,energy-10,false);
                            }
                            energy_change=10;
                            //不够5点就掉血
                        }else {
                            livingEntity.damage(world.getDamageSources().magic(),1);
                            energy_change=1;
                        }
                    }else {
                        //没有魔力也掉血
                        livingEntity.damage(world.getDamageSources().magic(),1);
                        energy_change=1;
                    }
                    livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 20, 1,false,false,true));
                    //要么吸对方5点魔力，要么吸对方血补1点魔力
                    if(MagicUtil.ENERGY.get(user)!=null){
                        if(!world.isClient()){
                            int energy = MagicUtil.ENERGY.get(user);
                            MagicUtil.energyUpdate(user,energy+energy_change,false);
                        }
                    }
               } else {
                    //把自己的魔力给目标
                    int energy_change = 0;
                    if(MagicUtil.ENERGY.get(user)!=null) {

                        //如果自己魔力够5点
                        int energy = MagicUtil.ENERGY.get(user);
                        if (energy >= 10) {
                            if (!world.isClient()) {
                                //如果使用者不是创造模式
                                if (user instanceof PlayerEntity playerEntity) {
                                    if (!playerEntity.getAbilities().creativeMode && !MagicUtil.isTrial(playerEntity)) {
                                        MagicUtil.energyUpdate(user,energy - 10,false);
                                    }
                                }
                            }
                            //自己扣5点给对方加5点
                            energy_change = 5;
                            if(MagicUtil.ENERGY.get(livingEntity)!=null){
                                int aimEnergy = MagicUtil.ENERGY.get(livingEntity);
                                if (!world.isClient()) {
                                    MagicUtil.energyUpdate(livingEntity,aimEnergy + 10,false);
                                }
                            }
                        }

                    }
                }
            }

            if(!world.isClient()){
                for (int i = 0; i <= 10; i++) {
                    double fraction = (double) i / 10;
                    Vec3d particlePos = Pos2.add(direction.multiply(fraction * length));
                    if(mode==109){
                        ((ServerWorld)user.getWorld()).spawnParticles(new DustParticleEffect(Vec3d.unpackRgb(0xDC71E8).toVector3f(), 1.0f), particlePos.x, particlePos.y, particlePos.z, 0, 0, 0.0, 0, 0.0);
                    }else {
                        ((ServerWorld)user.getWorld()).spawnParticles(new DustParticleEffect(Vec3d.unpackRgb(0x00D1FF).toVector3f(), 1.0f), particlePos.x, particlePos.y, particlePos.z, 0, 0, 0.0, 0, 0.0);
                    }
                }
            }
        }
    }


    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.masi.arcane_tentacles_magic.tooltip"));
        tooltip.add(Text.translatable("item.masi.arcane_tentacles_magic2.tooltip"));
        super.appendTooltip(stack,world,tooltip,context);
    }
}
