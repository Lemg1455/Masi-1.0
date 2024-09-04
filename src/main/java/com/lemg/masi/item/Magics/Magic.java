package com.lemg.masi.item.Magics;

import com.lemg.masi.entity.MagicBulletEntity;
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
import net.minecraft.util.hit.HitResult;
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
        if(!user.getWorld().isClient()){
            MagicUtil.circleGround(0,user,user.getX(),user.getY(),user.getZ());
            if(user.getItemUseTime() >= singFinishTick()){
                double yawRadians = Math.toRadians(user.getYaw()+90);
                double x = user.getX() + Math.cos(yawRadians) * 1;
                double z = user.getZ() + Math.sin(yawRadians) * 1;
                double y = user.getY()+2;
                MagicUtil.circleForward(1,user,x,y,z);
            }
        }
    }

    //如果魔法的效果不是立即生效的，或者应该生效一段时间的，应在咏唱或释放中赋予目标时长/倒计时
    public void magicEffect(ItemStack staffStack, World world, LivingEntity user, Object aim,float ticks){}

    public void BulletEffect(HitResult hitResult, LivingEntity livingEntity, MagicBulletEntity magicBullet){

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

    //是否具有多重释放的附魔效果
    public boolean Multiple(){
        return false;
    }

    //是否是被动的
    public boolean passive(){
        return false;
    }

    //如果魔法在释放后会持续一段时间，此为持续的时间
    public int releaseContinueTime(){return 0;}

        //魔法的描述
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal(Text.translatable("item.masi.magic.nbtstring1").getString() + studyNeed()));
        tooltip.add(Text.literal(Text.translatable("item.masi.magic.nbtstring2").getString() + energyConsume()));
        tooltip.add(Text.literal(Text.translatable("item.masi.magic.nbtstring3").getString() + singFinishTick()));

    }
}
