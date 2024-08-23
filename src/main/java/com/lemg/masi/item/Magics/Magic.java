package com.lemg.masi.item.Magics;

import com.lemg.masi.network.ModMessage;
import com.lemg.masi.util.MagicUtil;
import it.unimi.dsi.fastutil.io.TextIO;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
/**
 *所有魔法的父类
 */
public abstract class Magic extends Item {

    public Magic(Settings settings) {
        super(settings);
    }

    //魔法的释放效果
    public void release(ItemStack staffStack, World world, LivingEntity user, float singingTicks){

    }

    //魔法咏唱中，释放前的效果
    public void onSinging(ItemStack staffStack, World world, LivingEntity user, float singingTicks){

    }

    //需要咏唱的时间
    public int singFinishTick(){
        return 60;
    }

    //消耗的魔力
    public int energyConsume(){
        return 60;
    }

    //学习该魔法需要的经验等级
    public int studyNeed(){
        return 1;
    }


    //魔法的描述
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal(Text.translatable("item.masi.magic.nbtstring1").getString() + studyNeed()));
        tooltip.add(Text.literal(Text.translatable("item.masi.magic.nbtstring2").getString() + energyConsume()));
        tooltip.add(Text.literal(Text.translatable("item.masi.magic.nbtstring3").getString() + singFinishTick()));

    }
}
