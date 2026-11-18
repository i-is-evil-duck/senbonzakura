package com.j3ly.senbonzakura;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.neoforged.fml.ModList;

import java.util.ArrayList;
import java.util.List;

/**
 * Optional integration with Ducky's Duck Mod. The duck types are looked up from
 * the registry by name so this mod never needs a compile-time dependency on it.
 */
public final class DuckCompat {
    public static final String DUCK_MOD_ID = "duckys_duck_mod";
    /** Persistent-data flag used to suppress loot from wand-spawned ducks. */
    public static final String NO_DROPS_KEY = "senbonzakura_no_drops";

    private static final List<ResourceLocation> DUCK_IDS = List.of(
            id("mallard_duck"), id("white_duck"), id("yellow_duck"));

    private DuckCompat() {
    }

    public static boolean available() {
        return ModList.get().isLoaded(DUCK_MOD_ID);
    }

    /** Returns a random duck type from the duck mod, or null if it is unavailable. */
    public static EntityType<?> randomDuck(RegistryAccess access, RandomSource random) {
        if (!available()) return null;
        Registry<EntityType<?>> registry = access.registryOrThrow(Registries.ENTITY_TYPE);
        List<EntityType<?>> present = new ArrayList<>();
        for (ResourceLocation id : DUCK_IDS) {
            EntityType<?> type = registry.get(id);
            if (type != null) present.add(type);
        }
        if (present.isEmpty()) return null;
        return present.get(random.nextInt(present.size()));
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(DUCK_MOD_ID, path);
    }
}
