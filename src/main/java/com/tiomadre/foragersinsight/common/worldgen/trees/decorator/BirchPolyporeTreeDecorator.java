package com.tiomadre.foragersinsight.common.worldgen.trees.decorator;

import com.tiomadre.foragersinsight.common.block.BirchPolyporeBlock;
import com.tiomadre.foragersinsight.core.registry.FIBlocks;
import com.tiomadre.foragersinsight.core.registry.FITreeDecoratorTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BirchPolyporeTreeDecorator extends TreeDecorator {
    public static final Codec<BirchPolyporeTreeDecorator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("probability").forGetter(decorator -> decorator.probability)
    ).apply(instance, BirchPolyporeTreeDecorator::new));

    private final float probability;

    public BirchPolyporeTreeDecorator(float probability) {
        this.probability = probability;
    }

    @Override
    protected @NotNull TreeDecoratorType<?> type() {
        return FITreeDecoratorTypes.BIRCH_POLYPORE_DECORATOR.get();
    }

    @Override
    public void place(Context context) {
        RandomSource random = context.random();
        if (random.nextFloat() >= this.probability) {
            return;
        }

        List<BlockPos> logs = context.logs();
        if (logs.isEmpty()) {
            return;
        }

        logs.sort(Comparator.comparingInt(BlockPos::getY));
        int sample = Math.min(logs.size(), 3);
        BlockPos chosen = logs.get(random.nextInt(sample));

        List<Direction> directions = List.of(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST);
        Collections.shuffle(directions, new java.util.Random(random.nextLong()));
        for (Direction direction : directions) {
            BlockPos target = chosen.relative(direction);
            BlockState polypore = FIBlocks.BIRCH_POLYPORE.get().defaultBlockState().setValue(BirchPolyporeBlock.FACING, direction);
            if (context.isAir(target) && polypore.canSurvive((LevelReader) context.level(), target)) {
                context.setBlock(target, polypore);
                break;
            }
        }
    }
}