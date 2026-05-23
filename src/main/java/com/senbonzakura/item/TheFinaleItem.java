package com.senbonzakura.item;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
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

public class TheFinaleItem extends SwordItem {

    private static final Map<UUID, Integer> chargeTicks = new HashMap<>();
    private static final Map<UUID, Boolean> isCharging = new HashMap<>();
    private static final Map<UUID, Long> cooldowns = new HashMap<>();
    private static final Map<UUID, Boolean> isChargingUp = new HashMap<>();
    private static final int CHARGE_TIME = 60;
    private static final long COOLDOWN_TIME = 600L;

    public TheFinaleItem(Properties properties) {
        super(Tiers.NETHERITE, 6, -2.4f, properties);
    }

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
            isChargingUp.put(id, true);
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

        ServerLevel serverLevel = (ServerLevel) level;
        Vec3 pos = player.position();

        for (int i = 0; i < 3; i++) {
            double angle = Math.random() * Math.PI * 2;
            double dist = 0.5 + Math.random() * 1.5;
            double px = pos.x + Math.cos(angle) * dist;
            double pz = pos.z + Math.sin(angle) * dist;
            double py = pos.y + 0.5 + Math.random() * 1.5;
            serverLevel.sendParticles(ParticleTypes.EXPLOSION, px, py, pz, 1, 0.1, 0.1, 0.1, 0.05);
            serverLevel.sendParticles(ParticleTypes.FLAME, px, py, pz, 1, 0.05, 0.05, 0.05, 0.02);
        }

        player.invulnerableTime = 10;

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
        isChargingUp.put(id, false);
        chargeTicks.put(id, 0);

        if (ticks >= CHARGE_TIME) {
            cooldowns.put(id, level.getGameTime());
            performUltimateAttack(player, (ServerLevel) level);
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
        isChargingUp.put(id, false);
        chargeTicks.put(id, 0);

        if (ticks >= CHARGE_TIME) {
            cooldowns.put(id, level.getGameTime());
            performUltimateAttack(player, (ServerLevel) level);
        }
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public float getDamage() {
        return 19.0f;
    }

    private void performUltimateAttack(Player player, ServerLevel level) {
        Vec3 pos = player.position();

        level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 2.0f, 0.8f);
        level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.WITHER_SPAWN, SoundSource.PLAYERS, 1.0f, 1.2f);

        AABB slashBox = new AABB(pos.x - 8, pos.y - 2, pos.z - 8, pos.x + 8, pos.y + 4, pos.z + 8);
        List<Entity> slashTargets = level.getEntities(player, slashBox, e -> e instanceof LivingEntity && e != player);

        for (Entity target : slashTargets) {
            if (target instanceof LivingEntity living) {
                Vec3 pushDir = target.position().subtract(pos).normalize();
                target.setDeltaMovement(pushDir.x * 2.0, 1.0, pushDir.z * 2.0);
                target.hurtMarked = true;
                living.hurt(level.damageSources().playerAttack(player), 50.0f);
            }
        }

        for (int i = 0; i < 8; i++) {
            double angle = (Math.PI * 2 / 8) * i;
            double px = pos.x + Math.cos(angle) * 24;
            double pz = pos.z + Math.sin(angle) * 24;
            level.sendParticles(ParticleTypes.FLAME, px, pos.y + 1, pz, 3, 0.1, 0.1, 0.1, 0.05);

            AABB projectileBox = new AABB(px - 1, pos.y - 1, pz - 1, px + 1, pos.y + 2, pz + 1);
            List<Entity> projectileTargets = level.getEntities(player, projectileBox, e -> e instanceof LivingEntity && e != player);
            for (Entity target : projectileTargets) {
                if (target instanceof LivingEntity living) {
                    living.hurt(level.damageSources().playerAttack(player), 50.0f);
                    living.setRemainingFireTicks(100);
                }
            }
        }

        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100, 4));
        player.addEffect(new MobEffectInstance(MobEffects.JUMP, 100, 2));
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 0));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, 0));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 100, 1));

        for (Entity target : slashTargets) {
            if (target instanceof LivingEntity living) {
                living.addEffect(new MobEffectInstance(MobEffects.WITHER, 60, 0));
                living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1));
                living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 1));
                living.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 60, 0));
                living.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0));
            }
        }

        for (int i = 0; i < 50; i++) {
            double angle = Math.random() * Math.PI * 2;
            double dist = Math.random() * 6;
            double px = pos.x + Math.cos(angle) * dist;
            double pz = pos.z + Math.sin(angle) * dist;
            level.sendParticles(ParticleTypes.EXPLOSION, px, pos.y + 1, pz, 2, 0.2, 0.2, 0.2, 0.1);
            level.sendParticles(ParticleTypes.FLAME, px, pos.y + 1, pz, 3, 0.1, 0.1, 0.1, 0.05);
        }
    }
}