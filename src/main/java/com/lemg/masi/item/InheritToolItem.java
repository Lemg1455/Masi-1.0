package com.lemg.masi.item;

import com.google.common.collect.ImmutableMap;
import com.lemg.masi.util.MagicUtil;
import net.fabricmc.yarn.constants.MiningLevels;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PillarBlock;
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
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.BlockTags;
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
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;


public class InheritToolItem extends Item {
    public InheritToolItem(Settings settings) {
        super(settings);
    }

    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        boolean trial = MagicUtil.isTrial((PlayerEntity) user);
        float singingTick = 0;//咏唱时间
        singingTick = this.getMaxUseTime(stack) - remainingUseTicks;//咏唱时间，tick
        if (!(user instanceof PlayerEntity player)) {
            return;//如果不是玩家实例，就返回
        }

        if (((double) (singingTick/20) < 0.1) && !trial) {
            return;//如果咏唱时间不足0.1秒,就不算开始
        }

        stack.damage(1, player, p -> p.sendToolBreakStatus(player.getActiveHand()));
        player.incrementStat(Stats.USED.getOrCreateStat(this));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack handStack = user.getStackInHand(hand);
        user.setCurrentHand(hand);
        return TypedActionResult.consume(handStack);



        //return TypedActionResult.fail(handStack);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.damage(1, attacker, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));

        stack.addAttributeModifier(EntityAttributes.GENERIC_ATTACK_SPEED,new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", (double)-2.4f, EntityAttributeModifier.Operation.ADDITION),EquipmentSlot.MAINHAND);
        if(attacker instanceof PlayerEntity player){
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
            boolean bl = state.isIn(BlockTags.AXE_MINEABLE) || state.isIn(BlockTags.PICKAXE_MINEABLE) || state.isIn(BlockTags.HOE_MINEABLE) || state.isIn(BlockTags.SHOVEL_MINEABLE);
            return bl ? stack.getNbt().getFloat("miningSpeed") : 1.0f;
        }
        return 1.0f;
    }

    /*@Override
    public boolean isSuitableFor(BlockState state) {

        int i = this.getMaterial().getMiningLevel();
        if (i < MiningLevels.DIAMOND && state.isIn(BlockTags.NEEDS_DIAMOND_TOOL)) {
            return false;
        }
        if (i < MiningLevels.IRON && state.isIn(BlockTags.NEEDS_IRON_TOOL)) {
            return false;
        }
        if (i < MiningLevels.STONE && state.isIn(BlockTags.NEEDS_STONE_TOOL)) {
            return false;
        }
        return state.isIn(this.effectiveBlocks);
    }*/

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        if (state.getHardness(world, pos) != 0.0f) {
            stack.damage(2, miner, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        }
        /*if(!world.isClient()){
            if(miner instanceof PlayerEntity player){
                boolean bl = world.getBlockState(pos).isAir();
                boolean bl2 = player.canHarvest(state);
                if (bl && !bl2 && stack.getNbt()!=null) {
                    boolean drop = true;
                    int i = (int) stack.getNbt().getFloat("attackDamage");
                    if (i < MiningLevels.DIAMOND && state.isIn(BlockTags.NEEDS_DIAMOND_TOOL)) {
                        drop = false;
                    }
                    if (i < MiningLevels.IRON && state.isIn(BlockTags.NEEDS_IRON_TOOL)) {
                        drop = false;
                    }
                    if (i < MiningLevels.STONE && state.isIn(BlockTags.NEEDS_STONE_TOOL)) {
                        drop = false;
                    }
                    if(drop){
                        state.getBlock().afterBreak(world, player, pos, state, world.getBlockEntity(pos), stack);
                    }
                }
            }
        }*/
        return true;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        if(new ItemStack(Items.IRON_AXE).getItem().useOnBlock(context)==ActionResult.success(world.isClient)){
            return ActionResult.success(world.isClient);
        }
        if(new ItemStack(Items.IRON_HOE).getItem().useOnBlock(context)==ActionResult.success(world.isClient)){
            return ActionResult.success(world.isClient);
        }
        if(new ItemStack(Items.IRON_SHOVEL).getItem().useOnBlock(context)==ActionResult.success(world.isClient)){
            return ActionResult.success(world.isClient);
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
            }
        }
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    protected static final Map<Block, Block> EFFECT_BLOCKS = new ImmutableMap.Builder<Block, Block>().put(Blocks.OAK_WOOD, Blocks.STRIPPED_OAK_WOOD).put(Blocks.OAK_LOG, Blocks.STRIPPED_OAK_LOG).put(Blocks.DARK_OAK_WOOD, Blocks.STRIPPED_DARK_OAK_WOOD).put(Blocks.DARK_OAK_LOG, Blocks.STRIPPED_DARK_OAK_LOG).put(Blocks.ACACIA_WOOD, Blocks.STRIPPED_ACACIA_WOOD).put(Blocks.ACACIA_LOG, Blocks.STRIPPED_ACACIA_LOG).put(Blocks.CHERRY_WOOD, Blocks.STRIPPED_CHERRY_WOOD).put(Blocks.CHERRY_LOG, Blocks.STRIPPED_CHERRY_LOG).put(Blocks.BIRCH_WOOD, Blocks.STRIPPED_BIRCH_WOOD).put(Blocks.BIRCH_LOG, Blocks.STRIPPED_BIRCH_LOG).put(Blocks.JUNGLE_WOOD, Blocks.STRIPPED_JUNGLE_WOOD).put(Blocks.JUNGLE_LOG, Blocks.STRIPPED_JUNGLE_LOG).put(Blocks.SPRUCE_WOOD, Blocks.STRIPPED_SPRUCE_WOOD).put(Blocks.SPRUCE_LOG, Blocks.STRIPPED_SPRUCE_LOG).put(Blocks.WARPED_STEM, Blocks.STRIPPED_WARPED_STEM).put(Blocks.WARPED_HYPHAE, Blocks.STRIPPED_WARPED_HYPHAE).put(Blocks.CRIMSON_STEM, Blocks.STRIPPED_CRIMSON_STEM).put(Blocks.CRIMSON_HYPHAE, Blocks.STRIPPED_CRIMSON_HYPHAE).put(Blocks.MANGROVE_WOOD, Blocks.STRIPPED_MANGROVE_WOOD).put(Blocks.MANGROVE_LOG, Blocks.STRIPPED_MANGROVE_LOG).put(Blocks.BAMBOO_BLOCK, Blocks.STRIPPED_BAMBOO_BLOCK).build();

    private Optional<BlockState> getEffectState(BlockState state) {
        return Optional.ofNullable(EFFECT_BLOCKS.get(state.getBlock())).map(block -> (BlockState)block.getDefaultState().with(PillarBlock.AXIS, state.get(PillarBlock.AXIS)));
    }


    //对法杖的描述
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.masi.magic_tool_item.tooltip"));
    }
}
