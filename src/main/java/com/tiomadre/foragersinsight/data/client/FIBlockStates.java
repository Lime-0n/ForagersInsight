package com.tiomadre.foragersinsight.data.client;

import com.tiomadre.foragersinsight.common.block.*;
import com.tiomadre.foragersinsight.core.ForagersInsight;
import com.tiomadre.foragersinsight.core.registry.FIItems;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.registries.RegistryObject;
import static com.tiomadre.foragersinsight.core.registry.FIBlocks.*;

public class FIBlockStates extends FIBlockStatesHelper {
    public FIBlockStates(GatherDataEvent e) {
        super(e.getGenerator().getPackOutput(), ForagersInsight.MOD_ID, e.getExistingFileHelper());
    }
    @Override
    protected void registerStatesAndModels() {
        //Cakes and Feasts
        this.sliceableCake();

        //Flower Crops
        this.RoseCrop(ROSE_CROP);
        this.RoselleCrop(ROSELLE_CROP);
        this.age5Crop(DANDELION_BUSH, FIItems.DANDELION_ROOT);
        this.age5Crop(POPPY_BUSH, FIItems.POPPY_SEEDS);

        //Sacks
        this.sackBlock(BLACK_ACORN_SACK);
        this.sackBlock(ROSE_HIP_SACK);
        this.sackBlock(ROSELLE_CALYX_SACK);
        this.sackBlock(SPRUCE_TIPS_SACK);
        this.sackBlock(POPPY_SEEDS_SACK);
        this.sackBlock(DANDELION_ROOT_SACK);

        //Saplings and Tree Crops
        this.crossCutout(BOUNTIFUL_OAK_SAPLING);
        this.bountifulLeaves(BOUNTIFUL_OAK_LEAVES, Blocks.OAK_LEAVES);
        this.crossCutout(BOUNTIFUL_DARK_OAK_SAPLING);
        this.bountifulLeaves(BOUNTIFUL_DARK_OAK_LEAVES, Blocks.DARK_OAK_LEAVES);
        this.bountifulLeaves(BOUNTIFUL_SPRUCE_LEAVES, Blocks.SPRUCE_LEAVES);
        this.spruceTipBlock();
        this.axisBlock((RotatedPillarBlock) SAPPY_BIRCH_LOG.get(), modTexture("sappy_birch_log"), mcLoc("block/birch_log_top"));
        this.blockItem(SAPPY_BIRCH_LOG.get());
        this.crossCutout(BOUNTIFUL_SPRUCE_SAPLING);

        //Foliage Mats
        this.matBlock(SCATTERED_ROSE_PETAL_MAT, "scattered_rose_petals");
        this.matBlock(SCATTERED_ROSELLE_PETAL_MAT, "scattered_roselle_petals");
        this.matBlock(SCATTERED_SPRUCE_TIP_MAT, "scattered_spruce_tips");
        this.matBlock(SCATTERED_STRAW_MAT, "scattered_straw");
        this.matBlock(DENSE_SPRUCE_TIP_MAT, "dense_spruce_tips");
        this.matBlock(DENSE_ROSE_PETAL_MAT, "dense_rose_petals");
        this.matBlock(DENSE_ROSELLE_PETAL_MAT, "dense_roselle_petals");
        this.matBlock(DENSE_STRAW_MAT, "dense_straw");

        //Diffuser n Tapper
        this.diffuserBlock();
        this.tapperBlock();

        //Wildflowers
        this.crossCutout(STOUT_BEACH_ROSE_BUSH);
        this.doubleCrossCutout(ROSELLE_BUSH);
        this.doubleCrossCutout(TALL_BEACH_ROSE_BUSH);

    }
    private void age5Crop(RegistryObject<Block> crop, RegistryObject<Item> seeds) {
        CropBlock cropBlock = (CropBlock) crop.get();
        VariantBlockStateBuilder builder = this.getVariantBuilder(cropBlock);
        IntegerProperty age = (IntegerProperty) cropBlock.getStateDefinition().getProperty("age");

        for (int i = 0; i <= cropBlock.getMaxAge(); i++) {
            builder.partialState().with(age, i)
                    .modelForState()
                    .modelFile(models().cross("%s_stage%d".formatted(name(cropBlock), i),
                            concatRL(blockTexture(cropBlock), "_stage%d".formatted(i))).renderType("cutout"))
                    .addModel();
        }
        this.itemModels().basicItem(seeds.get());
    }
    private void diffuserBlock() {
        Block diffuser = DIFFUSER.get();

        BlockModelBuilder model = this.models().getBuilder(name(diffuser))
                .parent(new ModelFile.UncheckedModelFile(mcLoc("block/block")))
                .texture("1", modTexture("diffuser_bottom"))
                .texture("2", modTexture("diffuser_side"))
                .texture("3", modTexture("diffuser_top"))
                .texture("particle", modTexture("diffuser_side"));

        model.element().from(4f, 0f, 4f).to(12f, 12f, 12f)
                .face(Direction.NORTH).uvs(4f, 4f, 12f, 16f).texture("#2").end()
                .face(Direction.EAST).uvs(4f, 4f, 12f, 16f).texture("#2").end()
                .face(Direction.SOUTH).uvs(4f, 4f, 12f, 16f).texture("#2").end()
                .face(Direction.WEST).uvs(4f, 4f, 12f, 16f).texture("#2").end()
                .face(Direction.UP).uvs(4f, 4f, 12f, 12f).texture("#3").end()
                .face(Direction.DOWN).uvs(4f, 4f, 12f, 12f).texture("#1").end()
                .end();

        model.element().from(9f, 12f, 6f).to(10f, 14f, 10f)
                .face(Direction.NORTH).uvs(9f, 2f, 10f, 4f).texture("#2").end()
                .face(Direction.EAST).uvs(6f, 2f, 10f, 4f).texture("#2").end()
                .face(Direction.SOUTH).uvs(6f, 2f, 7f, 4f).texture("#2").end()
                .face(Direction.WEST).uvs(6f, 2f, 10f, 4f).texture("#2").end()
                .face(Direction.UP).uvs(10f, 3f, 6f, 2f).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).texture("#2").end()
                .face(Direction.DOWN).uvs(6f, 2f, 10f, 4f).texture("#2").end()
                .end();

