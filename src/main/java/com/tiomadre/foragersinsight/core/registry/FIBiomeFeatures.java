package com.tiomadre.foragersinsight.core.registry;

import com.tiomadre.foragersinsight.common.worldgen.feature.DarkOakBushFeature;
import com.tiomadre.foragersinsight.common.worldgen.feature.FallenTreeFeature;
import com.tiomadre.foragersinsight.common.worldgen.feature.PuddleFeature;
import com.tiomadre.foragersinsight.core.ForagersInsight;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;


public class FIBiomeFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(Registries.FEATURE, ForagersInsight.MOD_ID);

    public static final Supplier<Feature<NoneFeatureConfiguration>> DARK_OAK_BUSH =
            FEATURES.register("dark_oak_bush", () -> new DarkOakBushFeature(NoneFeatureConfiguration.CODEC));

    public static final Supplier<Feature<NoneFeatureConfiguration>> FALLEN_TREES =
            FEATURES.register("fallen_trees", () -> new FallenTreeFeature(NoneFeatureConfiguration.CODEC));

    public static final Supplier<Feature<NoneFeatureConfiguration>> PUDDLE =
            FEATURES.register("puddle", () -> new PuddleFeature(NoneFeatureConfiguration.CODEC));


    public static void register(IEventBus bus) {
        FEATURES.register(bus);
    }
}
