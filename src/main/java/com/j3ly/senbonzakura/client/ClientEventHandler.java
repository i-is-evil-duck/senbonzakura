package com.j3ly.senbonzakura.client;

import com.j3ly.senbonzakura.SenbonzakuraMod;
import com.j3ly.senbonzakura.item.SenbonzakuraItem;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = SenbonzakuraMod.MOD_ID, value = Dist.CLIENT)
public class ClientEventHandler {

    private static int chargeTime = 0;
    private static boolean wasCharging = false;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.level == null) return;

        boolean isCharging = player.getMainHandItem().getItem() instanceof SenbonzakuraItem && player.isUsingItem();

        if (isCharging) {
            chargeTime++;
            wasCharging = true;

            Vec3 pos = player.position();
            Vec3 look = player.getLookAngle();

            if (chargeTime % 2 == 0) {
                double px = pos.x + look.x * 0.5 + (player.getRandom().nextDouble() - 0.5) * 0.5;
                double py = pos.y + 1.0 + (player.getRandom().nextDouble() - 0.5) * 0.3;
                double pz = pos.z + look.z * 0.5 + (player.getRandom().nextDouble() - 0.5) * 0.5;
                mc.level.addParticle(ParticleTypes.ENCHANTED_HIT, px, py, pz, 0, 0.05, 0);
            }

            if (chargeTime >= 10) {
                float intensity = Math.min((chargeTime - 10) / 30.0f, 1.0f);
                float shakeAmount = 0.5f * intensity;
                player.setYRot(player.getYRot() + (player.getRandom().nextFloat() - 0.5f) * shakeAmount);
                player.setXRot(player.getXRot() + (player.getRandom().nextFloat() - 0.5f) * shakeAmount * 0.5f);
            }
        } else {
            if (wasCharging) {
                chargeTime = 0;
                wasCharging = false;
            }
        }
    }
}
