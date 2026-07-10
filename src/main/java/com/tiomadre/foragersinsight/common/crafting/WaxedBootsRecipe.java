package com.tiomadre.foragersinsight.common.crafting;

import com.tiomadre.foragersinsight.core.other.WaxedBoots;
import com.tiomadre.foragersinsight.core.registry.FIRecipeSerializers;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class WaxedBootsRecipe extends CustomRecipe {
    private static final Ingredient HONEYCOMB = Ingredient.of(Items.HONEYCOMB);

    public WaxedBootsRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
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
        if (container.getWidth() < 3 || container.getHeight() < 2) {
            return java.util.Optional.empty();
        }

        for (int y = 0; y <= container.getHeight() - 2; y++) {
            for (int x = 0; x <= container.getWidth() - 3; x++) {
                ItemStack topHoneycomb = container.getItem(x + 1 + y * container.getWidth());
                ItemStack leftHoneycomb = container.getItem(x + (y + 1) * container.getWidth());
                ItemStack boots = container.getItem(x + 1 + (y + 1) * container.getWidth());
                ItemStack rightHoneycomb = container.getItem(x + 2 + (y + 1) * container.getWidth());

                if (!HONEYCOMB.test(topHoneycomb) || !HONEYCOMB.test(leftHoneycomb) || !WaxedBoots.isBoots(boots)
                        || !HONEYCOMB.test(rightHoneycomb)) {
                    continue;
                }

                if (hasUnexpectedItems(container, x, y)) {
                    continue;
                }

                ItemStack result = boots.copy();
                result.setCount(1);
                WaxedBoots.wax(result);
                return java.util.Optional.of(result);
            }
        }
        return java.util.Optional.empty();
    }

    private boolean hasUnexpectedItems(CraftingContainer container, int recipeX, int recipeY) {
        for (int y = 0; y < container.getHeight(); y++) {
            for (int x = 0; x < container.getWidth(); x++) {
                boolean recipeSlot = (x == recipeX + 1 && y == recipeY)
                        || (x == recipeX && y == recipeY + 1)
                        || (x == recipeX + 1 && y == recipeY + 1)
                        || (x == recipeX + 2 && y == recipeY + 1);
                if (!recipeSlot && !container.getItem(x + y * container.getWidth()).isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 3 && height >= 2;
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