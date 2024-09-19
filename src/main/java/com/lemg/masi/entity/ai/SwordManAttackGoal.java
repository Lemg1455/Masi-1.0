package com.lemg.masi.entity.ai;


import com.lemg.masi.entity.entities.minions.ArcaneMinionEntity;
import com.lemg.masi.entity.entities.minions.SwordManEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.Hand;

public class SwordManAttackGoal extends MeleeAttackGoal {
    private final SwordManEntity entity;
    private int attackDelay = 40;
    private int tickUntilNextAttack = 20;
    private boolean shouldCountTillNextAttack = false;
    public SwordManAttackGoal(PathAwareEntity mob, double speed, boolean pauseWhenMobIdle) {
        super(mob, speed, pauseWhenMobIdle);
        this.entity = (SwordManEntity)mob;
    }
    @Override
    public boolean canStart() {
        return super.canStart();
    }
    @Override
    public void start() {
        super.start();

        attackDelay = 40;
        tickUntilNextAttack = 20;
    }

    @Override
    public void stop() {
        super.stop();
        entity.setAttacking(false);
    }

    @Override
    public void tick() {
        super.tick();
        if (shouldCountTillNextAttack){
            this.tickUntilNextAttack = Math.max(this.tickUntilNextAttack - 1 ,0);
        }
    }

    @Override
    protected void attack(LivingEntity target, double squaredDistance) {
        if (isEnemyWithinAttackDistanse(target, squaredDistance)) {
            shouldCountTillNextAttack = true;

            if (isTimeToStartAttackAnimation()){
                entity.setAttacking(true);
            }
            if (isTimeToAttack()){
                this.mob.getLookControl().lookAt(target.getX(),target.getY(),target.getZ());
                preformAttack(target);
            }
        } else {
            resetAttackCoolDown();
            shouldCountTillNextAttack = false;
            entity.setAttacking(false);
            entity.clientAttackTimeOut = 0;
        }
    }

    private void resetAttackCoolDown() {
        this.tickUntilNextAttack = this.getTickCount(attackDelay * 1);
    }

    private void preformAttack(LivingEntity target) {
        this.resetAttackCoolDown();
        this.mob.swingHand(Hand.MAIN_HAND);
        this.mob.tryAttack(target);
    }

    private boolean isTimeToAttack() {
        return this.tickUntilNextAttack <= 0;
    }

    private boolean isTimeToStartAttackAnimation() {
        return this.tickUntilNextAttack <= attackDelay;
    }

    private boolean isEnemyWithinAttackDistanse(LivingEntity target, double squaredDistance) {
        return squaredDistance <= this.getSquaredMaxAttackDistance(target);
    }
    protected double getSquaredMaxAttackDistance(LivingEntity entity) {
        return this.mob.getWidth() * 10.0f * (this.mob.getWidth() * 10.0f) + entity.getWidth();
    }
}