package com.tiomadre.foragersinsight.data.server.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tiomadre.foragersinsight.core.registry.FIItems;
import com.tiomadre.foragersinsight.core.registry.FIMobEffects;
import com.tiomadre.foragersinsight.data.server.tags.FITags;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import vectorwing.farmersdelight.client.recipebook.CookingPotRecipeBookTab;
import vectorwing.farmersdelight.common.registry.ModRecipeSerializers;
import vectorwing.farmersdelight.common.registry.ModItems;
import vectorwing.farmersdelight.common.tag.ForgeTags;
import vectorwing.farmersdelight.data.builder.CookingPotRecipeBuilder;
import java.util.function.Consumer;
import static com.tiomadre.foragersinsight.core.registry.FIItems.*;

public class FICookingRecipes {
    public static final int FAST_COOKING = 100;      // 5 seconds
    public static final int NORMAL_COOKING = 200;// 10 seconds
    public static final int MODERATE_COOKING = 300;// 15 seconds
    public static final int SLOW_COOKING = 400;      // 20 seconds

    public static final float SMALL_EXP = 0.35F;
    public static final float MEDIUM_EXP = 1.0F;
    public static final float MODERATE_EXP = 1.3F;
    public static final float LARGE_EXP = 2.0F;

    public static void buildRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
        //Comfort
        CookingPotRecipeBuilder.cookingPotRecipe(BLEWIT_BITES.get(), 2, NORMAL_COOKING, MEDIUM_EXP)
                .addIngredient(BLEWIT_MUSHROOM.get())
                .addIngredient(FITags.ItemTag.WHEAT)
                .addIngredient(FITags.ItemTag.BLEWIT_STUFFING)
                .addIngredient(FITags.ItemTag.WHEAT)
                .addIngredient(BLEWIT_MUSHROOM.get())
                .unlockedByAnyIngredient(BLEWIT_MUSHROOM.get())
                .setRecipeBookTab(CookingPotRecipeBookTab.MEALS)
                .build(consumer);
        CookingPotRecipeBuilder.cookingPotRecipe(CARROT_POPPY_CHOWDER.get(), 1, NORMAL_COOKING, MEDIUM_EXP)
                .addIngredient(FITags.ItemTag.POPPY_SEEDS)
                .addIngredient(FITags.ItemTag.POPPY_SEEDS)
                .addIngredient(Items.CARROT)
                .addIngredient(SEED_BUTTER.get())
                .unlockedByAnyIngredient(POPPY_SEEDS.get(), Items.CARROT)
                .setRecipeBookTab(CookingPotRecipeBookTab.MEALS)
                .build(consumer);
        CookingPotRecipeBuilder.cookingPotRecipe(COD_AND_PUMPKIN_STEW.get(), 1, NORMAL_COOKING, MEDIUM_EXP)
                .addIngredient(ForgeTags.RAW_FISHES_COD)
                .addIngredient(ModItems.PUMPKIN_SLICE.get())
                .addIngredient(SEED_BUTTER.get())
                .addIngredient(ModItems.TOMATO.get())
                .unlockedByAnyIngredient(ModItems.PUMPKIN_SLICE.get(), Items.COD)
                .setRecipeBookTab(CookingPotRecipeBookTab.MEALS)
                .build(consumer);
        CookingPotRecipeBuilder.cookingPotRecipe(ROSE_HIP_SOUP.get(), 1, NORMAL_COOKING, MEDIUM_EXP)
                .addIngredient(ROSE_HIP.get())
                .addIngredient(ROSE_HIP.get())
                .addIngredient(FITags.ItemTag.MILK_BOTTLE)
                .unlockedByAnyIngredient(ROSE_HIP.get())
                .setRecipeBookTab(CookingPotRecipeBookTab.MEALS)
                .build(consumer);
        CookingPotRecipeBuilder.cookingPotRecipe(STEAMY_KELP_RICE.get(), 1, NORMAL_COOKING, MEDIUM_EXP)
                .addIngredient(ModItems.RICE.get())
                .addIngredient(Items.DRIED_KELP)
                .addIngredient(Items.DRIED_KELP)
                .unlockedByAnyIngredient(Items.KELP, ModItems.RICE.get())
                .setRecipeBookTab(CookingPotRecipeBookTab.MEALS)
                .build(consumer);
        CookingPotRecipeBuilder.cookingPotRecipe(HEARTY_SPRUCE_PILAF.get(), 1, NORMAL_COOKING, MEDIUM_EXP)
                .addIngredient(GREEN_SAUCE.get())
                .addIngredient(ModItems.RICE.get())
                .addIngredient(FITags.ItemTag.ACORN)
                .addIngredient(FITags.ItemTag.ACORN)
                .addIngredient(ModItems.PUMPKIN_SLICE.get())
                .addIngredient(SPRUCE_TIPS.get())
                .unlockedByAnyIngredient(SPRUCE_TIPS.get())
                .setRecipeBookTab(CookingPotRecipeBookTab.MEALS)
                .build(consumer);
        CookingPotRecipeBuilder.cookingPotRecipe(BLACK_FOREST_MUFFIN.get(), 2, NORMAL_COOKING, MEDIUM_EXP)
                .addIngredient(ACORN_DOUGH.get())
                .addIngredient(FITags.ItemTag.COCOA)
                .addIngredient(FITags.ItemTag.COCOA)
                .addIngredient(Items.SUGAR)
                .unlockedByAnyIngredient(BLACK_ACORN.get(), Items.COCOA_BEANS)
                .setRecipeBookTab(CookingPotRecipeBookTab.MEALS)
                .build(consumer);
        CookingPotRecipeBuilder.cookingPotRecipe(RED_VELVET_CUPCAKE.get(), 2, NORMAL_COOKING, MEDIUM_EXP)
                .addIngredient(ModItems.WHEAT_DOUGH.get())
                .addIngredient(Items.BEETROOT)
                .addIngredient(FITags.ItemTag.COCOA)
                .addIngredient(Items.SUGAR)
                .unlockedByAnyIngredient(Items.BEETROOT, Items.COCOA_BEANS)
                .setRecipeBookTab(CookingPotRecipeBookTab.MEALS)
                .build(consumer);
        CookingPotRecipeBuilder.cookingPotRecipe(POPPY_SEED_BAGEL.get(), 2, NORMAL_COOKING, MEDIUM_EXP)
                .addIngredient(FITags.ItemTag.DOUGH)
                .addIngredient(FITags.ItemTag.POPPY_SEEDS)
                .addIngredient(FITags.ItemTag.POPPY_SEEDS)
                .addIngredient(FITags.ItemTag.POPPY_SEEDS)
                .unlockedByAnyIngredient(POPPY_SEEDS.get(), Items.WHEAT)
                .setRecipeBookTab(CookingPotRecipeBookTab.MEALS)
                .build(consumer);
        CookingPotRecipeBuilder.cookingPotRecipe(LILAC_TEACAKE.get(), 2, NORMAL_COOKING, MEDIUM_EXP)
                .addIngredient(ModItems.WHEAT_DOUGH.get())
                .addIngredient(FITags.ItemTag.LILAC)
                .addIngredient(FITags.ItemTag.LILAC)
                .addIngredient(Items.SUGAR)
                .unlockedByAnyIngredient(Items.BEETROOT, Items.COCOA_BEANS)
                .setRecipeBookTab(CookingPotRecipeBookTab.MEALS)
                .build(consumer);
        //Nourishment
        CookingPotRecipeBuilder.cookingPotRecipe(ACORN_NOODLES.get(), 1, NORMAL_COOKING, MEDIUM_EXP)
                .addIngredient(ACORN_DOUGH.get())
                .addIngredient(FITags.ItemTag.ACORN)
                .addIngredient(FITags.ItemTag.ACORN)
                .addIngredient(ForgeTags.MILK)
                .unlockedByAnyIngredient(BLACK_ACORN.get())
                .setRecipeBookTab(CookingPotRecipeBookTab.MEALS)
                .build(consumer);
        CookingPotRecipeBuilder.cookingPotRecipe(GLAZED_PORKCHOP_AND_ACORN_GRITS.get(), 1, NORMAL_COOKING, MEDIUM_EXP)
                .addIngredient(Ingredient.of(BIRCH_SYRUP_BOTTLE.get(), BIRCH_SYRUP_BUCKET.get()))
                .addIngredient(Items.PORKCHOP)
                .addIngredient(FITags.ItemTag.ACORN)
                .addIngredient(FITags.ItemTag.ACORN)
                .addIngredient(ForgeTags.MILK)
                .addIngredient(SPRUCE_TIPS.get())
                .unlockedByAnyIngredient(BIRCH_SYRUP_BOTTLE.get(), BIRCH_SYRUP_BUCKET.get())
                .setRecipeBookTab(CookingPotRecipeBookTab.MEALS)
                .build(consumer);
        CookingPotRecipeBuilder.cookingPotRecipe(ROSE_ROASTED_ROOTS.get(), 1, NORMAL_COOKING, MEDIUM_EXP)
                .addIngredient(DANDELION_ROOT.get())
                .addIngredient(Items.BEETROOT)
                .addIngredient(ModItems.RICE.get())
                .addIngredient(Ingredient.of(DANDELION_ROOT.get(), Items.BEETROOT))
                .addIngredient(FIItems.ROSE_HIP.get())
                .unlockedByAnyIngredient(FIItems.ROSE_HIP.get(), DANDELION_ROOT.get())
                .setRecipeBookTab(CookingPotRecipeBookTab.MEALS)
                .build(consumer);
        CookingPotRecipeBuilder.cookingPotRecipe(SEASIDE_SIZZLER.get(), 1, NORMAL_COOKING, MEDIUM_EXP)
                .addIngredient(Items.TROPICAL_FISH)
                .addIngredient(Items.BEETROOT)
                .addIngredient(Items.KELP)
                .addIngredient(Items.BEETROOT)
                .addIngredient(ModItems.RICE.get())
                .unlockedByAnyIngredient(Items.KELP, Items.BEETROOT)
                .setRecipeBookTab(CookingPotRecipeBookTab.MEALS)
                .build(consumer);
        CookingPotRecipeBuilder.cookingPotRecipe(SYRUP_TOAST_STACKS.get(), 1, NORMAL_COOKING, MEDIUM_EXP)
                .addIngredient(Ingredient.of(BIRCH_SYRUP_BOTTLE.get(), BIRCH_SYRUP_BUCKET.get()))
                .addIngredient(Items.BREAD)
                .addIngredient(ForgeTags.EGGS)
                .addIngredient(FITags.ItemTag.MILK_BOTTLE)
                .unlockedByAnyIngredient(BIRCH_SYRUP_BOTTLE.get(), BIRCH_SYRUP_BUCKET.get())
                .setRecipeBookTab(CookingPotRecipeBookTab.MEALS)
                .build(consumer);
        CookingPotRecipeBuilder.cookingPotRecipe(WOODLAND_PASTA.get(), 1, NORMAL_COOKING, MEDIUM_EXP)
                .addIngredient(GREEN_SAUCE.get())
                .addIngredient(ModItems.RAW_PASTA.get())
                .addIngredient(Ingredient.of(FITags.ItemTag.MUSHROOM))
                .addIngredient(Ingredient.of(FITags.ItemTag.MUSHROOM))
                .addIngredient(Ingredient.of(FITags.ItemTag.MUSHROOM))
                .unlockedByAnyIngredient(SPRUCE_TIPS.get(), BLEWIT_MUSHROOM.get())
                .setRecipeBookTab(CookingPotRecipeBookTab.MEALS)
                .build(consumer);
        CookingPotRecipeBuilder.cookingPotRecipe(TART_WHEAT_PILAF.get(), 1, NORMAL_COOKING, MEDIUM_EXP)
                .addIngredient(Items.WHEAT_SEEDS)
                .addIngredient(ROSELLE_CALYX.get())
                .addIngredient(Items.WHEAT_SEEDS)
                .addIngredient(ROSELLE_CALYX.get())
                .addIngredient(Items.WHEAT_SEEDS)
                .unlockedByAnyIngredient(ROSELLE_CALYX.get())
                .setRecipeBookTab(CookingPotRecipeBookTab.MEALS)
                .build(consumer);
        CookingPotRecipeBuilder.cookingPotRecipe(SAVORY_PASTA_ROLL.get(), 1, NORMAL_COOKING, MEDIUM_EXP)
                .addIngredient(ModItems.RAW_PASTA.get())
                .addIngredient(SEED_BUTTER.get())
                .addIngredient(ModItems.TOMATO_SAUCE.get())
                .addIngredient(FITags.ItemTag.MILK_BOTTLE)
                .unlockedByAnyIngredient(SEED_BUTTER.get())
                .setRecipeBookTab(CookingPotRecipeBookTab.MEALS)
                .build(consumer);
        //Drinks
        CookingPotRecipeBuilder.cookingPotRecipe(ROSE_CORDIAL.get(), 1, NORMAL_COOKING, MEDIUM_EXP)
                .addIngredient(ROSE_PETALS.get())
                .addIngredient(Items.SUGAR)
                .addIngredient(FIItems.ROSE_HIP.get())
                .unlockedByAnyIngredient(ROSE_PETALS.get(), FIItems.ROSE_HIP.get())
                .setRecipeBookTab(CookingPotRecipeBookTab.DRINKS)
                .build(consumer);
        CookingPotRecipeBuilder.cookingPotRecipe(GLOWING_CARROT_JUICE.get(), 1, NORMAL_COOKING, MEDIUM_EXP)
                .addIngredient(Items.GLOW_BERRIES)
                .addIngredient(Items.SUGAR)
                .addIngredient(Items.CARROT)
                .unlockedByAnyIngredient(Items.CARROT, Items.GLOW_BERRIES)
                .setRecipeBookTab(CookingPotRecipeBookTab.DRINKS)
                .build(consumer);
        //Medicinal
        CookingPotRecipeBuilder.cookingPotRecipe(DANDELION_ROOT_TEA.get(), 1, NORMAL_COOKING, MEDIUM_EXP)
                .addIngredient(DANDELION_ROOT.get())
                .addIngredient(DANDELION_ROOT.get())
                .addIngredient(ForgeTags.MILK_BOTTLE)
                .unlockedByAnyIngredient(DANDELION_ROOT.get())
                .setRecipeBookTab(CookingPotRecipeBookTab.DRINKS)
                .build(consumer);
        CookingPotRecipeBuilder.cookingPotRecipe(FOREST_ELIXIR.get(), 1, NORMAL_COOKING, MEDIUM_EXP)
                .addIngredient(SPRUCE_TIPS.get())
                .addIngredient(SPRUCE_TIPS.get())
                .addIngredient(Ingredient.of(BIRCH_SYRUP_BOTTLE.get(), BIRCH_SYRUP_BUCKET.get()))
                .unlockedByAnyIngredient(SPRUCE_TIPS.get(), BIRCH_SYRUP_BUCKET.get(), BIRCH_SYRUP_BOTTLE.get())
                .setRecipeBookTab(CookingPotRecipeBookTab.DRINKS)
                .build(consumer);
        CookingPotRecipeBuilder.cookingPotRecipe(ROSELLE_JUICE.get(), 1, NORMAL_COOKING, MEDIUM_EXP)
                .addIngredient(ROSELLE_CALYX.get())
                .addIngredient(ROSELLE_CALYX.get())
                .addIngredient(Items.HONEY_BOTTLE)
                .unlockedByAnyIngredient(ROSELLE_CALYX.get())
                .setRecipeBookTab(CookingPotRecipeBookTab.DRINKS)
                .build(consumer);
        //Seed Milk
        CookingPotRecipeBuilder.cookingPotRecipe(SEED_MILK_BUCKET.get(), 1, MODERATE_COOKING, MODERATE_EXP, Items.BUCKET)
                .addIngredient(ForgeTags.SEEDS)
                .addIngredient(ForgeTags.SEEDS)
                .addIngredient(ForgeTags.SEEDS)
                .addIngredient(ForgeTags.SEEDS)
                .unlockedByAnyIngredient(Items.WHEAT_SEEDS)
                .setRecipeBookTab(CookingPotRecipeBookTab.DRINKS)
                .build(consumer);
        //Other
        buildAuspiciousStewRecipe(consumer, ROSE_PETALS.get(), MobEffects.REGENERATION, "auspicious_stew_from_rose_petals");
        buildAuspiciousStewRecipe(consumer, ROSELLE_PETALS.get(), FIMobEffects.BLOOM.get(), "auspicious_stew_from_roselle_petals");
        buildAuspiciousStewRecipe(consumer, SPRUCE_TIPS.get(), MobEffects.ABSORPTION, "auspicious_stew_from_spruce_tips");
        buildAuspiciousStewRecipe(consumer, LILAC_BLOOM.get(), FIMobEffects.MEDICINAL.get(), "auspicious_stew_from_lilac_bloom");

