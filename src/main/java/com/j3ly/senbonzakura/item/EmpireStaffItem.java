package com.j3ly.senbonzakura.item;

import com.j3ly.senbonzakura.AbilityCooldown;
import com.j3ly.senbonzakura.ModGameRules;
import com.j3ly.senbonzakura.SenbonzakuraMod;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import com.j3ly.senbonzakura.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EmpireStaffItem extends SwordItem {

    private static final Map<UUID, Integer> chargeTicks = new HashMap<>();
    private static final Map<UUID, Boolean> isCharging = new HashMap<>();
    private static final int CHARGE_TIME = 40; // 2 seconds
    private static final long COOLDOWN_TIME = 300L; // 15 seconds

    private static final List<net.minecraft.core.Holder<MobEffect>> RANDOM_EFFECTS = List.of(
            // Good
            MobEffects.MOVEMENT_SPEED,
            MobEffects.DIG_SPEED,
            MobEffects.DAMAGE_BOOST,
            MobEffects.HEAL,
            MobEffects.JUMP,
            MobEffects.REGENERATION,
            MobEffects.DAMAGE_RESISTANCE,
            MobEffects.FIRE_RESISTANCE,
            MobEffects.WATER_BREATHING,
            MobEffects.INVISIBILITY,
            MobEffects.NIGHT_VISION,
            MobEffects.HEALTH_BOOST,
            MobEffects.ABSORPTION,
            MobEffects.SATURATION,
            MobEffects.LUCK,
            MobEffects.SLOW_FALLING,
            // Bad
            MobEffects.MOVEMENT_SLOWDOWN,
            MobEffects.DIG_SLOWDOWN,
            MobEffects.CONFUSION,
            MobEffects.WEAKNESS,
            MobEffects.POISON,
            MobEffects.WITHER,
            MobEffects.BLINDNESS,
            MobEffects.HUNGER,
            MobEffects.LEVITATION,
            MobEffects.UNLUCK,
            MobEffects.DARKNESS,
            SenbonzakuraMod.CINDERING
    );

    public EmpireStaffItem(Properties properties) {
        super(Tiers.DIAMOND, properties.attributes(SwordItem.createAttributes(Tiers.DIAMOND, 9, -2.6f))); // 12 ATK, 1.6 speed
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            UUID id = player.getUUID();
            if (!AbilityCooldown.isReady(stack, level)) {
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

        if (ticks % 4 == 0) {
            Vec3 pos = player.position().add(0, 1.2, 0);
            double angle = ticks * 0.2;
            double radius = 1.0;
            double px = pos.x + Math.cos(angle) * radius;
            double pz = pos.z + Math.sin(angle) * radius;
            serverLevel.sendParticles(ParticleTypes.END_ROD, px, pos.y, pz, 1, 0.01, 0.05, 0.01, 0.01);
            serverLevel.sendParticles(ParticleTypes.ENCHANT, px, pos.y, pz, 1, 0.1, 0.1, 0.1, 0.02);
        }

        if (ticks >= CHARGE_TIME) {
            player.releaseUsingItem();
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        triggerAbility(stack, level, entity);
        return stack;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeCharged) {
        triggerAbility(stack, level, entity);
    }

    private void triggerAbility(ItemStack stack, Level level, LivingEntity entity) {
        if (!(entity instanceof Player player)) return;
        if (!(level instanceof ServerLevel serverLevel)) return;

        UUID id = player.getUUID();
        int ticks = chargeTicks.getOrDefault(id, 0);
        isCharging.put(id, false);
        chargeTicks.put(id, 0);

        if (ticks < CHARGE_TIME) return;
        if (!ModGameRules.tryConsumeAbilityXp(player, serverLevel)) return;
        AbilityCooldown.start(stack, serverLevel, COOLDOWN_TIME);

        Vec3 pos = player.position();
        level.playSound(null, pos.x, pos.y, pos.z, ModSounds.EMPIRE_STAFF_CAST.get(), SoundSource.PLAYERS, 1.3f, 1.1f);
        serverLevel.sendParticles(ParticleTypes.FIREWORK, pos.x, pos.y + 1.2, pos.z, 40, 1.5, 0.8, 1.5, 0.05);

        List<LivingEntity> nearby = serverLevel.getEntitiesOfClass(
                LivingEntity.class,
                player.getBoundingBox().inflate(8.0),
                e -> e != player
        );

        for (int i = 0; i < 6; i++) {
            boolean nearEnemy = !nearby.isEmpty() && (i % 2 == 1);
            Vec3 spawn = nearEnemy
                    ? nearby.get(player.getRandom().nextInt(nearby.size())).position()
                    : pos;
            double ox = (player.getRandom().nextDouble() - 0.5) * 4.0;
            double oz = (player.getRandom().nextDouble() - 0.5) * 4.0;

            ThrownPotion potion = new ThrownPotion(level, player);
            potion.setPos(spawn.x + ox, spawn.y + 2.5, spawn.z + oz);
            potion.setItem(randomPotionStack(player));
            potion.shoot(0.0, -0.7, 0.0, 0.4f, 0.0f);
            level.addFreshEntity(potion);
        }
    }

    private static ItemStack randomPotionStack(Player player) {
        var effect = RANDOM_EFFECTS.get(player.getRandom().nextInt(RANDOM_EFFECTS.size()));
        PotionContents contents = new PotionContents(java.util.Optional.of(Potions.WATER), java.util.Optional.empty(),
                List.of(new MobEffectInstance(effect, 140, 0)));
        ItemStack stack = new ItemStack(net.minecraft.world.item.Items.SPLASH_POTION);
        stack.set(net.minecraft.core.component.DataComponents.POTION_CONTENTS, contents);
        return stack;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return AbilityCooldown.isVisible(stack);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return AbilityCooldown.getBarWidth(stack);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return AbilityCooldown.getBarColor(stack);
    }
}
