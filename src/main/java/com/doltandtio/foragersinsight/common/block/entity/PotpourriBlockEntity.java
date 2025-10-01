package com.doltandtio.foragersinsight.common.block.entity;

import com.doltandtio.foragersinsight.common.block.PotpourriBlock;
import com.doltandtio.foragersinsight.core.ForagersInsight;
import com.doltandtio.foragersinsight.core.registry.FIBlockEntityTypes;
import com.doltandtio.foragersinsight.core.registry.FIItems;
import com.doltandtio.foragersinsight.core.registry.FIMobEffects;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class PotpourriBlockEntity extends BlockEntity implements Clearable {
    private static final int SLOT_COUNT = 3;
    private static final int EFFECT_INTERVAL = 40;
    private static final int SCENT_DURATION_TICKS = 18000;

    private static final List<ScentRecipe> RECIPES = List.of(
            new ScentRecipe(PotpourriScent.ROSEY,
                    List.of(FIItems.ROSE_PETALS, FIItems.ROSE_PETALS, FIItems.ROSE_PETALS)),
            new ScentRecipe(PotpourriScent.CONIFEROUS,
                    List.of(FIItems.SPRUCE_TIPS, FIItems.SPRUCE_TIPS, FIItems.SPRUCE_TIPS)),
            new ScentRecipe(PotpourriScent.FLORAL,
                    List.of(FIItems.ROSELLE_PETALS, () -> Items.LILAC, FIItems.ROSE_PETALS))
    );

    private final NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);

    @Nullable
    private PotpourriScent activeScent;
    private int scentTicksRemaining;
    private int effectTicker;

    public PotpourriBlockEntity(BlockPos pos, BlockState state) {
        super(FIBlockEntityTypes.POTPOURRI.get(), pos, state);
    }

    public boolean addItem(ItemStack stack) {
        if (isScentActive()) {
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
        if (isScentActive()) {
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
        if (isScentActive()) {
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
        Collections.fill(items, ItemStack.EMPTY);
        activeScent = null;
        scentTicksRemaining = 0;
        effectTicker = 0;
        setChanged();
        updateState();
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        ContainerHelper.loadAllItems(tag, items);
        scentTicksRemaining = tag.getInt("ScentTime");
        effectTicker = tag.getInt("ScentTicker");
        if (tag.contains("ActiveScent", Tag.TAG_STRING)) {
            ResourceLocation id = ResourceLocation.tryParse(tag.getString("ActiveScent"));
            activeScent = PotpourriScent.byId(id);
        } else {
            activeScent = null;
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, items);
        tag.putInt("ScentTime", scentTicksRemaining);
        tag.putInt("ScentTicker", effectTicker);
        if (activeScent != null) {
            tag.putString("ActiveScent", activeScent.id().toString());
        }
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        ContainerHelper.saveAllItems(tag, items);
        tag.putInt("ScentTime", scentTicksRemaining);
        tag.putInt("ScentTicker", effectTicker);
        if (activeScent != null) {
            tag.putString("ActiveScent", activeScent.id().toString());
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

    @Override
    public void onLoad() {
        super.onLoad();
        if (level != null && !level.isClientSide) {
            if (!isScentActive()) {
                tryActivateScent();
            }
            updateState();
        }
    }

    private void onContentsChanged() {
        setChanged();
        if (level != null && !level.isClientSide) {
            tryActivateScent();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    private void tryActivateScent() {
        if (level == null || level.isClientSide || isScentActive()) {
            return;
        }
        if (items.stream().anyMatch(ItemStack::isEmpty)) {
            return;
        }
        for (ScentRecipe recipe : RECIPES) {
            if (recipe.matches(items)) {
                startScent(recipe.scent());
                return;
            }
        }
    }

    private void startScent(PotpourriScent scent) {
        activeScent = scent;
        scentTicksRemaining = SCENT_DURATION_TICKS;
        effectTicker = 0;
        Collections.fill(items, ItemStack.EMPTY);
        setChanged();
        updateState();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    private void finishScent() {
        activeScent = null;
        scentTicksRemaining = 0;
        effectTicker = 0;
        setChanged();
        updateState();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }
    private void updateState() {
        if (level == null || level.isClientSide) {
            return;
        }

        BlockState currentState = level.getBlockState(worldPosition);
        if (!currentState.hasProperty(PotpourriBlock.CONTENTS)) {
            return;
        }

        PotpourriBlock.PotpourriContents desired = activeScent != null
                ? activeScent.contents()
                : PotpourriBlock.PotpourriContents.EMPTY;

        if (currentState.getValue(PotpourriBlock.CONTENTS) == desired) {
            return;
        }

        BlockState newState = currentState.setValue(PotpourriBlock.CONTENTS, desired);

        level.setBlock(worldPosition, newState,
                Block.UPDATE_CLIENTS | Block.UPDATE_NEIGHBORS | Block.UPDATE_KNOWN_SHAPE);
        level.sendBlockUpdated(worldPosition, currentState, newState, Block.UPDATE_CLIENTS);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, PotpourriBlockEntity blockEntity) {
        if (level.isClientSide) {
            return;
        }
        blockEntity.updateState();
        if (!blockEntity.isScentActive()) {
            blockEntity.effectTicker = 0;
            return;
        }

        if (blockEntity.scentTicksRemaining <= 0) {
            blockEntity.finishScent();
            return;
        }

        blockEntity.scentTicksRemaining--;
        if (blockEntity.scentTicksRemaining % 20 == 0) {
            blockEntity.setChanged();
        }

        blockEntity.effectTicker++;
        if (blockEntity.effectTicker < EFFECT_INTERVAL) {
            return;
        }
        blockEntity.effectTicker = 0;

        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        PotpourriScent scent = blockEntity.activeScent;
        if (scent == null) {
            return;
        }

        AABB area = new AABB(pos).inflate(scent.radius());
        MobEffectInstance instance = new MobEffectInstance(scent.effect(), scent.effectDuration(), scent.amplifier(), true, true);
        for (Player player : serverLevel.getEntitiesOfClass(Player.class, area, Player::isAlive)) {
            player.addEffect(new MobEffectInstance(instance));
        }
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, PotpourriBlockEntity blockEntity) {
        if (!blockEntity.isScentActive() && blockEntity.isEmpty()) {
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

    public boolean isScentActive() {
        return activeScent != null && scentTicksRemaining > 0;
    }

    public Container getItemsForDrop() {
        if (isScentActive()) {
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

    public Optional<Component> getActiveScentName() {
        return Optional.ofNullable(activeScent).map(PotpourriScent::displayName);
    }

    private record ScentRecipe(PotpourriScent scent, List<Supplier<? extends Item>> ingredients) {
        boolean matches(List<ItemStack> inventory) {
            if (inventory.size() != SLOT_COUNT) {
                return false;
            }
            Map<Item, Long> recipeCounts = ingredients.stream()
                    .map(Supplier::get)
                    .map(Item::asItem)
                    .collect(Collectors.groupingBy(item -> item, Collectors.counting()));

            Map<Item, Long> inventoryCounts = inventory.stream()
                    .filter(stack -> !stack.isEmpty())
                    .map(ItemStack::getItem)
                    .collect(Collectors.groupingBy(item -> item, Collectors.counting()));

            return inventoryCounts.equals(recipeCounts);
        }
    }

    private enum PotpourriScent {
        ROSEY("rosey", MobEffects.REGENERATION, 0, 5.0D, 200, PotpourriBlock.PotpourriContents.ROSEY),
        CONIFEROUS("coniferous", MobEffects.DAMAGE_RESISTANCE, 0, 5.0D, 200, PotpourriBlock.PotpourriContents.CONIFEROUS),
        FLORAL("floral", FIMobEffects.BLOOM, 0, 5.0D, 200, PotpourriBlock.PotpourriContents.FLORAL);

        private static final Map<ResourceLocation, PotpourriScent> BY_ID = new HashMap<>();

        static {
            for (PotpourriScent scent : values()) {
                BY_ID.put(scent.id, scent);
            }
        }

        private final ResourceLocation id;
        private final Supplier<? extends MobEffect> effect;
        private final int amplifier;
        private final double radius;
        private final int effectDuration;
        private final PotpourriBlock.PotpourriContents contents;

        PotpourriScent(String name, MobEffect effect, int amplifier, double radius, int effectDuration,
                       PotpourriBlock.PotpourriContents contents) {
            this(name, () -> effect, amplifier, radius, effectDuration, contents);
        }

        PotpourriScent(String name, Supplier<? extends MobEffect> effect, int amplifier, double radius, int effectDuration,
                       PotpourriBlock.PotpourriContents contents) {
            this.id = new ResourceLocation(ForagersInsight.MOD_ID, name);
            this.effect = effect;
            this.amplifier = amplifier;
            this.radius = radius;
            this.effectDuration = effectDuration;
            this.contents = contents;
        }

        public ResourceLocation id() {
            return id;
        }

        @Nullable
        static PotpourriScent byId(@Nullable ResourceLocation id) {
            if (id == null) {
                return null;
            }
            return BY_ID.get(id);
        }

        public MobEffect effect() {
            return effect.get();
        }

        public int amplifier() {
            return amplifier;
        }

        public double radius() {
            return radius;
        }

        public int effectDuration() {
            return effectDuration;
        }

        public PotpourriBlock.PotpourriContents contents() {
            return contents;
        }

        public Component displayName() {
            return Component.translatable("scent.%s.%s".formatted(id.getNamespace(), id.getPath()));
        }
    }
}