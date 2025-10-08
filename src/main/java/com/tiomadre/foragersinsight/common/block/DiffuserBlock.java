package com.tiomadre.foragersinsight.common.block;

import com.tiomadre.foragersinsight.common.block.entity.DiffuserBlockEntity;
import com.tiomadre.foragersinsight.common.diffuser.DiffuserScent;
import com.tiomadre.foragersinsight.core.registry.FIBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BaseEntityBlock;
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
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import vectorwing.farmersdelight.common.utility.TextUtils;

public class DiffuserBlock extends BaseEntityBlock {
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

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
                diffuser.getActiveScent().ifPresentOrElse(
                        scent -> player.displayClientMessage(
                                TextUtils.getTranslation("diffuser.scent_info", scent.displayName(), scent.description()), true),
                        () -> player.displayClientMessage(TextUtils.getTranslation("diffuser.no_scent"), true)
                );
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
            player.openMenu(diffuser);
            }
        return InteractionResult.sidedSuccess(level.isClientSide);
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
        } else {
            level.addParticle(ParticleTypes.SMOKE, x, y + 0.05D, z, offsetX, 0.02D, offsetZ);
        }

        Vec3 scentColor = getScentColor(level, pos, state);
        Vector3f tint = new Vector3f((float) scentColor.x(), (float) scentColor.y(), (float) scentColor.z());
        ParticleOptions scentedParticle = new DustColorTransitionOptions(tint, tint, 0.75F);
        level.addParticle(scentedParticle, x, y + 0.15D, z, offsetX * 1.5D, 0.01D, offsetZ * 1.5D);
    }

    protected @NotNull Vec3 getScentColor(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state) {
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof DiffuserBlockEntity diffuser) {
            net.minecraft.world.phys.Vec3 color = diffuser.getScentColor();
            return new Vec3(color.x(), color.y(), color.z());
        }
        return DiffuserScent.DEFAULT_COLOR;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }
}