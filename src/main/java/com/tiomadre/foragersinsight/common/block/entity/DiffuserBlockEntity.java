package com.tiomadre.foragersinsight.common.block.entity;

import com.tiomadre.foragersinsight.common.block.DiffuserBlock;
import com.tiomadre.foragersinsight.common.diffuser.DiffuserScent;
import com.tiomadre.foragersinsight.common.gui.DiffuserMenu;
import com.tiomadre.foragersinsight.core.registry.FIBlockEntityTypes;
import com.tiomadre.foragersinsight.data.server.tags.FITags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class DiffuserBlockEntity extends BaseContainerBlockEntity {
    public static final int INPUT_SLOT_COUNT = 3;
    public static final int RESULT_SLOT_INDEX = INPUT_SLOT_COUNT;
    private static final int TOTAL_SLOT_COUNT = INPUT_SLOT_COUNT + 1;
    private static final int DATA_COUNT = 4;
    private static final int DEFAULT_DIFFUSION_TIME = DiffuserScent.STANDARD_DURATION;

    private NonNullList<ItemStack> items = NonNullList.withSize(TOTAL_SLOT_COUNT, ItemStack.EMPTY);
    private int litTime;
    private int litDuration;
    private int craftProgress;
    private int craftTimeTotal = DEFAULT_DIFFUSION_TIME;
    @Nullable
    private DiffuserScent activeScent;

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> DiffuserBlockEntity.this.litTime;
                case 1 -> DiffuserBlockEntity.this.litDuration;
                case 2 -> DiffuserBlockEntity.this.craftProgress;
                case 3 -> DiffuserBlockEntity.this.craftTimeTotal;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> DiffuserBlockEntity.this.litTime = value;
                case 1 -> DiffuserBlockEntity.this.litDuration = value;
                case 2 -> DiffuserBlockEntity.this.craftProgress = value;
                case 3 -> DiffuserBlockEntity.this.craftTimeTotal = value;
            }
        }

        @Override
        public int getCount() {
            return DATA_COUNT;
        }
    };

    public DiffuserBlockEntity(BlockPos pos, BlockState state) {
        super(FIBlockEntityTypes.DIFFUSER.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, DiffuserBlockEntity blockEntity) {
        boolean wasLit = blockEntity.isLit();
        boolean changed = false;

        if (blockEntity.isLit()) {
            blockEntity.litTime = Math.max(0, blockEntity.litTime - 1);
            blockEntity.craftProgress = Mth.clamp(blockEntity.craftProgress + 1, 0, blockEntity.craftTimeTotal);
            changed = true;
            if (blockEntity.litTime <= 0 || blockEntity.craftProgress >= blockEntity.craftTimeTotal) {
                blockEntity.finishCycle();
            }
        }

        if (!blockEntity.isLit() && blockEntity.craftProgress != 0) {
            blockEntity.craftProgress = 0;
            blockEntity.craftTimeTotal = DEFAULT_DIFFUSION_TIME;
            blockEntity.litDuration = 0;
            changed = true;
        }

        if (wasLit != blockEntity.isLit()) {
            state = state.setValue(DiffuserBlock.LIT, blockEntity.isLit());
            level.setBlock(pos, state, Block.UPDATE_ALL);
            changed = true;
        }

        if (changed) {
            blockEntity.setChanged();
        }
    }

    private void startCycle(@NotNull DiffuserScent scent) {
        this.activeScent = scent;
        this.items.set(RESULT_SLOT_INDEX, scent.createIcon());
        this.litDuration = scent.duration();
        this.craftTimeTotal = scent.duration();
        this.litTime = this.litDuration;
        this.craftProgress = 0;
        this.updateLitState(true);
        this.setChanged();
    }

    private void finishCycle() {
        if (this.activeScent != null) {
            this.consumeIngredients(this.activeScent);
            this.items.set(RESULT_SLOT_INDEX, ItemStack.EMPTY);
            this.activeScent = null;
        }
        this.litTime = 0;
        this.litDuration = 0;
        this.craftProgress = 0;
        this.craftTimeTotal = DEFAULT_DIFFUSION_TIME;
        this.updateLitState(false);
        this.setChanged();
    }

    private void consumeIngredients(@NotNull DiffuserScent scent) {
        for (DiffuserScent.IngredientCount requirement : scent.ingredients()) {
            int remaining = requirement.count();
            for (int slot = 0; slot < INPUT_SLOT_COUNT && remaining > 0; slot++) {
                ItemStack stack = this.items.get(slot);
                if (requirement.ingredient().test(stack)) {
                    int removed = Math.min(remaining, stack.getCount());
                    stack.shrink(removed);
                    remaining -= removed;
                    if (stack.isEmpty()) {
                        this.items.set(slot, ItemStack.EMPTY);
                    }
                }
            }
        }
    }

    public boolean isLit() {
        return this.litTime > 0;
    }

    public Optional<DiffuserScent> getActiveScent() {
        return Optional.ofNullable(this.activeScent);
    }

    public Optional<DiffuserScent> getMatchingScent() {
        if (this.isLit()) {
            return Optional.empty();
        }
        List<ItemStack> inputs = this.items.subList(0, INPUT_SLOT_COUNT);
        return DiffuserScent.findMatch(inputs);
    }

    public boolean tryStartDiffusion() {
        if (this.level == null || this.isLit()) {
            return false;
        }
        Optional<DiffuserScent> scent = this.getMatchingScent();
        if (scent.isEmpty()) {
            return false;
        }
        this.startCycle(scent.get());
        return true;
    }

    public @NotNull net.minecraft.world.phys.Vec3 getScentColor() {
        return this.activeScent != null ? this.activeScent.particleColor() : DiffuserScent.DEFAULT_COLOR;
    }

    private void updateLitState(boolean lit) {
        Level level = this.getLevel();
        if (level == null) {
            return;
        }
        BlockState state = level.getBlockState(this.worldPosition);
        if (state.hasProperty(DiffuserBlock.LIT) && state.getValue(DiffuserBlock.LIT) != lit) {
            level.setBlock(this.worldPosition, state.setValue(DiffuserBlock.LIT, lit), Block.UPDATE_ALL);
        }
    }

    public ContainerData getDataAccess() {
        return this.dataAccess;
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return Component.translatable("container.foragersinsight.diffuser");
    }

    @Override
    protected @NotNull AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory) {
        return new DiffuserMenu(id, inventory, this);
    }

    @Override
    public int getContainerSize() {
        return TOTAL_SLOT_COUNT;
    }

    @Override
    public boolean isEmpty() {
        for (int slot = 0; slot < INPUT_SLOT_COUNT; slot++) {
            if (!this.items.get(slot).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public @NotNull ItemStack getItem(int pSlot) {
        return this.items.get(pSlot);
    }

    @Override
    public @NotNull ItemStack removeItem(int pSlot, int pAmount) {
        if (pSlot == RESULT_SLOT_INDEX || this.isLit()) {
            return ItemStack.EMPTY;
        }
        ItemStack removed = ContainerHelper.removeItem(this.items, pSlot, pAmount);
        if (!removed.isEmpty()) {
            this.setChanged();
        }
        return removed;
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int pSlot) {
        if (pSlot == RESULT_SLOT_INDEX) {
            this.items.set(pSlot, ItemStack.EMPTY);
            return ItemStack.EMPTY;
        }
        ItemStack stack = this.items.get(pSlot);
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        this.items.set(pSlot, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setItem(int pSlot, @NotNull ItemStack pStack) {
        if (pSlot < INPUT_SLOT_COUNT) {
            if (this.isLit() || (!pStack.isEmpty() && !pStack.is(FITags.ItemTag.AROMATICS))) {
                return;
            }
        }

        this.items.set(pSlot, pStack);
        if (pSlot < INPUT_SLOT_COUNT && !pStack.isEmpty() && pStack.getCount() > this.getMaxStackSize()) {
            pStack.setCount(this.getMaxStackSize());
        }
        this.setChanged();
    }

    @Override
    public boolean stillValid(@NotNull Player pPlayer) {
        Level level = this.getLevel();
        if (level == null) {
            return false;
        }
        if (level.getBlockEntity(this.worldPosition) != this) {
            return false;
        }
        return pPlayer.distanceToSqr(
                this.worldPosition.getX() + 0.5D,
                this.worldPosition.getY() + 0.5D,
                this.worldPosition.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public boolean canPlaceItem(int slot, @NotNull ItemStack stack) {
        return slot < INPUT_SLOT_COUNT && !this.isLit() && stack.is(FITags.ItemTag.AROMATICS);
    }

    @Override
    public void clearContent() {
        this.items = NonNullList.withSize(TOTAL_SLOT_COUNT, ItemStack.EMPTY);
        this.activeScent = null;
        this.litTime = 0;
        this.litDuration = 0;
        this.craftProgress = 0;
        this.craftTimeTotal = DEFAULT_DIFFUSION_TIME;
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items);
        this.litTime = tag.getInt("LitTime");
        this.litDuration = tag.getInt("LitDuration");
        this.craftProgress = tag.getInt("CraftProgress");
        this.craftTimeTotal = tag.contains("CraftTimeTotal", Tag.TAG_INT)
                ? tag.getInt("CraftTimeTotal")
                : DEFAULT_DIFFUSION_TIME;
        if (this.craftTimeTotal <= 0) {
            this.craftTimeTotal = DEFAULT_DIFFUSION_TIME;
        }
        this.activeScent = null;
        if (tag.contains("ActiveScent", Tag.TAG_STRING)) {
            String id = tag.getString("ActiveScent");
            if (!id.isEmpty()) {
                this.activeScent = DiffuserScent.byId(new ResourceLocation(id)).orElse(null);
            }
        }
        if (this.activeScent != null && this.items.get(RESULT_SLOT_INDEX).isEmpty()) {
            this.items.set(RESULT_SLOT_INDEX, this.activeScent.createIcon());
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, this.items);
        tag.putInt("LitTime", this.litTime);
        tag.putInt("LitDuration", this.litDuration);
        tag.putInt("CraftProgress", this.craftProgress);
        tag.putInt("CraftTimeTotal", this.craftTimeTotal);
        if (this.activeScent != null) {
            tag.putString("ActiveScent", this.activeScent.id().toString());
        }
    }

    public BlockPos readBlockPos() {
        return this.worldPosition;
    }
}