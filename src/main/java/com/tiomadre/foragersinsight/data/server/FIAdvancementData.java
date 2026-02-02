package com.tiomadre.foragersinsight.data.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tiomadre.foragersinsight.core.registry.FIAdvancementTree;
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

        advancements.put(FIAdvancementTree.ROOT, rootAdvancement());
        advancements.put(FIAdvancementTree.BRUSH_IT_OFF, brushItOff());
        advancements.put(FIAdvancementTree.WILD_FLOWERS, wildFlowers());
        advancements.put(FIAdvancementTree.SHEARING_IS_CARING, shearingIsCaring());
        advancements.put(FIAdvancementTree.GIVING_TREES, givingTrees());

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
        JsonObject advancement = baseDisplay(FIAdvancementTree.ROOT_ICON,
                "advancements.foragersinsight.adventure.foragers_insight.title",
                "advancements.foragersinsight.adventure.foragers_insight.description");
        advancement.getAsJsonObject("display")
                .addProperty("background", "minecraft:textures/gui/advancements/backgrounds/adventure.png");

        JsonObject criteria = new JsonObject();
        criteria.add("has_foragers_insight_item",
                inventoryChangedWithTag("foragersinsight:foragers_insight_items"));
        advancement.add("criteria", criteria);
        advancement.add("requirements", requirements("has_foragers_insight_item"));
        return advancement;
    }

    private static JsonObject brushItOff() {
        JsonObject advancement = childAdvancement(FIAdvancementTree.BRUSH_IT_OFF_ICON,
                "advancements.foragersinsight.adventure.brush_it_off.title",
                "advancements.foragersinsight.adventure.brush_it_off.description");
        JsonObject criteria = new JsonObject();
        criteria.add("brush_suspicious_litter", inventoryChangedWithItem("foragersinsight:suspicious_leaf_litter"));
        advancement.add("criteria", criteria);
        advancement.add("requirements", requirements("brush_suspicious_litter"));
        return advancement;
    }

    private static JsonObject wildFlowers() {
        JsonObject advancement = childAdvancement(FIAdvancementTree.WILD_FLOWERS_ICON,
                "advancements.foragersinsight.adventure.wild_flowers.title",
                "advancements.foragersinsight.adventure.wild_flowers.description");
        JsonObject criteria = new JsonObject();
        criteria.add("forage_wild_flower", inventoryChangedWithTag(tagLocation(FITags.ItemTag.WILD_FLOWER_DROPS)));
        advancement.add("criteria", criteria);
        advancement.add("requirements", requirements("forage_wild_flower"));
        return advancement;
    }

    private static JsonObject shearingIsCaring() {
        JsonObject advancement = childAdvancement(FIAdvancementTree.SHEARING_IS_CARING_ICON,
                "advancements.foragersinsight.adventure.shearing_is_caring.title",
                "advancements.foragersinsight.adventure.shearing_is_caring.description");
        JsonObject criteria = new JsonObject();
        criteria.add("snip_a_ripe_crop", inventoryChangedWithItem("foragersinsight:spruce_tips"));
        advancement.add("criteria", criteria);
        advancement.add("requirements", requirements("snip_a_ripe_crop"));
        return advancement;
    }

    private static JsonObject givingTrees() {
        JsonObject advancement = childAdvancement(FIAdvancementTree.GIVING_TREES_ICON,
                "advancements.foragersinsight.adventure.giving_trees.title",
                "advancements.foragersinsight.adventure.giving_trees.description");
        JsonObject criteria = new JsonObject();
        JsonObject placedBlock = new JsonObject();
        placedBlock.addProperty("trigger", "minecraft:placed_block");
        JsonObject conditions = new JsonObject();
        conditions.addProperty("block", "minecraft:lilac");
        placedBlock.add("conditions", conditions);
        criteria.add("plant_on_rich_soil", placedBlock);
        advancement.add("criteria", criteria);
        advancement.add("requirements", requirements("plant_on_rich_soil"));
        return advancement;
    }

    private static JsonObject childAdvancement(ResourceLocation icon, String titleKey, String descriptionKey) {
        JsonObject advancement = baseDisplay(icon, titleKey, descriptionKey);
        advancement.addProperty("parent", FIAdvancementTree.ROOT.toString());
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