package com.j3ly.senbonzakura.client;

import com.j3ly.senbonzakura.entity.worm.WormEntity;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

/**
 * Placeholder segment: a plated body box centered on the origin.
 * TODO: replace with a reskinned SilverfishModel (legs + segments) once the pipeline works.
 */
public class WormSegmentModel extends HierarchicalModel<WormEntity> {
    private final ModelPart root;

    public WormSegmentModel(ModelPart root) {
        this.root = root;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition part = mesh.getRoot();
        part.addOrReplaceChild("body", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-4.0F, -3.0F, -5.0F, 8.0F, 6.0F, 10.0F),
                PartPose.ZERO);
        part.addOrReplaceChild("plate", CubeListBuilder.create()
                        .texOffs(0, 16).addBox(-3.0F, -4.0F, -3.0F, 6.0F, 2.0F, 6.0F),
                PartPose.ZERO);
        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(WormEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
    }
}
