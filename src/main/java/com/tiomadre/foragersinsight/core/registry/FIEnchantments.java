package com.tiomadre.foragersinsight.core.registry;

import com.tiomadre.foragersinsight.common.enchantments.ConcussiveEnchantment;
import com.tiomadre.foragersinsight.common.enchantments.FarmhandEnchantment;
import com.tiomadre.foragersinsight.common.enchantments.LuckOfTheTreesEnchantment;
import com.tiomadre.foragersinsight.core.ForagersInsight;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class FIEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, ForagersInsight.MOD_ID);

    public static final RegistryObject<Enchantment> FARMHAND =
            ENCHANTMENTS.register("farmhand", FarmhandEnchantment::new);

    public static final RegistryObject<Enchantment> CONCUSSIVE =
            ENCHANTMENTS.register("concussive", () -> new ConcussiveEnchantment(Enchantment.Rarity.UNCOMMON));

    public static final RegistryObject<Enchantment> LUCK_OF_THE_TREES =
            ENCHANTMENTS.register("luck_of_the_trees", LuckOfTheTreesEnchantment::new);

    private FIEnchantments() {
    }

    public static void register(IEventBus bus) {
        ENCHANTMENTS.register(bus);
    }
}