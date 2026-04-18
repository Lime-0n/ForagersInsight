package com.tiomadre.foragersinsight.common.worldgen.trees.decorator;

import com.mojang.serialization.Codec;
import com.tiomadre.foragersinsight.core.registry.FITreeDecoratorTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;

public class WoodlandsDarkOakStemDecorator extends TreeDecorator {
    public static final Codec<WoodlandsDarkOakStemDecorator> CODEC = Codec.unit(WoodlandsDarkOakStemDecorator::new);

    @Override
    protected @NotNull TreeDecoratorType<?> type() {
        return FITreeDecoratorTypes.WOODLANDS_DARK_OAK_STEM_DECORATOR.get();
    }

    @Override
    public void place(Context context) {
        List<BlockPos> logs = context.logs();
        if (logs.isEmpty()) return;

        BlockPos topLog = logs.stream().max(Comparator.comparingInt(BlockPos::getY)).orElse(null);
        if (topLog == null) return;

        RandomSource random = context.random();
        Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        BlockPos sidePos = topLog.relative(direction);

        if (!context.isAir(sidePos)) return;

        context.setBlock(sidePos, net.minecraft.world.level.block.Blocks.DARK_OAK_LOG.defaultBlockState()
                .setValue(RotatedPillarBlock.AXIS, direction.getAxis()));
    }
}