package com.j3ly.senbonzakura.item;

import com.j3ly.senbonzakura.ModGameRules;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
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

public class RiteOfCommandmentsItem extends SwordItem {

    private static final Map<UUID, Integer> chargeTicks = new HashMap<>();
    private static final Map<UUID, Boolean> isCharging = new HashMap<>();
    private static final Map<UUID, Long> cooldowns = new HashMap<>();
    private static final int CHARGE_TIME = 10;
    private static final long COOLDOWN_TIME = 240L;

    public RiteOfCommandmentsItem(Properties properties) {
        super(Tiers.DIAMOND, properties.attributes(SwordItem.createAttributes(Tiers.DIAMOND, 3, -2.4f)));
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
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingUseDuration) {
        if (!(entity instanceof Player player)) return;
        if (!(level instanceof ServerLevel serverLevel)) return;

        UUID id = player.getUUID();
        if (!Boolean.TRUE.equals(isCharging.get(id))) return;

        int ticks = chargeTicks.getOrDefault(id, 0) + 1;
        chargeTicks.put(id, ticks);

        if (ticks % 3 == 0) {
            Vec3 pos = player.position();
            serverLevel.sendParticles(ParticleTypes.WHITE_ASH, pos.x + (Math.random() - 0.5), pos.y + 1, pos.z + (Math.random() - 0.5), 1, 0.1, 0.1, 0.1, 0.02);
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
            performLightningStrike(player, serverLevel);
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
            performLightningStrike(player, serverLevel);
        }
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    private void performLightningStrike(Player player, ServerLevel level) {
        Vec3 pos = player.position();
        level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.TRIDENT_THUNDER, SoundSource.PLAYERS, 1.0f, 1.0f);

        AABB area = new AABB(pos.x - 10, pos.y - 5, pos.z - 10, pos.x + 10, pos.y + 10, pos.z + 10);
        List<Entity> entities = level.getEntities(player, area, e -> e instanceof LivingEntity && e != player && !(e instanceof net.minecraft.world.entity.monster.Creeper));

        int boltCount = entities.size() <= 3 ? 2 : 1;

        for (Entity target : entities) {
            for (int i = 0; i < boltCount; i++) {
                LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
                if (bolt != null) {
                    bolt.moveTo(target.getX(), target.getY(), target.getZ());
                    level.addFreshEntity(bolt);
                }
            }

            if (target instanceof LivingEntity living) {
                living.hurt(level.damageSources().lightningBolt(), 5.0f);
            }
        }

        for (int i = 0; i < 20; i++) {
            double angle = Math.random() * Math.PI * 2;
            double dist = Math.random() * 10;
            double px = pos.x + Math.cos(angle) * dist;
            double pz = pos.z + Math.sin(angle) * dist;
            level.sendParticles(ParticleTypes.WHITE_ASH, px, pos.y + 0.5, pz, 1, 0.05, 0.1, 0.05, 0.01);
        }
    }
}
