package com.tiomadre.foragersinsight.core.registry;

import com.tiomadre.foragersinsight.core.ForagersInsight;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class FIAdvancements {
        private static final int X_SPACING = 8;
        private static final int Y_SPACING = 4;

    public enum FIFrameType {
        TASK("task"),
        GOAL("goal"),
        CHALLENGE("challenge");

        public final String id;

        FIFrameType(String id) {
            this.id = id;
        }
    }

    public static final ResourceLocation ROOT = ForagersInsight.rl("adventure/foragers_insight");

    public static final ResourceLocation SPRING_CLEANING = ForagersInsight.rl("adventure/spring_cleaning");
    public static final ResourceLocation BRUSH_IT_OFF = ForagersInsight.rl("adventure/brush_it_off");
    public static final ResourceLocation RARE_FIND = ForagersInsight.rl("adventure/rare_find");
    public static final ResourceLocation WILD_FLOWERS = ForagersInsight.rl("adventure/wild_flowers");
    public static final ResourceLocation SHEARING_IS_CARING = ForagersInsight.rl("adventure/shearing_is_caring");
    public static final ResourceLocation GIVING_TREES = ForagersInsight.rl("adventure/giving_trees");
    public static final ResourceLocation STOP_HAMMER_TIME = ForagersInsight.rl("adventure/stop_hammer_time");
    public static final ResourceLocation WILL_IT_CRUSH = ForagersInsight.rl("adventure/will_it_crush");

    public static final ResourceLocation ROOT_ICON = ForagersInsight.rl("handbasket");
    public static final ResourceLocation SPRING_CLEANING_ICON = new ResourceLocation("minecraft", "brush");
    public static final ResourceLocation BRUSH_IT_OFF_ICON = ForagersInsight.rl("suspicious_leaf_litter");
    public static final ResourceLocation RARE_FIND_ICON = ForagersInsight.rl("blewit_mushroom");
    public static final ResourceLocation GIVING_TREES_ICON = new ResourceLocation("minecraft", "apple");
    public static final ResourceLocation STINKY_SITUATION_ICON = new ResourceLocation("farmersdelight", "organic_compost");
    public static final ResourceLocation TAP_THAT_ICON = ForagersInsight.rl("tapper");
    public static final ResourceLocation BIRCH_PLEASE_ICON = ForagersInsight.rl("birch_sap_bucket");
    public static final ResourceLocation SCENTSATIONAL_ICON = ForagersInsight.rl("diffuser");
    public static final ResourceLocation STOP_HAMMER_TIME_ICON = ForagersInsight.rl("flint_mallet");
    public static final ResourceLocation WILL_IT_CRUSH_ICON = ForagersInsight.rl("wheat_flour");

    public static final ResourceLocation TAP_THAT = ForagersInsight.rl("adventure/tap_that");
    public static final ResourceLocation BIRCH_PLEASE = ForagersInsight.rl("adventure/birch_please");

    public static final ResourceLocation WILD_FLOWERS_ICON = new ResourceLocation("minecraft", "rose_bush");
    public static final ResourceLocation SHEARING_IS_CARING_ICON = ForagersInsight.rl("flint_shears");

    public static final ResourceLocation SCENTSATIONAL = ForagersInsight.rl("adventure/scentsational");
    public static final ResourceLocation STINKY_SITUATION = ForagersInsight.rl("adventure/stinky_situation");

    public static final Node ROOT_NODE = node(ROOT, ROOT_ICON, 0, 0, FIFrameType.TASK);

    public static final List<Node> FIRST_ROW = List.of(
            node(SPRING_CLEANING, SPRING_CLEANING_ICON, -3, 1, FIFrameType.TASK),
            node(WILD_FLOWERS, WILD_FLOWERS_ICON, -1, 1, FIFrameType.TASK),
            node(GIVING_TREES, GIVING_TREES_ICON, 1, 1, FIFrameType.TASK),
            node(TAP_THAT, TAP_THAT_ICON, 3, 1, FIFrameType.TASK),
            node(STOP_HAMMER_TIME, STOP_HAMMER_TIME_ICON, 5, 1, FIFrameType.TASK)
    );

    public static final List<Node> SECOND_ROW = List.of(
            node(BRUSH_IT_OFF, BRUSH_IT_OFF_ICON, -3, 2, FIFrameType.GOAL),
            node(SCENTSATIONAL, SCENTSATIONAL_ICON, -1, 2, FIFrameType.GOAL),
            node(SHEARING_IS_CARING, SHEARING_IS_CARING_ICON, 1, 2, FIFrameType.GOAL),
            node(BIRCH_PLEASE, BIRCH_PLEASE_ICON, 3, 2, FIFrameType.GOAL),
            node(WILL_IT_CRUSH, WILL_IT_CRUSH_ICON, 5, 2, FIFrameType.GOAL)
    );

    public static final List<Node> THIRD_ROW = List.of(
            node(RARE_FIND, RARE_FIND_ICON, -3, 3, FIFrameType.CHALLENGE),
            node(STINKY_SITUATION, STINKY_SITUATION_ICON, -1, 3, FIFrameType.CHALLENGE)
    );

    private static final Map<ResourceLocation, Node> NODE_LOOKUP = createNodeLookup();

    private FIAdvancements() {
    }

    public static Node node(ResourceLocation id) {
        Node node = NODE_LOOKUP.get(id);
        if (node == null) {
            throw new IllegalArgumentException("Missing advancement node for id: " + id);
        }
        return node;
    }

    public record Node(
            ResourceLocation id,
            ResourceLocation icon,
            int x,
            int y,
            FIFrameType frame
    ) {
    }

    private static Node node(ResourceLocation id, ResourceLocation icon, int column, int row, FIFrameType frame) {
        return new Node(id, icon, column * X_SPACING, row * Y_SPACING, frame);
    }

    private static Map<ResourceLocation, Node> createNodeLookup() {
        Map<ResourceLocation, Node> nodes = new LinkedHashMap<>();
        nodes.put(ROOT_NODE.id(), ROOT_NODE);
        for (Node node : FIRST_ROW) {
            nodes.put(node.id(), node);
        }
        for (Node node : SECOND_ROW) {
            nodes.put(node.id(), node);
        }
        for (Node node : THIRD_ROW) {
            nodes.put(node.id(), node);
        }
        return Map.copyOf(nodes);
    }
}