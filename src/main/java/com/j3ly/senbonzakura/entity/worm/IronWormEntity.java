package com.j3ly.senbonzakura.entity.worm;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/** The original iron-plated ground worm. */
public class IronWormEntity extends WormEntity {
    public IronWormEntity(EntityType<? extends WormEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public WormVariant variant() {
        return WormVariant.IRON;
    }
}
