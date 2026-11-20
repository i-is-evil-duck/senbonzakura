package com.j3ly.senbonzakura.item.boss;

import com.j3ly.senbonzakura.entity.ModEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.Item;

import java.util.Set;

/**
 * Storm Caller Charm — one-time-use summon item for Sky Serpent. Crafted with
 * phantom membranes and feathers. Only works in the Overworld at high Y.
 */
public class StormTotemItem extends BossSummonItem {
    public StormTotemItem(Item.Properties properties) {
        super(
                (EntityType<? extends LivingEntity>) ModEntities.SKY_SERPENT.get(),
                true,
                Set.of(Level.OVERWORLD),
                properties
        );
    }
}
