package com.tiomadre.foragersinsight.core.registry;

import com.tiomadre.foragersinsight.common.effect.BloomEffect;
import com.tiomadre.foragersinsight.common.effect.ChilledEffect;
import com.tiomadre.foragersinsight.common.effect.MedicinalEffect;
import com.tiomadre.foragersinsight.common.effect.OdorousEffect;
import com.tiomadre.foragersinsight.core.ForagersInsight;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class FIMobEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, ForagersInsight.MOD_ID);

    public static final RegistryObject<MobEffect> CHILLED = MOB_EFFECTS.register("chilled", ChilledEffect::new);
    public static final RegistryObject<MobEffect> MEDICINAL = MOB_EFFECTS.register("medicinal", MedicinalEffect::new);
    public static final RegistryObject<MobEffect> BLOOM = MOB_EFFECTS.register("bloom", BloomEffect::new);
    public static final RegistryObject<MobEffect> ODOROUS = MOB_EFFECTS.register("odorous", OdorousEffect::new);
}