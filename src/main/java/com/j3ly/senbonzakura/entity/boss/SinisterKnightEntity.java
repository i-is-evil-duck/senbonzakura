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
 * Sinister Knight — post-Wendigo Warlord boss, 800 HP (400 hearts).
 * Iron Golem placeholder.
 */
public class SinisterKnightEntity extends ChaoticBossEntity {
    public SinisterKnightEntity(EntityType<? extends IronGolem> type, Level level) {
        super(type, level, "Sinister Knight", BossEvent.BossBarColor.RED, 800.0F, 0.35D, 10.0D);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createBossAttributes(800.0F, 0.35D, 10.0D);
    }

    @Override
    protected void onCustomAiTick() {
        if (this.getCustomAiTick() % 70 == 0) {
            Player target = this.findNearestPlayer(8.0);
            if (target != null && this.level() instanceof ServerLevel serverLevel) {
                this.leapToTarget(target, 1.5);
                if (this.distanceToSqr(target) < 4.0) {
                    this.areaDamage(serverLevel, 3.0, 8.0F);
                }
            }
        }
    }
}
