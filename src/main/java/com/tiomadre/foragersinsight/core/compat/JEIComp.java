package com.tiomadre.foragersinsight.core.compat;

import com.tiomadre.foragersinsight.common.crafting.WaxedBootsRecipe;
import com.tiomadre.foragersinsight.core.ForagersInsight;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class JEIComp implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return ForagersInsight.rl("jei_plugin");
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(RecipeTypes.CRAFTING, WaxedBootsRecipe.createJeiRecipes());
    }
}