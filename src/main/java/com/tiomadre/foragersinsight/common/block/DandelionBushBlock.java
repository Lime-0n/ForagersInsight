package com.tiomadre.foragersinsight.common.block;

import com.tiomadre.foragersinsight.core.registry.FIBlocks;
import com.tiomadre.foragersinsight.core.registry.FIItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jetbrains.annotations.NotNull;

public class DandelionBushBlock extends CropBlock implements BonemealableBlock {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_4;
    public static final int MAX_AGE = 4;

    public DandelionBushBlock(Properties props) {
        super(props);
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
    }

    @Override
    public @NotNull BlockState getPlant(@NotNull BlockGetter level, @NotNull BlockPos pos) {
        return FIBlocks.DANDELION_BUSH.get().defaultBlockState();
    }

    @Override
    protected @NotNull ItemLike getBaseSeedId() {
        return FIItems.DANDELION_ROOT.get();
    }

    @Override
    protected @NotNull IntegerProperty getAgeProperty() {
        return AGE;
    }

    @Override
    public int getMaxAge() {
        return MAX_AGE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }
    @Override
    protected int getBonemealAgeIncrease(@NotNull Level level) {
        return 1;
    }
}