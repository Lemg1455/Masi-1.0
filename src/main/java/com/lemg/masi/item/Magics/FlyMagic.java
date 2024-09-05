package com.lemg.masi.item.Magics;

import com.lemg.masi.Masi;
import com.lemg.masi.network.ModMessage;
import com.lemg.masi.util.MagicUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class FlyMagic extends Magic{
    public FlyMagic(Settings settings) {
        super(settings);
    }
    @Override
    public int singFinishTick(){
        return 200;
    }

    @Override
    public int energyConsume(){
        return 5;
    }
    @Override
    public int studyNeed(){
        return 5;
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
                    ((ServerWorld)user.getWorld()).spawnParticles(Masi.CIRCLE_GROUND_WHITE, user.getX(),user.getY(),user.getZ(), 0, 0, 0.0, 0, 0.0);
                }
                user.updateVelocity(1, new Vec3d(0, 0.1, 0.1));
                if (!player.getAbilities().creativeMode && !MagicUtil.isTrial(player)) {

                    if(!player.getWorld().isClient()){
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

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.masi.fly_magic.tooltip"));
        super.appendTooltip(stack,world,tooltip,context);
    }
}