        CookingPotRecipeBuilder.cookingPotRecipe(CANDIED_CALYCES.get(), 2, NORMAL_COOKING, MEDIUM_EXP)
                .addIngredient(Items.SUGAR)
                .addIngredient(ROSELLE_CALYX.get())
                .addIngredient(ROSELLE_CALYX.get())
                .addIngredient(Items.SUGAR)
                .unlockedByAnyIngredient(ROSELLE_CALYX.get())
                .setRecipeBookTab(CookingPotRecipeBookTab.MEALS)
                .build(consumer);
        CookingPotRecipeBuilder.cookingPotRecipe(APPLE_DIPPERS.get(), 1, NORMAL_COOKING, MEDIUM_EXP)
                .addIngredient(Items.SUGAR)
                .addIngredient(FITags.ItemTag.APPLE)
                .addIngredient(Ingredient.of(Items.HONEY_BOTTLE, BIRCH_SYRUP_BOTTLE.get()))
                .unlockedByAnyIngredient(Items.APPLE)
                .setRecipeBookTab(CookingPotRecipeBookTab.MEALS)
                .build(consumer);
        CookingPotRecipeBuilder.cookingPotRecipe(DANDELION_FRIES.get(), 1, FAST_COOKING, SMALL_EXP)
                .addIngredient(DANDELION_ROOT.get())
                .addIngredient(DANDELION_ROOT.get())
                .unlockedByAnyIngredient(DANDELION_ROOT.get())
                .setRecipeBookTab(CookingPotRecipeBookTab.MEALS)
                .build(consumer);
        CookingPotRecipeBuilder.cookingPotRecipe(GREEN_SAUCE.get(), 1, NORMAL_COOKING, MEDIUM_EXP)
                .addIngredient(SPRUCE_TIPS.get())
                .addIngredient(SPRUCE_TIPS.get())
                .addIngredient(SPRUCE_TIPS.get())
                .unlockedByAnyIngredient(SPRUCE_TIPS.get())
                .setRecipeBookTab(CookingPotRecipeBookTab.MISC)
                .build(consumer);
        CookingPotRecipeBuilder.cookingPotRecipe(SEED_BUTTER.get(), 1, NORMAL_COOKING, MEDIUM_EXP)
                .addIngredient(ForgeTags.SEEDS)
                .addIngredient(ForgeTags.SEEDS)
                .addIngredient(ForgeTags.SEEDS)
                .unlockedByAnyIngredient(Items.WHEAT_SEEDS)
                .setRecipeBookTab(CookingPotRecipeBookTab.MISC)
                .build(consumer);
        CookingPotRecipeBuilder.cookingPotRecipe(Items.SLIME_BALL, 4, MODERATE_COOKING, MEDIUM_EXP)
                .addIngredient(Ingredient.of(BIRCH_SAP_BOTTLE.get(), BIRCH_SAP_BUCKET.get()))
                .addIngredient(Items.KELP)
                .addIngredient(Items.KELP)
                .addIngredient(Ingredient.of(Items.BONE, Items.BONE_MEAL))
                .unlockedByAnyIngredient(BIRCH_SAP_BUCKET.get(), BIRCH_SAP_BOTTLE.get())
                .setRecipeBookTab(CookingPotRecipeBookTab.MISC)
                .build(consumer);

