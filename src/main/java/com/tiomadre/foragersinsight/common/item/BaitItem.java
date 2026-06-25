package com.tiomadre.foragersinsight.common.item;

import com.tiomadre.foragersinsight.core.registry.FIItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

public class BaitItem extends Item {
    private static final ResourceLocation FARMERS_DELIGHT_CHICKEN_CUTS = new ResourceLocation("farmersdelight", "chicken_cuts");

    public BaitItem(Properties properties) {
        super(properties);
    }

    public static boolean isBait(ItemStack stack) {
        return !stack.isEmpty() && getTargetMob(stack) != null;
    }

    public static Class<? extends LivingEntity> getTargetMob(ItemStack stack) {
        if (stack.is(Items.CARROT) || stack.is(FIItems.DANDELION_ROOT.get())) {
            return Rabbit.class;
        }
        if (stack.is(Items.CHICKEN) || isOptionalItem(stack, FARMERS_DELIGHT_CHICKEN_CUTS)) {
            return Ocelot.class;
        }
        if (stack.is(Items.WHEAT_SEEDS)) {
            return Chicken.class;
        }
        return null;
    }

    public static boolean attracts(ItemStack stack, LivingEntity entity) {
        Class<? extends LivingEntity> targetMob = getTargetMob(stack);
        return targetMob != null && targetMob.isInstance(entity);
    }

    private static boolean isOptionalItem(ItemStack stack, ResourceLocation itemId) {
        Item item = ForgeRegistries.ITEMS.getValue(itemId);
        return item != null && stack.is(item);
    }
}