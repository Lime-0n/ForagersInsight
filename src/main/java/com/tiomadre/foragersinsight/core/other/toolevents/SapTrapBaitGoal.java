package com.tiomadre.foragersinsight.core.other.toolevents;

import com.tiomadre.foragersinsight.common.block.entity.SapTrapBlockEntity;
import com.tiomadre.foragersinsight.common.item.BaitItem;
import com.tiomadre.foragersinsight.core.registry.FIBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;

import java.util.EnumSet;

public class SapTrapBaitGoal extends Goal {
    private static final int SEARCH_RANGE = 10;
    private static final double SPEED_MODIFIER = 1.0D;
    private final PathfinderMob mob;
    private BlockPos targetPos;

    public SapTrapBaitGoal(PathfinderMob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.mob.getTarget() != null || this.mob.getRandom().nextInt(20) != 0) {
            return false;
        }
        this.targetPos = this.findBaitedTrap();
        return this.targetPos != null;
    }

    @Override
    public boolean canContinueToUse() {
        return this.targetPos != null && this.isValidBaitedTrap(this.targetPos) && !this.mob.getNavigation().isDone();
    }

    @Override
    public void start() {
        this.mob.getNavigation().moveTo(this.targetPos.getX() + 0.5D, this.targetPos.getY(), this.targetPos.getZ() + 0.5D, SPEED_MODIFIER);
    }

    @Override
    public void tick() {
        if (this.targetPos != null) {
            this.mob.getLookControl().setLookAt(this.targetPos.getX() + 0.5D, this.targetPos.getY() + 0.1D, this.targetPos.getZ() + 0.5D);
        }
    }

    @Override
    public void stop() {
        this.targetPos = null;
    }

    private BlockPos findBaitedTrap() {
        Level level = this.mob.level();
        BlockPos origin = this.mob.blockPosition();
        BlockPos closest = null;
        double closestDistance = Double.MAX_VALUE;

        for (BlockPos pos : BlockPos.betweenClosed(origin.offset(-SEARCH_RANGE, -2, -SEARCH_RANGE), origin.offset(SEARCH_RANGE, 2, SEARCH_RANGE))) {
            if (!this.isValidBaitedTrap(pos)) {
                continue;
            }
            double distance = pos.distSqr(origin);
            if (distance < closestDistance) {
                closest = pos.immutable();
                closestDistance = distance;
            }
        }
        return closest;
    }

    private boolean isValidBaitedTrap(BlockPos pos) {
        Level level = this.mob.level();
        return level.getBlockState(pos).is(FIBlocks.SAP_TRAP.get())
                && level.getBlockEntity(pos) instanceof SapTrapBlockEntity sapTrap
                && BaitItem.attracts(sapTrap.getBait(), this.mob);
    }
}