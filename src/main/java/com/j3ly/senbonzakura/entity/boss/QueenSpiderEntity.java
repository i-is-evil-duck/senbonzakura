package com.j3ly.senbonzakura.entity.boss;

import com.j3ly.senbonzakura.entity.ModEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.HashSet;
import java.util.Set;

/**
 * Queen Spider — pre-iron tier boss, 90 HP (45 hearts), vanilla spider reskin
 * with boss bar. Never despawns naturally.
 */
public class QueenSpiderEntity extends Spider {
    private final ServerBossEvent bossEvent;
    private final Set<ServerPlayer> trackedPlayers = new HashSet<>();

    public QueenSpiderEntity(EntityType<? extends Spider> type, Level level) {
        super(type, level);
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(90.0D);
        this.setHealth(this.getMaxHealth());
        this.bossEvent = new ServerBossEvent(
                Component.literal("Queen Spider"),
                BossEvent.BossBarColor.PINK,
                BossEvent.BossBarOverlay.NOTCHED_20
        );
        this.bossEvent.setVisible(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AttributeSupplier.builder()
                .add(Attributes.MAX_HEALTH, 90.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D);
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
        this.trackedPlayers.add(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
        this.trackedPlayers.remove(player);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level() instanceof ServerLevel serverLevel) {
            this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
            for (ServerPlayer player : serverLevel.getPlayers(p -> true)) {
                if (!this.trackedPlayers.contains(player) &&
                        player.distanceToSqr(this) < 64.0 * 64.0) {
                    this.bossEvent.addPlayer(player);
                    this.trackedPlayers.add(player);
                }
            }
        }
    }

    @Override
    public void checkDespawn() {
        // Bosses never despawn naturally.
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (this.level() instanceof ServerLevel serverLevel) {
            this.bossEvent.removeAllPlayers();
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("QueenSpiderBoss", true);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
    }
}
