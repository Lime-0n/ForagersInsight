package com.tiomadre.foragersinsight.common.worldgen.trees.foliage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tiomadre.foragersinsight.core.registry.FIBlocks;
import com.tiomadre.foragersinsight.core.registry.FIFoliagePlacerType;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
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
        for (int i = offset; i >= offset - height; --i) {
            int range = Math.max(radius + attachment.radiusOffset() - 1 - i / 2, 0);
            placeLeavesRow(level, blockSetter, rand, config, attachment.pos(), range, i, attachment.doubleTrunk(), blossomsPlaced);
        }
    }

    private void placeLeavesRow(@NotNull LevelSimulatedReader level, @NotNull FoliageSetter setter, @NotNull RandomSource rand,
                                @NotNull TreeConfiguration config, @NotNull BlockPos pos, int range, int localY, boolean large,
                                int[] blossomsPlaced) {
        int i = large ? 1 : 0;
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        for (int x = -range; x <= range + i; ++x) {
            for (int z = -range; z <= range + i; ++z) {
                if (!this.shouldSkipLocationSigned(rand, x, localY, z, range, large)) {
                    mutablePos.setWithOffset(pos, x, localY, z);
                    tryPlaceLeaf(level, setter, rand, config, mutablePos, blossomsPlaced);
                }
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
                                          @NotNull TreeConfiguration config, @NotNull BlockPos pos, int[] blossomsPlaced) {
        if (!TreeFeature.validTreePos(level, pos)) {
            return false;
        }

        boolean canBlossom = blossomsPlaced[0] < MAX_BLOSSOMS
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
        return !level.isStateAtPosition(pos.below(), state -> state.is(BlockTags.LEAVES) || state.is(BlockTags.LOGS));
    }
}