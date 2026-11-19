package com.j3ly.senbonzakura.item.boss;

import com.j3ly.senbonzakura.entity.ModEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.Item;

import java.util.Set;

/**
 * Putrid Bait — one-time-use summon item for Zombie General. Crafted with 1
 * gold block, 4 rotten flesh, 2 poisonous potato. Only works in the Overworld.
 */
public class PutridBaitItem extends BossSummonItem {
    public PutridBaitItem(Item.Properties properties) {
        super(
                (EntityType<? extends LivingEntity>) ModEntities.ZOMBIE_GENERAL.get(),
                true,
                Set.of(Level.OVERWORLD),
                properties
        );
    }
}
