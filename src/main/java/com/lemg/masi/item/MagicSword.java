package com.lemg.masi.item;

import com.lemg.masi.Masi;
import com.lemg.masi.entity.*;
import com.lemg.masi.item.Magics.Magic;
import com.lemg.masi.util.MagicUtil;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
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
        float time = getProgress(this.getMaxUseTime(stack) - remainingUseTicks);
        if(time<0.5){
            return;
        }
        SwordEnergyEntity.size1 = time;
        SwordEnergyEntity swordEnergyEntity = ModEntities.SWORD_ENERGY.create(user.getWorld());
        if (swordEnergyEntity != null) {
            if(user instanceof PlayerEntity player){
                swordEnergyEntity.setOwner(player);
            }
            float pitch = user.getPitch();
            float yaw = user.getYaw();
            float f = -MathHelper.sin(yaw * ((float)Math.PI / 180)) * MathHelper.cos(pitch * ((float)Math.PI / 180));
            float g = -MathHelper.sin(pitch * ((float)Math.PI / 180));
            float h = MathHelper.cos(yaw * ((float)Math.PI / 180)) * MathHelper.cos(pitch * ((float)Math.PI / 180));

            Vec3d pos = new Vec3d(user.getX()+f*2,user.getY()+g*3,user.getZ()+h*2);
            Vec3d vec3d = new Vec3d(f,g,h).normalize().multiply(2.0f);

            swordEnergyEntity.refreshPositionAndAngles(pos.getX(),pos.getY(),pos.getZ(),yaw,pitch);
            swordEnergyEntity.setVelocity(vec3d);
            swordEnergyEntity.setNoGravity(true);
            if(!user.getWorld().isClient()){
                ((ServerWorld)user.getWorld()).spawnEntity(swordEnergyEntity);
            }
        }
        stack.damage((int) time, user, p -> p.sendToolBreakStatus(user.getActiveHand()));

    }

    public static float getProgress(int useTicks) {
        float f = (float) useTicks / 20.0f;
        if (f > 5.0f) {
            f = 5.0f;
        }else if(f>0.5f && f<1.0f){
            f = 1.0f;
        }
        return f;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack handStack = user.getStackInHand(hand);
        user.setCurrentHand(hand);
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
        float l = 5.0f;
        if(MagicUtil.MAX_ENERGY.get(attacker)!=null){
            l = MagicUtil.MAX_ENERGY.get(attacker)/20.0f;
        }
        List<LivingEntity> list = attacker.getWorld().getNonSpectatingEntities(LivingEntity.class, target.getBoundingBox().expand(1.0, 0.25, 1.0));
        for (LivingEntity livingEntity : list) {
            if (livingEntity == attacker || livingEntity == target || attacker.isTeammate(livingEntity) || livingEntity instanceof ArmorStandEntity && ((ArmorStandEntity)livingEntity).isMarker() || !(attacker.squaredDistanceTo(livingEntity) < 9.0)) continue;
            livingEntity.takeKnockback(0.4f, MathHelper.sin(attacker.getYaw() * ((float)Math.PI / 180)), -MathHelper.cos(attacker.getYaw() * ((float)Math.PI / 180)));
            livingEntity.damage(attacker.getDamageSources().magic(), l);
        }
        attacker.getWorld().playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, attacker.getSoundCategory(), 1.0f, 1.0f);
        stack.damage(1, attacker, p -> p.sendToolBreakStatus(attacker.getActiveHand()));
        return true;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 30000;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.CROSSBOW;
    }





    //对法杖的描述
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.masi.magic_sword.tooltip"));
    }
}
