package com.tiomadre.foragersinsight.core.registry;

import com.teamabnormals.blueprint.core.annotations.ConfigKey;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class FIConfig {

    public static class Common {

        @ConfigKey("config")
        public final ForgeConfigSpec.BooleanValue enableFlavorText;
        public final ForgeConfigSpec.DoubleValue xpGlobalMultiplier;
        public final ForgeConfigSpec.BooleanValue enableCropHarvestXP;
        public final ForgeConfigSpec.BooleanValue enableForagingXP;
        public final ForgeConfigSpec.BooleanValue enableAnimalShearXP;
        public final ForgeConfigSpec.BooleanValue enableTapperXP;
        public final ForgeConfigSpec.BooleanValue enableBeehiveXP;
        public final ForgeConfigSpec.BooleanValue enableMilkingXP;


        public Common(ForgeConfigSpec.Builder builder) {


            // Flavor Text Tooltips
            builder.push("Flavor Text");
            this.enableFlavorText = builder.comment("Enable Flavor Text tooltips.")
                    .define("Flavor Text", false);
            builder.pop();

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
        }
    }

    public static final ForgeConfigSpec COMMON_SPEC;
    public static final Common COMMON;

    static {
        Pair<Common, ForgeConfigSpec> pair =
                new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON_SPEC = pair.getRight();
        COMMON = pair.getLeft();
    }

    public static boolean isFlavorTextEnabled() {
        return COMMON.enableFlavorText.get();
    }
}
