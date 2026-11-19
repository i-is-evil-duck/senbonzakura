package com.j3ly.senbonzakura.item.boss;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import java.util.Set;

/**
 * Base class for one-time-use boss summon items. On use (right-click in air or
 * on a block), consumes the stack and spawns the bound boss entity at the
 * player's position, if the current dimension is allowed.
 */
public abstract class BossSummonItem extends Item {
    /** Entity type of the boss this item summons. */
    private final EntityType<? extends LivingEntity> bossType;

    /** Whether this summon only works in specific dimensions. */
    private final boolean dimensionRestricted;

    /** Allowed dimension keys. Empty means any dimension. */
    private final Set<ResourceKey<Level>> allowedDimensions;

    protected BossSummonItem(
            EntityType<? extends LivingEntity> bossType,
            boolean dimensionRestricted,
            Set<ResourceKey<Level>> allowedDimensions,
            Properties properties) {
        super(properties);
        this.bossType = bossType;
        this.dimensionRestricted = dimensionRestricted;
        this.allowedDimensions = allowedDimensions;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            return InteractionResultHolder.success(stack);
        }
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResultHolder.pass(stack);
        }

        LivingEntity boss = bossType.create(serverLevel);
        if (boss == null) {
            return InteractionResultHolder.fail(stack);
        }
        boss.moveTo(player.getX(), player.getY() + 1.0, player.getZ(), player.getYRot(), 0.0F);
        serverLevel.addFreshEntity(boss);

        // Consume the summon item (one-time use).
        stack.shrink(1);

        serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                net.minecraft.sounds.SoundEvents.SLIME_ATTACK,
                net.minecraft.sounds.SoundSource.HOSTILE, 1.0F, 0.8F);

        return InteractionResultHolder.consume(stack);
    }
}
