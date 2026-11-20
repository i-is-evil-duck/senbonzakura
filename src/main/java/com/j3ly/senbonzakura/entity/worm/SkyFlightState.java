package com.j3ly.senbonzakura.entity.worm;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

/**
 * Shared flight state for the sky/ocean serpents. Vanilla Phantom keeps its anchor, attack phase
 * and circle parameters on the entity; we keep them here so the three goals can be constructed
 * during {@code Mob}'s constructor (before subclass fields exist).
 */
final class SkyFlightState {
    static final int CIRCLE = 0;
    static final int SWOOP = 1;

    private final WormEntity worm;
    private final RandomSource random;

    Vec3 anchor = Vec3.ZERO;
    int phase = CIRCLE;
    float angle;
    float distance = 8.0F;
    float height = 0.0F;
    float clockwise = 1.0F;

    SkyFlightState(WormEntity worm) {
        this.worm = worm;
        this.random = worm.getRandom();
    }

    boolean isCircling() {
        return this.phase == CIRCLE;
    }

    boolean isSwooping() {
        return this.phase == SWOOP;
    }

    void setPhase(int phase) {
        this.phase = phase;
    }

    /** Target-dependent anchor: above the sea for the sky serpent, below it for the ocean one. */
    void setAnchorAboveTarget() {
        LivingEntity target = this.worm.getTarget();
        if (target == null) return;
        int seaLevel = this.worm.level().getSeaLevel();
        if (this.worm.variant() == WormVariant.OCEAN) {
            this.anchor = target.position().add(0.0, -6 - this.random.nextInt(6), 0.0);
            if (this.anchor.y > seaLevel - 2) {
                this.anchor = new Vec3(this.anchor.x, seaLevel - 2, this.anchor.z);
            }
        } else {
            this.anchor = target.position().add(0.0, 20 + this.random.nextInt(20), 0.0);
            if (this.anchor.y < seaLevel + 4) {
                this.anchor = new Vec3(this.anchor.x, seaLevel + 4, this.anchor.z);
            }
        }
    }

    /** Vanilla-style reset when the target is lost: park the anchor high above nearby terrain. */
    void resetAnchorAfterTarget() {
        if (Vec3.ZERO.equals(this.anchor)) this.anchor = this.worm.position();
        BlockPos block = BlockPos.containing(this.anchor);
        int seaLevel = this.worm.level().getSeaLevel();
        if (this.worm.variant() == WormVariant.OCEAN) {
            this.anchor = new Vec3(this.anchor.x, Math.min(seaLevel - 3, this.anchor.y), this.anchor.z);
        } else {
            int ground = this.worm.level().getHeight(Heightmap.Types.MOTION_BLOCKING, block.getX(), block.getZ());
            this.anchor = new Vec3(block.getX() + 0.5,
                    Math.max(seaLevel + 4, ground + 10 + this.random.nextInt(20)),
                    block.getZ() + 0.5);
        }
    }

    /** A swoop is only allowed in the serpent's own medium. */
    boolean canDiveAt(LivingEntity target) {
        if (target == null || !target.isAlive()) return false;
        if (this.worm.variant() == WormVariant.OCEAN) {
            return target.isInWater();
        }
        return !target.isInWater() && target.getY() > this.worm.level().getSeaLevel() + 1;
    }

    /** Advance one point around the circle and keep it inside the correct medium. */
    void selectNext() {
        if (Vec3.ZERO.equals(this.anchor)) this.anchor = this.worm.position();
        this.angle += this.clockwise * 15.0F * ((float) Math.PI / 180.0F);
        int seaLevel = this.worm.level().getSeaLevel();
        double y = this.anchor.y + this.height;
        if (this.worm.variant() == WormVariant.OCEAN) {
            y = Math.min(y, seaLevel - 1);
        } else {
            y = Math.max(y, seaLevel + 2);
        }
        this.worm.moveTargetPoint = new Vec3(
                this.anchor.x + this.distance * Math.cos(this.angle),
                y,
                this.anchor.z + this.distance * Math.sin(this.angle));
    }
}
