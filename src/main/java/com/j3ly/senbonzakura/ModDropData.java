package com.j3ly.senbonzakura;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ModDropData extends SavedData {
    public record DropRate(String item, String source, int baseOneInN) {}

    private static final String DATA_NAME = SenbonzakuraMod.MOD_ID + "_drop_data";

    public static final List<DropRate> DROP_RATES = List.of(
            new DropRate("Exalted Blossom", "Cherry Blossom Leaves", 7500),
            new DropRate("Ancient Amber", "Breaking/stripping logs with an axe", 900),
            new DropRate("Deepwoken Relic", "Zombies, Skeletons, Spiders, Creepers, Endermen", 6000),
            new DropRate("Sinners Ash", "Ender Dragon, Wither, Warden", 30),
            new DropRate("Skystone", "Phantoms, Breeze", 300),
            new DropRate("Cindering Core", "Ghast, Blaze", 300),
            new DropRate("Inky Pearl", "Elder Guardian", 90),
            new DropRate("Inky Pearl", "Guardian, Drowned", 900),
            new DropRate("Darkened Rubble", "Zombified Piglin (Nether)", 450)
    );

    private final Map<UUID, Integer> dropPercents = new HashMap<>();
    private double globalMultiplier = 1.0;

    public static final SavedData.Factory<ModDropData> FACTORY = new SavedData.Factory<>(
            ModDropData::new,
            ModDropData::load,
            DataFixTypes.LEVEL
    );

    public ModDropData() {
    }

    public static ModDropData load(CompoundTag tag, HolderLookup.Provider registries) {
        ModDropData data = new ModDropData();
        data.globalMultiplier = tag.contains("globalMultiplier") ? tag.getDouble("globalMultiplier") : 1.0;
        if (data.globalMultiplier <= 0) data.globalMultiplier = 1.0;
        ListTag list = tag.getList("players", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            if (!entry.hasUUID("uuid")) continue;
            UUID uuid = entry.getUUID("uuid");
            int pct = Math.max(0, Math.min(100, entry.getInt("percent")));
            data.dropPercents.put(uuid, pct);
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putDouble("globalMultiplier", globalMultiplier);
        ListTag list = new ListTag();
        for (Map.Entry<UUID, Integer> e : dropPercents.entrySet()) {
            CompoundTag entry = new CompoundTag();
            entry.putUUID("uuid", e.getKey());
            entry.putInt("percent", e.getValue());
            list.add(entry);
        }
        tag.put("players", list);
        return tag;
    }

    public double getGlobalMultiplier() {
        return globalMultiplier;
    }

    public void setGlobalMultiplier(double multiplier) {
        if (multiplier <= 0) multiplier = 0.0001;
        this.globalMultiplier = multiplier;
        setDirty();
    }

    public int getPercent(UUID uuid) {
        return dropPercents.getOrDefault(uuid, 100);
    }

    public void setPercent(UUID uuid, int percent) {
        int clamped = Math.max(0, Math.min(100, percent));
        if (clamped == 100) {
            dropPercents.remove(uuid);
        } else {
            dropPercents.put(uuid, clamped);
        }
        setDirty();
    }

    public static ModDropData get(ServerLevel level) {
        ServerLevel overworld = level.getServer().overworld();
        return overworld.getDataStorage().computeIfAbsent(FACTORY, DATA_NAME);
    }

    public static boolean canDropFor(ServerLevel level, UUID playerUuid) {
        if (playerUuid == null) return true;
        int pct = get(level).getPercent(playerUuid);
        if (pct >= 100) return true;
        if (pct <= 0) return false;
        return level.getRandom().nextInt(100) < pct;
    }

    public static boolean rollDrop(ServerLevel level, UUID playerUuid, int baseOneInN) {
        ModDropData data = get(level);
        if (playerUuid != null) {
            int pct = data.getPercent(playerUuid);
            if (pct <= 0) return false;
            if (pct < 100 && level.getRandom().nextInt(100) >= pct) return false;
        }
        double chance = data.globalMultiplier / (double) baseOneInN;
        if (chance >= 1.0) return true;
        if (chance <= 0.0) return false;
        return level.getRandom().nextDouble() < chance;
    }

    public static String formatRate(int baseOneInN, double multiplier) {
        double chance = multiplier / (double) baseOneInN;
        double oneIn = chance <= 0 ? Double.POSITIVE_INFINITY : (1.0 / chance);
        if (Double.isInfinite(oneIn)) return "1/∞";
        if (oneIn < 10) {
            return String.format("1/%.2f (%.2f%%)", oneIn, chance * 100.0);
        } else if (oneIn < 1000) {
            return String.format("1/%.1f (%.4f%%)", oneIn, chance * 100.0);
        } else {
            return String.format("1/%.0f (%.6f%%)", oneIn, chance * 100.0);
        }
    }

    public static List<String> describeRates(ServerLevel level) {
        ModDropData data = get(level);
        double m = data.globalMultiplier;
        List<String> lines = new ArrayList<>();
        lines.add("Global drop rate multiplier: " + trimDouble(m) + "x");
        for (DropRate r : DROP_RATES) {
            lines.add("- " + r.item() + " (" + r.source() + "): " + formatRate(r.baseOneInN(), m));
        }
        return lines;
    }

    private static String trimDouble(double v) {
        if (v == (long) v) return String.format("%d", (long) v);
        return String.format("%s", v);
    }
}
