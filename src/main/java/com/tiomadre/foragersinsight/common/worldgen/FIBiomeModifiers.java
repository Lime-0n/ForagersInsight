package com.tiomadre.foragersinsight.common.worldgen;

import com.tiomadre.foragersinsight.core.ForagersInsight;
import com.tiomadre.foragersinsight.data.server.tags.FITags;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.registries.ForgeRegistries;

public class FIBiomeModifiers {
    public static final ResourceKey<BiomeModifier> ADD_APPLE_TREES = registerKey("add_apple_trees");
    public static final ResourceKey<BiomeModifier> ADD_ACORN_TREES = registerKey("add_acorn_trees");
    public static final ResourceKey<BiomeModifier> ADD_LILAC_TREES = registerKey("add_lilac_trees");
    public static final ResourceKey<BiomeModifier> ADD_SPRUCE_TIP_TREES = registerKey("add_spruce_tip_trees");
    public static final ResourceKey<BiomeModifier> ADD_SAPPY_BIRCH_TREES = registerKey("add_sappy_birch_trees");
    public static final ResourceKey<BiomeModifier> ADD_MUSHROOM_BIRCH_TREES = registerKey("add_mushroom_birch_trees");

    public static final ResourceKey<BiomeModifier> ADD_ROSELLE_BUSHES = registerKey("add_roselle_bushes");
    public static final ResourceKey<BiomeModifier> ADD_BEACH_ROSES = registerKey("add_beach_roses");
    public static final ResourceKey<BiomeModifier> ADD_WOODLAND_FERNS = registerKey("add_woodland_ferns");
    public static final ResourceKey<BiomeModifier> ADD_GHOST_PIPE = registerKey("add_ghost_pipe");

    public static final ResourceKey<BiomeModifier> ADD_OAK_SUSPICIOUS_LEAF_LITTER = registerKey("add_oak_suspicious_leaf_litter");
    public static final ResourceKey<BiomeModifier> ADD_BIRCH_SUSPICIOUS_LEAF_LITTER = registerKey("add_birch_suspicious_leaf_litter");
    public static final ResourceKey<BiomeModifier> ADD_SPRUCE_SUSPICIOUS_LEAF_LITTER = registerKey("add_spruce_suspicious_leaf_litter");
    public static final ResourceKey<BiomeModifier> ADD_DARK_OAK_SUSPICIOUS_LEAF_LITTER = registerKey("add_dark_oak_suspicious_leaf_litter");
    public static final ResourceKey<BiomeModifier> ADD_FLOWER_SUSPICIOUS_LEAF_LITTER = registerKey("add_flower_suspicious_leaf_litter");

    public static final ResourceKey<BiomeModifier> ADD_OAK_FALLEN_TREES = registerKey("add_oak_fallen_trees");
    public static final ResourceKey<BiomeModifier> ADD_BIRCH_FALLEN_TREES = registerKey("add_birch_fallen_trees");
    public static final ResourceKey<BiomeModifier> ADD_SPRUCE_FALLEN_TREES = registerKey("add_spruce_fallen_trees");
    public static final ResourceKey<BiomeModifier> ADD_DARK_OAK_FALLEN_TREES = registerKey("add_dark_oak_fallen_trees");
    public static final ResourceKey<BiomeModifier> ADD_FLOWER_FALLEN_TREES = registerKey("add_flower_fallen_trees");

    public static final ResourceKey<BiomeModifier> ADD_PUDDLES = registerKey("add_puddles");

