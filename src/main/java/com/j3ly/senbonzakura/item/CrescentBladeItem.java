package com.j3ly.senbonzakura.item;

import com.j3ly.senbonzakura.ModGameRules;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
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

    public CrescentBladeItem(Properties properties) {
        super(Tiers.DIAMOND, properties.attributes(SwordItem.createAttributes(Tiers.DIAMOND, 3, -2.4f)));
    }

    private static final Map<UUID, Integer> chargeTicks = new HashMap<>();
    private static final Map<UUID, Boolean> isCharging = new HashMap<>();
    private static final Map<UUID, Long> cooldowns = new HashMap<>();
    private static final int CHARGE_TIME = 10;
    private static final long COOLDOWN_TIME = 140L;

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            UUID id = player.getUUID();
            Long lastUse = cooldowns.get(id);
            if (lastUse != null && level.getGameTime() - lastUse < COOLDOWN_TIME) {
                return InteractionResultHolder.fail(stack);
            }
            isCharging.put(id, true);
            chargeTicks.put(id, 0);
        }
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingUseDuration) {
        if (!(entity instanceof Player player)) return;
        if (!(level instanceof ServerLevel serverLevel)) return;

        UUID id = player.getUUID();
        if (!Boolean.TRUE.equals(isCharging.get(id))) return;

        int ticks = chargeTicks.getOrDefault(id, 0) + 1;
        chargeTicks.put(id, ticks);

        if (ticks % 3 == 0) {
            Vec3 pos = player.position().add(0, 1, 0);
            double angle = ticks * 0.3;
            double radius = 1.5;
            double px = pos.x + Math.cos(angle) * radius;
            double pz = pos.z + Math.sin(angle) * radius;
            serverLevel.sendParticles(ParticleTypes.CLOUD, px, pos.y, pz, 1, 0, 0.1, 0, 0.02);
        }

        if (ticks >= CHARGE_TIME) {
            player.releaseUsingItem();
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!(entity instanceof Player player)) return stack;
        if (!(level instanceof ServerLevel serverLevel)) return stack;

        UUID id = player.getUUID();
        int ticks = chargeTicks.getOrDefault(id, 0);
        isCharging.put(id, false);
        chargeTicks.put(id, 0);

        if (ticks >= CHARGE_TIME) {
            if (!ModGameRules.tryConsumeAbilityXp(player, serverLevel)) {
                return stack;
            }
            cooldowns.put(id, level.getGameTime());
            performSlash(player, serverLevel);
        }

        return stack;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeCharged) {
        if (!(entity instanceof Player player)) return;
        if (!(level instanceof ServerLevel serverLevel)) return;

        UUID id = player.getUUID();
        int ticks = chargeTicks.getOrDefault(id, 0);
        isCharging.put(id, false);
        chargeTicks.put(id, 0);

        if (ticks >= CHARGE_TIME) {
            if (!ModGameRules.tryConsumeAbilityXp(player, serverLevel)) {
                return;
            }
            cooldowns.put(id, level.getGameTime());
            performSlash(player, serverLevel);
        }
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    public static boolean isDualWielding(Player player) {
        return player.getMainHandItem().getItem() instanceof CrescentBladeItem
            && player.getOffhandItem().getItem() instanceof CrescentBladeItem;
    }

    private void performSlash(Player player, ServerLevel level) {
        boolean dualWield = isDualWielding(player);
        int slashCount = dualWield ? 2 : 1;

        Vec3 pos = player.position();
        float baseDamage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float damage = baseDamage * 1.5f;
        if (dualWield) {
            damage *= 2.0f;
        }

        for (int slash = 0; slash < slashCount; slash++) {
            if (slash == 1) {
                pos = pos.add(player.getLookAngle().scale(0.5));
            }

            double radius = 4.0;
            AABB slashBox = new AABB(
                pos.x - radius, pos.y - 1, pos.z - radius,
                pos.x + radius, pos.y + 3, pos.z + radius
            );

            List<Entity> entities = level.getEntities(player, slashBox,
                    e -> e instanceof LivingEntity && e != player);

            level.playSound(null, pos.x, pos.y, pos.z,
                    SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0f, 0.8f + slash * 0.2f);

            for (int i = 0; i < 36; i++) {
                double angle = Math.toRadians(i * 10 + slash * 5);
                double px = pos.x + Math.cos(angle) * radius * 0.7;
                double pz = pos.z + Math.sin(angle) * radius * 0.7;
                level.sendParticles(ParticleTypes.SWEEP_ATTACK, px, pos.y + 1, pz, 1, 0, 0, 0, 0.1);
                level.sendParticles(ParticleTypes.CLOUD, px, pos.y + 0.5, pz, 1, 0, 0.2, 0, 0.05);
            }

            for (Entity target : entities) {
                if (target instanceof LivingEntity living) {
                    Vec3 toTarget = target.position().subtract(pos).normalize();
                    if (toTarget.lengthSqr() < 0.001) {
                        toTarget = new Vec3(1, 0, 0);
                    }
                    target.setDeltaMovement(toTarget.x * 1.8, 0.6, toTarget.z * 1.8);
                    target.hurtMarked = true;
                    living.hurt(level.damageSources().playerAttack(player), damage);
                    level.sendParticles(ParticleTypes.DAMAGE_INDICATOR,
                            target.getX(), target.getY() + 1, target.getZ(),
                            3, 0.2, 0.2, 0.2, 0.1);
                }
            }
        }

        if (dualWield) {
            level.sendParticles(ParticleTypes.EXPLOSION, pos.x, pos.y + 1, pos.z, 1, 0, 0, 0, 0);
        }
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return super.hurtEnemy(stack, target, attacker);
    }
}
