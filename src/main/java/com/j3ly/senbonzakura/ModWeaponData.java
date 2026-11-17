package com.j3ly.senbonzakura;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;

/**
 * World-persisted tuning for weapon abilities that operators can change at runtime.
 */
public class ModWeaponData extends SavedData {
    private static final String DATA_NAME = SenbonzakuraMod.MOD_ID + "_weapon_data";
    private static final String WENDIGO_ARROW_MULTIPLIER_KEY = "wendigoArrowDamageMultiplier";

    private double wendigoArrowDamageMultiplier = 1.0;

    public static final SavedData.Factory<ModWeaponData> FACTORY = new SavedData.Factory<>(
            ModWeaponData::new,
            ModWeaponData::load,
            DataFixTypes.LEVEL
    );

    public ModWeaponData() {
    }

    public static ModWeaponData load(CompoundTag tag, HolderLookup.Provider registries) {
        ModWeaponData data = new ModWeaponData();
        data.wendigoArrowDamageMultiplier = sanitize(
                tag.contains(WENDIGO_ARROW_MULTIPLIER_KEY)
                        ? tag.getDouble(WENDIGO_ARROW_MULTIPLIER_KEY)
                        : 1.0);
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putDouble(WENDIGO_ARROW_MULTIPLIER_KEY, wendigoArrowDamageMultiplier);
        return tag;
    }

    public double getWendigoArrowDamageMultiplier() {
        return wendigoArrowDamageMultiplier;
    }

    public void setWendigoArrowDamageMultiplier(double multiplier) {
        wendigoArrowDamageMultiplier = sanitize(multiplier);
        setDirty();
    }

    private static double sanitize(double multiplier) {
        if (!Double.isFinite(multiplier) || multiplier <= 0) return 1.0;
        return Math.min(1000.0, multiplier);
    }

    public static ModWeaponData get(ServerLevel level) {
        ServerLevel overworld = level.getServer().overworld();
        return overworld.getDataStorage().computeIfAbsent(FACTORY, DATA_NAME);
    }
}
