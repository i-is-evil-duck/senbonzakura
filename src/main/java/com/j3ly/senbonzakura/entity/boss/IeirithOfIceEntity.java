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
 * Ieirith, of Ice — post-Sinister Knight boss, 1000 HP (500 hearts).
 * Iron Golem placeholder.
 */
public class IeirithOfIceEntity extends ChaoticBossEntity {
    public IeirithOfIceEntity(EntityType<? extends IronGolem> type, Level level) {
        super(type, level, "Ieirith, of Ice", BossEvent.BossBarColor.BLUE, 1000.0F, 0.25D, 10.0D);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createBossAttributes(1000.0F, 0.25D, 10.0D);
    }

    @Override
    protected void onCustomAiTick() {
        if (this.getCustomAiTick() % 90 == 0) {
            Player target = this.findNearestPlayer(10.0);
            if (target != null && this.level() instanceof ServerLevel serverLevel) {
                this.shootArrow(serverLevel, target);
            }
        }
    }
}
