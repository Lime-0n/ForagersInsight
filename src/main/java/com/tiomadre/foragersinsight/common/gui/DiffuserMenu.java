package com.tiomadre.foragersinsight.common.gui;

import com.tiomadre.foragersinsight.common.block.entity.DiffuserBlockEntity;
import com.tiomadre.foragersinsight.common.diffuser.DiffuserScent;
import com.tiomadre.foragersinsight.core.registry.FIMenuTypes;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@SuppressWarnings("ALL")
public abstract class DiffuserMenu extends AbstractContainerMenu {
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

    private final Container diffuserInv = new SimpleContainer(DIFFUSER_SLOT_COUNT);
    private final ContainerLevelAccess access = ContainerLevelAccess.NULL;
    private final ContainerData data = new ContainerData() {
        private final int[] values = new int[4];

        @Override
        public int get(int index) {
            if (index < 0 || index >= values.length) {
            }
            return values[index];
        }

        @Override
        public void set(int index, int value) {
            if (index < 0 || index >= values.length) {
                throw new IndexOutOfBoundsException("Index out of bounds: " + index);
            }
            values[index] = value;
        }

        @Override
        public int getCount() {
            return values.length;
        }
    };
    @Nullable
    private final DiffuserBlockEntity diffuser = null;

    protected DiffuserMenu(MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
    }

    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        Slot sourceSlot = this.slots.get(index);
        if (!sourceSlot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSource = sourceStack.copy();

        if (index < INPUT_SLOT_COUNT) {
            if (!this.moveItemStackTo(sourceStack, INPUT_SLOT_COUNT, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (!this.moveItemStackTo(sourceStack, 0, INPUT_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
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

    public Optional<DiffuserScent> getActiveScent() {
        if (this.diffuser != null) {
            return this.diffuser.getActiveScent();
        }
        return Optional.empty();
    }
}