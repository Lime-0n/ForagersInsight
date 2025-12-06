package com.tiomadre.foragersinsight.common.block.entity.suspiciouslitter;

import com.tiomadre.foragersinsight.common.block.SuspiciousLitterBlock;
import com.tiomadre.foragersinsight.core.other.FarmingXPEvents;
import com.tiomadre.foragersinsight.core.registry.FIEnchantments;
import com.tiomadre.foragersinsight.core.registry.FIBlockEntityTypes;
import com.tiomadre.foragersinsight.core.registry.FIBlocks;
import com.tiomadre.foragersinsight.core.registry.FIParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BrushItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class SuspiciousLitterBlockEntity extends BlockEntity {
    private static final int BRUSH_DURATION_TICKS = 60;
    private static final double MAX_DISTANCE_SQR = 9.0D;
    private static final String NBT_BRUSHER = "Brusher";
    private static final String NBT_BRUSH_TICKS = "BrushTicks";
    private static final String NBT_REVEALED_ITEM = "RevealedItem";
    private static final String NBT_LUCK_OF_THE_TREES = "LuckOfTheTrees";

    private UUID brusher;
    private int brushTicks;
    private ItemStack revealedItem = ItemStack.EMPTY;
    private int luckOfTheTreesLevel;

    public SuspiciousLitterBlockEntity(BlockPos pos, BlockState state) {
        super(FIBlockEntityTypes.SUSPICIOUS_LEAF_LITTER.get(), pos, state);
    }

    public void startBrushing(Player player) {
        this.brusher = player.getUUID();
        this.luckOfTheTreesLevel = EnchantmentHelper.getItemEnchantmentLevel(
                FIEnchantments.LUCK_OF_THE_TREES.get(), player.getUseItem());
        if (this.level instanceof ServerLevel serverLevel) {
            resolveRevealedItem(serverLevel, getBlockState());
            sync();
        }
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
        if (player == null) {
            blockEntity.brusher = null;
            blockEntity.luckOfTheTreesLevel = 0;
            return;
        }

        if (Vec3.atCenterOf(pos).distanceToSqr(player.position()) > MAX_DISTANCE_SQR) {
            blockEntity.brusher = null;
            blockEntity.luckOfTheTreesLevel = 0;
            return;
        }

        if (!isBrushing(player)) {
            return;
        }
        if (!blockEntity.isTargetingThisBlock(player)) {
            blockEntity.brusher = null;
            blockEntity.luckOfTheTreesLevel = 0;
            return;
        }

        blockEntity.resolveRevealedItem(serverLevel, state);
        blockEntity.brushTicks++;
        blockEntity.spawnBreakParticles(serverLevel, state);
        blockEntity.sync();
        if (blockEntity.brushTicks >= BRUSH_DURATION_TICKS) {
            serverLevel.playSound(null, pos, SoundEvents.CHERRY_LEAVES_BREAK, SoundSource.BLOCKS, 1.0F, 0.5F);
            serverLevel.playSound(null, pos, SoundEvents.ROOTED_DIRT_BREAK, SoundSource.BLOCKS, 0.75F, 2.0F);
            ItemStack drop = blockEntity.revealedItem.isEmpty()
                    ? SuspiciousLitterLoot.chooseLoot(state, serverLevel.random)
                    : blockEntity.revealedItem;
            SuspiciousLitterLoot.dropLoot(serverLevel, pos, state, drop);
            if (player instanceof ServerPlayer serverPlayer) {
                FarmingXPEvents.awardSuspiciousLitterXP(serverLevel, serverPlayer, state, drop);
            }
            serverLevel.setBlock(pos, Block.pushEntitiesUp(state, Blocks.AIR.defaultBlockState(), serverLevel, pos), Block.UPDATE_ALL);
            blockEntity.resetProgress();
        }
    }

    private static boolean isBrushing(@Nullable Player player) {
        return player != null && player.isUsingItem() && player.getUseItem().getItem() instanceof BrushItem;
    }
    private boolean isTargetingThisBlock(@NotNull Player player) {
        double reach = Math.sqrt(MAX_DISTANCE_SQR);
        HitResult hitResult = player.pick(reach, 0.0F, false);
        if (hitResult instanceof BlockHitResult blockHitResult) {
            return blockHitResult.getBlockPos().equals(this.worldPosition);
        }
        return false;
    }


    private void resetProgress() {
        this.brusher = null;
        this.brushTicks = 0;
        this.revealedItem = ItemStack.EMPTY;
        this.luckOfTheTreesLevel = 0;
        sync();
    }

    private void resolveRevealedItem(ServerLevel level, BlockState state) {
        if (this.revealedItem.isEmpty()) {
            this.revealedItem = SuspiciousLitterLoot.chooseLoot(state, level.random, this.luckOfTheTreesLevel);
            sync();
        }
    }
    private void spawnBreakParticles(ServerLevel level, BlockState state) {
        if (this.brushTicks % 4 != 0) {
            return;
        }
        SuspiciousLitterBlock.FoliageType foliageType = state.getValue(SuspiciousLitterBlock.FOLIAGE);
        SimpleParticleType particle = switch (foliageType) {
            case SPRUCE -> FIParticleTypes.SUSPICIOUS_NEEDLES.get();
            case FLOWER -> FIParticleTypes.SUSPICIOUS_FLOWER.get();
            default -> FIParticleTypes.SUSPICIOUS_LEAVES.get();
        };

        double x = this.worldPosition.getX() + 0.5D;
        double y = this.worldPosition.getY() + 0.35D;
        double z = this.worldPosition.getZ() + 0.5D;
        level.sendParticles(particle, x, y, z, 4, 0.2D, 0.08D, 0.2D, 0.01D);
    }

    private void sync() {
        if (this.level instanceof ServerLevel serverLevel) {
            setChanged();
            serverLevel.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    public float getProgress() {
        return Math.min(1.0F, (float) this.brushTicks / (float) BRUSH_DURATION_TICKS);
    }

    public ItemStack getRevealedItem() {
        return revealedItem;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        if (this.brusher != null) {
            tag.putUUID(NBT_BRUSHER, this.brusher);
        }
        tag.putInt(NBT_BRUSH_TICKS, this.brushTicks);
        tag.putInt(NBT_LUCK_OF_THE_TREES, this.luckOfTheTreesLevel);
        if (!this.revealedItem.isEmpty()) {
            tag.put(NBT_REVEALED_ITEM, this.revealedItem.save(new CompoundTag()));
        }
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.brusher = tag.hasUUID(NBT_BRUSHER) ? tag.getUUID(NBT_BRUSHER) : null;
        this.brushTicks = tag.getInt(NBT_BRUSH_TICKS);
        this.luckOfTheTreesLevel = tag.getInt(NBT_LUCK_OF_THE_TREES);
        if (tag.contains(NBT_REVEALED_ITEM)) {
            this.revealedItem = ItemStack.of(tag.getCompound(NBT_REVEALED_ITEM));
        } else {
            this.revealedItem = ItemStack.EMPTY;
            this.luckOfTheTreesLevel = 0;
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}