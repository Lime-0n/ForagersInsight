package com.tiomadre.foragersinsight.data.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tiomadre.foragersinsight.core.ForagersInsight;
import com.tiomadre.foragersinsight.core.registry.FIAdvancements;
import com.tiomadre.foragersinsight.data.server.tags.FITags;
import com.teamabnormals.blueprint.common.advancement.modification.AdvancementModifierProvider;
import com.teamabnormals.blueprint.common.advancement.modification.modifiers.CriteriaModifier;
import net.minecraft.advancements.critereon.ConsumeItemTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class FIAdvancementData extends AdvancementModifierProvider {
    private final PackOutput.PathProvider pathProvider;

    public FIAdvancementData(GatherDataEvent event) {
        super(ForagersInsight.MOD_ID, event.getGenerator().getPackOutput(), event.getLookupProvider());
        this.pathProvider = event.getGenerator().getPackOutput()
                .createPathProvider(PackOutput.Target.DATA_PACK, "advancements");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        return CompletableFuture.allOf(super.run(output), this.runAdvancements(output));
    }

    @Override
    protected void registerEntries(HolderLookup.Provider provider) {
        this.balancedDiet();
    }

    // Husbandry
    private void balancedDiet() {
        CriteriaModifier.Builder builder = CriteriaModifier.builder(this.modId);
        AtomicInteger counter = new AtomicInteger(0);
        ForgeRegistries.ITEMS.getValues().stream()
                .filter(item -> ForgeRegistries.ITEMS.getKey(item).getNamespace().equals(ForagersInsight.MOD_ID))
                .filter(Item::isEdible)
                .forEach(item -> builder.addCriterion("%d".formatted(counter.getAndIncrement()),
                        ConsumeItemTrigger.TriggerInstance.usedItem(ItemPredicate.Builder.item().of(item).build())));

        this.entry("husbandry/balanced_diet").selects("husbandry/balanced_diet").addModifier(builder.build());
    }

    // Forager advancement tree
    private CompletableFuture<?> runAdvancements(CachedOutput output) {
        Map<ResourceLocation, JsonObject> advancements = new LinkedHashMap<>();

        advancements.put(FIAdvancements.ROOT, rootAdvancement());
        advancements.put(FIAdvancements.SPRING_CLEANING, springCleaning());
        advancements.put(FIAdvancements.BRUSH_IT_OFF, brushItOff());
        advancements.put(FIAdvancements.RARE_FIND, rareFind());
        advancements.put(FIAdvancements.WILD_FLOWERS, wildFlowers());
        advancements.put(FIAdvancements.GIVING_TREES, givingTrees());
        advancements.put(FIAdvancements.SHEARING_IS_CARING, shearingIsCaring());
        advancements.put(FIAdvancements.SCENTSATIONAL, scentsational());
        advancements.put(FIAdvancements.STINKY_SITUATION, stinkySituation());
        advancements.put(FIAdvancements.TAP_THAT, tapThat());
        advancements.put(FIAdvancements.BIRCH_PLEASE, birchPlease());
        advancements.put(FIAdvancements.STOP_HAMMER_TIME, stopHammerTime());
        advancements.put(FIAdvancements.WILL_IT_CRUSH, willItCrush());

        CompletableFuture<?>[] writes = advancements.entrySet().stream()
                .map(entry -> DataProvider.saveStable(output, entry.getValue(), this.pathProvider.json(entry.getKey())))
                .toArray(CompletableFuture[]::new);
        return CompletableFuture.allOf(writes);
    }

    @Override
    public String getName() {
        return "Forager's Insight Advancements";
    }

    private static JsonObject rootAdvancement() {
        JsonObject advancement = baseDisplay(FIAdvancements.node(FIAdvancements.ROOT),
                "advancements.foragersinsight.adventure.foragers_insight.title",
                "advancements.foragersinsight.adventure.foragers_insight.description");
        advancement.getAsJsonObject("display")
                .addProperty("background", "foragersinsight:textures/gui/advancements/backgrounds/foraging.png");

        JsonObject criteria = new JsonObject();
        criteria.add("has_foragers_insight_item",
                inventoryChangedWithTag("foragersinsight:foragers_insight_items"));
        advancement.add("criteria", criteria);
        advancement.add("requirements", requirements("has_foragers_insight_item"));
        return advancement;
    }

    private static JsonObject brushItOff() {
        return singleCriterionAdvancement(FIAdvancements.BRUSH_IT_OFF, FIAdvancements.SPRING_CLEANING,
                "advancements.foragersinsight.adventure.brush_it_off.title",
                "advancements.foragersinsight.adventure.brush_it_off.description",
                "brush_suspicious_litter", simpleTrigger("foragersinsight:brush_suspicious_litter"));
    }

    private static JsonObject rareFind() {
        return singleCriterionAdvancement(FIAdvancements.RARE_FIND, FIAdvancements.BRUSH_IT_OFF,
                "advancements.foragersinsight.adventure.rare_find.title",
                "advancements.foragersinsight.adventure.rare_find.description",
                "find_blewit_mushroom", simpleTrigger("foragersinsight:find_blewit_mushroom"));
    }

    private static JsonObject wildFlowers() {
        return singleCriterionAdvancement(FIAdvancements.WILD_FLOWERS,
                "advancements.foragersinsight.adventure.wild_flowers.title",
                "advancements.foragersinsight.adventure.wild_flowers.description",
                "forage_wild_flower", inventoryChangedWithTag(tagLocation(FITags.ItemTag.WILD_FLOWER_DROPS)));
    }

    private static JsonObject springCleaning() {
        return singleCriterionAdvancement(FIAdvancements.SPRING_CLEANING,
                "advancements.foragersinsight.adventure.spring_cleaning.title",
                "advancements.foragersinsight.adventure.spring_cleaning.description",
                "pick_up_brush", inventoryChangedWithItem("minecraft:brush"));
    }

    private static JsonObject shearingIsCaring() {
        return singleCriterionAdvancement(FIAdvancements.SHEARING_IS_CARING, FIAdvancements.GIVING_TREES,
                "advancements.foragersinsight.adventure.shear.title",
                "advancements.foragersinsight.adventure.shear.description",
                "shear_bountiful_tree", simpleTrigger("foragersinsight:shear_bountiful_tree"));
    }

    private static JsonObject scentsational() {
        return singleCriterionAdvancement(FIAdvancements.SCENTSATIONAL, FIAdvancements.WILD_FLOWERS,
                "advancements.foragersinsight.adventure.scentsational.title",
                "advancements.foragersinsight.adventure.scentsational.description",
                "scentsational", simpleTrigger("foragersinsight:scentsational"));
    }

    private static JsonObject stinkySituation() {
        return singleCriterionAdvancement(FIAdvancements.STINKY_SITUATION, FIAdvancements.SCENTSATIONAL,
                "advancements.foragersinsight.adventure.stinky_situation.title",
                "advancements.foragersinsight.adventure.stinky_situation.description",
                "stinky_situation", simpleTrigger("foragersinsight:stinky_situation"));
    }

    private static JsonObject tapThat() {
        return singleCriterionAdvancement(FIAdvancements.TAP_THAT,
                "advancements.foragersinsight.adventure.tap_that.title",
                "advancements.foragersinsight.adventure.tap_that.description",
                "tap_that", simpleTrigger("foragersinsight:tap_that"));
    }

    private static JsonObject birchPlease() {
        return singleCriterionAdvancement(FIAdvancements.BIRCH_PLEASE, FIAdvancements.TAP_THAT,
                "advancements.foragersinsight.adventure.birch_please.title",
                "advancements.foragersinsight.adventure.birch_please.description",
                "birch_please", simpleTrigger("foragersinsight:birch_please"));
    }

    private static JsonObject givingTrees() {
        return singleCriterionAdvancement(FIAdvancements.GIVING_TREES,
                "advancements.foragersinsight.adventure.giving_trees.title",
                "advancements.foragersinsight.adventure.giving_trees.description",
                "giving_trees", inventoryChangedWithItems(
                        "foragersinsight:lilac_bloom",
                        "minecraft:apple",
                        "foragersinsight:black_acorn",
                        "foragersinsight:spruce_tips"
                ));
    }

    private static JsonObject stopHammerTime() {
        return singleCriterionAdvancement(FIAdvancements.STOP_HAMMER_TIME,
                "advancements.foragersinsight.adventure.stop_hammer_time.title",
                "advancements.foragersinsight.adventure.stop_hammer_time.description",
                "pick_up_mallet", inventoryChangedWithTag(tagLocation(FITags.ItemTag.MALLETS)));
    }

    private static JsonObject willItCrush() {
        return singleCriterionAdvancement(FIAdvancements.WILL_IT_CRUSH, FIAdvancements.STOP_HAMMER_TIME,
                "advancements.foragersinsight.adventure.will_it_crush.title",
                "advancements.foragersinsight.adventure.will_it_crush.description",
                "will_it_crush", simpleTrigger("foragersinsight:will_it_crush"));
    }


    private static JsonObject singleCriterionAdvancement(ResourceLocation id, String titleKey, String descriptionKey,
                                                         String criterionName, JsonObject criterion) {
        return singleCriterionAdvancement(id, FIAdvancements.ROOT, titleKey, descriptionKey, criterionName, criterion);
    }

    private static JsonObject singleCriterionAdvancement(ResourceLocation id, ResourceLocation parent, String titleKey,
                                                         String descriptionKey, String criterionName,
                                                         JsonObject criterion) {
        JsonObject advancement = childAdvancement(id, parent, titleKey, descriptionKey);
        JsonObject criteria = new JsonObject();
        criteria.add(criterionName, criterion);
        advancement.add("criteria", criteria);
        advancement.add("requirements", requirements(criterionName));
        return advancement;
    }

    private static JsonObject childAdvancement(ResourceLocation id, String titleKey, String descriptionKey) {
        return childAdvancement(id, FIAdvancements.ROOT, titleKey, descriptionKey);
    }

    private static JsonObject childAdvancement(ResourceLocation id, ResourceLocation parent, String titleKey,
                                               String descriptionKey) {
        JsonObject advancement = baseDisplay(FIAdvancements.node(id), titleKey, descriptionKey);
        advancement.addProperty("parent", parent.toString());
        JsonObject display = advancement.getAsJsonObject("display");
        FIAdvancements.Node node = FIAdvancements.node(id);
        FIAdvancements.Node parentNode = FIAdvancements.node(parent);
        display.addProperty("x", node.x() - parentNode.x());
        display.addProperty("y", node.y() - parentNode.y());
        return advancement;
    }

    private static JsonObject baseDisplay(FIAdvancements.Node node, String titleKey, String descriptionKey) {
        JsonObject advancement = new JsonObject();
        JsonObject display = new JsonObject();
        JsonObject displayIcon = new JsonObject();
        displayIcon.addProperty("item", node.icon().toString());
        display.add("icon", displayIcon);
        display.add("title", translation(titleKey));
        display.add("description", translation(descriptionKey));
        display.addProperty("frame", node.frame().id);
        display.addProperty("show_toast", true);
        display.addProperty("announce_to_chat", true);
        display.addProperty("hidden", false);
        display.addProperty("x", node.x());
        display.addProperty("y", node.y());
        advancement.add("display", display);
        return advancement;
    }

    private static JsonObject inventoryChangedWithItems(String... itemIds) {
        JsonArray itemIdsJson = new JsonArray();
        for (String itemId : itemIds) {
            itemIdsJson.add(itemId);
        }

        JsonObject item = new JsonObject();
        item.add("items", itemIdsJson);
        return inventoryChanged(item);
    }

    private static JsonObject translation(String key) {
        JsonObject translate = new JsonObject();
        translate.addProperty("translate", key);
        return translate;
    }

    private static JsonObject inventoryChangedWithTag(String tag) {
        JsonObject item = new JsonObject();
        item.addProperty("tag", tag);
        return inventoryChanged(item);
    }

    private static JsonObject simpleTrigger(String triggerId) {
        JsonObject criterion = new JsonObject();
        criterion.addProperty("trigger", triggerId);
        return criterion;
    }

    private static JsonObject inventoryChangedWithItem(String itemId) {
        JsonArray itemIds = new JsonArray();
        itemIds.add(itemId);

        JsonObject item = new JsonObject();
        item.add("items", itemIds);
        return inventoryChanged(item);
    }

    private static JsonObject inventoryChanged(JsonObject item) {
        JsonObject criterion = new JsonObject();
        criterion.addProperty("trigger", "minecraft:inventory_changed");

        JsonArray items = new JsonArray();
        items.add(item);

        JsonObject conditions = new JsonObject();
        conditions.add("items", items);
        criterion.add("conditions", conditions);
        return criterion;
    }

    private static JsonArray requirements(String criterion) {
        JsonArray requirements = new JsonArray();
        JsonArray group = new JsonArray();
        group.add(criterion);
        requirements.add(group);
        return requirements;
    }

    private static String tagLocation(net.minecraft.tags.TagKey<?> tag) {
        return tag.location().toString();
    }
}
