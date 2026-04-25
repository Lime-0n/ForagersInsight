package com.tiomadre.foragersinsight.core.registry;

import com.tiomadre.foragersinsight.common.block.SuspiciousLitterBlock;
import com.tiomadre.foragersinsight.core.ForagersInsight;
import net.minecraft.resources.ResourceLocation;

public final class FIForageLoot {

    public static final ResourceLocation SUSPICIOUS_LEAF_LITTER_OAK = ForagersInsight.rl("archaeology/suspicious_leaf_litter/oak");
    public static final ResourceLocation SUSPICIOUS_LEAF_LITTER_BIRCH = ForagersInsight.rl("archaeology/suspicious_leaf_litter/birch");
    public static final ResourceLocation SUSPICIOUS_LEAF_LITTER_SPRUCE = ForagersInsight.rl("archaeology/suspicious_leaf_litter/spruce");
    public static final ResourceLocation SUSPICIOUS_LEAF_LITTER_DARK_OAK = ForagersInsight.rl("archaeology/suspicious_leaf_litter/dark_oak");
    public static final ResourceLocation SUSPICIOUS_LEAF_LITTER_FLOWER = ForagersInsight.rl("archaeology/suspicious_leaf_litter/flower");

    private FIForageLoot() {
    }

    public static ResourceLocation suspiciousLeafLitterByFoliage(SuspiciousLitterBlock.FoliageType foliageType) {
        return switch (foliageType) {
            case BIRCH -> SUSPICIOUS_LEAF_LITTER_BIRCH;
            case SPRUCE -> SUSPICIOUS_LEAF_LITTER_SPRUCE;
            case DARK_OAK -> SUSPICIOUS_LEAF_LITTER_DARK_OAK;
            case FLOWER -> SUSPICIOUS_LEAF_LITTER_FLOWER;
            case OAK -> SUSPICIOUS_LEAF_LITTER_OAK;
        };
    }
}