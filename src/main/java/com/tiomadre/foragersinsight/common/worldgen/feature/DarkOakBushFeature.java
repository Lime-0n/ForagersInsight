package com.tiomadre.foragersinsight.common.worldgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class DarkOakBushFeature extends Feature<NoneFeatureConfiguration> {
    public DarkOakBushFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();

        if (!level.getBlockState(origin.below()).is(BlockTags.DIRT)) {
            return false;
        }

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                BlockPos pos = origin.offset(x, 0, z);
                if (!canReplace(level, pos)) {
                    return false;
                }
            }
        }

        BlockPos[] topLayer = new BlockPos[] {
                origin.above(),
                origin.above().north(),
                origin.above().south(),
                origin.above().east(),
                origin.above().west()
        };

        for (BlockPos pos : topLayer) {
            if (!canReplace(level, pos)) {
                return false;
            }
        }

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                BlockPos pos = origin.offset(x, 0, z);
                BlockState state = (x == 0 && z == 0) ? Blocks.DARK_OAK_LOG.defaultBlockState() : Blocks.DARK_OAK_LEAVES.defaultBlockState();
                setBlock(level, pos, state);
            }
        }

        for (BlockPos pos : topLayer) {
            setBlock(level, pos, Blocks.DARK_OAK_LEAVES.defaultBlockState());
        }

        return true;
    }

    private static boolean canReplace(WorldGenLevel level, BlockPos pos) {
        return level.getBlockState(pos).canBeReplaced();
    }
}