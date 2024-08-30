package com.lemg.masi.item.Magics;

import com.lemg.masi.Masi;
import com.lemg.masi.network.ModMessage;
import com.lemg.masi.util.MagicUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ElementAggregationMagic extends Magic{
    public ElementAggregationMagic(Settings settings) {
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
    public ConcurrentHashMap<PlayerEntity,ConcurrentHashMap<LivingEntity, Integer>> aim = new ConcurrentHashMap<>();

    @Override
    public void release(ItemStack stack, World world, LivingEntity user, float singingTicks){

        super.release(stack,world,user,singingTicks);
    }
    @Override
    public void onSinging(ItemStack stack, World world, LivingEntity user, float singingTicks){
        if(user instanceof PlayerEntity player) {
            if (MagicUtil.ENERGY.get(player) >= this.energyConsume()) {
                if (!player.getWorld().isClient()) {
                    if(aim.get(player)==null){
                        List<Entity> list = world.getOtherEntities(user, user.getBoundingBox().expand(20,20,20));
                        for(Entity entity : list){
                            if(entity instanceof LivingEntity livingEntity){
                                if(livingEntity.isAlive()){
                                    Random random = new Random();
                                    int i = random.nextInt(4);
                                    ConcurrentHashMap<LivingEntity, Integer> map = new ConcurrentHashMap<>();
                                    map.put(livingEntity,i);
                                    aim.put(player,map);
                                    break;
                                }
                            }
                        }
                    }
                    if(aim.get(player)!=null){
                        ConcurrentHashMap<LivingEntity, Integer> map = aim.get(player);
                        LivingEntity livingEntity = map.keys().nextElement();
                        int i = map.get(livingEntity);
                        if(i==0){
                            MagicUtil.circleGround(24, user,user.getX(),user.getY(),user.getZ());
                            MagicUtil.circleGround(27, livingEntity,livingEntity.getX(),livingEntity.getY()+10,livingEntity.getZ());

                        } else if (i==1) {
                            MagicUtil.circleGround(0, user,user.getX(),user.getY(),user.getZ());
                            MagicUtil.circleGround(3, livingEntity,livingEntity.getX(),livingEntity.getY()+10,livingEntity.getZ());

                        } else if (i==2) {
                            MagicUtil.circleGround(12, user,user.getX(),user.getY(),user.getZ());
                            MagicUtil.circleGround(15, livingEntity,livingEntity.getX(),livingEntity.getY()+10,livingEntity.getZ());

                        } else {
                            MagicUtil.circleGround(8, user,user.getX(),user.getY(),user.getZ());
                            MagicUtil.circleGround(11, livingEntity,livingEntity.getX(),livingEntity.getY()+10,livingEntity.getZ());
                        }
                        if(singingTicks%30==0){
                            Vec3d Pos1 = livingEntity.getPos().add(0,10,0);
                            Vec3d Pos2 = livingEntity.getPos();
                            Vec3d direction = Pos1.subtract(Pos2).normalize();
                            double length = Pos1.distanceTo(Pos2);

                            int mode = 105;
                            if(i==0){
                                mode = 105;
                            }else if(i==1){
                                mode = 106;
                            }else if(i==2){
                                mode = 107;
                            }else if(i==3){
                                mode = 108;
                            }

                            for (int j = 0; j <= 10; j++) {
                                double fraction = (double) j / 10;
                                Vec3d particlePos = Pos2.add(direction.multiply(fraction * length));
                                MagicUtil.circleForward(mode,player,particlePos.x, particlePos.y, particlePos.z);
                            }

                            Box box = new Box(Pos1,Pos2).expand(1,0,1);
                            List<Entity> list = player.getWorld().getOtherEntities(player,box);
                            for(Entity entity : list){
                                if(entity instanceof LivingEntity livingEntity1){
                                    if(livingEntity1==player){
                                        continue;
                                    }
                                    livingEntity1.damage(player.getWorld().getDamageSources().playerAttack(player),10f);
                                    if(i==0){
                                        livingEntity1.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 5,false,false,false));
                                    }else if(i==1){
                                        livingEntity1.setAir(0);
                                        livingEntity.setFrozenTicks(100);
                                    } else if (i==2) {
                                        livingEntity1.setFireTicks(100);
                                    } else if (i==3) {
                                        livingEntity1.setVelocity(0,1,0);
                                        livingEntity1.addStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, 100, 4,false,false,false));
                                    }
                                }
                            }
                            aim.remove(player);
                        }
                    }
                }
                if (!player.getAbilities().creativeMode && !MagicUtil.isTrial(player)) {

                    if(player.getWorld().isClient()){
                        if(singingTicks%30==0){
                            //节约魔力附魔，最高减少三分之一消耗
                            int e = EnchantmentHelper.getLevel(Masi.ENERGY_CONSERVATION, stack);
                            int energyConsume = this.energyConsume();
                            if (e > 0) {
                                energyConsume = (int) (energyConsume - (float) (energyConsume * e * (0.11)));
                            }

                            int energy = MagicUtil.ENERGY.get(player) - energyConsume;
                            MagicUtil.ENERGY.put(player,energy);
                            PacketByteBuf buf = PacketByteBufs.create();
                            buf.writeInt(0);
                            buf.writeUuid(player.getUuid());
                            buf.writeInt(energy);
                            ClientPlayNetworking.send(ModMessage.ENERGY_UPDATE_ID, buf);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.masi.element_aggregation_magic.tooltip"));
        super.appendTooltip(stack,world,tooltip,context);
    }
}
