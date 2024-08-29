package com.lemg.masi.entity.ai;

import com.lemg.masi.entity.ArcaneMinionEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

import java.util.EnumSet;


public class FollowMinionOwnerGoal
        extends Goal {
    public static final int TELEPORT_DISTANCE = 12;
    private static final int HORIZONTAL_RANGE = 2;
    private static final int HORIZONTAL_VARIATION = 3;
    private static final int VERTICAL_VARIATION = 1;
    private final ArcaneMinionEntity arcaneMinionEntity;
    private LivingEntity owner;
    private final WorldView world;
    private final double speed;
    private final EntityNavigation navigation;
    private int updateCountdownTicks;
    private final float maxDistance;
    private final float minDistance;
    private float oldWaterPathfindingPenalty;
    private final boolean leavesAllowed;

    public FollowMinionOwnerGoal(ArcaneMinionEntity arcaneMinionEntity, double speed, float minDistance, float maxDistance, boolean leavesAllowed) {
        this.arcaneMinionEntity = arcaneMinionEntity;
        this.world = arcaneMinionEntity.getWorld();
        this.speed = speed;
        this.navigation = arcaneMinionEntity.getNavigation();
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.leavesAllowed = leavesAllowed;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        if (!(arcaneMinionEntity.getNavigation() instanceof MobNavigation) && !(arcaneMinionEntity.getNavigation() instanceof BirdNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
        }
    }

    @Override
    public boolean canStart() {
        LivingEntity livingEntity = this.arcaneMinionEntity.getOwner();
        if (livingEntity == null) {
            return false;
        }
        if (livingEntity.isSpectator()) {
            return false;
        }
        if (this.cannotFollow()) {
            return false;
        }
        if (this.arcaneMinionEntity.squaredDistanceTo(livingEntity) < (double)(this.minDistance * this.minDistance)) {
            return false;
        }
        this.owner = livingEntity;
        return true;
    }

    @Override
    public boolean shouldContinue() {
        if (this.navigation.isIdle()) {
            return false;
        }
        if (this.cannotFollow()) {
            return false;
        }
        return !(this.arcaneMinionEntity.squaredDistanceTo(this.owner) <= (double)(this.maxDistance * this.maxDistance));
    }

    private boolean cannotFollow() {
        return this.arcaneMinionEntity.hasVehicle();
    }

    @Override
    public void start() {
        this.updateCountdownTicks = 0;
        this.oldWaterPathfindingPenalty = this.arcaneMinionEntity.getPathfindingPenalty(PathNodeType.WATER);
        this.arcaneMinionEntity.setPathfindingPenalty(PathNodeType.WATER, 0.0f);
    }

    @Override
    public void stop() {
        this.owner = null;
        this.navigation.stop();
        this.arcaneMinionEntity.setPathfindingPenalty(PathNodeType.WATER, this.oldWaterPathfindingPenalty);
    }

    @Override
    public void tick() {
        this.arcaneMinionEntity.getLookControl().lookAt(this.owner, 10.0f, this.arcaneMinionEntity.getMaxLookPitchChange());
        if (--this.updateCountdownTicks > 0) {
            return;
        }
        this.updateCountdownTicks = this.getTickCount(10);
        if (this.arcaneMinionEntity.squaredDistanceTo(this.owner) >= 144.0) {
            this.tryTeleport();
        } else {
            this.navigation.startMovingTo(this.owner, this.speed);
        }
    }

    private void tryTeleport() {
        BlockPos blockPos = this.owner.getBlockPos();
        for (int i = 0; i < 10; ++i) {
            int j = this.getRandomInt(-3, 3);
            int k = this.getRandomInt(-1, 1);
            int l = this.getRandomInt(-3, 3);
            boolean bl = this.tryTeleportTo(blockPos.getX() + j, blockPos.getY() + k, blockPos.getZ() + l);
            if (!bl) continue;
            return;
        }
    }

    private boolean tryTeleportTo(int x, int y, int z) {
        if (Math.abs((double)x - this.owner.getX()) < 2.0 && Math.abs((double)z - this.owner.getZ()) < 2.0) {
            return false;
        }
        if (!this.canTeleportTo(new BlockPos(x, y, z))) {
            return false;
        }
        this.arcaneMinionEntity.refreshPositionAndAngles((double)x + 0.5, y, (double)z + 0.5, this.arcaneMinionEntity.getYaw(), this.arcaneMinionEntity.getPitch());
        this.navigation.stop();
        return true;
    }

    private boolean canTeleportTo(BlockPos pos) {
        PathNodeType pathNodeType = LandPathNodeMaker.getLandNodeType(this.world, pos.mutableCopy());
        if (pathNodeType != PathNodeType.WALKABLE) {
            return false;
        }
        BlockState blockState = this.world.getBlockState(pos.down());
        if (!this.leavesAllowed && blockState.getBlock() instanceof LeavesBlock) {
            return false;
        }
        BlockPos blockPos = pos.subtract(this.arcaneMinionEntity.getBlockPos());
        return this.world.isSpaceEmpty(this.arcaneMinionEntity, this.arcaneMinionEntity.getBoundingBox().offset(blockPos));
    }

    private int getRandomInt(int min, int max) {
        return this.arcaneMinionEntity.getRandom().nextInt(max - min + 1) + min;
    }
}