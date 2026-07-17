package com.tiomadre.foragersinsight.core.other;

import com.tiomadre.foragersinsight.core.ForagersInsight;
import com.tiomadre.foragersinsight.core.other.toolevents.WaxedBoots;
import com.tiomadre.foragersinsight.core.registry.FIMobEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = ForagersInsight.MOD_ID)
public class StickyResistanceEvents {
    private static final int SPEED_BOOST_DURATION = 60;
    private static final String SPEED_BOOST_END_TICK_TAG = "ForagersInsightStickyResistanceSpeedEndTick";
    private static final UUID SPEED_BOOST_MODIFIER_UUID = UUID.fromString("8b341286-6da1-4a7d-a35b-4fb71d37d678");
    private static final AttributeModifier SPEED_BOOST_MODIFIER = new AttributeModifier(
            SPEED_BOOST_MODIFIER_UUID,
            "Sticky resistance speed boost",
            0.1D,
            AttributeModifier.Operation.MULTIPLY_TOTAL
    );

    @SubscribeEvent
    public static void onMobEffectApplicable(MobEffectEvent.Applicable event) {
        MobEffect incomingEffect = event.getEffectInstance().getEffect();
        if (incomingEffect != FIMobEffects.STUCK.get()) return;
        if (!event.getEntity().hasEffect(FIMobEffects.STICKY_RESISTANCE.get())) return;

        WaxedBoots.drainForStuckPrevention(event.getEntity());
        event.setResult(Event.Result.DENY);
    }

    @SubscribeEvent
    public static void onMobEffectAdded(MobEffectEvent.Added event) {
        LivingEntity entity = event.getEntity();
        MobEffect addedEffect = event.getEffectInstance().getEffect();
        if (addedEffect != FIMobEffects.STICKY_RESISTANCE.get()) return;
        if (!entity.hasEffect(FIMobEffects.STUCK.get())) return;

        entity.removeEffect(FIMobEffects.STUCK.get());
        applySpeedBoost(entity);
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide()) return;

        long speedEndTick = entity.getPersistentData().getLong(SPEED_BOOST_END_TICK_TAG);
        if (speedEndTick == 0L || entity.level().getGameTime() < speedEndTick) return;

        removeSpeedBoost(entity);
        entity.getPersistentData().remove(SPEED_BOOST_END_TICK_TAG);
    }

    private static void applySpeedBoost(LivingEntity entity) {
        AttributeInstance movementSpeed = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeed == null) return;

        removeSpeedBoost(entity);
        movementSpeed.addTransientModifier(SPEED_BOOST_MODIFIER);
        entity.getPersistentData().putLong(
                SPEED_BOOST_END_TICK_TAG,
                entity.level().getGameTime() + SPEED_BOOST_DURATION
        );
    }

    private static void removeSpeedBoost(LivingEntity entity) {
        AttributeInstance movementSpeed = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeed == null || movementSpeed.getModifier(SPEED_BOOST_MODIFIER_UUID) == null) return;

        movementSpeed.removeModifier(SPEED_BOOST_MODIFIER_UUID);
    }
}