package com.doltandtio.foragersinsight.common.block;

import com.doltandtio.foragersinsight.common.block.entity.PotpourriBlockEntity;
import com.doltandtio.foragersinsight.core.registry.FIBlockEntityTypes;
import com.doltandtio.foragersinsight.data.server.tags.FITags;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.common.utility.TextUtils;

public class PotpourriBlock extends BaseEntityBlock {
    public static final EnumProperty<PotpourriContents> CONTENTS = EnumProperty.create("contents", PotpourriContents.class);
    private static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 7.0D, 14.0D);

    public PotpourriBlock() {
        super(Properties.copy(Blocks.DECORATED_POT).noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(CONTENTS, PotpourriContents.EMPTY));
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                        @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    public boolean useShapeForLightOcclusion(@NotNull BlockState state) {
        return true;
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof PotpourriBlockEntity potpourri)) {
            return InteractionResult.PASS;
        }

        if (potpourri.isBlendActive()) {
            if (!level.isClientSide) {
                potpourri.getActiveBlendName().ifPresent(name ->
                        player.displayClientMessage(
                                TextUtils.getTranslation("interaction.potpourri.active", name),
                                true));
            }

        return InteractionResult.sidedSuccess(level.isClientSide);
        }

        ItemStack held = player.getItemInHand(hand);

        if (!held.isEmpty() && held.is(FITags.ItemTag.AROMATICS)) {
            if (level.isClientSide) {
                return potpourri.isFull() ? InteractionResult.PASS : InteractionResult.SUCCESS;
            }
            ItemStack toInsert = held.copy();
            toInsert.setCount(1);
            if (potpourri.addItem(toInsert)) {
                if (!player.getAbilities().instabuild) {
                    held.shrink(1);
                }
                level.playSound(null, pos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 0.6F, 1.0F);
                potpourri.updateAppearance(state);
                return InteractionResult.CONSUME;
            }
            return InteractionResult.PASS;
        }

        // Remove last item with empty hand
        if (held.isEmpty()) {
            if (level.isClientSide) {
                return potpourri.isEmpty() ? InteractionResult.PASS : InteractionResult.SUCCESS;
            }
            ItemStack removed = potpourri.removeLastItem();
            if (!removed.isEmpty()) {
                if (!player.addItem(removed)) {
                    player.drop(removed, false);
                }
                potpourri.updateAppearance(state);
                return InteractionResult.CONSUME;
            }
            return InteractionResult.PASS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                         @NotNull BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof PotpourriBlockEntity potpourri) {
                Containers.dropContents(level, pos, potpourri.getItemsForDrop());
                level.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, level, pos, newState, isMoving);
        } else {
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new PotpourriBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CONTENTS);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(@NotNull Level level,
                                                                            @NotNull BlockState state,
                                                                            @NotNull BlockEntityType<T> type) {
        if (level.isClientSide) {
            return createTickerHelper(type, FIBlockEntityTypes.POTPOURRI.get(), PotpourriBlockEntity::clientTick);
        }
        return createTickerHelper(type, FIBlockEntityTypes.POTPOURRI.get(), PotpourriBlockEntity::serverTick);
    }

    public enum PotpourriContents implements StringRepresentable {
        EMPTY("empty"),
        ROSEY("rosey"),
        CONIFEROUS("coniferous");

        private final String name;

        PotpourriContents(String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return name;
        }

        public static PotpourriContents fromBlend(@Nullable ResourceLocation id) {
            if (id == null) return EMPTY;
            return switch (id.getPath()) {
                case "rosey" -> ROSEY;
                case "coniferous" -> CONIFEROUS;
                default -> EMPTY;
            };
        }
    }
}
