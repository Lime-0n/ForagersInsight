package com.tiomadre.foragersinsight.common.enchantments;

import net.minecraft.world.item.BrushItem;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.registries.ForgeRegistries;

public class FIEnchantCategories {

    public static final EnchantmentCategory HARVEST_EXCLUSIVE = EnchantmentCategory.create("Forager", item -> {
        // Hoes
        if (item.getDescriptionId().contains("hoe")) return true;
        // Shears
        if (item instanceof ShearsItem) return true;
        // Knives
        String registryName = ForgeRegistries.ITEMS.getKey(item).toString();
        return registryName.startsWith("farmersdelight:") && registryName.contains("_knife");
    });

    public static final EnchantmentCategory BRUSH_EXCLUSIVE = EnchantmentCategory.create("Brush", item ->
            item instanceof BrushItem);

}