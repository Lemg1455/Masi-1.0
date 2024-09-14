package com.lemg.masi.item;

import com.google.common.collect.ImmutableMap;
import com.lemg.masi.util.MagicUtil;
import net.fabricmc.yarn.constants.MiningLevels;
import net.minecraft.block.*;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;


public class InheritToolItem extends Item {
    public InheritToolItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack handStack = user.getStackInHand(hand);
        ItemStack offhandStack = user.getStackInHand(Hand.OFF_HAND);
        if(offhandStack.isOf(Items.SHIELD)){
            return TypedActionResult.pass(handStack);
        }

        if(handStack.getNbt()!=null){
            handStack.getNbt().putFloat("tool",0.60f);

            if (user.fishHook != null) {
                if (!world.isClient) {
                    int i = user.fishHook.use(handStack);
                    handStack.damage(i*3, user, p -> p.sendToolBreakStatus(hand));
                }
                world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_FISHING_BOBBER_RETRIEVE, SoundCategory.NEUTRAL, 1.0f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));
                user.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH);
            } else {
                world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_FISHING_BOBBER_THROW, SoundCategory.NEUTRAL, 0.5f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));
                if (!world.isClient) {
                    int i = EnchantmentHelper.getLure(handStack);
                    int j = EnchantmentHelper.getLuckOfTheSea(handStack);
                    world.spawnEntity(new FishingBobberEntity(user, world, j, i));
                }
                user.incrementStat(Stats.USED.getOrCreateStat(this));
                user.emitGameEvent(GameEvent.ITEM_INTERACT_START);
            }
            return TypedActionResult.success(handStack, world.isClient());

        }
        return TypedActionResult.success(handStack, world.isClient());
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.damage(getDamage(stack), attacker, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));

        stack.addAttributeModifier(EntityAttributes.GENERIC_ATTACK_SPEED,new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", (double)-2.4f, EntityAttributeModifier.Operation.ADDITION),EquipmentSlot.MAINHAND);
        if(attacker instanceof PlayerEntity player){
            if(stack.getNbt()!=null){
                float material = getMaterial(stack);
                if(player.fallDistance > 0.0f && !player.isOnGround() && !player.isClimbing() && !player.isTouchingWater()){
                    stack.getNbt().putFloat("tool",material + 0.02f);
                }else {
                    stack.getNbt().putFloat("tool",material + 0.01f);
                }
            }

            float f = (float)player.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
            float g = target instanceof LivingEntity ? EnchantmentHelper.getAttackDamage(player.getMainHandStack(), ((LivingEntity)target).getGroup()) : EnchantmentHelper.getAttackDamage(player.getMainHandStack(), EntityGroup.DEFAULT);
            float h = player.getAttackCooldownProgress(0.5f);
            if ((f *= 0.2f + h * h * 0.8f) > 0.0f || g > 0.0f) {
                ItemStack itemStack;
                boolean bl = h > 0.038;

                boolean bl2 = false;
                if (player.isSprinting() && bl) {
                    bl2 = true;
                }
                boolean bl3 = bl && player.fallDistance > 0.0f && !player.isOnGround() && !player.isClimbing() && !player.isTouchingWater() && !player.hasStatusEffect(StatusEffects.BLINDNESS) && !player.hasVehicle() && target instanceof LivingEntity;
                boolean bl4 = bl3 = bl3 && !player.isSprinting();
                boolean bl42 = false;
                double d = player.horizontalSpeed - player.prevHorizontalSpeed;
                if (bl && !bl3 && !bl2 && player.isOnGround() && d < (double)player.getMovementSpeed() && (itemStack = player.getStackInHand(Hand.MAIN_HAND)).getItem() instanceof InheritToolItem) {
                    bl42 = true;
                }
                if (bl42) {
                    float l = 1.0f + EnchantmentHelper.getSweepingMultiplier(player) * f;
                    List<LivingEntity> list = player.getWorld().getNonSpectatingEntities(LivingEntity.class, target.getBoundingBox().expand(1.0, 0.25, 1.0));
                    for (LivingEntity livingEntity : list) {
                        if (livingEntity == player || livingEntity == target || player.isTeammate(livingEntity) || livingEntity instanceof ArmorStandEntity && ((ArmorStandEntity)livingEntity).isMarker() || !(player.squaredDistanceTo(livingEntity) < 9.0)) continue;
                        livingEntity.takeKnockback(0.4f, MathHelper.sin(player.getYaw() * ((float)Math.PI / 180)), -MathHelper.cos(player.getYaw() * ((float)Math.PI / 180)));
                        livingEntity.damage(player.getDamageSources().playerAttack(player), l);
                    }
                    player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, player.getSoundCategory(), 1.0f, 1.0f);
                    player.spawnSweepAttackParticles();
                }
            }
        }
        return true;
    }

    @Override
    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
        if(stack.getNbt()!=null){
            if (state.isOf(Blocks.COBWEB)) {
                return 15.0f;
            }
            boolean bl = state.isIn(BlockTags.AXE_MINEABLE);
            boolean bl2 = state.isIn(BlockTags.PICKAXE_MINEABLE);
            boolean bl3 = state.isIn(BlockTags.HOE_MINEABLE);
            boolean bl4 = state.isIn(BlockTags.SHOVEL_MINEABLE);

            float material = getMaterial(stack);
            if(bl){
                stack.getNbt().putFloat("tool",material + 0.02f);
            }else if ((bl2)){
                stack.getNbt().putFloat("tool",material + 0.03f);
            }else if ((bl3)){
                stack.getNbt().putFloat("tool",material + 0.04f);
            }else if ((bl4)){
                stack.getNbt().putFloat("tool",material + 0.05f);
            }

            return bl||bl2||bl3||bl4 ? stack.getNbt().getFloat("miningSpeed") : 1.0f;
        }
        return 1.0f;
    }


    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        if (state.getHardness(world, pos) != 0.0f) {
            stack.damage(getDamage(stack)*2, miner, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        }
        return true;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        ItemStack stack = context.getStack();
        PlayerEntity player = context.getPlayer();
        if(stack.getNbt()!=null){
            float material = getMaterial(stack);
            if(new ItemStack(Items.IRON_AXE).getItem().useOnBlock(context)==ActionResult.success(world.isClient)){
                stack.getNbt().putFloat("tool",material+0.02f);
                if (player != null) {
                    stack.damage(getDamage(stack)-1, player, p -> p.sendToolBreakStatus(context.getHand()));
                }
                return ActionResult.success(world.isClient);
            }
            if(new ItemStack(Items.IRON_HOE).getItem().useOnBlock(context)==ActionResult.success(world.isClient)){
                stack.getNbt().putFloat("tool",material+0.04f);
                if (player != null) {
                    stack.damage(getDamage(stack)-1, player, p -> p.sendToolBreakStatus(context.getHand()));
                }
                return ActionResult.success(world.isClient);
            }
            if(new ItemStack(Items.IRON_SHOVEL).getItem().useOnBlock(context)==ActionResult.success(world.isClient)){
                stack.getNbt().putFloat("tool",material+0.05f);
                if (player != null) {
                    stack.damage(getDamage(stack)-1, player, p -> p.sendToolBreakStatus(context.getHand()));
                }
                return ActionResult.success(world.isClient);
            }
        }
        return ActionResult.PASS;
    }
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected){
        if(entity instanceof PlayerEntity player){
            if(player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof InheritToolItem){
                if(stack.getNbt()!=null){
                    NbtCompound nbt = stack.getNbt();
                    float axeAttackDamage = 6.0f;
                    if(nbt.getFloat("attackDamage")==1.0f){
                        axeAttackDamage = 7.0f;
                    }else if(nbt.getFloat("attackDamage")>=3.0f){
                        axeAttackDamage = 5.0f;
                    }

                    if(player.fallDistance > 0.0f && !player.isOnGround() && !player.isClimbing() && !player.isTouchingWater() && !player.hasStatusEffect(StatusEffects.BLINDNESS) && !player.hasVehicle()){
                        stack.addAttributeModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Tool modifier", (double)axeAttackDamage + nbt.getFloat("attackDamage"), EntityAttributeModifier.Operation.ADDITION),EquipmentSlot.MAINHAND);
                    }else {

                        if(stack.getAttributeModifiers(EquipmentSlot.MAINHAND).containsKey(EntityAttributes.GENERIC_ATTACK_DAMAGE)){
                            if(stack.getAttributeModifiers(EquipmentSlot.MAINHAND).get(EntityAttributes.GENERIC_ATTACK_DAMAGE).stream().toList().get(0).getValue()==(double)3.0f + nbt.getFloat("attackDamage")) {
                                return;
                            }

                        }
                        stack.addAttributeModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Tool modifier", (double)3.0f + nbt.getFloat("attackDamage"), EntityAttributeModifier.Operation.ADDITION),EquipmentSlot.MAINHAND);
                    }
                }
            }else {
                if(!(player.getStackInHand(Hand.OFF_HAND).getItem() instanceof InheritToolItem)){
                    if(stack.getNbt()!=null){
                        stack.getNbt().putFloat("tool",0.0f);
                    }
                }
            }
        }
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.NONE;
    }

    public float getMaterial(ItemStack stack){
        if(stack.getNbt()!=null){
            float material = stack.getNbt().getFloat("miningSpeed");
            if(material==2.0f){
                material = 0.0f;
            }else if(material==4.0f){
                material = 0.1f;
            }else if(material==6.0f){
                material = 0.2f;
            }else if(material==12.0f){
                material = 0.3f;
            }else if(material==8.0f){
                material = 0.4f;
            }else if(material==9.0f){
                material = 0.5f;
            }
            return material;
        }
        return 0.0f;
    }

    public int getDamage(ItemStack stack){
        if(stack.getNbt()!=null){
            float material = stack.getNbt().getFloat("miningSpeed");
            int damage = 0;
            if(material==2.0f){
                damage = 100;
            }else if(material==4.0f){
                damage = 50;
            }else if(material==6.0f){
                damage = 25;
            }else if(material==12.0f){
                damage = 150;
            }else if(material==8.0f){
                damage = 4;
            }else if(material==9.0f){
                damage = 3;
            }
            return damage;
        }
        return 0;
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return false;
    }


    //对法杖的描述
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.masi.inherit_tool_item.tooltip"));
    }
}
