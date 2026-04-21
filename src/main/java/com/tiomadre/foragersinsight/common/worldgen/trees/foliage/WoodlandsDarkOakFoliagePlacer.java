package com.tiomadre.foragersinsight.common.worldgen.trees.foliage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tiomadre.foragersinsight.core.registry.FIFoliagePlacerType;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import org.jetbrains.annotations.NotNull;

public class WoodlandsDarkOakFoliagePlacer extends FoliagePlacer {
    private static final String[] CROSS_LAYER = {
            ".X.",
            "XXX",
            ".X."
    };
    private static final String[] SECOND_LAYER = {
            "..XXX..",
            ".XXXXX.",
            "XXXXXXX",
            "XXXXXXX",
            "XXXXXXX",
            ".XXXXX.",
            "..XXX.."
    };
    private static final String[] THIRD_LAYER = {
            "..XXX..",
            ".XXXXX.",
            ".XXXXX.",
            ".XXXXX.",
            "..XXX.."
    };

    public static final Codec<WoodlandsDarkOakFoliagePlacer> CODEC = RecordCodecBuilder.create(instance ->
            foliagePlacerParts(instance).apply(instance, WoodlandsDarkOakFoliagePlacer::new));

    public WoodlandsDarkOakFoliagePlacer(IntProvider radius, IntProvider offset) {
        super(radius, offset);
    }

    @Override
    protected @NotNull FoliagePlacerType<?> type() {
        return FIFoliagePlacerType.WOODLANDS_DARK_OAK_FOLIAGE_PLACER.get();
    }

    @Override
    protected void createFoliage(@NotNull LevelSimulatedReader level, @NotNull FoliageSetter setter, @NotNull RandomSource random,
                                 @NotNull TreeConfiguration config, int maxFreeTreeHeight, @NotNull FoliageAttachment attachment,
                                 int foliageHeight, int radius, int offset) {
        BlockPos center = attachment.pos();
        placeLayerPattern(level, setter, random, config, center, offset - 1, CROSS_LAYER);
        placeLayerPattern(level, setter, random, config, center, offset, SECOND_LAYER);
        placeLayerPattern(level, setter, random, config, center, offset + 1, THIRD_LAYER);
        placeLayerPattern(level, setter, random, config, center, offset + 2, CROSS_LAYER);
    }

    private void placeLayerPattern(@NotNull LevelSimulatedReader level, @NotNull FoliageSetter setter,
                                   @NotNull RandomSource random, @NotNull TreeConfiguration config,
                                   @NotNull BlockPos center, int localY, String[] pattern) {
        int halfWidth = pattern[0].length() / 2;
        int halfDepth = pattern.length / 2;
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        for (int z = 0; z < pattern.length; z++) {
            String row = pattern[z];
            for (int x = 0; x < row.length(); x++) {
                if (row.charAt(x) != 'X') {
                    continue;
                }

                mutablePos.setWithOffset(center, x - halfWidth, localY, z - halfDepth);
                tryPlaceLeaf(level, setter, random, config, mutablePos);
            }
        }
    }

    @Override
    public int foliageHeight(@NotNull RandomSource random, int height, @NotNull TreeConfiguration config) {
        return 3;
    }

    @Override
    protected boolean shouldSkipLocation(@NotNull RandomSource random, int localX, int localY, int localZ, int range, boolean large) {
        return false;
    }
}