package com.tiomadre.foragersinsight.common.gui;

import com.tiomadre.foragersinsight.common.block.entity.DiffuserBlockEntity;
import com.tiomadre.foragersinsight.core.registry.FIBlocks;
import com.tiomadre.foragersinsight.core.registry.FIMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

public class DiffuserMenu extends AbstractContainerMenu {
    private static final int INPUT_SLOT_COUNT = DiffuserBlockEntity.INPUT_SLOT_COUNT;
    private static final int RESULT_SLOT_INDEX = DiffuserBlockEntity.RESULT_SLOT_INDEX;
    private static final int DIFFUSER_SLOT_COUNT = INPUT_SLOT_COUNT + 1;
    private static final int PLAYER_INVENTORY_ROWS = 3;
    private static final int PLAYER_INVENTORY_COLUMNS = 9;
    private static final int DATA_LIT_TIME = 0;
    private static final int DATA_LIT_DURATION = 1;
    private static final int DATA_PROGRESS = 2;
    private static final int DATA_TOTAL = 3;

    private static final int SLOT_SIZE = 18;
    private static final int SLOT_SPACING = SLOT_SIZE;
    private static final int SLOT_Y = 37;
    private static final int SLOT_START_X = 34;
    private static final int RESULT_SLOT_X = 126;
    private static final int INV_START_X = 8;
    private static final int INV_START_Y = 87;
    private static final int HOTBAR_Y = INV_START_Y + PLAYER_INVENTORY_ROWS * SLOT_SIZE + 4;

    private final Container diffuserContainer;
    private final DiffuserBlockEntity diffuser;
    private final ContainerLevelAccess access;
    private final ContainerData dataAccess;

    public DiffuserMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buffer) {
        this(containerId, playerInventory, resolveBlockEntity(playerInventory, buffer));
    }

    public DiffuserMenu(int containerId, Inventory playerInventory, DiffuserBlockEntity diffuser) {
        super(FIMenuTypes.DIFFUSER_MENU.get(), containerId);
        this.diffuser = Objects.requireNonNull(diffuser, "diffuser");
        this.diffuserContainer = this.diffuser;
        Level level = this.diffuser.getLevel();
        this.access = level != null ? ContainerLevelAccess.create(level, this.diffuser.getBlockPos()) : ContainerLevelAccess.NULL;
        this.dataAccess = this.diffuser.getDataAccess();

        checkContainerSize(this.diffuserContainer, DIFFUSER_SLOT_COUNT);
        this.diffuserContainer.startOpen(playerInventory.player);

        addDiffuserSlots();
        addPlayerInventorySlots(playerInventory);

        this.addDataSlots(this.dataAccess);
    }

    private static DiffuserBlockEntity resolveBlockEntity(Inventory playerInventory, FriendlyByteBuf buffer) {
        Objects.requireNonNull(playerInventory, "playerInventory");
        Objects.requireNonNull(buffer, "buffer");

        BlockPos pos = buffer.readBlockPos();
        Level level = playerInventory.player.level();
        if (level == null) {
            throw new IllegalStateException("Player level is not available");
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof DiffuserBlockEntity diffuser) {
            return diffuser;
        }
        return null;
    }
    private void addDiffuserSlots() {
        for (int slot = 0; slot < INPUT_SLOT_COUNT; slot++) {
            int x = SLOT_START_X + slot * SLOT_SPACING;
            this.addSlot(new Slot(this.diffuserContainer, slot, x, SLOT_Y));
        }

        this.addSlot(new Slot(this.diffuserContainer, RESULT_SLOT_INDEX, RESULT_SLOT_X, SLOT_Y) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return false;
            }

            @Override
            public boolean mayPickup(@NotNull Player player) {
                return false;
            }
        });
    }

    private void addPlayerInventorySlots(Inventory playerInventory) {
        for (int row = 0; row < PLAYER_INVENTORY_ROWS; row++) {
            for (int column = 0; column < PLAYER_INVENTORY_COLUMNS; column++) {
                int slotIndex = column + row * PLAYER_INVENTORY_COLUMNS + PLAYER_INVENTORY_COLUMNS;
                int x = INV_START_X + column * SLOT_SIZE;
                int y = INV_START_Y + row * SLOT_SIZE;
                this.addSlot(new Slot(playerInventory, slotIndex, x, y));
            }
        }

        for (int column = 0; column < PLAYER_INVENTORY_COLUMNS; column++) {
            int x = INV_START_X + column * SLOT_SIZE;
            this.addSlot(new Slot(playerInventory, column, x, HOTBAR_Y));
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        Slot sourceSlot = this.slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copy = sourceStack.copy();

        if (index < DIFFUSER_SLOT_COUNT) {
            if (!this.moveItemStackTo(sourceStack, DIFFUSER_SLOT_COUNT, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (!this.moveItemStackTo(sourceStack, 0, INPUT_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (sourceStack.isEmpty()) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }

        return copy;
    }

    public boolean isLit() {
        return this.dataAccess.get(DATA_LIT_TIME) > 0;
    }

    public int getLitProgress() {
        int lit = this.dataAccess.get(DATA_LIT_TIME);
        int duration = this.dataAccess.get(DATA_LIT_DURATION);
        if (duration <= 0) {
            return 0;
        }
        int max = 14;
        return Math.min(max, (int) ((long) lit * max / duration));
    }

    public int getCraftProgress() {
        int progress = this.dataAccess.get(DATA_PROGRESS);
        int total = this.dataAccess.get(DATA_TOTAL);
        if (total <= 0) {
            return 0;
        }
        int max = 24;
        return Math.min(max, (int) ((long) progress * max / total));
    }

    public <diffuserScent> Optional<diffuserScent> getActiveScent() {
        return (Optional<diffuserScent>) this.diffuser.getActiveScent();
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(this.access, player, FIBlocks.DIFFUSER.get());
    }

    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);
        this.diffuserContainer.stopOpen(player);
    }
}