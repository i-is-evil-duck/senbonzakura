package com.j3ly.senbonzakura.client;

import com.j3ly.senbonzakura.SenbonzakuraMod;
import com.j3ly.senbonzakura.item.SenbonzakuraItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SenbonzakuraMod.MOD_ID, value = Dist.CLIENT)
public class ClientEventHandler {

    private static int chargeTime = 0;
    private static boolean wasCharging = false;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        ItemStack mainHand = player.getMainHandItem();
        boolean holdingSenbonzakura = mainHand.getItem() instanceof SenbonzakuraItem;
        boolean usingItem = player.isUsingItem();

        if (holdingSenbonzakura && usingItem) {
            chargeTime++;
            wasCharging = true;

            // Spawn charge particles around player
            if (chargeTime % 4 == 0) {
                double x = player.getX() + (Math.random() - 0.5) * 2;
                double y = player.getY() + Math.random() * 2;
                double z = player.getZ() + (Math.random() - 0.5) * 2;
                player.level().addParticle(ParticleTypes.ENCHANTED_HIT, x, y, z, 0, 0.1, 0);
            }

            // Screen shake effect when fully charged
            if (chargeTime >= 60) {
                float shake = (float) (Math.random() - 0.5) * 0.02f;
                player.setXRot(player.getXRot() + shake);
            }
        } else if (wasCharging) {
            chargeTime = 0;
            wasCharging = false;
        }
    }
}
