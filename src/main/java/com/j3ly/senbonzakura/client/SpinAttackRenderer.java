package com.j3ly.senbonzakura.client;

import com.j3ly.senbonzakura.SenbonzakuraMod;
import com.j3ly.senbonzakura.network.ModNetworking;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLivingEvent;

/**
 * Visually rotates a player (and the item in their hands) during a 180/360
 * sweep attack. Uses NeoForge's render event, so no mixins are needed.
 */
@EventBusSubscriber(modid = SenbonzakuraMod.MOD_ID, value = Dist.CLIENT)
@SuppressWarnings("rawtypes")
public class SpinAttackRenderer {

    @SubscribeEvent
    public static void onRenderLiving(RenderLivingEvent.Pre event) {
        if (!(event.getEntity() instanceof AbstractClientPlayer player)) return;
        if (Minecraft.getInstance().level == null) return;

        ModNetworking.SpinState spin = ModNetworking.getSpin(player.getUUID());
        if (spin == null) return;

        long now = Minecraft.getInstance().level.getGameTime();
        float progress = (float) (now - spin.startTick()) / (float) spin.durationTicks();
        if (progress >= 1.0F) {
            ModNetworking.clearSpin(player.getUUID());
            return;
        }
        if (progress < 0.0F) return;

        float eased;
        if (spin.pingPong()) {
            // Out and back, so a 180 degree sweep ends facing forward again.
            eased = (float) Math.sin(progress * Math.PI);
        } else {
            // Ease-out full spin, ending at exactly 360 degrees (= original facing).
            eased = 1.0F - (1.0F - progress) * (1.0F - progress);
        }

        PoseStack pose = event.getPoseStack();
        pose.mulPose(Axis.YP.rotationDegrees(spin.degrees() * eased));
    }
}