        model.element().from(6f, 12f, 6f).to(7f, 14f, 10f)
                .face(Direction.NORTH).uvs(9f, 2f, 10f, 4f).texture("#2").end()
                .face(Direction.EAST).uvs(6f, 2f, 10f, 4f).texture("#2").end()
                .face(Direction.SOUTH).uvs(6f, 2f, 7f, 4f).texture("#2").end()
                .face(Direction.WEST).uvs(6f, 2f, 10f, 4f).texture("#2").end()
                .face(Direction.UP).uvs(10f, 3f, 6f, 2f).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).texture("#2").end()
                .face(Direction.DOWN).uvs(6f, 2f, 10f, 4f).texture("#2").end()
                .end();

        model.element().from(7f, 12f, 6f).to(9f, 14f, 7f)
                .face(Direction.NORTH).uvs(7f, 2f, 9f, 4f).texture("#2").end()
                .face(Direction.EAST).uvs(7f, 2f, 9f, 4f).texture("#2").end()
                .face(Direction.SOUTH).uvs(7f, 2f, 9f, 4f).texture("#2").end()
                .face(Direction.WEST).uvs(7f, 2f, 9f, 4f).texture("#2").end()
                .face(Direction.UP).uvs(7f, 2f, 9f, 3f).texture("#2").end()
                .face(Direction.DOWN).uvs(7f, 2f, 9f, 4f).texture("#2").end()
                .end();

        model.element().from(7f, 12f, 9f).to(9f, 14f, 10f)
                .face(Direction.NORTH).uvs(7f, 2f, 9f, 4f).texture("#2").end()
                .face(Direction.EAST).uvs(7f, 2f, 9f, 4f).texture("#2").end()
                .face(Direction.SOUTH).uvs(7f, 2f, 9f, 4f).texture("#2").end()
                .face(Direction.WEST).uvs(7f, 2f, 9f, 4f).texture("#2").end()
                .face(Direction.UP).uvs(7f, 2f, 9f, 3f).texture("#2").end()
                .face(Direction.DOWN).uvs(7f, 2f, 9f, 4f).texture("#2").end()
                .end();

        this.simpleBlock(diffuser, model);
        this.blockItem(diffuser);
    }

    public void crossCutout(RegistryObject<? extends Block> cross) {
        this.simpleBlock(cross.get(), this.models().cross(name(cross.get()), this.blockTexture(cross.get()))
                .renderType("cutout"));
        this.generatedItem(cross.get(), "block");
    }
    public void doubleCrossCutout(RegistryObject<? extends Block> plant) {
        DoublePlantBlock block = (DoublePlantBlock) plant.get();
        ModelFile lower = this.models().cross(name(block) + "_lower", concatRL(this.blockTexture(block), "_lower"))
                .renderType("cutout");
        ModelFile upper = this.models().cross(name(block) + "_upper", concatRL(this.blockTexture(block), "_upper"))
                .renderType("cutout");

        this.getVariantBuilder(block)
                .partialState().with(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER)
                .modelForState().modelFile(lower).addModel();

        this.getVariantBuilder(block)
                .partialState().with(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER)
                .modelForState().modelFile(upper).addModel();

        this.itemModels().withExistingParent(name(block), mcLoc("item/generated"))
                .texture("layer0", concatRL(this.blockTexture(block), "_lower"));
}

    public void sackBlock(RegistryObject<? extends Block> block) {
        String name = name(block.get());
        this.simpleBlock(block.get(), models().cube(name,
                modTexture(name + "_bottom"), modTexture(name + "_top"),  modTexture(name + "_side"),
                modTexture(name + "_side_special"), modTexture(name + "_side"),
                modTexture(name + "_side")).texture("particle", modTexture(name + "_top")));

        this.blockItem(block.get());
    }

    public void crateBlock(RegistryObject<? extends Block> block, String cropName) {
        this.simpleBlock(block.get(),
                models().cubeBottomTop(name(block.get()), modTexture(cropName + "_crate_side"),
                        modTexture("crate_bottom"),
                        modTexture(cropName + "_crate_top")));


        this.blockItem(block.get());
    }

    private void bountifulLeaves(RegistryObject<? extends Block> block, Block base) {
        Block leaves = block.get();

        if (leaves instanceof BountifulLeavesBlock bountifulLeaves) {
            this.getVariantBuilder(bountifulLeaves).forAllStatesExcept(state -> {
                int age = bountifulLeaves.getAge(state);
                String stageName = "%s_stage%d".formatted(name(bountifulLeaves), age);

                ModelFile stageModel = models()
                        .withExistingParent(stageName, "foragersinsight:block/leaves_with_overlay")
                        .texture("all", blockTexture(base))
                        .texture("overlay", concatRL(blockTexture(bountifulLeaves), "_stage%d".formatted(age)));

                return ConfiguredModel.builder().modelFile(stageModel).build();
            }, LeavesBlock.DISTANCE, LeavesBlock.PERSISTENT, LeavesBlock.WATERLOGGED);

            this.itemModels().withExistingParent(name(bountifulLeaves), concatRL(blockTexture(bountifulLeaves), "_stage0"));
            return;
        }

        if (leaves instanceof LeavesBlock leavesBlock) {
            this.simpleBlock(leavesBlock, models().cubeAll(name(leavesBlock), blockTexture(leavesBlock)).renderType("cutout_mipped"));
            this.blockItem(leavesBlock);
        }

    }

    public void RoseCrop(RegistryObject<? extends Block> crop) {
        RoseCropBlock block = (RoseCropBlock) crop.get();

        this.getVariantBuilder(block).forAllStates(state -> {
            int age;
            String half;
            if (RoseCropBlock.isIllegalState(state)) {
                age = 0;
                half = "lower";
            }
            else {
                age = state.getValue(RoseCropBlock.AGE);
                half = state.getValue(RoseCropBlock.HALF) == DoubleBlockHalf.UPPER ? "upper" : "lower";
            }
            return ConfiguredModel.builder().modelFile(models().withExistingParent("rose_hip_stage%d_%s".formatted(age, half), "block/cross")
                    .texture("cross", concatRL(modTexture("rose_hip"), "_stage%d_%s".formatted(age, half))).renderType("cutout")).build();
        });

        this.itemModels().basicItem(crop.get().asItem());
    }
    public void RoselleCrop(RegistryObject<? extends Block> crop) {
        RoseCropBlock block = (RoseCropBlock) crop.get();

        this.getVariantBuilder(block).forAllStates(state -> {
            int age;
            String half;
            if (RoseCropBlock.isIllegalState(state)) {
                age = 0;
                half = "lower";
            }
            else {
                age = state.getValue(RoseCropBlock.AGE);
                half = state.getValue(RoseCropBlock.HALF) == DoubleBlockHalf.UPPER ? "upper" : "lower";
            }
            return ConfiguredModel.builder().modelFile(models().withExistingParent("roselle_crop_stage%d_%s".formatted(age, half), "block/cross")
                    .texture("cross", concatRL(modTexture("roselle_crop"), "_stage%d_%s".formatted(age, half))).renderType("cutout")).build();
        });

        this.itemModels().basicItem(crop.get().asItem());
    }

    public void matBlock(RegistryObject<? extends Block> block, String texture) {
        this.simpleBlock(block.get(),
                models().withExistingParent(name(block.get()), mcLoc("block/carpet"))
                        .texture("wool", modTexture(texture))
                        .renderType("cutout"));
        this.blockItem(block.get());
    }

    private void spruceTipBlock() {
        Block tip = ((RegistryObject<? extends Block>) com.tiomadre.foragersinsight.core.registry.FIBlocks.BOUNTIFUL_SPRUCE_TIPS).get();
        this.getVariantBuilder(tip).forAllStates(state -> ConfiguredModel.builder()
                .modelFile(models().cross("%s_stage%d".formatted(name(tip), state.getValue(SpruceTipBlock.AGE)),
                                concatRL(blockTexture(tip), "_stage%d".formatted(state.getValue(SpruceTipBlock.AGE))))
                        .renderType("cutout")).build());
    }


    private void tapperBlock() {
        Block tapper = TAPPER.get();

        ModelFile[] fillModels = new ModelFile[] {
                tapperModel(name(tapper), "bucket_top_stage0", "bucket_side", "knife_tap"),
                tapperModel(name(tapper) + "_stage1", "bucket_top_stage1", "bucket_side", "sappy_knife_tap"),
                tapperModel(name(tapper) + "_stage2", "bucket_top_stage2", "bucket_side", "sappy_knife_tap"),
                tapperModel(name(tapper) + "_stage3", "bucket_top_stage3", "bucket_side", "sappy_knife_tap"),
                tapperModel(name(tapper) + "_stage4", "bucket_top_stage4", "bucket_side_full", "sappy_knife_tap")
        };

        VariantBlockStateBuilder builder = this.getVariantBuilder(tapper);
        for (int fill = 0; fill < fillModels.length; fill++) {
            ModelFile model = fillModels[fill];
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                builder.partialState()
                        .with(TapperBlock.FILL, fill)
                        .with(TapperBlock.FACING, direction)
                        .modelForState()
                        .modelFile(model)
                        .rotationY((int) direction.getOpposite().toYRot())
                        .addModel();
            }
        }
    }

    private void sliceableCake() {
        SliceableCakeBlock cake = (SliceableCakeBlock) ACORN_CARROT_CAKE.get();
        String name = name(cake);

        VariantBlockStateBuilder builder = this.getVariantBuilder(cake);
        for (int bites = 0; bites < cake.getMaxBites(); bites++) {
            String parent = bites == 0 ? "block/cake" : "block/cake_slice" + bites;
            ModelFile model = this.models().withExistingParent(bites == 0 ? name : name + "_slice" + bites, mcLoc(parent))
                    .texture("particle", modTexture(name + "_side"))
                    .texture("bottom", modTexture(name + "_bottom"))
                    .texture("top", modTexture(name + "_top"))
                    .texture("side", modTexture(name + "_side"))
                    .texture("inner", modTexture(name + "_inner"));

            builder.partialState().with(SliceableCakeBlock.BITES, bites)
                    .modelForState().modelFile(model)
                    .addModel();
        }
    }


    private ModelFile tapperModel(String name, String bucketTop, String bucketSide, String tapTexture) {
        BlockModelBuilder builder = this.models().getBuilder(name)
                .texture("particle", modTexture(bucketTop))
                .texture("4", modTexture(bucketTop))
                .texture("7", modTexture("bucket_bottom"))
                .texture("8", modTexture(bucketSide))
                .texture("12", modTexture(tapTexture));

        builder.element().from(7.5f, 12f, 0f).to(8.5f, 15f, 6f)
                .face(Direction.NORTH).uvs(8f, 5f, 9f, 8f).texture("#12").end()
                .face(Direction.EAST).uvs(0f, 1f, 6f, 4f).texture("#12").end()
                .face(Direction.SOUTH).uvs(5f, 5f, 4f, 8f).texture("#12").end()
                .face(Direction.WEST).uvs(0f, 1f, 6f, 4f).texture("#12").end()
                .face(Direction.UP).uvs(6f, 1f, 0f, 2f).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).texture("#12").end()
                .face(Direction.DOWN).uvs(6f, 3f, 0f, 4f).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).texture("#12").end()
                .end();

        builder.element().from(7.5f, 13f, 6f).to(8.5f, 15f, 11f)
                .face(Direction.NORTH).uvs(10f, 1f, 9f, 3f).texture("#12").end()
                .face(Direction.EAST).uvs(10f, 3f, 5f, 1f).texture("#12").end()
                .face(Direction.SOUTH).uvs(9f, 1f, 10f, 3f).texture("#12").end()
                .face(Direction.WEST).uvs(5f, 3f, 10f, 1f).texture("#12").end()
                .face(Direction.UP).uvs(10f, 1f, 5f, 2f).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).texture("#12").end()
                .face(Direction.DOWN).uvs(10f, 1f, 5f, 2f).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).texture("#12").end()
                .end();

        builder.element().from(3.5f, 2f, 1f).to(12.5f, 11f, 10f)
                .face(Direction.NORTH).uvs(0f, 2f, 9f, 11f).texture("#8").end()
                .face(Direction.EAST).uvs(0f, 2f, 9f, 11f).texture("#8").end()
                .face(Direction.SOUTH).uvs(0f, 2f, 9f, 11f).texture("#8").end()
                .face(Direction.WEST).uvs(0f, 2f, 9f, 11f).texture("#8").end()
                .face(Direction.UP).uvs(0f, 0f, 9f, 9f).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).texture("#4").end()
                .face(Direction.DOWN).uvs(0f, 0f, 9f, 9f).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).texture("#7").end()
                .end();

        builder.element().from(11.5f, 11f, 5f).to(11.5f, 15f, 6f)
                .face(Direction.NORTH).uvs(10f, 6f, 11f, 1f).texture("#8").end()
                .face(Direction.EAST).uvs(10f, 5f, 11f, 1f).texture("#8").end()
                .face(Direction.SOUTH).uvs(10f, 6f, 11f, 1f).texture("#8").end()
                .face(Direction.WEST).uvs(10f, 5f, 11f, 1f).texture("#8").end()
                .face(Direction.UP).uvs(10f, 6f, 11f, 1f).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).texture("#8").end()
                .face(Direction.DOWN).uvs(10f, 5f, 11f, 1f).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).texture("#8").end()
                .end();

        builder.element().from(4.5f, 11f, 5f).to(4.5f, 15f, 6f)
                .face(Direction.NORTH).uvs(10f, 6f, 11f, 1f).texture("#8").end()
                .face(Direction.EAST).uvs(10f, 5f, 11f, 1f).texture("#8").end()
                .face(Direction.SOUTH).uvs(10f, 6f, 11f, 1f).texture("#8").end()
                .face(Direction.WEST).uvs(10f, 5f, 11f, 1f).texture("#8").end()
                .face(Direction.UP).uvs(10f, 6f, 11f, 1f).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).texture("#8").end()
                .face(Direction.DOWN).uvs(10f, 5f, 11f, 1f).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).texture("#8").end()
                .end();

        builder.element().from(4.5f, 15f, 5f).to(11.5f, 15f, 6f)
                .face(Direction.NORTH).uvs(10f, 1f, 11f, 10f).texture("#8").end()
                .face(Direction.EAST).uvs(10f, 1f, 11f, 10f).texture("#8").end()
                .face(Direction.SOUTH).uvs(10f, 1f, 11f, 10f).texture("#8").end()
                .face(Direction.WEST).uvs(10f, 1f, 11f, 10f).texture("#8").end()
                .face(Direction.UP).uvs(10f, 0f, 11f, 7f).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).texture("#8").end()
                .face(Direction.DOWN).uvs(10f, 0f, 11f, 7f).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).texture("#8").end()
                .end();

        return builder;
    }

}
