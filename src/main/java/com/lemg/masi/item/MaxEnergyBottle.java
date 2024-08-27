package com.lemg.masi.item;

import com.lemg.masi.network.ModMessage;
import com.lemg.masi.util.MagicUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MaxEnergyBottle extends Item {
    public MaxEnergyBottle(Settings settings) {
        super(settings);
    }

    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if(user instanceof PlayerEntity player){
            player.incrementStat(Stats.USED.getOrCreateStat(this));
        }
    }

    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected){
        if(entity instanceof PlayerEntity player){
            if(player.getStackInHand(player.getActiveHand()).getItem()==this){
                if(player.getItemUseTime()>0 && player.getItemUseTime() % 30==0){
                    if(MagicUtil.MAX_ENERGY.get(player)!=null){
                        //增加魔力上限
                        if(world.isClient()){
                            int energy = MagicUtil.MAX_ENERGY.get(player)+10;
                            MagicUtil.MAX_ENERGY.put(player,energy);
                            PacketByteBuf buf = PacketByteBufs.create();
                            buf.writeInt(1);
                            buf.writeUuid(player.getUuid());
                            buf.writeInt(energy);
                            ClientPlayNetworking.send(ModMessage.ENERGY_UPDATE_ID, buf);
                            if(!player.getAbilities().creativeMode){
                                stack.decrement(1);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        boolean bl;
        ItemStack handStack = player.getStackInHand(hand);
        player.setCurrentHand(hand);

        return TypedActionResult.consume(handStack);
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 30;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

    //对法杖的描述
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.masi.max_energy_bottle.tooltip"));
    }

}
