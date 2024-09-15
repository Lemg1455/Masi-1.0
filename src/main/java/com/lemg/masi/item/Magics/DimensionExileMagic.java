package com.lemg.masi.item.Magics;

import com.lemg.masi.Masi;
import com.lemg.masi.network.ModMessage;
import com.lemg.masi.util.MagicUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockLocating;
import net.minecraft.world.Heightmap;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.NetherPortal;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class DimensionExileMagic extends Magic{

    public DimensionExileMagic(Settings settings,int singFinishTick,int energyConsume,int studyNeed) {
        super(settings,singFinishTick,energyConsume,studyNeed);
    }

    @Override
    public void release(ItemStack stack, World world, LivingEntity user, float singingTicks){
        MinecraftClient client = MinecraftClient.getInstance();
        HitResult hit = client.crosshairTarget;
        if (Objects.requireNonNull(hit.getType()) == HitResult.Type.ENTITY) {
            EntityHitResult entityHit = (EntityHitResult) hit;
            Entity entity = entityHit.getEntity();
            if(entity instanceof LivingEntity livingEntity){
                if(livingEntity.isAlive()){
                    if(world.isClient()){
                        PacketByteBuf buf = PacketByteBufs.create();
                        buf.writeUuid(livingEntity.getUuid());
                        buf.writeUuid(user.getUuid());
                        buf.writeItemStack(this.getDefaultStack());
                        ClientPlayNetworking.send(ModMessage.CROSSHAIR_ENTITY_ID, buf);
                    }
                }
            }
        }
        super.release(stack,world,user,singingTicks);
    }
    @Override
    public void onSinging(ItemStack stack, World world, LivingEntity user, float singingTicks){
        if(!user.getWorld().isClient()){
            ((ServerWorld)user.getWorld()).spawnParticles(Masi.LARGE_CIRCLE_GROUND_BLACK, user.getX(),user.getY(),user.getZ(), 0, 0, 0.0, 0, 0.0);

            if(user.getItemUseTime() >= singFinishTick()){
                double yawRadians = Math.toRadians(user.getYaw()+90);
                double x = user.getX() + Math.cos(yawRadians) * 1;
                double z = user.getZ() + Math.sin(yawRadians) * 1;
                double y = user.getY()+2;
                ((ServerWorld)user.getWorld()).spawnParticles(Masi.CIRCLE_FORWARD_BLACK, x,y,z, 0, 0, 0.0, 0, 0.0);

            }
        }
    }

    @Nullable
    public Entity moveToWorld(ServerWorld destination,Entity entity) {
        if (!(entity.getWorld() instanceof ServerWorld) || entity.isRemoved()) {
            return null;
        }
        entity.getWorld().getProfiler().push("changeDimension");
        entity.detach();
        entity.getWorld().getProfiler().push("reposition");
        TeleportTarget teleportTarget = this.getTeleportTarget(destination,entity);
        if (teleportTarget == null) {
            return null;
        }
        entity.getWorld().getProfiler().swap("reloading");
        Entity entity2 = entity.getType().create(destination);
        if (entity2 != null) {
            entity2.copyFrom(entity);
            entity2.refreshPositionAndAngles(teleportTarget.position.x, teleportTarget.position.y, teleportTarget.position.z, teleportTarget.yaw, ((Entity)entity).getPitch());
            entity2.setVelocity(teleportTarget.velocity);
            destination.onDimensionChanged(entity2);
            if (destination.getRegistryKey() == World.END) {
                ServerWorld.createEndSpawnPlatform(destination);
            }
        }
        entity.setRemoved(Entity.RemovalReason.CHANGED_DIMENSION);
        entity.getWorld().getProfiler().pop();
        ((ServerWorld)entity.getWorld()).resetIdleTimeout();
        destination.resetIdleTimeout();
        entity.getWorld().getProfiler().pop();
        return entity2;
    }

    @Nullable
    protected TeleportTarget getTeleportTarget(ServerWorld destination,Entity entity) {
        BlockPos blockPos = entity.getBlockPos();
        //return new TeleportTarget(new Vec3d((double)blockPos.getX(), blockPos.getY(), (double)blockPos.getZ()), entity.getVelocity(), entity.getYaw(), entity.getPitch());
        return new TeleportTarget(new Vec3d((double)0, 0, (double)0), entity.getVelocity(), entity.getYaw(), entity.getPitch());

    }


    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.masi.dimension_exile_magic.tooltip"));
        super.appendTooltip(stack,world,tooltip,context);
    }
}
