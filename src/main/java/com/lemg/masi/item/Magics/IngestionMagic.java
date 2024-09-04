package com.lemg.masi.item.Magics;

import com.lemg.masi.Masi;
import com.lemg.masi.network.ModMessage;
import com.lemg.masi.util.MagicUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
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

public class IngestionMagic extends Magic{
    public IngestionMagic(Settings settings) {
        super(settings);
    }
    @Override
    public int singFinishTick(){
        return 200;
    }

    @Override
    public int energyConsume(){
        return 10;
    }
    @Override
    public int studyNeed(){
        return 10;
    }

    @Override
    public void release(ItemStack stack, World world, LivingEntity user, float singingTicks){

        super.release(stack,world,user,singingTicks);
    }
    @Override
    public void onSinging(ItemStack stack, World world, LivingEntity user, float singingTicks){
        if(user instanceof PlayerEntity player) {
            if (MagicUtil.ENERGY.get(player) >= this.energyConsume()) {
                if (!player.getWorld().isClient()) {
                    MagicUtil.circleGround(8, user,user.getX(),user.getY(),user.getZ());
                }
                float yaw = user.getYaw();
                float pitch = user.getPitch();
                float ff = -MathHelper.sin(yaw * ((float)Math.PI / 180)) * MathHelper.cos(pitch * ((float)Math.PI / 180));
                float gg = -MathHelper.sin(pitch * ((float)Math.PI / 180));
                float hh = MathHelper.cos(yaw * ((float)Math.PI / 180)) * MathHelper.cos(pitch * ((float)Math.PI / 180));

                Vec3d Pos1 = new Vec3d(player.getX()+ff*15,player.getY()+gg*15,player.getZ()+hh*15);
                Vec3d Pos2 = player.getPos().add(0,1,0);
                Vec3d direction = Pos1.subtract(Pos2).normalize();
                double length = Pos1.distanceTo(Pos2);

                Box box = new Box(Pos1,Pos2);
                List<Entity> list = player.getWorld().getOtherEntities(player,box);
                for(Entity entity : list){
                    moveToPos(entity,Pos1);
                    entity.fallDistance = 0;
                    if(entity instanceof LivingEntity livingEntity){
                        livingEntity.damage(player.getWorld().getDamageSources().playerAttack(player),1f);
                    }
                }

                BlockPos blockPos = new BlockPos((int) (player.getX()+ff*15), (int) (player.getY()+gg*15), (int) (player.getZ()+hh*15));
                List<Entity> list2 = player.getWorld().getOtherEntities(player,new Box(blockPos).expand(3));
                for(Entity entity : list2){
                    moveToPos(entity,Pos1);
                    entity.fallDistance = 0;
                }

                if(!player.getWorld().isClient()){
                    for (int i = 0; i <= 10; i++) {
                        double fraction = (double) i / 10;
                        Vec3d particlePos = Pos2.add(direction.multiply(fraction * length));
                        MagicUtil.circleForward(101,player,particlePos.x, particlePos.y, particlePos.z);
                    }
                }
                if (!player.getAbilities().creativeMode && !MagicUtil.isTrial(player)) {

                    if(player.getWorld().isClient()){
                        if(singingTicks%20==0){
                            //节约魔力附魔，最高减少三分之一消耗
                            int e = EnchantmentHelper.getLevel(Masi.ENERGY_CONSERVATION, stack);
                            int energyConsume = this.energyConsume();
                            if (e > 0) {
                                energyConsume = (int) (energyConsume - (float) (energyConsume * e * (0.11)));
                            }

                            int energy = MagicUtil.ENERGY.get(player) - energyConsume;

                            MagicUtil.energyUpdate(player,energy,false);

                        }
                    }
                }
            }
        }
    }

    public void moveToPos(Entity entity,Vec3d pos){
        Vec3d vec3d3 = entity.getRotationVec(1.0f);
        double l = entity.getX() - vec3d3.x;
        double m = entity.getY()+1;
        double n = entity.getZ() - vec3d3.z;

        double o = pos.getX() - l;
        double p = pos.getY() - m;
        double q = pos.getZ() - n;

        entity.setVelocity(o/2,p/2,q/2);
        if(!entity.getWorld().isClient()){
            if(entity instanceof PlayerEntity player){
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeItemStack(this.getDefaultStack());
                buf.writeDouble(player.getVelocity().x);
                buf.writeDouble(player.getVelocity().y);
                buf.writeDouble(player.getVelocity().z);
                ServerPlayNetworking.send((ServerPlayerEntity) player, ModMessage.VELOCITY_UPDATE_ID, buf);
            }
        }
    }
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.masi.ingestion_magic.tooltip"));
        super.appendTooltip(stack,world,tooltip,context);
    }
}
