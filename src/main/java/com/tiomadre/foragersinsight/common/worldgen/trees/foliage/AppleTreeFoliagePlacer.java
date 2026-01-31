package com.tiomadre.foragersinsight.common.worldgen.trees.foliage;

import com.tiomadre.foragersinsight.core.registry.FIBlocks;
import com.tiomadre.foragersinsight.core.registry.FIFoliagePlacerType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AppleTreeFoliagePlacer extends FoliagePlacer {
    protected final int height;

    public static final Codec<AppleTreeFoliagePlacer> CODEC = RecordCodecBuilder
            .create(instance -> foliagePlacerParts(instance)
                    .and(Codec.intRange(0, 16).fieldOf("height").forGetter(fp -> fp.height)).apply(instance, AppleTreeFoliagePlacer::new));

    public AppleTreeFoliagePlacer(IntProvider pRadius, IntProvider pOffset, int height) {
        super(pRadius, pOffset);
        this.height = height;
    }

    @Override
    protected @NotNull FoliagePlacerType<?> type() {
        return FIFoliagePlacerType.APPLE_FOLIAGE_PLACER.get();
    }

    @Override
    protected void createFoliage(@NotNull LevelSimulatedReader level, @NotNull FoliageSetter blockSetter, @NotNull RandomSource rand, @NotNull TreeConfiguration config,
                                 int pMaxFreeTreeHeight, @NotNull FoliageAttachment attachment, int height, int radius, int offset) {

        List<BlockPos> leafPositions = new ArrayList<>();
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (int i = offset; i >= offset - height; --i) {
            int j = Math.max(radius + attachment.radiusOffset() - 1 - i / 2, 0);
            this.collectLeavesRow(level, rand, attachment.pos(), j, i, attachment.doubleTrunk(), mutablePos, leafPositions);
        }

        shufflePositions(rand, leafPositions);
        int bountifulLimit = Math.min(5, leafPositions.size());
        for (int i = 0; i < leafPositions.size(); i++) {
            BlockState blockState = i < bountifulLimit
                    ? FIBlocks.BOUNTIFUL_OAK_LEAVES.get().defaultBlockState()
                    : Blocks.OAK_LEAVES.defaultBlockState();
            placeLeafBlock(level, blockSetter, blockState, leafPositions.get(i));
        }
    }

    private void collectLeavesRow(@NotNull LevelSimulatedReader pLevel, @NotNull RandomSource pRandom, @NotNull BlockPos pPos, int pRange, int pLocalY, boolean pLarge, BlockPos.MutableBlockPos pMutablePos, List<BlockPos> pLeafPositions) {
        int i = pLarge ? 1 : 0;

        for(int j = -pRange; j <= pRange + i; ++j) {
            for(int k = -pRange; k <= pRange + i; ++k) {
                if (!this.shouldSkipLocationSigned(pRandom, j, pLocalY, k, pRange, pLarge)) {
                    pMutablePos.setWithOffset(pPos, j, pLocalY, k);
                    if (TreeFeature.validTreePos(pLevel, pMutablePos)) {
                        pLeafPositions.add(pMutablePos.immutable());
                    }
                }
            }
        }

    }

    @Override
    public int foliageHeight(@NotNull RandomSource pRandom, int pHeight, @NotNull TreeConfiguration pConfig) {
        return this.height;
    }

    @Override
    protected boolean shouldSkipLocation(@NotNull RandomSource pRandom, int pLocalX, int pLocalY, int pLocalZ, int pRange, boolean pLarge) {
        return pLocalX == pRange && pLocalZ == pRange && (pRandom.nextInt(2) == 0 || pLocalY == 0);
    }

    private static void shufflePositions(RandomSource pRandom, List<BlockPos> pPositions) {
        for (int i = pPositions.size() - 1; i > 0; i--) {
            int swapIndex = pRandom.nextInt(i + 1);
            if (swapIndex != i) {
                BlockPos temp = pPositions.get(i);
                pPositions.set(i, pPositions.get(swapIndex));
                pPositions.set(swapIndex, temp);
            }
        }
    }

    private static void placeLeafBlock(LevelSimulatedReader pLevel, FoliagePlacer.@NotNull FoliageSetter pFoliageSetter, BlockState pBlockState, @NotNull BlockPos pPos) {
        BlockState blockstate = pBlockState;
        if (blockstate.hasProperty(BlockStateProperties.WATERLOGGED)) {
            blockstate = blockstate.setValue(BlockStateProperties.WATERLOGGED, pLevel.isFluidAtPosition(pPos, fluidState -> fluidState.isSourceOfType(Fluids.WATER)));
        }

        pFoliageSetter.set(pPos, blockstate);
    }
}