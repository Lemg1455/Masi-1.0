package com.lemg.masi.item.Magics;

import com.lemg.masi.Masi;
import com.lemg.masi.entity.ArcaneMinionEntity;
import com.lemg.masi.item.ModItems;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ArcaneBowMagic extends Magic{
    public ArcaneBowMagic(Settings settings) {
        super(settings);
    }
    @Override
    public int singFinishTick(){
        return 20;
    }

    @Override
    public int energyConsume(){
        return 50;
    }
    @Override
    public int studyNeed(){
        return 10;
    }

    @Override
    public void release(ItemStack stack, World world, LivingEntity user, float singingTicks){
        ItemStack itemStack = new ItemStack(ModItems.ARCANE_BOW);
        if(user instanceof PlayerEntity playerEntity){
            if(playerEntity.getInventory().getEmptySlot()!=-1){
                playerEntity.getInventory().insertStack(itemStack);
            }else {
                playerEntity.dropStack(itemStack);
            }
        } else if (user instanceof ArcaneMinionEntity arcaneMinion) {
            arcaneMinion.dropStack(itemStack);
        }
        super.release(stack,world,user,singingTicks);
    }
    @Override
    public void onSinging(ItemStack stack, World world, LivingEntity user, float singingTicks){
        if(!user.getWorld().isClient()){
            ((ServerWorld)user.getWorld()).spawnParticles(Masi.CIRCLE_GROUND_PURPLE, user.getX(),user.getY(),user.getZ(), 0, 0, 0.0, 0, 0.0);
            if(user.getItemUseTime() >= singFinishTick()){
                double yawRadians = Math.toRadians(user.getYaw()+90);
                double x = user.getX() + Math.cos(yawRadians) * 1;
                double z = user.getZ() + Math.sin(yawRadians) * 1;
                double y = user.getY()+2;
                ((ServerWorld)user.getWorld()).spawnParticles(Masi.CIRCLE_FORWARD_PURPLE, x,y,z, 0, 0, 0.0, 0, 0.0);
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.masi.arcane_bow_magic.tooltip"));
        super.appendTooltip(stack,world,tooltip,context);
    }
}
