package com.tiomadre.foragersinsight.core.other;

import com.tiomadre.foragersinsight.core.registry.FIAdvancementCriteria;
import com.tiomadre.foragersinsight.core.registry.FIEnchantments;
import com.tiomadre.foragersinsight.core.registry.FIMobEffects;
import com.tiomadre.foragersinsight.core.ForagersInsight;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import vectorwing.farmersdelight.common.block.RichSoilBlock;
import vectorwing.farmersdelight.common.block.RichSoilFarmlandBlock;

import java.util.List;

@Mod.EventBusSubscriber(modid = ForagersInsight.MOD_ID)
public class FIEvents {
    //Odorous Effect logic
    @SubscribeEvent
    public static void onMobJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (!(event.getEntity() instanceof PathfinderMob mob)) return;

        boolean alreadyPresent = mob.goalSelector.getAvailableGoals().stream()
                .map(WrappedGoal::getGoal)
                .anyMatch(goal -> goal instanceof OdorousAvoidance);
        if (alreadyPresent) return;

        mob.goalSelector.addGoal(3, new OdorousAvoidance(mob, 1.1D, 1.25D));
    }

    @SubscribeEvent
    public static void onMobTargetChange(LivingChangeTargetEvent event) {
        LivingEntity attacker = event.getEntity();
        LivingEntity newTarget = event.getNewTarget();

        if (!(attacker instanceof Monster monster)) return;
        if (!(newTarget instanceof Player player)) return;
        if (!player.hasEffect(FIMobEffects.ODOROUS.get())) return;

        event.setNewTarget(null);
        monster.setTarget(null);
    }
    // Bloom Effect XP Amp n Reduction
    @SubscribeEvent
    public static void onXpChange(PlayerXpEvent.XpChange event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!player.hasEffect(FIMobEffects.BLOOM.get())) return;

        int amount = scaleAmount(event.getAmount(), 1.2f, 0.8f);
        event.setAmount(amount);
    }

    @SubscribeEvent
    public static void onXpLevelChange(PlayerXpEvent.LevelChange event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!player.hasEffect(FIMobEffects.BLOOM.get())) return;

        int levels = scaleAmount(event.getLevels(), 1.2f, 0.8f);
        event.setLevels(levels);
    }

    private static int scaleAmount(int amount, float positiveMultiplier, float negativeMultiplier) {
        if (amount > 0) {
            return Math.round(amount * positiveMultiplier);
        } else if (amount < 0) {
            return Math.round(amount * negativeMultiplier);
        }
        return amount;
    }

    // Farmhand Enchant logic
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) return;

        ItemStack tool = player.getMainHandItem();
        int level = EnchantmentHelper.getItemEnchantmentLevel(FIEnchantments.FARMHAND.get(), tool);
        if (level <= 0) return;

        ServerLevel levelWorld = (ServerLevel) event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = event.getState();
        BlockEntity blockEntity = levelWorld.getBlockEntity(pos);

        event.setCanceled(true);
        levelWorld.setBlock(pos, state.getFluidState().createLegacyBlock(), 3);
        // Drops stuff directly into inventory
        List<ItemStack> drops = Block.getDrops(state, levelWorld, pos, blockEntity, player, tool);
        for (ItemStack drop : drops) {
            if (!player.getInventory().add(drop)) {
                player.drop(drop, false);
            }
        }

        tool.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(player.getUsedItemHand()));
    }
    // Giving Trees Advancement Check
    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        BlockState state = event.getPlacedBlock();
        BlockPos pos = event.getPos();
        BlockState belowState = event.getLevel().getBlockState(pos.below());

        if (isRichSoilSaplingPlacement(state, belowState)) {
            FIAdvancementCriteria.recordRichSoilTreeOwner(level, pos, player);
            return;
        }

        if (isRichSoilLilacPlacement(state, belowState)) {
            FIAdvancementCriteria.recordRichSoilTreeOwner(level, pos.below(), player);
        }
    }
    private static boolean isRichSoilSaplingPlacement(BlockState state, BlockState belowState) {
        if (!(belowState.getBlock() instanceof RichSoilBlock) && !(belowState.getBlock() instanceof RichSoilFarmlandBlock)) {
            return false;
        }
        return state.is(BlockTags.SAPLINGS) && (state.is(Blocks.OAK_SAPLING)
                || state.is(Blocks.DARK_OAK_SAPLING)
                || state.is(Blocks.SPRUCE_SAPLING)
                || state.is(Blocks.BIRCH_SAPLING));
    }

    private static boolean isRichSoilLilacPlacement(BlockState state, BlockState belowState) {
        if (!(belowState.getBlock() instanceof RichSoilBlock)) {
            return false;
        }
        return state.is(Blocks.LILAC) && state.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.LOWER;
    }
}