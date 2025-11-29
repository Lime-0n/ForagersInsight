package com.tiomadre.foragersinsight.core.other;

import com.tiomadre.foragersinsight.common.block.BountifulLeavesBlock;
import com.tiomadre.foragersinsight.common.block.SpruceTipBlock;
import com.tiomadre.foragersinsight.common.block.TapperBlock;
import com.tiomadre.foragersinsight.common.block.entity.suspiciouslitter.SuspiciousLitterBlockEntity;
import com.tiomadre.foragersinsight.common.block.entity.suspiciouslitter.SuspiciousLitterLoot;
import com.tiomadre.foragersinsight.core.ForagersInsight;
import com.tiomadre.foragersinsight.core.registry.FIBlocks;
import com.tiomadre.foragersinsight.core.registry.FIConfig;
import com.tiomadre.foragersinsight.core.registry.FIMobEffects;
import com.tiomadre.foragersinsight.data.server.tags.FITags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BrushItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import vectorwing.farmersdelight.common.block.MushroomColonyBlock;
import vectorwing.farmersdelight.common.block.TomatoVineBlock;
import vectorwing.farmersdelight.common.item.KnifeItem;

import java.util.*;

@Mod.EventBusSubscriber(modid = ForagersInsight.MOD_ID)
public class FarmingXPEvents {

    private static final Map<ResourceKey<Level>, Map<BlockPos, PendingForage>> PENDING_FORAGING_DROPS = new HashMap<>();
    private static final int FORAGING_DROP_TIMEOUT_TICKS = 20;


    @SubscribeEvent
    public static void onKnifeHarvest(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) return;
        if (!(event.getLevel() instanceof ServerLevel level)) return;

        ItemStack tool = player.getMainHandItem();
        if (!(tool.getItem() instanceof KnifeItem)) return;

        BlockState state = event.getState();

