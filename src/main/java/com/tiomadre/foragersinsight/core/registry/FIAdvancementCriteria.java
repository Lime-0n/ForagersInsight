package com.tiomadre.foragersinsight.core.registry;

import com.google.gson.JsonObject;
import com.tiomadre.foragersinsight.core.ForagersInsight;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class FIAdvancementCriteria {
    public static final SimpleTrigger BRUSH_SUSPICIOUS_LITTER = register(new SimpleTrigger("brush_suspicious_litter"));
    public static final SimpleTrigger GROW_RICH_SOIL_TREE = register(new SimpleTrigger("grow_rich_soil_tree"));
    private static final Map<ResourceLocation, Map<BlockPos, UUID>> RICH_SOIL_TREE_OWNERS = new HashMap<>();

    private FIAdvancementCriteria() {
    }

    private static <T extends CriterionTrigger<?>> T register(T trigger) {
        return CriteriaTriggers.register(trigger);
    }

    public static void register() {
    }

    public static void recordRichSoilTreeOwner(ServerLevel level, BlockPos pos, ServerPlayer player) {
        RICH_SOIL_TREE_OWNERS
                .computeIfAbsent(level.dimension().location(), key -> new HashMap<>())
                .put(pos.immutable(), player.getUUID());
    }

    @Nullable
    public static ServerPlayer consumeRichSoilTreeOwner(ServerLevel level, BlockPos pos) {
        Map<BlockPos, UUID> owners = RICH_SOIL_TREE_OWNERS.get(level.dimension().location());
        if (owners == null) {
            return null;
        }
        UUID ownerId = owners.remove(pos);
        if (ownerId == null) {
            return null;
        }
        return (ServerPlayer) level.getPlayerByUUID(ownerId);
    }

    public static class SimpleTrigger extends SimpleCriterionTrigger<SimpleTrigger.TriggerInstance> {
        private final ResourceLocation id;

        public SimpleTrigger(String id) {
            this.id = ForagersInsight.rl(id);
        }

        @Override
        public ResourceLocation getId() {
            return id;
        }

        @Override
        protected TriggerInstance createInstance(JsonObject json, ContextAwarePredicate player, DeserializationContext context) {
            return new TriggerInstance(id, player);
        }

        public void trigger(ServerPlayer player) {
            this.trigger(player, TriggerInstance::matches);
        }

        public static class TriggerInstance extends AbstractCriterionTriggerInstance {
            public TriggerInstance(ResourceLocation id, ContextAwarePredicate player) {
                super(id, player);
            }

            public boolean matches() {
                return true;
            }
        }
    }
}