package com.lemg.masi.item;

import com.lemg.masi.Masi;
import com.lemg.masi.MasiClient;
import com.lemg.masi.event.KeyInputHandler;
import com.lemg.masi.item.Magics.Magic;
import com.lemg.masi.network.ModMessage;
import com.lemg.masi.util.MagicUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 *法杖
 */
public class Staff extends Item {

    //public ItemStack magic = null;//要咏唱的魔法
    public static ConcurrentHashMap<LivingEntity,Item> UsersMagic = new ConcurrentHashMap<>();
    public Staff(Settings settings) {
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

        if(UsersMagic.get(user) == null){
            return;
        }

        if(UsersMagic.get(user) instanceof Magic magic1){
            if(((double) (singingTick) >= magic1.singFinishTick()) || trial){

                //如果魔法可以响应多重释放的附魔
                if(magic1.Multiple()){
                    int j = EnchantmentHelper.getLevel(Masi.MULTIPLE_RELEASE, stack);
                    List<Object> list = Arrays.asList(magic1, j * 10);
                    MagicUtil.TIME_REQUIRED.put(player,list);
                }
                //如果魔法释放后持续一段时间，与多重释放冲突
                else if(magic1.releaseContinueTime()>0){
                    List<Object> list = Arrays.asList(magic1, magic1.releaseContinueTime());
                    MagicUtil.TIME_REQUIRED.put(player,list);
                }else {
                    magic1.release(stack, world, user, magic1.singFinishTick());//释放的效果
                }

                //消耗魔力
                if(!player.getAbilities().creativeMode && !trial){
                    if(!world.isClient()){
                        //节约魔力附魔，最高减少三分之一消耗
                        int e = EnchantmentHelper.getLevel(Masi.ENERGY_CONSERVATION, stack);
                        int energyConsume = magic1.energyConsume();
                        if(e>0){
                            energyConsume = (int)(energyConsume - (float)(energyConsume*e*(0.11)));
                        }

                        int energy = MagicUtil.ENERGY.get(player) - energyConsume;

                        MagicUtil.energyUpdate(player,energy,false);

                    }
                }
            }
        }

        stack.damage(1, player, p -> p.sendToolBreakStatus(player.getActiveHand()));
        player.incrementStat(Stats.USED.getOrCreateStat(this));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack handStack = user.getStackInHand(hand);
        //如果上一个魔法已经释放完毕
        if(MagicUtil.TIME_REQUIRED.get(user)!=null && (int)MagicUtil.TIME_REQUIRED.get(user).get(1)>=0){
            return TypedActionResult.fail(handStack);
        }
        //当前选择的魔法，如果不为空，且魔力足够
        ItemStack itemStack = MagicUtil.MagicNow(user);
        if(itemStack!=null){
            Item magic = itemStack.getItem();
            if (magic!=null) {
                UsersMagic.put(user,magic);
                if(magic instanceof Magic magic1){
                    if(!magic1.passive()){
                        if((MagicUtil.ENERGY.get(user)>=magic1.energyConsume()) || MagicUtil.isTrial(user) || user.getAbilities().creativeMode){
                            user.setCurrentHand(hand);
                            return TypedActionResult.consume(handStack);
                        }
                    }
                }
            }
        }

        return TypedActionResult.fail(handStack);
    }

    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected){
        if(entity instanceof PlayerEntity player){
            if(player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof Staff){
                if(world.isClient()){

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





    //对法杖的描述
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.masi.staff.tooltip"));
    }
}
