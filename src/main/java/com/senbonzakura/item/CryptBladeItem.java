package com.senbonzakura.item;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CryptBladeItem extends AxeItem {

    // Same stats as netherite axe
    public CryptBladeItem(Properties properties) {
        super(Tiers.NETHERITE, 5, -3.0f, properties);
    }

    private static final Map<UUID, Integer> chargeTicks = new HashMap<>();
    private static final Map<UUID, Boolean> isCharging = new HashMap<>();
    private static final Map<UUID, Vec3> frozenPositions = new HashMap<>();
    private static final int CHARGE_TIME = 100; // 5 seconds at 20 tps

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            UUID id = player.getUUID();
            isCharging.put(id, true);
            chargeTicks.put(id, 0);
            frozenPositions.put(id, player.position());

            // Apply effects: resistance + slowness (can't move)
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, CHARGE_TIME + 10, 1, false, false, true));
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, CHARGE_TIME + 10, 255, false, false, true));
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

        // Freeze player in place - cannot be moved or knocked back
        Vec3 frozenPos = frozenPositions.get(id);
        if (frozenPos != null) {
            // Force position every tick
            if (player.position().distanceToSqr(frozenPos) > 0.01) {
                player.teleportTo(frozenPos.x, frozenPos.y, frozenPos.z);
            }
            // Zero out velocity
            player.setDeltaMovement(0, 0, 0);
            player.hurtMarked = true;
        }

        // Dark particle aura during charge
        if (ticks % 4 == 0) {
            ServerLevel serverLevel = (ServerLevel) level;
            Vec3 pos = player.position().add(0, 1, 0);
            double angle = ticks * 0.15;
            double radius = 1.2 + Math.sin(ticks * 0.1) * 0.3;

            // Dark swirling particles
            for (int i = 0; i < 4; i++) {
                double a = angle + (Math.PI / 2) * i;
                double px = pos.x + Math.cos(a) * radius;
                double pz = pos.z + Math.sin(a) * radius;
                double py = pos.y + Math.sin(ticks * 0.2 + i) * 0.5;
                serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, px, py, pz, 1, 0, 0, 0, 0.01);
            }

            // Smoke particles
            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, 
                pos.x, pos.y - 0.5, pos.z, 2, 0.5, 0.2, 0.5, 0.02);
        }

        // Intense particles in last second
        if (ticks >= 80 && ticks % 2 == 0) {
            ServerLevel serverLevel = (ServerLevel) level;
            Vec3 pos = player.position();
            serverLevel.sendParticles(ParticleTypes.SOUL, 
                pos.x, pos.y + 1, pos.z, 3, 1.0, 1.0, 1.0, 0.05);
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
        frozenPositions.remove(id);

        // Remove slowness, keep resistance briefly
        player.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);

        if (ticks >= CHARGE_TIME) {
            performExplosion(player, (ServerLevel) level);
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
        frozenPositions.remove(id);
        player.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);

        if (ticks >= CHARGE_TIME) {
            performExplosion(player, (ServerLevel) level);
        }
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // Normal axe attack
        return super.hurtEnemy(stack, target, attacker);
    }

    private void performExplosion(Player player, ServerLevel level) {
        Vec3 pos = player.position();
        double explosionRadius = 6.0;

        // Play explosion sound
        level.playSound(null, pos.x, pos.y, pos.z,
                SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 2.0f, 0.8f);

        // Play wither spawn sound for dark effect
        level.playSound(null, pos.x, pos.y, pos.z,
                SoundEvents.WITHER_SPAWN, SoundSource.PLAYERS, 0.5f, 1.2f);

        // Visual explosion particles (no block damage)
        // Center explosion
        level.sendParticles(ParticleTypes.EXPLOSION, 
            pos.x, pos.y + 1, pos.z, 5, 0.5, 0.5, 0.5, 0.1);

        // Large smoke ring
        for (int i = 0; i < 36; i++) {
            double angle = Math.toRadians(i * 10);
            double px = pos.x + Math.cos(angle) * explosionRadius * 0.5;
            double pz = pos.z + Math.sin(angle) * explosionRadius * 0.5;
            level.sendParticles(ParticleTypes.LARGE_SMOKE, px, pos.y + 0.5, pz, 1, 0, 0.2, 0, 0.05);
        }

        // Soul fire burst
        level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
            pos.x, pos.y + 1, pos.z, 30, explosionRadius * 0.5, 1.0, explosionRadius * 0.5, 0.3);

        // Ash/soul particles
        level.sendParticles(ParticleTypes.ASH,
            pos.x, pos.y + 1, pos.z, 50, explosionRadius * 0.7, 1.5, explosionRadius * 0.7, 0.1);

        // Damage entities in radius (but NOT the player)
        AABB explosionBox = new AABB(
            pos.x - explosionRadius, pos.y - 2, pos.z - explosionRadius,
            pos.x + explosionRadius, pos.y + 4, pos.z + explosionRadius
        );

        List<Entity> entities = level.getEntities(player, explosionBox,
                e -> e instanceof LivingEntity && e != player);

        float damage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE) * 1.5f;

        for (Entity target : entities) {
            if (target instanceof LivingEntity living) {
                // Calculate distance-based damage falloff
                double dist = target.distanceTo(player);
                float finalDamage = damage * (float)(1.0 - (dist / explosionRadius) * 0.5);
                if (finalDamage < 1.0f) finalDamage = 1.0f;

                // Apply damage
                living.hurt(level.damageSources().playerAttack(player), finalDamage);

                // Strong knockback away from explosion center
                Vec3 toTarget = target.position().subtract(pos).normalize();
                if (toTarget.lengthSqr() < 0.001) {
                    toTarget = new Vec3(1, 0, 0);
                }
                double knockbackStrength = 2.5 * (1.0 - dist / explosionRadius);
                target.setDeltaMovement(
                    toTarget.x * knockbackStrength,
                    0.8 + knockbackStrength * 0.3,
                    toTarget.z * knockbackStrength
                );
                target.hurtMarked = true;

                // Dark hit particles
                level.sendParticles(ParticleTypes.SOUL,
                    target.getX(), target.getY() + 1, target.getZ(),
                    5, 0.3, 0.3, 0.3, 0.05);
            }
        }

        // Screen shake effect for player
        player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 20, 0, false, false, false));
    }
}
