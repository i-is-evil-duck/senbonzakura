package com.j3ly.senbonzakura.entity.worm;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

/**
 * Vanilla {@code PhantomCircleAroundAnchorGoal}: keeps {@code moveTargetPoint} walking around the
 * flight anchor. It also runs when there is no target, so the serpent glides instead of stopping
 * dead and then jumping when a target appears.
 */
public class SkyCircleAroundAnchorGoal extends Goal {
    private final WormEntity worm;
    private final SkyFlightState flight;

    public SkyCircleAroundAnchorGoal(WormEntity worm, SkyFlightState flight) {
        this.worm = worm;
        this.flight = flight;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return this.worm.getTarget() == null || this.flight.isCircling();
    }

    @Override
    public void start() {
        RandomSource random = this.worm.getRandom();
        this.flight.distance = 5.0F + random.nextFloat() * 10.0F;
        this.flight.height = -4.0F + random.nextFloat() * 9.0F;
        this.flight.clockwise = random.nextBoolean() ? 1.0F : -1.0F;
        this.flight.selectNext();
    }

    @Override
    public void tick() {
        RandomSource random = this.worm.getRandom();
        if (random.nextInt(this.adjustedTickDelay(350)) == 0) {
            this.flight.height = -4.0F + random.nextFloat() * 9.0F;
        }
        if (random.nextInt(this.adjustedTickDelay(250)) == 0) {
            this.flight.distance++;
            if (this.flight.distance > 15.0F) {
                this.flight.distance = 5.0F;
                this.flight.clockwise = -this.flight.clockwise;
            }
        }
        if (random.nextInt(this.adjustedTickDelay(450)) == 0) {
            this.flight.angle = random.nextFloat() * Mth.TWO_PI;
            this.flight.selectNext();
        }
        if (this.touchingTarget()) {
            this.flight.selectNext();
        }

        // Steer away from ceilings/floors like vanilla does.
        if (this.worm.moveTargetPoint.y < this.worm.getY()
                && !this.worm.level().isEmptyBlock(this.worm.blockPosition().below())) {
            this.flight.height = Math.max(1.0F, this.flight.height);
            this.flight.selectNext();
        }
        if (this.worm.moveTargetPoint.y > this.worm.getY()
                && !this.worm.level().isEmptyBlock(this.worm.blockPosition().above())) {
            this.flight.height = Math.min(-1.0F, this.flight.height);
            this.flight.selectNext();
        }
    }

    private boolean touchingTarget() {
        return this.worm.moveTargetPoint.distanceToSqr(this.worm.position()) < 4.0;
    }
}
