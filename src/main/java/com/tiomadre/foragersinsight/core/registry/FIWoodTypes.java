package com.tiomadre.foragersinsight.core.registry;

import com.tiomadre.foragersinsight.core.ForagersInsight;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

public final class FIWoodTypes {
    public static final WoodType LILAC = WoodType.register(new WoodType(ForagersInsight.MOD_ID + ":lilac", BlockSetType.OAK));

    private FIWoodTypes() {
    }

    public static void register() {
    }
}