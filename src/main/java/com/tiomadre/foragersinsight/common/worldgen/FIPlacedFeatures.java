package com.tiomadre.foragersinsight.common.worldgen;

import com.tiomadre.foragersinsight.core.ForagersInsight;
import com.tiomadre.foragersinsight.data.server.tags.FITags;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.tags.BiomeTags;

import java.util.List;

import vectorwing.farmersdelight.common.world.filter.BiomeTagFilter;

public class FIPlacedFeatures {
    public static final ResourceKey<PlacedFeature> APPLE_TREE_PLACED_KEY = registerKey("apple_tree_placed");
    public static final ResourceKey<PlacedFeature> ACORN_DARK_OAK_TREE_PLACED_KEY = registerKey("acorn_dark_oak_placed");
    public static final ResourceKey<PlacedFeature> LILAC_TREE_PLACED_KEY = registerKey("lilac_tree_placed");
    public static final ResourceKey<PlacedFeature> SPRUCE_TIP_TREE_PLACED_KEY = registerKey("spruce_tip_tree_placed");
    public static final ResourceKey<PlacedFeature> SAPPY_BIRCH_TREE_PLACED_KEY = registerKey("sappy_birch_tree_placed");

    public static final ResourceKey<PlacedFeature> ROSELLE_PATCH_PLACED_KEY = registerKey("roselle_patch_placed");
    public static final ResourceKey<PlacedFeature> BEACH_ROSE_PATCH_PLACED_KEY = registerKey("beach_rose_patch_placed");
    public static final ResourceKey<PlacedFeature> WOODLAND_FERN_PATCH_PLACED_KEY = registerKey("woodland_fern_patch_placed");
    public static final ResourceKey<PlacedFeature> PHLOX_PATCH_PLACED_KEY = registerKey("phlox_patch_placed");

    public static final ResourceKey<PlacedFeature> OAK_SUSPICIOUS_LEAF_LITTER_PATCH_PLACED_KEY = registerKey("oak_suspicious_leaf_litter_patch_placed");
    public static final ResourceKey<PlacedFeature> BIRCH_SUSPICIOUS_LEAF_LITTER_PATCH_PLACED_KEY = registerKey("birch_suspicious_leaf_litter_patch_placed");
    public static final ResourceKey<PlacedFeature> SPRUCE_SUSPICIOUS_LEAF_LITTER_PATCH_PLACED_KEY = registerKey("spruce_suspicious_leaf_litter_patch_placed");
    public static final ResourceKey<PlacedFeature> DARK_OAK_SUSPICIOUS_LEAF_LITTER_PATCH_PLACED_KEY = registerKey("dark_oak_suspicious_leaf_litter_patch_placed");
    public static final ResourceKey<PlacedFeature> FLOWER_SUSPICIOUS_LEAF_LITTER_PATCH_PLACED_KEY = registerKey("flower_suspicious_leaf_litter_patch_placed");
    public static final ResourceKey<PlacedFeature> DARK_WOODLANDS_SUSPICIOUS_LEAF_LITTER_PATCH_PLACED_KEY = registerKey("dark_woodlands_suspicious_leaf_litter_patch_placed");

    public static final ResourceKey<PlacedFeature> DARK_WOODLANDS_TREES_PLACED_KEY = registerKey("dark_woodlands_trees_placed");
    public static final ResourceKey<PlacedFeature> DARK_OAK_BUSH_PLACED_KEY = registerKey("dark_oak_bush_placed");
    public static final ResourceKey<PlacedFeature> WOODLANDS_PATCH_PLACED_KEY = registerKey("woodlands_patch_placed");

    public static final ResourceKey<PlacedFeature> DARK_WOODLANDS_FALLEN_TREES_PLACED_KEY = registerKey("dark_woodlands_fallen_trees_placed");
    public static final ResourceKey<PlacedFeature> OAK_FALLEN_TREES_PLACED_KEY = registerKey("oak_fallen_trees_placed");
    public static final ResourceKey<PlacedFeature> BIRCH_FALLEN_TREES_PLACED_KEY = registerKey("birch_fallen_trees_placed");
    public static final ResourceKey<PlacedFeature> SPRUCE_FALLEN_TREES_PLACED_KEY = registerKey("spruce_fallen_trees_placed");
    public static final ResourceKey<PlacedFeature> DARK_OAK_FALLEN_TREES_PLACED_KEY = registerKey("dark_oak_fallen_trees_placed");

    public static final ResourceKey<PlacedFeature> PUDDLE_PLACED_KEY = registerKey("puddle_placed");


