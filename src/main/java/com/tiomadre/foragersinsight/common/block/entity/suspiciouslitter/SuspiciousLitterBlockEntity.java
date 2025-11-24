package com.tiomadre.foragersinsight.common.block.entity.suspiciouslitter;

import com.tiomadre.foragersinsight.common.block.SuspiciousLitterBlock;
import com.tiomadre.foragersinsight.core.registry.FIBlockEntityTypes;
import com.tiomadre.foragersinsight.core.registry.FIBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BrushItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class SuspiciousLitterBlockEntity extends BlockEntity {
    private static final int BRUSH_DURATION_TICKS = 60;
    private static final double MAX_DISTANCE_SQR = 9.0D;

    private UUID brusher;
    private int brushTicks;

    public SuspiciousLitterBlockEntity(BlockPos pos, BlockState state) {
        super(FIBlockEntityTypes.SUSPICIOUS_LEAF_LITTER.get(), pos, state);
    }

    public void startBrushing(Player player) {
        this.brusher = player.getUUID();
        this.brushTicks = 0;
    }

    public static void serverTick(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull SuspiciousLitterBlockEntity blockEntity) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        if (!state.is(FIBlocks.SUSPICIOUS_LEAF_LITTER.get()) || !state.hasProperty(SuspiciousLitterBlock.FOLIAGE)) {
            level.removeBlockEntity(pos);
            return;
        }
        if (blockEntity.brusher == null) {
            return;
        }

        Player player = serverLevel.getPlayerByUUID(blockEntity.brusher);
        if (!isBrushing(player)) {
            blockEntity.resetProgress();
            return;
        }

        if (Vec3.atCenterOf(pos).distanceToSqr(player.position()) > MAX_DISTANCE_SQR) {
            blockEntity.resetProgress();
            return;
        }

        blockEntity.brushTicks++;
        if (blockEntity.brushTicks >= BRUSH_DURATION_TICKS) {
            SuspiciousLitterLoot.dropLoot(serverLevel, pos, state, serverLevel.random);
            serverLevel.setBlock(pos, Block.pushEntitiesUp(state, Blocks.AIR.defaultBlockState(), serverLevel, pos), Block.UPDATE_ALL);
            blockEntity.resetProgress();
        }
    }

    private static boolean isBrushing(@Nullable Player player) {
        return player != null && player.isUsingItem() && player.getUseItem().getItem() instanceof BrushItem;
    }

    private void resetProgress() {
        this.brusher = null;
        this.brushTicks = 0;
    }
}