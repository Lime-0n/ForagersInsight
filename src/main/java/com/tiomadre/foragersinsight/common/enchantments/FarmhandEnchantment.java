package com.tiomadre.foragersinsight.common.enchantments;

import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.entity.EquipmentSlot;
import vectorwing.farmersdelight.common.item.KnifeItem;

public class FarmhandEnchantment extends Enchantment {
    public FarmhandEnchantment() {
        super(Rarity.UNCOMMON, FIEnchantCategories.HARVEST_EXCLUSIVE, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }
    @Override
    public int getMaxLevel() {
        return super.getMaxLevel();
    }
    @Override
    public boolean canEnchant(ItemStack stack) {
        Item item = stack.getItem();
        // any hoes
        if (item instanceof HoeItem) return true;
        // any shears
        if (item instanceof ShearsItem) return true;
        // any knives
        return item instanceof KnifeItem;
    }
}