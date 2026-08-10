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
    private static final Ingredient HONEYCOMB_BLOCK = Ingredient.of(Items.HONEYCOMB_BLOCK);

    public WaxedBootsRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    public static java.util.List<CraftingRecipe> createJeiRecipes() {
        return java.util.List.of(
                new ShapelessRecipe(ForagersInsight.rl("jei_waxed_boots"), "waxed_boots",
                        CraftingBookCategory.EQUIPMENT, createResult(false), createIngredients(false)),
                new ShapelessRecipe(ForagersInsight.rl("jei_waxed_boots_honeycomb_block"), "waxed_boots",
                        CraftingBookCategory.EQUIPMENT, createResult(true), createIngredients(true))
        );
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
        boolean hasHoneycombBlock = false;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }

            if (HONEYCOMB.test(stack)) {
                honeycombCount++;
                continue;
            }

            if (HONEYCOMB_BLOCK.test(stack) && !hasHoneycombBlock) {
                hasHoneycombBlock = true;
                continue;
            }

            if (WaxedBoots.isBoots(stack) && boots.isEmpty()) {
                boots = stack;
                continue;
            }

            return java.util.Optional.empty();
        }

        boolean isNormalRecipe = honeycombCount == WaxedBoots.HONEYCOMB_COUNT && !hasHoneycombBlock;
        boolean isHoneycombBlockRecipe = honeycombCount == 0 && hasHoneycombBlock;
        if (boots.isEmpty() || (!isNormalRecipe && !isHoneycombBlockRecipe)) {
            return java.util.Optional.empty();
        }

        ItemStack result = boots.copy();
        result.setCount(1);
        if (isHoneycombBlockRecipe) {
            WaxedBoots.waxWithHoneycombBlock(result);
        } else {
            WaxedBoots.wax(result);
        }
        return java.util.Optional.of(result);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 3;
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        return createIngredients(false);
    }

    private static NonNullList<Ingredient> createIngredients(boolean useHoneycombBlock) {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(createBootsIngredient());
        if (useHoneycombBlock) {
            ingredients.add(HONEYCOMB_BLOCK);
        } else {
            ingredients.add(HONEYCOMB);
            ingredients.add(HONEYCOMB);
        }
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
        return createResult(false);
    }

    private static ItemStack createResult(boolean useHoneycombBlock) {
        ItemStack result = new ItemStack(Items.DIAMOND_BOOTS);
        if (useHoneycombBlock) {
            WaxedBoots.waxWithHoneycombBlock(result);
        } else {
            WaxedBoots.wax(result);
        }
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