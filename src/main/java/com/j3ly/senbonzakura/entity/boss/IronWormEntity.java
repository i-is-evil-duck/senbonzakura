package com.j3ly.senbonzakura.entity.boss;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Iron Worm — pre-iron tier boss gate, 12 segments × 40 HP (480 total / 240 hearts).
 * Iron Golem placeholder. Summoned with Corroding Lure.
 */
public class IronWormEntity extends ChaoticBossEntity {
    public IronWormEntity(EntityType<? extends IronGolem> type, Level level) {
        super(type, level, "Iron Worm", BossEvent.BossBarColor.WHITE, 480.0F, 0.2D, 9.0D);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createBossAttributes(480.0F, 0.2D, 9.0D);
    }

    @Override
    protected void onCustomAiTick() {
        if (this.getCustomAiTick() % 100 == 0) {
            Player target = this.findNearestPlayer(10.0);
            if (target != null && this.level() instanceof ServerLevel serverLevel) {
                this.leapToTarget(target, 1.0);
                if (this.distanceToSqr(target) < 9.0) {
                    this.areaDamage(serverLevel, 4.0, 6.0F);
                }
            }
        }
    }
}
