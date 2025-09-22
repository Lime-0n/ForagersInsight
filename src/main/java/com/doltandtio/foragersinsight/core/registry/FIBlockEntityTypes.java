package com.doltandtio.foragersinsight.core.registry;

import com.doltandtio.foragersinsight.common.block.entity.PotpourriBlockEntity;
import com.doltandtio.foragersinsight.core.ForagersInsight;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class FIBlockEntityTypes {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ForagersInsight.MOD_ID);

    public static final RegistryObject<BlockEntityType<PotpourriBlockEntity>> POTPOURRI = BLOCK_ENTITY_TYPES.register(
            "potpourri",
            () -> BlockEntityType.Builder.of(PotpourriBlockEntity::new, FIBlocks.POTPOURRI.get()).build(null)
    );

    public static void register(IEventBus bus) {
        BLOCK_ENTITY_TYPES.register(bus);
    }
}