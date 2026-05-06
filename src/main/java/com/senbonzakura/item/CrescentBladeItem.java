package com.senbonzakura.item;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
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

public class CrescentBladeItem extends SwordItem {

    // Same stats as diamond sword
    public CrescentBladeItem(Properties properties) {
        super(Tiers.DIAMOND, 3, -2.4f, properties);
    }

    private static final Map<UUID, Integer> chargeTicks = new HashMap<>();
    private static final Map<UUID, Boolean> isCharging = new HashMap<>();
    private static final int CHARGE_TIME = 40; // 2 seconds at 20 tps

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

        // Wind swirl particles during charge
        if (ticks % 3 == 0) {
            ServerLevel serverLevel = (ServerLevel) level;
            Vec3 pos = player.position().add(0, 1, 0);
            double angle = ticks * 0.3;
            double radius = 1.5;
            double px = pos.x + Math.cos(angle) * radius;
            double pz = pos.z + Math.sin(angle) * radius;
            serverLevel.sendParticles(ParticleTypes.CLOUD, px, pos.y, pz, 1, 0, 0.1, 0, 0.02);
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

        if (ticks >= CHARGE_TIME) {
            performSlash(player, (ServerLevel) level);
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
            performSlash(player, (ServerLevel) level);
        }
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    // Check if player is dual-wielding Crescent Blades
    public static boolean isDualWielding(Player player) {
        return player.getMainHandItem().getItem() instanceof CrescentBladeItem
            && player.getOffhandItem().getItem() instanceof CrescentBladeItem;
    }

    @Override
    public float getDamage() {
        // Base diamond sword damage
        return 7.0f; // 3 (base) + 4 (tier bonus) = 7, but we handle dual-wield in attack logic
    }

    private void performSlash(Player player, ServerLevel level) {
        boolean dualWield = isDualWielding(player);
        int slashCount = dualWield ? 2 : 1;

        Vec3 pos = player.position();
        float baseDamage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
        // Double damage if dual-wielding
        float damage = dualWield ? baseDamage * 2.0f : baseDamage;

        for (int slash = 0; slash < slashCount; slash++) {
            // Delay second slash slightly
            if (slash == 1) {
                // For simplicity, we do both immediately; in a real mod you'd schedule the second
                // Here we'll offset the second slash position slightly
                pos = pos.add(player.getLookAngle().scale(0.5));
            }

            // 360° slash - hit everything in a large radius
            double radius = 4.0;
            AABB slashBox = new AABB(
                pos.x - radius, pos.y - 1, pos.z - radius,
                pos.x + radius, pos.y + 3, pos.z + radius
            );

            List<Entity> entities = level.getEntities(player, slashBox,
                    e -> e instanceof LivingEntity && e != player);

            // Play slash sound
            level.playSound(null, pos.x, pos.y, pos.z,
                    SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0f, 0.8f + slash * 0.2f);

            // Wind slash particles - ring effect
            for (int i = 0; i < 36; i++) {
                double angle = Math.toRadians(i * 10 + slash * 5);
                double px = pos.x + Math.cos(angle) * radius * 0.7;
                double pz = pos.z + Math.sin(angle) * radius * 0.7;
                level.sendParticles(ParticleTypes.SWEEP_ATTACK, px, pos.y + 1, pz, 1, 0, 0, 0, 0.1);
                level.sendParticles(ParticleTypes.CLOUD, px, pos.y + 0.5, pz, 1, 0, 0.2, 0, 0.05);
            }

            // Damage and knockback all entities in range
            for (Entity target : entities) {
                if (target instanceof LivingEntity living) {
                    // Knockback away from player
                    Vec3 toTarget = target.position().subtract(pos).normalize();
                    if (toTarget.lengthSqr() < 0.001) {
                        toTarget = new Vec3(1, 0, 0);
                    }
                    // Strong knockback
                    target.setDeltaMovement(toTarget.x * 1.8, 0.6, toTarget.z * 1.8);
                    target.hurtMarked = true;

                    // Apply damage
                    living.hurt(level.damageSources().playerAttack(player), damage);

                    // Hit particles
                    level.sendParticles(ParticleTypes.DAMAGE_INDICATOR,
                            target.getX(), target.getY() + 1, target.getZ(),
                            3, 0.2, 0.2, 0.2, 0.1);
                }
            }
        }

        // Screen shake effect for dual wield
        if (dualWield) {
            level.sendParticles(ParticleTypes.EXPLOSION, pos.x, pos.y + 1, pos.z, 1, 0, 0, 0, 0);
        }
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // Apply doubled damage if dual-wielding during normal attacks too
        if (attacker instanceof Player player && isDualWielding(player)) {
            // The actual damage doubling is handled by attribute modifiers in a real implementation
            // For now, the sweep attack handles the doubling
        }
        return super.hurtEnemy(stack, target, attacker);
    }
}
