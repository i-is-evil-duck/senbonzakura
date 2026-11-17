package com.j3ly.senbonzakura.item;

import com.j3ly.senbonzakura.AbilityCooldown;
import com.j3ly.senbonzakura.ModGameRules;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import com.j3ly.senbonzakura.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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

public class YukiNoAmeItem extends SwordItem {

    private static final Map<UUID, Integer> chargeTicks = new HashMap<>();
    private static final Map<UUID, Boolean> isCharging = new HashMap<>();
    private static final int CHARGE_TIME = 10;
    private static final long COOLDOWN_TIME = 100L;

    public YukiNoAmeItem(Properties properties) {
        super(Tiers.NETHERITE, properties.attributes(SwordItem.createAttributes(Tiers.NETHERITE, 7, -2.4f)));
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

        if (ticks % 3 == 0) {
            Vec3 pos = player.position().add(0, 1, 0);
            serverLevel.sendParticles(ParticleTypes.SNOWFLAKE, pos.x + (Math.random() - 0.5), pos.y + (Math.random() - 0.5), pos.z + (Math.random() - 0.5), 1, 0.1, 0.1, 0.1, 0.02);
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
            AbilityCooldown.start(stack, level, COOLDOWN_TIME);
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 140, 1, false, true, true)); // Swiftness II, 7s
            performDash(player, serverLevel);
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
            AbilityCooldown.start(stack, level, COOLDOWN_TIME);
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 140, 1, false, true, true)); // Swiftness II, 7s
            performDash(player, serverLevel);
        }
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    private void performDash(Player player, ServerLevel level) {
        Vec3 look = player.getLookAngle();
        Vec3 start = player.position();
        Vec3 end = start.add(look.x * 12, look.y * 12, look.z * 12);

        level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.YUKI_NO_AME_SWING.get(), SoundSource.PLAYERS, 1.0f, 1.0f);

        player.teleportTo(end.x, end.y, end.z);
        player.fallDistance = 0;

        for (int i = 0; i <= 12; i++) {
            Vec3 pos = start.add(look.x * i, look.y * i, look.z * i);
            AABB dashBox = new AABB(pos.x - 1.5, pos.y - 1.0, pos.z - 1.5, pos.x + 1.5, pos.y + 2.0, pos.z + 1.5);
            List<Entity> entities = level.getEntities(player, dashBox, e -> e instanceof LivingEntity && e != player);

            for (Entity target : entities) {
                if (target instanceof LivingEntity living) {
                    Vec3 pushDir = target.position().subtract(start).normalize();
                    if (pushDir.lengthSqr() < 0.001) {
                        pushDir = look;
                    }
                    target.setDeltaMovement(pushDir.x * 1.5, 0.5, pushDir.z * 1.5);
                    target.hurtMarked = true;

                    float damage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.5f + 4.0f;
                    living.hurt(level.damageSources().playerAttack(player), damage);

                    living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1));
                    living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 1));
                }
            }

            level.sendParticles(ParticleTypes.SNOWFLAKE, pos.x, pos.y + 1, pos.z, 1, 0.1, 0.1, 0.1, 0.02);
        }

        performSweepAttack(player, level);
    }

    private void performSweepAttack(Player player, ServerLevel level) {
        Vec3 pos = player.position();
        Vec3 look = player.getLookAngle();

        level.playSound(null, pos.x, pos.y, pos.z, ModSounds.YUKI_NO_AME_SWING.get(), SoundSource.PLAYERS, 1.0f, 1.0f);

        level.sendParticles(ParticleTypes.SNOWFLAKE, pos.x + look.x * 2, pos.y + 1, pos.z + look.z * 2, 5, 2.0, 0.5, 2.0, 0.1);

        AABB sweepBox = new AABB(pos.x - 4, pos.y - 1, pos.z - 4, pos.x + 4, pos.y + 3, pos.z + 4);

        List<Entity> entities = level.getEntities(player, sweepBox, e -> e instanceof LivingEntity && e != player);

        for (Entity target : entities) {
            if (target instanceof LivingEntity living) {
                Vec3 toTarget = target.position().subtract(pos).normalize();
                double dot = look.dot(toTarget);

                if (dot > -0.1) {
                    Vec3 knockback = toTarget.scale(0.8);
                    target.setDeltaMovement(knockback.x, 0.3, knockback.z);
                    target.hurtMarked = true;

                    float damage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
                    living.hurt(level.damageSources().playerAttack(player), damage);

                    level.sendParticles(ParticleTypes.SNOWFLAKE, target.getX(), target.getY() + 1, target.getZ(), 3, 0.2, 0.2, 0.2, 0.1);
                }
            }
        }
    }


    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof Player player && player.getAttackStrengthScale(0.5f) >= 0.9f) {
            // Fully charged hit: 25% chance to apply Slowness II + Weakness for 5 seconds
            if (player.getRandom().nextFloat() < 0.25f) {
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 1));
                target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 1));
            }
        }
        return super.hurtEnemy(stack, target, attacker);
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
