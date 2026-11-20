package com.j3ly.senbonzakura.entity.worm;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/** Larger, longer nether serpent with the same rear-up strike. */
public class HellSerpentEntity extends WormEntity {
    public HellSerpentEntity(EntityType<? extends WormEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public WormVariant variant() {
        return WormVariant.HELL;
    }
}
