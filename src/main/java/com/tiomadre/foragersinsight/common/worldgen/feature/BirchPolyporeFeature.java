package com.tiomadre.foragersinsight.common.worldgen.feature;

import com.tiomadre.foragersinsight.common.block.BirchPolyporeBlock;
import com.tiomadre.foragersinsight.core.registry.FIBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.tags.BlockTags;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BirchPolyporeFeature extends Feature<NoneFeatureConfiguration> {
    public BirchPolyporeFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();

        List<BlockPos> birchLogs = new ArrayList<>();
        MutableBlockPos cursor = new MutableBlockPos();
        for (BlockPos pos : BlockPos.betweenClosed(origin.offset(-4, -1, -4), origin.offset(4, 6, 4))) {
            cursor.set(pos);
            BlockState state = level.getBlockState(cursor);
            if (state.is(BlockTags.BIRCH_LOGS)) {
                birchLogs.add(cursor.immutable());
            }
        }

        if (birchLogs.isEmpty()) {
            return false;
        }

        Collections.shuffle(birchLogs, new java.util.Random(random.nextLong()));
        for (BlockPos logPos : birchLogs) {
            Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            BlockPos target = logPos.relative(direction);
            BlockState polypore = FIBlocks.BIRCH_POLYPORE.get().defaultBlockState().setValue(BirchPolyporeBlock.FACING, direction);
            if (level.isEmptyBlock(target) && polypore.canSurvive(level, target)) {
                level.setBlock(target, polypore, Block.UPDATE_ALL);
                return true;
            }
        }

        return false;
    }
}