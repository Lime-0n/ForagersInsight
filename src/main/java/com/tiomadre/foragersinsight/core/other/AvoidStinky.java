package com.tiomadre.foragersinsight.core.other;

import com.tiomadre.foragersinsight.core.registry.FIMobEffects;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.player.Player;

public class AvoidStinky extends AvoidEntityGoal<Player> {
    public AvoidStinky(PathfinderMob mob, double walkSpeedModifier, double sprintSpeedModifier) {
        super(mob, Player.class, 8.0F, walkSpeedModifier, sprintSpeedModifier, player -> player.hasEffect(FIMobEffects.ODOROUS.get()));
    }
}