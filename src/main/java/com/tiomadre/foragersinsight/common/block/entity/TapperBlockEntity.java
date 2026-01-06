package com.tiomadre.foragersinsight.common.block.entity;

import com.tiomadre.foragersinsight.core.registry.FIBlockEntityTypes;
import com.tiomadre.foragersinsight.core.registry.FIItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TapperBlockEntity extends BlockEntity {
    private static final String TAPPER_KEY = "Tapper";

    private ItemStack tapperStack = ItemStack.EMPTY;

    public TapperBlockEntity(BlockPos pos, BlockState state) {
        super(FIBlockEntityTypes.TAPPER.get(), pos, state);
    }

    public void setTapperStack(ItemStack stack) {
        tapperStack = stack.copy();
        tapperStack.setCount(1);
        setChanged();
    }

    public @NotNull ItemStack getTapperStack() {
        if (tapperStack.isEmpty()) {
            return new ItemStack(FIItems.TAPPER.get());
        }
        return tapperStack.copy();
    }

    public int getFireAspectLevel() {
        return EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FIRE_ASPECT, getTapperStack());
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put(TAPPER_KEY, getTapperStack().save(new CompoundTag()));
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        tapperStack = ItemStack.of(tag.getCompound(TAPPER_KEY));
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public @Nullable ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}