package com.tiomadre.foragersinsight.common.enchantments;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.enchantment.Enchantment;

public class LuckOfTheTreesEnchantment extends Enchantment {

    public LuckOfTheTreesEnchantment() {
        super(Rarity.RARE, FIEnchantCategories.BRUSH_EXCLUSIVE, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinCost(int level) {
        return 15 + (level - 1) * 10;
    }

    @Override
    public int getMaxCost(int level) {
        return this.getMinCost(level) + 25;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }
}