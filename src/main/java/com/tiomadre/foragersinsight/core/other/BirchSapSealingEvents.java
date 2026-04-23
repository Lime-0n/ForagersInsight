package com.tiomadre.foragersinsight.core.other;

import com.tiomadre.foragersinsight.core.ForagersInsight;
import com.tiomadre.foragersinsight.core.registry.FIBlocks;
import com.tiomadre.foragersinsight.core.registry.FIItems;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

@Mod.EventBusSubscriber(modid = ForagersInsight.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BirchSapSealingEvents {
    private static final Map<Block, Block> SEALED_PLANKS = Map.ofEntries(
            Map.entry(Blocks.OAK_PLANKS, FIBlocks.SEALED_OAK_PLANKS.get()),
            Map.entry(Blocks.SPRUCE_PLANKS, FIBlocks.SEALED_SPRUCE_PLANKS.get()),
            Map.entry(Blocks.BIRCH_PLANKS, FIBlocks.SEALED_BIRCH_PLANKS.get()),
            Map.entry(Blocks.JUNGLE_PLANKS, FIBlocks.SEALED_JUNGLE_PLANKS.get()),
            Map.entry(Blocks.ACACIA_PLANKS, FIBlocks.SEALED_ACACIA_PLANKS.get()),
            Map.entry(Blocks.DARK_OAK_PLANKS, FIBlocks.SEALED_DARK_OAK_PLANKS.get()),
            Map.entry(Blocks.MANGROVE_PLANKS, FIBlocks.SEALED_MANGROVE_PLANKS.get()),
            Map.entry(Blocks.CHERRY_PLANKS, FIBlocks.SEALED_CHERRY_PLANKS.get()),
            Map.entry(Blocks.BAMBOO_PLANKS, FIBlocks.SEALED_BAMBOO_PLANKS.get()),
            Map.entry(Blocks.CRIMSON_PLANKS, FIBlocks.SEALED_CRIMSON_PLANKS.get()),
            Map.entry(Blocks.WARPED_PLANKS, FIBlocks.SEALED_WARPED_PLANKS.get()),
            Map.entry(FIBlocks.LILAC_PLANKS.get(), FIBlocks.SEALED_LILAC_PLANKS.get())
    );

    @SubscribeEvent
    public static void onBrushSapRightClick(PlayerInteractEvent.RightClickBlock event) {
        if (event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }

        ItemStack mainHand = event.getEntity().getMainHandItem();
        ItemStack offHand = event.getEntity().getOffhandItem();
        boolean canPrimeBrush = mainHand.is(Items.BRUSH) && offHand.is(FIItems.BIRCH_SAP_BOTTLE.get());

        if (canPrimeBrush) {
            if (!event.getLevel().isClientSide) {
                ItemStack sappyBrush = new ItemStack(FIItems.SAPPY_BRUSH.get());
                mainHand.shrink(1);
                if (mainHand.isEmpty()) {
                    event.getEntity().setItemInHand(InteractionHand.MAIN_HAND, sappyBrush);
                } else if (!event.getEntity().getInventory().add(sappyBrush)) {
                    event.getEntity().drop(sappyBrush, false);
                }
                damageSapBottle(offHand, event.getEntity());
            }
            event.setCanceled(true);
            event.setCancellationResult(net.minecraft.world.InteractionResult.sidedSuccess(event.getLevel().isClientSide));
            return;
        }

        if (!mainHand.is(FIItems.SAPPY_BRUSH.get())) {
            return;
        }

        Block target = event.getLevel().getBlockState(event.getPos()).getBlock();
        Block sealed = SEALED_PLANKS.get(target);
        if (sealed == null) {
            return;
        }

        if (!event.getLevel().isClientSide) {
            event.getLevel().setBlock(event.getPos(), sealed.defaultBlockState(), 3);
            mainHand.hurtAndBreak(1, event.getEntity(), p -> p.broadcastBreakEvent(InteractionHand.MAIN_HAND));
            if (mainHand.isEmpty()) {
                event.getEntity().setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BRUSH));
            }
        }
        event.setCanceled(true);
        event.setCancellationResult(net.minecraft.world.InteractionResult.sidedSuccess(event.getLevel().isClientSide));
    }

    @SubscribeEvent
    public static void onFuelBurnTime(FurnaceFuelBurnTimeEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.is(
                net.minecraft.tags.ItemTags.create(ForagersInsight.rl("sealed_planks"))
        )) {
            event.setBurnTime(600);
        }
    }

    private static void damageSapBottle(ItemStack sapBottle, net.minecraft.world.entity.player.Player player) {
        sapBottle.setDamageValue(sapBottle.getDamageValue() + 1);
        if (sapBottle.getDamageValue() >= sapBottle.getMaxDamage()) {
            player.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(Items.GLASS_BOTTLE));
        }
    }
}