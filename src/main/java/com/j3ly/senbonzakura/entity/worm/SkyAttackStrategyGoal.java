package com.j3ly.senbonzakura.entity.worm;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

/**
 * Vanilla {@code PhantomAttackStrategyGoal}: only decides when to stop circling and start a
 * swoop. It never writes {@code moveTargetPoint} - the circle/swoop goals do that.
 */
public class SkyAttackStrategyGoal extends Goal {
    private final WormEntity worm;
    private final SkyFlightState flight;
    private int nextSwoopTick;

    public SkyAttackStrategyGoal(WormEntity worm, SkyFlightState flight) {
        this.worm = worm;
        this.flight = flight;
    }

    @Override
    public boolean canUse() {
        return this.worm.getTarget() != null;
    }

    @Override
    public void start() {
        this.nextSwoopTick = this.adjustedTickDelay(10);
        this.flight.setPhase(SkyFlightState.CIRCLE);
        this.flight.setAnchorAboveTarget();
    }

    @Override
    public void tick() {
        if (!this.flight.isCircling()) return;
        LivingEntity target = this.worm.getTarget();
        if (target == null || --this.nextSwoopTick > 0) return;

        if (this.flight.canDiveAt(target)) {
            this.flight.setPhase(SkyFlightState.SWOOP);
            this.flight.setAnchorAboveTarget();
            this.nextSwoopTick = this.adjustedTickDelay((8 + this.worm.getRandom().nextInt(4)) * 20);
            this.worm.playSound(SoundEvents.PHANTOM_SWOOP, 6.0F,
                    0.95F + this.worm.getRandom().nextFloat() * 0.1F);
        } else {
            // Wrong medium: stay in the circle and try again shortly.
            this.nextSwoopTick = this.adjustedTickDelay(60);
        }
    }

    @Override
    public void stop() {
        this.flight.resetAnchorAfterTarget();
    }
}
