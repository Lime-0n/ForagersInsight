package com.tiomadre.foragersinsight.common.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class BirchSapBottleItem extends Item {
    public BirchSapBottleItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack stack) {
        ItemStack remainder = stack.copy();
        remainder.setCount(1);
        remainder.setDamageValue(remainder.getDamageValue() + 1);
        if (remainder.getDamageValue() >= remainder.getMaxDamage()) {
            return new ItemStack(Items.GLASS_BOTTLE);
        }
        return remainder;
    }
}