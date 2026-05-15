package com.tiomadre.foragersinsight.common.worldgen.feature;

import com.mojang.serialization.Codec;
import com.tiomadre.foragersinsight.common.block.ShallowBlock;
import com.tiomadre.foragersinsight.core.registry.FIBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.ArrayList;
import java.util.List;

public class PuddleFeature extends Feature<NoneFeatureConfiguration> {
    public PuddleFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin().below();

        List<BlockPos> puddleBlocks = new ArrayList<>();
        List<BlockPos> borderBlocks = new ArrayList<>();

        for (int x = -2; x <= 1; x++) {
            for (int z = -2; z <= 1; z++) {
                BlockPos currentPos = origin.offset(x, 0, z);
                if (!canPlacePuddle(level, currentPos)) {
                    return false;
                }

                float normX = (x + 0.5F) / 1.6F;
                float normZ = (z + 0.5F) / 1.6F;
                float distance = (normX * normX) + (normZ * normZ);
                float noise = random.nextFloat() * 0.35F;

                if (distance <= 0.95F + noise) {
                    puddleBlocks.add(currentPos);
                }
            }
        }

        if (puddleBlocks.size() < 6) {
            return false;
        }

        for (int x = -3; x <= 2; x++) {
            for (int z = -3; z <= 2; z++) {
                BlockPos currentPos = origin.offset(x, 0, z);
                if (puddleBlocks.contains(currentPos) || !canPlacePuddle(level, currentPos)) {
                    continue;
                }

                for (Direction direction : Direction.Plane.HORIZONTAL) {
                    if (puddleBlocks.contains(currentPos.relative(direction))) {
                        borderBlocks.add(currentPos);
                        break;
                    }
                }
            }
        }


        BlockState centerState = FIBlocks.CONDENSED_DIRT.get().defaultBlockState().setValue(ShallowBlock.WATERLOGGED, true);
        BlockState woodlandFernState = FIBlocks.WOODLAND_FERN.get().defaultBlockState();
        BlockState skunkCabbageState = FIBlocks.SKUNK_CABBAGE.get().defaultBlockState();

        for (BlockPos pos : puddleBlocks) {
            level.setBlock(pos, centerState, 2);
        }

        for (BlockPos pos : borderBlocks) {
            level.setBlock(pos, Blocks.GRASS_BLOCK.defaultBlockState(), 2);

            BlockPos plantPos = pos.above();
            BlockState plantState = random.nextFloat() < 0.75F ? woodlandFernState : skunkCabbageState;
            if (level.getBlockState(plantPos).canBeReplaced() && plantState.canSurvive(level, plantPos)) {
                level.setBlock(plantPos, plantState, 2);
            }
        }

        return true;
    }

    private static boolean canPlacePuddle(WorldGenLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        BlockPos belowPos = pos.below();
        return (state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.DIRT) || state.is(Blocks.PODZOL) || state.canBeReplaced())
                && level.getBlockState(belowPos).isFaceSturdy(level, belowPos, Direction.UP);
    }
}
