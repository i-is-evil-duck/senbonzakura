package com.j3ly.senbonzakura.entity.worm;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/** Flying serpent: phantom-style circling and swoops with a trailing worm body. */
public class SkySerpentEntity extends WormEntity {
    public SkySerpentEntity(EntityType<? extends WormEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public WormVariant variant() {
        return WormVariant.SKY;
    }
}
