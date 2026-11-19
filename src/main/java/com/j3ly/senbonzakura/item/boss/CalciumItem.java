package com.j3ly.senbonzakura.item.boss;

import com.j3ly.senbonzakura.entity.ModEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.Item;

import java.util.Set;

/**
 * Calcium — one-time-use summon item for Skeleton General. Crafted with 4 bone
 * blocks, 4 buckets of milk. Only works in the Overworld.
 */
public class CalciumItem extends BossSummonItem {
    public CalciumItem(Item.Properties properties) {
        super(
                (EntityType<? extends LivingEntity>) ModEntities.SKELETON_GENERAL.get(),
                true,
                Set.of(Level.OVERWORLD),
                properties
        );
    }
}
