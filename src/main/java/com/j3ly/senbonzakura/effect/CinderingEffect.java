package com.j3ly.senbonzakura.effect;

import com.j3ly.senbonzakura.SenbonzakuraMod;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

/**
 * Cindering: fire burns out twice as fast, but fire damage is tripled.
 */
public class CinderingEffect extends MobEffect {

    public static final int COLOR = 0xFF6A00;

    public CinderingEffect() {
        super(MobEffectCategory.NEUTRAL, COLOR);
    }

    @Override
    public ParticleOptions createParticleOptions(MobEffectInstance instance) {
        return SenbonzakuraMod.CINDERING_PARTICLES.get();
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        // Extinguish one extra fire tick per tick, doubling the normal burn-out rate.
        if (!entity.level().isClientSide && entity.getRemainingFireTicks() > 0) {
            entity.setRemainingFireTicks(Math.max(0, entity.getRemainingFireTicks() - 1));
        }
        return true;
    }
}
