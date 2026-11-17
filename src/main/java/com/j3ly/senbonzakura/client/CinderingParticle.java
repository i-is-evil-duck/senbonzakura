package com.j3ly.senbonzakura.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CinderingParticle extends TextureSheetParticle {

    private final SpriteSet sprites;

    protected CinderingParticle(ClientLevel level, double x, double y, double z,
                                double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.sprites = sprites;
        this.lifetime = 12 + this.random.nextInt(10);
        this.quadSize *= 0.8f + this.random.nextFloat() * 0.5f;
        this.gravity = -0.015f;
        this.friction = 0.96f;
        this.hasPhysics = false;
        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);
        this.alpha = 1.0f - ((float) this.age / (float) this.lifetime);
    }

    @Override
    public float getQuadSize(float partialTick) {
        float progress = ((float) this.age + partialTick) / (float) this.lifetime;
        return this.quadSize * (1.0f - progress * 0.5f);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public record Provider(SpriteSet sprites) implements ParticleProvider<SimpleParticleType> {
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new CinderingParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites);
        }
    }
}
