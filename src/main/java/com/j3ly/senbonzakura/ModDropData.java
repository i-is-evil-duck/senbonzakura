package com.j3ly.senbonzakura;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ModDropData extends SavedData {
    private static final String DATA_NAME = SenbonzakuraMod.MOD_ID + "_drop_data";
    private final Map<UUID, Integer> dropPercents = new HashMap<>();

    public static final SavedData.Factory<ModDropData> FACTORY = new SavedData.Factory<>(
            ModDropData::new,
            ModDropData::load,
            DataFixTypes.LEVEL
    );

    public ModDropData() {
    }

    public static ModDropData load(CompoundTag tag, HolderLookup.Provider registries) {
        ModDropData data = new ModDropData();
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
}
