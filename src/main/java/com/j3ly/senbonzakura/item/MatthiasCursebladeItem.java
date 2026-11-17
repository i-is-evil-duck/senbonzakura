package com.j3ly.senbonzakura.item;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import com.j3ly.senbonzakura.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * Matthias' Curseblade - 9 ATK, 1.6 swing speed.
 *
 * <p>Passive: whenever a mob is defeated by a player holding this blade, a purple
 * blast damages nearby mobs only. Players are never hurt and blocks are never
 * affected (no vanilla explosion is created, so there is no terrain destruction).
 */
public class MatthiasCursebladeItem extends SwordItem {

    private static final float BLAST_RADIUS = 4.0F;
    private static final float BLAST_DAMAGE = 8.0F;

    public MatthiasCursebladeItem(Properties properties) {
        // Netherite bonus is 4, plus the 1.0 base player attack damage = 9 ATK.
        // Attack speed modifier -2.4 on the 4.0 base = 1.6 swings/second.
        super(Tiers.NETHERITE, properties.attributes(SwordItem.createAttributes(Tiers.NETHERITE, 4, -2.4f)));
    }

    /**
     * Called from {@code SenbonzakuraMod.onLivingDeath} when a mob is defeated by
     * a player holding the Curseblade.
     */
    public static void onVictimDefeated(ServerLevel level, LivingEntity victim, Player killer) {
        Vec3 pos = victim.position();

        level.playSound(null, pos.x, pos.y, pos.z,
                ModSounds.MATTHIAS_CURSEBLADE_BURST.get(), SoundSource.PLAYERS, 1.2f, 1.2f);
        level.sendParticles(ParticleTypes.PORTAL,
                pos.x, pos.y + 0.8, pos.z, 40, 1.2f, 1.0f, 1.2f, 0.15f);
        level.sendParticles(ParticleTypes.WITCH,
                pos.x, pos.y + 0.8, pos.z, 20, 1.0f, 0.8f, 1.0f, 0.08f);

        AABB area = new AABB(
                pos.x - BLAST_RADIUS, pos.y - BLAST_RADIUS, pos.z - BLAST_RADIUS,
                pos.x + BLAST_RADIUS, pos.y + BLAST_RADIUS, pos.z + BLAST_RADIUS);

        // Mob excludes Player, so players are never damaged. No level.explode()
        // call is made, so blocks are never destroyed.
        List<Entity> targets = level.getEntities(killer, area,
                e -> e instanceof Mob && e != victim && e.isAlive());

        for (Entity entity : targets) {
            double distance = entity.position().distanceTo(pos);
            if (distance > BLAST_RADIUS) continue;

            float damage = BLAST_DAMAGE * (1.0f - (float) (distance / BLAST_RADIUS) * 0.5f);
            if (damage < 1.0f) damage = 1.0f;

            if (entity instanceof Mob mob) {
                mob.hurt(level.damageSources().playerAttack(killer), damage);

                Vec3 away = entity.position().subtract(pos);
                if (away.lengthSqr() < 0.001) away = new Vec3(1, 0, 0);
                away = away.normalize();
                double knockback = 1.5 * (1.0 - distance / BLAST_RADIUS);
                mob.setDeltaMovement(away.x * knockback, 0.5 + knockback * 0.3, away.z * knockback);
                mob.hurtMarked = true;

                level.sendParticles(ParticleTypes.PORTAL,
                        mob.getX(), mob.getY() + 0.8, mob.getZ(), 8, 0.3f, 0.3f, 0.3f, 0.08f);
            }
        }
    }
}
