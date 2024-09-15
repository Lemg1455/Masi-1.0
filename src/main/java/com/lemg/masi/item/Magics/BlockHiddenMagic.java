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
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class BlockHiddenMagic extends Magic{
    public BlockHiddenMagic(Settings settings,int singFinishTick,int energyConsume,int studyNeed) {
        super(settings,singFinishTick,energyConsume,studyNeed);
    }

    @Override
    public void release(ItemStack stack, World world, LivingEntity user, float singingTicks){
        ConcurrentHashMap<BlockPos, BlockState> blocksAndpos = new ConcurrentHashMap<>();
        Object[] bbp = new Object[2];
        Box box = new Box(user.getBlockPos()).expand(5);
        int x = (int) box.minX;
        int y = (int) box.minY;
        int z = (int) box.minZ;
        for(;x<box.maxX;x++){
            for(;z<box.maxZ;z++){
                for (;y<box.maxY;y++){
                    BlockPos blockPos = new BlockPos(x,y,z);
                    BlockState blockState = world.getBlockState(blockPos);
                    blocksAndpos.put(blockPos,blockState);
                    world.setBlockState(blockPos, Blocks.AIR.getDefaultState());
                }
                y = (int) box.minY;
            }
            z = (int) box.minZ;
        }
        bbp[0]=box;
        bbp[1]=blocksAndpos;
        MagicUtil.putEffect(world,bbp,user,this,1200);
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
        if(aim instanceof Object[] bbp && ticks<=1160){
            if(bbp[0] instanceof Box box){
                List<Entity> list = world.getOtherEntities(user,box);
                if(!list.isEmpty()){
                    for(Entity entity : list) {
                        if (entity instanceof LivingEntity) {
                            if(bbp[1] instanceof ConcurrentHashMap<?,?>){
                                ConcurrentHashMap<BlockPos, BlockState> blocksAndpos = (ConcurrentHashMap<BlockPos, BlockState>) bbp[1];
                                for(BlockPos blockPos : blocksAndpos.keySet()){
                                    world.setBlockState(blockPos, blocksAndpos.get(blockPos));
                                }
                                MagicUtil.putEffect(world,aim,user,this,0);
                            }
                        }
                    }
                } else if (ticks<=2) {
                    if(bbp[1] instanceof ConcurrentHashMap<?,?>){
                        ConcurrentHashMap<BlockPos, BlockState> blocksAndpos = (ConcurrentHashMap<BlockPos, BlockState>) bbp[1];
                        for(BlockPos blockPos : blocksAndpos.keySet()){
                            world.setBlockState(blockPos, blocksAndpos.get(blockPos));
                        }
                        MagicUtil.putEffect(world,aim,user,this,0);
                    }
                }
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.masi.block_hidden_magic.tooltip"));
        tooltip.add(Text.translatable("item.masi.block_hidden_magic.tooltip2"));
        super.appendTooltip(stack,world,tooltip,context);
    }
}
