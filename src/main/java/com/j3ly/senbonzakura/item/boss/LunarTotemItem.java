package com.j3ly.senbonzakura.item.boss;

import com.j3ly.senbonzakura.entity.ModEntities;
import com.j3ly.senbonzakura.entity.boss.WerewolfTribeChiefEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.Item;

import java.util.Set;

/**
 * Lunar Howl Totem — one-time-use summon item for Werewolf Tribe Chief.
 * Crafted with golden carrots and rabbit hide. Only works in the Overworld.
 */
public class LunarTotemItem extends BossSummonItem {
    public LunarTotemItem(Item.Properties properties) {
        super(
                (EntityType<? extends LivingEntity>) ModEntities.WEREWOLF_TRIBE_CHIEF.get(),
                true,
                Set.of(Level.OVERWORLD),
                properties
        );
    }
}
