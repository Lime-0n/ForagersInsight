package com.doltandtio.foragersinsight.common.block.entity;

import com.doltandtio.foragersinsight.core.registry.FIBlockEntityTypes;
import com.doltandtio.foragersinsight.core.registry.FIItems;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
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
    private static final int MAX_BLEND_DURATION_TICKS = 20 * 60 * 20;

    private static final Map<List<ResourceLocation>, ScentBlend> SCENT_BLENDS = Util.make(new HashMap<>(), map -> {
        map.put(keyOf(FIItems.ROSE_PETALS, FIItems.ROSE_PETALS, FIItems.ROSE_PETALS),
                new ScentBlend(MobEffects.REGENERATION, 200, 0, 5.0D));

        map.put(keyOf(FIItems.SPRUCE_TIPS, FIItems.SPRUCE_TIPS, FIItems.SPRUCE_TIPS),
                new ScentBlend(MobEffects.DAMAGE_RESISTANCE, 200, 0, 5.0D));

    });

    private final NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
    @SuppressWarnings("unused")
    private final List<ItemStack> displayView = java.util.Collections.unmodifiableList(items);

    @Nullable
    private ScentBlend cachedBlend;
    private int remainingBlendTicks;
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

    public boolean isFull() {
        for (ItemStack stack : items) {
            if (stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public boolean isEmpty() {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void clearContent() {
        Collections.fill(items, ItemStack.EMPTY);
        onContentsChanged();
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        ContainerHelper.loadAllItems(tag, items);
        remainingBlendTicks = tag.getInt("BlendTime");
        refreshBlend(false);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, items);
        tag.putInt("BlendTime", remainingBlendTicks);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        ContainerHelper.saveAllItems(tag, items);
        tag.putInt("BlendTime", remainingBlendTicks);
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
        refreshBlend();
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    private void refreshBlend() {
        refreshBlend(true);
    }

    private void refreshBlend(boolean resetToFullDuration) {
        List<ResourceLocation> cachedKey = createKeyFromInventory();
        if (cachedKey.size() != SLOT_COUNT) {
            cachedBlend = null;
            tickCounter = 0;
            remainingBlendTicks = 0;
            return;
        }

        cachedBlend = SCENT_BLENDS.get(cachedKey);
        if (cachedBlend == null) {
            tickCounter = 0;
            remainingBlendTicks = 0;
            return;
        }

        tickCounter = EFFECT_INTERVAL - 1;
        if (resetToFullDuration || remainingBlendTicks <= 0 || remainingBlendTicks > MAX_BLEND_DURATION_TICKS) {
            remainingBlendTicks = MAX_BLEND_DURATION_TICKS;
        }
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

    public static void serverTick(Level level, BlockPos pos, BlockState state, PotpourriBlockEntity blockEntity) {
        if (level.isClientSide) {
            return;
        }
        ScentBlend blend = blockEntity.cachedBlend;
        if (blend == null) {
            blockEntity.tickCounter = 0;
            return;
        }

        if (blockEntity.remainingBlendTicks <= 0) {
            blockEntity.clearContent();
            return;
        }

        blockEntity.remainingBlendTicks--;
        if (blockEntity.remainingBlendTicks % 20 == 0) {
            blockEntity.setChanged();
        }

        blockEntity.tickCounter++;
        if (blockEntity.tickCounter < EFFECT_INTERVAL) {
            return;
        }
        blockEntity.tickCounter = 0;

        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        AABB area = new AABB(pos).inflate(blend.radius());
        for (Player player : serverLevel.getEntitiesOfClass(Player.class, area, Player::isAlive)) {
            player.addEffect(new MobEffectInstance(blend.effect(), blend.duration(), blend.amplifier(), true, true));
        }
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, PotpourriBlockEntity blockEntity) {
        if (blockEntity.isEmpty()) {
            return;
        }

        if (level.random.nextInt(4) != 0) {
            return;
        }

        double centerX = pos.getX() + 0.5D;
        double centerY = pos.getY() + 0.6D;
        double centerZ = pos.getZ() + 0.5D;
        double offsetX = (level.random.nextDouble() - 0.5D) * 0.3D;
        double offsetZ = (level.random.nextDouble() - 0.5D) * 0.3D;
        double verticalSpeed = 0.02D + level.random.nextDouble() * 0.02D;

        level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                centerX + offsetX,
                centerY,
                centerZ + offsetZ,
                0.0D,
                verticalSpeed,
                0.0D);
    }

    private record ScentBlend(MobEffect effect, int duration, int amplifier, double radius) {}

    @SuppressWarnings("unused")
    public List<ItemStack> getDisplayView() {
        return java.util.Collections.unmodifiableList(items);
    }
}
