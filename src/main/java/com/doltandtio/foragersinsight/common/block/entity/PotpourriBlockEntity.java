package com.doltandtio.foragersinsight.common.block.entity;

import com.doltandtio.foragersinsight.common.block.PotpourriBlock;
import com.doltandtio.foragersinsight.core.ForagersInsight;
import com.doltandtio.foragersinsight.core.registry.FIBlockEntityTypes;
import com.doltandtio.foragersinsight.core.registry.FIItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Clearable;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class PotpourriBlockEntity extends BlockEntity implements Clearable {
    private static final int SLOT_COUNT = 3;
    private static final int EFFECT_INTERVAL = 40;
    private static final int MAX_BLEND_DURATION_TICKS = 20 * 60 * 20;

    private static final Map<List<ResourceLocation>, ScentBlend> SCENT_BLENDS = new HashMap<>();
    private static final Map<ResourceLocation, ScentBlend> SCENT_BLENDS_BY_ID = new HashMap<>();

    static {
        registerBlend("rosey", MobEffects.REGENERATION,
                FIItems.ROSE_PETALS, FIItems.ROSE_PETALS, FIItems.ROSE_PETALS);
        registerBlend("coniferous", MobEffects.DAMAGE_RESISTANCE,
                FIItems.SPRUCE_TIPS, FIItems.SPRUCE_TIPS, FIItems.SPRUCE_TIPS);
    }

    private final NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);

    @Nullable
    private ScentBlend activeBlend;
    private int remainingBlendTicks;
    private int tickCounter;

    public PotpourriBlockEntity(BlockPos pos, BlockState state) {
        super(FIBlockEntityTypes.POTPOURRI.get(), pos, state);
    }

    public boolean addItem(ItemStack stack) {
        if (isBlendActive()) {
            return false;
        }
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
        if (isBlendActive()) {
            return ItemStack.EMPTY;
        }
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
        if (isBlendActive()) {
            return true;
        }
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
        activeBlend = null;
        remainingBlendTicks = 0;
        tickCounter = 0;
        Collections.fill(items, ItemStack.EMPTY);
        onContentsChanged();
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        ContainerHelper.loadAllItems(tag, items);
        remainingBlendTicks = tag.getInt("BlendTime");
        tickCounter = tag.getInt("BlendTickCounter");
        if (tag.contains("ActiveBlend", Tag.TAG_STRING)) {
            ResourceLocation id = ResourceLocation.tryParse(tag.getString("ActiveBlend"));
            activeBlend = id != null ? SCENT_BLENDS_BY_ID.get(id) : null;
        } else {
            activeBlend = null;
        }
        if (!isBlendActive()) {
            refreshBlend(false);
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, items);
        tag.putInt("BlendTime", remainingBlendTicks);
        tag.putInt("BlendTickCounter", tickCounter);
        if (activeBlend != null) {
            tag.putString("ActiveBlend", activeBlend.id().toString());
        }
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        ContainerHelper.saveAllItems(tag, items);
        tag.putInt("BlendTime", remainingBlendTicks);
        tag.putInt("BlendTickCounter", tickCounter);
        if (activeBlend != null) {
            tag.putString("ActiveBlend", activeBlend.id().toString());
        }
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
        updateAppearance();
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    private void refreshBlend() {
        refreshBlend(true);
    }

    private void refreshBlend(boolean resetToFullDuration) {
        if (isBlendActive()) {
            return;
        }
        List<ResourceLocation> cachedKey = createKeyFromInventory();
        if (cachedKey.size() != SLOT_COUNT) {
            tickCounter = 0;
            remainingBlendTicks = 0;
            return;
        }

        ScentBlend blend = SCENT_BLENDS.get(cachedKey);
        if (blend == null) {
            tickCounter = 0;
            remainingBlendTicks = 0;
            return;
        }

        activeBlend = blend;
        tickCounter = EFFECT_INTERVAL - 1;
        if (resetToFullDuration || remainingBlendTicks <= 0 || remainingBlendTicks > MAX_BLEND_DURATION_TICKS) {
            remainingBlendTicks = MAX_BLEND_DURATION_TICKS;
        }
        Collections.fill(items, ItemStack.EMPTY);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        updateAppearance();
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

    @SafeVarargs
    private static void registerBlend(String name, MobEffect effect,
                                      Supplier<? extends Item>... ingredients) {
        ResourceLocation id = new ResourceLocation(ForagersInsight.MOD_ID, name);
        ScentBlend blend = new ScentBlend(id, effect, 200, 0, 5.0);
        List<ResourceLocation> key = keyOf(ingredients);
        SCENT_BLENDS.put(key, blend);
        SCENT_BLENDS_BY_ID.put(id, blend);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, PotpourriBlockEntity blockEntity) {
        if (level.isClientSide) {
            return;
        }
        if (!blockEntity.isBlendActive()) {
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

        ScentBlend blend = blockEntity.activeBlend;
        if (blend == null) {
            return;
        }

        AABB area = new AABB(pos).inflate(blend.radius());
        for (Player player : serverLevel.getEntitiesOfClass(Player.class, area, Player::isAlive)) {
            player.addEffect(new MobEffectInstance(blend.effect(), blend.duration(), blend.amplifier(), true, true));
        }
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, PotpourriBlockEntity blockEntity) {
        if (!blockEntity.isBlendActive() && blockEntity.isEmpty()) {
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

    public boolean isBlendActive() {
        return activeBlend != null && remainingBlendTicks > 0;
    }

    public Container getItemsForDrop() {
        if (isBlendActive()) {
            return new SimpleContainer(0);
        }
        SimpleContainer container = new SimpleContainer(SLOT_COUNT);
        for (int i = 0; i < SLOT_COUNT; i++) {
            ItemStack stack = items.get(i);
            if (!stack.isEmpty()) {
                container.setItem(i, stack.copy());
            }
        }
        return container;
    }

    public Optional<Component> getActiveBlendName() {
        return Optional.ofNullable(activeBlend).map(ScentBlend::name);
    }

    private record ScentBlend(ResourceLocation id, MobEffect effect, int duration, int amplifier, double radius) {
        Component name() {
            return Component.translatable("blend.%s.%s".formatted(id.getNamespace(), id.getPath()));
        }
    }

    private void updateAppearance() {
        if (level == null || level.isClientSide) {
            return;
        }
        BlockState state = level.getBlockState(worldPosition);
        if (!state.hasProperty(PotpourriBlock.CONTENTS)) {
            return;
        }
        PotpourriBlock.PotpourriContents desired = PotpourriBlock.PotpourriContents.fromBlend(activeBlend != null ? activeBlend.id() : null);
        if (state.getValue(PotpourriBlock.CONTENTS) != desired) {
            level.setBlock(worldPosition, state.setValue(PotpourriBlock.CONTENTS, desired), Block.UPDATE_ALL);
        }
    }
}