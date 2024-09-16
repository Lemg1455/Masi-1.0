package com.lemg.masi.entity.ai;


import com.lemg.masi.entity.entities.minions.ArcaneMinionEntity;
import com.lemg.masi.entity.entities.minions.Minion;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.mob.MobEntity;

import java.util.EnumSet;


public class TrackMinionOwnerAttackerGoal
        extends TrackTargetGoal {
    private final Minion minionEntity;
    private LivingEntity attacker;
    private int lastAttackedTime;

    public TrackMinionOwnerAttackerGoal(Minion minionEntity) {
        super((MobEntity) minionEntity, false);
        this.minionEntity = minionEntity;
        this.setControls(EnumSet.of(Control.TARGET));
    }

    @Override
    public boolean canStart() {
        LivingEntity livingEntity = this.minionEntity.getOwner();
        //System.out.println(abstractDionEnity.getOwnerUuid());
        if (livingEntity == null) {
            return false;
        }
        this.attacker = livingEntity.getAttacker();
        int i = livingEntity.getLastAttackedTime();
        return i != this.lastAttackedTime && this.canTrack(this.attacker, TargetPredicate.DEFAULT) && this.minionEntity.canAttackWithOwner(this.attacker, livingEntity);
    }

    @Override
    public void start() {
        this.mob.setTarget(this.attacker);
        LivingEntity livingEntity = minionEntity.getOwner();
        if (livingEntity != null) {
            this.lastAttackedTime = livingEntity.getLastAttackedTime();
        }
        super.start();
    }
}