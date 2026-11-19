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
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * Abstract base class for all Chaotic Mode bosses. Uses Iron Golem as the
 * placeholder reskin. Provides shared boss infrastructure: boss bar, health
 * tracking, anti-despawn, and a custom AI tick hook.
 */
public abstract class ChaoticBossEntity extends IronGolem {
    private final ServerBossEvent bossEvent;
    private final Set<ServerPlayer> trackedPlayers = new HashSet<>();
    private int customAiTick = 0;

    protected ChaoticBossEntity(EntityType<? extends IronGolem> type, Level level,
                                String displayName, BossEvent.BossBarColor color,
                                float maxHealth, double speed, double attackDamage) {
        super(type, level);
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(maxHealth);
        this.setHealth(this.getMaxHealth());
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(speed);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(attackDamage);
        this.bossEvent = new ServerBossEvent(
                Component.literal(displayName),
                color,
                BossEvent.BossBarOverlay.NOTCHED_20
        );
        this.bossEvent.setVisible(true);
    }

    public static AttributeSupplier.Builder createBossAttributes(float maxHealth, double speed, double attackDamage) {
        return AttributeSupplier.builder()
                .add(Attributes.MAX_HEALTH, maxHealth)
                .add(Attributes.MOVEMENT_SPEED, speed)
                .add(Attributes.ATTACK_DAMAGE, attackDamage);
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

    /** Custom AI logic executed every tick. Subclasses override for signature abilities. */
    protected abstract void onCustomAiTick();

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
        tag.putBoolean("ChaoticBoss", true);
        tag.putInt("CustomAiTick", this.customAiTick);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.customAiTick = tag.getInt("CustomAiTick");
    }

    protected Player findNearestPlayer(double range) {
        Level level = this.level();
        if (!(level instanceof ServerLevel serverLevel)) return null;
        return serverLevel.getNearestPlayer(this, range);
    }

    /** Spawns N iron golems as minions around this boss. */
    protected void spawnMinions(ServerLevel serverLevel, int count, double radius) {
        for (int i = 0; i < count; i++) {
            net.minecraft.world.entity.animal.IronGolem minion =
                    new net.minecraft.world.entity.animal.IronGolem(
                            net.minecraft.world.entity.EntityType.IRON_GOLEM, serverLevel);
            double angle = this.getRandom().nextDouble() * 2 * Math.PI;
            double dist = 2.0 + this.getRandom().nextDouble() * radius;
            minion.moveTo(
                    this.getX() + Math.cos(angle) * dist,
                    this.getY(),
                    this.getZ() + Math.sin(angle) * dist,
                    this.getYRot(), 0.0F
            );
            serverLevel.addFreshEntity(minion);
        }
    }

    /** Deals area damage to all players within radius. */
    protected void areaDamage(ServerLevel serverLevel, double radius, float damage) {
        serverLevel.getEntitiesOfClass(Player.class,
                new AABB(this.getX() - radius, this.getY() - radius, this.getZ() - radius,
                        this.getX() + radius, this.getY() + radius, this.getZ() + radius))
                .forEach(player -> {
                    if (player.isAlive() && player.distanceToSqr(this) < radius * radius) {
                        player.hurt(this.damageSources().mobAttack(this), damage);
                    }
                });
    }

    /** Launches this boss toward a target. */
    protected void leapToTarget(Player target, double speed) {
        double dx = target.getX() - this.getX();
        double dz = target.getZ() - this.getZ();
        double dist = Math.sqrt(dx * dx + dz * dz);
        if (dist > 0.5) {
            Vec3 look = this.getLookAngle();
            this.setDeltaMovement(dx / dist * speed, 0.5, dz / dist * speed);
        }
    }

    /** Fires an arrow at the target. */
    protected void shootArrow(ServerLevel serverLevel, Player target) {
        net.minecraft.world.entity.projectile.Arrow arrow =
                new net.minecraft.world.entity.projectile.Arrow(
                        net.minecraft.world.entity.EntityType.ARROW, serverLevel);
        arrow.moveTo(this.getX(), this.getEyeY(), this.getZ());
        double dx = target.getX() - this.getX();
        double dy = target.getY() + target.getEyeHeight() - 1.0 - this.getY();
        double dz = target.getZ() - this.getZ();
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz) * 0.2;
        arrow.shoot(dx * dist, dy * dist + 0.2, dz * dist, 1.6F, 2.0F);
        serverLevel.addFreshEntity(arrow);
    }

    /** Places cobwebs around the target. */
    protected void placeWebs(ServerLevel serverLevel, Player target, int count, double radius) {
        for (int i = 0; i < count; i++) {
            double offsetX = (this.getRandom().nextDouble() - 0.5) * radius;
            double offsetZ = (this.getRandom().nextDouble() - 0.5) * radius;
            BlockPos webPos = new BlockPos(
                    (int) (target.getX() + offsetX),
                    (int) target.getY(),
                    (int) (target.getZ() + offsetZ)
            );
            if (serverLevel.isEmptyBlock(webPos)) {
                serverLevel.setBlock(webPos, net.minecraft.world.level.block.Blocks.COBWEB.defaultBlockState(), 3);
            }
        }
    }
}
