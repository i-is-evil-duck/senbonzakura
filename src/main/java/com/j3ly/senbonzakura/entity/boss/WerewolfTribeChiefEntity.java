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
 * Werewolf Tribe Chief — post-Ender Dragon / Wither boss, 400 HP (200 hearts).
 * Iron Golem placeholder. Summoned with Lunar Howl Totem.
 */
public class WerewolfTribeChiefEntity extends ChaoticBossEntity {
    public WerewolfTribeChiefEntity(EntityType<? extends IronGolem> type, Level level) {
        super(type, level, "Werewolf Tribe Chief", BossEvent.BossBarColor.RED, 400.0F, 0.35D, 8.0D);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createBossAttributes(400.0F, 0.35D, 8.0D);
    }

    @Override
    protected void onCustomAiTick() {
        if (this.getCustomAiTick() % 160 == 0) {
            Player target = this.findNearestPlayer(12.0);
            if (target != null && this.level() instanceof ServerLevel serverLevel) {
                this.howlAndSummon(serverLevel);
            }
        }
    }

    private void howlAndSummon(ServerLevel serverLevel) {
        int count = 2 + this.getRandom().nextInt(2);
        for (int i = 0; i < count; i++) {
            IronGolem minion = new IronGolem(net.minecraft.world.entity.EntityType.IRON_GOLEM, serverLevel);
            double angle = this.getRandom().nextDouble() * 2 * Math.PI;
            double dist = 3.0 + this.getRandom().nextDouble() * 3.0;
            minion.moveTo(this.getX() + Math.cos(angle) * dist, this.getY(), this.getZ() + Math.sin(angle) * dist, this.getYRot(), 0.0F);
            serverLevel.addFreshEntity(minion);
        }
    }
}
