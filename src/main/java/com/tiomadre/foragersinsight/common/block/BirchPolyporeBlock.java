package com.tiomadre.foragersinsight.common.block;

import com.tiomadre.foragersinsight.core.registry.FIBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BirchPolyporeBlock extends HorizontalDirectionalBlock implements BonemealableBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 4);
    public static final int MAX_AGE = 4;

    private static final VoxelShape[] BASE_SHAPES = new VoxelShape[]{
            Shapes.box(0.3125D, 0.3125D, 0.75D, 0.6875D, 0.75D, 1.0D),
            Shapes.box(0.25D, 0.25D, 0.625D, 0.75D, 0.8125D, 1.0D),
            Shapes.box(0.1875D, 0.25D, 0.625D, 0.8125D, 0.8125D, 1.0D),
            Shapes.box(0.125D, 0.25D, 0.5625D, 0.875D, 0.8125D, 1.0D),
            Shapes.box(0.0625D, 0.25D, 0.5D, 0.9375D, 0.8125D, 1.0D)
    };

    public BirchPolyporeBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(AGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, AGE);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getClickedFace();
        if (!direction.getAxis().isHorizontal()) {
            return null;
        }

        BlockState state = this.defaultBlockState().setValue(FACING, direction);
        return state.canSurvive(context.getLevel(), context.getClickedPos()) ? state : null;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        BlockPos supportPos = pos.relative(facing.getOpposite());
        return level.getBlockState(supportPos).is(net.minecraft.tags.BlockTags.LOGS);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbourState, LevelAccessor level, BlockPos pos, BlockPos neighbourPos) {
        return direction == state.getValue(FACING).getOpposite() && !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : state;
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return state.getValue(AGE) < MAX_AGE;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.canSurvive(level, pos)) {
            level.destroyBlock(pos, true);
            return;
        }

        if (state.getValue(AGE) < MAX_AGE && random.nextFloat() < 0.2F) {
            level.setBlock(pos, state.setValue(AGE, state.getValue(AGE) + 1), Block.UPDATE_ALL);
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (state.getValue(AGE) == MAX_AGE) {
            if (!level.isClientSide) {
                popResource(level, pos, new ItemStack(FIBlocks.BIRCH_POLYPORE.get(), 2));
                level.setBlock(pos, state.setValue(AGE, 0), Block.UPDATE_ALL);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return super.use(state, level, pos, player, hand, hit);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        VoxelShape base = BASE_SHAPES[state.getValue(AGE)];

        return switch (facing) {
            case SOUTH -> rotateShape(base, Rotation.CLOCKWISE_180);
            case WEST -> rotateShape(base, Rotation.COUNTERCLOCKWISE_90);
            case EAST -> rotateShape(base, Rotation.CLOCKWISE_90);
            default -> base;
        };
    }

    public boolean isValidBonemealTarget(BlockGetter level, BlockPos pos, BlockState state, boolean isClient) {
        return state.getValue(AGE) < MAX_AGE && level instanceof LevelReader levelReader && state.canSurvive(levelReader, pos);
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        int age = Math.min(MAX_AGE, state.getValue(AGE) + 1);
        level.setBlock(pos, state.setValue(AGE, age), Block.UPDATE_ALL);
    }

    private VoxelShape rotateShape(VoxelShape shape, Rotation rotation) {
        VoxelShape[] buffer = new VoxelShape[]{shape, Shapes.empty()};
        for (int i = 0; i < rotation.getRotations(); ++i) {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = Shapes.or(buffer[1], Shapes.box(1.0D - maxZ, minY, minX, 1.0D - minZ, maxY, maxX)));
            buffer[0] = buffer[1];
            buffer[1] = Shapes.empty();
        }
        return buffer[0];
    }

    private enum Rotation {
        NONE(0),
        CLOCKWISE_90(1),
        CLOCKWISE_180(2),
        COUNTERCLOCKWISE_90(3);

        private final int rotations;

        Rotation(int rotations) {
            this.rotations = rotations;
        }

        public int getRotations() {
            return rotations;
        }
    }
}