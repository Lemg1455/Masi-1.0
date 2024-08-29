package com.lemg.masi.entity.ai;


import com.lemg.masi.entity.ArcaneMinionEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.TrackTargetGoal;

import java.util.EnumSet;


public class TrackMinionOwnerAttackerGoal
        extends TrackTargetGoal {
    private final ArcaneMinionEntity arcaneMinionEntity;
    private LivingEntity attacker;
    private int lastAttackedTime;

    public TrackMinionOwnerAttackerGoal(ArcaneMinionEntity arcaneMinionEntity) {
        super(arcaneMinionEntity, false);
        this.arcaneMinionEntity = arcaneMinionEntity;
        this.setControls(EnumSet.of(Control.TARGET));
    }

    @Override
    public boolean canStart() {
        LivingEntity livingEntity = this.arcaneMinionEntity.getOwner();
        //System.out.println(abstractDionEnity.getOwnerUuid());
        if (livingEntity == null) {
            return false;
        }
        this.attacker = livingEntity.getAttacker();
        int i = livingEntity.getLastAttackedTime();
        return i != this.lastAttackedTime && this.canTrack(this.attacker, TargetPredicate.DEFAULT) && this.arcaneMinionEntity.canAttackWithOwner(this.attacker, livingEntity);
    }

    @Override
    public void start() {
        this.mob.setTarget(this.attacker);
        LivingEntity livingEntity = arcaneMinionEntity.getOwner();
        if (livingEntity != null) {
            this.lastAttackedTime = livingEntity.getLastAttackedTime();
        }
        super.start();
    }
}