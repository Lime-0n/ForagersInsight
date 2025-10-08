package com.tiomadre.foragersinsight.common.gui;

import com.tiomadre.foragersinsight.common.block.entity.DiffuserBlockEntity;
import com.tiomadre.foragersinsight.core.registry.FIBlocks;
import com.tiomadre.foragersinsight.core.registry.FIMenuTypes;
import com.tiomadre.foragersinsight.data.server.tags.FITags;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@SuppressWarnings("ALL")
public class DiffuserMenu extends AbstractContainerMenu {
    private static final int INPUT_SLOT_COUNT = DiffuserBlockEntity.INPUT_SLOT_COUNT;
    private static final int RESULT_SLOT_INDEX = DiffuserBlockEntity.RESULT_SLOT_INDEX;
    private static final int DIFFUSER_SLOT_COUNT = INPUT_SLOT_COUNT + 1;
    private static final int SLOT_SIZE = 18;
    private static final int SLOT_SPACING = SLOT_SIZE + 2;
    private static final int SLOT_Y = 41;
    private static final int SLOT_START_X = 28;
    private static final int RESULT_SLOT_X = 134;
    private static final int INV_START_X = 8;
    private static final int INV_START_Y = 84;
    private static final int HOTBAR_Y = INV_START_Y + 3 * SLOT_SIZE + 4;

    private static final int DATA_LIT_TIME = 0;
    private static final int DATA_LIT_DURATION = 1;
    private static final int DATA_PROGRESS = 2;
    private static final int DATA_TOTAL = 3;

    private final Container diffuserInv;
    private final ContainerLevelAccess access;
    private final ContainerData data;
    @Nullable
    private final DiffuserBlockEntity diffuser;

    public DiffuserMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
        this(id, playerInv, getBlockEntity(playerInv, buf));
    }


    public DiffuserMenu(int id, Inventory playerInv, DiffuserBlockEntity be) {
        this(id, playerInv,
                be,
                be.getLevel() == null ? ContainerLevelAccess.NULL : ContainerLevelAccess.create(be.getLevel(), be.getBlockPos()),
                be.getDataAccess());
    }

    private DiffuserMenu(int id, Inventory playerInv, Container container, ContainerLevelAccess access, ContainerData data) {
        super(FIMenuTypes.DIFFUSER_MENU.get(), id);
        checkContainerSize(container, DIFFUSER_SLOT_COUNT);
        this.diffuserInv = container;
        this.access = access;
        this.data = data;
        this.diffuser = container instanceof DiffuserBlockEntity diffuser ? diffuser : null;
        this.addDataSlots(this.data);
        container.startOpen(playerInv.player);

        for (int slot = 0; slot < INPUT_SLOT_COUNT; slot++) {
            int x = SLOT_START_X + slot * SLOT_SPACING;
            addSlot(new Slot(container, slot, x, SLOT_Y) {
                @Override
                public boolean mayPlace(@NotNull ItemStack stack) {
                    return !isLocked() && stack.is(FITags.ItemTag.AROMATICS);
                }

                @Override
                public boolean mayPickup(@NotNull Player player) {
                    return !isLocked();
                }

                private boolean isLocked() {
                    return DiffuserMenu.this.diffuser != null && DiffuserMenu.this.diffuser.isLit();
                }
            });
        }

        addSlot(new Slot(container, RESULT_SLOT_INDEX, RESULT_SLOT_X, SLOT_Y) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return false;
            }

            @Override
            public boolean mayPickup(@NotNull Player player) {
                return false;
            }
        });

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int index = col + row * 9 + 9;
                int x = INV_START_X + col * SLOT_SIZE;
                int y = INV_START_Y + row * SLOT_SIZE;
                addSlot(new Slot(playerInv, index, x, y));
            }
        }

        for (int col = 0; col < 9; col++) {
            int x = INV_START_X + col * SLOT_SIZE;
            addSlot(new Slot(playerInv, col, x, HOTBAR_Y));
        }
    }

    private static DiffuserBlockEntity getBlockEntity(Inventory playerInv, FriendlyByteBuf buf) {
        Objects.requireNonNull(playerInv, "playerInv");
        Objects.requireNonNull(buf, "buf");
        BlockPos pos = buf.readBlockPos();
        Level level = playerInv.player.level();
        Objects.requireNonNull(level, "player level");
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof DiffuserBlockEntity diffuser) return diffuser;
        throw new IllegalStateException("Missing Diffuser block entity at %s".formatted(pos));
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(this.access, player, FIBlocks.DIFFUSER.get());
    }

    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);
        this.diffuserInv.stopOpen(player);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        Slot sourceSlot = this.slots.get(index);
        if (!sourceSlot.hasItem()) return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSource = sourceStack.copy();

        if (index < DIFFUSER_SLOT_COUNT) {
            if (index == RESULT_SLOT_INDEX) {
                return ItemStack.EMPTY;
            }
            if (!this.moveItemStackTo(sourceStack, DIFFUSER_SLOT_COUNT, this.slots.size(), true))
                return ItemStack.EMPTY;
        } else {
            if (!sourceStack.is(FITags.ItemTag.AROMATICS) ||
                    !this.moveItemStackTo(sourceStack, 0, INPUT_SLOT_COUNT, false))
                return ItemStack.EMPTY;
        }

        if (sourceStack.isEmpty()) sourceSlot.set(ItemStack.EMPTY);
        else sourceSlot.setChanged();
        return copyOfSource;
    }

    public boolean isLit() {
        return this.data.get(DATA_LIT_TIME) > 0;
    }

    public int getLitProgress() {
        int lit = this.data.get(DATA_LIT_TIME);
        int dur = this.data.get(DATA_LIT_DURATION);
        if (dur <= 0) return 0;
        int max = 14;
        return Math.min(max, (int)((long)lit * max / dur));
    }

    public int getCraftProgress() {
        int prog = this.data.get(DATA_PROGRESS);
        int total = this.data.get(DATA_TOTAL);
        if (total <= 0) return 0;
        int max = 24;
        return Math.min(max, (int)((long)prog * max / total));
    }
}
