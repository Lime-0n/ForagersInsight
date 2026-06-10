package com.tiomadre.foragersinsight.core.other;

import com.tiomadre.foragersinsight.core.registry.FIAdvancementCriteria;
import com.tiomadre.foragersinsight.core.registry.FIItems;
import com.tiomadre.foragersinsight.core.registry.FIMobEffects;
import com.tiomadre.foragersinsight.core.ForagersInsight;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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
        //Advancement Triggers
    }
    @SubscribeEvent
    public static void onMobEffectAdded(MobEffectEvent.Added event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (event.getEffectInstance().getEffect() != FIMobEffects.ODOROUS.get()) return;

        FIAdvancementCriteria.STINKY_SITUATION.trigger(player);
    }

    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!event.getCrafting().is(FIItems.TAPPER.get())) return;

        FIAdvancementCriteria.TAP_THAT.trigger(player);
    }
    //Stuck Effect Logico
    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (!entity.hasEffect(FIMobEffects.STUCK.get())) return;

        entity.setDeltaMovement(0.0D, Math.min(entity.getDeltaMovement().y, 0.0D), 0.0D);
        entity.hurtMarked = true;

        if (entity instanceof Player player) {
            player.xxa = 0.0F;
            player.zza = 0.0F;
            player.setJumping(false);
        }
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
}