package com.lemg.masi.item;

import com.lemg.masi.Masi;
import com.lemg.masi.item.Magics.Magic;
import com.lemg.masi.util.MagicUtil;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
public class MagicSword extends Item {
    public MagicSword(Settings settings) {
        super(settings);
    }


    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {

    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack handStack = user.getStackInHand(hand);

        return TypedActionResult.consume(handStack);
        //return TypedActionResult.fail(handStack);
    }

    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected){
        if(entity instanceof PlayerEntity player){
            if(player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof MagicSword){
                if(world.isClient()){

                }
            }
        }
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        double d = -MathHelper.sin(attacker.getYaw() * ((float)Math.PI / 180));
        double e = MathHelper.cos(attacker.getYaw() * ((float)Math.PI / 180));
        if (attacker.getWorld() instanceof ServerWorld) {
            ((ServerWorld)attacker.getWorld()).spawnParticles(Masi.MAGIC_SWORD_SWEEP, attacker.getX() + d, attacker.getBodyY(0.6), attacker.getZ() + e, 0, d, 0.0, e, 0.0);
        }
        return true;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }





    //对法杖的描述
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.masi.staff.tooltip"));
    }
}
