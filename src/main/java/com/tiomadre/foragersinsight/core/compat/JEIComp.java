package com.tiomadre.foragersinsight.core.compat;

import com.tiomadre.foragersinsight.core.ForagersInsight;
import com.tiomadre.foragersinsight.core.compat.jei.DiffusingRecipeCategory;
import com.tiomadre.foragersinsight.core.registry.FIBlocks;
import com.tiomadre.foragersinsight.data.server.recipes.FIDiffusingRecipes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class JEIComp implements IModPlugin {
    public static final RecipeType<FIDiffusingRecipes> DIFFUSING = RecipeType.create(
            ForagersInsight.MOD_ID,
            "diffusing",
            FIDiffusingRecipes.class
    );

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ForagersInsight.rl("jei_plugin");
    }

    @Override
    public void registerCategories(@NotNull IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new DiffusingRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        registration.addRecipes(DIFFUSING, FIDiffusingRecipes.all());
    }

    @Override
    public void registerRecipeCatalysts(@NotNull IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(FIBlocks.DIFFUSER.get(), DIFFUSING);
    }
}