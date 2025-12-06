package com.tiomadre.foragersinsight.core.registry;

import com.tiomadre.foragersinsight.common.worldgen.feature.BirchPolyporeFeature;
import com.tiomadre.foragersinsight.core.ForagersInsight;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class FIFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, ForagersInsight.MOD_ID);

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> BIRCH_POLYPORE = FEATURES.register(
            "birch_polypore", BirchPolyporeFeature::new);
}