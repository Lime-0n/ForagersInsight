package com.tiomadre.foragersinsight.client.model;

import com.tiomadre.foragersinsight.core.ForagersInsight;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public final class FIModelLayers {
    public static final ModelLayerLocation AMADOU_CAP =
            new ModelLayerLocation(
                    new ResourceLocation(ForagersInsight.MOD_ID, "amadou_cap"),
                    "main"
            );

    private FIModelLayers() {
    }
}