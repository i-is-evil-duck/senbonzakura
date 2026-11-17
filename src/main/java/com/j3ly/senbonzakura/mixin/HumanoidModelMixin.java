package com.j3ly.senbonzakura.mixin;

import com.j3ly.senbonzakura.item.CryptBladeItem;
import com.j3ly.senbonzakura.item.SenbonzakuraItem;
import com.j3ly.senbonzakura.item.YukiNoAmeItem;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Third-person (F5 / other players) two-handed hold for the mod's greatswords.
 *
 * <p>The first-person hold is handled by {@code TwoHandedWeaponRenderer} via
 * {@code RenderHandEvent}, which is never called for third-person models. This
 * mixin runs at the end of {@link HumanoidModel#setupAnim} - after vanilla has
 * finished all arm/walk/swing animation - and poses the empty support arm so it
 * reaches across the body to the weapon grip. Running here (rather than in
 * {@code PlayerModel.setupAnim}) means the sleeves are still copied from the
 * arms afterwards, so armour sleeves follow the new pose too.</p>
 */
@Mixin(HumanoidModel.class)
public abstract class HumanoidModelMixin {

    @Shadow public ModelPart rightArm;
    @Shadow public ModelPart leftArm;

    // Tweak these to line the support arm up with the grip in third person.
    private static final float SUPPORT_REACH = 0.45F; // how far forward the support arm reaches
    private static final float SUPPORT_ACROSS = 0.55F; // how far it reaches across the body
    private static final float SUPPORT_TUCK = 0.45F;  // how far it tucks toward the main hand

    @Inject(method = "setupAnim", at = @At("TAIL"))
    private void senbonzakura$poseTwoHanded(LivingEntity entity, float limbSwing, float limbSwingAmount,
                                            float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (!(entity instanceof Player player)) return;

        ItemStack mainHand = player.getMainHandItem();
        if (!isTwoHanded(mainHand)) return;
        if (!player.getOffhandItem().isEmpty()) return; // a real offhand item wins

        boolean mainRight = player.getMainArm() == HumanoidArm.RIGHT;
        ModelPart support = mainRight ? this.leftArm : this.rightArm;
        ModelPart mainArmPart = mainRight ? this.rightArm : this.leftArm;
        float side = mainRight ? 1.0F : -1.0F;

        // Follow the main arm's walk/swing motion, then reach across to the grip.
        support.xRot = mainArmPart.xRot - SUPPORT_REACH;
        support.yRot = mainArmPart.yRot + side * SUPPORT_ACROSS;
        support.zRot = -side * SUPPORT_TUCK;
    }

    private static boolean isTwoHanded(ItemStack stack) {
        return stack.getItem() instanceof CryptBladeItem
                || stack.getItem() instanceof SenbonzakuraItem
                || stack.getItem() instanceof YukiNoAmeItem;
    }
}
