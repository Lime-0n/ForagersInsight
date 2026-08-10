package com.tiomadre.foragersinsight.common.item;

import com.tiomadre.foragersinsight.core.registry.FISapTrapBaits;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class BaitItem extends Item {
    public BaitItem(Properties properties) {
        super(properties);
    }

    public static boolean isBait(ItemStack stack) {
        return FISapTrapBaits.isBait(stack);
    }

    public static boolean attracts(ItemStack stack, LivingEntity entity) {
        return FISapTrapBaits.attracts(stack, entity);
    }
}