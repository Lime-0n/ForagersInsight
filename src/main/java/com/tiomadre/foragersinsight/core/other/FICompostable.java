package com.tiomadre.foragersinsight.core.other;


import com.teamabnormals.blueprint.core.util.DataUtil;
import net.minecraft.world.level.ItemLike;

import java.util.function.Supplier;

import static com.tiomadre.foragersinsight.core.registry.FIBlocks.*;
import static com.tiomadre.foragersinsight.core.registry.FIItems.*;

public class FICompostable {
    public static void registerCompat() {
        registerCompostable();
    }

    private static void registerCompostable() {
     // Leaves and Saplings
        registerCompostables(0.3f,
                BOUNTIFUL_OAK_LEAVES, BOUNTIFUL_OAK_SAPLING, BOUNTIFUL_DARK_OAK_LEAVES, BOUNTIFUL_DARK_OAK_SAPLING,
                BOUNTIFUL_SPRUCE_LEAVES, BOUNTIFUL_SPRUCE_SAPLING);

     // Storage
        registerCompostables(1.0f,
                APPLE_CRATE,BLACK_ACORN_SACK, DANDELION_ROOT_SACK, POPPY_SEEDS_SACK,
                ROSE_HIP_SACK, ROSELLE_CALYX_SACK, SPRUCE_TIPS_SACK);

     // Decorative
        // Flowers & Petals
        registerCompostables(0.4f, ROSELLE_BUSH, TALL_BEACH_ROSE_BUSH);
        registerCompostables(0.3f, STOUT_BEACH_ROSE_BUSH);
        registerCompostables(0.2f, ROSE_PETALS, ROSELLE_PETALS);
        // Foliage Mats
            // Scattered
        registerCompostables(0.3f,
                SCATTERED_ROSE_PETAL_MAT, SCATTERED_ROSELLE_PETAL_MAT,
                SCATTERED_SPRUCE_TIP_MAT, SCATTERED_STRAW_MAT);
            // Dense
        registerCompostables(0.6f,
                DENSE_ROSE_PETAL_MAT, DENSE_ROSELLE_PETAL_MAT,
                DENSE_SPRUCE_TIP_MAT, DENSE_STRAW_MAT);

     // Crops
        registerCompostables(0.3f,
                APPLE_SLICE, BLACK_ACORN, DANDELION_ROOT, SPRUCE_TIPS, POPPY_SEEDS,
                ROSE_HIP, ROSELLE_CALYX);
     // Food
        registerCompostables(0.85f,
                ACORN_COOKIE, ROSE_COOKIE, CANDIED_CALYCES, APPLE_DIPPERS, KELP_WRAP,
                SEED_BUTTER_JAMWICH, JAMMY_BREAKFAST_SANDWICH,CREAMY_SALMON_BAGEL);
        registerCompostables(0.3f, WHEAT_FLOUR, ACORN_MEAL,POPPY_SEED_PASTE, COCOA_POWDER);
    }

    @SafeVarargs
    private static void registerCompostables(float chance, Supplier<? extends ItemLike>... items) {
        for (Supplier<? extends ItemLike> item : items) {
            DataUtil.registerCompostable(item.get(), chance);
        }
    }
}