    public static ResourceKey<PlacedFeature> registerKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, ForagersInsight.rl(name));
    }

    public static void bootstap(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);
        //Trees
        register(context, APPLE_TREE_PLACED_KEY, configuredFeatures.getOrThrow(FIConfiguredFeatures.APPLE_TREE_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(1, 0.2f, 1), Blocks.OAK_SAPLING));
        register(context, ACORN_DARK_OAK_TREE_PLACED_KEY, configuredFeatures.getOrThrow(FIConfiguredFeatures.ACORN_TREE_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(0, 0.1f, 1), Blocks.DARK_OAK_SAPLING));
        register(context, LILAC_TREE_PLACED_KEY, configuredFeatures.getOrThrow(FIConfiguredFeatures.LILAC_TREE_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(1, 0.1f, 1), Blocks.LILAC));
        register(context, SPRUCE_TIP_TREE_PLACED_KEY, configuredFeatures.getOrThrow(FIConfiguredFeatures.SPRUCE_TIP_TREE_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(1, 0.2f, 1), Blocks.SPRUCE_SAPLING));
        register(context, SAPPY_BIRCH_TREE_PLACED_KEY, configuredFeatures.getOrThrow(FIConfiguredFeatures.SAPPY_BIRCH_TREE_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(1, 0.2f, 1), Blocks.BIRCH_SAPLING));
        //Biomes
        register(context, DARK_WOODLANDS_TREES_PLACED_KEY, configuredFeatures.getOrThrow(FIConfiguredFeatures.DARK_WOODLANDS_TREES_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(2, 0.25f, 2), Blocks.DARK_OAK_SAPLING));
        register(context, DARK_OAK_BUSH_PLACED_KEY, configuredFeatures.getOrThrow(FIConfiguredFeatures.DARK_OAK_BUSH_KEY),
                List.of(CountPlacement.of(3), InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES),
                        BiomeFilter.biome()));
        register(context, WOODLANDS_PATCH_PLACED_KEY, configuredFeatures.getOrThrow(FIConfiguredFeatures.WOODLANDS_PATCH_KEY),
                List.of(RarityFilter.onAverageOnceEvery(8), InSquarePlacement.spread(), HeightmapPlacement
                        .onHeightmap(Heightmap.Types.MOTION_BLOCKING), BiomeFilter.biome()));

        //Wild Flower + Plants
        register(context, ROSELLE_PATCH_PLACED_KEY, configuredFeatures.getOrThrow(FIConfiguredFeatures.ROSELLE_BUSH_PATCH_KEY),
                List.of(RarityFilter.onAverageOnceEvery(65), InSquarePlacement.spread(), HeightmapPlacement
                        .onHeightmap(Heightmap.Types.MOTION_BLOCKING), BiomeFilter.biome(), BiomeTagFilter.biomeIsInTag(BiomeTags.IS_OVERWORLD)));
        register(context, BEACH_ROSE_PATCH_PLACED_KEY, configuredFeatures.getOrThrow(FIConfiguredFeatures.BEACH_ROSE_PATCH_KEY),
                List.of(RarityFilter.onAverageOnceEvery(70), InSquarePlacement.spread(), HeightmapPlacement
                        .onHeightmap(Heightmap.Types.MOTION_BLOCKING), BiomeFilter.biome(), BiomeTagFilter.biomeIsInTag(FITags.BiomeTag.HAS_BEACH_ROSES)));
        register(context, WOODLAND_FERN_PATCH_PLACED_KEY, configuredFeatures.getOrThrow(FIConfiguredFeatures.WOODLAND_FERN_PATCH_KEY),
                List.of(RarityFilter.onAverageOnceEvery(6), InSquarePlacement.spread(), HeightmapPlacement
                        .onHeightmap(Heightmap.Types.MOTION_BLOCKING), BiomeFilter.biome(), BiomeTagFilter.biomeIsInTag(FITags.BiomeTag.HAS_OAK_FERNS)));
        register(context, PHLOX_PATCH_PLACED_KEY, configuredFeatures.getOrThrow(FIConfiguredFeatures.PHLOX_PATCH_KEY),
                List.of(RarityFilter.onAverageOnceEvery(10), InSquarePlacement.spread(), HeightmapPlacement
                        .onHeightmap(Heightmap.Types.MOTION_BLOCKING), BiomeFilter.biome()));

        //Forest Litter
        register(context, OAK_SUSPICIOUS_LEAF_LITTER_PATCH_PLACED_KEY, configuredFeatures.getOrThrow(FIConfiguredFeatures.OAK_SUSPICIOUS_LEAF_LITTER_PATCH_KEY),
                List.of(RarityFilter.onAverageOnceEvery(6), InSquarePlacement.spread(), HeightmapPlacement
                        .onHeightmap(Heightmap.Types.MOTION_BLOCKING), BiomeFilter.biome(), BiomeTagFilter.biomeIsInTag(FITags.BiomeTag.HAS_OAK_FOREST_LITTER)));
        register(context, BIRCH_SUSPICIOUS_LEAF_LITTER_PATCH_PLACED_KEY, configuredFeatures.getOrThrow(FIConfiguredFeatures.BIRCH_SUSPICIOUS_LEAF_LITTER_PATCH_KEY),
                List.of(RarityFilter.onAverageOnceEvery(6), InSquarePlacement.spread(), HeightmapPlacement
                        .onHeightmap(Heightmap.Types.MOTION_BLOCKING), BiomeFilter.biome(), BiomeTagFilter.biomeIsInTag(FITags.BiomeTag.HAS_BIRCH_FOREST_LITTER)));
        register(context, SPRUCE_SUSPICIOUS_LEAF_LITTER_PATCH_PLACED_KEY, configuredFeatures.getOrThrow(FIConfiguredFeatures.SPRUCE_SUSPICIOUS_LEAF_LITTER_PATCH_KEY),
                List.of(RarityFilter.onAverageOnceEvery(6), InSquarePlacement.spread(), HeightmapPlacement
                        .onHeightmap(Heightmap.Types.MOTION_BLOCKING), BiomeFilter.biome(), BiomeTagFilter.biomeIsInTag(FITags.BiomeTag.HAS_SPRUCE_FOREST_LITTER)));
        register(context, DARK_OAK_SUSPICIOUS_LEAF_LITTER_PATCH_PLACED_KEY, configuredFeatures.getOrThrow(FIConfiguredFeatures.DARK_OAK_SUSPICIOUS_LEAF_LITTER_PATCH_KEY),
                List.of(RarityFilter.onAverageOnceEvery(6), InSquarePlacement.spread(), HeightmapPlacement
                        .onHeightmap(Heightmap.Types.MOTION_BLOCKING), BiomeFilter.biome(), BiomeTagFilter.biomeIsInTag(FITags.BiomeTag.HAS_DARK_OAK_FOREST_LITTER)));
        register(context, DARK_WOODLANDS_SUSPICIOUS_LEAF_LITTER_PATCH_PLACED_KEY, configuredFeatures.getOrThrow(FIConfiguredFeatures.DARK_OAK_SUSPICIOUS_LEAF_LITTER_PATCH_KEY),
                List.of(RarityFilter.onAverageOnceEvery(8), InSquarePlacement.spread(), HeightmapPlacement
                        .onHeightmap(Heightmap.Types.MOTION_BLOCKING), BiomeFilter.biome()));
        register(context, FLOWER_SUSPICIOUS_LEAF_LITTER_PATCH_PLACED_KEY, configuredFeatures.getOrThrow(FIConfiguredFeatures.FLOWER_SUSPICIOUS_LEAF_LITTER_PATCH_KEY),
                List.of(RarityFilter.onAverageOnceEvery(6), InSquarePlacement.spread(), HeightmapPlacement
                        .onHeightmap(Heightmap.Types.MOTION_BLOCKING), BiomeFilter.biome(), BiomeTagFilter.biomeIsInTag(FITags.BiomeTag.HAS_FLOWER_FOREST_LITTER)));
        //Fallen Trees
        register(context, DARK_WOODLANDS_FALLEN_TREES_PLACED_KEY, configuredFeatures.getOrThrow(FIConfiguredFeatures.DARK_WOODLANDS_FALLEN_TREES_KEY),
                List.of(RarityFilter.onAverageOnceEvery(25), InSquarePlacement.spread(), HeightmapPlacement
                        .onHeightmap(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES), BiomeFilter.biome()));
        register(context, OAK_FALLEN_TREES_PLACED_KEY, configuredFeatures.getOrThrow(FIConfiguredFeatures.DARK_WOODLANDS_FALLEN_TREES_KEY),
                List.of(RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), HeightmapPlacement
                        .onHeightmap(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES), BiomeFilter.biome(), BiomeTagFilter.biomeIsInTag(FITags.BiomeTag.HAS_OAK_FOREST_LITTER)));
        register(context, BIRCH_FALLEN_TREES_PLACED_KEY, configuredFeatures.getOrThrow(FIConfiguredFeatures.DARK_WOODLANDS_FALLEN_TREES_KEY),
                List.of(RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), HeightmapPlacement
                        .onHeightmap(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES), BiomeFilter.biome(), BiomeTagFilter.biomeIsInTag(FITags.BiomeTag.HAS_BIRCH_FOREST_LITTER)));
        register(context, SPRUCE_FALLEN_TREES_PLACED_KEY, configuredFeatures.getOrThrow(FIConfiguredFeatures.DARK_WOODLANDS_FALLEN_TREES_KEY),
                List.of(RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), HeightmapPlacement
                        .onHeightmap(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES), BiomeFilter.biome(), BiomeTagFilter.biomeIsInTag(FITags.BiomeTag.HAS_SPRUCE_FOREST_LITTER)));
        register(context, DARK_OAK_FALLEN_TREES_PLACED_KEY, configuredFeatures.getOrThrow(FIConfiguredFeatures.DARK_WOODLANDS_FALLEN_TREES_KEY),
                List.of(RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), HeightmapPlacement
                        .onHeightmap(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES), BiomeFilter.biome(), BiomeTagFilter.biomeIsInTag(FITags.BiomeTag.HAS_DARK_OAK_FOREST_LITTER)));

        register(context, PUDDLE_PLACED_KEY, configuredFeatures.getOrThrow(FIConfiguredFeatures.PUDDLE_KEY),
                List.of(RarityFilter.onAverageOnceEvery(9), InSquarePlacement.spread(), HeightmapPlacement
                        .onHeightmap(Heightmap.Types.MOTION_BLOCKING), BiomeFilter.biome(), BiomeTagFilter.biomeIsInTag(FITags.BiomeTag.HAS_PUDDLES)));

    }

    private static void register(BootstapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?, ?>> configuration,
                                 List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }
}
