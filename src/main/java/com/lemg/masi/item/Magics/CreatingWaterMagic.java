package com.lemg.masi.item.Magics;

import com.lemg.masi.network.ModMessage;
import com.lemg.masi.util.MagicUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidFillable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class CreatingWaterMagic extends Magic{
    public CreatingWaterMagic(Settings settings) {
        super(settings);
    }
    @Override
    public int singFinishTick(){
        return 10;
    }

    @Override
    public int energyConsume(){
        return 10;
    }
    @Override
    public int studyNeed(){
        return 3;
    }
    @Override
    public boolean Multiple(){
        return true;
    }
    @Override
    public void release(ItemStack stack, World world, LivingEntity user, float singingTicks){
        MinecraftClient client = MinecraftClient.getInstance();
        HitResult hit = client.crosshairTarget;
        if (Objects.requireNonNull(hit.getType()) == HitResult.Type.ENTITY) {
            EntityHitResult entityHit = (EntityHitResult) hit;
            Entity entity = entityHit.getEntity();
            if(entity instanceof LivingEntity livingEntity){
                livingEntity.setAir(0);
            }
            if(world.isClient()){
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeUuid(entity.getUuid());
                buf.writeUuid(user.getUuid());
                buf.writeItemStack(this.getDefaultStack());
                ClientPlayNetworking.send(ModMessage.CROSSHAIR_ENTITY_ID, buf);
                world.setBlockState(entity.getBlockPos().add(0,1,0), Blocks.WATER.getDefaultState());
            }
        }else if(Objects.requireNonNull(hit.getType()) == HitResult.Type.BLOCK){
            if(world.isClient()){
                BlockHitResult blockHit = (BlockHitResult) hit;

                BlockPos blockPos3;
                BlockPos blockPos = blockHit.getBlockPos();
                Direction direction = blockHit.getSide();
                BlockPos blockPos2 = blockPos.offset(direction);

                if (!world.canPlayerModifyAt((PlayerEntity) user, blockPos) || !((PlayerEntity)user).canPlaceOn(blockPos2, direction, new ItemStack(Blocks.WATER))) {
                    return;
                }
                this.placeFluid((PlayerEntity) user, world, blockPos, blockHit);

                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeItemStack(this.getDefaultStack());
                buf.writeBlockHitResult(blockHit);
                ClientPlayNetworking.send(ModMessage.CROSSHAIR_BLOCK_ID, buf);
            }
        }else {
            //world.setBlockState(user.getBlockPos(), Blocks.WATER.getDefaultState());
        }

        super.release(stack,world,user,singingTicks);

    }
    @Override
    public void onSinging(ItemStack stack, World world, LivingEntity user, float singingTicks){
        super.onSinging(stack,world,user,singingTicks);
    }

    public boolean placeFluid(@Nullable PlayerEntity player, World world, BlockPos pos, @Nullable BlockHitResult hitResult) {
        boolean bl2;

        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        boolean bl3 = bl2 = blockState.isAir() || block instanceof FluidFillable && ((FluidFillable)((Object)block)).canFillWithFluid(world, pos, blockState, Fluids.WATER);
        if (!bl2) {
            return hitResult != null && this.placeFluid(player, world, hitResult.getBlockPos().offset(hitResult.getSide()), null);
        }
        if (world.getDimension().ultrawarm() && Fluids.WATER.isIn(FluidTags.WATER)) {
            int i = pos.getX();
            int j = pos.getY();
            int k = pos.getZ();
            world.playSound(player, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5f, 2.6f + (world.random.nextFloat() - world.random.nextFloat()) * 0.8f);
            for (int l = 0; l < 8; ++l) {
                world.addParticle(ParticleTypes.LARGE_SMOKE, (double)i + Math.random(), (double)j + Math.random(), (double)k + Math.random(), 0.0, 0.0, 0.0);
            }
            return true;
        }
        if (block instanceof FluidFillable) {
            ((FluidFillable)((Object)block)).tryFillWithFluid(world, pos, blockState, ((FlowableFluid)Fluids.WATER).getStill(false));
            this.playEmptyingSound(player, world, pos);
            return true;
        }
        if (!world.isClient && !blockState.isLiquid()) {
            world.breakBlock(pos, true);
        }
        if (world.setBlockState(pos, Fluids.WATER.getDefaultState().getBlockState(), Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD) || blockState.getFluidState().isStill()) {
            this.playEmptyingSound(player, world, pos);
            return true;
        }
        return false;
    }

    protected void playEmptyingSound(@Nullable PlayerEntity player, WorldAccess world, BlockPos pos) {
        SoundEvent soundEvent = Fluids.WATER.isIn(FluidTags.LAVA) ? SoundEvents.ITEM_BUCKET_EMPTY_LAVA : SoundEvents.ITEM_BUCKET_EMPTY;
        world.playSound(player, pos, soundEvent, SoundCategory.BLOCKS, 1.0f, 1.0f);
        world.emitGameEvent((Entity)player, GameEvent.FLUID_PLACE, pos);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.masi.creating_water_magic.tooltip"));
        super.appendTooltip(stack,world,tooltip,context);
    }
}
