package com.tiomadre.foragersinsight.common.diffuser;

import com.tiomadre.foragersinsight.core.ForagersInsight;
import com.tiomadre.foragersinsight.core.registry.FIItems;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@SuppressWarnings("ALL")
public final class DiffuserScent {
    public static final int STANDARD_DURATION = 1800;
    public static final Vec3 DEFAULT_COLOR = new Vec3(0.8D, 0.8D, 0.8D);

    private static final List<DiffuserScent> ALL = new ArrayList<>();
    private static final Map<ResourceLocation, DiffuserScent> BY_ID = new ConcurrentHashMap<>();

    public static final DiffuserScent ROSEY = register(
            ForagersInsight.rl("rosey"),
            List.of(IngredientCount.of(FIItems.ROSE_PETALS, 3)),
            ForagersInsight.rl("textures/scents/rosey.png"),
            colorFromRgb(0xff007f),
            STANDARD_DURATION,
            "scent.foragersinsight.rosey"
    );

    public static void bootstrap() {
        Objects.requireNonNull(ROSEY, "Default scent not registered");
    }

    private final ResourceLocation id;
    private final List<IngredientCount> ingredients;
    private final ResourceLocation icon;
    private final Vec3 particleColor;
    private final int duration;
    private final int totalItemCount;
    private final String translationKey;
    private final int networkId;

    private DiffuserScent(ResourceLocation id,
                          List<IngredientCount> ingredients,
                          ResourceLocation icon,
                          Vec3 particleColor,
                          int duration,
                          String translationKey,
                          int networkId) {
        this.id = Objects.requireNonNull(id, "id");
        this.ingredients = List.copyOf(ingredients);
        this.icon = Objects.requireNonNull(icon, "icon");
        this.particleColor = particleColor;
        this.duration = duration;
        this.totalItemCount = this.ingredients.stream().mapToInt(IngredientCount::count).sum();
        this.translationKey = Objects.requireNonNull(translationKey, "translationKey");
        this.networkId = networkId;
    }

    private static DiffuserScent register(ResourceLocation id,
                                          List<IngredientCount> ingredients,
                                          ResourceLocation icon,
                                          Vec3 particleColor,
                                          int duration,
                                          String translationKey) {
        int networkId = ALL.size();
        DiffuserScent scent = new DiffuserScent(id, ingredients, icon, particleColor, duration, translationKey, networkId);
        ALL.add(scent);
        BY_ID.put(scent.id, scent);
        return scent;
    }

    public ResourceLocation id() {
        return this.id;
    }

    public List<IngredientCount> ingredients() {
        return this.ingredients;
    }

    public ResourceLocation icon() {
        return this.icon;
    }

    public Vec3 particleColor() {
        return this.particleColor;
    }

    public int duration() {
        return this.duration;
    }

    public static Optional<DiffuserScent> findMatch(List<ItemStack> stacks) {
        for (DiffuserScent scent : ALL) {
            if (scent.matches(stacks)) {
                return Optional.of(scent);
            }
        }
        return Optional.empty();
    }

    public static Optional<DiffuserScent> byId(ResourceLocation id) {
        return Optional.ofNullable(BY_ID.get(id));
    }

    public int networkId() {
        return this.networkId;
    }

    public static Optional<DiffuserScent> byNetworkId(int networkId) {
        if (networkId < 0 || networkId >= ALL.size()) {
            return Optional.empty();
        }
        return Optional.ofNullable(ALL.get(networkId));
    }

    public Component displayName() {
        return Component.translatable(this.translationKey);
    }

    private boolean matches(List<ItemStack> stacks) {
        if (this.ingredients.isEmpty()) {
            return false;
        }
        int[] remaining = new int[this.ingredients.size()];
        for (int i = 0; i < this.ingredients.size(); i++) {
            remaining[i] = this.ingredients.get(i).count();
        }

        int totalItems = 0;
        for (ItemStack stack : stacks) {
            if (stack.isEmpty()) {
                continue;
            }
            totalItems += stack.getCount();
            boolean matched = false;
            for (int i = 0; i < this.ingredients.size(); i++) {
                IngredientCount ingredient = this.ingredients.get(i);
                if (ingredient.ingredient().test(stack)) {
                    remaining[i] -= stack.getCount();
                    if (remaining[i] < 0) {
                        return false;
                    }
                    matched = true;
                    break;
                }
            }
            if (!matched) {
                return false;
            }
        }

        if (totalItems != this.totalItemCount) {
            return false;
        }

        for (int value : remaining) {
            if (value != 0) {
                return false;
            }
        }
        return true;
    }

    private static Vec3 colorFromRgb(int rgb) {
        double r = (rgb >> 16 & 0xFF) / 255.0D;
        double g = (rgb >> 8 & 0xFF) / 255.0D;
        double b = (rgb & 0xFF) / 255.0D;
        return new Vec3(r, g, b);
    }

    public record IngredientCount(Supplier<Ingredient> ingredientSupplier, int count) {
        public Ingredient ingredient() {
            return this.ingredientSupplier.get();
        }

        public static IngredientCount of(Supplier<? extends ItemLike> item, int count) {
            return new IngredientCount(() -> Ingredient.of(item.get()), count);
        }
    }
}