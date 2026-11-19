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
 * Drallius, Chaos: UNLEASHED — immediately spawns after defeating Drallius,
 * 5000 HP (2500 hearts). Iron Golem placeholder.
 */
public class DralliusChaosUnleashedEntity extends ChaoticBossEntity {
    public DralliusChaosUnleashedEntity(EntityType<? extends IronGolem> type, Level level) {
        super(type, level, "Drallius, Chaos: UNLEASHED", BossEvent.BossBarColor.PURPLE, 5000.0F, 0.3D, 15.0D);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createBossAttributes(5000.0F, 0.3D, 15.0D);
    }

    @Override
    protected void onCustomAiTick() {
        if (this.getCustomAiTick() % 50 == 0) {
            Player target = this.findNearestPlayer(14.0);
            if (target != null && this.level() instanceof ServerLevel serverLevel) {
                this.spawnMinions(serverLevel, 3, 8.0);
                this.areaDamage(serverLevel, 7.0, 12.0F);
            }
        }
    }
}
