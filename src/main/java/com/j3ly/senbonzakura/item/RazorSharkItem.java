package com.j3ly.senbonzakura.item;

import com.j3ly.senbonzakura.AbilityCooldown;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class RazorSharkItem extends AxeItem {

    private static final Map<UUID, Integer> chargeTicks = new HashMap<>();
    private static final Map<UUID, Boolean> isCharging = new HashMap<>();
    private static final int CHARGE_TIME = 30; // 1.5 seconds
    private static final long COOLDOWN_TIME = 180L; // 9 seconds

    public RazorSharkItem(Properties properties) {
        super(Tiers.NETHERITE, properties.attributes(AxeItem.createAttributes(Tiers.NETHERITE, 8, -3.0f))); // 13 ATK
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
            Vec3 look = player.getLookAngle();
            Vec3 start = player.getEyePosition().add(look.scale(0.5));
            serverLevel.sendParticles(ParticleTypes.BUBBLE, start.x, start.y, start.z, 2, 0.05, 0.05, 0.05, 0.01);
        }

        if (ticks >= CHARGE_TIME) {
            player.releaseUsingItem();
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        fireBeam(stack, level, entity);
        return stack;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeCharged) {
        fireBeam(stack, level, entity);
    }

    private void fireBeam(ItemStack stack, Level level, LivingEntity entity) {
        if (!(entity instanceof Player player)) return;
        if (!(level instanceof ServerLevel serverLevel)) return;

        UUID id = player.getUUID();
        int ticks = chargeTicks.getOrDefault(id, 0);
        isCharging.put(id, false);
        chargeTicks.put(id, 0);

        if (ticks < CHARGE_TIME) return;
        if (!ModGameRules.tryConsumeAbilityXp(player, serverLevel)) return;
        AbilityCooldown.start(stack, serverLevel, COOLDOWN_TIME);

        Vec3 start = player.getEyePosition();
        Vec3 look = player.getLookAngle().normalize();
        double range = 35.0;

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_INSIDE, SoundSource.PLAYERS, 1.2f, 1.2f);

        Set<Integer> hitIds = new HashSet<>();
        for (double d = 0; d <= range; d += 0.5) {
            Vec3 pos = start.add(look.scale(d));
            serverLevel.sendParticles(ParticleTypes.BUBBLE, pos.x, pos.y, pos.z, 1, 0.05, 0.05, 0.05, 0.0);
            if (d % 2 == 0) {
                serverLevel.sendParticles(ParticleTypes.BUBBLE_POP, pos.x, pos.y, pos.z, 1, 0.02, 0.02, 0.02, 0.0);
            }

            AABB box = new AABB(pos.x - 0.8, pos.y - 0.8, pos.z - 0.8, pos.x + 0.8, pos.y + 0.8, pos.z + 0.8);
            List<Entity> targets = serverLevel.getEntities(player, box, e -> e instanceof LivingEntity && e != player);
            for (Entity t : targets) {
                if (!hitIds.add(t.getId())) continue;
                if (t instanceof LivingEntity living) {
                    living.hurt(serverLevel.damageSources().playerAttack(player), 10.0f);
                    Vec3 push = look.scale(1.4).add(0, 0.2, 0);
                    t.setDeltaMovement(t.getDeltaMovement().add(push));
                    t.hurtMarked = true;
                }
            }
        }
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (level.isClientSide) return;
        if (!(entity instanceof Player player)) return;
        if (!player.getInventory().contains(stack)) return;
        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 220, 0, false, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 220, 0, false, false, true));
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
