package com.tiomadre.foragersinsight.common.block;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;

public class ThinLogBlock extends RotatedPillarBlock {
    private static final VoxelShape SHAPE_Y = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);
    private static final VoxelShape SHAPE_X = Block.box(0.0D, 0.0D, 4.0D, 16.0D, 8.0D, 12.0D);
    private static final VoxelShape SHAPE_Z = Block.box(4.0D, 0.0D, 0.0D, 12.0D, 8.0D, 16.0D);

    public ThinLogBlock(BlockBehaviour.Properties properties) {
        super(properties);
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

    private static VoxelShape getShapeForAxis(Direction.Axis axis) {
        return switch (axis) {
            case X -> SHAPE_X;
            case Z -> SHAPE_Z;
            default -> SHAPE_Y;
        };
    }
}