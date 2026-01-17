package com.tiomadre.foragersinsight.common.block;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;

public class ThinLogBlock extends RotatedPillarBlock {
    private static final VoxelShape SHAPE_Y = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);
    private static final VoxelShape SHAPE_X = Block.box(0.0D, 0.0D, 4.0D, 16.0D, 8.0D, 12.0D);
    private static final VoxelShape SHAPE_Z = Block.box(4.0D, 0.0D, 0.0D, 12.0D, 8.0D, 16.0D);
    private final Supplier<? extends Block> strippedBlock;

    public ThinLogBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.strippedBlock = null;
    }

    public ThinLogBlock(Supplier<? extends Block> strippedBlock, BlockBehaviour.Properties properties) {
        super(properties);
        this.strippedBlock = strippedBlock;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, net.minecraft.core.BlockPos pos, CollisionContext context) {
        return getShapeForAxis(state.getValue(AXIS));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, net.minecraft.core.BlockPos pos, CollisionContext context) {
        return getShapeForAxis(state.getValue(AXIS));
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, net.minecraft.core.BlockPos pos) {
        return getShapeForAxis(state.getValue(AXIS));
    }

    @Override
    public BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction toolAction, boolean simulate) {
        if (toolAction == ToolActions.AXE_STRIP && strippedBlock != null) {
            return strippedBlock.get().defaultBlockState().setValue(AXIS, state.getValue(AXIS));
        }

        return super.getToolModifiedState(state, context, toolAction, simulate);
    }
    @Override
    public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return true;
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 5;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 5;
    }

    private static VoxelShape getShapeForAxis(Direction.Axis axis) {
        return switch (axis) {
            case X -> SHAPE_X;
            case Z -> SHAPE_Z;
            default -> SHAPE_Y;
        };
    }
}
