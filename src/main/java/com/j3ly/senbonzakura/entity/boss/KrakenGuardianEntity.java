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
 * Kraken Guardian — post-Warden boss, 520 HP (260 hearts).
 * Iron Golem placeholder.
 */
public class KrakenGuardianEntity extends ChaoticBossEntity {
    public KrakenGuardianEntity(EntityType<? extends IronGolem> type, Level level) {
        super(type, level, "Kraken Guardian", BossEvent.BossBarColor.PURPLE, 520.0F, 0.2D, 9.0D);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createBossAttributes(520.0F, 0.2D, 9.0D);
    }

    @Override
    protected void onCustomAiTick() {
        if (this.getCustomAiTick() % 100 == 0) {
            Player target = this.findNearestPlayer(8.0);
            if (target != null && this.level() instanceof ServerLevel serverLevel) {
                this.placeWebs(serverLevel, target, 8, 5.0);
            }
        }
    }
}
