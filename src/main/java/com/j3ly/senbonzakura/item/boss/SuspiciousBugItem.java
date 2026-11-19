package com.j3ly.senbonzakura.item.boss;

import com.j3ly.senbonzakura.entity.ModEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.Item;

import java.util.Set;

/**
 * Suspicious Bug — one-time-use summon item for Queen Spider. Crafted with 4
 * wool, 4 fermented spider eyes, 1 potion of infestation. Only works in the
 * Overworld.
 */
public class SuspiciousBugItem extends BossSummonItem {
    public SuspiciousBugItem(Item.Properties properties) {
        super(
                (EntityType<? extends LivingEntity>) ModEntities.QUEEN_SPIDER.get(),
                true,
                Set.of(Level.OVERWORLD),
                properties
        );
    }
}
