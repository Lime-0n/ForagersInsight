package com.tiomadre.foragersinsight.core.registry;

import com.tiomadre.foragersinsight.core.ForagersInsight;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public final class FIAdvancementTree {
    public static final ResourceLocation ROOT = ForagersInsight.rl("adventure/foragers_insight");
    public static final ResourceLocation WILD_FLOWERS = ForagersInsight.rl("adventure/wild_flowers");
    public static final ResourceLocation SHEARING_IS_CARING = ForagersInsight.rl("adventure/shearing_is_caring");
    public static final ResourceLocation GIVING_TREES = ForagersInsight.rl("adventure/giving_trees");

    public static final Node ROOT_NODE = new Node(ROOT, 0, 0);
    public static final List<Node> FIRST_ROW = List.of(
            new Node(WILD_FLOWERS, -2, 1),
            new Node(SHEARING_IS_CARING, 0, 1),
            new Node(GIVING_TREES, 2, 1)
    );

    private FIAdvancementTree() {
    }

    public record Node(ResourceLocation id, int x, int y) {
    }
}