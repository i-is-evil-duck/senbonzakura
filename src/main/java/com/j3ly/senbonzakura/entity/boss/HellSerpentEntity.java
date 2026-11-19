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
 * Hell Serpent — post-Ender Dragon / Wither boss, 480 HP (240 hearts).
 * Nether-restricted. Iron Golem placeholder.
 */
public class HellSerpentEntity extends ChaoticBossEntity {
    public HellSerpentEntity(EntityType<? extends IronGolem> type, Level level) {
        super(type, level, "Hell Serpent", BossEvent.BossBarColor.RED, 480.0F, 0.3D, 8.0D);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createBossAttributes(480.0F, 0.3D, 8.0D);
    }

    @Override
    protected void onCustomAiTick() {
        if (this.getCustomAiTick() % 90 == 0) {
            Player target = this.findNearestPlayer(10.0);
            if (target != null && this.level() instanceof ServerLevel serverLevel) {
                this.areaDamage(serverLevel, 5.0, 5.0F);
            }
        }
    }
}
