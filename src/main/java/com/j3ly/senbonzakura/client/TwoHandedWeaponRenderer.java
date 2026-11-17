package com.j3ly.senbonzakura.client;

import com.j3ly.senbonzakura.SenbonzakuraMod;
import com.j3ly.senbonzakura.item.CryptBladeItem;
import com.j3ly.senbonzakura.item.SenbonzakuraItem;
import com.j3ly.senbonzakura.item.YukiNoAmeItem;
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
 * Renders a second (support) hand gripping two-handed weapons in first person.
 *
 * <p>Uses only APIs that already ship with the mod: {@link RenderHandEvent} is
 * provided by NeoForge, and {@link PlayerRenderer#renderLeftHand} /
 * {@link PlayerRenderer#renderRightHand} are public vanilla methods. No mixins,
 * access transformers, or third-party dependencies are required.</p>
 */
@EventBusSubscriber(modid = SenbonzakuraMod.MOD_ID, value = Dist.CLIENT)
public class TwoHandedWeaponRenderer {

    // --- Tweak these to line the support hand up with the grip in-game ---
    private static final float GRIP_UP_DOWN = 0.55F; // lower = arm sits lower on the weapon
    private static final float GRIP_FORWARD = 0.57F;
    // ----------------------------------------------------------------------

    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;

        ItemStack mainHand = player.getMainHandItem();
        boolean twoHanded = isTwoHanded(mainHand);

        if (event.getHand() == InteractionHand.OFF_HAND) {
            // Suppress the normal offhand arm while both hands are on the weapon.
            if (twoHanded && player.getOffhandItem().isEmpty()) {
                event.setCanceled(true);
            }
            return;
        }

        if (event.getHand() != InteractionHand.MAIN_HAND) return;
        if (!twoHanded) return;
        if (!player.getOffhandItem().isEmpty()) return; // a real offhand item wins

        EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
        if (!(dispatcher.getRenderer(player) instanceof PlayerRenderer renderer)) return;

        HumanoidArm mainArm = player.getMainArm();
        float mainSide = mainArm == HumanoidArm.RIGHT ? 1.0F : -1.0F;
        HumanoidArm supportArm = mainArm == HumanoidArm.RIGHT ? HumanoidArm.LEFT : HumanoidArm.RIGHT;
        float supportSide = supportArm == HumanoidArm.RIGHT ? 1.0F : -1.0F;

        PoseStack pose = event.getPoseStack();
        pose.pushPose();

        float swing = event.getSwingProgress();
        float rootSwing = Mth.sqrt(swing);

        // Match the main-hand item's transform chain exactly so the support hand
        // stays glued to the weapon through the whole swing/equip animation.
        // 1) swing translation
        pose.translate(
                mainSide * -0.4F * Mth.sin(rootSwing * (float) Math.PI),
                0.2F * Mth.sin(rootSwing * (float) (Math.PI * 2)),
                -0.2F * Mth.sin(swing * (float) Math.PI)
        );
        // 2) base arm transform
        pose.translate(mainSide * 0.56F, -0.52F + event.getEquipProgress() * -0.6F, -0.72F);
        // 3) attack rotation
        float attackF = Mth.sin(swing * swing * (float) Math.PI);
        pose.mulPose(Axis.YP.rotationDegrees(mainSide * (45.0F + attackF * -20.0F)));
        float swingF = Mth.sin(rootSwing * (float) Math.PI);
        pose.mulPose(Axis.ZP.rotationDegrees(mainSide * swingF * -20.0F));
        pose.mulPose(Axis.XP.rotationDegrees(swingF * -80.0F));
        pose.mulPose(Axis.YP.rotationDegrees(mainSide * -45.0F));

        // 4) grip offset. X mirrors with the main hand; the base transform was
        // subtracted out so the idle pose matches the previous tuning.
        pose.translate(mainSide * -0.21F, GRIP_UP_DOWN, GRIP_FORWARD);

        // 5) vanilla map-hand pose that brings the arm up from below the view.
        pose.mulPose(Axis.YP.rotationDegrees(92.0F));
        pose.mulPose(Axis.XP.rotationDegrees(45.0F));
        pose.mulPose(Axis.ZP.rotationDegrees(supportSide * -41.0F));
        pose.translate(supportSide * 0.3F, -1.1F, 0.45F);

        if (supportArm == HumanoidArm.RIGHT) {
            renderer.renderRightHand(pose, event.getMultiBufferSource(), event.getPackedLight(), player);
        } else {
            renderer.renderLeftHand(pose, event.getMultiBufferSource(), event.getPackedLight(), player);
        }

        pose.popPose();
    }

    private static boolean isTwoHanded(ItemStack stack) {
        return stack.getItem() instanceof CryptBladeItem
                || stack.getItem() instanceof SenbonzakuraItem
                || stack.getItem() instanceof YukiNoAmeItem;
    }
}
