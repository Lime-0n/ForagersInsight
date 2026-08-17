package com.tiomadre.foragersinsight.core.registry;

import com.tiomadre.foragersinsight.common.effect.BloomEffect;
import com.tiomadre.foragersinsight.common.effect.MedicinalEffect;
import com.tiomadre.foragersinsight.common.effect.OdorousEffect;
import com.tiomadre.foragersinsight.common.effect.StuckEffect;
import com.tiomadre.foragersinsight.common.effect.StickyResistanceEffect;
import com.tiomadre.foragersinsight.core.ForagersInsight;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;


public class FIMobEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, ForagersInsight.MOD_ID);
    public static final DeferredHolder<MobEffect, MedicinalEffect> MEDICINAL = MOB_EFFECTS.register("medicinal", MedicinalEffect::new);
    public static final DeferredHolder<MobEffect, BloomEffect> BLOOM = MOB_EFFECTS.register("bloom", BloomEffect::new);
    public static final DeferredHolder<MobEffect, OdorousEffect> ODOROUS = MOB_EFFECTS.register("odorous", OdorousEffect::new);
    public static final DeferredHolder<MobEffect, StuckEffect> STUCK = MOB_EFFECTS.register("stuck", StuckEffect::new);
    public static final DeferredHolder<MobEffect, StickyResistanceEffect> STICKY_RESISTANCE = MOB_EFFECTS.register("sticky_resistance", StickyResistanceEffect::new);

}