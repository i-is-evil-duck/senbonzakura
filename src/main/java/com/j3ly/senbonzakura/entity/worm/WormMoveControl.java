package com.j3ly.senbonzakura.entity.worm;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.Vec3;

/** Direct steering (no pathfinding). PoC stand-in for the future burrowing controller. */
public class WormMoveControl extends MoveControl {
    private final WormEntity worm;

    public WormMoveControl(WormEntity worm) {
        super(worm);
        this.worm = worm;
    }

    @Override
    public void tick() {
        if (this.operation != Operation.MOVE_TO) {
            this.worm.setDeltaMovement(this.worm.getDeltaMovement().scale(0.9));
            return;
        }
        Vec3 dir = new Vec3(this.wantedX - this.worm.getX(),
                this.wantedY - this.worm.getY(),
                this.wantedZ - this.worm.getZ());
        double dist = dir.length();
        if (dist < 0.3) {
            stop();
            return;
        }
        dir = dir.normalize();
        float speed = (float) (this.speedModifier * this.worm.getAttributeValue(Attributes.MOVEMENT_SPEED));
        Vec3 velocity = dir.scale(speed);

        // Climb assist: with noGravity the worm can rise over steps, but only if we
        // give it explicit upward velocity instead of pushing straight into the wall.
        double heightDiff = this.wantedY - this.worm.getY();
        if (heightDiff > 0.1) {
            velocity = velocity.add(0.0, Math.min(0.35, heightDiff * 0.12), 0.0);
        }
        if (this.worm.horizontalCollision && heightDiff > 0.0) {
            this.worm.getJumpControl().jump();
            velocity = velocity.add(0.0, 0.25, 0.0);
        }
        this.worm.setDeltaMovement(velocity);

        // Minecraft yaw 0 faces +Z, -90 faces +X (same convention as vanilla MoveControl).
        float targetYaw = (float) Math.toDegrees(Math.atan2(-dir.x, dir.z));
        this.worm.setYRot(Mth.rotLerp(0.15F, this.worm.getYRot(), targetYaw));
        this.operation = Operation.WAIT;
    }

    public void stop() {
        this.operation = Operation.WAIT;
    }
}
