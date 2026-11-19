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
import net.minecraft.world.phys.AABB;

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
    private int customAiTick = 0;

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

    protected void onCustomAiTick() {
        // King Slime: jump slam every 60 ticks — leaps at target and slams down
        if (this.customAiTick % 60 == 0 && this.onGround()) {
            Player target = this.findNearestPlayer(10.0);
            if (target != null && this.level() instanceof ServerLevel serverLevel) {
                this.jumpSlam(serverLevel, target);
            }
        }
    }

    private void jumpSlam(ServerLevel serverLevel, Player target) {
        // Leap toward the target
        double dx = target.getX() - this.getX();
        double dz = target.getZ() - this.getZ();
        double dist = Math.sqrt(dx * dx + dz * dz);
        if (dist > 0.5) {
            this.setDeltaMovement(dx / dist * 0.8, 0.6, dz / dist * 0.8);
        }
        // Slash/damage on landing handled in next tick when onGround
        if (this.onGround() && dist < 4.0) {
            this.doSlamDamage(serverLevel);
        }
    }

    private void doSlamDamage(ServerLevel serverLevel) {
        // Area damage on slam landing
        serverLevel.getEntitiesOfClass(Player.class,
                new AABB(this.getX() - 2.5, this.getY() - 1.0, this.getZ() - 2.5,
                        this.getX() + 2.5, this.getY() + 1.0, this.getZ() + 2.5)).forEach(player -> {
            if (player.isAlive() && player.distanceToSqr(this) < 16.0) {
                player.hurt(this.damageSources().mobAttack(this), 6.0F);
            }
        });
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
        tag.putInt("CustomAiTick", this.customAiTick);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.customAiTick = tag.getInt("CustomAiTick");
    }

    /** Finds the nearest living player within range. */
    protected Player findNearestPlayer(double range) {
        Level level = this.level();
        if (!(level instanceof ServerLevel serverLevel)) return null;
        return serverLevel.getNearestPlayer(this, range);
    }
}
