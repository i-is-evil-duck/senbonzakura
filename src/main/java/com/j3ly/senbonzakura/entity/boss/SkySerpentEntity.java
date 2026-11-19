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
 * Sky Serpent — post-Ender Dragon / Wither boss, 420 HP (210 hearts).
 * Iron Golem placeholder. Summoned with Storm Caller Charm.
 */
public class SkySerpentEntity extends ChaoticBossEntity {
    public SkySerpentEntity(EntityType<? extends IronGolem> type, Level level) {
        super(type, level, "Sky Serpent", BossEvent.BossBarColor.YELLOW, 420.0F, 0.5D, 7.0D);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createBossAttributes(420.0F, 0.5D, 7.0D);
    }

    @Override
    protected void onCustomAiTick() {
        if (this.getCustomAiTick() % 100 == 0 && this.onGround()) {
            Player target = this.findNearestPlayer(16.0);
            if (target != null && this.level() instanceof ServerLevel serverLevel) {
                this.leapToTarget(target, 1.2);
                if (this.distanceToSqr(target) < 4.0) {
                    this.areaDamage(serverLevel, 3.0, 7.0F);
                }
            }
        }
    }
}
