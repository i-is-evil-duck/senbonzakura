package com.j3ly.senbonzakura.item;

import com.j3ly.senbonzakura.AbilityCooldown;
import com.j3ly.senbonzakura.ModGameRules;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WendigoItem extends SwordItem {

    private static final Map<UUID, Long> cooldowns = new HashMap<>();
    private static final long COOLDOWN_TIME = 200L; // 10 seconds

    public WendigoItem(Properties properties) {
        super(Tiers.NETHERITE, properties.attributes(SwordItem.createAttributes(Tiers.NETHERITE, 10, -2.4f))); // 15 ATK
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) return InteractionResultHolder.success(stack);
        if (!(level instanceof ServerLevel serverLevel)) return InteractionResultHolder.pass(stack);

        UUID id = player.getUUID();
        Long lastUse = cooldowns.get(id);
        if (lastUse != null && level.getGameTime() - lastUse < COOLDOWN_TIME) {
            return InteractionResultHolder.fail(stack);
        }
        if (!ModGameRules.tryConsumeAbilityXp(player, serverLevel)) {
            return InteractionResultHolder.fail(stack);
        }
        cooldowns.put(id, level.getGameTime());
        AbilityCooldown.set(stack, serverLevel, COOLDOWN_TIME);

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
                SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS, 1.2f, 1.0f);

        for (int i = 0; i < arrowCount; i++) {
            double spreadH = (player.getRandom().nextDouble() - 0.5) * 0.35;
            double spreadV = (player.getRandom().nextDouble() - 0.5) * 0.25;
            Vec3 dir = look.add(right.scale(spreadH)).add(realUp.scale(spreadV)).normalize();
            Arrow arrow = new Arrow(level, player, new ItemStack(Items.ARROW), stack);
            arrow.setPos(eye.x + dir.x * 0.5, eye.y + dir.y * 0.5, eye.z + dir.z * 0.5);
            arrow.shoot(dir.x, dir.y, dir.z, 3.15f, 1.0f);
            arrow.pickup = AbstractArrow.Pickup.DISALLOWED;

            if (power > 0) {
                arrow.setBaseDamage(arrow.getBaseDamage() + 0.5D * (double) power + 0.5D);
            }
            // Punch knockback API varies by mappings; skipped for compatibility.
            if (flame > 0) {
                arrow.igniteForSeconds(100);
            }

            level.addFreshEntity(arrow);
        }

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
