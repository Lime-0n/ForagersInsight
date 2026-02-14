package com.tiomadre.foragersinsight.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.MushroomBlock;
import net.minecraft.world.level.block.state.BlockState;
import vectorwing.farmersdelight.common.block.MushroomColonyBlock;

import java.util.function.Supplier;

public class WildMushroomBlock extends MushroomBlock {
    private final Supplier<Block> colonyBlock;

    public WildMushroomBlock(Properties props) {
        super(props, null);
        this.colonyBlock = () -> null;
    }

    public WildMushroomBlock(Properties props, Supplier<Block> colonyBlock) {
        super(props, null);
        this.colonyBlock = colonyBlock;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        Block colony = this.colonyBlock.get();
        if (colony == null) {
            return;
        }

        BlockState colonyState = colony.defaultBlockState();
        if (colonyState.hasProperty(MushroomColonyBlock.COLONY_AGE)) {
            int maxAge = MushroomColonyBlock.COLONY_AGE.getPossibleValues().size() - 1;
            colonyState = colonyState.setValue(MushroomColonyBlock.COLONY_AGE, maxAge);
        }

        if (colonyState.canSurvive(level, pos)) {
            level.setBlock(pos, colonyState, Block.UPDATE_ALL);
        }
    }
}