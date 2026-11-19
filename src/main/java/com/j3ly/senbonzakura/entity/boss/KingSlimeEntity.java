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
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * King Slime — pre-iron tier boss. Vanilla slime reskin with 44 HP (22 hearts)
 * and a jump-slam attack. Splits into smaller slimes when defeated.
 */
public class KingSlimeEntity extends Slime {
    private static final int SIZE = 4;
    private static final UUID BOSS_UUID = UUID.fromString("11111111-1111-1111-1111-111111111101");

    private final ServerBossEvent bossEvent;
    private final Set<ServerPlayer> trackedPlayers = new HashSet<>();

    public KingSlimeEntity(EntityType<? extends Slime> type, Level level) {
        super(type, level);
        // Override size-based health: 44 HP (22 hearts).
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(44.0D);
        this.setHealth(this.getMaxHealth());
        this.bossEvent = new ServerBossEvent(
                Component.literal("King Slime"),
                BossEvent.BossBarColor.PURPLE,
                BossEvent.BossBarOverlay.NOTCHED_20
        );
        this.bossEvent.setVisible(true);
        this.setSize(SIZE, false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AttributeSupplier.builder()
                .add(Attributes.MAX_HEALTH, 44.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D);
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
        if (this.level() instanceof ServerLevel serverLevel) {
            // Split into 2-4 medium slimes on death.
            int count = 2 + this.getRandom().nextInt(3);
            for (int i = 0; i < count; i++) {
                Slime slime = new Slime(EntityType.SLIME, serverLevel);
                slime.setSize(2, false);
                slime.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
                serverLevel.addFreshEntity(slime);
            }
        }
        super.die(source);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("KingSlimeBoss", true);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
    }
}