        // Override FD Recipes
        //Hot Cocoa
        net.minecraftforge.common.crafting.ConditionalRecipe.builder()
                .addCondition(new net.minecraftforge.common.crafting.conditions.ModLoadedCondition("farmersdelight"))
                .addRecipe(r -> CookingPotRecipeBuilder.cookingPotRecipe(ModItems.HOT_COCOA.get(), 1, NORMAL_COOKING, MEDIUM_EXP)
                        .addIngredient(ForgeTags.MILK)
                        .addIngredient(Items.SUGAR)
                        .addIngredient(FITags.ItemTag.COCOA)
                        .addIngredient(FITags.ItemTag.COCOA)
                        .unlockedByAnyIngredient(Items.COCOA_BEANS)
                        .setRecipeBookTab(CookingPotRecipeBookTab.DRINKS)
                        .build(r, new ResourceLocation("farmersdelight", "hot_cocoa")))
                .build(consumer, new ResourceLocation("farmersdelight", "hot_cocoa__override"));
        //Apple Cider
        net.minecraftforge.common.crafting.ConditionalRecipe.builder()
                .addCondition(new net.minecraftforge.common.crafting.conditions.ModLoadedCondition("farmersdelight"))
                .addRecipe(r -> CookingPotRecipeBuilder.cookingPotRecipe(ModItems.APPLE_CIDER.get(), 1, NORMAL_COOKING, MEDIUM_EXP)
                        .addIngredient(FITags.ItemTag.APPLE)
                        .addIngredient(FITags.ItemTag.APPLE)
                        .addIngredient(Items.SUGAR)
                        .unlockedByAnyIngredient(Items.APPLE)
                        .setRecipeBookTab(CookingPotRecipeBookTab.DRINKS)
                        .build(r, new ResourceLocation("farmersdelight", "apple_cider")))
                .build(consumer, new ResourceLocation("farmersdelight", "apple_cider__override"));

    }

    private static void buildAuspiciousStewRecipe(Consumer<FinishedRecipe> consumer, ItemLike flower, MobEffect effect, String recipeName) {
        ItemStack stew = createAuspiciousStew(effect);
        NonNullList<Ingredient> ingredients = NonNullList.of(
                Ingredient.EMPTY,
                Ingredient.of(BLEWIT_MUSHROOM.get()),
                Ingredient.of(BLEWIT_MUSHROOM.get()),
                Ingredient.of(flower)
        );
        ResourceLocation id = new ResourceLocation("farmersdelight", "cooking/" + recipeName);
        JsonObject advancement = buildCookingAdvancement(id, BLEWIT_MUSHROOM.get(), flower);
        ResourceLocation advancementId = new ResourceLocation("farmersdelight", "recipes/cooking/" + recipeName);
        consumer.accept(new CookingPotRecipeWithNbt(id, CookingPotRecipeBookTab.MEALS, ingredients, stew, NORMAL_COOKING, MEDIUM_EXP, advancement, advancementId));
    }

    private static ItemStack createAuspiciousStew(MobEffect effect) {
        ItemStack stew = new ItemStack(AUSPICIOUS_STEW.get());
        CompoundTag tag = stew.getOrCreateTag();
        tag.putString("AuspiciousEffect", BuiltInRegistries.MOB_EFFECT.getKey(effect).toString());
        return stew;
    }

    private static JsonObject buildCookingAdvancement(ResourceLocation recipeId, ItemLike... ingredients) {
        JsonObject advancement = new JsonObject();
        advancement.addProperty("parent", "minecraft:recipes/root");
        JsonObject criteria = new JsonObject();
        JsonObject hasAnyIngredient = new JsonObject();
        JsonObject conditions = new JsonObject();
        JsonArray items = new JsonArray();
        JsonObject itemEntry = new JsonObject();
        JsonArray itemIds = new JsonArray();
        for (ItemLike ingredient : ingredients) {
            itemIds.add(BuiltInRegistries.ITEM.getKey(ingredient.asItem()).toString());
        }
        itemEntry.add("items", itemIds);
        items.add(itemEntry);
        conditions.add("items", items);
        hasAnyIngredient.add("conditions", conditions);
        hasAnyIngredient.addProperty("trigger", "minecraft:inventory_changed");
        criteria.add("has_any_ingredient", hasAnyIngredient);
        JsonObject hasRecipe = new JsonObject();
        JsonObject recipeConditions = new JsonObject();
        recipeConditions.addProperty("recipe", recipeId.toString());
        hasRecipe.add("conditions", recipeConditions);
        hasRecipe.addProperty("trigger", "minecraft:recipe_unlocked");
        criteria.add("has_the_recipe", hasRecipe);
        advancement.add("criteria", criteria);
        JsonArray requirements = new JsonArray();
        JsonArray requirement = new JsonArray();
        requirement.add("has_any_ingredient");
        requirement.add("has_the_recipe");
        requirements.add(requirement);
        advancement.add("requirements", requirements);
        JsonObject rewards = new JsonObject();
        JsonArray recipes = new JsonArray();
        recipes.add(recipeId.toString());
        rewards.add("recipes", recipes);
        advancement.add("rewards", rewards);
        advancement.addProperty("sends_telemetry_event", true);
        return advancement;
    }

    private record CookingPotRecipeWithNbt(ResourceLocation id, CookingPotRecipeBookTab tab, NonNullList<Ingredient> ingredients, ItemStack result, int cookingTime,
    float experience, JsonObject advancement, ResourceLocation advancementId) implements FinishedRecipe {

        @Override
            public void serializeRecipeData(JsonObject json) {
                if (tab != null) {
                    json.addProperty("recipe_book_tab", tab.toString());
                }
                JsonArray ingredientArray = new JsonArray();
                for (Ingredient ingredient : ingredients) {
                    ingredientArray.add(ingredient.toJson());
                }
                json.add("ingredients", ingredientArray);
                JsonObject resultObject = new JsonObject();
                resultObject.addProperty("item", BuiltInRegistries.ITEM.getKey(result.getItem()).toString());
                if (result.getCount() > 1) {
                    resultObject.addProperty("count", result.getCount());
                }
                if (result.hasTag()) {
                    resultObject.addProperty("nbt", result.getTag().toString());
                }
                json.add("result", resultObject);
                if (experience > 0) {
                    json.addProperty("experience", experience);
                }
                json.addProperty("cookingtime", cookingTime);
            }

            @Override
            public ResourceLocation getId() {
                return id;
            }

            @Override
            public net.minecraft.world.item.crafting.RecipeSerializer<?> getType() {
                return ModRecipeSerializers.COOKING.get();
            }

            @Override
            public JsonObject serializeAdvancement() {
                return advancement;
            }

            @Override
            public ResourceLocation getAdvancementId() {
                return advancementId;
            }

        }
}
