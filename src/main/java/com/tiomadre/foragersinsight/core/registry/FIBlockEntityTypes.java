package com.tiomadre.foragersinsight.core.registry;

import com.tiomadre.foragersinsight.common.block.entity.DiffuserBlockEntity;
import com.tiomadre.foragersinsight.core.ForagersInsight;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class FIBlockEntityTypes {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ForagersInsight.MOD_ID);

    public static final RegistryObject<BlockEntityType<DiffuserBlockEntity>> DIFFUSER =
            BLOCK_ENTITY_TYPES.register("diffuser",
                    () -> BlockEntityType.Builder.of(DiffuserBlockEntity::new, FIBlocks.DIFFUSER.get()).build(null));

    public static void register(IEventBus bus) {
        BLOCK_ENTITY_TYPES.register(bus);
    }
}