        if (state.is(FITags.BlockTag.FORAGING)) {
            trackPotentialForageDrop(level, event.getPos(), state, player);
        }
    }

    @SubscribeEvent
    public static void onForageDrop(EntityJoinLevelEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (!(event.getEntity() instanceof ItemEntity item)) return;

        PendingForage pending = popPendingForageNear(level, item.blockPosition());
        if (pending == null) return;
        if (pending.player.level() != level) return;
        if (item.getItem().isEmpty()) return;

        awardUnifiedXP(level, pending.player, 0, 2, XPSource.FORAGING, true);
    }

    // Crop Harvest XP
    @SubscribeEvent
    public static void onCropHarvest(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) return;
        if (!(event.getLevel() instanceof ServerLevel level)) return;

        BlockPos pos = event.getPos();
        BlockState state = event.getState();
        Block block = state.getBlock();

        // Gourds attached to stems
        if (block instanceof StemGrownBlock) {
            boolean connectedToStem = isConnectedToStem(level, pos);
            awardUnifiedXP(level, player, connectedToStem ? 1 : 0, 2, XPSource.CROP, false);
            return;
        }

        Optional<IntegerProperty> ageProp = getAgeProp(state);
        if (ageProp.isPresent() && isMature(state, ageProp.get())) {

            if (!isIntactDoublePlant(level, pos, state)) return;
            awardUnifiedXP(level, player, 1, 3, XPSource.CROP, false); // 1–2
        }
    }
    // Right-Click Harvests
    @SubscribeEvent
    public static void onRightClickHarvest(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!(event.getLevel() instanceof ServerLevel level)) return;

        BlockPos pos = event.getPos();
        ItemStack held = player.getItemInHand(event.getHand());
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();

        // Beehive
        if (block instanceof BeehiveBlock &&
                state.getValue(BeehiveBlock.HONEY_LEVEL) >= 5 &&
                ((held.getItem() instanceof ShearsItem) || held.is(Items.GLASS_BOTTLE))) {

            awardUnifiedXP(level, player, 2, 4, XPSource.BEEHIVE, false);
            return;
        }

        // Tapper
        if (block instanceof TapperBlock &&
                state.getValue(TapperBlock.HAS_TAPPER) &&
                state.getValue(TapperBlock.FILL) == 4 &&
                held.is(Items.BUCKET)) {

            awardUnifiedXP(level, player, 1, 3, XPSource.TAPPER, false);
            return;
        }

        // Mushroom Colony
        if (block instanceof MushroomColonyBlock) {
            int before = state.getValue(MushroomColonyBlock.COLONY_AGE);
            if (before > 0) {
                defer(level, () -> {
                    BlockState after = level.getBlockState(pos);
                    if (!(after.getBlock() instanceof MushroomColonyBlock)) return;
                    int afterAge = after.getValue(MushroomColonyBlock.COLONY_AGE);
                    if (afterAge < before) {
                        awardUnifiedXP(level, player, 0, 2, XPSource.FORAGING, true);
                    }
                });
                return;
            }
        }

        // Tomato Vine
        if (block instanceof TomatoVineBlock vine) {
            int current = state.getValue(TomatoVineBlock.VINE_AGE);
            if (current >= vine.getMaxAge() && (held.isEmpty() || held.getItem() instanceof ShearsItem)) {

                defer(level, () -> {
                    BlockState after = level.getBlockState(pos);
                    if (!(after.getBlock() instanceof TomatoVineBlock)) return;
                    if (after.getValue(TomatoVineBlock.VINE_AGE) < current) {
                        awardUnifiedXP(level, player, 1, 3, XPSource.CROP, false);
                    }
                });
                return;
            }
        }

        // Generic right-click harvests
        if (block instanceof BonemealableBlock &&
                !(block instanceof CropBlock) &&
                !(block instanceof StemBlock) &&
                !(block instanceof AttachedStemBlock)) {

            Optional<IntegerProperty> ageProp = getAgeProp(state);
            if (ageProp.isEmpty()) return;
            IntegerProperty age = ageProp.get();

            int current = state.getValue(age);
            if (!isMature(state, age)) return;

            defer(level, () -> {
                BlockState updated = level.getBlockState(pos);
                boolean harvested =
                        !updated.is(state.getBlock()) ||
                                (updated.hasProperty(age) && updated.getValue(age) < current);

                if (harvested) {
                    boolean leavesOrTips =
                            state.getBlock() instanceof BountifulLeavesBlock ||
                                    state.getBlock() instanceof SpruceTipBlock;

                    awardUnifiedXP(level, player,
                            leavesOrTips ? 0 : 1,
                            leavesOrTips ? 2 : 3,
                            XPSource.FORAGING,
                            true
                    );
                }
            });
        }
    }

    // Shearing Animals
    @SubscribeEvent
    public static void onAnimalShear(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        Level lvl = event.getLevel();
        if (lvl.isClientSide()) return;

        ItemStack tool = player.getItemInHand(event.getHand());
        if (!(tool.getItem() instanceof ShearsItem)) return;

        // Sheep
        if (event.getTarget() instanceof Sheep sheep) {
            if (!sheep.isBaby() && !sheep.isSheared()) {
                awardUnifiedXP((ServerLevel) lvl, player, 1, 3, XPSource.ANIMAL_SHEAR, false);
            }
            return;
        }

        // Chickens (Feather Shearing)
        if (event.getTarget() instanceof Chicken chicken) {
            if (!chicken.isBaby()) {
                long now = lvl.getGameTime();
                long last = chicken.getPersistentData().getLong("ShearFeatherTime");
                if (now - last >= 2400L) {
                    awardUnifiedXP((ServerLevel) lvl, player, 1, 3, XPSource.ANIMAL_SHEAR, false);
                }
            }
        }
    }

    private static void awardUnifiedXP(ServerLevel level, ServerPlayer player, int min, int max, XPSource src, boolean isForaging) {

        if (!src.isEnabled()) return;

        int delta = Math.max(0, max - min);
        int val = (delta == 0) ? min : (min + player.getRandom().nextInt(delta));

        if (isForaging && player.hasEffect(FIMobEffects.BLOOM.get())) {
            val = Math.max(val, 1);
        }

        double mult = FIConfig.COMMON.xpGlobalMultiplier.get();
        val = (int) Math.floor(val * mult);

        if (val > 0) {
            ExperienceOrb.award(level, player.position(), val);
        }
    }

    private static Optional<IntegerProperty> getAgeProp(BlockState state) {
        for (Property<?> property : state.getProperties()) {
            if (property instanceof IntegerProperty integerProperty &&
                    "age".equals(property.getName())) {
                return Optional.of(integerProperty);
            }
        }
        return Optional.empty();
    }

    private static boolean isMature(BlockState state, IntegerProperty age) {
        int cur = state.getValue(age);
        int max = Integer.MIN_VALUE;
        for (int candidate : age.getPossibleValues()) {
            if (candidate > max) max = candidate;
        }
        if (max == Integer.MIN_VALUE) max = cur;
        return cur >= max;
    }

    private static boolean isIntactDoublePlant(Level level, BlockPos pos, BlockState state) {
        if (state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)) {
            DoubleBlockHalf half = state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF);
            BlockPos counterpart = (half == DoubleBlockHalf.UPPER) ? pos.below() : pos.above();
            return level.getBlockState(counterpart).is(state.getBlock());
        }
        for (Property<?> prop : state.getProperties()) {
            if ("half".equals(prop.getName()) &&
                    prop instanceof net.minecraft.world.level.block.state.properties.EnumProperty<?> ep) {

                Comparable<?> val = state.getValue(ep);
                if (val instanceof DoubleBlockHalf half) {
                    BlockPos counterpart = (half == DoubleBlockHalf.UPPER) ? pos.below() : pos.above();
                    return level.getBlockState(counterpart).is(state.getBlock());
                }
            }
        }
        return true;
        //Brushing Suspicious Leaf Litter, 1-3 depending on rarity of loot
    }
    @SubscribeEvent
    public static void onBrushSuspiciousLitter(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!(event.getLevel() instanceof ServerLevel level)) return;

        ItemStack held = event.getItemStack();
        if (!(held.getItem() instanceof BrushItem)) return;

        BlockState state = level.getBlockState(event.getPos());
        if (!state.is(FIBlocks.SUSPICIOUS_LEAF_LITTER.get())) return;

        int min = 1;
        int max = 3;

        if (level.getBlockEntity(event.getPos()) instanceof SuspiciousLitterBlockEntity blockEntity) {
            ItemStack revealed = blockEntity.getRevealedItem();
            if (!revealed.isEmpty()) {
                OptionalInt weight = SuspiciousLitterLoot.getDropWeight(state, revealed);
                if (weight.isPresent()) {
                    int rareBoost = Math.max(0, 5 - weight.getAsInt());
                    min += rareBoost;
                    max += rareBoost;
                }
            }
        }

        awardUnifiedXP(level, player, min, max, XPSource.FORAGING, true);
    }

    private static void defer(ServerLevel level, Runnable r) {
        level.getServer().execute(r);
    }

    private static void trackPotentialForageDrop(ServerLevel level, BlockPos pos, BlockState state, ServerPlayer player) {
        ResourceKey<Level> dim = level.dimension();
        Map<BlockPos, PendingForage> map = PENDING_FORAGING_DROPS.computeIfAbsent(dim, k -> new HashMap<>());
        long now = level.getGameTime();
        cleanupExpiredEntries(map, now);

        BlockPos trackAt = adjustTrackedPosition(state, pos).immutable();
        map.put(trackAt, new PendingForage(player, now + FORAGING_DROP_TIMEOUT_TICKS));
    }

    private static PendingForage popPendingForageNear(ServerLevel level, BlockPos itemPos) {
        ResourceKey<Level> dim = level.dimension();
        Map<BlockPos, PendingForage> map = PENDING_FORAGING_DROPS.get(dim);
        if (map == null || map.isEmpty()) return null;

        long now = level.getGameTime();
        cleanupExpiredEntries(map, now);

        for (BlockPos probe : new BlockPos[]{itemPos, itemPos.below(), itemPos.above()}) {
            PendingForage got = map.remove(probe);
            if (got != null) {
                if (map.isEmpty()) PENDING_FORAGING_DROPS.remove(dim);
                return got.isExpired(now) ? null : got;
            }
        }
        if (map.isEmpty()) PENDING_FORAGING_DROPS.remove(dim);
        return null;
    }

    private static void cleanupExpiredEntries(Map<BlockPos, PendingForage> entries, long now) {
        entries.entrySet().removeIf(e -> e.getValue().isExpired(now));
    }

    private static BlockPos adjustTrackedPosition(BlockState state, BlockPos pos) {
        if (state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF) &&
                state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER) {
            return pos.below();
        }
        return pos;
    }

    private static boolean isConnectedToStem(Level level, BlockPos pos) {
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockPos n = pos.relative(dir);
            BlockState s = level.getBlockState(n);
            Block b = s.getBlock();

            if (b instanceof StemBlock) return true;

            if (b instanceof AttachedStemBlock &&
                    s.hasProperty(AttachedStemBlock.FACING) &&
                    s.getValue(AttachedStemBlock.FACING) == dir.getOpposite()) {
                return true;
            }
        }
        return false;
    }

    private record PendingForage(ServerPlayer player, long expiryTick) {
        boolean isExpired(long now) {
            return now > expiryTick || !player.isAlive();
        }
    }
}
  enum XPSource {
    CROP(FIConfig.COMMON.enableCropHarvestXP),
    FORAGING(FIConfig.COMMON.enableForagingXP),
    ANIMAL_SHEAR(FIConfig.COMMON.enableAnimalShearXP),
    TAPPER(FIConfig.COMMON.enableTapperXP),
    BEEHIVE(FIConfig.COMMON.enableBeehiveXP);

    private final java.util.function.Supplier<Boolean> enabled;

    XPSource(java.util.function.Supplier<Boolean> enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled.get();
    }
}
