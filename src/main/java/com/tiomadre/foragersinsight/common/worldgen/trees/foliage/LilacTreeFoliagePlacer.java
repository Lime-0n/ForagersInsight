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

    private static final String[] TOPMOST_LAYER = {
            ".X.",
            "XXX",
            ".X."
    };
    private static final String[] TOP_MIDDLE_LAYER = {
            "XXX",
            "XXX",
            "XXX"
    };
    private static final String[] MIDDLE_LAYER = {
            ".XxX.",
            "XXXXX",
            "xXXXx",
            "XXXXX",
            ".XxX."
    };
    private static final String[] BOTTOM_LAYER = {
            ".x.",
            "xXx",
            ".x."
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
        BlockPos basePos = attachment.pos();
        placeLayerPattern(level, blockSetter, config, basePos, offset + 1, TOPMOST_LAYER);
        placeLayerPattern(level, blockSetter, config, basePos, offset, TOP_MIDDLE_LAYER);
        placeLayerPattern(level, blockSetter, config, basePos, offset - 1, MIDDLE_LAYER);
        placeLayerPattern(level, blockSetter, config, basePos, offset - 2, BOTTOM_LAYER);
    }

    private void placeLayerPattern(@NotNull LevelSimulatedReader level, @NotNull FoliageSetter setter,
                                   @NotNull TreeConfiguration config, @NotNull BlockPos pos, int localY,
                                   String[] pattern) {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        int half = pattern.length / 2;

        for (int z = 0; z < pattern.length; ++z) {
            String row = pattern[z];
            for (int x = 0; x < row.length(); ++x) {
                char symbol = row.charAt(x);
                if (symbol != 'X' && symbol != 'x') {
                    continue;
                }
                mutablePos.setWithOffset(pos, x - half, localY, z - half);
                boolean shouldBlossom = symbol == 'x';
                tryPlaceLeaf(level, setter, config, mutablePos, shouldBlossom);
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

    protected static boolean tryPlaceLeaf(LevelSimulatedReader level, @NotNull FoliageSetter setter,
                                          @NotNull TreeConfiguration config, @NotNull BlockPos pos, boolean placeBlossom) {
        if (!TreeFeature.validTreePos(level, pos)) {
            return false;
        }

        BlockState blockstate = (placeBlossom ? FIBlocks.BLOSSOMING_LILAC_LEAVES.get() : FIBlocks.LILAC_LEAVES.get()).defaultBlockState();
        if (blockstate.hasProperty(BlockStateProperties.WATERLOGGED)) {
            blockstate = blockstate.setValue(BlockStateProperties.WATERLOGGED,
                    level.isFluidAtPosition(pos, fluidState -> fluidState.isSourceOfType(Fluids.WATER)));
        }

        setter.set(pos, blockstate);
        return true;
    }

}