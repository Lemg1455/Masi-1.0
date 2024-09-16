package com.lemg.masi.item.Magics;

import com.lemg.masi.Masi;
import com.lemg.masi.entity.entities.minions.ArcaneMinionEntity;
import com.lemg.masi.entity.ModEntities;
import com.lemg.masi.util.MagicUtil;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ArcaneMinionMagic extends Magic{

    public ArcaneMinionMagic(Settings settings,int singFinishTick,int energyConsume,int studyNeed) {
        super(settings,singFinishTick,energyConsume,studyNeed);
    }


    @Override
    public void release(ItemStack stack, World world, LivingEntity user, float singingTicks){
        ArcaneMinionEntity arcaneMinionEntity = ModEntities.ARCANE_MINION.create(user.getWorld());
        if (arcaneMinionEntity != null) {
            if(user instanceof PlayerEntity player){
                arcaneMinionEntity.setOwner(player);
                MagicUtil.ENERGY.put(arcaneMinionEntity,80);
            }
            arcaneMinionEntity.refreshPositionAndAngles(user.getPos().getX(), user.getPos().getY(), user.getPos().getZ(), 0.0f, 0.0f);
            if(!user.getWorld().isClient()){
                ((ServerWorld)user.getWorld()).spawnEntityAndPassengers(arcaneMinionEntity);
            }
        }
    }
    @Override
    public void onSinging(ItemStack stack, World world, LivingEntity user, float singingTicks){
        if(!user.getWorld().isClient()){
            ((ServerWorld)user.getWorld()).spawnParticles(Masi.LARGE_CIRCLE_GROUND_PURPLE, user.getX(),user.getY(),user.getZ(), 0, 0, 0.0, 0, 0.0);

            if(user.getItemUseTime() >= singFinishTick()){
                double yawRadians = Math.toRadians(user.getYaw()+90);
                double x = user.getX() + Math.cos(yawRadians) * 2;
                double z = user.getZ() + Math.sin(yawRadians) * 2;
                double y = user.getY()+4;
                ((ServerWorld)user.getWorld()).spawnParticles(Masi.LARGE_CIRCLE_FORWARD_PURPLE, x,y,z, 0, 0, 0.0, 0, 0.0);
            }
        }
    }


    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.masi.arcane_minion_magic.tooltip"));
        super.appendTooltip(stack,world,tooltip,context);
    }
}
