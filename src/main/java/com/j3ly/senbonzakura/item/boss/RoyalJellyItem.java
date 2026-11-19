package com.j3ly.senbonzakura.item.boss;

import com.j3ly.senbonzakura.entity.ModEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.Item;

import java.util.Set;

/**
 * Royal Jelly — one-time-use summon item for King Slime. Crafted with 1 honey
 * comb + 8 slime blocks. Only works in the Overworld.
 */
public class RoyalJellyItem extends BossSummonItem {
    public RoyalJellyItem(Item.Properties properties) {
        super(
                (EntityType<? extends LivingEntity>) ModEntities.KING_SLIME.get(),
                true,
                Set.of(Level.OVERWORLD),
                properties
        );
    }
}
