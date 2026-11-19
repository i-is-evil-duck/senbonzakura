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
 * Lucius, Retainer of Ruin — post-Elemental Heroes boss, 2000 HP (1000 hearts).
 * Iron Golem placeholder.
 */
public class LuciusRetainerOfRuinEntity extends ChaoticBossEntity {
    public LuciusRetainerOfRuinEntity(EntityType<? extends IronGolem> type, Level level) {
        super(type, level, "Lucius, Retainer of Ruin", BossEvent.BossBarColor.RED, 2000.0F, 0.3D, 12.0D);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createBossAttributes(2000.0F, 0.3D, 12.0D);
    }

    @Override
    protected void onCustomAiTick() {
        if (this.getCustomAiTick() % 70 == 0) {
            Player target = this.findNearestPlayer(10.0);
            if (target != null && this.level() instanceof ServerLevel serverLevel) {
                this.areaDamage(serverLevel, 5.0, 9.0F);
            }
        }
    }
}
