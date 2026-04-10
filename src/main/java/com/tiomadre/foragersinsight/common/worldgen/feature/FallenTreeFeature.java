package com.tiomadre.foragersinsight.common.worldgen.feature;

import com.mojang.serialization.Codec;
import com.tiomadre.foragersinsight.common.block.HollowLogBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import com.tiomadre.foragersinsight.core.registry.FIBlocks;

public class FallenTreeFeature extends Feature<NoneFeatureConfiguration> {
    public FallenTreeFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();

        Direction direction = random.nextBoolean() ? Direction.EAST : Direction.NORTH;
        Direction sideA = direction.getClockWise();
        Direction sideB = direction.getCounterClockWise();
        int logLength = 3 + random.nextInt(2);

        for (int i = 0; i < logLength; i++) {
            BlockPos logPos = origin.relative(direction, i);
            if (!canPlaceOnGround(level, logPos)) {
                return false;
            }
        }
        BlockState fallenLogState = FIBlocks.HOLLOW_LOG.get().defaultBlockState()
                .setValue(RotatedPillarBlock.AXIS, direction.getAxis())
                .setValue(HollowLogBlock.LOG_TEXTURE, HollowLogBlock.LogTexture.DARK_OAK);
        boolean placed = false;
        for (int i = 0; i < logLength; i++) {
            BlockPos logPos = origin.relative(direction, i);
            this.setBlock(level, logPos, fallenLogState);
            placed = true;

            placeGrassNextToLog(level, random, logPos.relative(sideA));
            placeGrassNextToLog(level, random, logPos.relative(sideB));
        }

        return placed;
    }

    private static boolean canPlaceOnGround(WorldGenLevel level, BlockPos pos) {
        BlockPos belowPos = pos.below();
        return level.getBlockState(pos).canBeReplaced()
                && !level.getFluidState(pos).isSource()
                && level.getBlockState(belowPos).isFaceSturdy(level, belowPos, Direction.UP);
    }

    private static void placeGrassNextToLog(WorldGenLevel level, RandomSource random, BlockPos pos) {
        if (random.nextFloat() > 0.45F || !canPlaceOnGround(level, pos)) {
            return;
        }

        if (random.nextFloat() < 0.35F && canPlaceDoublePlant(level, pos)) {
            level.setBlock(pos, Blocks.TALL_GRASS.defaultBlockState().setValue(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER), 2);
            level.setBlock(pos.above(), Blocks.TALL_GRASS.defaultBlockState().setValue(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER), 2);
            return;
        }

        level.setBlock(pos, Blocks.GRASS.defaultBlockState(), 2);
    }

    private static boolean canPlaceDoublePlant(WorldGenLevel level, BlockPos pos) {
        BlockPos belowPos = pos.below();
        return level.getBlockState(pos).canBeReplaced()
                && level.getBlockState(pos.above()).canBeReplaced()
                && level.getBlockState(belowPos).isFaceSturdy(level, belowPos, Direction.UP);
    }
}