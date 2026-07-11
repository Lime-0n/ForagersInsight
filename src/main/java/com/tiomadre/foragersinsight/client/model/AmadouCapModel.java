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
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition root = mesh.getRoot();
        PartDefinition head = root.getChild("head");

        head.addOrReplaceChild("cap_crown", CubeListBuilder.create()
                        .texOffs(0, 1)
                        .addBox(-2.325F, -8.5F, -2.175F, 4.5F, 0.8F, 4.5F, new CubeDeformation(0.2F)),
                PartPose.ZERO);
        head.addOrReplaceChild("cap_dome", CubeListBuilder.create()
                        .texOffs(0, 2)
                        .addBox(-1.7F, -9.6F, -1.65F, 3.35F, 0.9F, 3.5F, new CubeDeformation(0.15F)),
                PartPose.ZERO);
        head.addOrReplaceChild("cap_stem", CubeListBuilder.create()
                        .texOffs(7, 5)
                        .addBox(-0.4F, -10.0F, -0.35F, 1.0F, 1.0F, 1.0F),
                PartPose.ZERO);

        return LayerDefinition.create(mesh, 16, 16);
    }
}