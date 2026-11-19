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
 * Sinful Squire — post-Wendigo Warlord boss, 700 HP (350 hearts).
 * Iron Golem placeholder.
 */
public class SinfulSquireEntity extends ChaoticBossEntity {
    public SinfulSquireEntity(EntityType<? extends IronGolem> type, Level level) {
        super(type, level, "Sinful Squire", BossEvent.BossBarColor.RED, 700.0F, 0.3D, 9.0D);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createBossAttributes(700.0F, 0.3D, 9.0D);
    }

    @Override
    protected void onCustomAiTick() {
        if (this.getCustomAiTick() % 100 == 0) {
            Player target = this.findNearestPlayer(10.0);
            if (target != null && this.level() instanceof ServerLevel serverLevel) {
                this.areaDamage(serverLevel, 4.0, 6.0F);
            }
        }
    }
}
