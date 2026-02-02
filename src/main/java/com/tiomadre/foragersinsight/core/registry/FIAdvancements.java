package com.tiomadre.foragersinsight.core.registry;

import com.tiomadre.foragersinsight.core.ForagersInsight;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public final class FIAdvancements {
    public static final ResourceLocation ROOT = ForagersInsight.rl("adventure/foragers_insight");
    public static final ResourceLocation BRUSH_IT_OFF = ForagersInsight.rl("adventure/brush_it_off");
    public static final ResourceLocation WILD_FLOWERS = ForagersInsight.rl("adventure/wild_flowers");
    public static final ResourceLocation SHEARING_IS_CARING = ForagersInsight.rl("adventure/shearing_is_caring");
    public static final ResourceLocation GIVING_TREES = ForagersInsight.rl("adventure/giving_trees");

    public static final ResourceLocation ROOT_ICON = ForagersInsight.rl("handbasket");
    public static final ResourceLocation BRUSH_IT_OFF_ICON = new ResourceLocation("minecraft", "brush");
    public static final ResourceLocation WILD_FLOWERS_ICON = ForagersInsight.rl("rose_hip");
    public static final ResourceLocation SHEARING_IS_CARING_ICON = ForagersInsight.rl("flint_shears");
    public static final ResourceLocation GIVING_TREES_ICON = ForagersInsight.rl("oak_sapling");

    public static final Node ROOT_NODE = new Node(ROOT, ROOT_ICON, 0, 0);
    public static final List<Node> FIRST_ROW = List.of(
            new Node(BRUSH_IT_OFF, BRUSH_IT_OFF_ICON, -6, 1),
            new Node(WILD_FLOWERS, WILD_FLOWERS_ICON, -3, 1),
            new Node(SHEARING_IS_CARING, SHEARING_IS_CARING_ICON, 0, 1),
            new Node(GIVING_TREES, GIVING_TREES_ICON, 3, 1)
    );

    private FIAdvancements() {
    }

    public record Node(ResourceLocation id, ResourceLocation icon, int x, int y) {
    }
}