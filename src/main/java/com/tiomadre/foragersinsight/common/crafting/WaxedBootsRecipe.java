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

        if (boots.isEmpty() || honeycombCount < 1 || honeycombCount > WaxedBoots.MAX_WAXED_LEVEL) {
            return java.util.Optional.empty();
        }

        ItemStack result = boots.copy();
        result.setCount(1);
        WaxedBoots.wax(result, honeycombCount);
        return java.util.Optional.of(result);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
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