package com.j3ly.senbonzakura.entity;

import com.j3ly.senbonzakura.DuckCompat;
import com.j3ly.senbonzakura.SenbonzakuraMod;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * A duck projectile that deals armor-bypassing magic damage. When Ducky's Duck
 * Mod is installed, a random duck spawns at the impact point and dies there
 * without dropping anything. Otherwise the original duck-sprite projectile
 * effect is used unchanged.
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
        spawnDuck(result.getLocation());
        // Deliberately skip vanilla snowball damage and effects.
        discard();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        spawnDuck(result.getLocation());
        super.onHitBlock(result);
    }

    private void spawnDuck(Vec3 impact) {
        Level level = level();
        EntityType<?> type = DuckCompat.randomDuck(level.registryAccess(), level.getRandom());
        if (type == null) return;
        Entity spawned = type.create(level);
        if (!(spawned instanceof LivingEntity duck)) return;
        duck.setPos(impact.x, impact.y, impact.z);
        duck.getPersistentData().putBoolean(DuckCompat.NO_DROPS_KEY, true);
        level.addFreshEntity(duck);
        duck.hurt(level.damageSources().magic(), 1000.0f);
    }
}
