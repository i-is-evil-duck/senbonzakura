package com.j3ly.senbonzakura.entity.worm;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/** Smaller, weaker overworld cousin of the iron worm. */
public class EarthWormEntity extends WormEntity {
    public EarthWormEntity(EntityType<? extends WormEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public WormVariant variant() {
        return WormVariant.EARTH;
    }
}
