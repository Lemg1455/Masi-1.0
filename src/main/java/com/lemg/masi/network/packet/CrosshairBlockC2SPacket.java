package com.lemg.masi.network.packet;


import com.lemg.masi.item.Magics.BlockPutMagic;
import com.lemg.masi.item.Magics.CreatingWaterMagic;
import com.lemg.masi.item.Magics.HealMagic;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.UUID;

public class CrosshairBlockC2SPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player,
                               ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender){
        ItemStack itemStack = buf.readItemStack();
        BlockHitResult blockHit = buf.readBlockHitResult();
        if(itemStack.getItem() instanceof CreatingWaterMagic creatingWaterMagic){
            BlockPos blockPos3;
            BlockPos blockPos = blockHit.getBlockPos();
            Direction direction = blockHit.getSide();
            BlockPos blockPos2 = blockPos.offset(direction);
            if (!player.getWorld().canPlayerModifyAt(player, blockPos) || !(player).canPlaceOn(blockPos2, direction, new ItemStack(Blocks.WATER))) {
                return;
            }
            creatingWaterMagic.placeFluid(player, player.getWorld(), blockPos, blockHit);
        } else if (itemStack.getItem() instanceof BlockPutMagic) {
            BlockPos blockPos = blockHit.getBlockPos();
            Direction direction = blockHit.getSide();
            BlockPos blockPos2 = blockPos.offset(direction);

            ItemStack itemStack1 = player.getStackInHand(Hand.OFF_HAND);
            Block block;
            if(itemStack1.getItem() instanceof BlockItem blockItem){
                block = blockItem.getBlock();
            }else {
                return;
            }
            if (!player.getWorld().canPlayerModifyAt(player, blockPos) || !player.canPlaceOn(blockPos2, direction, itemStack1)) {
                return;
            }
            player.getWorld().setBlockState(blockHit.getBlockPos().offset(blockHit.getSide()), block.getDefaultState());
            if(!player.getAbilities().creativeMode){
                itemStack1.setCount(itemStack1.getCount()-1);
            }
        }
    }
}
