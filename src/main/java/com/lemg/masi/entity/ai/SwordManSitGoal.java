package com.lemg.masi.entity.ai;

import com.lemg.masi.entity.entities.minions.Minion;
import com.lemg.masi.entity.entities.minions.SwordManEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;

import java.util.EnumSet;


public class SwordManSitGoal
        extends Goal {
    private final SwordManEntity swordMan;

    public SwordManSitGoal(SwordManEntity swordMan) {
        this.swordMan = swordMan;
        this.setControls(EnumSet.of(Control.JUMP, Control.MOVE));
    }

    @Override
    public boolean shouldContinue() {

        return this.swordMan.isSitting();
    }

    @Override
    public boolean canStart() {
        if (this.swordMan.isInsideWaterOrBubbleColumn()) {
            return false;
        }
        if (!this.swordMan.isOnGround()) {
            return false;
        }
        if (this.swordMan.hasPassengers()) {
            return false;
        }
        LivingEntity livingEntity = this.swordMan.getOwner();
        if (livingEntity == null) {
            return true;
        }
        if (this.swordMan.squaredDistanceTo(livingEntity) < 144.0 && livingEntity.getAttacker() != null) {
            return false;
        }
        return this.swordMan.isSitting();
    }

    @Override
    public void start() {
        this.swordMan.getNavigation().stop();
        this.swordMan.setInSittingPose(true);
    }

    @Override
    public void stop() {
        this.swordMan.setInSittingPose(false);
    }
}