package com.lemg.masi.item.Magics;

import com.lemg.masi.Masi;
import com.lemg.masi.entity.entities.MagicBulletEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
/**
 *所有魔法的父类
 */
public abstract class Magic extends Item {
    public int singFinishTick=60;
    public int energyConsume=60;
    public int studyNeed=1;

    public Magic(Settings settings,int singFinishTick,int energyConsume,int studyNeed) {
        super(settings);
        this.singFinishTick=singFinishTick;
        this.energyConsume=energyConsume;
        this.studyNeed=studyNeed;
    }

    //魔法的释放效果
    public void release(ItemStack staffStack, World world, LivingEntity user, float singingTicks){

    }

    //魔法咏唱中，释放前的效果
    public void onSinging(ItemStack staffStack, World world, LivingEntity user, float singingTicks){
        if(!user.getWorld().isClient()){
            //法阵的粒子效果
            ((ServerWorld)user.getWorld()).spawnParticles(Masi.CIRCLE_GROUND_BLUE, user.getX(),user.getY(),user.getZ(), 0, 0, 0.0, 0, 0.0);

            if(user.getItemUseTime() >= singFinishTick()){
                double yawRadians = Math.toRadians(user.getYaw()+90);
                double x = user.getX() + Math.cos(yawRadians) * 1;
                double z = user.getZ() + Math.sin(yawRadians) * 1;
                double y = user.getY()+2;
                ((ServerWorld)user.getWorld()).spawnParticles(Masi.CIRCLE_FORWARD_BLUE, x,y,z, 0, 0, 0.0, 0, 0.0);
            }
        }
    }

    //如果魔法的效果不是立即生效的，或者应该生效一段时间的，应在咏唱或释放中赋予目标时长或者倒计时
    public void magicEffect(ItemStack staffStack, World world, LivingEntity user, Object aim,float ticks){}

    //当它会释放投掷物，比如对砸中的目标产生效果，这里是碰撞后要产生的效果
    public void BulletEffect(HitResult hitResult, LivingEntity livingEntity, MagicBulletEntity magicBullet){

    }
    //需要咏唱的时间
    public int singFinishTick(){
        return singFinishTick;
    }

    //消耗的魔力
    public int energyConsume(){
        return energyConsume;
    }

    //学习该魔法需要的经验等级
    public int studyNeed(){
        return studyNeed;
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
        if(this.Multiple()){
            tooltip.add(Text.translatable("item.masi.magic.nbtstring4"));
        }
    }
}
