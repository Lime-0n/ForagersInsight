package com.tiomadre.foragersinsight.core.registry;

import com.tiomadre.foragersinsight.common.recipe.AuspiciousStewRecipe;
import com.tiomadre.foragersinsight.core.ForagersInsight;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class FIRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ForagersInsight.MOD_ID);

    public static final RegistryObject<RecipeSerializer<?>> AUSPICIOUS_STEW = RECIPE_SERIALIZERS.register(
            "auspicious_stew",
            () -> new SimpleCraftingRecipeSerializer<>(AuspiciousStewRecipe::new)
    );

    public static void register(IEventBus bus) {
        RECIPE_SERIALIZERS.register(bus);
    }
}