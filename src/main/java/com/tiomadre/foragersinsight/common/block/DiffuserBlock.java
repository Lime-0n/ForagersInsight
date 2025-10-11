package com.tiomadre.foragersinsight.common.block;

import com.tiomadre.foragersinsight.common.block.entity.DiffuserBlockEntity;
import com.tiomadre.foragersinsight.common.diffuser.DiffuserScent;
import com.tiomadre.foragersinsight.core.registry.FIBlockEntityTypes;
import com.tiomadre.foragersinsight.core.registry.FIParticleTypes;
import net.minecraft.core.BlockPos;

import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.tiomadre.foragersinsight.common.utility.TextUtils;
import net.minecraftforge.network.NetworkHooks;

public class DiffuserBlock extends BaseEntityBlock {
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    private static final VoxelShape SHAPE = Shapes.or(
            Block.box(4.0D, 0.0D, 4.0D, 12.0D, 12.0D, 12.0D),
            Block.box(9.0D, 12.0D, 6.0D, 10.0D, 14.0D, 10.0D),
            Block.box(6.0D, 12.0D, 6.0D, 7.0D, 14.0D, 10.0D),
            Block.box(7.0D, 12.0D, 6.0D, 9.0D, 14.0D, 7.0D),
            Block.box(7.0D, 12.0D, 9.0D, 9.0D, 14.0D, 10.0D)
    );


    public DiffuserBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LIT, false));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new DiffuserBlockEntity(pos, state);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        boolean flintAndSteel = held.is(Items.FLINT_AND_STEEL);
        BlockEntity entity = level.getBlockEntity(pos);
        if (!(entity instanceof DiffuserBlockEntity diffuser)) {
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (player.isShiftKeyDown()) {
            if (!level.isClientSide) {
          if (diffuser.isLit()) {
                    diffuser.getActiveScent().ifPresentOrElse(
                            scent -> player.displayClientMessage(
                                    TextUtils.getTranslation("diffuser.scent", scent.displayName(), scent.description()), true),
                            () -> player.displayClientMessage(TextUtils.getTranslation("diffuser.no_scent"), true)
                    );
                } else {
                    player.displayClientMessage(TextUtils.getTranslation("diffuser.no_scent"), true);
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (!level.isClientSide) {
            if (flintAndSteel && diffuser.tryStartDiffusion()) {
                held.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
                level.playSound(null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F,
                        level.random.nextFloat() * 0.4F + 0.8F);
                return InteractionResult.CONSUME;
            }

            if (player instanceof ServerPlayer serverPlayer) {
                NetworkHooks.openScreen(serverPlayer, diffuser, pos);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos,
                                        @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    public boolean canSurvive(@NotNull BlockState state, @NotNull LevelReader level, @NotNull BlockPos pos) {
        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(belowPos);

        return belowState.isFaceSturdy(level, belowPos, Direction.UP)
                || Block.isFaceFull(belowState.getCollisionShape(level, belowPos), Direction.UP);
    }

    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState state, @NotNull Direction direction,
                                           @NotNull BlockState neighborState, @NotNull LevelAccessor level,
                                           @NotNull BlockPos currentPos, @NotNull BlockPos neighborPos) {
        if (direction == Direction.DOWN && !state.canSurvive(level, currentPos)) {
            return Blocks.AIR.defaultBlockState();
        }

        return super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                                 @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    public @NotNull VoxelShape getBlockSupportShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                                    @NotNull BlockPos pos) {
        return Shapes.empty();
    }


    @Override
    public @NotNull VoxelShape getOcclusionShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                                 @NotNull BlockPos pos) {
        return Shapes.empty();
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                         @NotNull BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof DiffuserBlockEntity diffuser) {
                Containers.dropContents(level, pos, diffuser);
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }


    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, FIBlockEntityTypes.DIFFUSER.get(), DiffuserBlockEntity::serverTick);
    }

    @Override
    public void animateTick(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        if (!state.getValue(LIT)) {
            return;
        }

        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 1.0D;
        double z = pos.getZ() + 0.5D;
        double offsetX = (random.nextDouble() - 0.5D) * 0.1D;
        double offsetZ = (random.nextDouble() - 0.5D) * 0.1D;

        if (random.nextInt(4) == 0) {
            level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, x, y, z, offsetX * 0.6D, 0.07D, offsetZ * 0.6D);
        }
        if (random.nextInt(5) == 0) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof DiffuserBlockEntity diffuser) {
                diffuser.getActiveScent().ifPresent(scent -> {
                    double scentX = x + (random.nextDouble() - 0.5D) * 0.3D;
                    double scentY = y + 0.2D + random.nextDouble() * 0.2D;
                    double scentZ = z + (random.nextDouble() - 0.5D) * 0.3D;
                    spawnScentParticle(level, random, scentX, scentY, scentZ, scent);
                });
            }
        }

    }
    private void spawnScentParticle(Level level, RandomSource random, double x, double y, double z, DiffuserScent scent) {
        SimpleParticleType particle = getScentParticleType(scent);
        if (particle == null) {
            return;
        }
        double driftX = (random.nextDouble() - 0.5D) * 0.08D;
        double driftY = 0.03D + random.nextDouble() * 0.02D;
        double driftZ = (random.nextDouble() - 0.5D) * 0.08D;
        level.addParticle(particle, x, y, z, driftX, driftY, driftZ);
    }

    private @Nullable SimpleParticleType getScentParticleType(DiffuserScent scent) {
        if (scent == DiffuserScent.ROSEY.get()) {
            return FIParticleTypes.ROSE_SCENT.get();
        }
        if (scent == DiffuserScent.CONIFEROUS.get()) {
            return FIParticleTypes.CONIFEROUS_SCENT.get();
        }
        if (scent == DiffuserScent.FLORAL.get()) {
            return FIParticleTypes.FLORAL_SCENT.get();
        }
        return null;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }
}