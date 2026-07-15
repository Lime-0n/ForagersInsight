package com.tiomadre.foragersinsight.client.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.LivingEntity;

public class AmadouCapModel<T extends LivingEntity> extends HumanoidModel<T> {
    public AmadouCapModel(ModelPart root) {
        super(root);
        this.body.visible = false;
        this.hat.visible = false;
        this.rightArm.visible = false;
        this.leftArm.visible = false;
        this.rightLeg.visible = false;
        this.leftLeg.visible = false;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition root = mesh.getRoot();
        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);

        head.addOrReplaceChild("brim", CubeListBuilder.create()
                        .texOffs(10, 5)
                        .addBox(-4.15F, -8.5F, -4.15F, 8.3F, 1.3F, 8.3F),
                PartPose.ZERO);
        head.addOrReplaceChild("head", CubeListBuilder.create()
                        .texOffs(6, 0)
                        .addBox(-3.15F, -7.35F, -3.15F, 6.3F, 2.3F, 6.3F),
                PartPose.ZERO);
        head.addOrReplaceChild("button", CubeListBuilder.create()
                        .texOffs(8, 5)
                        .addBox(-0.5F, -8.15F, -0.5F, 1.0F, 1.0F, 1.0F),
                PartPose.ZERO);

        return LayerDefinition.create(mesh, 16, 16);
    }
}