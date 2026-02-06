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
                "advancements.foragersinsight.adventure.foragers_insight.description",
                FIAdvancements.FIFrameType.TASK);
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
        JsonObject advancement = childAdvancement(FIAdvancements.BRUSH_IT_OFF, FIAdvancements.SPRING_CLEANING,
                "advancements.foragersinsight.adventure.brush_it_off.title",
                "advancements.foragersinsight.adventure.brush_it_off.description",
                FIAdvancements.FIFrameType.GOAL);
        JsonObject criteria = new JsonObject();
        criteria.add("brush_suspicious_litter", simpleTrigger("foragersinsight:brush_suspicious_litter"));
        advancement.add("criteria", criteria);
        advancement.add("requirements", requirements("brush_suspicious_litter"));
        return advancement;
    }
    private static JsonObject rareFind() {
        JsonObject advancement = childAdvancement(FIAdvancements.RARE_FIND,FIAdvancements.BRUSH_IT_OFF,
                "advancements.foragersinsight.adventure.rare_find.title",
                "advancements.foragersinsight.adventure.rare_find.description",
                FIAdvancements.FIFrameType.CHALLENGE);
        JsonObject criteria = new JsonObject();
        criteria.add("find_blewit_mushroom", simpleTrigger("foragersinsight:find_blewit_mushroom"));
        advancement.add("criteria", criteria);
        advancement.add("requirements", requirements("find_blewit_mushroom"));
        return advancement;
    }

    private static JsonObject wildFlowers() {
        JsonObject advancement = childAdvancement(FIAdvancements.WILD_FLOWERS,
                "advancements.foragersinsight.adventure.wild_flowers.title",
                "advancements.foragersinsight.adventure.wild_flowers.description",
                FIAdvancements.FIFrameType.TASK);
        JsonObject criteria = new JsonObject();
        criteria.add("forage_wild_flower", inventoryChangedWithTag(tagLocation(FITags.ItemTag.WILD_FLOWER_DROPS)));
        advancement.add("criteria", criteria);
        advancement.add("requirements", requirements("forage_wild_flower"));
        return advancement;
    }

    private static JsonObject springCleaning() {
        JsonObject advancement = childAdvancement(FIAdvancements.SPRING_CLEANING,
                "advancements.foragersinsight.adventure.spring_cleaning.title",
                "advancements.foragersinsight.adventure.spring_cleaning.description",
                FIAdvancements.FIFrameType.TASK);
        JsonObject criteria = new JsonObject();
        criteria.add("pick_up_brush", inventoryChangedWithItem("minecraft:brush"));
        advancement.add("criteria", criteria);
        advancement.add("requirements", requirements("pick_up_brush"));
        return advancement;
    }

    private static JsonObject shearingIsCaring() {
        JsonObject advancement = childAdvancement(FIAdvancements.SHEARING_IS_CARING, FIAdvancements.GIVING_TREES,
                "advancements.foragersinsight.adventure.shear.title",
                "advancements.foragersinsight.adventure.shear.description",
                FIAdvancements.FIFrameType.GOAL);
        JsonObject criteria = new JsonObject();
        criteria.add("shear_bountiful_tree", simpleTrigger("foragersinsight:shear_bountiful_tree"));
        advancement.add("criteria", criteria);
        advancement.add("requirements", requirements("shear_bountiful_tree"));
        return advancement;
    }
    private static JsonObject scentsational() {
        JsonObject advancement = childAdvancement(FIAdvancements.SCENTSATIONAL,FIAdvancements.WILD_FLOWERS,
                "advancements.foragersinsight.adventure.scentsational.title",
                "advancements.foragersinsight.adventure.scentsational.description",
                FIAdvancements.FIFrameType.GOAL);
        JsonObject criteria = new JsonObject();
        criteria.add("scentsational", simpleTrigger("foragersinsight:scentsational"));
        advancement.add("criteria", criteria);
        advancement.add("requirements", requirements("scentsational"));
        return advancement;
    }

    private static JsonObject stinkySituation() {
        JsonObject advancement = childAdvancement(FIAdvancements.STINKY_SITUATION,FIAdvancements.SCENTSATIONAL,
                "advancements.foragersinsight.adventure.stinky_situation.title",
                "advancements.foragersinsight.adventure.stinky_situation.description",
                FIAdvancements.FIFrameType.CHALLENGE);
        JsonObject criteria = new JsonObject();
        criteria.add("stinky_situation", simpleTrigger("foragersinsight:stinky_situation"));
        advancement.add("criteria", criteria);
        advancement.add("requirements", requirements("stinky_situation"));
        return advancement;
    }

    private static JsonObject tapThat() {
        JsonObject advancement = childAdvancement(FIAdvancements.TAP_THAT,
                "advancements.foragersinsight.adventure.tap_that.title",
                "advancements.foragersinsight.adventure.tap_that.description",
                FIAdvancements.FIFrameType.TASK);
        JsonObject criteria = new JsonObject();
        criteria.add("tap_that", simpleTrigger("foragersinsight:tap_that"));
        advancement.add("criteria", criteria);
        advancement.add("requirements", requirements("tap_that"));
        return advancement;
    }

    private static JsonObject birchPlease() {
        JsonObject advancement = childAdvancement(FIAdvancements.BIRCH_PLEASE,FIAdvancements.TAP_THAT,
                "advancements.foragersinsight.adventure.birch_please.title",
                "advancements.foragersinsight.adventure.birch_please.description",
                FIAdvancements.FIFrameType.GOAL);
        JsonObject criteria = new JsonObject();
        criteria.add("birch_please", simpleTrigger("foragersinsight:birch_please"));
        advancement.add("criteria", criteria);
        advancement.add("requirements", requirements("birch_please"));
        return advancement;
    }

    private static JsonObject givingTrees() {
        JsonObject advancement = childAdvancement(FIAdvancements.GIVING_TREES,
                "advancements.foragersinsight.adventure.giving_trees.title",
                "advancements.foragersinsight.adventure.giving_trees.description",
                FIAdvancements.FIFrameType.TASK);
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

    private static JsonObject stopHammerTime() {
        JsonObject advancement = childAdvancement(FIAdvancements.STOP_HAMMER_TIME,
                "advancements.foragersinsight.adventure.stop_hammer_time.title",
                "advancements.foragersinsight.adventure.stop_hammer_time.description",
                FIAdvancements.FIFrameType.TASK);
        JsonObject criteria = new JsonObject();
        criteria.add("pick_up_mallet", inventoryChangedWithTag(tagLocation(FITags.ItemTag.MALLETS)));
        advancement.add("criteria", criteria);
        advancement.add("requirements", requirements("pick_up_mallet"));
        return advancement;
    }

    private static JsonObject willItCrush() {
        JsonObject advancement = childAdvancement(FIAdvancements.WILL_IT_CRUSH,FIAdvancements.STOP_HAMMER_TIME,
                "advancements.foragersinsight.adventure.will_it_crush.title",
                "advancements.foragersinsight.adventure.will_it_crush.description",
                FIAdvancements.FIFrameType.GOAL);
        JsonObject criteria = new JsonObject();
        criteria.add("will_it_crush", simpleTrigger("foragersinsight:will_it_crush"));
        advancement.add("criteria", criteria);
        advancement.add("requirements", requirements("will_it_crush"));
        return advancement;
    }
    private static JsonObject childAdvancement(ResourceLocation id, String titleKey, String descriptionKey,
                                               FIAdvancements.FIFrameType frame) {
        return childAdvancement(id, FIAdvancements.ROOT, titleKey, descriptionKey, frame);
    }

    private static JsonObject childAdvancement(ResourceLocation id, ResourceLocation parent, String titleKey,
                                               String descriptionKey, FIAdvancements.FIFrameType frame) {
        JsonObject advancement = baseDisplay(FIAdvancements.node(id), titleKey, descriptionKey, frame);
        advancement.addProperty("parent", parent.toString());
        return advancement;
    }

    private static JsonObject baseDisplay(FIAdvancements.Node node, String titleKey, String descriptionKey,
                                          FIAdvancements.FIFrameType frame) {
        JsonObject advancement = new JsonObject();
        JsonObject display = new JsonObject();
        JsonObject displayIcon = new JsonObject();
        displayIcon.addProperty("item", node.icon().toString());
        display.add("icon", displayIcon);
        display.add("title", translation(titleKey));
        display.add("description", translation(descriptionKey));
        display.addProperty("frame", frame.id);
        display.addProperty("show_toast", true);
        display.addProperty("announce_to_chat", true);
        display.addProperty("hidden", false);
        display.addProperty("x", node.x());
        display.addProperty("y", node.y());
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
