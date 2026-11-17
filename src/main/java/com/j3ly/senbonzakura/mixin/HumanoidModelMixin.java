package com.j3ly.senbonzakura.mixin;

import com.j3ly.senbonzakura.item.CryptBladeItem;
import com.j3ly.senbonzakura.item.SenbonzakuraItem;
import com.j3ly.senbonzakura.item.YukiNoAmeItem;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
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
 * finished all arm/walk/swing animation - and rotates BOTH arms so their hands
 * meet on the grip just right of the body's centre. Running here (rather than in
 * {@code PlayerModel.setupAnim}) means the sleeves are still copied from the
 * arms afterwards, so armour sleeves follow the pose too.</p>
 */
@Mixin(HumanoidModel.class)
public abstract class HumanoidModelMixin {

    @Shadow public ModelPart rightArm;
    @Shadow public ModelPart leftArm;

    // --- Tweak these to line the two hands up with the grip in third person ---
    private static final float MAIN_REACH = 0.90F;      // main arm forward reach (radians)
    private static final float SUPPORT_REACH = 0.95F;   // support arm forward reach
    private static final float MAIN_ACROSS = 0.18F;     // main arm yaw toward the centre
    private static final float SUPPORT_ACROSS = 0.50F;  // support arm yaw across the body
    private static final float MAIN_TUCK = 0.25F;       // main arm roll toward the centre
    private static final float SUPPORT_TUCK = 0.55F;    // support arm roll toward the centre
    private static final float MAIN_BLEND = 0.80F;      // 0 = keep vanilla arm pose, 1 = full two-hand pose
    // --------------------------------------------------------------------------

    @Inject(method = "setupAnim", at = @At("TAIL"))
    private void senbonzakura$poseTwoHanded(LivingEntity entity, float limbSwing, float limbSwingAmount,
                                            float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (!(entity instanceof Player player)) return;

        ItemStack mainHand = player.getMainHandItem();
        if (!isTwoHanded(mainHand)) return;
        if (!player.getOffhandItem().isEmpty()) return; // a real offhand item wins

        boolean mainRight = player.getMainArm() == HumanoidArm.RIGHT;
        ModelPart main = mainRight ? this.rightArm : this.leftArm;
        ModelPart support = mainRight ? this.leftArm : this.rightArm;
        // +1 when the weapon is in the right hand, -1 when it is in the left.
        // model x=-5 is the player's right, which is screen-right in F5.
        float side = mainRight ? 1.0F : -1.0F;

        // Main arm: ease it off its vanilla swing and angle it inward so the
        // hand (and the sword) sit slightly right of centre.
        main.xRot = Mth.lerp(MAIN_BLEND, main.xRot, -MAIN_REACH);
        main.yRot = Mth.lerp(MAIN_BLEND, main.yRot, -side * MAIN_ACROSS);
        main.zRot = Mth.lerp(MAIN_BLEND, main.zRot, -side * MAIN_TUCK);

        // Support arm: cross the body to meet the main hand on the grip.
        // In model space both positive yaw and positive roll move the hand toward
        // x=-5 (the player's right), so the signs mirror with `side`.
        support.xRot = -SUPPORT_REACH;
        support.yRot = side * SUPPORT_ACROSS;
        support.zRot = side * SUPPORT_TUCK;
    }

    private static boolean isTwoHanded(ItemStack stack) {
        return stack.getItem() instanceof CryptBladeItem
                || stack.getItem() instanceof SenbonzakuraItem
                || stack.getItem() instanceof YukiNoAmeItem;
    }
}
