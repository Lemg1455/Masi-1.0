package com.lemg.masi.item;

import com.lemg.masi.Masi;
import com.lemg.masi.event.KeyInputHandler;
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
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MageCertificate extends Item {
    int TimeOut = 20;

    public MageCertificate(Settings settings) {
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
        if(handStack.getNbt()!=null && Objects.equals(handStack.getNbt().getUuid("owner").toString(), player.getUuid().toString())){
            if(world.isClient()){
                //右键打开面板
                MinecraftClient MC = MinecraftClient.getInstance();
                MagicPanelScreen magicPanelScreen = new MagicPanelScreen(player, world.getEnabledFeatures());
                MC.setScreen(magicPanelScreen);
                return TypedActionResult.consume(handStack);
            }
        }
        if(player.getAbilities().creativeMode){

        }
        return TypedActionResult.fail(handStack);

    }

    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected){
        if(entity instanceof PlayerEntity player){
            //如果首次获得，就添加主人
            NbtCompound nbt = stack.getNbt();
            if(nbt==null || (!nbt.contains("owner"))){
                NbtCompound nbt2 = new NbtCompound();
                nbt2.putUuid("owner",player.getUuid());
                nbt2.putInt("max_energy",100);
                nbt2.putInt("energy",100);
                nbt2.putInt("energy_restored",2);
                nbt2.putInt("magic_choose",0);
                stack.setNbt(nbt2);
                stack.setCustomName(Text.literal(stack.getName().getString() + player.getName().getString()));
            }

            //保存和加载数据
            if(nbt==null || !Objects.equals(nbt.getUuid("owner").toString(), player.getUuid().toString())){
                return;
            }

            if(MagicUtil.MAX_ENERGY.get(player)==null){
                MagicUtil.MAX_ENERGY.put(player,nbt.getInt("max_energy"));
                MagicUtil.ENERGY.put(player,nbt.getInt("energy"));
                MagicUtil.ENERGY_RESTORED.put(player,nbt.getInt("energy_restored"));
            }
            if(MagicUtil.MAX_ENERGY.get(player)!=null){
                nbt.putInt("max_energy",MagicUtil.MAX_ENERGY.get(player));
                nbt.putInt("energy",MagicUtil.ENERGY.get(player));
                nbt.putInt("energy_restored",MagicUtil.ENERGY_RESTORED.get(player));

            }

            if(MagicUtil.MAGIC_CHOOSE.get(player)==null){
                MagicUtil.MAGIC_CHOOSE.put(player,nbt.getInt("magic_choose"));
            }

            if(MagicUtil.MAGIC_CHOOSE.get(player)!=null){
                if(MagicUtil.MAGIC_CHOOSE.get(player)!=nbt.getInt("magic_choose")){
                    nbt.putInt("magic_choose",MagicUtil.MAGIC_CHOOSE.get(player));
                }
            }

            //每次进入加载nbt中的数据
            List<ItemStack> Learned_magics = MagicUtil.LEARNED_MAGICS.get(player);
            if(Learned_magics==null && nbt.contains("LearnedMagics")){
                Learned_magics = new ArrayList<>();
                NbtList list = nbt.getList("LearnedMagics", NbtElement.COMPOUND_TYPE);
                for (int i = 0; i < list.size(); ++i) {
                    NbtCompound nbtCompound = list.getCompound(i);
                    Learned_magics.add(ItemStack.fromNbt(nbtCompound));
                }
                if(Learned_magics.size()!=0){
                    MagicUtil.LEARNED_MAGICS.put(player,Learned_magics);
                }
            }
            //将数据保存到nbt
            if(Learned_magics!=null){

                List<ItemStack> Learned_magics_nbt = new ArrayList<>();
                NbtList list = nbt.getList("LearnedMagics", NbtElement.COMPOUND_TYPE);
                for (int i = 0; i < list.size(); ++i) {
                    NbtCompound nbtCompound = list.getCompound(i);
                    Learned_magics_nbt.add(ItemStack.fromNbt(nbtCompound));
                }
                if(Learned_magics.size()!=Learned_magics_nbt.size()){
                    NbtList nbtList = new NbtList();
                    for (int i = 0; i < Learned_magics.size(); i++) {
                        ItemStack itemStack = Learned_magics.get(i);
                        if (itemStack.isEmpty()) continue;
                        NbtCompound nbtCompound = new NbtCompound();
                        itemStack.writeNbt(nbtCompound);
                        nbtList.add(nbtCompound);
                    }
                    nbt.put("LearnedMagics", nbtList);
                    stack.setNbt(nbt);
                }
            }
            List<Item> equip_magics = MagicUtil.EQUIP_MAGICS.get(player);
            if(equip_magics==null && nbt.contains("EquipMagics")){
                equip_magics = new ArrayList<>();
                NbtList list = nbt.getList("EquipMagics", NbtElement.COMPOUND_TYPE);
                for (int i = 0; i < list.size(); ++i) {
                    NbtCompound nbtCompound = list.getCompound(i);
                    equip_magics.add(ItemStack.fromNbt(nbtCompound).getItem());
                }
                if(equip_magics.size()!=0){
                    MagicUtil.EQUIP_MAGICS.put(player,equip_magics);
                }
            }
            if(equip_magics!=null){
                DefaultedList<ItemStack> itemStacks = DefaultedList.of();
                itemStacks.addAll(MagicUtil.getItemsStacks(MagicUtil.EQUIP_MAGICS.get(player)));
                NbtList nbtList = new NbtList();
                for (int i = 0; i < itemStacks.size(); ++i) {
                    ItemStack itemStack = itemStacks.get(i);
                    if (itemStack.isEmpty()) continue;
                    NbtCompound nbtCompound = new NbtCompound();
                    nbtCompound.putByte("Slot", (byte)i);
                    itemStack.writeNbt(nbtCompound);
                    nbtList.add(nbtCompound);
                }
                if (!nbtList.isEmpty()) {
                    nbt.put("EquipMagics", nbtList);
                }
                stack.setNbt(nbt);
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
        tooltip.add(Text.translatable("item.masi.mage_certificate.tooltip"));
    }

}
