package com.tiomadre.foragersinsight.core.registry;

import com.tiomadre.foragersinsight.core.ForagersInsight;
import com.teamabnormals.blueprint.core.registry.BlueprintBoatTypes;
import net.minecraft.resources.ResourceLocation;

public final class FIBoatTypes {
    public static final ResourceLocation LILAC = ForagersInsight.rl("lilac");
    private static boolean registered = false;

    private FIBoatTypes() {
    }

    public static void register() {
        if (registered) {
            return;
        }
        registered = true;
        BlueprintBoatTypes.registerType(LILAC, FIItems.LILAC_BOAT, FIItems.LILAC_CHEST_BOAT, FIBlocks.LILAC_PLANKS, false);
    }
}