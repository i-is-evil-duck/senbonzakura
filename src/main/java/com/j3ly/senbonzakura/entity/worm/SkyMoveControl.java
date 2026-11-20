package com.j3ly.senbonzakura.entity.worm;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.Vec3;

/**
 * Phantom-style flight steering: bank toward the goal's moving point and ease velocity toward
 * it. Mirrors vanilla {@code PhantomMoveControl} so the serpent circles, climbs and dives the
 * same way, while the body segments simply trail behind through {@code WormEntity}'s buffer.
 */
public class SkyMoveControl extends MoveControl {
    private final WormEntity worm;
    private float speed = 0.1F;

    public SkyMoveControl(WormEntity worm) {
        super(worm);
        this.worm = worm;
    }

    @Override
    public void tick() {
        if (this.worm.horizontalCollision) {
            this.worm.setYRot(this.worm.getYRot() + 180.0F);
            this.speed = 0.1F;
        }

        Vec3 target = this.worm.moveTargetPoint;
        double dx = target.x - this.worm.getX();
        double dy = target.y - this.worm.getY();
        double dz = target.z - this.worm.getZ();
        double flat = Math.sqrt(dx * dx + dz * dz);
        if (Math.abs(flat) <= 1.0E-5) return;

        // Flatten steep climbs into wider arcs, like the phantom does.
        double damp = 1.0 - Math.abs(dy * 0.7) / flat;
        dx *= damp;
        dz *= damp;
        flat = Math.sqrt(dx * dx + dz * dz);
        double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (length < 1.0E-5) return;

        float yaw = this.worm.getYRot();
        float desired = (float) Math.atan2(dz, dx) * (180.0F / (float) Math.PI);
        this.worm.setYRot(Mth.approachDegrees(Mth.wrapDegrees(yaw + 90.0F),
                Mth.wrapDegrees(desired), 4.0F) - 90.0F);
        this.worm.yBodyRot = this.worm.getYRot();

        // Speed up once lined up with the target; slow while banked into a turn.
        if (Mth.degreesDifferenceAbs(yaw, this.worm.getYRot()) < 3.0F) {
            this.speed = Mth.approach(this.speed, 1.8F, 0.005F * (1.8F / this.speed));
        } else {
            this.speed = Mth.approach(this.speed, 0.2F, 0.025F);
        }

        float pitch = (float) -(Math.atan2(-dy, flat) * 180.0F / Math.PI);
        this.worm.setXRot(pitch);

        float heading = (this.worm.getYRot() + 90.0F) * ((float) Math.PI / 180.0F);
        double vx = this.speed * Math.cos(heading) * Math.abs(dx / length);
        double vz = this.speed * Math.sin(heading) * Math.abs(dz / length);
        double vy = this.speed * Math.sin(pitch * ((float) Math.PI / 180.0F)) * Math.abs(dy / length);

        Vec3 current = this.worm.getDeltaMovement();
        this.worm.setDeltaMovement(current.add(new Vec3(vx, vy, vz).subtract(current).scale(0.2)));
    }
}
