package com.j3ly.senbonzakura.entity;

import com.j3ly.senbonzakura.SenbonzakuraMod;
import com.j3ly.senbonzakura.entity.boss.KingSlimeEntity;
import com.j3ly.senbonzakura.entity.boss.QueenSpiderEntity;
import com.j3ly.senbonzakura.entity.boss.SkeletonGeneralEntity;
import com.j3ly.senbonzakura.entity.boss.ZombieGeneralEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Registers all custom boss entities. */
public final class ModEntities {
    private ModEntities() {
    }

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, SenbonzakuraMod.MOD_ID);

    /** King Slime — pre-iron tier boss, 44 HP (22 hearts), vanilla slime reskin. */
    public static final DeferredHolder<EntityType<?>, EntityType<KingSlimeEntity>> KING_SLIME =
            ENTITY_TYPES.register("king_slime", () -> EntityType.Builder
                    .<KingSlimeEntity>of(KingSlimeEntity::new, MobCategory.MONSTER)
                    .sized(2.3F, 2.3F)
                    .clientTrackingRange(10)
                    .build("king_slime"));

    /** Zombie General — pre-iron tier boss, 80 HP (40 hearts), vanilla zombie reskin. */
    public static final DeferredHolder<EntityType<?>, EntityType<ZombieGeneralEntity>> ZOMBIE_GENERAL =
            ENTITY_TYPES.register("zombie_general", () -> EntityType.Builder
                    .<ZombieGeneralEntity>of(ZombieGeneralEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(10)
                    .build("zombie_general"));

    /** Skeleton General — pre-iron tier boss, 80 HP (40 hearts), vanilla skeleton reskin. */
    public static final DeferredHolder<EntityType<?>, EntityType<SkeletonGeneralEntity>> SKELETON_GENERAL =
            ENTITY_TYPES.register("skeleton_general", () -> EntityType.Builder
                    .<SkeletonGeneralEntity>of(SkeletonGeneralEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.99F)
                    .clientTrackingRange(10)
                    .build("skeleton_general"));

    /** Queen Spider — pre-iron tier boss, 90 HP (45 hearts), vanilla spider reskin, 2x size. */
    public static final DeferredHolder<EntityType<?>, EntityType<QueenSpiderEntity>> QUEEN_SPIDER =
            ENTITY_TYPES.register("queen_spider", () -> EntityType.Builder
                    .<QueenSpiderEntity>of(QueenSpiderEntity::new, MobCategory.MONSTER)
                    .sized(2.8F, 1.8F)
                    .clientTrackingRange(10)
                    .build("queen_spider"));
}
