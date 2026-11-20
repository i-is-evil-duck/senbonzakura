package com.j3ly.senbonzakura.client;

import com.j3ly.senbonzakura.entity.worm.WormEntity;
import com.j3ly.senbonzakura.entity.worm.WormPart;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;

/** Renders the head model plus one segment model per part, oriented along the chain. */
public class WormRenderer extends EntityRenderer<WormEntity> {
    private final WormSegmentModel headModel;
    private final WormSegmentModel segmentModel;

    public WormRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.headModel = new WormSegmentModel(WormSegmentModel.createBodyLayer().bakeRoot());
        this.segmentModel = new WormSegmentModel(WormSegmentModel.createBodyLayer().bakeRoot());
    }

    @Override
    public void render(WormEntity worm, float entityYaw, float partialTicks, PoseStack pose,
                       MultiBufferSource buffer, int packedLight) {
        super.render(worm, entityYaw, partialTicks, pose, buffer, packedLight);
        Vec3 base = worm.getPosition(partialTicks);
        float bodyYaw = Mth.rotLerp(partialTicks, worm.yRotO, worm.getYRot());
        float blend = worm.getPoseBlend();
        float modelScale = worm.variant().modelScale();
        VertexConsumer vc = buffer.getBuffer(this.headModel.renderType(getTextureLocation(worm)));

        // ---- HEAD ----
        pose.pushPose();
        // The entity origin stays grounded, but the pose raises the head just like the parts.
        float headOffset = 0.4F * (modelScale / 1.25F) + worm.variant().headLift() * blend;
        pose.translate(0.0, headOffset, 0.0);
        pose.mulPose(Axis.YP.rotationDegrees(180.0F - bodyYaw));
        // Pitch down while reared up, like a cobra aiming at prey.
        pose.mulPose(Axis.XP.rotationDegrees(60.0F * blend));
        pose.scale(modelScale, modelScale, modelScale);
        this.headModel.renderToBuffer(pose, vc, packedLight, OverlayTexture.NO_OVERLAY);
        pose.popPose();

        // ---- SEGMENTS ----
        PartEntity<?>[] parts = worm.getParts();
        for (int i = 0; i < parts.length; i++) {
            WormPart part = (WormPart) parts[i];
            Vec3 rel = part.getPosition(partialTicks).subtract(base);
            pose.pushPose();
            pose.translate(rel.x, rel.y, rel.z);
            float partYaw = Mth.rotLerp(partialTicks, part.yRotO, part.getYRot());
            pose.mulPose(Axis.YP.rotationDegrees(180.0F - partYaw));
            // Gentle idle wiggle along the chain, suppressed during the pose
            float wiggle = Mth.sin((worm.tickCount + i * 3) * 0.4F) * 6.0F * (1.0F - blend);
            pose.mulPose(Axis.XP.rotationDegrees(wiggle));
            this.segmentModel.setupAnim(worm,
                    worm.walkAnimation.position(partialTicks) + i * 1.3F,
                    worm.walkAnimation.speed(partialTicks),
                    worm.tickCount + i * 2, 0.0F, 0.0F);
            pose.scale(modelScale, modelScale, modelScale);
            this.segmentModel.renderToBuffer(pose, vc, packedLight, OverlayTexture.NO_OVERLAY);
            pose.popPose();
        }
    }

    @Override
    public ResourceLocation getTextureLocation(WormEntity entity) {
        return ResourceLocation.fromNamespaceAndPath("senbonzakura", entity.variant().texture());
    }
}
