package com.doltandtio.foragersinsight.common.block.entity;

import com.doltandtio.foragersinsight.core.registry.FIBlockEntityTypes;
import com.doltandtio.foragersinsight.core.registry.FIItems;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Clearable;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class PotpourriBlockEntity extends BlockEntity implements Clearable {
    private static final int SLOT_COUNT = 3;
    private static final int EFFECT_INTERVAL = 40;

    private static final Map<List<ResourceLocation>, PotpourriInfusion> INFUSIONS = Util.make(new HashMap<>(), map -> {
        map.put(keyOf(FIItems.ROSE_PETALS), new PotpourriInfusion(MobEffects.REGENERATION, 200, 0, 5.0D));
        map.put(keyOf(FIItems.ROSELLE_PETALS), new PotpourriInfusion(MobEffects.DAMAGE_RESISTANCE, 200, 0, 5.0D));
        map.put(keyOf(FIItems.SPRUCE_TIPS), new PotpourriInfusion(MobEffects.MOVEMENT_SPEED, 200, 0, 5.0D));
        map.put(keyOf(() -> Items.GLOW_BERRIES), new PotpourriInfusion(MobEffects.NIGHT_VISION, 200, 0, 6.0D));

        map.put(keyOf(FIItems.ROSE_PETALS, FIItems.ROSELLE_PETALS), new PotpourriInfusion(MobEffects.HEALTH_BOOST, 400, 0, 7.0D));
        map.put(keyOf(FIItems.ROSE_PETALS, FIItems.SPRUCE_TIPS), new PotpourriInfusion(MobEffects.LUCK, 400, 0, 7.0D));
        map.put(keyOf(FIItems.ROSELLE_PETALS, FIItems.SPRUCE_TIPS), new PotpourriInfusion(MobEffects.DAMAGE_BOOST, 200, 1, 7.0D));
        map.put(keyOf(FIItems.ROSE_PETALS, () -> Items.GLOW_BERRIES), new PotpourriInfusion(MobEffects.GLOWING, 200, 0, 6.0D));
        map.put(keyOf(FIItems.SPRUCE_TIPS, () -> Items.GLOW_BERRIES), new PotpourriInfusion(MobEffects.MOVEMENT_SPEED, 200, 1, 7.0D));

        map.put(keyOf(FIItems.ROSE_PETALS, FIItems.ROSELLE_PETALS, FIItems.SPRUCE_TIPS),
                new PotpourriInfusion(MobEffects.REGENERATION, 200, 1, 10.0D));
    });

    private final net.minecraft.core.NonNullList<ItemStack> items = net.minecraft.core.NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
    private final List<ItemStack> displayView = java.util.Collections.unmodifiableList(items);

    @Nullable
    private PotpourriInfusion cachedEffect;
    private int tickCounter;

    public PotpourriBlockEntity(BlockPos pos, BlockState state) {
        super(FIBlockEntityTypes.POTPOURRI.get(), pos, state);
    }

    public boolean addItem(ItemStack stack) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).isEmpty()) {
                ItemStack copy = stack.copy();
                copy.setCount(1);
                items.set(i, copy);
                onContentsChanged();
                return true;
            }
        }
        return false;
    }

    public ItemStack removeLastItem() {
        for (int i = items.size() - 1; i >= 0; i--) {
            ItemStack stack = items.get(i);
            if (!stack.isEmpty()) {
                items.set(i, ItemStack.EMPTY);
                onContentsChanged();
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    public net.minecraft.core.NonNullList<ItemStack> getItemsForDrop() {
        net.minecraft.core.NonNullList<ItemStack> drops = net.minecraft.core.NonNullList.withSize(items.size(), ItemStack.EMPTY);
        for (int i = 0; i < items.size(); i++) {
            ItemStack stack = items.get(i);
            drops.set(i, stack.isEmpty() ? ItemStack.EMPTY : stack.copy());
        }
        return drops;
    }

    public List<ItemStack> getDisplayedItems() {
        return displayView;
    }

    public boolean isEmpty() {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public boolean isFull() {
        for (ItemStack stack : items) {
            if (stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void clearContent() {
        // Replace loop with replaceAll (same effect, cleaner)
        Collections.fill(items, ItemStack.EMPTY);
        onContentsChanged();
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        ContainerHelper.loadAllItems(tag, items);
        refreshEffect();
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, items);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        ContainerHelper.saveAllItems(tag, items);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void handleUpdateTag(@NotNull CompoundTag tag) {
        load(tag);
    }

    private void onContentsChanged() {
        refreshEffect();
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    private void refreshEffect() {
        List<ResourceLocation> cachedKey = createKeyFromInventory();
        cachedEffect = INFUSIONS.get(cachedKey);
        tickCounter = cachedEffect == null ? 0 : EFFECT_INTERVAL - 1;
    }

    private List<ResourceLocation> createKeyFromInventory() {
        List<ResourceLocation> key = items.stream()
                .filter(stack -> !stack.isEmpty())
                .map(ItemStack::getItem)
                .map(ForgeRegistries.ITEMS::getKey)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(ResourceLocation::toString))
                .collect(Collectors.toCollection(ArrayList::new));
        return List.copyOf(key);
    }

    @SafeVarargs
    private static List<ResourceLocation> keyOf(Supplier<? extends Item>... items) {
        List<ResourceLocation> key = new ArrayList<>(items.length);
        for (Supplier<? extends Item> supplier : items) {
            Item item = supplier.get();
            ResourceLocation location = ForgeRegistries.ITEMS.getKey(item);
            if (location != null) {
                key.add(location);
            }
        }
        key.sort(Comparator.comparing(ResourceLocation::toString));
        return List.copyOf(key);
    }

    @SuppressWarnings("unused")
    public static void serverTick(Level level, BlockPos pos, BlockState state, PotpourriBlockEntity blockEntity) {
        if (level.isClientSide) {
            return;
        }
        PotpourriInfusion infusion = blockEntity.cachedEffect;
        if (infusion == null) {
            blockEntity.tickCounter = 0;
            return;
        }

        blockEntity.tickCounter++;
        if (blockEntity.tickCounter < EFFECT_INTERVAL) {
            return;
        }
        blockEntity.tickCounter = 0;

        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        // FIX: use BlockPos overload directly, then inflate
        AABB area = new AABB(pos).inflate(infusion.radius());
        for (Player player : serverLevel.getEntitiesOfClass(Player.class, area, Player::isAlive)) {
            player.addEffect(new MobEffectInstance(infusion.effect(), infusion.duration(), infusion.amplifier(), true, true));
        }
    }

    private record PotpourriInfusion(MobEffect effect, int duration, int amplifier, double radius) {}
}
