package com.j3ly.senbonzakura.item;

import com.j3ly.senbonzakura.AbilityCooldown;
import com.j3ly.senbonzakura.ModGameRules;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import com.j3ly.senbonzakura.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WendigoItem extends SwordItem {

    private static final long COOLDOWN_TIME = 200L; // 10 seconds
    private static final String ARROW_MARKER = "senbonzakura_wendigo_arrow";

    private static final Map<UUID, SpawnBatch> activeArrows = new HashMap<>();

    private static class SpawnBatch {
        final long expiresAt;
        final List<UUID> arrowIds;

        SpawnBatch(long expiresAt, List<UUID> arrowIds) {
            this.expiresAt = expiresAt;
            this.arrowIds = arrowIds;
        }
    }

    public WendigoItem(Properties properties) {
        super(Tiers.NETHERITE, properties.attributes(SwordItem.createAttributes(Tiers.NETHERITE, 10, -2.4f))); // 15 ATK
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) return InteractionResultHolder.success(stack);
        if (!(level instanceof ServerLevel serverLevel)) return InteractionResultHolder.pass(stack);

        UUID id = player.getUUID();
        if (!AbilityCooldown.isReady(stack, serverLevel)) {
            return InteractionResultHolder.fail(stack);
        }
        if (!ModGameRules.tryConsumeAbilityXp(player, serverLevel)) {
            return InteractionResultHolder.fail(stack);
        }
        AbilityCooldown.start(stack, serverLevel, COOLDOWN_TIME);

        int extraLevels = getEnchantmentLevel(serverLevel, stack, "senbonzakura", "increased_arrows");
        int power = getEnchantmentLevel(serverLevel, stack, "minecraft", "power");
        int punch = getEnchantmentLevel(serverLevel, stack, "minecraft", "punch");
        int flame = getEnchantmentLevel(serverLevel, stack, "minecraft", "flame");

        int arrowCount = 64 + (extraLevels * 32);

        Vec3 look = player.getLookAngle().normalize();
        Vec3 up = new Vec3(0, 1, 0);
        Vec3 right = look.cross(up);
        if (right.lengthSqr() < 1.0E-4) {
            right = new Vec3(1, 0, 0);
        } else {
            right = right.normalize();
        }
        Vec3 realUp = right.cross(look).normalize();
        Vec3 eye = player.getEyePosition();

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                ModSounds.WENDIGO_ARROWS.get(), SoundSource.PLAYERS, 1.2f, 1.0f);

        long expireAt = level.getGameTime() + COOLDOWN_TIME;
        List<UUID> spawned = new ArrayList<>(arrowCount);
        for (int i = 0; i < arrowCount; i++) {
            double spreadH = (player.getRandom().nextDouble() - 0.5) * 0.35;
            double spreadV = (player.getRandom().nextDouble() - 0.5) * 0.25;
            Vec3 dir = look.add(right.scale(spreadH)).add(realUp.scale(spreadV)).normalize();

            // Use a tipped-arrow pickup stack with permanent marker data; pickup remains DISALLOWED,
            // but if obtained by any means it can be identified and removed.
            ItemStack pickupStack = new ItemStack(Items.TIPPED_ARROW);
            pickupStack.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.WATER));
            CustomData.update(DataComponents.CUSTOM_DATA, pickupStack, tag -> {
                tag.putBoolean(ARROW_MARKER, true);
                tag.putUUID("senbonzakura_wendigo_owner", id);
                tag.putLong("senbonzakura_wendigo_expire", expireAt);
                tag.putString("senbonzakura_wendigo_id", UUID.randomUUID().toString());
            });

            Arrow arrow = new Arrow(level, player, pickupStack, stack);
            arrow.setOwner(player);
            arrow.setPos(eye.x + dir.x * 0.8, eye.y + dir.y * 0.8, eye.z + dir.z * 0.8);
            arrow.shoot(dir.x, dir.y, dir.z, 3.15f, 1.0f); // crossbow-like speed
            arrow.setCritArrow(true); // fully charged
            arrow.pickup = AbstractArrow.Pickup.DISALLOWED;

            // Tag entity for robust cleanup (works even if batch tracking is missed/dimension changes)
            arrow.addTag(ARROW_MARKER);
            arrow.getPersistentData().putLong("senbonzakura_wendigo_expire", expireAt);
            arrow.getPersistentData().putUUID("senbonzakura_wendigo_owner", id);

            if (power > 0) {
                arrow.setBaseDamage(arrow.getBaseDamage() + 0.5D * (double) power + 0.5D);
            }
            // Punch knockback API varies by mappings; skipped for compatibility.
            if (flame > 0) {
                arrow.igniteForSeconds(100);
            }

            level.addFreshEntity(arrow);
            spawned.add(arrow.getUUID());
        }

        activeArrows.put(id, new SpawnBatch(expireAt, spawned));

        return InteractionResultHolder.consume(stack);
    }

    private static int getEnchantmentLevel(ServerLevel level, ItemStack stack, String namespace, String path) {
        return level.registryAccess().lookup(Registries.ENCHANTMENT)
                .flatMap(reg -> reg.get(ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(namespace, path))))
                .map(holder -> stack.getEnchantmentLevel(holder))
                .orElse(0);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 0)); // 2 seconds slowness
        return super.hurtEnemy(stack, target, attacker);
    }

    public static void tickCleanup(ServerLevel level, UUID playerId, long gameTime) {
        // Fast path: cleanup known batch for this player in this level
        SpawnBatch batch = activeArrows.get(playerId);
        if (batch != null && gameTime >= batch.expiresAt) {
            for (UUID arrowId : batch.arrowIds) {
                Entity e = level.getEntity(arrowId);
                if (e instanceof AbstractArrow arrow && arrow.isAlive()) {
                    arrow.discard();
                }
            }
            activeArrows.remove(playerId);
        }

        // Robust fallback: remove any expired tagged Wendigo arrows in this level
        cleanupExpiredTagged(level, gameTime);
    }

    public static void cleanupExpiredTagged(ServerLevel level, long gameTime) {
        List<AbstractArrow> toDiscard = new ArrayList<>();
        for (Entity e : level.getAllEntities()) {
            if (!(e instanceof AbstractArrow arrow)) continue;
            if (!arrow.getTags().contains(ARROW_MARKER)) continue;
            if (!arrow.getPersistentData().contains("senbonzakura_wendigo_expire")) continue;
            long expire = arrow.getPersistentData().getLong("senbonzakura_wendigo_expire");
            if (gameTime >= expire) {
                toDiscard.add(arrow);
            }
        }
        for (AbstractArrow arrow : toDiscard) {
            if (arrow.isAlive()) {
                arrow.discard();
            }
        }
    }

    public static boolean isAbilityArrowStack(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) return false;
        CompoundTag tag = data.copyTag();
        return tag.getBoolean(ARROW_MARKER);
    }

    public static void removeAbilityArrowsFromInventory(Player player) {
        var inv = player.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (isAbilityArrowStack(stack)) {
                inv.setItem(i, ItemStack.EMPTY);
            }
        }
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
