package com.j3ly.senbonzakura.item;

import com.j3ly.senbonzakura.ModGameRules;
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

public class SentenceItem extends AxeItem {

    private static final Map<UUID, Integer> chargeTicks = new HashMap<>();
    private static final Map<UUID, Boolean> isCharging = new HashMap<>();
    private static final Map<UUID, Long> cooldowns = new HashMap<>();
    private static final int CHARGE_TIME = 30; // 1.5 seconds
    private static final long COOLDOWN_TIME = 180L; // 9 seconds

    public SentenceItem(Properties properties) {
        super(Tiers.NETHERITE, properties.attributes(AxeItem.createAttributes(Tiers.NETHERITE, 8, -3.0f))); // 13 ATK
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

        if (ticks % 5 == 0) {
            Vec3 pos = player.position().add(0, 1, 0);
            serverLevel.sendParticles(ParticleTypes.DAMAGE_INDICATOR, pos.x, pos.y, pos.z, 1, 0.2, 0.2, 0.2, 0.0);
        }

        if (ticks >= CHARGE_TIME) {
            player.releaseUsingItem();
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        doSwing(stack, level, entity);
        return stack;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeCharged) {
        doSwing(stack, level, entity);
    }

    private void doSwing(ItemStack stack, Level level, LivingEntity entity) {
        if (!(entity instanceof Player player)) return;
        if (!(level instanceof ServerLevel serverLevel)) return;

        UUID id = player.getUUID();
        int ticks = chargeTicks.getOrDefault(id, 0);
        isCharging.put(id, false);
        chargeTicks.put(id, 0);

        if (ticks < CHARGE_TIME) return;
        if (!ModGameRules.tryConsumeAbilityXp(player, serverLevel)) return;
        cooldowns.put(id, level.getGameTime());

        Vec3 pos = player.position();
        Vec3 look = player.getLookAngle();

        level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.3f, 0.7f);

        double range = 6.0;
        AABB box = new AABB(pos.x - range, pos.y - 1, pos.z - range, pos.x + range, pos.y + 3, pos.z + range);
        List<Entity> targets = serverLevel.getEntities(player, box, e -> e instanceof LivingEntity && e != player);

        for (Entity t : targets) {
            if (!(t instanceof LivingEntity living)) continue;
            Vec3 toTarget = t.position().subtract(pos);
            Vec3 flat = new Vec3(toTarget.x, 0, toTarget.z);
            if (flat.lengthSqr() < 0.0001) flat = look;
            double dist = flat.length();
            if (dist > range) continue;
            Vec3 dir = flat.normalize();
            Vec3 lookFlat = new Vec3(look.x, 0, look.z).normalize();
            double dot = lookFlat.dot(dir);
            if (dot < 0.0) continue; // roughly 180 degrees in front

            living.hurt(serverLevel.damageSources().playerAttack(player), 12.0f);
            Vec3 knock = dir.scale(1.6).add(0, 0.35, 0);
            living.setDeltaMovement(living.getDeltaMovement().add(knock));
            living.hurtMarked = true;
            living.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 100, 0));
            living.addEffect(new MobEffectInstance(MobEffects.WITHER, 100, 0));
            serverLevel.sendParticles(ParticleTypes.DAMAGE_INDICATOR, living.getX(), living.getY() + 1.0, living.getZ(), 4, 0.3, 0.3, 0.3, 0.1);
        }

        for (int i = -9; i <= 9; i++) {
            double angle = Math.atan2(look.z, look.x) + Math.toRadians(i * 10);
            double px = pos.x + Math.cos(angle) * (range * 0.8);
            double pz = pos.z + Math.sin(angle) * (range * 0.8);
            serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK, px, pos.y + 1.0, pz, 1, 0.0, 0.0, 0.0, 0.0);
        }
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }
}
