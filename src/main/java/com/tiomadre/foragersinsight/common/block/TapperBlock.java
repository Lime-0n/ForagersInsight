package com.tiomadre.foragersinsight.common.block;

import com.tiomadre.foragersinsight.core.registry.FIBlocks;
import com.tiomadre.foragersinsight.core.registry.FIItems;
import com.tiomadre.foragersinsight.core.registry.FIParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class TapperBlock extends HorizontalDirectionalBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty HAS_TAPPER = BooleanProperty.create("has_tapper");
    public static final IntegerProperty FILL = IntegerProperty.create("fill", 0, 4);
    // hit box
    private static final VoxelShape NORTH_SHAPE = Stream.of(
            Block.box(7.5, 12, 0, 8.5, 15, 6),
            Block.box(7.5, 13, 6, 8.5, 15, 11),
            Block.box(3.5, 2, 1, 12.5, 11, 10),
            Block.box(11.5, 11, 5, 11.5, 15, 6),
            Block.box(4.5, 11, 5, 4.5, 15, 6),
            Block.box(4.5, 15, 5, 11.5, 15, 6)
    ).reduce(Shapes.empty(), Shapes::or);
     private static final VoxelShape EAST_SHAPE = rotateShape(Direction.EAST);
    private static final VoxelShape SOUTH_SHAPE = rotateShape(Direction.SOUTH);
    private static final VoxelShape WEST_SHAPE = rotateShape(Direction.WEST);

    public TapperBlock(Properties props) {
        super(props);
        registerDefaultState(getStateDefinition().any()
                .setValue(FACING, Direction.NORTH)
                .setValue(HAS_TAPPER, false)
                .setValue(FILL, 0));
    }
    @Override
    public @NotNull ItemStack getCloneItemStack(@NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull BlockState state) {
        return new ItemStack(FIItems.TAPPER.get());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, FILL, HAS_TAPPER);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter world,
                                        @NotNull BlockPos pos, @NotNull CollisionContext ctx) {
        return switch (state.getValue(FACING)) {
            case EAST -> WEST_SHAPE;
            case SOUTH -> NORTH_SHAPE;
            case WEST -> EAST_SHAPE;
            default -> SOUTH_SHAPE;
        };
    }
    private static VoxelShape rotateShape(Direction to) {
        if (Direction.NORTH == to) return TapperBlock.NORTH_SHAPE;
        VoxelShape[] buffer = new VoxelShape[]{TapperBlock.NORTH_SHAPE, Shapes.empty()};
        int rotations = (to.get2DDataValue() - Direction.NORTH.get2DDataValue() + 4) % 4;
        for (int i = 0; i < rotations; i++) {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) ->
                    buffer[1] = Shapes.or(buffer[1], Shapes.box(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX)));
            buffer[0] = buffer[1];
            buffer[1] = Shapes.empty();
        }
        return buffer[0];
    }
    @Override
    public @Nullable BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        Direction face = context.getClickedFace();
        if (face.getAxis().isVertical()) return null;
        Level level = context.getLevel();
        BlockPos logPos = context.getClickedPos().relative(face.getOpposite());
        BlockState logState = level.getBlockState(logPos);

        // place on a Sappy Birch Log to begin harvest
        if (!logState.is(FIBlocks.SAPPY_BIRCH_LOG.get()) ||
                logState.getValue(RotatedPillarBlock.AXIS) != Direction.Axis.Y) {
            return null;
        }
        return defaultBlockState()
                .setValue(FACING, face)
                .setValue(HAS_TAPPER, true)
                .setValue(FILL, 0);
    }

    @Override
    public boolean canSurvive(@NotNull BlockState state, @NotNull LevelReader world, @NotNull BlockPos pos) {
        Direction attachDir = state.getValue(FACING).getOpposite();
        BlockState logState = world.getBlockState(pos.relative(attachDir));
        return logState.is(FIBlocks.SAPPY_BIRCH_LOG.get()) &&
                logState.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y;
    }
    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState state, @NotNull Direction direction,
                                           @NotNull BlockState neighborState, @NotNull LevelAccessor level,
                                           @NotNull BlockPos currentPos, @NotNull BlockPos neighborPos) {
        Direction attachDir = state.getValue(FACING).getOpposite();
        if (direction == attachDir && !canSurvive(state, level, currentPos)) {
            if (state.getValue(HAS_TAPPER) && level instanceof Level lvl && !lvl.isClientSide) {
                popResource(lvl, currentPos, new ItemStack(FIItems.TAPPER.get()));
            }
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
    }

    @Override
    public boolean isRandomlyTicking(@NotNull BlockState state) {
        return state.getValue(HAS_TAPPER) && state.getValue(FILL) < 4;
    }

    @Override
    public void randomTick(@NotNull BlockState state, @NotNull ServerLevel level,
                           @NotNull BlockPos pos, @NotNull RandomSource random) {
        level.setBlock(pos, state.setValue(FILL, state.getValue(FILL) + 1), Block.UPDATE_CLIENTS);
    }
    // drips
    @Override
    public void animateTick(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        if (!state.getValue(HAS_TAPPER)) return;
        if (random.nextInt(10) == 0) {
            Direction facing = state.getValue(FACING);
            double x = pos.getX() + 0.5D;
            double y = pos.getY() + 0.8D;
            double z = pos.getZ() + 0.5D;
            double inwardOffset = 0.25D;
            x -= facing.getStepX() * inwardOffset;
            z -= facing.getStepZ() * inwardOffset;

            level.addParticle(FIParticleTypes.DRIPPING_SAP.get(), x, y, z, 0.0D, -0.005D, 0.0D);
            level.playLocalSound(x, y, z, SoundEvents.BEEHIVE_DRIP, SoundSource.BLOCKS, 0.6F, 0.0001F, false);
        }
    }
    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        //collect with empty bucket
        ItemStack held = player.getItemInHand(hand);
        if (state.getValue(HAS_TAPPER) && state.getValue(FILL) == 4 && held.is(Items.BUCKET)) {
            if (!level.isClientSide) {
                ItemStack sap = new ItemStack(FIItems.BIRCH_SAP_BUCKET.get());
                if (!player.addItem(sap)) player.drop(sap, false);
                level.setBlock(pos, state.setValue(FILL, 0).setValue(HAS_TAPPER, true), Block.UPDATE_ALL);
                level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 0.9F, 0.9F);
                level.playSound(null, pos, SoundEvents.BEEHIVE_DRIP, SoundSource.BLOCKS, 1.1F, 0.0001F);
                if (!player.getAbilities().instabuild) held.shrink(1);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }

    @Override
    public void onRemove(@NotNull BlockState oldState, @NotNull Level level, @NotNull BlockPos pos,
                         @NotNull BlockState newState, boolean isMoving) {
        super.onRemove(oldState, level, pos, newState, isMoving);
    }
}