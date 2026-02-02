package com.tiomadre.foragersinsight.common.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import vectorwing.farmersdelight.common.block.RichSoilBlock;

import java.util.Optional;

public final class LilacTreeGrowth {
    private static final int LILAC_TREE_CHANCE = 8;

    private LilacTreeGrowth() {
    }

    public static void tryGrowFromRandomTick(ServerLevel level, BlockPos soilPos, RandomSource random) {
        if (!shouldGrow(random)) {
            return;
        }

        tryGrowLilacTree(level, soilPos, random);
    }

    public static boolean shouldGrow(RandomSource random) {
        return random.nextInt(LILAC_TREE_CHANCE) == 0;
    }

    public static boolean tryGrowLilacTree(ServerLevel level, BlockPos soilPos, RandomSource random) {
        BlockState soilState = level.getBlockState(soilPos);
        if (!(soilState.getBlock() instanceof RichSoilBlock)) {
            return false;
        }

        BlockPos lilacPos = soilPos.above();
        BlockState lowerLilacState = level.getBlockState(lilacPos);
        if (!lowerLilacState.is(Blocks.LILAC) || lowerLilacState.getValue(DoublePlantBlock.HALF) != DoubleBlockHalf.LOWER) {
            return false;
        }

        BlockPos upperLilacPos = lilacPos.above();
        BlockState upperLilacState = level.getBlockState(upperLilacPos);
        if (!upperLilacState.is(Blocks.LILAC) || upperLilacState.getValue(DoublePlantBlock.HALF) != DoubleBlockHalf.UPPER) {
            return false;
        }

        Optional<ConfiguredFeature<?, ?>> configuredFeature = level.registryAccess()
                .registryOrThrow(Registries.CONFIGURED_FEATURE)
                .getHolder(FIConfiguredFeatures.LILAC_TREE_KEY)
                .map(Holder.Reference::value);

        if (configuredFeature.isEmpty()) {
            return false;
        }

        level.setBlock(soilPos, Blocks.ROOTED_DIRT.defaultBlockState(), Block.UPDATE_ALL);
        level.setBlock(lilacPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        level.setBlock(upperLilacPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);

        boolean placed = configuredFeature.get().place(
                level,
                level.getChunkSource().getGenerator(),
                random,
                lilacPos
        );

        if (!placed) {
            level.setBlock(soilPos, soilState, Block.UPDATE_ALL);
            level.setBlock(lilacPos, lowerLilacState, Block.UPDATE_ALL);
            level.setBlock(upperLilacPos, upperLilacState, Block.UPDATE_ALL);
        }

        return placed;
    }
}