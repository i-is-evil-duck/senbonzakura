package com.j3ly.senbonzakura.client;

import com.j3ly.senbonzakura.SenbonzakuraMod;
import com.j3ly.senbonzakura.item.CryptBladeItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderHandEvent;

/**
 * Renders a second (support) hand gripping the Crypt Blade in first person.
 *
 * <p>Uses only APIs that already ship with the mod: {@link RenderHandEvent} is
 * provided by NeoForge, and {@link PlayerRenderer#renderLeftHand} /
 * {@link PlayerRenderer#renderRightHand} are public vanilla methods. No mixins,
 * access transformers, or third-party dependencies are required.</p>
 */
@EventBusSubscriber(modid = SenbonzakuraMod.MOD_ID, value = Dist.CLIENT)
public class CryptBladeHandRenderer {

    // --- Tweak these to line the support hand up with the blade grip in-game ---
    private static final float GRIP_OFFSET_X = 0.35F;
    private static final float GRIP_OFFSET_Y = 0.25F;
    private static final float GRIP_OFFSET_Z = -0.15F;
    // --------------------------------------------------------------------------

    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;

        ItemStack mainHand = player.getMainHandItem();
        boolean cryptBlade = mainHand.getItem() instanceof CryptBladeItem;

        if (event.getHand() == InteractionHand.OFF_HAND) {
            // Suppress the normal offhand arm while both hands are on the blade.
            if (cryptBlade && player.getOffhandItem().isEmpty()) {
                event.setCanceled(true);
            }
            return;
        }

        if (event.getHand() != InteractionHand.MAIN_HAND) return;
        if (!cryptBlade) return;
        if (!player.getOffhandItem().isEmpty()) return; // a real offhand item wins

        EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
        if (!(dispatcher.getRenderer(player) instanceof PlayerRenderer renderer)) return;

        HumanoidArm supportArm = player.getMainArm() == HumanoidArm.RIGHT
                ? HumanoidArm.LEFT : HumanoidArm.RIGHT;
        float side = supportArm == HumanoidArm.RIGHT ? 1.0F : -1.0F;

        PoseStack pose = event.getPoseStack();
        pose.pushPose();

        // Follow the main hand's swing so the support hand stays near the blade.
        float swing = event.getSwingProgress();
        float swingX = -0.4F * Mth.sin(Mth.sqrt(swing) * (float) Math.PI);
        float swingY = 0.2F * Mth.sin(Mth.sqrt(swing) * (float) (Math.PI * 2));
        float swingZ = -0.2F * Mth.sin(swing * (float) Math.PI);

        // View-space shift toward the blade, then the vanilla map-hand pose that
        // brings an arm up from below into the middle of the view.
        pose.translate(GRIP_OFFSET_X + swingX, GRIP_OFFSET_Y + swingY, GRIP_OFFSET_Z + swingZ);
        pose.mulPose(Axis.YP.rotationDegrees(92.0F));
        pose.mulPose(Axis.XP.rotationDegrees(45.0F));
        pose.mulPose(Axis.ZP.rotationDegrees(side * -41.0F));
        pose.translate(side * 0.3F, -1.1F, 0.45F);

        if (supportArm == HumanoidArm.RIGHT) {
            renderer.renderRightHand(pose, event.getMultiBufferSource(), event.getPackedLight(), player);
        } else {
            renderer.renderLeftHand(pose, event.getMultiBufferSource(), event.getPackedLight(), player);
        }

        pose.popPose();
    }
}
