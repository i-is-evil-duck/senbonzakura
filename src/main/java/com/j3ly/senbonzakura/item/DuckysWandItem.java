package com.j3ly.senbonzakura.item;

import com.j3ly.senbonzakura.AbilityCooldown;
import com.j3ly.senbonzakura.DuckyDomain;
import com.j3ly.senbonzakura.ModGameRules;
import com.j3ly.senbonzakura.ModSounds;
import com.j3ly.senbonzakura.entity.DuckProjectile;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 19 ATK wand. Left click fires 3-heart armor-bypassing ducks; right click opens Ducky Domain.
 */
public class DuckysWandItem extends SwordItem {
    /** 120 seconds. */
    private static final long COOLDOWN_TIME = 2400L;

    private static final Map<UUID, Boolean> wasSwinging = new HashMap<>();

    public DuckysWandItem(Properties properties) {
        // 1 (base) + 4 (netherite tier) + 14 = 19 ATK; -2.4 speed gives 1.6 swings/second.
        super(Tiers.NETHERITE, properties.attributes(SwordItem.createAttributes(Tiers.NETHERITE, 14, -2.4f)));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) return InteractionResultHolder.success(stack);
        if (!(level instanceof ServerLevel serverLevel)) return InteractionResultHolder.pass(stack);

        if (!AbilityCooldown.isReady(stack, serverLevel)) return InteractionResultHolder.fail(stack);
        if (DuckyDomain.isActive()) {
            player.displayClientMessage(Component.literal("A Ducky Domain is already active!"), true);
            return InteractionResultHolder.fail(stack);
        }
        if (!ModGameRules.tryConsumeAbilityXp(player, serverLevel)) return InteractionResultHolder.fail(stack);

        AbilityCooldown.start(stack, serverLevel, COOLDOWN_TIME);
        DuckyDomain.open(serverLevel, player);
        return InteractionResultHolder.consume(stack);
    }

    /** Called from the server player tick; one duck per left-click swing, including swings that miss. */
    public static void tickSwing(Player player, ServerLevel level) {
        UUID id = player.getUUID();
        boolean swinging = player.swinging && player.swingingArm == InteractionHand.MAIN_HAND;
        if (swinging && !wasSwinging.getOrDefault(id, false)) {
            fireDuck(level, player);
        }
        if (swinging) {
            wasSwinging.put(id, true);
        } else {
            wasSwinging.remove(id);
        }
    }

    public static void fireDuck(ServerLevel level, Player player) {
        Vec3 look = player.getLookAngle().normalize();
        Vec3 eye = player.getEyePosition();
        DuckProjectile duck = new DuckProjectile(level, player);
        duck.setPos(eye.x + look.x * 0.8, eye.y + look.y * 0.8, eye.z + look.z * 0.8);
        duck.shoot(look.x, look.y, look.z, 1.6f, 1.0f);
        level.addFreshEntity(duck);
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                ModSounds.DUCKYS_WAND_DUCK.get(), SoundSource.PLAYERS,
                0.8f, 0.9f + player.getRandom().nextFloat() * 0.2f);
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
