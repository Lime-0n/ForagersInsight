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
        BlockPos origin = context.origin();

        int radiusX = 2 + random.nextInt(3);
        int radiusZ = 2 + random.nextInt(3);
        float edgeNoise = 0.70F + (random.nextFloat() * 0.40F);

        List<BlockPos> puddleBlocks = new ArrayList<>();
        List<BlockPos> perimeter = new ArrayList<>();

        for (int x = -radiusX - 1; x <= radiusX + 1; x++) {
            for (int z = -radiusZ - 1; z <= radiusZ + 1; z++) {
                float normX = (float) x / radiusX;
                float normZ = (float) z / radiusZ;
                float dist = (normX * normX) + (normZ * normZ);

                float noise = (Math.abs((x * 31 + z * 17) % 11) / 20.0F) + (random.nextFloat() * 0.18F);
                boolean inside = dist <= (edgeNoise + noise);

                BlockPos currentPos = origin.offset(x, 0, z);
                if (inside && canPlacePuddle(level, currentPos)) {
                    puddleBlocks.add(currentPos);
                }
            }
        }

        if (puddleBlocks.size() < 6) {
            return false;
        }

        BlockState puddleState = FIBlocks.CONDENSED_DIRT.get().defaultBlockState().setValue(ShallowBlock.WATERLOGGED, true);
        for (BlockPos puddlePos : puddleBlocks) {
            level.setBlock(puddlePos, puddleState, 2);

            for (Direction direction : Direction.Plane.HORIZONTAL) {
                BlockPos sidePos = puddlePos.relative(direction);
                if (!puddleBlocks.contains(sidePos) && canPlacePerimeterFlora(level, sidePos)) {
                    perimeter.add(sidePos);
                }
            }
        }

        for (BlockPos edgePos : perimeter) {
            if (random.nextFloat() < 0.50F) {
                BlockState floraState = random.nextFloat() < 0.65F
                        ? Blocks.GRASS.defaultBlockState()
                        : FIBlocks.WOODLAND_FERN.get().defaultBlockState();

                if (floraState.canSurvive(level, edgePos)) {
                    level.setBlock(edgePos, floraState, 2);
                }
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

    private static boolean canPlacePerimeterFlora(WorldGenLevel level, BlockPos pos) {
        return level.getBlockState(pos).canBeReplaced() && !level.getFluidState(pos).isSource();
    }
}