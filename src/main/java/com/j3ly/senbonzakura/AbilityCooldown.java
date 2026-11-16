package com.j3ly.senbonzakura;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

public final class AbilityCooldown {
    private static final String END = "senbonzakura_cooldown_end";
    private static final String TOTAL = "senbonzakura_cooldown_total";

    private AbilityCooldown() {}

    public static void set(ItemStack stack, Level level, long cooldownTicks) {
        start(stack, level, cooldownTicks);
    }

    public static void start(ItemStack stack, Level level, long cooldownTicks) {
        if (cooldownTicks <= 0) return;
        long now = level.getGameTime();
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
            tag.putLong(END, now + cooldownTicks);
            tag.putLong(TOTAL, cooldownTicks);
        });
    }

    public static boolean isReady(ItemStack stack, Level level) {
        return remainingTicks(stack, level) <= 0;
    }

    public static long remainingTicks(ItemStack stack, Level level) {
        long now = level != null ? level.getGameTime() : currentGameTime();
        return remainingTicksAt(stack, now);
    }

    public static boolean isVisible(ItemStack stack) {
        return remainingTicks(stack, null) > 0;
    }

    public static int getBarWidth(ItemStack stack) {
        long remaining = remainingTicks(stack, null);
        long total = totalTicks(stack);
        if (remaining <= 0 || total <= 0) return 0;
        // Full bar means ready (no cooldown shown); as cooldown progresses, bar fills up.
        long elapsed = total - remaining;
        int width = Math.round(13.0f * (float) elapsed / (float) total);
        if (width < 0) width = 0;
        if (width > 13) width = 13;
        return width;
    }

    public static int getBarColor(ItemStack stack) {
        long remaining = remainingTicks(stack, null);
        long total = totalTicks(stack);
        if (remaining <= 0 || total <= 0) return 0x00FF00;
        float progress = (float) (total - remaining) / (float) total; // 0 -> 1
        if (progress < 0.5f) return 0xFF5555; // recharging
        if (progress < 0.85f) return 0xFFAA00;
        return 0x55FF55; // almost ready
    }

    private static long remainingTicksAt(ItemStack stack, long now) {
        if (now < 0) return 0;
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) return 0;
        CompoundTag tag = data.copyTag();
        if (!tag.contains(END)) return 0;
        long end = tag.getLong(END);
        long rem = end - now;
        return Math.max(0, rem);
    }

    private static long totalTicks(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) return 0;
        CompoundTag tag = data.copyTag();
        return tag.contains(TOTAL) ? tag.getLong(TOTAL) : 0;
    }

    private static long currentGameTime() {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            return ClientGameTime.now();
        }
        return -1;
    }

    private static class ClientGameTime {
        static long now() {
            try {
                net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
                if (mc.level != null) return mc.level.getGameTime();
            } catch (Throwable ignored) {}
            return -1;
        }
    }
}
