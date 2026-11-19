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
 * Frozen Serpent — post-Warden boss, 500 HP (250 hearts).
 * Iron Golem placeholder.
 */
public class FrozenSerpentEntity extends ChaoticBossEntity {
    public FrozenSerpentEntity(EntityType<? extends IronGolem> type, Level level) {
        super(type, level, "Frozen Serpent", BossEvent.BossBarColor.BLUE, 500.0F, 0.25D, 7.0D);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createBossAttributes(500.0F, 0.25D, 7.0D);
    }

    @Override
    protected void onCustomAiTick() {
        if (this.getCustomAiTick() % 110 == 0) {
            Player target = this.findNearestPlayer(10.0);
            if (target != null && this.level() instanceof ServerLevel serverLevel) {
                this.shootArrow(serverLevel, target);
            }
        }
    }
}
