package com.j3ly.senbonzakura.entity;

import com.j3ly.senbonzakura.SenbonzakuraMod;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

/**
 * A snowball-shaped projectile that renders as a duck item sprite.
 * Uses magic damage so armor is bypassed entirely.
 */
public class DuckProjectile extends Snowball {
    /** 3 hearts of damage, unaffected by armor. */
    public static final float DAMAGE = 6.0f;

    public DuckProjectile(Level level, LivingEntity owner) {
        super(level, owner);
        setItem(new ItemStack(SenbonzakuraMod.DUCK_PROJECTILE.get()));
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity target = result.getEntity();
        if (target instanceof LivingEntity living) {
            Entity owner = getOwner();
            DamageSource source = owner != null
                    ? living.level().damageSources().indirectMagic(this, owner)
                    : living.level().damageSources().magic();
            living.hurt(source, DAMAGE);
        }
        // Deliberately skip vanilla snowball damage and effects.
        discard();
    }
}
