package com.tiomadre.foragersinsight.core.registry;

import com.tiomadre.foragersinsight.common.worldgen.trees.foliage.*;
import com.tiomadre.foragersinsight.core.ForagersInsight;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class FIFoliagePlacerType {
    public static final DeferredRegister<FoliagePlacerType<?>> FOLIAGE_PLACER_TYPE =
            DeferredRegister.create(ForgeRegistries.FOLIAGE_PLACER_TYPES, ForagersInsight.MOD_ID);

    public static final RegistryObject<FoliagePlacerType<AppleTreeFoliagePlacer>> APPLE_FOLIAGE_PLACER =
            FOLIAGE_PLACER_TYPE.register("apple_tree_foliage_placer", () ->
                    new FoliagePlacerType<>(AppleTreeFoliagePlacer.CODEC));

    public static final RegistryObject<FoliagePlacerType<AcornTreeFoliagePlacer>> ACORN_FOLIAGE_PLACER =
            FOLIAGE_PLACER_TYPE.register("acorn_tree_foliage_placer", () ->
                    new FoliagePlacerType<>(AcornTreeFoliagePlacer.CODEC));

    public static final RegistryObject<FoliagePlacerType<SpruceTipTreeFoliagePlacer>> SPRUCE_TIP_FOLIAGE_PLACER =
            FOLIAGE_PLACER_TYPE.register("spruce_tip_tree_foliage_placer", () ->
                    new FoliagePlacerType<>(SpruceTipTreeFoliagePlacer.CODEC));

    public static final RegistryObject<FoliagePlacerType<LilacTreeFoliagePlacer>> LILAC_FOLIAGE_PLACER =
            FOLIAGE_PLACER_TYPE.register("lilac_tree_foliage_placer", () ->
                    new FoliagePlacerType<>(LilacTreeFoliagePlacer.CODEC));

    public static final RegistryObject<FoliagePlacerType<WoodlandsDarkOakFoliagePlacer>> WOODLANDS_DARK_OAK_FOLIAGE_PLACER =
            FOLIAGE_PLACER_TYPE.register("woodlands_dark_oak_foliage_placer", () ->
                    new FoliagePlacerType<>(WoodlandsDarkOakFoliagePlacer.CODEC));
}