    public static void bootstap(BootstapContext<BiomeModifier> context) {
        var placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        var biomes = context.lookup(Registries.BIOME);
        //Tree Stuff
        context.register(ADD_APPLE_TREES, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                tagSet(biomes, FITags.BiomeTag.HAS_APPLE_TREES),
                HolderSet.direct(placedFeatures.getOrThrow(FIPlacedFeatures.APPLE_TREE_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION)
        );

        context.register(ADD_ACORN_TREES, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                tagSet(biomes, FITags.BiomeTag.HAS_ACORN_TREES),
                HolderSet.direct(placedFeatures.getOrThrow(FIPlacedFeatures.ACORN_DARK_OAK_TREE_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION)
        );
        context.register(ADD_LILAC_TREES, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                tagSet(biomes, FITags.BiomeTag.HAS_LILAC_TREES),
                HolderSet.direct(placedFeatures.getOrThrow(FIPlacedFeatures.LILAC_TREE_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION)
        );

        context.register(ADD_SPRUCE_TIP_TREES, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                tagSet(biomes, FITags.BiomeTag.HAS_SPRUCE_TIP_TREES),
                HolderSet.direct(placedFeatures.getOrThrow(FIPlacedFeatures.SPRUCE_TIP_TREE_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION)
        );

        context.register(ADD_SAPPY_BIRCH_TREES, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                        tagSet(biomes, FITags.BiomeTag.HAS_SAPPY_BIRCH_TREES),
                        HolderSet.direct(placedFeatures.getOrThrow(FIPlacedFeatures.SAPPY_BIRCH_TREE_PLACED_KEY)),
                        GenerationStep.Decoration.VEGETAL_DECORATION)
        );
        context.register(ADD_MUSHROOM_BIRCH_TREES, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                tagSet(biomes, FITags.BiomeTag.HAS_BIRCH_FOREST_LITTER),
                HolderSet.direct(placedFeatures.getOrThrow(FIPlacedFeatures.MUSHROOM_BIRCH_TREE_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION)
        );
        //Wild Flower Patches
        context.register(ADD_ROSELLE_BUSHES, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                tagSet(biomes, FITags.BiomeTag.HAS_ROSELLE_BUSHES),
                HolderSet.direct(placedFeatures.getOrThrow(FIPlacedFeatures.ROSELLE_PATCH_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION)
        );
        context.register(ADD_BEACH_ROSES, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                tagSet(biomes, FITags.BiomeTag.HAS_BEACH_ROSES),
                HolderSet.direct(placedFeatures.getOrThrow(FIPlacedFeatures.BEACH_ROSE_PATCH_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION)
        );
                //Suspicious Litter Patches
        context.register(ADD_OAK_SUSPICIOUS_LEAF_LITTER, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                tagSet(biomes, FITags.BiomeTag.HAS_OAK_FOREST_LITTER),
                HolderSet.direct(placedFeatures.getOrThrow(FIPlacedFeatures.OAK_SUSPICIOUS_LEAF_LITTER_PATCH_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION)
        );
        context.register(ADD_BIRCH_SUSPICIOUS_LEAF_LITTER, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                tagSet(biomes, FITags.BiomeTag.HAS_BIRCH_FOREST_LITTER),
                HolderSet.direct(placedFeatures.getOrThrow(FIPlacedFeatures.BIRCH_SUSPICIOUS_LEAF_LITTER_PATCH_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION)
        );
        context.register(ADD_SPRUCE_SUSPICIOUS_LEAF_LITTER, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                tagSet(biomes, FITags.BiomeTag.HAS_SPRUCE_FOREST_LITTER),
                HolderSet.direct(placedFeatures.getOrThrow(FIPlacedFeatures.SPRUCE_SUSPICIOUS_LEAF_LITTER_PATCH_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION)
        );
        context.register(ADD_DARK_OAK_SUSPICIOUS_LEAF_LITTER, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                tagSet(biomes, FITags.BiomeTag.HAS_DARK_OAK_FOREST_LITTER),
                HolderSet.direct(placedFeatures.getOrThrow(FIPlacedFeatures.DARK_OAK_SUSPICIOUS_LEAF_LITTER_PATCH_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION)
        );
        context.register(ADD_FLOWER_SUSPICIOUS_LEAF_LITTER, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                tagSet(biomes, FITags.BiomeTag.HAS_FLOWER_FOREST_LITTER),
                HolderSet.direct(placedFeatures.getOrThrow(FIPlacedFeatures.FLOWER_SUSPICIOUS_LEAF_LITTER_PATCH_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION)
        );
                //Other Flora Patches
        context.register(ADD_WOODLAND_FERNS, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                tagSet(biomes, FITags.BiomeTag.HAS_WOODLAND_FERNS),
                HolderSet.direct(placedFeatures.getOrThrow(FIPlacedFeatures.WOODLAND_FERN_PATCH_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION)
                //Fallen Trees
        );
        context.register(ADD_OAK_FALLEN_TREES, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                tagSet(biomes, FITags.BiomeTag.HAS_OAK_FOREST_LITTER),
                HolderSet.direct(placedFeatures.getOrThrow(FIPlacedFeatures.OAK_FALLEN_TREES_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION)
        );
        context.register(ADD_BIRCH_FALLEN_TREES, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                tagSet(biomes, FITags.BiomeTag.HAS_BIRCH_FOREST_LITTER),
                HolderSet.direct(placedFeatures.getOrThrow(FIPlacedFeatures.BIRCH_FALLEN_TREES_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION)
        );
        context.register(ADD_SPRUCE_FALLEN_TREES, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                tagSet(biomes, FITags.BiomeTag.HAS_SPRUCE_FOREST_LITTER),
                HolderSet.direct(placedFeatures.getOrThrow(FIPlacedFeatures.SPRUCE_FALLEN_TREES_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION)
        );
        context.register(ADD_DARK_OAK_FALLEN_TREES, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                tagSet(biomes, FITags.BiomeTag.HAS_DARK_OAK_FOREST_LITTER),
                HolderSet.direct(placedFeatures.getOrThrow(FIPlacedFeatures.DARK_OAK_FALLEN_TREES_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION)
        );
        context.register(ADD_FLOWER_FALLEN_TREES, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                tagSet(biomes, FITags.BiomeTag.HAS_FLOWER_FOREST_LITTER),
                HolderSet.direct(placedFeatures.getOrThrow(FIPlacedFeatures.FLOWER_FALLEN_TREES_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION)
        );  //Other
        context.register(ADD_PUDDLES, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                tagSet(biomes, FITags.BiomeTag.HAS_PUDDLES),
                HolderSet.direct(placedFeatures.getOrThrow(FIPlacedFeatures.PUDDLE_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION)
        );

    }


    public static ResourceKey<BiomeModifier> registerKey(String name) {
        return ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, ForagersInsight.rl(name));
    }

    private static <T> HolderSet<T> tagSet(HolderGetter<T> lookup, TagKey<T> tag) {
        return lookup.getOrThrow(tag);
    }

}