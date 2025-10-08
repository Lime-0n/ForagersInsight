package com.tiomadre.foragersinsight.common.block.entity;

import com.tiomadre.foragersinsight.common.block.DiffuserBlock;
import com.tiomadre.foragersinsight.common.gui.DiffuserMenu;
import com.tiomadre.foragersinsight.core.registry.FIBlockEntityTypes;
import com.tiomadre.foragersinsight.data.server.tags.FITags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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

public class DiffuserBlockEntity extends BaseContainerBlockEntity {
    private static final int SLOT_COUNT = 3;
    private static final int DATA_COUNT = 4;
    private static final int DEFAULT_DIFFUSION_TIME = 200;

    private NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
    private int litTime;
    private int litDuration;
    private int craftProgress;
    private int craftTimeTotal = DEFAULT_DIFFUSION_TIME;

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

        if (blockEntity.litTime > 0) {
            blockEntity.litTime--;
        }

        if (blockEntity.isLit()) {
            blockEntity.craftProgress = Mth.clamp(blockEntity.craftProgress + 1, 0, blockEntity.craftTimeTotal);
        }

        if (!level.isClientSide) {
            if (!blockEntity.isLit()) {
                if (blockEntity.craftProgress != 0) {
                    blockEntity.craftProgress = 0;
                    changed = true;
                }
                if (blockEntity.consumeOneAromatic()) {
                    blockEntity.startCycle();
                    changed = true;
                }
            }
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

    private void startCycle() {
        this.litDuration = DEFAULT_DIFFUSION_TIME;
        this.craftTimeTotal = DEFAULT_DIFFUSION_TIME;
        this.litTime = this.litDuration;
        this.craftProgress = 0;
    }

    private boolean consumeOneAromatic() {
        for (int slot = 0; slot < this.items.size(); slot++) {
            ItemStack stack = this.items.get(slot);
            if (!stack.isEmpty() && stack.is(FITags.ItemTag.AROMATICS)) {
                stack.shrink(1);
                if (stack.isEmpty()) {
                    this.items.set(slot, ItemStack.EMPTY);
                }
                return true;
            }
        }
        return false;
    }

    public boolean isLit() {
        return this.litTime > 0;
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
        return SLOT_COUNT;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public @NotNull ItemStack getItem(int pSlot) {
        return null;
    }

    @Override
    public @NotNull ItemStack removeItem(int pSlot, int pAmount) {
        return null;
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int pSlot) {
        return null;
    }

    @Override
    public void setItem(int pSlot, @NotNull ItemStack pStack) {

    }

    @Override
    public boolean stillValid(@NotNull Player pPlayer) {
        return false;
    }

    @Override
    public boolean canPlaceItem(int slot, @NotNull ItemStack stack) {
        return stack.is(FITags.ItemTag.AROMATICS);
    }

    @Override
    public void clearContent() {
        this.items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items);
        this.litTime = tag.getInt("LitTime");
        this.litDuration = tag.getInt("LitDuration");
        this.craftProgress = tag.getInt("CraftProgress");
        this.craftTimeTotal = Math.max(DEFAULT_DIFFUSION_TIME, tag.getInt("CraftTimeTotal"));
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, this.items);
        tag.putInt("LitTime", this.litTime);
        tag.putInt("LitDuration", this.litDuration);
        tag.putInt("CraftProgress", this.craftProgress);
        tag.putInt("CraftTimeTotal", this.craftTimeTotal);
    }

    public BlockPos readBlockPos() {
        return null;
    }
}
