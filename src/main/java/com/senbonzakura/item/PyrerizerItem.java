package com.senbonzakura.item;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PyrerizerItem extends AxeItem {

    private static final Map<UUID, Integer> chargeTicks = new HashMap<>();
    private static final Map<UUID, Boolean> isCharging = new HashMap<>();
    private static final Map<UUID, Long> cooldowns = new HashMap<>();
    private static final int CHARGE_TIME = 10;
    private static final long COOLDOWN_TIME = 140L;

    public PyrerizerItem(Properties properties) {
        super(Tiers.NETHERITE, 8, -3.0f, properties);
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

        if (ticks % 2 == 0) {
            ServerLevel serverLevel = (ServerLevel) level;
            Vec3 pos = player.position();
            for (int i = 0; i < 3; i++) {
                double angle = Math.random() * Math.PI * 2;
                double dist = 0.5 + Math.random() * 0.5;
                double px = pos.x + Math.cos(angle) * dist;
                double pz = pos.z + Math.sin(angle) * dist;
                serverLevel.sendParticles(ParticleTypes.FLAME, px, pos.y + 0.5, pz, 1, 0.02, 0.05, 0.02, 0.01);
            }
        }

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
            cooldowns.put(id, level.getGameTime());
            performFireLeap(player, (ServerLevel) level);
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
            cooldowns.put(id, level.getGameTime());
            performFireLeap(player, (ServerLevel) level);
        }
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    private void performFireLeap(Player player, ServerLevel level) {
        Vec3 look = player.getLookAngle();
        Vec3 end = player.position().add(look.x * 8, look.y * 8 + 2, look.z * 8);

        player.teleportTo(end.x, end.y, end.z);
        player.fallDistance = 0;

        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.GHAST_SHOOT, SoundSource.PLAYERS, 1.0f, 1.0f);

        AABB explosionBox = new AABB(end.x - 5, end.y - 2, end.z - 5, end.x + 5, end.y + 4, end.z + 5);
        List<Entity> entities = level.getEntities(player, explosionBox, e -> e instanceof LivingEntity && e != player);

        for (Entity target : entities) {
            if (target instanceof LivingEntity living) {
                float damage = 13.0f;
                living.hurt(level.damageSources().playerAttack(player), damage);
                living.setRemainingFireTicks(100);
            }

            Vec3 pushDir = target.position().subtract(end).normalize();
            if (pushDir.lengthSqr() < 0.001) pushDir = look;
            target.setDeltaMovement(pushDir.x * 1.5, 0.8, pushDir.z * 1.5);
            target.hurtMarked = true;
        }

        for (int i = 0; i < 30; i++) {
            double angle = Math.random() * Math.PI * 2;
            double dist = Math.random() * 4;
            double px = end.x + Math.cos(angle) * dist;
            double pz = end.z + Math.sin(angle) * dist;
            level.sendParticles(ParticleTypes.FLAME, px, end.y + 0.5, pz, 2, 0.1, 0.1, 0.1, 0.05);
            level.sendParticles(ParticleTypes.LARGE_SMOKE, px, end.y + 0.5, pz, 1, 0.1, 0.1, 0.1, 0.02);
        }
    }
}