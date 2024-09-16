package com.lemg.masi.entity.entities.minions;

import net.minecraft.entity.LivingEntity;

public interface Minion {
    boolean isMinion();
    LivingEntity getOwner();

    boolean canAttackWithOwner(LivingEntity target, LivingEntity owner);
}
