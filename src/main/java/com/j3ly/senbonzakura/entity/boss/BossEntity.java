package com.j3ly.senbonzakura.entity.boss;

import com.j3ly.senbonzakura.entity.ModEntities;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.HashSet;
import java.util.Set;

/**
 * Abstract base class for all Chaotic Mode bosses. Provides shared boss
 * infrastructure: boss bar, health tracking, anti-despawn, and a custom AI
 * tick hook that subclasses implement for their signature abilities.
 */
public abstract class BossEntity extends Monster {
    private final ServerBossEvent bossEvent;
    private final Set<ServerPlayer> trackedPlayers = new HashSet<>();
    private int customAiTick = 0;

    protected BossEntity(EntityType<? extends Monster> type, Level level, String displayName,
                         BossEvent.BossBarColor color, float maxHealth) {
        super(type, level);
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(maxHealth);
        this.setHealth(this.getMaxHealth());
        this.bossEvent = new ServerBossEvent(
                Component.literal(displayName),
                color,
                BossEvent.BossBarOverlay.NOTCHED_20
        );
        this.bossEvent.setVisible(true);
    }

    public static AttributeSupplier.Builder createBossAttributes(float maxHealth, double speed, double attack) {
        return AttributeSupplier.builder()
                .add(Attributes.MAX_HEALTH, maxHealth)
                .add(Attributes.MOVEMENT_SPEED, speed)
                .add(Attributes.ATTACK_DAMAGE, attack);
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
        this.customAiTick++;
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
        this.onCustomAiTick();
    }

    /**
     * Custom AI logic executed every tick. Subclasses override this for
     * signature abilities (jump slam, arrow barrage, web traps, etc.).
     * {@code customAiTick} increments every tick and resets on death.
     */
    protected abstract void onCustomAiTick();

    /** Returns the number of ticks since this boss spawned. */
    protected int getCustomAiTick() {
        return customAiTick;
    }

    @Override
    public void checkDespawn() {
        // Bosses never despawn naturally.
    }

    @Override
    public void die(DamageSource source) {
        if (this.level() instanceof ServerLevel serverLevel) {
            this.bossEvent.removeAllPlayers();
        }
        super.die(source);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("BossEntity", true);
        tag.putInt("CustomAiTick", this.customAiTick);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.customAiTick = tag.getInt("CustomAiTick");
    }

    /**
     * Finds the nearest living player within range, or null.
     */
    protected Player findNearestPlayer(double range) {
        Level level = this.level();
        if (!(level instanceof ServerLevel serverLevel)) return null;
        return serverLevel.getNearestPlayer(this, range);
    }

    /** Returns the boss's registry name for logging/debugging. */
    protected String bossName() {
        return this.getClass().getSimpleName();
    }
}
