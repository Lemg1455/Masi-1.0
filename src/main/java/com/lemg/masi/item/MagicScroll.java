package com.lemg.masi.item;

import com.lemg.masi.item.Magics.Magic;
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

import java.util.*;

public class MagicScroll extends Item {

    public MagicScroll(Settings settings) {
        super(settings);
    }

    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if(user instanceof PlayerEntity player){
            player.incrementStat(Stats.USED.getOrCreateStat(this));
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        boolean bl;
        ItemStack handStack = player.getStackInHand(hand);
        NbtCompound nbt = handStack.getNbt();
        if(nbt!=null && nbt.contains("magic")){
            int i = nbt.getInt("magic");
            Item magic = MagicUtil.magicStudy999.get(i);

            List<ItemStack> Learned_magics = MagicUtil.LEARNED_MAGICS.get(player);

            Boolean b1;
            if(Learned_magics==null){
                Learned_magics = new ArrayList<>();
                b1 = true;
            }else {
                b1 = !MagicUtil.getStacksItems(Learned_magics).contains(magic);
            }
            if(b1){
                Learned_magics.add(magic.getDefaultStack());
                MagicUtil.LEARNED_MAGICS.put(player,Learned_magics);
                if(world.isClient()){player.sendMessage(Text.translatable("masi.panel.learned.message"));}
                if(!player.getAbilities().creativeMode){
                    handStack.decrement(1);
                }
                return TypedActionResult.consume(handStack);
            }else {
                if(world.isClient()){player.sendMessage(Text.translatable("masi.scroll.fail.message"));}
            }
        }
        return TypedActionResult.fail(handStack);
    }

    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected){
        if(entity instanceof PlayerEntity player){
            //如果首次获得，随机添加魔法
            NbtCompound nbt = stack.getNbt();
            if(nbt==null || (!nbt.contains("magic"))){
                NbtCompound nbt2 = new NbtCompound();
                Random random = new Random();
                int i = random.nextInt(MagicUtil.magicStudy999.size());
                nbt2.putInt("magic",i);
                stack.setNbt(nbt2);
                stack.setCustomName(Text.literal(stack.getName().getString() +"---"+ MagicUtil.magicStudy999.get(i).getDefaultStack().getName().getString()));
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

    //对法杖的描述
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.masi.magic_scroll.tooltip"));
    }
}
