package com.lemg.masi.item.Magics;

import com.lemg.masi.Masi;
import com.lemg.masi.network.ModMessage;
import com.lemg.masi.util.MagicUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class BlockPutMagic extends Magic{
    public BlockPutMagic(Settings settings) {
        super(settings);
    }
    @Override
    public int singFinishTick(){
        return 15;
    }

    @Override
    public int energyConsume(){
        return 20;
    }
    @Override
    public int studyNeed(){
        return 5;
    }

    @Override
    public void release(ItemStack stack, World world, LivingEntity user, float singingTicks){
        MagicUtil.putEffect(world,user,user,this,200);
        super.release(stack,world,user,singingTicks);
    }
    @Override
    public void onSinging(ItemStack stack, World world, LivingEntity user, float singingTicks){
        if(!user.getWorld().isClient()){
            ((ServerWorld)user.getWorld()).spawnParticles(Masi.CIRCLE_GROUND_YELLOW, user.getX(),user.getY(),user.getZ(), 0, 0, 0.0, 0, 0.0);

            if(user.getItemUseTime() >= singFinishTick()){
                double yawRadians = Math.toRadians(user.getYaw()+90);
                double x = user.getX() + Math.cos(yawRadians) * 1;
                double z = user.getZ() + Math.sin(yawRadians) * 1;
                double y = user.getY()+2;
                ((ServerWorld)user.getWorld()).spawnParticles(Masi.CIRCLE_FORWARD_YELLOW, x,y,z, 0, 0, 0.0, 0, 0.0);

            }
        }
    }

    @Override
    public void magicEffect(ItemStack staffStack, World world, LivingEntity user, Object aim,float ticks){
        if(ticks%2==0){
            if(aim instanceof PlayerEntity player){
                if(player.isAlive()){
                    MinecraftClient client = MinecraftClient.getInstance();
                    HitResult hit = client.crosshairTarget;
                    if(Objects.requireNonNull(hit.getType()) == HitResult.Type.BLOCK){
                        if(world.isClient()){
                            BlockHitResult blockHit = (BlockHitResult) hit;
                            BlockPos blockPos3;
                            BlockPos blockPos = blockHit.getBlockPos();
                            Direction direction = blockHit.getSide();
                            BlockPos blockPos2 = blockPos.offset(direction);

                            ItemStack itemStack = player.getStackInHand(Hand.OFF_HAND);
                            Block block;
                            if(itemStack.getItem() instanceof BlockItem blockItem){
                                block = blockItem.getBlock();
                            }else {
                                return;
                            }
                            if (!world.canPlayerModifyAt((PlayerEntity) user, blockPos) || !((PlayerEntity)user).canPlaceOn(blockPos2, direction, itemStack)) {
                                return;
                            }
                            world.setBlockState(blockHit.getBlockPos().offset(blockHit.getSide()), block.getDefaultState());
                            if(!player.getAbilities().creativeMode){
                                itemStack.setCount(itemStack.getCount()-1);
                            }
                            PacketByteBuf buf = PacketByteBufs.create();
                            buf.writeItemStack(this.getDefaultStack());
                            buf.writeBlockHitResult(blockHit);
                            ClientPlayNetworking.send(ModMessage.CROSSHAIR_BLOCK_ID, buf);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.masi.block_put_magic.tooltip"));
        super.appendTooltip(stack,world,tooltip,context);
    }
}
