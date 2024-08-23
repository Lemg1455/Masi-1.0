package com.lemg.masi.item;

import com.lemg.masi.network.ModMessage;
import com.lemg.masi.screen.MagicPanelScreen;
import com.lemg.masi.util.MagicUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class EnergyBottle extends Item {
    public EnergyBottle(Settings settings) {
        super(settings);
    }

    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if(user instanceof PlayerEntity player){
            player.incrementStat(Stats.USED.getOrCreateStat(this));
        }
    }

    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected){
        if(entity instanceof PlayerEntity player){
            if(player.getItemUseTime()>0 && player.getItemUseTime() % 30==0){
                if(player.getStackInHand(player.getActiveHand()).getItem()==this){
                    if(MagicUtil.ENERGY.get(player)!=null){
                        //补充魔力
                        if(world.isClient()){
                            int energy = MagicUtil.ENERGY.get(player)+20;
                            if(energy>MagicUtil.MAX_ENERGY.get(player)){
                                energy=MagicUtil.MAX_ENERGY.get(player);
                            }

                            MagicUtil.ENERGY.put(player,energy);
                            PacketByteBuf buf = PacketByteBufs.create();
                            buf.writeInt(0);
                            buf.writeInt(energy);
                            ClientPlayNetworking.send(ModMessage.ENERGY_UPDATE_ID, buf);
                        }
                        if(!player.getAbilities().creativeMode){
                            stack.decrement(1);
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
        tooltip.add(Text.translatable("item.masi.energy_bottle.tooltip"));
    }

}
