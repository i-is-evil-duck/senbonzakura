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
 * Sinful Bishop — post-Wendigo Warlord boss, 700 HP (350 hearts).
 * Iron Golem placeholder.
 */
public class SinfulBishopEntity extends ChaoticBossEntity {
    public SinfulBishopEntity(EntityType<? extends IronGolem> type, Level level) {
        super(type, level, "Sinful Bishop", BossEvent.BossBarColor.PURPLE, 700.0F, 0.25D, 8.0D);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createBossAttributes(700.0F, 0.25D, 8.0D);
    }

    @Override
    protected void onCustomAiTick() {
        if (this.getCustomAiTick() % 90 == 0) {
            Player target = this.findNearestPlayer(12.0);
            if (target != null && this.level() instanceof ServerLevel serverLevel) {
                this.shootArrow(serverLevel, target);
            }
        }
    }
}
