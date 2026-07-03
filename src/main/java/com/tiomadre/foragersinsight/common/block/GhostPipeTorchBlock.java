package com.tiomadre.foragersinsight.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.Supplier;

public class GhostPipeTorchBlock extends Block {
    public static final DirectionProperty FACING = DirectionProperty.create("facing", direction -> direction != Direction.DOWN);
    protected static final VoxelShape STANDING_SHAPE = Block.box(6.0D, 0.0D, 6.0D, 10.0D, 16.0D, 10.0D);
    protected static final VoxelShape NORTH_SHAPE = Block.box(6.0D, 3.0D, 0.0D, 10.0D, 16.0D, 10.0D);
    protected static final VoxelShape SOUTH_SHAPE = Block.box(6.0D, 3.0D, 6.0D, 10.0D, 16.0D, 16.0D);
    protected static final VoxelShape WEST_SHAPE = Block.box(0.0D, 3.0D, 6.0D, 10.0D, 16.0D, 10.0D);
    protected static final VoxelShape EAST_SHAPE = Block.box(6.0D, 3.0D, 6.0D, 16.0D, 16.0D, 10.0D);
    private final Supplier<SimpleParticleType> flameParticle;

    public GhostPipeTorchBlock(Properties properties, Supplier<SimpleParticleType> flameParticle) {
        super(properties);
        this.flameParticle = flameParticle;
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case NORTH -> NORTH_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case WEST -> WEST_SHAPE;
            case EAST -> EAST_SHAPE;
            default -> STANDING_SHAPE;
        };
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction clickedFace = context.getClickedFace();
        if (clickedFace != Direction.DOWN) {
            BlockState state = this.defaultBlockState().setValue(FACING, clickedFace);
            if (state.canSurvive(context.getLevel(), context.getClickedPos())) {
                return state;
            }
        }

        return null;
    }

    public boolean canSurvive(BlockState state, BlockGetter level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        Direction supportDirection = facing == Direction.UP ? Direction.DOWN : facing.getOpposite();
        BlockPos supportPos = pos.relative(supportDirection);
        return Block.canSupportCenter((LevelReader) level, supportPos, supportDirection.getOpposite());
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        Direction facing = state.getValue(FACING);
        Direction supportDirection = facing == Direction.UP ? Direction.DOWN : facing.getOpposite();
        return direction == supportDirection && !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        Direction facing = state.getValue(FACING);
        double x = (double) pos.getX() + 0.5D;
        double y = (double) pos.getY() + 0.7D;
        double z = (double) pos.getZ() + 0.5D;

        if (facing != Direction.UP) {
            Direction opposite = facing.getOpposite();
            x += 0.27D * (double) opposite.getStepX();
            y += 0.22D;
            z += 0.27D * (double) opposite.getStepZ();
        }

        level.addParticle(this.flameParticle.get(), x, y, z, 0.0D, 0.0D, 0.0D);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}