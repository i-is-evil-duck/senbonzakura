package com.j3ly.senbonzakura.entity.worm;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/** Vanilla {@code PhantomSweepAttackGoal}: dive at the target and bite once on contact. */
public class SkySwoopAttackGoal extends Goal {
    private final WormEntity worm;
    private final SkyFlightState flight;

    public SkySwoopAttackGoal(WormEntity worm, SkyFlightState flight) {
        this.worm = worm;
        this.flight = flight;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.worm.getTarget();
        return target != null && this.flight.isSwooping() && this.flight.canDiveAt(target);
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.worm.getTarget();
        return target != null && target.isAlive() && this.flight.isSwooping();
    }

    @Override
    public void start() {
    }

    @Override
    public void tick() {
        LivingEntity target = this.worm.getTarget();
        if (target == null) return;
        if (!this.flight.canDiveAt(target)) {
            endSwoop();
            return;
        }

        this.worm.moveTargetPoint = new Vec3(target.getX(), target.getY(0.5), target.getZ());
        if (this.worm.getBoundingBox().inflate(0.3F).intersects(target.getBoundingBox())) {
            this.worm.doHurtTarget(target);
            this.worm.playSound(this.worm.variant().attackSound(), 1.0F, 1.0F);
            endSwoop();
        } else if (this.worm.horizontalCollision || this.worm.hurtTime > 0) {
            endSwoop();
        }
    }

    @Override
    public void stop() {
        if (this.flight.isSwooping()) endSwoop();
    }

    private void endSwoop() {
        this.flight.setPhase(SkyFlightState.CIRCLE);
        this.flight.setAnchorAboveTarget();
    }
}
