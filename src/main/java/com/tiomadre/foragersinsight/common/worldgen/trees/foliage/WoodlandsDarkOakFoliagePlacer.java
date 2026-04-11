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
        placeLeavesRow(level, setter, random, config, center, 2, 0, false); // 5x5
        placeLeavesRow(level, setter, random, config, center, 3, -1, false); // 7x7
        placeLeavesRow(level, setter, random, config, center, 1, -2, false); // 3x3

    }

    @Override
    public int foliageHeight(@NotNull RandomSource random, int height, @NotNull TreeConfiguration config) {
        return 3;
    }

    @Override
    protected boolean shouldSkipLocation(@NotNull RandomSource random, int localX, int localY, int localZ, int range, boolean large) {
        return range >= 2 && Math.abs(localX) == range;
    }

}