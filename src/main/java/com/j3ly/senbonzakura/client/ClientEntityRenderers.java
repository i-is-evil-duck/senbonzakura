package com.j3ly.senbonzakura.client;

import com.j3ly.senbonzakura.SenbonzakuraMod;
import com.j3ly.senbonzakura.entity.ModEntities;
import com.j3ly.senbonzakura.entity.boss.*;
import net.minecraft.client.renderer.entity.IronGolemRenderer;
import net.minecraft.client.renderer.entity.SlimeRenderer;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.client.renderer.entity.SpiderRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

/**
 * Registers entity renderers for all Chaotic Mode bosses.
 * Tier 1 bosses (Slime/Zombie/Skeleton/Spider) use vanilla renderers;
 * Iron Golem-based bosses use IronGolemRenderer as a placeholder.
 */
@EventBusSubscriber(modid = SenbonzakuraMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEntityRenderers {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // Tier 1 — use vanilla renderers for reskin bosses
        event.registerEntityRenderer(ModEntities.KING_SLIME.get(),
                context -> new SlimeRenderer(context));
        event.registerEntityRenderer(ModEntities.ZOMBIE_GENERAL.get(),
                context -> new ZombieRenderer(context));
        event.registerEntityRenderer(ModEntities.SKELETON_GENERAL.get(),
                context -> new SkeletonRenderer(context));
        event.registerEntityRenderer(ModEntities.QUEEN_SPIDER.get(),
                context -> new SpiderRenderer(context));

        // Segmented worm renderers (integrated from the Iron Worm PoC)
        event.registerEntityRenderer(ModEntities.IRON_WORM.get(), WormRenderer::new);
        event.registerEntityRenderer(ModEntities.EARTH_WORM.get(), WormRenderer::new);
        event.registerEntityRenderer(ModEntities.HELL_SERPENT.get(), WormRenderer::new);
        event.registerEntityRenderer(ModEntities.SKY_SERPENT.get(), WormRenderer::new);
        event.registerEntityRenderer(ModEntities.OCEAN_SERPENT.get(), WormRenderer::new);

        // Iron Golem placeholder renderers for the remaining Iron Golem-based bosses
        event.registerEntityRenderer(ModEntities.WEREWOLF_TRIBE_CHIEF.get(),
                context -> new IronGolemRenderer(context));
        event.registerEntityRenderer(ModEntities.FROZEN_SERPENT.get(),
                context -> new IronGolemRenderer(context));
        event.registerEntityRenderer(ModEntities.KRAKEN_GUARDIAN.get(),
                context -> new IronGolemRenderer(context));
        event.registerEntityRenderer(ModEntities.WENDIGO_WARLORD.get(),
                context -> new IronGolemRenderer(context));
        event.registerEntityRenderer(ModEntities.SINFUL_SQUIRE.get(),
                context -> new IronGolemRenderer(context));
        event.registerEntityRenderer(ModEntities.SINFUL_BISHOP.get(),
                context -> new IronGolemRenderer(context));
        event.registerEntityRenderer(ModEntities.SINISTER_KNIGHT.get(),
                context -> new IronGolemRenderer(context));
        event.registerEntityRenderer(ModEntities.FELIO_OF_FLAME.get(),
                context -> new IronGolemRenderer(context));
        event.registerEntityRenderer(ModEntities.IEIRITH_OF_ICE.get(),
                context -> new IronGolemRenderer(context));
        event.registerEntityRenderer(ModEntities.THERIM_OF_THUNDER.get(),
                context -> new IronGolemRenderer(context));
        event.registerEntityRenderer(ModEntities.WAHIKO_OF_WIND.get(),
                context -> new IronGolemRenderer(context));
        event.registerEntityRenderer(ModEntities.SAKURAI_WAHIKO.get(),
                context -> new IronGolemRenderer(context));
        event.registerEntityRenderer(ModEntities.SERRIK_OF_SHADOW.get(),
                context -> new IronGolemRenderer(context));
        event.registerEntityRenderer(ModEntities.LUCIUS_RETAINER_OF_RUIN.get(),
                context -> new IronGolemRenderer(context));
        event.registerEntityRenderer(ModEntities.DRALLIUS_VISIONS_OF_DOOM.get(),
                context -> new IronGolemRenderer(context));
        event.registerEntityRenderer(ModEntities.DRALLIUS_CHAOS_UNLEASHED.get(),
                context -> new IronGolemRenderer(context));
    }
}
