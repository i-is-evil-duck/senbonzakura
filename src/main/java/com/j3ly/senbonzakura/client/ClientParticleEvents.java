package com.j3ly.senbonzakura.client;

import com.j3ly.senbonzakura.SenbonzakuraMod;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@EventBusSubscriber(modid = SenbonzakuraMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientParticleEvents {

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(SenbonzakuraMod.CINDERING_PARTICLES.get(), CinderingParticle.Provider::new);
    }
}
