package com.tiomadre.foragersinsight.core.other;

import com.tiomadre.foragersinsight.common.block.WallMushroomBlock;
import com.tiomadre.foragersinsight.core.ForagersInsight;
import com.tiomadre.foragersinsight.core.registry.FIBlocks;
import com.tiomadre.foragersinsight.core.registry.FIItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = ForagersInsight.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WMCEvents {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlaceMushroomOnWall(RightClickBlock event) {
        Direction face = event.getFace();
        if (face == null || !face.getAxis().isHorizontal()) {
            return;
        }

        ItemStack stack = event.getItemStack();
        RegistryObject<Block> wallMushroom = getWallMushroom(stack.getItem());
        if (wallMushroom == null) {
            return;
        }

        Level level = event.getLevel();
        Player player = event.getEntity();
        BlockPos placePos = event.getPos().relative(face);
        BlockState stateAtPlacePos = level.getBlockState(placePos);
        if (!stateAtPlacePos.canBeReplaced()) {
            return;
        }

        BlockState wallState = wallMushroom.get().defaultBlockState().setValue(WallMushroomBlock.FACING, face);
        if (!wallState.canSurvive(level, placePos) || !level.mayInteract(player, placePos)) {
            return;
        }

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));

        if (level.isClientSide) {
            return;
        }

        level.setBlock(placePos, wallState, Block.UPDATE_ALL);
        SoundType sound = wallState.getSoundType(level, placePos, player);
        level.playSound(null, placePos, sound.getPlaceSound(), SoundSource.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
    }

    private static RegistryObject<Block> getWallMushroom(Item item) {
        if (item == Items.RED_MUSHROOM) {
            return FIBlocks.WALL_RED_MUSHROOM;
        }
        if (item == Items.BROWN_MUSHROOM) {
            return FIBlocks.WALL_BROWN_MUSHROOM;
        }
        if (item == FIItems.BLEWIT_MUSHROOM.get()) {
            return FIBlocks.WALL_BLEWIT_MUSHROOM;
        }
        return null;
    }
}