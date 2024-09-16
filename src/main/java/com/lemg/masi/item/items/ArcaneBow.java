package com.lemg.masi.item.items;

import com.lemg.masi.entity.entities.ArcaneArrowEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.item.Vanishable;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class ArcaneBow extends RangedWeaponItem implements Vanishable {
    public ArcaneBow(Settings settings) {
        super(settings);
    }
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity playerEntity)) {
            return;
        }

        boolean bl = playerEntity.getAbilities().creativeMode;
        float time;
        if ((time = getPullProgress(this.getMaxUseTime(stack) - remainingUseTicks)) <= 0.1) {
            return;
        }

        if (!world.isClient) {
            ArcaneArrowEntity arcaneArrowEntity = new ArcaneArrowEntity(world,user, (int) (1+(time-1)*2));
            float yaw = user.getYaw();
            float pitch = user.getPitch();
            float ff = -MathHelper.sin(yaw * ((float)Math.PI / 180)) * MathHelper.cos(pitch * ((float)Math.PI / 180));
            float gg = -MathHelper.sin(pitch * ((float)Math.PI / 180));
            float hh = MathHelper.cos(yaw * ((float)Math.PI / 180)) * MathHelper.cos(pitch * ((float)Math.PI / 180));
            arcaneArrowEntity.setVelocity(ff, gg, hh, 2.5f, 1.0f);
            Vec3d vec3d = user.getVelocity();
            arcaneArrowEntity.setVelocity(arcaneArrowEntity.getVelocity().add(vec3d.x, 0.0, vec3d.z));
            //arcaneArrowEntity.setNoGravity(true);
            if(!world.isClient()){
                //在世界上生成这个实体，即发射箭头
                world.spawnEntity(arcaneArrowEntity);
            }

            stack.damage((int)time, playerEntity, p -> p.sendToolBreakStatus(playerEntity.getActiveHand()));
        }
        playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
    }

    public static float getPullProgress(int useTicks) {
        float f = (float) useTicks / 20.0f;
        if (f > 5.0f) {
            f = 5.0f;
        }else if(f>0.1f && f<1.0f){
            f = 1.0f;
        }
        return f;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        user.setCurrentHand(hand);
        return TypedActionResult.consume(itemStack);
        //return TypedActionResult.fail(itemStack);

    }

    @Override
    public Predicate<ItemStack> getProjectiles() {
        return BOW_PROJECTILES;
    }

    @Override
    public int getRange() {
        return 15;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.masi.arcane_bow.tooltip"));
        tooltip.add(Text.translatable("item.masi.arcane_bow2.tooltip"));
    }
}

