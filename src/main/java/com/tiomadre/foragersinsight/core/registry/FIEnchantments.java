package com.tiomadre.foragersinsight.core.registry;

import com.tiomadre.foragersinsight.common.enchantments.FarmhandEnchantment;
import com.tiomadre.foragersinsight.common.enchantments.LuckOfTheTreesEnchantment;
import com.tiomadre.foragersinsight.core.ForagersInsight;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;


public final class FIEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(Registries.ENCHANTMENT, ForagersInsight.MOD_ID);

    public static final DeferredHolder<Enchantment, FarmhandEnchantment> FARMHAND =
            ENCHANTMENTS.register("farmhand", FarmhandEnchantment::new);

    public static final DeferredHolder<Enchantment, LuckOfTheTreesEnchantment> LUCK_OF_THE_TREES =
            ENCHANTMENTS.register("luck_of_the_trees", LuckOfTheTreesEnchantment::new);

    private FIEnchantments() {
    }

    public static void register(IEventBus bus) {
        ENCHANTMENTS.register(bus);
    }
}