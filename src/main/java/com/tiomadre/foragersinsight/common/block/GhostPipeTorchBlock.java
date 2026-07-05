package com.tiomadre.foragersinsight.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
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

    protected static final VoxelShape STANDING_SHAPE = Block.box(
            7.0D, 0.0D, 7.0D,
            9.0D, 10.0D, 9.0D
    );

    private static final double WALL_MIN_Y = 3.6163D;
    private static final double WALL_MAX_Y = 13.6204D;
    private static final double WALL_INNER = 11.7577D;
    private static final double WALL_OUTER = 17.4323D;

    protected static final VoxelShape SOUTH_SHAPE = Block.box(
            7.0D, WALL_MIN_Y, WALL_INNER,
            9.0D, WALL_MAX_Y, WALL_OUTER
    );

    protected static final VoxelShape NORTH_SHAPE = Block.box(
            7.0D, WALL_MIN_Y, 16.0D - WALL_OUTER,
            9.0D, WALL_MAX_Y, 16.0D - WALL_INNER
    );

    protected static final VoxelShape EAST_SHAPE = Block.box(
            WALL_INNER, WALL_MIN_Y, 7.0D,
            WALL_OUTER, WALL_MAX_Y, 9.0D
    );

    protected static final VoxelShape WEST_SHAPE = Block.box(
            16.0D - WALL_OUTER, WALL_MIN_Y, 7.0D,
            16.0D - WALL_INNER, WALL_MAX_Y, 9.0D
    );

    private final Supplier<SimpleParticleType> flameParticle;

    public GhostPipeTorchBlock(Properties properties, Supplier<SimpleParticleType> flameParticle) {
        super(properties);
        this.flameParticle = flameParticle;
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case NORTH -> SOUTH_SHAPE;
            case SOUTH -> NORTH_SHAPE;
            case WEST -> EAST_SHAPE;
            case EAST -> WEST_SHAPE;
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
        BlockState supportState = level.getBlockState(supportPos);

        return supportState.isFaceSturdy(level, supportPos, supportDirection.getOpposite());
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        Direction facing = state.getValue(FACING);
        Direction supportDirection = facing == Direction.UP ? Direction.DOWN : facing.getOpposite();

        return direction == supportDirection && !state.canSurvive(level, pos)
                ? Blocks.AIR.defaultBlockState()
                : super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        Direction facing = state.getValue(FACING);

        double x = (double) pos.getX() + 0.5D;
        double y = (double) pos.getY() + 0.7D;
        double z = (double) pos.getZ() + 0.5D;

        if (facing != Direction.UP) {
            Direction opposite = facing.getOpposite();

            x += 0.27D * (double) facing.getStepX();
            y += 0.22D;
            z += 0.27D * (double) facing.getStepZ();
        }

        level.addParticle(this.flameParticle.get(), x, y, z, 0.0D, 0.0D, 0.0D);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}