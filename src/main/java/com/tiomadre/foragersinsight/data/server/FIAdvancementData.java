package com.tiomadre.foragersinsight.data.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tiomadre.foragersinsight.core.registry.FIAdvancements;
import com.tiomadre.foragersinsight.data.server.tags.FITags;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.data.event.GatherDataEvent;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class FIAdvancementData implements DataProvider {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final PackOutput.PathProvider pathProvider;

    public FIAdvancementData(GatherDataEvent event) {
        this.pathProvider = event.getGenerator().getPackOutput()
                .createPathProvider(PackOutput.Target.DATA_PACK, "advancements");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        Map<ResourceLocation, JsonObject> advancements = new LinkedHashMap<>();

        advancements.put(FIAdvancements.ROOT, rootAdvancement());
        advancements.put(FIAdvancements.BRUSH_IT_OFF, brushItOff());
        advancements.put(FIAdvancements.RARE_FIND, rareFind());
        advancements.put(FIAdvancements.WILD_FLOWERS, wildFlowers());
        advancements.put(FIAdvancements.SHEARING_IS_CARING, shearingIsCaring());
        advancements.put(FIAdvancements.SCENTSATIONAL, scentsational());
        advancements.put(FIAdvancements.STINKY_SITUATION, stinkySituation());
        advancements.put(FIAdvancements.TAP_THAT, tapThat());
        advancements.put(FIAdvancements.BIRCH_PLEASE, birchPlease());
        advancements.put(FIAdvancements.GIVING_TREES, givingTrees());

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
        JsonObject advancement = baseDisplay(FIAdvancements.ROOT_ICON,
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
        JsonObject advancement = childAdvancement(FIAdvancements.BRUSH_IT_OFF_ICON,
                "advancements.foragersinsight.adventure.brush_it_off.title",
                "advancements.foragersinsight.adventure.brush_it_off.description");
        JsonObject criteria = new JsonObject();
        criteria.add("brush_suspicious_litter", simpleTrigger("foragersinsight:brush_suspicious_litter"));
        advancement.add("criteria", criteria);
        advancement.add("requirements", requirements("brush_suspicious_litter"));
        return advancement;
    }
    private static JsonObject rareFind() {
        JsonObject advancement = childAdvancement(FIAdvancements.BRUSH_IT_OFF, FIAdvancements.RARE_FIND_ICON,
                "advancements.foragersinsight.adventure.rare_find.title",
                "advancements.foragersinsight.adventure.rare_find.description");
        JsonObject criteria = new JsonObject();
        criteria.add("find_blewit_mushroom", simpleTrigger("foragersinsight:find_blewit_mushroom"));
        advancement.add("criteria", criteria);
        advancement.add("requirements", requirements("find_blewit_mushroom"));
        return advancement;
    }

    private static JsonObject wildFlowers() {
        JsonObject advancement = childAdvancement(FIAdvancements.WILD_FLOWERS_ICON,
                "advancements.foragersinsight.adventure.wild_flowers.title",
                "advancements.foragersinsight.adventure.wild_flowers.description");
        JsonObject criteria = new JsonObject();
        criteria.add("forage_wild_flower", inventoryChangedWithTag(tagLocation(FITags.ItemTag.WILD_FLOWER_DROPS)));
        advancement.add("criteria", criteria);
        advancement.add("requirements", requirements("forage_wild_flower"));
        return advancement;
    }

    private static JsonObject shearingIsCaring() {
        JsonObject advancement = childAdvancement(FIAdvancements.SHEARING_IS_CARING_ICON,
                "advancements.foragersinsight.adventure.shear.title",
                "advancements.foragersinsight.adventure.shear.description");
        JsonObject criteria = new JsonObject();
        criteria.add("snip_a_crop", inventoryChangedWithItem("foragersinsight:spruce_tips"));
        advancement.add("criteria", criteria);
        advancement.add("requirements", requirements("snip_a_crop"));
        return advancement;
    }
    private static JsonObject scentsational() {
        JsonObject advancement = childAdvancement(FIAdvancements.SCENTSATIONAL_ICON,
                "advancements.foragersinsight.adventure.scentsational.title",
                "advancements.foragersinsight.adventure.scentsational.description");
        JsonObject criteria = new JsonObject();
        criteria.add("scentsational", simpleTrigger("foragersinsight:scentsational"));
        advancement.add("criteria", criteria);
        advancement.add("requirements", requirements("scentsational"));
        return advancement;
    }

    private static JsonObject stinkySituation() {
        JsonObject advancement = childAdvancement(FIAdvancements.STINKY_SITUATION_ICON,
                "advancements.foragersinsight.adventure.stinky_situation.title",
                "advancements.foragersinsight.adventure.stinky_situation.description");
        JsonObject criteria = new JsonObject();
        criteria.add("stinky_situation", simpleTrigger("foragersinsight:stinky_situation"));
        advancement.add("criteria", criteria);
        advancement.add("requirements", requirements("stinky_situation"));
        return advancement;
    }

    private static JsonObject tapThat() {
        JsonObject advancement = childAdvancement(FIAdvancements.TAP_THAT_ICON,
                "advancements.foragersinsight.adventure.tap_that.title",
                "advancements.foragersinsight.adventure.tap_that.description");
        JsonObject criteria = new JsonObject();
        criteria.add("tap_that", simpleTrigger("foragersinsight:tap_that"));
        advancement.add("criteria", criteria);
        advancement.add("requirements", requirements("tap_that"));
        return advancement;
    }

    private static JsonObject birchPlease() {
        JsonObject advancement = childAdvancement(FIAdvancements.TAP_THAT, FIAdvancements.BIRCH_PLEASE_ICON,
                "advancements.foragersinsight.adventure.birch_please.title",
                "advancements.foragersinsight.adventure.birch_please.description");
        JsonObject criteria = new JsonObject();
        criteria.add("birch_please", simpleTrigger("foragersinsight:birch_please"));
        advancement.add("criteria", criteria);
        advancement.add("requirements", requirements("birch_please"));
        return advancement;
    }

    private static JsonObject givingTrees() {
        JsonObject advancement = childAdvancement(FIAdvancements.WILD_FLOWERS, FIAdvancements.GIVING_TREES_ICON,
                "advancements.foragersinsight.adventure.giving_trees.title",
                "advancements.foragersinsight.adventure.giving_trees.description");
        JsonObject criteria = new JsonObject();
        criteria.add("giving_trees", inventoryChangedWithItems(
                "foragersinsight:lilac_bloom",
                "minecraft:apple",
                "foragersinsight:black_acorn",
                "foragersinsight:spruce_tips"
        ));
        advancement.add("criteria", criteria);
        advancement.add("requirements", requirements("giving_trees"));
        return advancement;
    }



    private static JsonObject childAdvancement(ResourceLocation icon, String titleKey, String descriptionKey) {
        return childAdvancement(FIAdvancements.ROOT, icon, titleKey, descriptionKey);
    }

    private static JsonObject childAdvancement(ResourceLocation parent, ResourceLocation icon, String titleKey, String descriptionKey) {
        JsonObject advancement = baseDisplay(icon, titleKey, descriptionKey);
        advancement.addProperty("parent", parent.toString());
        return advancement;
    }

    private static JsonObject baseDisplay(ResourceLocation icon, String titleKey, String descriptionKey) {
        JsonObject advancement = new JsonObject();
        JsonObject display = new JsonObject();
        JsonObject displayIcon = new JsonObject();
        displayIcon.addProperty("item", icon.toString());
        display.add("icon", displayIcon);
        display.add("title", translation(titleKey));
        display.add("description", translation(descriptionKey));
        display.addProperty("frame", "task");
        display.addProperty("show_toast", true);
        display.addProperty("announce_to_chat", true);
        display.addProperty("hidden", false);
        advancement.add("display", display);
        return advancement;
    }
    private static JsonObject inventoryChangedWithItems(String... itemIds) {
        JsonObject criterion = new JsonObject();
        criterion.addProperty("trigger", "minecraft:inventory_changed");
        JsonObject conditions = new JsonObject();
        JsonArray items = new JsonArray();
        JsonObject item = new JsonObject();
        JsonArray itemList = new JsonArray();
        for (String itemId : itemIds) {
            itemList.add(itemId);
        }
        item.add("items", itemList);
        items.add(item);
        conditions.add("items", items);
        criterion.add("conditions", conditions);
        return criterion;
    }

    private static JsonObject translation(String key) {
        JsonObject translate = new JsonObject();
        translate.addProperty("translate", key);
        return translate;
    }

    private static JsonObject inventoryChangedWithTag(String tag) {
        JsonObject criterion = new JsonObject();
        criterion.addProperty("trigger", "minecraft:inventory_changed");
        JsonObject conditions = new JsonObject();
        JsonArray items = new JsonArray();
        JsonObject item = new JsonObject();
        item.addProperty("tag", tag);
        items.add(item);
        conditions.add("items", items);
        criterion.add("conditions", conditions);
        return criterion;
    }
    private static JsonObject simpleTrigger(String triggerId) {
        JsonObject criterion = new JsonObject();
        criterion.addProperty("trigger", triggerId);
        return criterion;
    }

    private static JsonObject inventoryChangedWithItem(String itemId) {
        JsonObject criterion = new JsonObject();
        criterion.addProperty("trigger", "minecraft:inventory_changed");
        JsonObject conditions = new JsonObject();
        JsonArray items = new JsonArray();
        JsonObject item = new JsonObject();
        JsonArray itemIds = new JsonArray();
        itemIds.add(itemId);
        item.add("items", itemIds);
        items.add(item);
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