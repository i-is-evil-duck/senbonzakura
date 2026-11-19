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
 * Drallius, Visions of Doom — post-Lucius boss, 5000 HP (2500 hearts).
 * Iron Golem placeholder.
 */
public class DralliusVisionsOfDoomEntity extends ChaoticBossEntity {
    public DralliusVisionsOfDoomEntity(EntityType<? extends IronGolem> type, Level level) {
        super(type, level, "Drallius, Visions of Doom", BossEvent.BossBarColor.PURPLE, 5000.0F, 0.25D, 14.0D);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createBossAttributes(5000.0F, 0.25D, 14.0D);
    }

    @Override
    protected void onCustomAiTick() {
        if (this.getCustomAiTick() % 60 == 0) {
            Player target = this.findNearestPlayer(12.0);
            if (target != null && this.level() instanceof ServerLevel serverLevel) {
                this.spawnMinions(serverLevel, 2, 6.0);
                this.areaDamage(serverLevel, 6.0, 10.0F);
            }
        }
    }
}
