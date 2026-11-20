package com.j3ly.senbonzakura.item.boss;

import com.j3ly.senbonzakura.entity.ModEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.Item;

import java.util.Set;

/**
 * Corroding Lure — one-time-use summon item for Iron Worm. Crafted with
 * 2 condensed slime, 4 forgotten insignia, 2 queen silk, 1 block of iron.
 * Only works in the Overworld.
 */
public class CorrodingLureItem extends BossSummonItem {
    public CorrodingLureItem(Item.Properties properties) {
        super(
                ModEntities.IRON_WORM.get(),
                true,
                Set.of(Level.OVERWORLD),
                properties
        );
    }
}
