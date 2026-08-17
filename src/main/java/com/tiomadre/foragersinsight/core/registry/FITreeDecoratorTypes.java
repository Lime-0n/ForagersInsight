package com.tiomadre.foragersinsight.core.registry;

import com.tiomadre.foragersinsight.common.worldgen.trees.decorator.BountifulOakLeafDecorator;
import com.tiomadre.foragersinsight.common.worldgen.trees.decorator.SappyBirchLogDecorator;
import com.tiomadre.foragersinsight.common.worldgen.trees.decorator.BountifulSpruceTipDecorator;
import com.tiomadre.foragersinsight.common.worldgen.trees.decorator.WallMushroomTreeDecorator;
import com.tiomadre.foragersinsight.core.ForagersInsight;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;


public class FITreeDecoratorTypes {
    public static final DeferredRegister<TreeDecoratorType<?>> TREE_DECORATOR_TYPES =
            DeferredRegister.create(Registries.TREE_DECORATOR_TYPE, ForagersInsight.MOD_ID);

    public static final Supplier<TreeDecoratorType<SappyBirchLogDecorator>> SAPPY_BIRCH_LOG_DECORATOR =
            TREE_DECORATOR_TYPES.register("sappy_birch_log_decorator",
                    () -> new TreeDecoratorType<>(SappyBirchLogDecorator.CODEC));

    public static final Supplier<TreeDecoratorType<BountifulOakLeafDecorator>> BOUNTIFUL_OAK_LEAF_DECORATOR =
            TREE_DECORATOR_TYPES.register("bountiful_oak_leaf_decorator",
                    () -> new TreeDecoratorType<>(BountifulOakLeafDecorator.CODEC));

    public static final Supplier<TreeDecoratorType<BountifulSpruceTipDecorator>> BOUNTIFUL_SPRUCE_TIP_DECORATOR =
            TREE_DECORATOR_TYPES.register("bountiful_spruce_tip_decorator",
                    () -> new TreeDecoratorType<>(BountifulSpruceTipDecorator.CODEC));
    public static final Supplier<TreeDecoratorType<WallMushroomTreeDecorator>> WALL_MUSHROOM_DECORATOR =
            TREE_DECORATOR_TYPES.register("wall_mushroom_decorator",
                    () -> new TreeDecoratorType<>(WallMushroomTreeDecorator.CODEC));
}
