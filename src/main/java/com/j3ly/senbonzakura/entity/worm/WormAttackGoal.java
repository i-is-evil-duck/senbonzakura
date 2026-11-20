package com.j3ly.senbonzakura.entity.worm;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

/** Chase the target; when close, trigger the rear-up strike. */
public class WormAttackGoal extends Goal {
    private final WormEntity worm;

    public WormAttackGoal(WormEntity worm) {
        this.worm = worm;
        setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return this.worm.getTarget() != null && this.worm.getPhase() == 0;
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    @Override
    public void tick() {
        LivingEntity target = this.worm.getTarget();
        if (target == null) return;
        this.worm.getLookControl().setLookAt(target, 30.0F, 30.0F);
        double flatDist = Math.hypot(target.getX() - this.worm.getX(), target.getZ() - this.worm.getZ());
        double heightDiff = target.getY() - this.worm.getY();
        // Only strike when roughly level with the target. If it ran up a hill, keep
        // chasing (including upward) instead of stopping at the base of the slope.
        if (flatDist > this.worm.variant().attackRange() || Math.abs(heightDiff) > 1.5) {
            ((WormMoveControl) this.worm.getMoveControl())
                    .setWantedPosition(target.getX(), target.getY() + 0.5, target.getZ(), 1.0);
        } else {
            ((WormMoveControl) this.worm.getMoveControl()).stop();
            this.worm.startAttack(target);
        }
    }
}
