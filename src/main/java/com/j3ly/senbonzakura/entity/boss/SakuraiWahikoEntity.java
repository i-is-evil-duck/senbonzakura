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
 * Sakurai Wahiko, Senbonzakura — Cherry Grove phase-2 variant of Wahiko,
 * 2500 HP (1250 hearts). Separate summon in Cherry Grove biome.
 * Iron Golem placeholder.
 */
public class SakuraiWahikoEntity extends ChaoticBossEntity {
    public SakuraiWahikoEntity(EntityType<? extends IronGolem> type, Level level) {
        super(type, level, "Sakurai Wahiko, Senbonzakura", BossEvent.BossBarColor.PURPLE, 2500.0F, 0.45D, 12.0D);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createBossAttributes(2500.0F, 0.45D, 12.0D);
    }

    @Override
    protected void onCustomAiTick() {
        if (this.getCustomAiTick() % 60 == 0) {
            Player target = this.findNearestPlayer(14.0);
            if (target != null && this.level() instanceof ServerLevel serverLevel) {
                this.leapToTarget(target, 1.5);
                if (this.distanceToSqr(target) < 5.0) {
                    this.areaDamage(serverLevel, 5.0, 10.0F);
                }
            }
        }
    }
}
