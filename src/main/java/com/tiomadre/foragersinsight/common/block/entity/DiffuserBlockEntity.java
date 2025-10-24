package com.tiomadre.foragersinsight.common.block.entity;

import com.tiomadre.foragersinsight.common.block.DiffuserBlock;
import com.tiomadre.foragersinsight.common.diffuser.DiffuserScent;
import com.tiomadre.foragersinsight.common.gui.DiffuserMenu;
import com.tiomadre.foragersinsight.core.registry.FIBlockEntityTypes;
import com.tiomadre.foragersinsight.data.server.tags.FITags;
import com.tiomadre.foragersinsight.core.registry.FIItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DiffuserBlockEntity extends BaseContainerBlockEntity {
    public static final int INPUT_SLOT_COUNT = 3;
    public static final int ENHANCEMENT_SLOT_INDEX = INPUT_SLOT_COUNT;
    public static final int RESULT_SLOT_INDEX = ENHANCEMENT_SLOT_INDEX + 1;
    private static final int SLOT_COUNT = RESULT_SLOT_INDEX + 1;
    private static final int DATA_COUNT = 4;

    private static final int DEFAULT_DIFFUSION_TIME = DiffuserScent.STANDARD_DURATION;
    private static final int EFFECT_APPLY_INTERVAL = 40;

    private NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
    private int litTime;
    private int litDuration;
    private int craftProgress;
    private int craftTimeTotal = DEFAULT_DIFFUSION_TIME;
    private DiffuserScent activeScent;
    private int effectTickCounter;
    private Enhancement activeEnhancement = Enhancement.NONE;
    private int respirationLevel;

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
            if (!level.isClientSide) {
                blockEntity.effectTickCounter++;
                if (blockEntity.effectTickCounter >= EFFECT_APPLY_INTERVAL) {
                    blockEntity.effectTickCounter = 0;
                    blockEntity.applyActiveScentEffects();
                }
            }
        } else if (blockEntity.craftProgress != 0) {
            blockEntity.craftProgress = 0;
            changed = true;
            blockEntity.effectTickCounter = 0;
        }

        if (!level.isClientSide && !blockEntity.isLit() && blockEntity.activeScent != null) {
            blockEntity.clearActiveScent();
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

    private void startCycle(DiffuserScent scent) {
        this.activeScent = scent;
        this.activeEnhancement = consumeEnhancement();
        int enhancedDuration = (int) Math.round(DEFAULT_DIFFUSION_TIME * this.activeEnhancement.durationMultiplier());
        this.litDuration = enhancedDuration;
        this.craftTimeTotal = enhancedDuration;
        this.litTime = this.litDuration;
        this.craftProgress = 0;
        this.effectTickCounter = 0;
    }

    private void consumeIngredients(DiffuserScent scent) {
        for (DiffuserScent.IngredientCount ingredient : scent.ingredients()) {
            int remaining = ingredient.count();
            for (int slot = 0; slot < INPUT_SLOT_COUNT && remaining > 0; slot++) {
                ItemStack stack = this.items.get(slot);
                if (stack.isEmpty()) {
                    continue;
                }

                if (!ingredient.ingredient().test(stack)) {
                    continue;
                }

                int removed = Math.min(stack.getCount(), remaining);
                stack.shrink(removed);
                remaining -= removed;
                if (stack.isEmpty()) {
                    this.items.set(slot, ItemStack.EMPTY);
                }
            }
        }
    }

    private Optional<DiffuserScent> findMatchingScent() {
        List<ItemStack> inputs = new ArrayList<>(INPUT_SLOT_COUNT);
        for (int slot = 0; slot < INPUT_SLOT_COUNT; slot++) {
            inputs.add(this.items.get(slot));
        }
        return DiffuserScent.findMatch(inputs);
    }

    private void clearActiveScent() {
        this.activeScent = null;
        this.effectTickCounter = 0;
        this.activeEnhancement = Enhancement.NONE;
    }

    public Optional<DiffuserScent> getActiveScent() {
        if (this.activeScent != null) {
            return Optional.of(this.activeScent);
        }
        return findMatchingScent();
    }

    public boolean tryStartDiffusion() {
        if (this.level == null || this.isLit()) {
            return false;
        }
        Optional<DiffuserScent> match = findMatchingScent();
        if (match.isEmpty()) {
            return false;
        }

        DiffuserScent scent = match.get();
        consumeIngredients(scent);
        startCycle(scent);
        this.setChanged();
        BlockState state = this.getBlockState();
        if (!state.getValue(DiffuserBlock.LIT)) {
            this.level.setBlock(this.worldPosition, state.setValue(DiffuserBlock.LIT, true), Block.UPDATE_ALL);
        } else {
            this.level.sendBlockUpdated(this.worldPosition, state, state, Block.UPDATE_ALL);
        }
        return true;
    }

    public boolean isLit() {
        return this.litTime > 0;
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
        if (this.hasActiveScent()) {
            return ItemStack.EMPTY;
        }
        ItemStack result = ContainerHelper.removeItem(this.items, pSlot, pAmount);
        if (!result.isEmpty()) {
            if (this.activeScent != null && !this.isLit()) {
                this.clearActiveScent();
            }
            this.setChanged();
        }
        return result;
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int pSlot) {
        if (this.hasActiveScent()) {
            return ItemStack.EMPTY;
        }
        ItemStack result = ContainerHelper.takeItem(this.items, pSlot);
        if (!result.isEmpty()) {
            if (this.activeScent != null && !this.isLit()) {
                this.clearActiveScent();
            }
            this.setChanged();
        }
        return result;
    }

    @Override
    public void setItem(int pSlot, @NotNull ItemStack pStack) {
        if (pSlot < 0 || pSlot >= this.items.size()) {
            return;
        }
        if (pSlot == RESULT_SLOT_INDEX) {
            return;
        }
        if (this.hasActiveScent()) {
            return;
        }

        this.items.set(pSlot, pStack);
        if ((pSlot < INPUT_SLOT_COUNT || pSlot == ENHANCEMENT_SLOT_INDEX) && pStack.getCount() > 1) {
            pStack.setCount(1);
        } else if (pStack.getCount() > this.getMaxStackSize()) {
            pStack.setCount(this.getMaxStackSize());
        }
        if (this.activeScent != null && !this.isLit()) {
            this.clearActiveScent();
        }
        this.setChanged();
    }
    public void extinguish() {
        boolean wasLit = this.isLit();
        boolean hadActiveScent = this.activeScent != null;

        this.litTime = 0;
        this.litDuration = 0;
        this.craftProgress = 0;
        this.effectTickCounter = 0;

        if (this.activeScent != null) {
            clearActiveScent();
        }

        if (this.level != null) {
            BlockState state = this.getBlockState();
            boolean blockLit = state.getValue(DiffuserBlock.LIT);
            if (blockLit != this.isLit()) {
                this.level.setBlock(this.worldPosition, state.setValue(DiffuserBlock.LIT, false), Block.UPDATE_ALL);
            } else if (wasLit || hadActiveScent) {
                this.level.sendBlockUpdated(this.worldPosition, state, state, Block.UPDATE_ALL);
            }
        }

        this.setChanged();
    }


    @Override
    public boolean stillValid(@NotNull Player pPlayer) {
        if (this.level == null) {
            return false;
        }
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        }
        return pPlayer.distanceToSqr(
                this.worldPosition.getX() + 0.5D,
                this.worldPosition.getY() + 0.5D,
                this.worldPosition.getZ() + 0.5D
        ) <= 64.0D;
    }
    @Override
    public boolean canPlaceItem(int slot, @NotNull ItemStack stack) {
        if (slot < INPUT_SLOT_COUNT) {
            return stack.is(FITags.ItemTag.AROMATICS) && !this.hasActiveScent();
        }
        if (slot == ENHANCEMENT_SLOT_INDEX) {
            return isEnhancementItem(stack) && !this.hasActiveScent();
        }
        return false;
    }
    @Override
    public void clearContent() {
        this.items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
        this.clearActiveScent();
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items);
        for (int slot = 0; slot < INPUT_SLOT_COUNT; slot++) {
            ItemStack stack = this.items.get(slot);
            if (!stack.isEmpty() && stack.getCount() > 1) {
                stack.setCount(1);
            }
            ItemStack enhancement = this.items.get(ENHANCEMENT_SLOT_INDEX);
            if (!enhancement.isEmpty() && enhancement.getCount() > 1) {
                enhancement.setCount(1);
            }
        }
        this.litTime = tag.getInt("LitTime");
        this.litDuration = tag.getInt("LitDuration");
        this.craftProgress = tag.getInt("CraftProgress");
        this.craftTimeTotal = Math.max(DEFAULT_DIFFUSION_TIME, tag.getInt("CraftTimeTotal"));
        this.effectTickCounter = 0;
        this.activeScent = null;
        this.activeEnhancement = Enhancement.NONE;
        if (tag.contains("ActiveScent", CompoundTag.TAG_STRING)) {
            DiffuserScent.byId(new ResourceLocation(tag.getString("ActiveScent")))
                    .ifPresent(scent -> this.activeScent = scent);
        } else if (tag.contains("ActiveScentId", CompoundTag.TAG_INT)) {
            DiffuserScent.byNetworkId(tag.getInt("ActiveScentId")).ifPresent(scent -> this.activeScent = scent);
        }
        if (tag.contains("ActiveEnhancement", CompoundTag.TAG_STRING)) {
            this.activeEnhancement = Enhancement.byName(tag.getString("ActiveEnhancement"));
        }
        this.respirationLevel = Mth.clamp(tag.getInt("RespirationLevel"), 0, 3);
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
            tag.putInt("ActiveScentId", this.activeScent.networkId());
        }
        tag.putString("ActiveEnhancement", this.activeEnhancement.getSerializedName());
        tag.putInt("RespirationLevel", this.respirationLevel);
    }

    public boolean hasActiveScent() {
        return this.activeScent != null;
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public ContainerData getDataAccess() {
        return dataAccess;
    }

    private void applyActiveScentEffects() {
        if (this.level == null || this.activeScent == null) {
            return;
        }
        AABB area = new AABB(this.worldPosition).inflate(this.getEffectiveRadius());
        List<LivingEntity> entities = this.level.getEntitiesOfClass(LivingEntity.class, area);

        this.activeScent.createEffectInstance().ifPresent(template -> {
            for (LivingEntity entity : entities) {
                MobEffectInstance instance = new MobEffectInstance(template.getEffect(), template.getDuration(),
                        template.getAmplifier(), template.isAmbient(), template.isVisible(), template.showIcon());
                entity.addEffect(instance);
            }
        });

        if (shouldRestoreBreath()) {
            restoreBreath(entities);
        }
    }

    private void restoreBreath(List<LivingEntity> entities) {
        for (LivingEntity entity : entities) {
            if (needsBreath(entity)) {
                entity.setAirSupply(entity.getMaxAirSupply());
            }
        }
    }

    private boolean needsBreath(LivingEntity entity) {
        return entity.getAirSupply() < entity.getMaxAirSupply() && entity.isEyeInFluid(FluidTags.WATER);
    }

    private boolean shouldRestoreBreath() {
        return isSubmergedInWater();
    }

    private boolean isSubmergedInWater() {
        if (this.level == null) {
            return false;
        }
        return this.level.getFluidState(this.worldPosition).is(FluidTags.WATER)
                || this.level.getFluidState(this.worldPosition.above()).is(FluidTags.WATER);
    }
    public Enhancement getActiveEnhancement() {
        return this.activeEnhancement;
    }

    public double getEffectiveRadius() {
        if (this.activeScent == null) {
            return 0.0D;
        }
        return this.activeScent.radius() * this.activeEnhancement.radiusMultiplier();
    }

    public int getRemainingDuration() {
        return this.litTime;
    }

    private Enhancement consumeEnhancement() {
        ItemStack stack = this.items.get(ENHANCEMENT_SLOT_INDEX);
        if (stack.isEmpty()) {
            return Enhancement.NONE;
        }

        Enhancement enhancement = Enhancement.fromStack(stack);
        if (enhancement == Enhancement.NONE) {
            return Enhancement.NONE;
        }

        stack.shrink(1);
        if (stack.isEmpty()) {
            this.items.set(ENHANCEMENT_SLOT_INDEX, ItemStack.EMPTY);
        }

        if (enhancement == Enhancement.DURATION && this.level != null && !this.level.isClientSide) {
            ItemStack emptyBottle = new ItemStack(Items.GLASS_BOTTLE);
            Containers.dropItemStack(this.level,
                    this.worldPosition.getX() + 0.5D,
                    this.worldPosition.getY() + 1.0D,
                    this.worldPosition.getZ() + 0.5D,
                    emptyBottle);
        }

        return enhancement;
    }

    private static boolean isEnhancementItem(ItemStack stack) {
        return Enhancement.fromStack(stack) != Enhancement.NONE;
    }

    public enum Enhancement {
        NONE(1.0D, 1.0D, "none"),
        RADIUS(1.2D, 1.0D, "honeycomb"),
        DURATION(1.0D, 1.2D, "birch_sap_bottle");

        private final double radiusMultiplier;
        private final double durationMultiplier;
        private final String serializedName;

        Enhancement(double radiusMultiplier, double durationMultiplier, String serializedName) {
            this.radiusMultiplier = radiusMultiplier;
            this.durationMultiplier = durationMultiplier;
            this.serializedName = serializedName;
        }

        public double radiusMultiplier() {
            return this.radiusMultiplier;
        }

        public double durationMultiplier() {
            return this.durationMultiplier;
        }

        public String getSerializedName() {
            return this.serializedName;
        }

        public static Enhancement fromStack(ItemStack stack) {
            if (stack.is(Items.HONEYCOMB)) {
                return RADIUS;
            }
            if (stack.is(FIItems.BIRCH_SAP_BOTTLE.get())) {
                return DURATION;
            }
            return NONE;
        }

        public static Enhancement byName(String name) {
            for (Enhancement enhancement : values()) {
                if (enhancement.serializedName.equals(name)) {
                    return enhancement;
                }
            }
            return NONE;
        }
    }
}