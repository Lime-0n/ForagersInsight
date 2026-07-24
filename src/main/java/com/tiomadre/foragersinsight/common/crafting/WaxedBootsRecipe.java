package com.tiomadre.foragersinsight.common.crafting;

import com.tiomadre.foragersinsight.core.ForagersInsight;
import com.tiomadre.foragersinsight.core.other.toolevents.WaxedBoots;
import com.tiomadre.foragersinsight.core.registry.FIRecipeSerializers;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public class WaxedBootsRecipe extends CustomRecipe {
    private static final Ingredient HONEYCOMB = Ingredient.of(Items.HONEYCOMB);

    public WaxedBootsRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    public static java.util.List<CraftingRecipe> createJeiRecipes() {
        return java.util.List.of(new ShapelessRecipe(ForagersInsight.rl("jei_waxed_boots"),
                "waxed_boots", CraftingBookCategory.EQUIPMENT, createResult(), createIngredients()));
    }

    @Override
    public boolean matches(@NotNull CraftingContainer container, @NotNull Level level) {
        return getCraftingResult(container).isPresent();
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingContainer container, @NotNull net.minecraft.core.RegistryAccess registryAccess) {
        return getCraftingResult(container).orElse(ItemStack.EMPTY);
    }

    private java.util.Optional<ItemStack> getCraftingResult(CraftingContainer container) {
        ItemStack boots = ItemStack.EMPTY;
        int honeycombCount = 0;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }

            if (HONEYCOMB.test(stack)) {
                honeycombCount++;
                continue;
            }

            if (WaxedBoots.isBoots(stack) && boots.isEmpty()) {
                boots = stack;
                continue;
            }

            return java.util.Optional.empty();
        }

        if (boots.isEmpty() || honeycombCount != WaxedBoots.HONEYCOMB_COUNT) {
            return java.util.Optional.empty();
        }

        ItemStack result = boots.copy();
        result.setCount(1);
        WaxedBoots.wax(result);
        return java.util.Optional.of(result);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 3;
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        return createIngredients();
    }

    private static NonNullList<Ingredient> createIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(createBootsIngredient());
        ingredients.add(HONEYCOMB);
        ingredients.add(HONEYCOMB);
        return ingredients;
    }

    private static Ingredient createBootsIngredient() {
        java.util.List<ItemStack> boots = ForgeRegistries.ITEMS.getValues().stream()
                .map(ItemStack::new)
                .filter(WaxedBoots::isBoots)
                .toList();
        return Ingredient.of(boots.stream());
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull net.minecraft.core.RegistryAccess registryAccess) {
        return createResult();
    }

    private static ItemStack createResult() {
        ItemStack result = new ItemStack(Items.DIAMOND_BOOTS);
        WaxedBoots.wax(result);
        return result;
    }

    @Override
    public @NotNull NonNullList<ItemStack> getRemainingItems(@NotNull CraftingContainer container) {
        NonNullList<ItemStack> remainingItems = NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);
        for (int i = 0; i < remainingItems.size(); ++i) {
            remainingItems.set(i, net.minecraftforge.common.ForgeHooks.getCraftingRemainingItem(container.getItem(i)));
        }
        return remainingItems;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return FIRecipeSerializers.WAXED_BOOTS.get();
    }
}