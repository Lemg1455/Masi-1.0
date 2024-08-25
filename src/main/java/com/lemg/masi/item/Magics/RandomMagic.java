package com.lemg.masi.item.Magics;

import com.lemg.masi.item.MagicGroups;
import com.lemg.masi.util.MagicUtil;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class RandomMagic extends Magic{
    public RandomMagic(Settings settings) {
        super(settings);
    }
    @Override
    public int singFinishTick(){
        return 20;
    }

    @Override
    public int energyConsume(){
        return 20;
    }
    @Override
    public int studyNeed(){
        return 999;
    }
    @Override
    public boolean Multiple(){
        return false;
    }
    @Override
    public void release(ItemStack stack, World world, LivingEntity user, float singingTicks){
        List<Item> items = new ArrayList<>();
        for(List<Object> magicGroup : MagicGroups.magicGroups){
            items.addAll((Collection<? extends Item>) magicGroup.get(0));
        }
        Random random = new Random();
        int i = random.nextInt(items.size());
        Item magic = items.get(i);
        if(magic instanceof Magic magic1){
            magic1.release(stack,world,user,singingTicks);
        }
        super.release(stack,world,user,singingTicks);
    }
    @Override
    public void onSinging(ItemStack stack, World world, LivingEntity user, float singingTicks){
        if(!user.getWorld().isClient()){
            MagicUtil.circleGround(4,user);
            if(user.getItemUseTime() >= singFinishTick()){
                MagicUtil.circleForward(5,user);
            }
        }
    }


    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.masi.random_magic.tooltip"));
        super.appendTooltip(stack,world,tooltip,context);
    }
}
