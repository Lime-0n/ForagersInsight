package com.tiomadre.foragersinsight.core.registry;

import com.teamabnormals.blueprint.core.annotations.ConfigKey;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class FIConfig {

    public static class Common {

        @ConfigKey("config")
        public final ModConfigSpec.DoubleValue xpGlobalMultiplier;
        public final ModConfigSpec.BooleanValue enableCropHarvestXP;
        public final ModConfigSpec.BooleanValue enableForagingXP;
        public final ModConfigSpec.BooleanValue enableAnimalShearXP;
        public final ModConfigSpec.BooleanValue enableTapperXP;
        public final ModConfigSpec.BooleanValue enableBeehiveXP;
        public final ModConfigSpec.BooleanValue enableMilkingXP;
        public final ModConfigSpec.BooleanValue milkRemovesOdorous;


        public Common(ModConfigSpec.Builder builder) {

            // Farming + Foraging Multiplier
            builder.push("Farming & Foraging XP");

            this.xpGlobalMultiplier = builder
                    .comment("Global multiplier applied to ALL farming & foraging XP.")
                    .defineInRange("XP Global Multiplier", 1.0, 0.0, 100.0);

            builder.pop();

            // Foraging + Farming XP Toggles
            builder.push("XP Source Toggles");

            this.enableCropHarvestXP = builder
                    .comment("Enable XP from crop harvesting, this includes knife-breaking crops for secondary drops.")
                    .define("Crop Harvest XP", true);

            this.enableForagingXP = builder
                    .comment("Enable XP from knife-breaking wild crops and for right-click harvests like Bountiful Leaves, etc.")
                    .define("Foraging XP", true);

            this.enableAnimalShearXP = builder
                    .comment("Enable XP from shearing wool or feathers from sheep and chickens.")
                    .define("Animal Shear XP", true);

            this.enableTapperXP = builder
                    .comment("Enable XP from collecting from a full Tapper.")
                    .define("Tapper XP", true);

            this.enableBeehiveXP = builder
                    .comment("Enable XP from harvesting Honey or shearing Honeycomb")
                    .define("Beehive XP", true);

            this.enableMilkingXP = builder
                    .comment("Enable XP from milking cows.")
                    .define("Milking XP", true);

            builder.pop();

            builder.push("Effect Toggles");

            this.milkRemovesOdorous = builder
                    .comment("Allow milk to remove the Odorous effect.")
                    .define("Milk Removes Odorous", false);

            builder.pop();
        }
    }

    public static final ModConfigSpec COMMON_SPEC;
    public static final Common COMMON;

    static {
        Pair<Common, ModConfigSpec> pair =
                new ModConfigSpec.Builder().configure(Common::new);
        COMMON_SPEC = pair.getRight();
        COMMON = pair.getLeft();
    }

}
