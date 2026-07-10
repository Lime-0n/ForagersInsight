package com.tiomadre.foragersinsight.core.registry;

import com.tiomadre.foragersinsight.common.crafting.WaxedBootsRecipe;
import com.tiomadre.foragersinsight.core.ForagersInsight;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class FIRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ForagersInsight.MOD_ID);

    public static final RegistryObject<RecipeSerializer<WaxedBootsRecipe>> WAXED_BOOTS = RECIPE_SERIALIZERS.register(
            "crafting_special_waxedboots",
            () -> new net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer<>(WaxedBootsRecipe::new)
    );
}