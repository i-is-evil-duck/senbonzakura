package com.j3ly.senbonzakura.entity;

import com.j3ly.senbonzakura.SenbonzakuraMod;
import com.j3ly.senbonzakura.entity.boss.KingSlimeEntity;
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
}
