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
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 *法杖
 */
public class Staff extends Item {

    public ItemStack magic = null;//要咏唱的魔法
    public float singingTick = 0;//咏唱时间
    public int release_continue_time = 0;//魔法在释放后持续的时间，例如释放后持续一段时间造成伤害
    private PlayerEntity playerEntity;
    public Staff(Settings settings) {
        super(settings);
    }


    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        singingTick = this.getMaxUseTime(stack) - remainingUseTicks;//咏唱时间，tick
        if (!(user instanceof PlayerEntity player)) {
            return;//如果不是玩家实例，就返回
        }

        if ((double) (singingTick/20) < 0.1) {
            return;//如果咏唱时间不足0.1秒,就不算开始
        }

        if(magic == null){
            return;
        }

        if(magic.getItem() instanceof Magic magic1){
            if((double) (singingTick) >= magic1.singFinishTick()){

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
                if(!player.getAbilities().creativeMode){
                    if(world.isClient()){
                        //节约魔力附魔，最高减少三分之一消耗
                        int e = EnchantmentHelper.getLevel(Masi.ENERGY_CONSERVATION, stack);
                        int energyConsume = magic1.energyConsume();
                        if(e>0){
                            energyConsume = (int)(energyConsume - (float)(energyConsume*e*(0.11)));
                        }

                        int energy = MagicUtil.ENERGY.get(player) - energyConsume;

                        MagicUtil.ENERGY.put(player,energy);
                        PacketByteBuf buf = PacketByteBufs.create();
                        buf.writeInt(0);
                        buf.writeInt(energy);
                        ClientPlayNetworking.send(ModMessage.ENERGY_UPDATE_ID, buf);
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
        magic = MagicNow(user);
        if (magic!=null) {
            if(magic.getItem() instanceof Magic magic1){
                if(MagicUtil.ENERGY.get(user)>=magic1.energyConsume()){
                    user.setCurrentHand(hand);
                    playerEntity = user;
                    return TypedActionResult.consume(handStack);
                }
            }
        }

        return TypedActionResult.fail(handStack);
    }

    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected){
        if(entity instanceof PlayerEntity player && player == playerEntity && magic!=null){
            if(player.isUsingItem() && player.getStackInHand(player.getActiveHand()).getItem() instanceof Staff) {
                if(magic.getItem() instanceof Magic magic1){
                    magic1.onSinging(stack,world,(LivingEntity)entity, singingTick);//咏唱开始之后，释放之前期间的效果
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

    //获取当前选择的魔法
    public static ItemStack MagicNow(PlayerEntity player){
        List<ItemStack> inventory = MagicUtil.EQUIP_MAGICS.get(player);
        if(inventory==null){
            return null;
        }
        List<ItemStack> magics = new ArrayList<>();

        for(ItemStack itemStack : inventory){
            if(!itemStack.isEmpty()){
                magics.add(itemStack);
            }
        }

        if(!magics.isEmpty()){
            return magics.get(MagicUtil.MAGIC_CHOOSE.get(player));
        }
        return null;
    }

    //对法杖的描述
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.masi.staff.tooltip"));
    }
}
