package com.lemg.masi.item.items;

import com.lemg.masi.item.Magics.Magic;
import com.lemg.masi.util.MagicUtil;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TrialCard extends Magic {

    public TrialCard(Settings settings) {
        super(settings,0,0,0);
    }


    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack handStack = user.getStackInHand(hand);
        MagicUtil.putEffect(world,user,user,this,1200);
        if(!user.getAbilities().creativeMode){
            handStack.decrement(1);
        }
        return TypedActionResult.fail(handStack);
    }

    @Override
    public void magicEffect(ItemStack staffStack, World world, LivingEntity user, Object aim,float ticks){
        ((PlayerEntity)user).sendMessage(Text.literal(String.valueOf(ticks)),true);
    }


    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.masi.trial_card.tooltip"));
    }
}
