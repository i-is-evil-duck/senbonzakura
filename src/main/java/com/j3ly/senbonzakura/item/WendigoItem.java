package com.j3ly.senbonzakura.item;

import com.j3ly.senbonzakura.ModGameRules;
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
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
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

        for (int i = 0; i < 32; i++) {
            double spreadH = (player.getRandom().nextDouble() - 0.5) * 0.35; // horizontal spread
            double spreadV = (player.getRandom().nextDouble() - 0.5) * 0.25; // vertical spread
            Vec3 dir = look.add(right.scale(spreadH)).add(realUp.scale(spreadV)).normalize();
            Arrow arrow = new Arrow(level, player, new ItemStack(net.minecraft.world.item.Items.ARROW), stack);
            arrow.setPos(eye.x + dir.x * 0.5, eye.y + dir.y * 0.5, eye.z + dir.z * 0.5);
            arrow.shoot(dir.x, dir.y, dir.z, 3.15f, 1.0f); // crossbow-like speed
            arrow.pickup = AbstractArrow.Pickup.DISALLOWED;
            level.addFreshEntity(arrow);
        }

        return InteractionResultHolder.consume(stack);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 0)); // 2 seconds slowness
        return super.hurtEnemy(stack, target, attacker);
    }
}
