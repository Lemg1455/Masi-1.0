package com.lemg.masi.entity.ai;


import com.lemg.masi.entity.ArcaneMinionEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.TrackTargetGoal;

import java.util.EnumSet;


public class AttackWithMinionOwnerGoal
        extends TrackTargetGoal {
    private final ArcaneMinionEntity arcaneMinionEntity;
    private LivingEntity attacking;
    private int lastAttackTime;

    public AttackWithMinionOwnerGoal(ArcaneMinionEntity arcaneMinionEntity) {
        super(arcaneMinionEntity, false);
        this.arcaneMinionEntity = arcaneMinionEntity;
        this.setControls(EnumSet.of(Control.TARGET));
    }

    @Override
    public boolean canStart() {
        LivingEntity livingEntity = this.arcaneMinionEntity.getOwner();
        if (livingEntity == null) {
            return false;
        }
        this.attacking = livingEntity.getAttacking();
        int i = livingEntity.getLastAttackTime();
        return i != this.lastAttackTime && this.canTrack(this.attacking, TargetPredicate.DEFAULT) && this.arcaneMinionEntity.canAttackWithOwner(this.attacking, livingEntity);
    }

    @Override
    public void start() {
        this.mob.setTarget(this.attacking);
        LivingEntity livingEntity = this.arcaneMinionEntity.getOwner();
        if (livingEntity != null) {
            this.lastAttackTime = livingEntity.getLastAttackTime();
        }
        super.start();
    }
}