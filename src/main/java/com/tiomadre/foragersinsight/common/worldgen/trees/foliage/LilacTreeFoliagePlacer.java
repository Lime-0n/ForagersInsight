package com.tiomadre.foragersinsight.common.worldgen.trees.foliage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tiomadre.foragersinsight.core.registry.FIBlocks;
import com.tiomadre.foragersinsight.core.registry.FIFoliagePlacerType;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;

public class LilacTreeFoliagePlacer extends FoliagePlacer {
    private static final int MAX_BLOSSOMS = 4;
    private static final int BLOSSOM_CHANCE = 5;
    private static final String[] TOP_LAYER = {
            "XXX",
            "XXX",
            "XXX"
    };
    private static final String[] MIDDLE_LAYER = {
            ".XXX.",
            "XXXXX",
            "XXXXX",
            "XXXXX",
            ".XXX."
    };
    private static final String[] BOTTOM_LAYER = {
            ".X.",
            "XXX",
            ".X."
    };
    protected final int height;

    public static final Codec<LilacTreeFoliagePlacer> CODEC = RecordCodecBuilder
            .create(instance -> foliagePlacerParts(instance)
                    .and(Codec.intRange(0, 16).fieldOf("height").forGetter(fp -> fp.height))
                    .apply(instance, LilacTreeFoliagePlacer::new));

    public LilacTreeFoliagePlacer(IntProvider radius, IntProvider offset, int height) {
        super(radius, offset);
        this.height = height;
    }

    @Override
    protected @NotNull FoliagePlacerType<?> type() {
        return FIFoliagePlacerType.LILAC_FOLIAGE_PLACER.get();
    }

    @Override
    protected void createFoliage(@NotNull LevelSimulatedReader level, @NotNull FoliageSetter blockSetter, @NotNull RandomSource rand,
                                 @NotNull TreeConfiguration config, int maxFreeTreeHeight, @NotNull FoliageAttachment attachment, int height,
                                 int radius, int offset) {
        int[] blossomsPlaced = {0};
        BlockPos basePos = attachment.pos();
        placeLayerPattern(level, blockSetter, rand, config, basePos, offset, blossomsPlaced, TOP_LAYER, false);
        placeLayerPattern(level, blockSetter, rand, config, basePos, offset - 1, blossomsPlaced, MIDDLE_LAYER, true);
        placeLayerPattern(level, blockSetter, rand, config, basePos, offset - 2, blossomsPlaced, BOTTOM_LAYER, true);
    }

    private void placeLayerPattern(@NotNull LevelSimulatedReader level, @NotNull FoliageSetter setter, @NotNull RandomSource rand,
                                   @NotNull TreeConfiguration config, @NotNull BlockPos pos, int localY, int[] blossomsPlaced,
                                   String[] pattern, boolean allowBlossoms) {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        int half = pattern.length / 2;

        for (int z = 0; z < pattern.length; ++z) {
            String row = pattern[z];
            for (int x = 0; x < row.length(); ++x) {
                if (row.charAt(x) != 'X') {
                    continue;
                }
                mutablePos.setWithOffset(pos, x - half, localY, z - half);
                tryPlaceLeaf(level, setter, rand, config, mutablePos, blossomsPlaced, allowBlossoms);
            }
        }
    }

    @Override
    public int foliageHeight(@NotNull RandomSource rand, int height, @NotNull TreeConfiguration config) {
        return this.height;
    }

    @Override
    protected boolean shouldSkipLocation(@NotNull RandomSource rand, int localX, int localY, int localZ, int range, boolean large) {
        return localX == range && localZ == range && (rand.nextInt(2) == 0 || localY == 0);
    }

    protected static boolean tryPlaceLeaf(LevelSimulatedReader level, @NotNull FoliageSetter setter, @NotNull RandomSource rand,
                                          @NotNull TreeConfiguration config, @NotNull BlockPos pos, int[] blossomsPlaced,
                                          boolean allowBlossoms) {
        if (!TreeFeature.validTreePos(level, pos)) {
            return false;
        }

        boolean canBlossom = allowBlossoms
                && blossomsPlaced[0] < MAX_BLOSSOMS
                && rand.nextInt(BLOSSOM_CHANCE) == 0
                && isExposedBelow(level, pos);
        BlockState blockstate = (canBlossom ? FIBlocks.BLOSSOMING_LILAC_LEAVES.get() : FIBlocks.LILAC_LEAVES.get()).defaultBlockState();
        if (blockstate.hasProperty(BlockStateProperties.WATERLOGGED)) {
            blockstate = blockstate.setValue(BlockStateProperties.WATERLOGGED,
                    level.isFluidAtPosition(pos, fluidState -> fluidState.isSourceOfType(Fluids.WATER)));
        }

        setter.set(pos, blockstate);
        if (canBlossom) {
            blossomsPlaced[0]++;
        }
        return true;
    }

    private static boolean isExposedBelow(LevelSimulatedReader level, BlockPos pos) {
        return level.isStateAtPosition(pos.below(), BlockState::isAir);
    }
}