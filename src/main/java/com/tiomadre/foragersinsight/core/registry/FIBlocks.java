package com.tiomadre.foragersinsight.core.registry;

import com.teamabnormals.blueprint.common.block.sign.BlueprintCeilingHangingSignBlock;
import com.teamabnormals.blueprint.common.block.sign.BlueprintStandingSignBlock;
import com.teamabnormals.blueprint.common.block.sign.BlueprintWallHangingSignBlock;
import com.teamabnormals.blueprint.common.block.sign.BlueprintWallSignBlock;
import com.tiomadre.foragersinsight.common.block.*;
import com.tiomadre.foragersinsight.common.block.feasts.RainbowSandwichFeastBlock;
import com.tiomadre.foragersinsight.core.ForagersInsight;
import com.teamabnormals.blueprint.common.block.LogBlock;
import com.teamabnormals.blueprint.core.util.registry.BlockSubRegistryHelper;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;
import vectorwing.farmersdelight.common.block.CabinetBlock;
import vectorwing.farmersdelight.common.block.MushroomColonyBlock;
import vectorwing.farmersdelight.common.registry.ModBlocks;
import static com.tiomadre.foragersinsight.core.registry.FIItems.*;
import static net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy;

@Mod.EventBusSubscriber(modid = ForagersInsight.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class FIBlocks {
    public static final BlockSubRegistryHelper HELPER = ForagersInsight.REGISTRY_HELPER.getBlockSubHelper();
    private static BlockBehaviour.Properties vanillaLeafProperties(Block vanillaLeaf) {
        return copy(vanillaLeaf)
                .noOcclusion()
                .isValidSpawn((state, level, pos, type) -> false)
                .isSuffocating((state, level, pos) -> false)
                .isViewBlocking((state, level, pos) -> false);
    }

    //Cakes and Feasts
    public static final RegistryObject<Block> ACORN_CARROT_CAKE = HELPER.createBlockNoItem("acorn_carrot_cake",
            () -> new SliceableCakeBlock(copy(Blocks.CAKE), FIItems.SLICE_OF_ACORN_CARROT_CAKE));
    public static final RegistryObject<Block> RAINBOW_SANDWICH = HELPER.createBlockNoItem("rainbow_sandwich",
            () -> new RainbowSandwichFeastBlock(copy(Blocks.CAKE).noOcclusion(), FIItems.SLICE_OF_RAINBOW_SANDWICH));
    //Crops
        //Flower
    public static final RegistryObject<Block> POPPY_BUSH = HELPER.createBlockNoItem("poppy_bush", () ->
            new PoppyBushBlock(copy(Blocks.BEETROOTS)));
    public static final RegistryObject<Block> DANDELION_BUSH = HELPER.createBlockNoItem("dandelion_bush", () ->
            new DandelionBushBlock(copy(Blocks.POTATOES)));
    public static final RegistryObject<Block> ROSE_CROP = HELPER.createBlockNoItem("rose_crop", () -> new RoseCropBlock(
            copy(Blocks.WHEAT), 3));
    public static final RegistryObject<Block> ROSELLE_CROP = HELPER.createBlockNoItem("roselle_crop", () -> new RoselleCropBlock(
            copy(Blocks.WHEAT), 3));

    //Mushrooms
    public static final RegistryObject<Block> BLEWIT_MUSHROOM_COLONY = HELPER.createBlock("blewit_mushroom_colony", () ->
            new MushroomColonyBlock(copy(Blocks.RED_MUSHROOM), FIItems.BLEWIT_MUSHROOM));

    public static final RegistryObject<Block> BLEWIT_MUSHROOM = HELPER.createBlockNoItem("blewit_mushroom", () ->
            new WildMushroomBlock(copy(Blocks.RED_MUSHROOM), BLEWIT_MUSHROOM_COLONY));
    //Trees
    //Oak
    public static final RegistryObject<Block> BOUNTIFUL_OAK_LEAVES = HELPER.createBlock("bountiful_oak_leaves", () ->
            new BountifulLeavesBlock(vanillaLeafProperties(Blocks.OAK_LEAVES), () -> Items.APPLE));
    //Dark Oak
    public static final RegistryObject<Block> BOUNTIFUL_DARK_OAK_LEAVES = HELPER.createBlock("bountiful_dark_oak_leaves", () ->
            new BountifulLeavesBlock(vanillaLeafProperties(Blocks.DARK_OAK_LEAVES), BLACK_ACORN));
    //Spruce
    public static final RegistryObject<Block> BOUNTIFUL_SPRUCE_LEAVES = HELPER.createBlock("bountiful_spruce_leaves", () ->
            new BountifulSpruceLeavesBlock(vanillaLeafProperties(Blocks.SPRUCE_LEAVES)));
    public static final RegistryObject<Block> BOUNTIFUL_SPRUCE_TIPS = HELPER.createBlockNoItem("bountiful_spruce_tips", () ->
            new SpruceTipBlock(copy(Blocks.SWEET_BERRY_BUSH).noCollission()));
    //Birch
    public static final RegistryObject<Block> SAPPY_BIRCH_LOG = HELPER.createFuelBlock("sappy_birch_log", () ->
            new LogBlock(FIBlocks.STRIPPED_SAPPY_BIRCH_LOG, copy(Blocks.BIRCH_LOG)), 300);
    public static final RegistryObject<Block> STRIPPED_SAPPY_BIRCH_LOG = HELPER.createFuelBlock("stripped_sappy_birch_log", () ->
            new RotatedPillarBlock(copy(Blocks.STRIPPED_BIRCH_LOG)), 300);
    //Lilac
    public static final RegistryObject<Block> LILAC_LEAVES = HELPER.createBlock("lilac_leaves", () ->
            new LeavesBlock(vanillaLeafProperties(Blocks.AZALEA_LEAVES)));
    public static final RegistryObject<Block> BLOSSOMING_LILAC_LEAVES = HELPER.createBlock("blossoming_lilac_leaves", () ->
            new BlossomingLilacLeavesBlock(vanillaLeafProperties(Blocks.FLOWERING_AZALEA_LEAVES)));
    public static final RegistryObject<Block> HANGING_LILAC_LEAVES = HELPER.createBlockNoItem("hanging_lilac_leaves", () ->
            new HangingLilacLeavesBlock(copy(Blocks.AZALEA_LEAVES).noCollission()));
    public static final RegistryObject<Block> LILAC_LOG = HELPER.createFuelBlock("lilac_log", () ->
            new ThinLogBlock(FIBlocks.STRIPPED_LILAC_LOG, copy(Blocks.OAK_LOG).noOcclusion()), 300);
    public static final RegistryObject<Block> STRIPPED_LILAC_LOG = HELPER.createFuelBlock("stripped_lilac_log", () ->
            new ThinLogBlock(copy(Blocks.STRIPPED_OAK_LOG).noOcclusion()), 300);
    //Wood Stuff
        //Lilac
    public static final RegistryObject<Block> LILAC_PLANKS = HELPER.createBlock("lilac_planks", () ->
            new Block(copy(Blocks.OAK_PLANKS)));
    public static final RegistryObject<Block> LILAC_STAIRS = HELPER.createBlock("lilac_stairs", () ->
            new StairBlock(LILAC_PLANKS.get().defaultBlockState(), copy(Blocks.OAK_STAIRS)));
    public static final RegistryObject<Block> LILAC_SLAB = HELPER.createBlock("lilac_slab", () ->
            new SlabBlock(copy(Blocks.OAK_SLAB)));
    public static final RegistryObject<Block> LILAC_FENCE = HELPER.createBlock("lilac_fence", () ->
            new FenceBlock(copy(Blocks.OAK_FENCE)));
    public static final RegistryObject<Block> LILAC_FENCE_GATE = HELPER.createBlock("lilac_fence_gate", () ->
            new FenceGateBlock(copy(Blocks.OAK_FENCE_GATE), FIWoodTypes.LILAC));
    public static final RegistryObject<Block> LILAC_DOOR = HELPER.createBlock("lilac_door", () ->
            new DoorBlock(copy(Blocks.OAK_DOOR), FIWoodTypes.LILAC.setType()));
    public static final RegistryObject<Block> LILAC_TRAPDOOR = HELPER.createBlock("lilac_trapdoor", () ->
            new TrapDoorBlock(copy(Blocks.OAK_TRAPDOOR), FIWoodTypes.LILAC.setType()));
    public static final RegistryObject<Block> LILAC_PRESSURE_PLATE = HELPER.createBlock("lilac_pressure_plate", () ->
            new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, copy(Blocks.OAK_PRESSURE_PLATE), FIWoodTypes.LILAC.setType()));
    public static final RegistryObject<Block> LILAC_BUTTON = HELPER.createBlock("lilac_button", () ->
            new ButtonBlock(copy(Blocks.OAK_BUTTON), FIWoodTypes.LILAC.setType(), 30, true));
    public static final RegistryObject<BlueprintStandingSignBlock> LILAC_SIGN = HELPER.createBlockNoItem("lilac_sign", () ->
            new BlueprintStandingSignBlock(copy(Blocks.OAK_SIGN), FIWoodTypes.LILAC));
    public static final RegistryObject<BlueprintWallSignBlock> LILAC_WALL_SIGN = HELPER.createBlockNoItem("lilac_wall_sign", () ->
            new BlueprintWallSignBlock(copy(Blocks.OAK_WALL_SIGN), FIWoodTypes.LILAC));
    public static final RegistryObject<BlueprintCeilingHangingSignBlock> LILAC_HANGING_SIGN = HELPER.createBlockNoItem("lilac_hanging_sign", () ->
            new BlueprintCeilingHangingSignBlock(copy(Blocks.OAK_HANGING_SIGN), FIWoodTypes.LILAC));
    public static final RegistryObject<BlueprintWallHangingSignBlock> LILAC_WALL_HANGING_SIGN = HELPER.createBlockNoItem("lilac_wall_hanging_sign", () ->
            new BlueprintWallHangingSignBlock(copy(Blocks.OAK_WALL_HANGING_SIGN), FIWoodTypes.LILAC));
    public static final RegistryObject<CabinetBlock> LILAC_CABINET = HELPER.createBlock("lilac_cabinet", () ->
            new CabinetBlock(copy(ModBlocks.OAK_CABINET.get())));
        //Sealed Planks
    public static final RegistryObject<Block> SEALED_OAK_PLANKS = HELPER.createBlock("sealed_oak_planks", () ->
                new Block(copy(Blocks.OAK_PLANKS)));
    public static final RegistryObject<Block> SEALED_SPRUCE_PLANKS = HELPER.createBlock("sealed_spruce_planks", () ->
            new Block(copy(Blocks.SPRUCE_PLANKS)));
    public static final RegistryObject<Block> SEALED_BIRCH_PLANKS = HELPER.createBlock("sealed_birch_planks", () ->
            new Block(copy(Blocks.BIRCH_PLANKS)));
    public static final RegistryObject<Block> SEALED_JUNGLE_PLANKS = HELPER.createBlock("sealed_jungle_planks", () ->
            new Block(copy(Blocks.JUNGLE_PLANKS)));
    public static final RegistryObject<Block> SEALED_ACACIA_PLANKS = HELPER.createBlock("sealed_acacia_planks", () ->
            new Block(copy(Blocks.ACACIA_PLANKS)));
    public static final RegistryObject<Block> SEALED_DARK_OAK_PLANKS = HELPER.createBlock("sealed_dark_oak_planks", () ->
            new Block(copy(Blocks.DARK_OAK_PLANKS)));
    public static final RegistryObject<Block> SEALED_MANGROVE_PLANKS = HELPER.createBlock("sealed_mangrove_planks", () ->
            new Block(copy(Blocks.MANGROVE_PLANKS)));
    public static final RegistryObject<Block> SEALED_CHERRY_PLANKS = HELPER.createBlock("sealed_cherry_planks", () ->
            new Block(copy(Blocks.CHERRY_PLANKS)));
    public static final RegistryObject<Block> SEALED_BAMBOO_PLANKS = HELPER.createBlock("sealed_bamboo_planks", () ->
            new Block(copy(Blocks.BAMBOO_PLANKS)));
    public static final RegistryObject<Block> SEALED_CRIMSON_PLANKS = HELPER.createBlock("sealed_crimson_planks", () ->
            new Block(copy(Blocks.CRIMSON_PLANKS)));
    public static final RegistryObject<Block> SEALED_WARPED_PLANKS = HELPER.createBlock("sealed_warped_planks", () ->
            new Block(copy(Blocks.WARPED_PLANKS)));
    public static final RegistryObject<Block> SEALED_LILAC_PLANKS = HELPER.createBlock("sealed_lilac_planks", () ->
            new Block(copy(Blocks.OAK_PLANKS)));


        //Syrup Tap
    public static final RegistryObject<Block> TAPPER = HELPER.createBlockNoItem("tapper", () ->
            new TapperBlock(copy(Blocks.IRON_BLOCK).noOcclusion()));
    //Diffuser
    public static final RegistryObject<Block> DIFFUSER = HELPER.createBlockNoItem("diffuser",
            () -> new DiffuserBlock(BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK).noOcclusion()));
    //DECORATIVE
        //Foliage Mats
    public static final RegistryObject<Block> SCATTERED_LILAC_BLOOM_MAT = HELPER.createBlock("scattered_lilac_blooms", FoliageMatBlock::new);
    public static final RegistryObject<Block> SCATTERED_ROSE_PETAL_MAT = HELPER.createBlock("scattered_rose_petals", FoliageMatBlock::new);
    public static final RegistryObject<Block> SCATTERED_ROSELLE_PETAL_MAT = HELPER.createBlock("scattered_roselle_petals", FoliageMatBlock::new);
    public static final RegistryObject<Block> SCATTERED_SPRUCE_TIP_MAT = HELPER.createBlock("scattered_spruce_tips", FoliageMatBlock::new);
    public static final RegistryObject<Block> SCATTERED_STRAW_MAT = HELPER.createBlock("scattered_straw", FoliageMatBlock::new);
    public static final RegistryObject<Block> DENSE_LILAC_BLOOM_MAT = HELPER.createBlock("dense_lilac_blooms", FoliageMatBlock::new);
    public static final RegistryObject<Block> DENSE_STRAW_MAT = HELPER.createBlock("dense_straw", FoliageMatBlock::new);
    public static final RegistryObject<Block> DENSE_SPRUCE_TIP_MAT = HELPER.createBlock("dense_spruce_tips", FoliageMatBlock::new);
    public static final RegistryObject<Block> DENSE_ROSELLE_PETAL_MAT = HELPER.createBlock("dense_roselle_petals", FoliageMatBlock::new);
    public static final RegistryObject<Block> DENSE_ROSE_PETAL_MAT = HELPER.createBlock("dense_rose_petals", FoliageMatBlock::new);
        //Wildflowers + Flora
    public static final RegistryObject<Block> ROSELLE_BUSH = HELPER.createBlockNoItem("roselle_bush", () ->
          new TallFlowerBlock(copy(Blocks.LILAC)));
    public static final RegistryObject<Block> STOUT_BEACH_ROSE_BUSH = HELPER.createBlockNoItem("stout_beach_rose_bush", () ->
            new SandyFlowerBlock(() -> MobEffects.REGENERATION, 5, copy(Blocks.ROSE_BUSH)));
    public static final RegistryObject<Block> TALL_BEACH_ROSE_BUSH = HELPER.createBlockNoItem("tall_beach_rose_bush", () ->
            new TallSandyFlowerBlock(copy(Blocks.ROSE_BUSH)));
    public static final RegistryObject<Block> WOODLAND_FERN = HELPER.createBlockNoItem("woodland_fern", () ->
            new GrassBlock(copy(Blocks.FERN)));
    public static final RegistryObject<Block> GHOST_PIPE = HELPER.createBlockNoItem("ghost_pipe", () ->
            new BushBlock(copy(Blocks.ALLIUM).lightLevel(state -> 8).noCollission()));
        //Other
    public static final RegistryObject<Block> SUSPICIOUS_LEAF_LITTER = HELPER.createBlock("suspicious_leaf_litter", SuspiciousLitterBlock::new);
    public static final RegistryObject<Block> HOLLOW_LOG = HELPER.createFuelBlock("hollow_log", () ->
            new HollowLogBlock(copy(Blocks.OAK_LOG).noOcclusion()), 300);


    //STORAGE
        //Crop Crates and Sacks
    public static final RegistryObject<Block> APPLE_CRATE = HELPER.createBlock("apple_crate", () ->
            new Block(copy(ModBlocks.BEETROOT_CRATE.get())));
    public static final RegistryObject<Block> DANDELION_ROOT_SACK = HELPER.createBlock("dandelion_root_sack", () ->
            new Block(copy(ModBlocks.RICE_BAG.get())));
    public static final RegistryObject<Block> LILAC_BLOOM_CRATE = HELPER.createBlock("lilac_crate", () ->
            new SlabBlock(copy(Blocks.OAK_SLAB)));
    public static final RegistryObject<Block> POPPY_SEEDS_SACK = HELPER.createBlock("poppy_seeds_sack", () ->
            new Block(copy(ModBlocks.RICE_BAG.get())));
    public static final RegistryObject<Block> ROSE_HIP_SACK = HELPER.createBlock("rose_hip_sack", () -> new Block(
            copy(ModBlocks.RICE_BAG.get())));
    public static final RegistryObject<Block> ROSELLE_CALYX_SACK = HELPER.createBlock("roselle_calyx_sack", () -> new Block(
            copy(ModBlocks.RICE_BAG.get())));
    public static final RegistryObject<Block> BLACK_ACORN_SACK = HELPER.createBlock("black_acorn_sack", () -> new Block(
            copy(ModBlocks.RICE_BAG.get())));
    public static final RegistryObject<Block> SPRUCE_TIPS_SACK = HELPER.createBlock("spruce_tips_sack", () -> new Block(
            copy(ModBlocks.RICE_BAG.get())));
    public static final RegistryObject<Block> BLEWIT_CRATE = HELPER.createBlock("blewit_mushroom_crate", () ->
            new SlabBlock(copy(Blocks.OAK_SLAB)));
}