package com.senbonzakura.item;

import com.senbonzakura.network.DashPacket;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SenbonzakuraItem extends SwordItem {

    public SenbonzakuraItem(Properties properties) {
        super(Tiers.NETHERITE, 3, -2.4f, properties);
    }

    // Track charge progress per player
    private static final Map<UUID, Integer> chargeTicks = new HashMap<>();
    private static final Map<UUID, Boolean> isCharging = new HashMap<>();
    private static final int CHARGE_TIME = 60; // 3 seconds at 20 tps

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            UUID id = player.getUUID();
            isCharging.put(id, true);
            chargeTicks.put(id, 0);
        }
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int count) {
        if (!(entity instanceof Player player)) return;
        if (level.isClientSide) return;

        UUID id = player.getUUID();
        if (!Boolean.TRUE.equals(isCharging.get(id))) return;

        int ticks = chargeTicks.getOrDefault(id, 0) + 1;
        chargeTicks.put(id, ticks);

        // Visual feedback during charge
        if (ticks % 5 == 0) {
            ServerLevel serverLevel = (ServerLevel) level;
            Vec3 pos = player.position().add(0, 1, 0);
            serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK,
                    pos.x, pos.y, pos.z, 1, 0.2, 0.2, 0.2, 0);
        }

        // Auto-release after full charge
        if (ticks >= CHARGE_TIME) {
            player.releaseUsingItem();
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!(entity instanceof Player player)) return stack;
        if (level.isClientSide) return stack;

        UUID id = player.getUUID();
        int ticks = chargeTicks.getOrDefault(id, 0);
        isCharging.put(id, false);
        chargeTicks.put(id, 0);

        // Only execute dash if charged for at least 3 seconds (60 ticks)
        if (ticks >= CHARGE_TIME) {
            performDash(player, (ServerLevel) level);
        }

        return stack;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeCharged) {
        if (!(entity instanceof Player player)) return;
        if (level.isClientSide) return;

        UUID id = player.getUUID();
        int ticks = chargeTicks.getOrDefault(id, 0);
        isCharging.put(id, false);
        chargeTicks.put(id, 0);

        if (ticks >= CHARGE_TIME) {
            performDash(player, (ServerLevel) level);
        }
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000; // Max charge time (like bow)
    }

    private void performDash(Player player, ServerLevel level) {
        Vec3 look = player.getLookAngle();
        Vec3 start = player.position();
        Vec3 end = start.add(look.x * 12, look.y * 12, look.z * 12);

        // Play dash sound
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0f, 1.0f);

        // Dash in segments to check for collisions and hit entities along the path
        int segments = 12;
        Vec3 step = end.subtract(start).scale(1.0 / segments);
        Vec3 currentPos = start;

        for (int i = 0; i < segments; i++) {
            Vec3 nextPos = currentPos.add(step);

            // Check for entities in the dash path
            AABB dashBox = new AABB(
                    Math.min(currentPos.x, nextPos.x) - 1.5,
                    Math.min(currentPos.y, nextPos.y) - 1.0,
                    Math.min(currentPos.z, nextPos.z) - 1.5,
                    Math.max(currentPos.x, nextPos.x) + 1.5,
                    Math.max(currentPos.y, nextPos.y) + 2.0,
                    Math.max(currentPos.z, nextPos.z) + 1.5
            );

            List<Entity> entities = level.getEntities(player, dashBox, e -> e instanceof LivingEntity && e != player);

            for (Entity target : entities) {
                if (target instanceof LivingEntity living) {
                    // Push back
                    Vec3 pushDir = target.position().subtract(player.position()).normalize();
                    if (pushDir.lengthSqr() < 0.001) {
                        pushDir = look;
                    }
                    target.setDeltaMovement(pushDir.x * 1.5, 0.5, pushDir.z * 1.5);
                    target.hurtMarked = true;

                    // Deal 50% weapon damage
                    float damage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.5f;
                    living.hurt(level.damageSources().playerAttack(player), damage);
                }
            }

            // Particle trail
            level.sendParticles(ParticleTypes.CLOUD,
                    nextPos.x, nextPos.y + 1, nextPos.z,
                    2, 0.1, 0.1, 0.1, 0.02);

            currentPos = nextPos;
        }

        // Teleport player to end of dash
        player.teleportTo(end.x, end.y, end.z);
        player.fallDistance = 0;

        // Mini 180° sweep attack at end
        performSweepAttack(player, level);
    }

    private void performSweepAttack(Player player, ServerLevel level) {
        Vec3 pos = player.position();
        Vec3 look = player.getLookAngle();

        // Play sweep sound
        level.playSound(null, pos.x, pos.y, pos.z,
                SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0f, 1.0f);

        // Spawn sweep particle
        level.sendParticles(ParticleTypes.SWEEP_ATTACK,
                pos.x + look.x * 2, pos.y + 1, pos.z + look.z * 2,
                5, 2.0, 0.5, 2.0, 0.1);

        // 180° sweep in front - hit all entities in a wide arc
        float attackDamage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
        AABB sweepBox = new AABB(
                pos.x - 4, pos.y - 1, pos.z - 4,
                pos.x + 4, pos.y + 3, pos.z + 4
        );

        List<Entity> entities = level.getEntities(player, sweepBox,
                e -> e instanceof LivingEntity && e != player);

        for (Entity target : entities) {
            if (target instanceof LivingEntity living) {
                // Check if entity is in front (180° cone)
                Vec3 toTarget = target.position().subtract(pos).normalize();
                double dot = look.dot(toTarget);

                // dot > 0 means within 90° of look direction, but we want 180°
                // For 180° sweep, we check if entity is in front hemisphere
                if (dot > -0.1) { // roughly 180°
                    // Apply knockback
                    Vec3 knockback = toTarget.scale(0.8);
                    target.setDeltaMovement(knockback.x, 0.3, knockback.z);
                    target.hurtMarked = true;

                    // Full weapon damage
                    living.hurt(level.damageSources().playerAttack(player), attackDamage);

                    // Spawn hit particles
                    level.sendParticles(ParticleTypes.DAMAGE_INDICATOR,
                            target.getX(), target.getY() + 1, target.getZ(),
                            3, 0.2, 0.2, 0.2, 0.1);
                }
            }
        }
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        // Normal sword attack behavior
        return false;
    }
}
