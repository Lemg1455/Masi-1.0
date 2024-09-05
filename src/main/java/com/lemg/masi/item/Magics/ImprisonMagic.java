package com.lemg.masi.item.Magics;

import com.lemg.masi.Masi;
import com.lemg.masi.network.ModMessage;
import com.lemg.masi.util.MagicUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.Blocks;
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
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class ImprisonMagic extends Magic{
    public ImprisonMagic(Settings settings) {
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
        return 3;
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
                if(player.getWorld().isClient()){
                    MinecraftClient client = MinecraftClient.getInstance();
                    HitResult hit = client.crosshairTarget;
                    if (Objects.requireNonNull(hit.getType()) == HitResult.Type.ENTITY) {
                        EntityHitResult entityHit = (EntityHitResult) hit;
                        Entity entity = entityHit.getEntity();
                        if(entity instanceof LivingEntity livingEntity){
                            livingEntity.setVelocity(0,0,0);
                            player.setVelocity(0,0,0);
                        }

                        PacketByteBuf buf = PacketByteBufs.create();
                        buf.writeUuid(entity.getUuid());
                        buf.writeUuid(player.getUuid());
                        buf.writeItemStack(this.getDefaultStack());
                        ClientPlayNetworking.send(ModMessage.CROSSHAIR_ENTITY_ID, buf);
                    }
                }
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
        tooltip.add(Text.translatable("item.masi.imprison_magic.tooltip"));
        super.appendTooltip(stack,world,tooltip,context);
    }
}
